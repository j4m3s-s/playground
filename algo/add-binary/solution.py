class Solution:
    def addBinary(self, a: str, b: str) -> str:
        res = ""

        i = len(a) - 1
        j = len(b) - 1
        carry = 0

        while i >= 0 and j >= 0:
            current = ord(a[i]) - ord('0') + ord(b[j]) - ord('0') + carry

            carry = current // 2
            current = current % 2

            res = chr(ord('0') + current) + res
            i -= 1
            j -= 1

        while i >= 0:
            current = ord(a[i]) - ord('0') + carry

            carry = current // 2
            current = current % 2

            res = chr(ord('0') + current) + res
            i -= 1

        while j >= 0:
            current = ord(b[j]) - ord('0') + carry

            carry = current // 2
            current = current % 2

            res = chr(ord('0') + current) + res
            j -= 1

        if carry:
            res = "1" + res

        return res


a = Solution()
print(a.addBinary("100", "111"))
