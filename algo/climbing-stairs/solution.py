class Solution:
    def climbStairs(self, n: int) -> int:
        pass

        arr = []

        i = n
        j = 0

        while i >= 0:
            arr.append([1 for e in range(0, i)] + [2 for e in range(0, j)])

            i -= 2
            j += 1

        return arr

a = Solution()
print(a.climbStairs(10))
