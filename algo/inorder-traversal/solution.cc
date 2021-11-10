#include <iostream>
#include <vector>
using std::vector;

struct TreeNode {
     int val;
     TreeNode *left;
     TreeNode *right;
     TreeNode() : val(0), left(nullptr), right(nullptr) {}
     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 };

class Solution {
public:
    void inorder(TreeNode* root, vector<int>& res) {
        if (!root)
            return;

        if (root->left)
            inorder(root->left, res);
        res.push_back(root->val);
        if (root->right)
            inorder(root->right, res);
    }
    vector<int> inorderTraversal(TreeNode* root) {
        std::vector<int> res;

        inorder(root, res);
        return  res;
    }
};

int main() {
	auto sol = Solution();
	auto a = new TreeNode(1, new TreeNode(2), new TreeNode(3));
	auto res = sol.inorderTraversal(a);
	for (auto elt : res) {
		std::cout << elt << '\n';
	}
}
