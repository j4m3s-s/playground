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



class Solution:
    def _getMaxPosition(self, lists: List[Optional[ListNode]]) -> int:
        if len(lists) == 0:
            return -1

        nodePos = 0
        for i in range(len(lists)):
            if lists[i].val < lists[nodePos].val:
                nodePos = i

        return nodePos

    def _popMax(self, lists: List[Optional[ListNode]], pos: int) -> ListNode:
        elt = lists[pos]
        lists[pos] = lists[pos].next
        return elt

    def _cleanup(self, lists: List[Optional[ListNode]]):
        pos = 0
        while pos < len(lists):
            if not lists[pos]:
                del lists[pos]
                continue

            pos += 1

    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        res = ListNode()
        it = res

        while len(lists) != 0:
            self._cleanup(lists)

            maxPos = self._getMaxPosition(lists)
            if maxPos == -1:
                break

            node = self._popMax(lists, maxPos)

            it.next = node
            node.next = None
            it = it.next

        return res.next


l = [ListNode(1, ListNode(2, ListNode(3))), ListNode(-1)]

a = Solution()
print(a.mergeKLists(l))
