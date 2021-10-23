from typing import List, Optional

class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

    def __str__(self):
        if not self.next:
            return f"({self.val})"
        else:
            return f"({self.val}, {self.next})"

def _rightRotate(node):
    left = node.left
    leftright = node.left.right

    left.right = node
    node.left = leftright

def _leftRotate(node):
    right = node.right
    rightleft = node.right.left

    right.left = node
    node.right = rightleft

def insert(node, val):
    if node is None:
        return AVLNode(val)

    if val < node.val:
        node.left = insert(node.left, val)
    else:
        node.right = insert(node.right, val)

    lefth = 0 if not node.left else node.left.height
    righth = 0 if not node.right else node.right.height
    node.height = 1 + max(lefth, righth)

    return node._rebalance()



class AVLNode:
    def __repr__(self):
        left = "" if not self.left else f"{self.left}"
        right = "" if not self.right else f"{self.right}"
        return f"({self.val}, ({left}) ({right})"

    def __str__(self):
        left = "" if not self.left else f"{self.left}"
        right = "" if not self.right else f"{self.right}"
        return f"({self.val}, ({left}) ({right})"

    def __init__(self, val, left=None, right=None):
        self.val = val
        self.right = right
        self.left = left
        self.height = 1


    def _height(self):
        left = 0 if self.left is None else self._height(self.left)
        right = 0 if self.right is None else self._height(self.right)

        return 1 + max(left, right)

    def _appendListNode(self, iterator):
        a = ListNode(self.val)
        iterator.next = a
        iterator = iterator.next

    def preOrderSerialize(self, iterator):
        if self.left:
            self.left.preOrderSerialize(iterator)
        self._appendListNode(iterator)
        if self.right:
            self.right.preOrderSerialize(iterator)

    def preOrderPrint(self):
        if self.left:
            self.left.preOrderPrint(iterator)
        print(f"val: {self.val}")
        if self.right:
            self.right.preOrderPrint(iterator)

    def _rebalance(self):

        lefth = 0 if not self.left else self.left.height
        righth = 0 if not self.right else self.right.height
        balance = lefth - righth

        # left left case
        if balance > 1 and self.val < self.left.val:
            return _rightRotate(self)
        # right right case
        elif balance < -1 and self.val > self.right.val:
            return _leftRotate(self)
        # left right
        elif balance > 1 and self.val > self.left.val:
            self.left = _leftRotate(self.left)
            return _rightRotate(self)
        # right left
        elif balance < -1 and self.val < self.right.val:
            self.right = _rightRotate(self.right)
            return _leftRotate(self)

        return self

class Solution:
    def _insert(tree, elt):
        pass

    def _serialize(tree) -> Optional[ListNode]:
        pass

    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        avl = None

        for elt in lists:
            it = elt

            while elt is not None:
                avl = insert(avl, elt.val)
                print(f"elt: {avl}")
                elt = elt.next

            print(f"after: {avl}")

        return None if avl is None else avl.preOrderSerialize()


l = [ListNode(1, ListNode(2, ListNode(3))), ListNode(-1)]

a = Solution()
print(a.mergeKLists(l))
