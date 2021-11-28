#include <gtest/gtest.h>
#include <iostream>

#include "linked-list.hh"

TEST(test, assertion) {
	ASSERT_EQ(1, 1);
}

TEST(llist, initialization) {
	auto h = new Llist<int>(1);
	ASSERT_NE(h, nullptr);
	delete h;
}

TEST(llist, length) {
	auto h = createList(1, 2, 3, 4);
	ASSERT_EQ(length(h), 4);
}


TEST(llist, null_tail) {
	Llist<int> *h = nullptr;
	ASSERT_EQ(tail(h), nullptr);
}

TEST(llist, null_head) {
	Llist<int> *h = nullptr;
	ASSERT_EQ(head(h), nullptr);
}

TEST(llist, zero_len) {
	Llist<int> *h = nullptr;
	ASSERT_EQ(length(h), 0);
}

int main(int argc, char *argv[]) {
	::testing::InitGoogleTest(&argc, argv);

	return RUN_ALL_TESTS();
}
