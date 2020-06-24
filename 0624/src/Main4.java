import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
public class Main4 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while(in.hasNext()){
            int n = in.nextInt();
            int[] A = new int[n];
            for(int i=0;i<n;i++){
                A[i] = in.nextInt();
            }
            int start = 0;
            ArrayList<int[]> result = new ArrayList<int[]>();
            Permutation(A,start,n,result);
            Set<String> sortResult = new TreeSet<String>();
            for(int[] out : result){
                if(isLegal(A,out,n)){
                    StringBuilder sb = new StringBuilder();
                    for(int i=0;i<n-1;i++){
                        sb.append(out[i]+" ");
                    }
                    sb.append(out[n-1]);
                    sortResult.add(sb.toString());
                }
            }
            for(String list:sortResult){
                System.out.println(list);
            }
            in.close();
        }

    }
    private static boolean isLegal(int[] in,int[] out,int n){

        LinkedList<Integer> stack = new LinkedList<Integer>();
        int i=0;
        int j=0;
        while(i<n){
            if(in[i] == out[j]){
                i++;
                j++;
            }else{
                if(stack.isEmpty()){
                    stack.push(in[i]);
                    i++;
                }else{
                    int top = stack.peek();
                    if(top ==out[j]){
                        j++;
                        stack.pop();
                    }else if(i<n){
                        stack.push(in[i]);
                        i++;
                    }
                }
            }
        }
        while(!stack.isEmpty() && j<n){
            int top = stack.pop();
            if(top == out[j]){
                j++;
            }else{
                return false;
            }
        }
        return true;
    }
    private static void Permutation(int[] A,int start,int n,ArrayList<int[]>
            result){
        if(start == n){
            return;
        }
        if(start == n-1){
            int[] B = A.clone();
            result.add(B);
            return;
        }
        for(int i=start;i<n;i++){
            swap(A,start,i);
            Permutation(A,start+1,n,result);
            swap(A,start,i);
        }
    }
    private static void swap(int[] A,int i,int j){
        int t = A[i];
        A[i] = A[j];
        A[j] = t;
    }
}
