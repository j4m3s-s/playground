#include <gtest/gtest.h>
#include <iostream>

#include "binary-tree.hh"

TEST(btree, only_root) {
	BTree<int> *h = new BTree<int>(1);
	ASSERT_EQ(h->size(), 1);
}

TEST(btree, size) {
	BTree<int> *root = new BTree<int>(1, new BTree<int>(2), new BTree<int>(3));
	ASSERT_EQ(root->size(), 3);
}

TEST(btree, preorder) {
	BTree<int> *root = new BTree<int>(1, new BTree<int>(2), new BTree<int>(3));
	size_t nb = 0;

	auto func = [&nb] (int data) mutable {
		std::cout << nb << ' ' << data << '\n';
		nb += data;
	};

	root->preorder(func);
	ASSERT_EQ(nb, 6);
}

TEST(btree, inorder) {
	BTree<int> *root = new BTree<int>(1, new BTree<int>(2), new BTree<int>(3));
	size_t nb = 0;

	auto func = [&nb] (int data) mutable {
		std::cout << nb << ' ' << data << '\n';
		nb += data;
	};

	root->inorder(func);
	ASSERT_EQ(nb, 6);
}

TEST(btree, postorder) {
	BTree<int> *root = new BTree<int>(1, new BTree<int>(2), new BTree<int>(3));
	size_t nb = 0;

	auto func = [&nb] (int data) mutable {
		std::cout << nb << ' ' << data << '\n';
		nb += data;
	};

	root->postorder(func);
	ASSERT_EQ(nb, 6);
}

int main(int argc, char *argv[]) {
	::testing::InitGoogleTest(&argc, argv);

	return RUN_ALL_TESTS();
}
