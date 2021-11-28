CXX ?= g++
CXXFLAGS=-Wall -Wextra -Werror -std=c++17 -fsanitize=address -g3
CPPFLAGS=
LDLIBS= -lgtest

CXXBINARIES: test-binary-tree test-linked-list

test-binary-tree: ./binary-tree/test-binary-tree.o
	$(LINK.cc) $^ $(LDLIBS) -o $@

test-linked-list: ./linked-list/test-linked-list.o
	$(LINK.cc) $^ $(LDLIBS) -o $@

.PHONY: all
all: $(CXXBINARIES)

.PHONY: clean
clean:
	rm -rf test-binary-tree test-linked-list
	find -name '*.o' | xargs rm
