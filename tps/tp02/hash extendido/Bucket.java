public class Bucket {
    public Node[] node;
    private int depth;
    private int Maxsize = 388;
    private int size = 0;

    Bucket(int depth) {
        node = new Node[Maxsize];
        this.depth = depth;
    }

    public void insert(int key, long value) {
        node[size++] = new Node(key, value);
        sort();
    }

    public boolean isFull() {
        return size == Maxsize;
    }

    void sort() {
        for (int i = 1; i < size; i++) {
            Node tmp = node[i];
            int j = i - 1;

            while ((j >= 0) && (node[j].getKey() > tmp.getKey())) {
                node[j + 1] = node[j];
                j--;
            }
            node[j + 1] = tmp;
        }
    }

    public void remove(int index) {
        for (int i = index; i < size - 1; i++) {
            node[i] = node[i + 1];
        }
        node[--size] = null;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getSize() {
        return size;
    }
}

class Node {
    private int key;
    private long value;

    public Node(int key, long value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
