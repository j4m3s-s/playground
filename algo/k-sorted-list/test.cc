#ifdef TEST
#include <set>
#include <vector>
using std::multiset;
using std::vector;
#endif

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
struct ListNode {
    int val;
    ListNode *next;
    ListNode() : val(0), next(nullptr) {}
    ListNode(int x) : val(x), next(nullptr) {}
    ListNode(int x, ListNode *next) : val(x), next(next) {}
};

class Solution {
    public:
	ListNode* mergeKLists(vector<ListNode*>& lists) {
		multiset<int> set;
		for (auto list : lists) {
			auto elt = list;
			while (elt) {
				set.insert(elt->val);
				elt = elt->next;
			}
		}

		ListNode root;
		ListNode *it = &root;

		for (auto elt: set) {
			auto node = new ListNode(elt);
			it->next = node;
			it = it->next;
		}

		return root.next;
        }
};

#ifdef TEST
#include <iostream>
ListNode* createList() {
	return nullptr;
}

template <typename T, typename... Types>
ListNode* createList(T elt, Types... tail) {
	auto e = new ListNode(elt);
	e->next = createList(tail...);
	return e;
}

int main() {
    auto l1 = createList(1,2,3,4,5,6);
    auto l2 = createList(1,2222,2,2,2,2,2,2,2,2,22);

    auto tmp = vector<ListNode*>({l1, l2});
    auto sol = Solution();
    auto res = sol.mergeKLists(tmp);

    for (ListNode* tmp = res; tmp; tmp = tmp->next) {
        std::cout << tmp->val << ' ';
    }
    std::cout << '\n';
}

#endif
