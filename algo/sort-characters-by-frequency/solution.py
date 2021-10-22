class Solution:
    def _getPositionToInsert(self, arr, val):
        i = 0
        while i < len(arr):
            if arr[i][1] < val[1]:
                return i - 1
            i += 1
        return i

    def frequencySort(self, s: str) -> str:
        dic = {}

        for c in s:
            if c not in dic:
                dic[c] = 1
            else:
                dic[c] += 1

        arr = []
        for key in dic:
            arr.append((key, dic[key]))

        arr = sorted(arr, key=lambda x: x[1], reverse=True)
        print(arr)

        res = ""
        for elt in arr:
            res += elt[1] * elt[0]

        return res

a = Solution()

for ex in [ "tree", "cccaaa", "Aabb", "raaeaedere"]:
    print(a.frequencySort(ex)) # == "eeeeaaarrd")
