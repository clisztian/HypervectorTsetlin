package util;

public class NumbericToBit {

    public static bit[] convert8(int number) {
        bit[] bits = new bit[32];
        for (int i = 0; i < 8; i++) {
            bits[i] = (number & (1 << i)) != 0 ? bit.ONE : bit.ZERO;
        }
        return bits;
    }

    public static void main(String[] args) {
        int number = 130;
        bit[] bits = convert8(number);
        for (int i = 0; i < 8; i++) {
            System.out.print(bits[i] == bit.ONE ? "1" : "0");
        }
        System.out.println();
    }

    public enum bit {
        ZERO, ONE
    }

}
