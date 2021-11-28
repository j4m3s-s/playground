#pragma once

#include <functional>

template <typename T>

struct BTree {
	BTree<T>* left;
	BTree<T>* right;
	T data;

	BTree(T data) {
		this->data = data;
	}

	BTree(T data, BTree<T>* left, BTree<T>* right) {
		this->data = data;
		this->left = left;
		this->right = right;
	}

	void preorder(std::function<void(T)> func);

	void inorder(std::function<void(T)> func);

	void postorder(std::function<void(T)> func);

	size_t size();
};

template <typename T>
size_t BTree<T>::size() {
	auto res = 1;
	if (left)
		res += left->size();
	if (right)
		res += right->size();

	return res;
}

template <typename T>
void BTree<T>::preorder(std::function<void(T)> func) {
	func(data);

	if (left)
		left->preorder(func);
	if (right)
		right->preorder(func);
}

template <typename T>
void BTree<T>::inorder(std::function<void(T)> func) {
	if (left)
		left->inorder(func);

	func(data);

	if (right)
		right->inorder(func);
}

template <typename T>
void BTree<T>::postorder(std::function<void(T)> func) {
	if (left)
		left->postorder(func);

	if (right)
		right->postorder(func);

	func(data);
}
