package Huffman;

class InternalNode extends Node {
    Node left;
    Node right;

    public InternalNode(int data) {
        super(data);
        left = null;
        right = null;
    }

    public InternalNode(int data, Node left, Node right) {
        super(data);
        this.left = left;
        this.right = right;
    }
}
