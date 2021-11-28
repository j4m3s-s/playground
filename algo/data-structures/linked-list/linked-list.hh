#pragma once

template <typename T>
struct Llist {
	Llist<T> *next;
	T data;

	Llist(T data) {
		this->data = data;
		this->next = nullptr;
	}

	bool hasNext() {
		return this->next != nullptr;
	}

	Llist<T>* operator++() {
		return this->next;
	}
};

template <typename T>
Llist<T>* head(Llist<T>* head) {
	return head;
}

template <typename T>
Llist<T>* tail(Llist<T>* head) {
	if (!head)
		return head;

	while (head->hasNext()) {
		head = head->next;
	}
	return head;
}

template <typename T>
size_t length(Llist<T>* head) {
	if (!head)
		return 0;

	size_t len = 1;

	while (head->hasNext()) {
		len++;
		head = head->next;
	}
	return len;
}

template <typename T>
Llist<T>* createList() {
	return nullptr;
}

template <typename T, typename... Types>
Llist<T>* createList(T elt, Types... tail) {
	auto e = new Llist<T>(elt);
	e->next = createList<T>(tail...);
	return e;
}
