import java.util.Arrays;

public class Solution2 {
    public int FindGreatestSumOfSubArray(int[] array) {
        int sum = 0,max = 0;
        for (int i = 0; i < array.length; i++) {

            sum += array[i];
            if (sum <= 0) {
                sum = 0;

            } else if (sum > max) {
                max = sum;
            }
        }
        if (max == 0) {
            Arrays.sort(array);
            return array[array.length-1];
        }
        return max;
    }
}
