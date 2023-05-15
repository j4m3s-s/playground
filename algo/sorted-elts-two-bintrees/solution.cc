#include <vector>
#include <stdlib.h>

using std::vector;

/* Given code */

struct TreeNode {
  int val;
  TreeNode *left;
  TreeNode *right;
  TreeNode() : val(0), left(nullptr), right(nullptr) {}
  TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
  TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
};

/* Solution */

void inorder_traversal(TreeNode* root, vector<int> &vec) {
  if (!root)
    return;

  inorder_traversal(root->left, vec);
  vec.push_back(root->val);
  inorder_traversal(root->right, vec);
}

class Solution {
public:
    vector<int> getAllElements(TreeNode* root1, TreeNode* root2) {
      size_t i = 0;
      size_t j = 0;

      vector<int> r1;
      inorder_traversal(root1, r1);
      vector<int> r2;
      inorder_traversal(root2, r2);

      vector<int> res;

      while (i < r1.size() && j < r2.size()) {
        if (r1[i] < r2[j]) {
          res.push_back(r1[i]);
          i++;
        } else {
          res.push_back(r2[j]);
          j++;
        }
      }

      while (i < r1.size()) {
        res.push_back(r1[i]);
      }
      while (j < r2.size()) {
        res.push_back(r2[j]);
      }

      return res;
    }
};

// Technically this should work, but blows up the memory limit. The memory
// optimized way is probably something along the line of making stack usage
// explicit and not use the temporary r1/r2 arrays.
// Using a lazy language actually solves that. Or coroutines.

/* Tests / main code */

