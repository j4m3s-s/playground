from typing import List


class Solution:
    def _getNextDifferentPosition(self, nums, i):
        val = nums[i]
        i += 1

        while i < len(nums) and nums[i] == val:
            i += 1

        return i - 1

    def removeDuplicates(self, nums: List[int]) -> int:
        if len(nums) == 0:
            return 0

        ret = len(nums)
        i = 0

        while i < ret:
            nextPos = self._getNextDifferentPosition(nums, i)
            nums[i:] = nums[nextPos:]
            ret -= (nextPos - i)

            i += 1

        return ret



a = Solution()
arr = [1,1,1,1,1,1,2,2,2,3,3,3,3]
print(a.removeDuplicates(arr))
print(arr)
