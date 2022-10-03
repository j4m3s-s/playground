use ::byte_strings::concat_bytes;
use std::fmt;

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

#[derive(Eq, PartialEq, Debug)]
enum ResponseCode {
    NoError,
    FormatError,
    ServerFailure,
    NameError,
    NotImplemented,
    Refused,
    // reserved for future use
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

// Using a result might make errors easier to deal with upstream
fn get_querytype(hdr: &ExternalDNSHeader) -> Option<QueryType> {
    let flags = hdr.flags;
    let query_type = (flags >> 11) & 0b10000;
    num::FromPrimitive::from_u16(query_type)
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
fn get_response_code(hdr: &ExternalDNSHeader) -> Option<ResponseCode> {
    let flags = hdr.flags;
    let rcode = flags & 0b1111;
    match rcode {
        0 => Some(ResponseCode::NoError),
        1 => Some(ResponseCode::FormatError),
        2 => Some(ResponseCode::ServerFailure),
        3 => Some(ResponseCode::NameError),
        4 => Some(ResponseCode::NotImplemented),
        5 => Some(ResponseCode::Refused),
        _ => None,
    }
}

// use result and propagate error
fn flags_from_u16(hdr: &ExternalDNSHeader) -> Flags {
    Flags {
        is_response: !is_query(&hdr),
        // Use result and propagate error
        opcode: get_querytype(&hdr).unwrap(),
        authoritative_server: is_authoritative(hdr),
        truncated: is_truncated(&hdr),
        recursion_desired: is_recursion_desired(&hdr),
        recursion_available: is_recursion_available(&hdr),
        rcode: get_response_code(&hdr).unwrap(),
    }
}

impl ExternalDNSHeader {
    // FIXME: make it the whole struct, not just the header
    fn serialize(&self) -> Header {
        Header{
            id: self.id,
            flags: flags_from_u16(self),
            question_count: self.qd_count,
            answer_count: self.an_count,
            nameserver_count: self.ns_count,
            additional_records_count: self.ar_count,
        }
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

fn get_header(packet: &[u8]) -> Option<ExternalDNSHeader> {
    Some(ExternalDNSHeader {
        id: u16::from_be_bytes(packet[0..2].try_into().unwrap()),
        flags: u16::from_be_bytes(packet[2..4].try_into().unwrap()),
        qd_count: u16::from_be_bytes(packet[4..6].try_into().unwrap()),
        an_count: u16::from_be_bytes(packet[6..8].try_into().unwrap()),
        ns_count: u16::from_be_bytes(packet[8..10].try_into().unwrap()),
        ar_count: u16::from_be_bytes(packet[10..12].try_into().unwrap()),
    })
}

fn is_query(hdr: &ExternalDNSHeader) -> bool {
    // query flag is the top most bit of the flags field
    hdr.flags & (0x1 << 15) == 0
}

/*
// Probably mostly unused since we usually use QClass
#[derive(Eq, PartialEq, Debug)]
enum Class {
    IN, // Internet
    CS, // CSNet
    CH, // Chaos
    HS, // Hesiod
}
*/

#[derive(Eq, PartialEq)]
enum QClass {
    IN, // Internet
    CS, // CSNet
    CH, // Chaos
    HS, // Hesiod
    ALL, // *
}

fn qclass_from_u16(qclass: u16) -> Option<QClass> {
    use QClass::*;
    match qclass {
        1 => Some(IN),
        2 => Some(CS),
        3 => Some(CH),
        4 => Some(HS),
        255 => Some(ALL),
        _ => None,
    }
}

/*
// Probably mostly unused since we usually use QType
#[derive(Eq, PartialEq, Debug)]
enum Type {
    A,
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
}
*/

#[derive(Eq, PartialEq)]
enum QType {
    A,
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
    AXFR,
    MAILB,
    MAILA,
    ALL, // *
}

fn qtype_from_u16(qtype: u16) -> Option<QType> {
    use QType::*;
    match qtype {
        1 => Some(A),
        2 => Some(NS),
        3 => Some(MD),
        4 => Some(MF),
        5 => Some(CNAME),
        6 => Some(SOA),
        7 => Some(MB),
        8 => Some(MG),
        9 => Some(MR),
        10 => Some(NULL),
        11 => Some(WKS),
        12 => Some(PTR),
        13 => Some(HINFO),
        14 => Some(MINFO),
        15 => Some(MX),
        16 => Some(TXT),
        // qtype specific
        252 => Some(AXFR),
        253 => Some(MAILB),
        254 => Some(MAILA),
        255 => Some(ALL),
        _ => None,
    }
}

struct Question {
    qname  : String,
    qtype  : QType,
    qclass : QClass,
}

fn main() {
    // this is an example packet dumped from tcpdump / wireshark
    // commands : tcpdump -i lo -w toto.pcap ; wireshark toto.pcap
    let packet = concat_bytes!(
        b"\x7a\xc6\x01\x00\x00\x01",
        b"\x00\x00\x00\x00\x00\x01\x01\x64\x00\x00\x01\x00\x01\x00\x00\x29",
        b"\x04\xb0\x00\x00\x00\x00\x00\x00",
    );

    let hdr = get_header(packet).unwrap();
    println!("{}", hdr);

    if is_query(&hdr) {
        println!("It's a query!");
    }
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
        let hdr = get_header(PACKET).unwrap();
        assert!(is_query(&hdr));
    }

    #[test]
    fn qd_count() {
        let hdr = get_header(PACKET).unwrap();
        assert_eq!(hdr.qd_count, 1);
    }

    #[test]
    fn an_count() {
        let hdr = get_header(PACKET).unwrap();
        assert_eq!(hdr.an_count, 0);
    }

    #[test]
    fn ns_count() {
        let hdr = get_header(PACKET).unwrap();
        assert_eq!(hdr.ns_count, 0);
    }

    #[test]
    fn ar_count() {
        let hdr = get_header(PACKET).unwrap();
        assert_eq!(hdr.ar_count, 1);
    }


    #[test]
    fn test_serialized_flags() {
        let hdr = get_header(PACKET).unwrap();

        let flags = flags_from_u16(&hdr);
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
        let hdr = get_header(PACKET).unwrap();
        let serialized_hdr = hdr.serialize();

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
}
