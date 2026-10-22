import java.util.Arrays;
import java.util.Scanner;

public class ItsAllMeat {

    public static void main(String[] args) {
        int t, n, q, qualified, x, p;
        Scanner sc = new Scanner(System.in);
        t = sc.nextInt();
        while (t-- != 0) {
            n = sc.nextInt();
            q = sc.nextInt();
            sc.nextLine();
            int[] a = Arrays.stream(sc.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            qualified = 0;
            for (int i = 0; i < n; i++) {
                if (a[i]%3 == 0 || a[i] == 5 || a[i] == 10) {
                    qualified++;
                }
            }
            System.out.print(qualified+" ");
            while (q-- != 0) {
                p = sc.nextInt();
                x = sc.nextInt();
                p--;

                if (a[p]%3 == 0 || a[p] == 5 || a[p] == 10) {
                    qualified--;
                }

                a[p] = x;

                if (a[p]%3 == 0 || a[p] == 5 || a[p] == 10) {
                    qualified++;
                }

                System.out.print(qualified+" ");
            }
            System.out.println();
        }
    }

}
