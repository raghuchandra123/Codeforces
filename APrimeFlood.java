import java.util.*;

public class APrimeFlood {

    private static long mod = 998244353L;
    private static Map<Integer, PrimeFactors> numberToPrimeFactors = new HashMap<>();
    private static int[] primes = new int[101];
    private static Map<Integer, Map<Integer, Integer>> xForPair = new HashMap<>();
    private static Map<Integer, List<Integer>> primesProductToPowersProductSorted = new HashMap<>();

    static long twoPowers(int n) {
        long result = 1;
        long base = 2;

        while (n > 0) {
            if ((n & 1) != 0) {
                result = (result * base) % mod;
            }

            base = (base * base) % mod;
            n >>= 1;
        }

        return result;
    }

    private static class PrimeFactors {

        int product;
        List<Integer> primeFactors;

        PrimeFactors() {
            primeFactors = new ArrayList<>();
        }

        PrimeFactors(int _p, List<Integer> _primeFactors) {
            product = _p;
            primeFactors = _primeFactors;
        }

        @Override
        public String toString() {
            return "PrimeFactors{" +
                    "product=" + product +
                    ", primeFactors=" + primeFactors +
                    '}';
        }
    }

    private static class Node {

        int freq, rightNext, count;

        Node() {
            freq = 0;
            rightNext = 0;
            count = 0;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }

        public void setRightNext(int rightNext) {
            this.rightNext = rightNext;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static void main(String[] args) {
        int t, n, maxN, initialFreq, rightIndex, count, l;
        Scanner sc = new Scanner(System.in);
        t = sc.nextInt();
        boolean isPrime = true;
        n = 0;
        int index;

        for (int i = 2; i < 548; i++) {
            index = 0;
            isPrime = true;
            while (index  < n && (primes[index]*primes[index]) <= i) {
                if ((i%primes[index]) == 0) {
                    isPrime = false;
                    break;
                }
                index++;
            }

            if (isPrime) {
                primes[n] = i;
                n++;
            }
        }

//        System.out.println("All Primes");
//        System.out.println(Arrays.toString(primes));

        while (t-- != 0) {
            n = sc.nextInt();
            sc.nextLine();
            int[] a = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            maxN = maxOfA(a, n);
//            System.out.println("Max Val : "+maxN);
            Node[] bucket = new Node[maxN+1];

            for (int i = 0; i <= maxN; i++) {
                bucket[i] = new Node();
            }

            for (int i = 0; i < n; i++) {
                initialFreq = bucket[a[i]].freq;
                bucket[a[i]].setFreq(initialFreq+1);
            }

            a = null;

            rightIndex = maxN+1;
            count = 0;
            for (int i = maxN; i > 0; i--) {
                l = maxN - i + 1;
                if (bucket[i].freq > 0) {
                    bucket[i].rightNext = rightIndex;
                    rightIndex = i;
                }
                else {
                    bucket[i].rightNext = rightIndex;
                }
                bucket[l].count = bucket[l-1].count + bucket[l].freq;
            }

//            for (int i = 1; i <= maxN; i++) {
//                System.out.println(i + " "+bucket[i].freq+" "+bucket[i].rightNext+" "+bucket[i].count);
//            }

            long sumOfX = 0;
            for (int i = 1; i <= maxN; i++) {
                if (bucket[i].freq > 0) {
                    sumOfX += calculateSumOfX( i, bucket, xForPair);
                }
            }
            System.out.println(sumOfX);
        }
    }

    private static long calculateSumOfX(int val, Node[] bucket, Map<Integer, Map<Integer, Integer>> xForPair) {
        PrimeFactors primeFactors;
        if (numberToPrimeFactors.containsKey(val)) {
            primeFactors = numberToPrimeFactors.get(val);
        }
        else {
            primeFactors = generatePrimeFactors(val);
            numberToPrimeFactors.put(val, primeFactors);
        }

//        System.out.println("Prime Factors for : "+val+" is "+primeFactors);

        long sumX = 0;
        int primeFactorsProduct = primeFactors.product;
        List<Integer> sortedPowersProduct;
        if (primesProductToPowersProductSorted.containsKey(primeFactorsProduct)) {
            sortedPowersProduct = primesProductToPowersProductSorted.get(primeFactorsProduct);
        }
        else {
            LinkedList<Integer> powersProduct = new LinkedList<>();
            generatePrimePowerProducts(powersProduct, primeFactors, 0, 1);
            sortedPowersProduct = new ArrayList<>(powersProduct);
            Collections.sort(sortedPowersProduct);
            primesProductToPowersProductSorted.put(primeFactorsProduct, sortedPowersProduct);
        }
//        System.out.println(" Sorted Powers Products : "+primeFactors.product+" is "+sortedPowersProduct);

        int indexInSortedPowers = Collections.binarySearch( sortedPowersProduct, val), prevIndex = val;
        int currentProduct = 0, newIndex;
        long prevCombinations = 1;

        int prevCount = bucket[val-1].count;

        for (int i = indexInSortedPowers; i <= sortedPowersProduct.size(); i++) {
            if (i != sortedPowersProduct.size()) {
                currentProduct = sortedPowersProduct.get(i);
            }

            if ((i == sortedPowersProduct.size()) || (sortedPowersProduct.get(i) >= bucket.length)) {
                newIndex = bucket.length;
            }
            else if (bucket[currentProduct].freq > 0) {
                newIndex = currentProduct;
            }
            else {
                newIndex = bucket[currentProduct].rightNext;
            }

            if (prevIndex != newIndex) {

                // prevIndex and newIndex are a[i] values

//                System.out.println("Product one : "+sortedPowersProduct.get(i-1)+" adjacent to the right a value "+prevIndex);
//                if (i != sortedPowersProduct.size()) {
//                    System.out.println("Product two : " + sortedPowersProduct.get(i) + " adjacent to the right a value " + newIndex);
//                }

                int count = bucket[newIndex-1].count - bucket[prevIndex-1].count;
                long x = getDPValue(val, sortedPowersProduct.get(i-1));
//                System.out.println("X : "+x);

                if (prevIndex == val) {
//                    System.out.println("Count : "+count);
//
//                    System.out.println("Increment on sumX : "+(((((twoPowers[count] - twoPowers[count - bucket[val].freq])%mod + mod)%mod*x)%mod)*prevCombinations)%mod);
                    sumX += (((((twoPowers(count) - twoPowers(count - bucket[val].freq))%mod + mod)%mod*x)%mod)*prevCombinations)%mod;
                }
                else {
//                    System.out.println("Count : "+count);
//                    System.out.println("Increment on sumX : "+((((twoPowers[count] - 1)*x)%mod)*prevCombinations)%mod);
                    sumX += ((((twoPowers(count) - 1)*x)%mod)*prevCombinations)%mod;
                }



                prevCombinations = twoPowers(bucket[newIndex-1].count - prevCount) - twoPowers(bucket[newIndex-1].count - prevCount - bucket[val].freq);
                prevCombinations = (prevCombinations + mod)%mod;
                prevIndex = newIndex;
            }

            if ((i == sortedPowersProduct.size()) || (sortedPowersProduct.get(i) > bucket.length)) {
                break;
            }
        }
//        System.out.println("For val : "+val+" sumX id : "+sumX);
        return sumX;
    }

    private static long getDPValue(int val, int fnl) {
        if (val == fnl) {
            return val;
        }

        if (xForPair.containsKey(val) && xForPair.get(val).containsKey(fnl)) {
            return xForPair.get(val).get(fnl);
        }

        int tmpVal = val, tmpFnl = fnl;

        val--;
        fnl--;


        PrimeFactors primeFactors;
        if (numberToPrimeFactors.containsKey(val)) {
            primeFactors = numberToPrimeFactors.get(val);
        }
        else {
            primeFactors = generatePrimeFactors(val);
            numberToPrimeFactors.put(val, primeFactors);
        }

        int primeFactorsProduct = primeFactors.product;
        List<Integer> sortedPowersProduct;
        if (primesProductToPowersProductSorted.containsKey(primeFactorsProduct)) {
            sortedPowersProduct = primesProductToPowersProductSorted.get(primeFactorsProduct);
        }
        else {
            LinkedList<Integer> powersProduct = new LinkedList<>();
            generatePrimePowerProducts(powersProduct, primeFactors, 0, 1);
            sortedPowersProduct = new ArrayList<>(powersProduct);
            Collections.sort(sortedPowersProduct);
            primesProductToPowersProductSorted.put(primeFactorsProduct, sortedPowersProduct);
        }

        int idx = Collections.binarySearch(sortedPowersProduct, fnl);

        if (idx < 0)
            idx = -idx - 2;

        long x = getDPValue(val, sortedPowersProduct.get(idx));
        Map<Integer, Integer> fnlMap;
        if (!xForPair.containsKey(tmpVal)) {
            fnlMap = new HashMap<>();
            xForPair.put(tmpVal, fnlMap);
        }
        else {
            fnlMap = xForPair.get(tmpVal);
        }
        fnlMap.put(tmpFnl, (int)x);
        return x;
    }

    private static void generatePrimePowerProducts(LinkedList<Integer> powersProduct, PrimeFactors primeFactors, int index, int product) {

        if (index == primeFactors.primeFactors.size()) {
            powersProduct.add(product);
            return;
        }

        int currentPrime = primeFactors.primeFactors.get(index);
        index++;
        int power = 1;

        while (product*power <= 300000) {
            generatePrimePowerProducts( powersProduct, primeFactors, index, product*power);
            power = power*currentPrime;
        }
    }

    private static PrimeFactors generatePrimeFactors(int val) {
        List<Integer> primeFactors = new ArrayList<>();
        int product = 1;
        int index =0;
        while (index < primes.length && ((primes[index] * primes[index]) <= val)) {
            if ((val%primes[index]) == 0) {
                primeFactors.add(primes[index]);
                product = product*primes[index];
            }

            while ((val%primes[index]) == 0) {
                val = val/primes[index];
            }
            index++;
        }

        if (val > 1) {
            primeFactors.add(val);
            product = product*val;
        }
        return new PrimeFactors( product, primeFactors);
    }

    private static int maxOfA(int[] a, int n) {
        int max = a[0];
        for (int i = 1; i < n; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }
}
