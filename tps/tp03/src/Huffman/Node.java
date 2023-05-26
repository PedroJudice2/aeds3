package Huffman;

import java.io.Serializable;

abstract class Node implements Serializable {
    protected int amount;

    public Node(int amount) {
        this.amount = amount;
    }
}