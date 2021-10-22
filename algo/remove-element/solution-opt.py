from typing import List

class Solution:
    def removeElement(self, arr: List[int], val: int) -> int:
        if len(arr) == 0:
            return 0

        write = 0
        read = 0

        while read < len(arr):
            while arr[read] == val:
                read += 1

            if read == len(arr):
                break

            arr[write] = arr[read]
            read += 1
            write += 1

        return write

a = Solution()
arr = [1,2,3,4,5,6,7,7,7,7,7,7,7,7,8,9]
res = a.removeElement(arr, 7)
print(arr[:res])
