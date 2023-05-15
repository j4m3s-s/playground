#include <map>

#include <iostream>

using namespace std;

// FIXME:
// - memory handling (unique ptr, string, string_view)

class PrefixTree {
  bool is_end;
  map<char, PrefixTree*> children;

 public:
    PrefixTree() {
      this->is_end = false;
    }

    ~PrefixTree() {
      for (const auto& [_key, value] : this->children) {
        delete value;
      }
    }

    void add(const char *str) {
      char c = str[0];
      // assert not null and str[0] != 0

      // Doesn't have any child for c
      if (!this->children.contains(c)) {
        auto child = new PrefixTree();
        if (str[1] == 0) {
          this->is_end = true;
        } else {
          child->add(++str);
          children.insert({c, child});
        }
      }
      // Have a child for c
    }

    static void print_spaces(int spaces) {
      for (int i = 0; i < spaces; i++) {
        cout << ' ';
      }
    }

    void print(int spacing = 0) const {
      if (this->children.empty()) {
        print_spaces(spacing);
        cout << "PrefixTree()\n";
        return;
      }

      print_spaces(spacing);
      cout << "PrefixTree(\n";

      for (const auto& [key, value] : this->children) {
        print_spaces(spacing + 2);
        cout << key << ' ';

        value->print(spacing + 2);
      }

      print_spaces(spacing);
      cout << ")\n";
    }
};

int main() {
  auto pt = new PrefixTree();

  pt->add("toto");
  pt->print();
}
