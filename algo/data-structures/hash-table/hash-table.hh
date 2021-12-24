#pragma once

#include <utility>
using std::pair;

#include "../linked-list/linked-list.hh"

using HashValue = uint32_t;

template <typename Key, typename Value>
struct HashTable {
	void insert(Key);
	Value isPresent(Key);

	Llist<pair<Key, Value, HashValue>>* array;
	size_t size;

	HashTable() {
		this->array = new Llist<pair<Key, Value>>[1024];
		this->size = 1024;
	}

	~HashTable() {
		delete[] this->array;
		this->array = nullptr;
	}
};

template <typename T>
HashValue hash(T s);

HashValue hash<const char *s>(const char *s) {
	uint32_t h = 5381;

	for (unsigned char c = *s; c != 0; c = *++s) {
		h = h * 33 + c;
	}

	return h;
}

template <typename Key, typename Value>
void HashTable<Key, Value>::insert(Key elt) {
	auto h = hash(elt);

	// insert at top
}
