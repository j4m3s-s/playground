use ::byte_strings::concat_bytes;
use std::fmt;

// HACK: We use a C representation. Be careful that this means there might
// be padding involved. Currently all of our fields are equal, thus we won't have
// any problem.
//
// This represents the header of a DNS packet.
#[repr(C)]
#[derive(Copy, Clone)]
struct DNSHeader {
    // This is a unique ID for each request.
    id: u16,
    // This contains bitfields of flags
    flags: u16,
    qd_count: u16,
    an_count: u16,
    ns_count: u16,
    ar_count: u16,
}

#[derive(Eq, PartialEq, Debug)]
enum QueryType {
    Query,
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
    is_response: bool,
    // HACK: use an enum
    opcode: QueryType,
    authoritative_server: bool,
    truncated: bool,
    recursion_desired: bool,
    recursion_available: bool,
    // z unused here, 3 bits
    rcode: ResponseCode,
}

// This is the internal way of representing a DNS Header. This might not be as optimized in space,
// however this makes query much easier to deal with by mutating a Header internally and
// serializing/deserializing at the edge.
// Last time I wrote such a DNS parser, having to deal internally with BigEndian (network byte
// order) was extremely annoying. Hence here using a very simple internal representation and
// transforming it into an external network version.
struct Header {
    id: u16,
    flags: Flags,
    question_count: u16,
    answer_count: u16,
    nameserver_count: u16,
    additional_records_count: u16,
}

// Using a result might make errors easier to deal with upstream
fn get_querytype(hdr: &DNSHeader) -> Option<QueryType> {
    let flags = hdr.flags;
    let query_type = (flags >> 11) & 0b10000;
    match query_type {
        0 => Some(QueryType::Query),
        1 => Some(QueryType::InverseQuery),
        2 => Some(QueryType::Status),
        _ => None
    }
}

fn is_authoritative(hdr: &DNSHeader) -> bool {
    // authoritative byte is the eleventh
    hdr.flags & (0x1 << 11) == 1
}

fn is_truncated(hdr: &DNSHeader) -> bool {
    hdr.flags & (0x1 << 10) == 1
}

fn is_recursion_desired(hdr: &DNSHeader) -> bool {
    hdr.flags & (0x1 << 9) == 1
}

fn is_recursion_available(hdr: &DNSHeader) -> bool {
    hdr.flags & (0x1 << 8) == 1
}

// Use result to propagate error
fn get_response_code(hdr: &DNSHeader) -> Option<ResponseCode> {
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
fn flags_from_u16(hdr: &DNSHeader) -> Flags {
    let mut res = Flags {
        is_response: !is_query(&hdr),
        // Use result and propagate error
        opcode: get_querytype(&hdr).unwrap(),
        authoritative_server: is_authoritative(hdr),
        truncated: is_truncated(&hdr),
        recursion_desired: is_recursion_desired(&hdr),
        recursion_available: is_recursion_available(&hdr),
        rcode: get_response_code(&hdr).unwrap(),
    };
    res
}

impl DNSHeader {
    // FIXME: make it the whole struct, not just the header
    fn serialize(&self) -> Header {
        let mut res = Header{
            id: self.id,
            flags: flags_from_u16(self),
            question_count: self.qd_count,
            answer_count: self.an_count,
            nameserver_count: self.ns_count,
            additional_records_count: self.ar_count,
        };

        res
    }
}

// Custom formatter to be able to print in hexadecimal
impl fmt::Display for DNSHeader {
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

fn get_header(packet: &[u8]) -> Option<DNSHeader> {
    Some(DNSHeader {
        id: u16::from_be_bytes(packet[0..2].try_into().unwrap()),
        flags: u16::from_be_bytes(packet[2..4].try_into().unwrap()),
        qd_count: u16::from_be_bytes(packet[4..6].try_into().unwrap()),
        an_count: u16::from_be_bytes(packet[6..8].try_into().unwrap()),
        ns_count: u16::from_be_bytes(packet[8..10].try_into().unwrap()),
        ar_count: u16::from_be_bytes(packet[10..12].try_into().unwrap()),
    })
}

fn is_query(hdr: &DNSHeader) -> bool {
    // query flag is the top most bit of the flags field
    hdr.flags & (0x1 << 15) == 0
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
    fn test_serialized_header() {
        let hdr = get_header(PACKET).unwrap();

        let flags = flags_from_u16(&hdr);
        assert!(!flags.is_response);
        assert_eq!(flags.opcode, QueryType::Query);
        assert!(!flags.authoritative_server);
        assert!(!flags.truncated);
        assert!(!flags.recursion_desired);
        assert_eq!(flags.rcode, ResponseCode::NoError);
    }
}
