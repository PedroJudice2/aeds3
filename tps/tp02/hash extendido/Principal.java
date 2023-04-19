import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        HashTable hash = new HashTable();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < 7787; i++) {
            hash.insert(i, i);
        }
        System.out.print("Qual numero vc quer: ");
        int key = sc.nextInt();
        System.out.println();

        long result = hash.search(key);

        if (result == -1)
            System.out.println("Nao achou");
        else
            System.out.println(result);

        sc.close();
    }
}
