import java.util.*;
import java.util.stream.Collectors;


class CollectorPalindrome {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String[] words = scanner.nextLine().split(" ");

        Map<Boolean, List<String>> palindromeOrNo = Arrays.stream(words)
                .collect(
                        Collectors.partitioningBy(w -> {
                            return isPalindrome(w);
                        })
                );

        palindromeOrNo = new LinkedHashMap<>(palindromeOrNo);

        System.out.println(palindromeOrNo);
    }

    private static boolean isPalindrome(String w) {
        StringBuilder candidate = new StringBuilder(w);

        return w.equals(candidate.reverse().toString());
    }
}