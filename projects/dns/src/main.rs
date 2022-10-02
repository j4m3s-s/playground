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
}

// Custom formatter to be able to print in hexadecimal
impl fmt::Display for DNSHeader {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "id: {:#02X}, flags: {:#02b}", self.id, self.flags)
    }
}

fn get_header(packet: &[u8]) -> Option<DNSHeader> {
    Some(DNSHeader {
        id: u16::from_be_bytes(packet[0..2].try_into().unwrap()),
        flags: u16::from_be_bytes(packet[2..4].try_into().unwrap()),
    })
}

fn is_query(hdr: &DNSHeader) -> bool {
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
