package Huffman;

class LeafNode extends Node {
    int letter;

    public LeafNode(int letter, int amount) {
        super(amount);
        this.letter = letter;
    }
}
