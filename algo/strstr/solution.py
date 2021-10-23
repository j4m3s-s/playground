
class Solution:
    def strStr(self, haystack: str, needle: str) -> int:
        if len(needle) == 0:
            return 0

        i = 0
        while i < len(haystack):
            while i < len(haystack) and haystack[i] != needle[0]:
                i += 1

            if i == len(haystack):
                return -1

            if haystack[i: i + len(needle)] == needle:
                return i

            i += 1

        return -1

a = Solution()
print(a.strStr("hello", "o"))
