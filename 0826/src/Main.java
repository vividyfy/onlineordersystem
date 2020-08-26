import java.util.*;
public class Main {
    public static int cutRope(int target) {
        if (target <= 1 || target == 2 || target == 3) {
            return target - 1;
        }
        int n = target%3==0?target/3:target/3+1;
        int m = 0;
        if (target % 3 != 0) {
            m = 3 - target % 3;
        }
        int a = n - m;
        return (int)(Math.pow(2,m) * Math.pow(3,a));
    }

    public static void main(String[] args) {
        System.out.println(cutRope(15));
    }



}