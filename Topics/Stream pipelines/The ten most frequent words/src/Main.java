import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Comparator<Map.Entry<String, Long>> entryComparatorByKey = Map.Entry.comparingByKey();
        Comparator<Map.Entry<String, Long>> entryComparatorByValue = Map.Entry.comparingByValue();

        final int limit = 10;

        Arrays.stream(scanner.nextLine().split("\\s+"))
                .map(String::toLowerCase)
                .map(Main::getWordOnly)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(entryComparatorByValue.reversed().thenComparing(entryComparatorByKey))
                .limit(limit)
                .forEach(entry -> System.out.println(entry.getKey()));

    }

    private static String getWordOnly(String text) {
        Pattern pattern = Pattern.compile("[a-z0-9]+");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }
}