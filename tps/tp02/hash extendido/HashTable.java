import java.util.ArrayList;

public class HashTable {
    public int depth = 1;
    public Bucket[] array;
    private int arraySize;

    HashTable() {
        int size = (int) Math.pow(2, depth);
        array = new Bucket[size];
        for (int i = 0; i < size; i++) {
            array[0] = new Bucket(depth);
        }
        arraySize = size;
    }

    public int hash(int key) {
        return (int) (key % Math.pow(2, depth));
    }

    public void insert(int key, long value) {
        int hash = hash(key);
        if (array[hash].isFull()) {
            split(array[hash], hash);
        }
        hash = hash(key);
        array[hash].insert(key, value);
    }

    private void split(Bucket bucket, int hash) {
        int bucketDepth = bucket.getDepth();
        bucket.setDepth(++bucketDepth);
        if (bucketDepth > depth) {
            depth++;
            Bucket[] newArray = new Bucket[(int) Math.pow(2, depth)];
            arraySize = array.length;
            for (int i = 0; i < newArray.length; i++) {
                newArray[i] = array[i % arraySize];
            }
            array = newArray;
            array[hash + arraySize] = new Bucket(bucketDepth);
            for (int i = 0; i < array[hash].getSize(); i++) {
                int newHash = hash(bucket.node[i].getKey());
                if (newHash != hash) {
                    insert(bucket.node[i].getKey(), bucket.node[i].getValue());
                    bucket.remove(array[hash].node[i].getKey());
                }
            }
        } else {
            array[hash + arraySize] = new Bucket(bucketDepth);
            for (int i = 0; i < array[hash].getSize(); i++) {
                int newHash = hash(bucket.node[i].getKey());
                if (newHash != hash) {
                    insert(bucket.node[i].getKey(), bucket.node[i].getValue());
                    bucket.remove(array[hash].node[i].getKey());
                }
            }
        }

    }
}