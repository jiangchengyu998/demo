import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // 读取元素个数
        int n = sc.nextInt();

        // 保存原数组
        List<Integer> list = new ArrayList<>();

        // 统计频率
        Map<Integer, Integer> freqMap = new HashMap<>();

        // 记录第一次出现的位置（保证相同频率时保持原顺序）
        Map<Integer, Integer> firstIndexMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            int num = sc.nextInt();

            list.add(num);

            // 统计频率
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);

            // 记录第一次出现的位置
            firstIndexMap.putIfAbsent(num, i);
        }

        // 排序
        List<Integer> result = list.stream()
                .sorted((a, b) -> {

                    // 先按频率降序
                    int freqCompare = freqMap.get(b) - freqMap.get(a);

                    if (freqCompare != 0) {
                        return freqCompare;
                    }

                    // 频率相同，按第一次出现位置升序
                    return firstIndexMap.get(a) - firstIndexMap.get(b);
                })
                .collect(Collectors.toList());

        // 输出结果
        for (int i = 0; i < result.size(); i++) {
            System.out.print(result.get(i));

            if (i != result.size() - 1) {
                System.out.print(" ");
            }
        }
    }
}