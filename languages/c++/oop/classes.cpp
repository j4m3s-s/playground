#include <iostream>
using std::cout;

/*
 * Abstract class that declares an interface to respect
 */
struct Base {
    // pure function : virtual = 0
    virtual void print() = 0;
};

struct DerivedA : Base {
    void print() {
        cout << "DerivedA\n";
    }
};

struct DerivedB : Base {
    void print() {
        cout << "DerivedB\n";
    }
};

#include <vector>
using std::vector;

int main() {
    vector<Base*> vec = { new DerivedA(), new DerivedB() };
    for (auto elt: vec) {
        elt->print();
    }
}
