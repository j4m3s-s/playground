from typing import List

class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        if len(nums) == 0:
            return 0

        max = nums[0]
        sum = nums[0]
        for i in range(0, len(nums)):
            sum = nums[i]
            for j in range(i + 1, len(nums)):
                sum += nums[j]

                if sum > max:
                    max = sum

        return max

a = Solution()
print(a.maxSubArray([-2,1,-3,4,-1,2,1,-5,4]))
