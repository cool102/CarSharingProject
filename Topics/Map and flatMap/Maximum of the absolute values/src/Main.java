import java.util.Arrays;
import java.util.Scanner;

public class Main {

    /**
     * Returns the largest absolute value in the array of numbers.
     *
     * @param numbers the input array of String integer numbers
     * @return the maximum integer absolute value in the array
     */
    public static int maxAbsValue(String[] numbers) {
        int maximum = Arrays.stream(numbers)
                .map(Integer::parseInt)
                .map(Math::abs)
                .max((i1,i2)->i1-i2)
                .orElse(10000000);
        return maximum;
    }

    // Don't change the code below
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(maxAbsValue(scanner.nextLine().split("\\s+")));
    }
}