package Huffman;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Huffman {
    private static PriorityQueue<HuffmanTree> queue = new PriorityQueue<>(new DescendingComparator());

    private static HashMap<Integer, Integer> counter = new HashMap<>();

    private Huffman() {
    }

    public static void compress(String filePath) {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath,
                    "rw");
            int byteData;
            while ((byteData = file.read()) != -1) {
                // Process each byte
                System.out.println(byteData);
            }
            addToDict(byteData);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int key : counter.keySet()) {
            Integer value = counter.get(key);
            HuffmanTree tree = new HuffmanTree(key, value);
        }
    }

    private static class DescendingComparator implements Comparator<HuffmanTree> {
        @Override
        public int compare(HuffmanTree a, HuffmanTree b) {
            // Compare in ascending order
            if (a.getRoot().amount > b.getRoot().amount) {
                return 1;
            } else if (a.getRoot().amount < b.getRoot().amount) {
                return -1;
            } else {
                int depthA = a.getDepht();
                int depthB = b.getDepht();
                if (depthA > depthB) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }

    private static void addToDict(int key) {
        counter.merge(key, 1, Integer::sum);
    }
}
