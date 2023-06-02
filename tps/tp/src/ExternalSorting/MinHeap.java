package ExternalSorting;

import Filme.Filme;

/**
 * The MinHeap class represents a minimum heap data structure that stores nodes
 * containing Filme objects, their index and cursor.
 * The heap is implemented using an array and supports the following operations:
 * - insert: adds a new node to the heap
 * - extractMin: removes and returns the node with the smallest Filme id
 * - peek: returns the node with the smallest Filme id without removing it from
 * the heap
 * - isEmpty: returns true if the heap is empty, false otherwise
 *
 * This class is used to implement external sorting algorithms that require
 * sorting large amounts of data that cannot fit into memory.
 * By using a minimum heap, we can efficiently get the smallest Node by
 * repeatedly
 * extracting the minimum element from the heap.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
class MinHeap {
    private Node[] heap;
    private int size;
    private int capacity;

    /**
     * Constructs an empty heap with a given capacity.
     * 
     * @param capacity
     */
    public MinHeap(int capacity) {
        this.heap = new Node[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    /**
     * calculates the index of the parent of the node at index i
     * 
     * @param i
     * @return
     */
    private int parent(int i) {
        return (i - 1) / 2;
    }

    /**
     * calculates the index of the left child of the node at index i
     * 
     * @param i
     * @return
     */
    private int leftChild(int i) {
        return 2 * i + 1;
    }

    /**
     * calculates the index of the right child of the node at index i
     * 
     * @param i
     * @return
     */
    private int rightChild(int i) {
        return 2 * i + 2;
    }

    /**
     * swaps the nodes at indexes i and j
     * 
     * @param i
     * @param j
     */
    private void swap(int i, int j) {
        Node temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    /**
     * restores the heap property by moving the node at index i up the heap
     * 
     * @param i
     */
    private void heapifyUp(int i) {
        int parent = parent(i);
        while (i > 0 && heap[parent].filme.getId() > heap[i].filme.getId()) {
            swap(parent, i);
            i = parent;
            parent = parent(i);
        }
    }

    /**
     * restores the heap property by moving the node at index i down the heap
     * 
     * @param i
     */
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

    /**
     * inserts a new Movie into the heap
     * 
     * @param filme
     * @param index
     * @param cursor
     */
    public void insert(Filme filme, int index, long cursor) {
        if (size >= capacity) {
            throw new RuntimeException("Heap is full");
        }
        heap[size] = new Node(filme, index, cursor);
        heapifyUp(size);
        size++;
    }

    /**
     * removes and returns the node with the smallest Movie id
     * 
     * @return
     */
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

    /**
     * returns the node with the smallest Movie id without removing it from the
     * heap
     * 
     * @return
     */
    public Node peek() {
        if (size <= 0) {
            throw new RuntimeException("Heap is empty");
        }
        return heap[0];
    }

    /**
     * returns true if the heap is empty, false otherwise
     * 
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }
}

/**
 * The Node class represents a node in the MinHeap class.
 * Each node contains a Filme object, its index and cursor.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @see MinHeap
 */
class Node {
    Filme filme;
    int index;
    long cursor;

    /**
     * Constructs a new Node with a given Filme object, index and cursor.
     * 
     * @param filme
     * @param index
     * @param cursor
     */
    Node(Filme filme, int index, long cursor) {
        this.filme = filme;
        this.index = index;
        this.cursor = cursor;
    }
}
