import java.util.Scanner;

public class GenerationalTriplets {

    public static void main(String[] args) {
        int t, mod = 1000000007;
        Scanner sc = new Scanner(System.in);
        t = sc.nextInt();
        long n, prev;

        long[] dp = new long[65];
        long[] two = new long[65];
        dp[0] = 0;
        dp[1] = 0;
        dp[2] = 1;

        two[1] = 1;
        two[2] = 3;
        prev = 2;

        for (int i = 3; i < 65; i++) {
            dp[i] = ((1+2*dp[i-2])%mod + dp[i-1])%mod;
            two[i] = (prev*2) + prev;
            prev = 2*prev;
        }

        while (t-- != 0) {
            n = sc.nextLong();

            // find the binary length of n
            int length = binaryLength(n);
            long possible = dp[length-1];
//            System.out.println("Length : "+length);
//            System.out.println("Possible : "+possible);
            if (two[length] <= n) {
                int[] binaryRep = binaryRep(n, length);
                int startingOnes = startingOnesCount(binaryRep);

                int j;
                boolean breakReached = false;
                for ( j = length-3; j >= 1; j--) {
                    if (binaryRep[j] == 1) {
                        if (breakReached) {
                            possible = ((possible + dp[j+1]) % mod + dp[j]) % mod;
                        }
                        else if  ((length-1)%2 == j%2) {
                            possible = ((possible + dp[j+1]) % mod + dp[j]) % mod;
                        }
                    }
                    else  {
                        breakReached = true;
                        if (startingOnes%2 == 1) {
                            break;
                        }
                    }
                }
//                System.out.println("Possible after moving in : "+possible);

                int totalOnes = getTotalOnes(binaryRep);

                if (startingOnes%2 == 0) {
                    int restOnes = totalOnes - startingOnes;
                    possible = ((restOnes + (startingOnes/2))%mod + possible)%mod;
                }
                else {
                    possible = (possible + (startingOnes/2)%mod)%mod;
                }
                System.out.println(possible);
            }
            else {
                System.out.println(possible);
            }

        }
    }

    private static int startingOnesCount(int[] binaryRep) {
        int lnth = binaryRep.length;
        int total = 0;
        for (int i = lnth-1; i > -1; i--) {
            if (binaryRep[i] == 0) {
                break;
            }
            total++;
        }
        return total;
    }

    private static int getTotalOnes(int[] binaryRep) {
        int ones = 0;
        for (int num : binaryRep) {
            if (num == 1) {
                ones++;
            }
        }
        return ones;
    }

    private static int[] binaryRep(long n, int length) {

        int[] binaryRep = new int[length];
        int index = 0;
        while (n > 0) {

            if ((n&1) == 1) {
                binaryRep[index] = 1;
            }
            else {
                binaryRep[index] = 0;
            }

            n = n >> 1;
            index++;
        }
        return binaryRep;
    }

    private static int binaryLength(long n) {
        int length = 0;
        while (n > 0) {
            length++;
            n = n >> 1;
        }
        return length;
    }

}
