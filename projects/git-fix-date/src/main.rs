use std::env::args;
use std::process::{Command, exit};

use std::collections::HashMap;

fn get_current_executable_name() -> String {
    let binding = current_exe().unwrap();
    let exe = &binding.to_str().unwrap()
                               .split('/').next_back().unwrap();
    exe.to_string()
}

// FIXME: verify it works on rebase

fn main() -> Result<(), std::io::Error> {
    let subcommand = args().nth(1);
    let commit = String::from("commit");
    // FIXME: compute new dates
    let new_env = match subcommand {
        Some(commit) => HashMap::from([("GIT_COMMIT_DATE", "today")]),
        _ => HashMap::new(),
    };

    // FIXME: override var
    let cmd = Command::new("echo")
        .args(args())
        .envs(new_env)
        .spawn().unwrap()
        .wait();

    exit(cmd.unwrap().code().unwrap());
}
