from typing import List


class Solution:
    def removeDuplicates(self, nums: List[int]) -> int:
        if len(nums) == 0:
            return 0

        ret = len(nums)
        i = 0

        while i < ret:
            while i + 1 < ret and nums[i] == nums[i + 1]:
                nums[i:] = nums[i + 1:]
                ret -= 1

            i += 1

        return ret



a = Solution()
arr = [1,1,1,1,1,1,2,2,2,3,3,3,3]
print(a.removeDuplicates(arr))
print(arr)
