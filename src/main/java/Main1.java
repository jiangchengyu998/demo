import java.io.*;
import java.util.*;

public class Main1 {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String[] nk = br.readLine().split(" ");
        int N = Integer.parseInt(nk[0]);
        int K = Integer.parseInt(nk[1]);

        if (N == 0 || K == 0) {
            System.out.println();
            return;
        }

        int[] arr = new int[N];
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) {
            arr[i] = Integer.parseInt(st.nextToken());
        }

        Deque<Integer> dq = new ArrayDeque<>();
        List<Integer> res = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            // 移除超出窗口左端的元素
            while (!dq.isEmpty() && dq.peekFirst() <= i - K) {
                dq.pollFirst();
            }

            // 维护双端队列：只保留最早出现的负数
            while (!dq.isEmpty() && arr[dq.peekLast()] >= 0) {
                dq.pollLast();
            }

            // 当前下标入队
            dq.addLast(i);

            // 窗口形成后开始记录结果
            if (i >= K - 1) {
                int val = arr[dq.peekFirst()];
                res.add(val < 0 ? val : 0);
            }
        }

        // 输出
        StringBuilder sb = new StringBuilder();
        for (int num : res) {
            sb.append(num).append(" ");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        System.out.println(sb);
    }
}