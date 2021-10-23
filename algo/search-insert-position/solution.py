from typing import List

class Solution:
    def searchInsert(self, nums: List[int], target: int) -> int:
        high = len(nums) - 1
        low = 0

        while low < high:
            mid = low + (high - low) // 2

            if nums[mid] == target:
                return mid
            elif target < nums[mid]:
                high = mid
            elif target > nums[mid]:
                low = mid + 1

        if nums[low] < target:
            return low + 1
        return low


a = Solution()

print(a.searchInsert([1,3,5,6], 5))
print(a.searchInsert([1,3,5,6], 2))
print(a.searchInsert([1,3,5,6], 7))
print(a.searchInsert([1,3,5,6], 0))
print(a.searchInsert([1], 0))
