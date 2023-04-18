public class Principal {
    public static void main (String[] args){
        HashTable table = new HashTable();
        table.insert(1, 1);
        System.out.println(table.search(2));

    }
}
