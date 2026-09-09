import java.util.*;

public class DivMEX {

    private static class MinIndexSegmentTree {

        private final int n;
        private final int[] values;
        private final int[] tree;

        public MinIndexSegmentTree(int n) {
            this.n = n;
            this.values = new int[n];      // Initially all 0
            this.tree = new int[4 * n];

            build(1, 0, n - 1);
        }

        public int getIndexValue(int index) {
            return values[index];
        }

        private void build(int node, int l, int r) {
            if (l == r) {
                tree[node] = l;
                return;
            }

            int mid = (l + r) / 2;

            build(node * 2, l, mid);
            build(node * 2 + 1, mid + 1, r);

            tree[node] = minIndex(
                    tree[node * 2],
                    tree[node * 2 + 1]
            );
        }

        /*
         * Returns the index having:
         *
         * 1. Smaller value
         * 2. If values are equal, smaller index
         */
        private int minIndex(int i, int j) {

            if (values[i] < values[j]) {
                return i;
            }

            if (values[i] > values[j]) {
                return j;
            }

            // Same value -> first index
            return Math.min(i, j);
        }

        /*
         * values[index] = value
         *
         * O(log n)
         */
        public void update(int index, int value) {
            values[index] = value;
            update(1, 0, n - 1, index);
        }

        private void update(int node, int l, int r, int index) {

            if (l == r) {
                tree[node] = index;
                return;
            }

            int mid = (l + r) / 2;

            if (index <= mid) {
                update(node * 2, l, mid, index);
            } else {
                update(node * 2 + 1, mid + 1, r, index);
            }

            tree[node] = minIndex(
                    tree[node * 2],
                    tree[node * 2 + 1]
            );
        }

        /*
         * Returns the SMALLEST INDEX having
         * the SMALLEST VALUE in [ql, qr].
         *
         * O(log n)
         */
        public int query(int ql, int qr) {
            return query(1, 0, n - 1, ql, qr);
        }

        private int query(
                int node,
                int l,
                int r,
                int ql,
                int qr
        ) {

            // Completely outside the query range
            if (r < ql || l > qr) {
                return -1;
            }

            // Completely inside the query range
            if (ql <= l && r <= qr) {
                return tree[node];
            }

            int mid = (l + r) / 2;

            int leftIndex = query(
                    node * 2,
                    l,
                    mid,
                    ql,
                    qr
            );

            int rightIndex = query(
                    node * 2 + 1,
                    mid + 1,
                    r,
                    ql,
                    qr
            );

            if (leftIndex == -1) {
                return rightIndex;
            }

            if (rightIndex == -1) {
                return leftIndex;
            }

            return minIndex(leftIndex, rightIndex);
        }
    }

    static List<Node> primesPowerLined;

    private static class Node {
        int base, value, exponent;

        public Node(int _base, int _value, int _exponent) {
            base = _base;
            value = _value;
            exponent = _exponent;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "base=" + base +
                    ", value=" + value +
                    ", exponent=" + exponent +
                    '}';
        }
    }

    public static void main(String[] args) {
        int t, n, index = 0, j;
        Scanner sc = new Scanner(System.in);
        primesPowerLined = new ArrayList<>();
        t = sc.nextInt();
        int[] primes = new int[86];
        primes[0] = 2;

        boolean isPrime;

        for (int i = 2; i < 448; i++) {

            isPrime = true;
            j = 0;
            while (j < index && (primes[j]*primes[j]) <= i) {
                if (i%primes[j] == 0) {
                    isPrime = false;
                    break;
                }
                j++;
            }

            if (isPrime) {
                primes[index] = i;
                index++;
            }

        }

        for (int i = 0; i < primes.length; i++) {
            int exponent = 1;
            int product=primes[i];
            int base = primes[i];

            while (product <= 200000) {
                primesPowerLined.add(new Node(base, product, exponent));
                exponent++;
                product = product*base;
            }
        }

        for (long i = 449; i <= 200004; i++) {

            isPrime = true;
            j = 0;
            while ((j < primes.length) && (primes[j]*primes[j]) <= i) {
                if (i%primes[j] == 0) {
                    isPrime = false;
                    break;
                }
                j++;
            }

            if (isPrime) {
                primesPowerLined.add(new Node((int)i, (int)i, 1));
                index++;
            }

        }

        Collections.sort(primesPowerLined, (x,y) -> {
            return x.value - y.value;
        });

        index = 0;
        Map<Integer, Integer> primeIndexMap = new HashMap<>();
        for (Node node : primesPowerLined) {
            primeIndexMap.put(node.value, index);
            index++;
        }

//        for (int i = 0; i < 30; i++) {
//            System.out.println(primesPowerLined.get(i));
//        }


//        int tmp = t , z=0;
        while (t-- != 0) {
//            z++;
            n = sc.nextInt();
            int minPrimeMissing = -1;
            sc.nextLine();
            int[] a = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

            MinIndexSegmentTree segTree = new MinIndexSegmentTree(primeIndexMap.size());

            Set<Integer> x = new HashSet<>();
//            Map<Integer, Stack<Node>> maps = new HashMap<>();
            for (int i = 0; i < n; i++) {
                Map<Integer, Integer> lcm = getPrimeFactors(a[i], primes);

                for (Map.Entry<Integer, Integer> entry : lcm.entrySet()) {
                    int prime = entry.getKey();
                    int exponent = entry.getValue();
                    int value = 1;

                    for (int k = 1; k <= exponent; k++) {
                        value = value*prime;
                        segTree.update(primeIndexMap.get(value), i+1);
                    }

                }

                index = primeIndexMap.size() - 1;
                while (index >= 0) {
                    index = segTree.query(0, index);
                    if (minPrimeMissing >= index) {
                        break;
                    }
                    j = segTree.getIndexValue(index);
                    if (j == (i+1)) {
                        break;
                    }
                    x.add(primesPowerLined.get(index).value);
                    index--;
                }

                while (x.contains(primesPowerLined.get(minPrimeMissing+1).value)) {
                    minPrimeMissing++;
                }
            }

            List<Integer> xs = new ArrayList<>(x);
            Collections.sort(xs);

            System.out.println(xs.size());
            for (Integer xi : xs) {
                System.out.print(xi+" ");
            }
            System.out.println();
        }
    }

    private static Map<Integer, Integer> getPrimeFactors(int n, int[] primes) {
        int j = 0;
        Map<Integer, Integer> primeFactors = new HashMap<>();
        int prev = 0;
        while ((j < primes.length) &&  (primes[j]*primes[j]) <= n) {
            while (n%primes[j] == 0) {
                n = n/primes[j];
                prev++;
            }

            if (prev != 0) {
                primeFactors.put(primes[j], prev);
            }
            prev = 0;
            j++;
        }

        if (n != 1) {
            primeFactors.put(n, 1);
        }
        return primeFactors;
    }

}


