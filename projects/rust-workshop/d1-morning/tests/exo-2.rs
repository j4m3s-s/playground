#![allow(unused_variables, dead_code)]

fn transpose(matrix: [[i32; 3]; 3]) -> [[i32; 3]; 3] {
    let mut res = [[0; 3]; 3];
    for i in 0..matrix.len() {
        for j in 0..matrix[0].len() {
            res[i][j] = matrix[j][i];
        }
    }
    res
}

&[1..2]

fn pretty_print(matrix: &[[i32; 3]; 3]) {
    for i in 0..matrix.len() {
        for j in 0..matrix[0].len() {
            let elt = matrix[i][j];
            print!("{elt} ");
        }
        print!("\n")
    }
}

#[cfg(test)]
mod tests {
    #[test]
    fn test_transpose() {
        let matrix = [
            [101, 102, 103], // <-- the comment makes rustfmt add a newline
            [201, 202, 203],
            [301, 302, 303],
        ];

        println!("matrix:");
        crate::pretty_print(&matrix);

        let transposed = crate::transpose(matrix);
        println!("transposed:");
        crate::pretty_print(&transposed);
    }
}
