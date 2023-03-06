# Basically the problem is :
# - string as input, max_size is 200
# - contains "patterns"
# You want to give the number of strictly equal parts to share this cake
# Characters are smarties, and you wanna give the same smarties to everyone.
# Answer is the number of parts.

# This one

def solution(s):
    input_ = s
    # Primes from 0 up to sqrt of n (which is max 200)
    primes = [2, 3, 5, 7, 11, 13]

    # Removes primes that we don't need
    # Complexity O(1)
    n = len(input_)
    last = primes[-1]
    while last * last > n:
        primes.pop()
        if len(primes) == 0:
            return 1
        last = primes[-1]

    is_divisible = [True for _ in primes]

    # Get all divisors among primes
    for (i, p) in enumerate(primes):
        if n % p != 0:
            is_divisible[i] = False
            continue

        if not check_match_size(p, input_):
            is_divisible[i] = False

    # product of all primes that divide
    product = 1
    for (i, b) in enumerate(is_divisible):
        if b:
            product *= primes[i]
    nb_parts = product

    # List of primes we're interested in
    divisable_primes = []
    for (i, b) in enumerate(is_divisible):
        if b:
            divisable_primes.append(primes[i])

    while divisable_primes:
        test_nb = divisable_primes[-1]

        arr = input_[:len(input_) / nb_parts]
        # we only need to check a subset of the array
        if len(arr) % test_nb != 0 or not check_match_size(test_nb, arr):
            divisable_primes.pop()
        else:
            nb_parts *= test_nb

    if nb_parts == 1:
        c = input_[0]

        for v in input_:
            if v != c:
                return 1
        return len(input_)
    return nb_parts


# Complexity O(n)
def check_match_size(part_nb, array):
    part_sz = len(array) / part_nb

    for j in range(1, part_nb):
        for i in range(part_sz):
            if array[i] != array[i + j * part_sz]:
                return False
    return True
tests = [
("abcabcabcabc", 4),
("abccbaabccba", 2),
("aaaaaaa", 7),
("abcd", 1),
("a" * 200, 200),
 ]

for (test, sol) in tests:

    print(test)
    a = solution(test)
    print(a)
    assert a == sol
