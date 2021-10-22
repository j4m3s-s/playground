from typing import List

class Solution:
    def _getNextDifferentPosition(self, nums, val, i):
        i += 1

        while i < len(nums) and nums[i] == val:
            i += 1

        return i

    def removeElement(self, nums: List[int], val: int) -> int:
        if len(nums) == 0:
            return 0

        ret = len(nums)
        i = 0

        while i < ret:
            if nums[i] != val:
                i += 1
                continue

            nextPos = self._getNextDifferentPosition(nums, val, i)
            nums[i:] = nums[nextPos:]
            ret -= (nextPos - i)

            i += 1

        return ret

a = Solution()
arr = [1,2,3,4,5,6,7,7,7,7,7,7,7,7,8,9]
print(a.removeElement(arr, 7))
print(arr)
