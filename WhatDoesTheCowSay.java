import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WhatDoesTheCowSay {

    private static int largestLen, k;

    private static class Node {

        List<Node> children;
        int startIndex, endIndex;

        public Node(int _startIndex) {
            startIndex = _startIndex;
            children = new ArrayList<>();
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }

        int getLengthOfChildren() {
            return endIndex - startIndex - 1;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }

    public static void main(String[] args) {
        int t, n;
        String s;
        Scanner sc = new Scanner(System.in);
        t = sc.nextInt();
        while (t-- != 0) {
            n = sc.nextInt();
            k = sc.nextInt();
            sc.nextLine();
            s = sc.nextLine();
            s = '('+s+')';

            Node[] parents = new Node[(n+2)/2];

            Node root = new Node(0);
            largestLen = 1;
            makeTree(s, 1, root, parents, 0);

            System.out.println(largestLen);
        }
    }

    private static int makeTree(String s, int i, Node node, Node[] parents, int index) {
        parents[index++] = node;

        while (true) {
            if (s.charAt(i) == ')') {
                node.setEndIndex(i);
                break;
            } else {
                Node childNode = new Node(i);
                node.addChild(childNode);
                i = makeTree(s, i + 1, childNode, parents, index);
            }
            i++;
        }

        if ((node.children.size()) == 0) {
            int left =0, right = index-1, mid;

            while (left < right) {
                mid = (left + right)/2;
                Node midNode = parents[mid];
                int minKReq = node.startIndex - midNode.startIndex - (index - 1 - mid);
                minKReq = minKReq/2;

                if (k >= minKReq) {

                    if (mid == 0) {
                        int lnt = minKReq + index - mid - 1 + Math.min(k - minKReq, (s.length() - 1 - node.startIndex - index)/2);
                        if (lnt > largestLen) {
                            largestLen = lnt;
                        }
                    }
                    else {
                        if (largestLen < (index - mid)) {
                            largestLen = index - mid;
                        }
                    }

                    right = mid;
                }
                else {
                    left = mid+1;
                }
            }
        }

        return i;

    }

}
