package MyScanner;

import java.util.Scanner;

/**
 * This class is used to create a single instance of Scanner that can be used
 * throughout the program.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
public class MyScanner {
    public static Scanner sc = new Scanner(System.in);

    private MyScanner() {
    }
}
