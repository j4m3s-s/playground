class Solution:
    def lengthOfLastWord(self, s: str) -> int:
        return len([elt for elt in s.split(" ") if elt != ""][-1])


a = Solution()
print(a.lengthOfLastWord("toto toto   "))
