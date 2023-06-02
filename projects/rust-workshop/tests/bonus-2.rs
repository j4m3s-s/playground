use std::convert::AsRef;
use std::fmt::Debug;

fn pretty_print<T, Line, Matrix>(matrix: Matrix)
where
    T: Debug,
    // A line references a slice of items
    Line: AsRef<[T]>,
    // A matrix references a slice of lines
    Matrix: AsRef<[Line]>
{
    for row in matrix.as_ref() {
        println!("{:?}", row.as_ref());
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test]
    fn test_transpose() {

        // &[&[i32]]
        pretty_print(&[[1, 2, 3], [4, 5, 6], [7, 8, 9]]);
        // [[&str; 2]; 2]
        pretty_print([["a", "b"], ["c", "d"]]);
        // Vec<Vec<i32>>
        pretty_print(vec![vec![1, 2], vec![3, 4]]);
        let a: i32 = [1, 2];
        a += 1;
    }
}
