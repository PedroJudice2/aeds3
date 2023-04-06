class Principal {
    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree(8);

        for (int i = 0; i < 100; i++)
            tree.insert(i, i);

        System.out.println(tree.search(27));
    }
}