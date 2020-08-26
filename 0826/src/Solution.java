public class Solution {
    public int MoreThanHalfNum_Solution(int [] array) {
        if (array.length == 0) {
            return 0;
        }
        int preValue = array[0];
        int count = 1;
        for (int i = 1; i < array.length; i++) {
            if (array[i] == preValue) {
                count++;
            } else {
                count--;
                if (count == 0) {
                    preValue = array[i];
                    count = 1;
                }
            }
        }
        int n = 0;
        for (int j = 0; j < array.length; j++) {
            if (array[j] == preValue) {
                n++;
            }
        }
        return n>array.length/2?preValue:0;
    }
}