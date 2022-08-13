OBJ=classes.o toto.o
SRC=classes.cpp toto.cpp
BIN=classes

CXX=g++
CXXFLAGS=-Wall -Wextra -Werror -pedantic -std=c++20

$(OBJ): $(SRC)
	$(CXX) $(CXXFLAGS) -c $< -o $@

$(BIN): $(OBJ)
	$(CXX) $(CXXFLAGS) $< -o $@

all: $(BIN)

.PHONY: clean
clean:
	rm -rf $(OBJ) $(BIN)
