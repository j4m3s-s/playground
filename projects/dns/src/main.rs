use ::byte_strings::concat_bytes;
use std::fmt;

// To use with ExternalDNSHeader
use std::mem;

// for automatic error conversion
use std::convert::From;
use std::string::FromUtf8Error;

// This is used to transform int to enums directly
extern crate num;
#[macro_use]
extern crate num_derive;


// HACK: We use a C representation. Be careful that this means there might
// be padding involved. Currently all of our fields are equal, thus we won't have
// any problem.
//
// This represents the header of a DNS packet.
#[repr(C)]
#[derive(Copy, Clone)]
struct ExternalDNSHeader {
    // This is a unique ID for each request.
    id       : u16,
    // This contains bitfields of flags
    flags    : u16,
    qd_count : u16,
    an_count : u16,
    ns_count : u16,
    ar_count : u16,
}

#[derive(Eq, PartialEq, Debug, FromPrimitive, ToPrimitive)]
enum QueryType {
    Query = 0,
    InverseQuery,
    Status,
    // Others are reserved for future use
}

impl QueryType {
    fn to_nb(item: Self) -> u8 {
        item as u8
    }
}

#[derive(Eq, PartialEq, Debug, FromPrimitive, ToPrimitive)]
enum ResponseCode {
    NoError,
    FormatError,
    ServerFailure,
    NameError,
    NotImplemented,
    Refused,
    // reserved for future use
}

impl ResponseCode {
    fn to_nb(item: Self) -> u8 {
        item as u8
    }
}

impl QueryType {
    fn to_nb(item: Self) -> u8 {
        item as u8
    }
}

struct Flags {
    is_response          : bool,
    // HACK              : use an enum
    opcode               : QueryType,
    authoritative_server : bool,
    truncated            : bool,
    recursion_desired    : bool,
    recursion_available  : bool,
    // z unused here, 3 bits
    rcode                : ResponseCode,
}

impl Flags {
    fn to_bytes(item: Self) -> u16 {
        let mut res: u16 = 0;

        res |= item.is_response << 15;
        res |= item.opcode.to_nb << 11;
        res |= item.authoritative_server << 10;
        res |= item.truncated << 9;
        res |= item.recursion_desired << 8;
        res |= item.rcode.to_nb;

        res
    }
}

// This is the internal way of representing a DNS Header. This might not be as optimized in space,
// however this makes query much easier to deal with by mutating a Header internally and
// serializing/deserializing at the edge.
// Last time I wrote such a DNS parser, having to deal internally with BigEndian (network byte
// order) was extremely annoying. Hence here using a very simple internal representation and
// transforming it into an external network version.
struct Header {
    id                       : u16,
    flags                    : Flags,
    question_count           : u16,
    answer_count             : u16,
    nameserver_count         : u16,
    additional_records_count : u16,
}

struct ByteHeader {
    bytes: [u16; 6],
}

impl Header {
    fn to_bytes(item: Self) -> ByteHeader {
        let mut res = ByteHeader {
            bytes: [0, 0, 0, 0, 0],
        };

        res.bytes[0] = item.id.to_be_bytes();
        res.bytes[1] = item.flags.to_bytes.to_be_bytes();
        res.bytes[2] = item.question_count.to_be_bytes();
        res.bytes[3] = item.answer_count.to_be_bytes();
        res.bytes[4] = item.nameserver_count.to_be_bytes();
        res.bytes[5] = item.additional_records_count.to_be_bytes();

        res
    }
}

#[derive(Debug, Eq, PartialEq)]
enum Error {
    ErrorQueryType,
    ErrorResponseCode,
    ErrorQClass,
    ErrorType,
    ErrorClass,
    UTF8Conversion,
}

// Using a result might make errors easier to deal with upstream
fn get_querytype(hdr: &ExternalDNSHeader) -> Result<QueryType, Error> {
    let flags = hdr.flags;
    let query_type = (flags >> 11) & 0b1111;
    num::FromPrimitive::from_u16(query_type).ok_or(Error::ErrorQueryType)
}

fn is_authoritative(hdr: &ExternalDNSHeader) -> bool {
    // authoritative byte is the eleventh
    hdr.flags & (0x1 << 11) == 1
}

fn is_truncated(hdr: &ExternalDNSHeader) -> bool {
    hdr.flags & (0x1 << 10) == 1
}

fn is_recursion_desired(hdr: &ExternalDNSHeader) -> bool {
    hdr.flags & (0x1 << 9) == 1
}

fn is_recursion_available(hdr: &ExternalDNSHeader) -> bool {
    hdr.flags & (0x1 << 8) == 1
}

// Use result to propagate error
fn get_response_code(hdr: &ExternalDNSHeader) -> Result<ResponseCode, Error> {
    let flags = hdr.flags;
    let rcode = flags & 0b1111;
    num::FromPrimitive::from_u16(rcode).ok_or(Error::ErrorResponseCode)
}

// use result and propagate error
fn flags_from_u16(hdr: &ExternalDNSHeader) -> Result<Flags, Error> {
    Ok(Flags {
        is_response: !is_query(&hdr),
        // Use result and propagate error
        opcode: get_querytype(&hdr)?,
        authoritative_server: is_authoritative(hdr),
        truncated: is_truncated(&hdr),
        recursion_desired: is_recursion_desired(&hdr),
        recursion_available: is_recursion_available(&hdr),
        rcode: get_response_code(&hdr)?,
    })
}

impl ExternalDNSHeader {
    // FIXME                         : make it the whole struct, not just the header
    fn serialize(&self) -> Result<Header, Error> {
        Ok(Header{
            id                       : self.id,
            flags                    : flags_from_u16(self)?,
            question_count           : self.qd_count,
            answer_count             : self.an_count,
            nameserver_count         : self.ns_count,
            additional_records_count : self.ar_count,
        })
    }
}

// Custom formatter to be able to print in hexadecimal
impl fmt::Display for ExternalDNSHeader {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "id: {:#02X}, \
                   flags: {:#02b} \
                   qd_count: {},
                   an_count: {},
                   ns_count: {},
                   ar_count: {},
                   ",
               self.id,
               self.flags,
               self.qd_count,
               self.an_count,
               self.ns_count,
               self.ar_count)
    }
}

fn get_header(packet: &[u8]) -> ExternalDNSHeader {
    ExternalDNSHeader {
        id: u16::from_be_bytes(packet[0..2].try_into().unwrap()),
        flags: u16::from_be_bytes(packet[2..4].try_into().unwrap()),
        qd_count: u16::from_be_bytes(packet[4..6].try_into().unwrap()),
        an_count: u16::from_be_bytes(packet[6..8].try_into().unwrap()),
        ns_count: u16::from_be_bytes(packet[8..10].try_into().unwrap()),
        ar_count: u16::from_be_bytes(packet[10..12].try_into().unwrap()),
    }
}

fn is_query(hdr: &ExternalDNSHeader) -> bool {
    // query flag is the top most bit of the flags field
    hdr.flags & (0x1 << 15) == 0
}

// Probably mostly unused since we usually use QClass
#[derive(Eq, PartialEq, Debug, FromPrimitive, ToPrimitive)]
enum Class {
    IN = 1, // Internet
    CS, // CSNet
    CH, // Chaos
    HS, // Hesiod
}

#[derive(Eq, PartialEq, FromPrimitive, ToPrimitive, Debug )]
enum QClass {
    IN = 1, // Internet
    CS, // CSNet
    CH, // Chaos
    HS, // Hesiod
    ALL, // *
}

fn qclass_from_u16(qclass: u16) -> Result<QClass, Error> {
    num::FromPrimitive::from_u16(qclass).ok_or(Error::ErrorQClass)
}

fn class_from_u16(class: u16) -> Result<Class, Error> {
    num::FromPrimitive::from_u16(class).ok_or(Error::ErrorClass)
}

// Probably mostly unused since we usually use QType
#[derive(Eq, PartialEq, Debug, FromPrimitive, ToPrimitive, Copy, Clone)]
enum Type {
    A = 1,
    NS,
    MD, // Obsolete
    MF, // Obsolete
    CNAME,
    SOA,
    MB,
    MG,
    MR,
    NULL,
    WKS,
    PTR,
    HINFO,
    MINFO,
    MX,
    TXT,

    // Not from RFC1035, this is used so that we don't parse resource record further
    // This is a pseudo-record for edns0
    OPT = 41,
}

#[derive(Eq, PartialEq, FromPrimitive, ToPrimitive, Debug)]
enum QType {
    A = 1,
    NS,
    MD, // Obsolete
    MF, // Obsolete
    CNAME,
    SOA,
    MB,
    MG,
    MR,
    NULL,
    WKS,
    PTR,
    HINFO,
    MINFO,
    MX,
    TXT,

    // QType specific types
    AXFR = 252,
    MAILB,
    MAILA,
    ALL, // *
}

fn type_from_u16(qtype: u16) -> Result<Type, Error> {
    num::FromPrimitive::from_u16(qtype).ok_or(Error::ErrorType)
}

fn qtype_from_u16(qtype: u16) -> Result<QType, Error> {
    num::FromPrimitive::from_u16(qtype).ok_or(Error::ErrorQueryType)
}

struct Question {
    qname  : String,
    qtype  : QType,
    qclass : QClass,
}

fn get_question(packet: &[u8], offset: usize) -> Result<(Question, usize), Error> {
    let mut offset: usize = offset;
    let mut question_size: u8 = packet[offset];

    let mut qname = String::new();
    while question_size != 0 {
        offset += 1;
        qname.push_str(String::from_utf8(packet[offset..offset + question_size as usize].try_into().unwrap()).unwrap().as_str());
        qname.push('.');
        offset += question_size as usize;

        question_size = packet[offset];
    }
    offset += 1;

    let qtype_u16 = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
    offset += 2;
    let qclass_u16 = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
    offset += 2;

    let qtype = qtype_from_u16(qtype_u16).unwrap();
    let qclass = qclass_from_u16(qclass_u16).unwrap();

    let question = Question {
        qname: qname,
        qtype: qtype,
        qclass: qclass,
    };
    Ok((question, offset))
}

fn get_questions_vec(packet: &[u8], question_count: u16) -> Result<(Vec<Question>, usize), Error> {
    let mut vec = vec![];

    let mut offset = mem::size_of::<ExternalDNSHeader>();
    for _ in 0..question_count {
        let (question, tmp_offset) = get_question(packet, offset)?;
        offset = tmp_offset;
        vec.push(question);
    }

    Ok((vec, offset))
}

struct DNSPacket {
    header: Header,
    questions: Vec<Question>,
    additional_records: Vec<ResourceRecord>,
    // And more!
}

enum Name<'buffer_life> {
    NonEmpty(&'buffer_life [u8]),
    Empty,
}

#[derive(Debug)]
struct ExternalResourceRecord<'buffer_life> {
    name: &'buffer_life [u8],
    rtype: u16,
    class: u16,
    ttl: u32,
    rdlength: u16,
    rdata: &'buffer_life [u8],
}

impl From<FromUtf8Error> for Error {
    fn from(error: FromUtf8Error) -> Self {
        Error::UTF8Conversion
    }
}

impl ExternalResourceRecord<'_> {
    fn serialize(&self) -> Result<ResourceRecord, Error> {
        let rtype = type_from_u16(self.rtype)?;
        println!("nm {:?} {:?}", self.name, rtype);
        let typedrrdata = match rtype {
            Type::A => TypedResourceRecordData::A(self.rdata.to_vec()),
            Type::NS => TypedResourceRecordData::NS(self.rdata.to_vec()),
            _ => TypedResourceRecordData::UNKNOWN,
        };

        Ok(ResourceRecord{
            name: String::from_utf8(self.name.to_vec()).unwrap(),
            rtype: rtype,
            class: class_from_u16(self.class)?,
            ttl: self.ttl,
            data: typedrrdata,
        })
    }
}

#[derive(Debug)]
enum TypedResourceRecordData {
    // FIXME: exo, use lifetime here or just use copy smhw
    A(Vec<u8>),
    // FIXME: use string
    NS(Vec<u8>),
    CNAME(String),
    SOA, // TODO: implement me

    // I'm trying to implement a DNS parser according to RFC1035 but a lot of other RFCs add new
    // behaviors, especially EDNS-0's one (bigger DNS packets basically). So adding this escape
    // hatch to be able to test things more easily.
    UNKNOWN,
}

#[derive(Debug)]
struct ResourceRecord {
    name: String,
    rtype: Type,
    class: Class,
    ttl: u32,
    data: TypedResourceRecordData,
}

impl ResourceRecord {
    fn serialize(item: Self) -> ExternalResourceRecord<'a> {
        ExternalResourceRecord {
            name: item.to_bytes(),

        }
    }
}

fn get_resource_record_vec(packet: &[u8], offset: usize) -> Result<(Vec<ResourceRecord>,
                                                                        usize), Error> {
    let mut res = vec![];
    let mut offset = offset;

    let possible_null_byte: u8 = packet[offset];
    let name: &[u8] = if possible_null_byte == 0 {
        // empty
        &packet[0..0]
    } else {
    // else parse something here
        let mut sz = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
        let mut res = &packet[0..0];

        let mask = 0xc000;
        // we have a DNS "compressed" name
        if sz & mask != 0 {
            // we remove the mask, this makes an offset in the DNS packet
            sz &= !mask;
            let slice = &packet[(sz - 2) as usize..sz as usize];
            let new_sz = u16::from_be_bytes(slice.try_into().unwrap());
            res = &packet[sz as usize..sz as usize + new_sz as usize];

            // We set size here as 1 since we skip the null byte later (which is not present here)
            // and the size is actually a u16 (a size of data).
            sz = 1;
        }
        offset += sz as usize;
        res
    };

    // skip null byte
    offset += 1;

    let rtype = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
    println!("rtype {}", rtype);
    offset += 2;
    // FIXME: OPT
    if rtype == 0x29 {
        res.push(ExternalResourceRecord {
            name: name,
            rtype: rtype,
            class: 1,
            ttl: 0,
            rdlength: 0,
            rdata: &packet[0..0]
        }.serialize()?);
        return Ok((res, offset));
    }


    let class = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
    offset += 2;

    let ttl = u32::from_be_bytes(packet[offset..offset + 4].try_into().unwrap());
    offset += 4;

    let rdlength = u16::from_be_bytes(packet[offset..offset + 2].try_into().unwrap());
    offset += 2;

    let rdata = packet[offset..offset + rdlength as usize].try_into().unwrap();
    offset += rdlength as usize;

    res.push(ExternalResourceRecord {
        name: name,
        rtype: rtype,
        class: class,
        ttl: ttl,
        rdlength: rdlength,
        rdata: rdata,
    }.serialize()?);
    Ok((res, offset))
}
fn get_parsed_dns_packet(packet: &[u8]) -> Result<DNSPacket, Error> {
    let header = get_header(packet).serialize()?;

    let (questions, offset) = get_questions_vec(packet, header.question_count)?;

    let (additional, _) = get_resource_record_vec(&packet, offset)?;
    println!("{:?}", additional[0]);


    Ok(DNSPacket {
        header: header,
        questions: questions,
        additional_records: additional,
    })
}

fn main() {
    // this is an example packet dumped from tcpdump / wireshark
    // commands : tcpdump -i lo -w toto.pcap ; wireshark toto.pcap
    let packet = concat_bytes!(
        b"\x7a\xc6\x01\x00\x00\x01",
        b"\x00\x00\x00\x00\x00\x01\x01\x64\x00\x00\x01\x00\x01\x00\x00\x29",
        b"\x04\xb0\x00\x00\x00\x00\x00\x00",
    );

    let hdr = get_header(packet);
    println!("{}", hdr);

    if is_query(&hdr) {
        println!("It's a query!");
    }

    let (vec, _) = get_questions_vec(packet, hdr.qd_count).unwrap();
    let elt = &vec[0];
    println!("{:?} {:?} {:?}", elt.qtype, elt.qname, elt.qclass);

    let bytes = byte_strings::concat_bytes!(b"\x9a\xf8\x01\x00\x00\x01",
                                            b"\x00\x00\x00\x00\x00\x01\x03\x77\x77\x77\x11\x74\x68\x65\x66\x72",
                                            b"\x65\x65\x64\x69\x63\x74\x69\x6f\x6e\x61\x72\x79\x03\x63\x6f\x6d",
                                            b"\x00\x00\x01\x00\x01\x00\x00\x29\x04\xb0\x00\x00\x00\x00\x00\x00");

    let (questions, _) = get_questions_vec(bytes.as_slice().try_into().unwrap(), 1).unwrap();
    let q = &questions[0];
    println!("{:?} {:?} {:?}", q.qtype, q.qname, q.qclass);

    let packet2 = byte_strings::concat_bytes!(
        // transaction id
        b"\x76\x11",

        // Flags
        b"\x01\x20",

        // question len
        b"\x00\x01",
        // answer len
        b"\x00\x00",
        // authority len
        b"\x00\x00",
        // additional len
        b"\x00\x01",

        // Query (won't develop this one further since parsing it works
        b"\x06\x67\x6f\x6f",
        b"\x67\x6c\x65\x03\x63\x6f\x6d\x00\x00\x02\x00\x01",

        // Additional records
        // root record
        b"\x00",
        // OPT
        b"\x00\x29",
        // UDP payload size (in wireshark) which is not supposed to be here
        b"\x04",
        b"\xd0\x00\x00\x00\x00\x00\x0c\x00\x0a\x00\x08\xa5\x64\xcb\x63\xbe",
        b"\x10\x0a\xf9");

    let _ = get_parsed_dns_packet(packet2).unwrap();

    let packet3 = byte_strings::concat_bytes!(
        b"\x76\x11\x81\x00\x00\x01\x00\x00\x00\x04\x00\x09\x06\x67\x6f\x6f",
        b"\x67\x6c\x65\x03\x63\x6f\x6d\x00\x00\x02\x00\x01\xc0\x0c\x00\x02",
        b"\x00\x01\x00\x02\xa3\x00\x00\x06\x03\x6e\x73\x32\xc0\x0c\xc0\x0c",
        b"\x00\x02\x00\x01\x00\x02\xa3\x00\x00\x06\x03\x6e\x73\x31\xc0\x0c",
        b"\xc0\x0c\x00\x02\x00\x01\x00\x02\xa3\x00\x00\x06\x03\x6e\x73\x33",
        b"\xc0\x0c\xc0\x0c\x00\x02\x00\x01\x00\x02\xa3\x00\x00\x06\x03\x6e",
        b"\x73\x34\xc0\x0c\xc0\x28\x00\x1c\x00\x01\x00\x02\xa3\x00\x00\x10",
        b"\x20\x01\x48\x60\x48\x02\x00\x34\x00\x00\x00\x00\x00\x00\x00\x0a",
        b"\xc0\x28\x00\x01\x00\x01\x00\x02\xa3\x00\x00\x04\xd8\xef\x22\x0a",
        b"\xc0\x3a\x00\x1c\x00\x01\x00\x02\xa3\x00\x00\x10\x20\x01\x48\x60",
        b"\x48\x02\x00\x32\x00\x00\x00\x00\x00\x00\x00\x0a\xc0\x3a\x00\x01",
        b"\x00\x01\x00\x02\xa3\x00\x00\x04\xd8\xef\x20\x0a\xc0\x4c\x00\x1c",
        b"\x00\x01\x00\x02\xa3\x00\x00\x10\x20\x01\x48\x60\x48\x02\x00\x36",
        b"\x00\x00\x00\x00\x00\x00\x00\x0a\xc0\x4c\x00\x01\x00\x01\x00\x02",
        b"\xa3\x00\x00\x04\xd8\xef\x24\x0a\xc0\x5e\x00\x1c\x00\x01\x00\x02",
        b"\xa3\x00\x00\x10\x20\x01\x48\x60\x48\x02\x00\x38\x00\x00\x00\x00",
        b"\x00\x00\x00\x0a\xc0\x5e\x00\x01\x00\x01\x00\x02\xa3\x00\x00\x04",
        b"\xd8\xef\x26\x0a\x00\x00\x29\x10\x00\x00\x00\x00\x00\x00\x00");

    let _ = get_parsed_dns_packet(packet3).unwrap();
}

// These are just basic sanity tests. Much advanced testing (and maybe fuzzing) would be required to
// make sure everything works correctly.
// Also, we need e2e tests to verify correct behavior.
mod tests {
    // Importing names from outer tests scope.
    use super::*;

    static PACKET: &[u8; 30] = byte_strings::concat_bytes!(
        b"\x7a\xc6\x01\x00\x00\x01",
        b"\x00\x00\x00\x00\x00\x01\x01\x64\x00\x00\x01\x00\x01\x00\x00\x29",
        b"\x04\xb0\x00\x00\x00\x00\x00\x00",
    );

    #[test]
    fn is_a_query() {
        let hdr = get_header(PACKET);
        assert!(is_query(&hdr));
    }

    #[test]
    fn qd_count() {
        let hdr = get_header(PACKET);
        assert_eq!(hdr.qd_count, 1);
    }

    #[test]
    fn an_count() {
        let hdr = get_header(PACKET);
        assert_eq!(hdr.an_count, 0);
    }

    #[test]
    fn ns_count() {
        let hdr = get_header(PACKET);
        assert_eq!(hdr.ns_count, 0);
    }

    #[test]
    fn ar_count() {
        let hdr = get_header(PACKET);
        assert_eq!(hdr.ar_count, 1);
    }


    #[test]
    fn test_serialized_flags() {
        let hdr = get_header(PACKET);

        let flags = flags_from_u16(&hdr).unwrap();
        assert!(!flags.is_response);
        assert_eq!(flags.opcode, QueryType::Query);
        assert!(!flags.authoritative_server);
        assert!(!flags.truncated);
        assert!(!flags.recursion_desired);
        assert!(!flags.recursion_available);
        assert_eq!(flags.rcode, ResponseCode::NoError);
    }

    #[test]
    fn test_serialized_header() {
        let hdr = get_header(PACKET);
        let serialized_hdr = hdr.serialize().unwrap();

        // Same as above tests, should I refacto?
        let flags = serialized_hdr.flags;
        assert!(!flags.is_response);
        assert_eq!(flags.opcode, QueryType::Query);
        assert!(!flags.authoritative_server);
        assert!(!flags.truncated);
        assert!(!flags.recursion_desired);
        assert_eq!(flags.rcode, ResponseCode::NoError);

        assert_eq!(serialized_hdr.id, 0x7ac6);
        assert_eq!(serialized_hdr.question_count, 1);
        assert_eq!(serialized_hdr.answer_count, 0);
        assert_eq!(serialized_hdr.nameserver_count, 0);
        assert_eq!(serialized_hdr.additional_records_count, 1);
    }

    #[test]
    fn test_questions_vec_simple() {
        let hdr = get_header(PACKET);
        let (questions, _) = get_questions_vec(PACKET, hdr.qd_count).unwrap();
        let q = &questions[0];
        assert!(questions.len() == 1);
        assert_eq!(q.qclass, QClass::IN);
        assert_eq!(q.qtype, QType::A);
        assert_eq!(q.qname, "d.");
    }

    #[test]
    fn test_multi_label_question() {
        let bytes = byte_strings::concat_bytes!(b"\x9a\xf8\x01\x00\x00\x01",
                                                b"\x00\x00\x00\x00\x00\x01\x03\x77\x77\x77\x11\x74\x68\x65\x66\x72",
                                                b"\x65\x65\x64\x69\x63\x74\x69\x6f\x6e\x61\x72\x79\x03\x63\x6f\x6d",
                                                b"\x00\x00\x01\x00\x01\x00\x00\x29\x04\xb0\x00\x00\x00\x00\x00\x00");

        let (questions, _) = get_questions_vec(bytes.as_slice().try_into().unwrap(), 1).unwrap();
        let q = &questions[0];
        assert!(questions.len() == 1);
        assert_eq!(q.qclass, QClass::IN);
        assert_eq!(q.qtype, QType::A);
        assert_eq!(q.qname, "www.thefreedictionary.com.");
    }
}
