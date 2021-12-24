#include <iostream>

struct ListNode {
	int val;
	ListNode *next;
	ListNode() : val(0), next(nullptr) {}
	ListNode(int x) : val(x), next(nullptr) {}
	ListNode(int x, ListNode *next) : val(x), next(next) {}
};

ListNode* createList() {
	return nullptr;
}

template <typename T, typename... Types>
ListNode* createList(T elt, Types... tail) {
	auto e = new ListNode(elt);
	e->next = createList(tail...);
	return e;
}

void print(ListNode* elt) {
	while (elt) {
		std::cout << elt->val << ' ';
		elt = elt->next;
	}

	std::cout << '\n';
}


/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode() : val(0), next(nullptr) {}
 *     ListNode(int x) : val(x), next(nullptr) {}
 *     ListNode(int x, ListNode *next) : val(x), next(next) {}
 * };
 */
class Solution {
public:
	ListNode* removeNthFromEnd(ListNode* head, int n) {
		if (n <= 0)
			return head;
		if (!head)
			return nullptr;
		if (n == 1)
			return head->next;

		ListNode *before = head;
		ListNode *current = head->next;

		while (current && n) {
			before = current;
			current = current->next;
			n--;
		}

		if (!current)
			return head;

		if (n != 0)
			return head;

		before->next = current->next;
		return head;
	}
};

int main() {
	auto a = Solution();
	auto lst = createList(1, 2, 3, 4, 5, 6);
	print(a.removeNthFromEnd(lst, 0));
}
