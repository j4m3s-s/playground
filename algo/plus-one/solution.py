from typing import List

class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        i = len(digits) - 1

        digits[-1] += 1
        carry = 0
        while i >= 0:
            if carry:
                digits[i] += 1
                carry = 0

            if digits[i] == 10:
                digits[i] = 0
                carry = 1

            i -= 1

        if carry:
            digits.insert(0, 1)

        return digits

a = Solution()
print(a.plusOne([9,9,9]))
