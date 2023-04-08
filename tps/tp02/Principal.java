import java.util.Scanner;

class Principal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BPlusTree tree = new BPlusTree(8);

        for (int i = 7789; i > 0; i--)
            tree.insert(i, i);

        System.out.print("Qual id você deseja buscar: ");
        int numero = sc.nextInt();
        sc.nextLine();
        System.out.println(tree.search(numero));
    }
}