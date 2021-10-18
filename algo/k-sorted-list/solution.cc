#ifdef TEST
#include <vector>
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
            ListNode *init = new ListNode(0);
            ListNode *resIt = init;


            auto it = lists.begin();
            while (lists.size()) {
                int val = lists[0]->val;
                ListNode *tmp = lists[0];;

                for (auto it : lists) {
                    if (it->val > val) {
                        val = it->val;
                        tmp = it;
                    }
                }
                resIt->next = tmp;
                resIt = resIt->next;

                if (!tmp->next)
                    lists.erase(it);
                else
                    *it = (*it)->next;

                it++;
                if (it == lists.end())
                    it = lists.begin();
            }
            return init->next;
        }
};

#ifdef TEST
#include <iostream>

int main() {
    auto l1 = new ListNode(1, new ListNode(2, new ListNode(3)));
    auto l2 = new ListNode(2, new ListNode(3, new ListNode(4)));

    auto tmp = vector({l1, l2});
    auto sol = Solution();
    auto res = sol.mergeKLists(tmp);

    for (ListNode* tmp = res; tmp; tmp = tmp->next) {
        std::cout << tmp->val << ' ';
    }
    std::cout << '\n';
}

#endif
