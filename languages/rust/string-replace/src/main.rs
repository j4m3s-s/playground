use std::str::FromStr;

fn main() {
    let mut res: String = String::from_str("toto").unwrap();
    let from = vec!["o"];
    let to = vec!["a"];

    for elem in std::iter::zip(&from, &to) {
        let from = elem.0;
        let to = elem.1;
        res = res.replace(from, to);
    }
    println!("{}", res);
}
