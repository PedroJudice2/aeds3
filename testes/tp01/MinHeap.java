public class MinHeap {
    private Node[] heap;
    private int size;
    private int capacity;

    public MinHeap(int capacity) {
        this.heap = new Node[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void heapifyUp(int i) {
        int parent = parent(i);
        while (i > 0 && heap[parent].filme.getId() > heap[i].filme.getId()) {
            swap(parent, i);
            i = parent;
            parent = parent(i);
        }
    }

    private void heapifyDown(int i) {
        int min = i;
        int left = leftChild(i);
        int right = rightChild(i);

        if (left < size && heap[left].filme.getId() < heap[min].filme.getId()) {
            min = left;
        }

        if (right < size && heap[right].filme.getId() < heap[min].filme.getId()) {
            min = right;
        }

        if (min != i) {
            swap(i, min);
            heapifyDown(min);
        }
    }

    public void insert(Filme filme, int index, long cursor) {
        if (size >= capacity) {
            throw new RuntimeException("Heap is full");
        }
        heap[size] = new Node(filme, index, cursor);
        heapifyUp(size);
        size++;
    }

    public Node extractMin() {
        if (size <= 0) {
            throw new RuntimeException("Heap is empty");
        }
        Node min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapifyDown(0);
        return min;
    }

    public Node peek() {
        if (size <= 0) {
            throw new RuntimeException("Heap is empty");
        }
        return heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }
}

class Node {
    Filme filme;
    int index;
    long cursor;

    Node(Filme filme, int index, long cursor) {
        this.filme = filme;
        this.index = index;
        this.cursor = cursor;
    }
}
