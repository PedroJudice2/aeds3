package Huffman;

import java.io.Serializable;
import java.util.HashMap;

public class HuffmanTree implements Serializable {
    private Node root;
    private HashMap<Integer, Byte[]> dictionary = new HashMap<>();
    private int numberOfBytes = -1;

    public Node getRoot() {
        return root;
    }

    HuffmanTree(int letter, int amount) {
        root = new LeafNode(letter, amount);
    }

    public void insertTree(HuffmanTree tree) {
        int amount = getRoot().amount + tree.getRoot().amount;
        root = new InternalNode(amount, root, tree.getRoot());
    }

    public HashMap<Integer, Byte[]> buildDict() {
        int treeDepht = getDepht();
        if (treeDepht != 0) {
            numberOfBytes = (int) Math.ceil(treeDepht / 8);
            StringBuilder compresByte = new StringBuilder();
            buildDict(root, compresByte);
            return dictionary;
        } else {
            throw new IllegalStateException("Tree is empty. Cannot Create dictonary.");
        }
    }

    private void buildDict(Node node, StringBuilder compressByte) {
        if (node instanceof LeafNode) {
            String binaryString = compressByte.toString();
            Byte convertedByte[] = createByteArray(binaryString, numberOfBytes);
            dictionary.put(((LeafNode) node).letter, convertedByte);
        } else {
            buildDict(((InternalNode) node).left, compressByte.append("1"));
            buildDict(((InternalNode) node).right, compressByte.append("0"));
        }
    }

    public int getDepht() {
        return getDepht(root);
    }

    private int getDepht(Node node) {
        if (root == null || node instanceof LeafNode) {
            return 0;
        } else {
            int leftDepth = getDepht(((InternalNode) node).left);
            int rightDepth = getDepht(((InternalNode) node).right);
            return Math.max(leftDepth, rightDepth) + 1;
        }
    }

    public static Byte[] createByteArray(String binaryString, int arrayLength) {
        Byte[] byteArray = new Byte[arrayLength];
        int intValue = Integer.parseInt(binaryString, 2);

        for (int i = 0; i < arrayLength; i++) {
            int bitPosition = arrayLength - i - 1; // Calculate the bit position from right to left
            int bitValue = (intValue >> bitPosition) & 1; // Get the bit value at the corresponding position
            byteArray[i] = (byte) (bitValue << (7 - i % 8)); // Set the bit in the byte array
        }

        return byteArray;
    }

    public int getNumberOfBytes() {
        if (numberOfBytes == -1) {
            throw new IllegalStateException("Must create the dictionary to get the number of bytes");
        } else {
            return numberOfBytes;
        }
    }
}
