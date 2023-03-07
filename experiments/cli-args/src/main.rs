use std::env;

// Wanted to test how I could dispatch on argv[0] in Rust (and other languages).
// To achieve something akin to busybox first arg dispatch.
fn main() -> Result<(), std::io::Error> {
    let binding = env::current_exe()?;
    let exe = &binding.display();
    println!("{exe}");
    Ok(())
}
