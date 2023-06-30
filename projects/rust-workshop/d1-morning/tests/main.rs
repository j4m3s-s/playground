fn multiply(x: i16, y: i16) -> i16 {
    x * y
}

// TODO: remove this when you're done with your implementation.


#[cfg(test)]
mod tests {
    #[test]
    fn test_multiply() {
        let x: i8 = 15;
        let y: i16 = 1000;

        println!("{x} * {y} = {}", crate::multiply(x.into(), y));
    }

    // TL;DR you cannot downcast with into since you lose information

    #[test]
    fn test_array() {
        let array = [10, 20, 30];
        println!("array: {array:?}");
    }

    #[test]
    fn test_range() {
        let array = [10, 20, 30];
        print!("Iterating over array:");
        for n in array {
            print!(" {n}");
        }
        println!();

        print!("Iterating over range:");
        for i in 0..3 {
            print!(" {}", array[i]);
        }
        println!();
    }
}


