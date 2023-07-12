// TODO: remove this when you're done with your implementation.
#![allow(unused_variables, dead_code)]

pub struct User {
    name: String,
    age: u32,
    weight: f32,
}

impl User {
    pub fn new(name: String, age: u32, weight: f32) -> Self {
        User{
            name: name,
            age: age,
            weight: weight
        }
    }

    pub fn name(&self) -> &str {
        &self.name
    }

    pub fn age(&self) -> u32 {
        self.age
    }

    pub fn weight(&self) -> f32 {
        self.weight
    }

    pub fn set_age(&mut self, new_age: u32) {
        self.age = new_age
    }

    pub fn set_weight(&mut self, new_weight: f32) {
        self.weight = new_weight
    }
}

fn main() {
    let bob = User::new(String::from("Bob"), 32, 155.2);
    println!("I'm {} and my age is {}", bob.name(), bob.age());
}

#[test]
fn test_weight() {
    let bob = User::new(String::from("Bob"), 32, 155.2);
    assert_eq!(bob.weight(), 155.2);
}

#[test]
fn test_set_age() {
    let mut bob = User::new(String::from("Bob"), 32, 155.2);
    assert_eq!(bob.age(), 32);
    bob.set_age(33);
    assert_eq!(bob.age(), 33);
}


// NEW
use std::cmp;

#[derive(Debug, Copy, Clone, PartialEq)]
pub struct Point {
    x: i32,
    y: i32
}

impl Point {
    // add methods
    fn new(x: i32, y: i32) -> Self {
        Point{
            x: x,
            y: y
        }
    }

    // add
    // dist
    fn dist(&self, another: Point) -> f64 {
        let x = cmp::max(self.x, another.x) - cmp::min(self.x, another.x);
        let y = cmp::max(self.y, another.y) - cmp::min(self.y, another.y);

        ((x * x + y * y) as f64).sqrt()
    }

    fn magnitude(&self) -> f64 {
        ((self.x * self.x + self.y * self.y) as f64).sqrt()
    }
}

use std::ops::Add;

impl Add for Point {
    type Output = Self;
    fn add(self, other: Self) -> Self {
        Self{
            x: self.x + other.x,
            y: self.y + other.y
        }
    }
}

pub struct Polygon {
    // add fields
    points: Vec<Point>
}

impl Polygon {
    fn new() -> Self {
        Self {
            points: vec![]
        }
    }

    pub fn iter(&self) -> std::slice::Iter<Point> {
        self.points.iter()
    }
    // add methods
    fn add_point(&mut self, p: Point) {
        self.points.push(p)
    }
    fn left_most_point(&self) -> Option<Point> {
        if self.points.is_empty() {
            return None;
        }

        let mut p = self.points[0];

        for point in &self.points {
            if point.x < p.x {
                p = *point;
            }
        }

        Some(p)
    }

    // left_most_point -> Option<Point>
}

impl Perimeter for Polygon {
    fn perimeter(&self) -> f64 {
        let mut res: f64 = 0.0;

        let mut prev = self.points[0];
        for p in &self.points[1..] {
            res += p.dist(prev);
            prev = *p;
        }
        res += self.points[self.points.len() - 1].dist(self.points[0]);

        res
    }
}

pub struct Circle {
    center: Point,
    radius: u32
}

use std::f64::consts::PI;

impl Circle {
    fn new(p: Point, r: u32) -> Self {
        Circle {
            center: p,
            radius: r
        }
    }
}

impl Perimeter for Circle {
    fn perimeter(&self) -> f64 {
        2.0 * PI * self.radius as f64
    }
}

pub enum Shape {
    Polygon(Polygon),
    Circle(Circle),
}

trait Perimeter {
    fn perimeter(&self) -> f64;
}

impl Perimeter for Shape {
    fn perimeter(&self) -> f64 {
        match self {
            Shape::Circle(c) => c.perimeter(),
            Shape::Polygon(p) => p.perimeter()
        }
    }
}

impl From<Polygon> for Shape {
    fn from(item: Polygon) -> Self {
        Shape::Polygon(item)
    }
}

impl From<Circle> for Shape {
    fn from(item: Circle) -> Self {
        Shape::Circle(item)
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    fn round_two_digits(x: f64) -> f64 {
        (x * 100.0).round() / 100.0
    }

    #[test]
    fn test_point_magnitude() {
        let p1 = Point::new(12, 13);
        assert_eq!(round_two_digits(p1.magnitude()), 17.69);
    }

    #[test]
    fn test_point_dist() {
        let p1 = Point::new(10, 10);
        let p2 = Point::new(14, 13);
        assert_eq!(round_two_digits(p1.dist(p2)), 5.00);
    }

    #[test]
    fn test_point_add() {
        let p1 = Point::new(16, 16);
        let p2 = p1 + Point::new(-4, 3);
        assert_eq!(p2, Point::new(12, 19));
    }

    #[test]
    fn test_polygon_left_most_point() {
        let p1 = Point::new(12, 13);
        let p2 = Point::new(16, 16);

        let mut poly = Polygon::new();
        poly.add_point(p1);
        poly.add_point(p2);
        assert_eq!(poly.left_most_point(), Some(p1));
    }

    #[test]
    fn test_polygon_iter() {
        let p1 = Point::new(12, 13);
        let p2 = Point::new(16, 16);

        let mut poly = Polygon::new();
        poly.add_point(p1);
        poly.add_point(p2);

        let points = poly.iter().cloned().collect::<Vec<_>>();
        assert_eq!(points, vec![Point::new(12, 13), Point::new(16, 16)]);
    }

    #[test]
    fn test_shape_perimeters() {
        let mut poly = Polygon::new();
        poly.add_point(Point::new(12, 13));
        poly.add_point(Point::new(17, 11));
        poly.add_point(Point::new(16, 16));
        let shapes = vec![
            Shape::from(poly),
            Shape::from(Circle::new(Point::new(10, 20), 5)),
        ];
        let perimeters = shapes
            .iter()
            .map(Shape::perimeter)
            .map(round_two_digits)
            .collect::<Vec<_>>();
        assert_eq!(perimeters, vec![15.48, 31.42]);
    }
}
