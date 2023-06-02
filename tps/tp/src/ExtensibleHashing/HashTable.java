package ExtensibleHashing;

import DataStruct.DataStruct;

/**
 * The HashTable class represents a hash table data structure that uses the
 * extensible hashing algorithm.
 * It extends the DataStruct class and implements the Serializable interface.
 * 
 * The hash table consists of an array of Buckets, where each Bucket is a linked
 * list of Node objects.
 * The hash function used is the modulo operation with the size of the array,
 * which is a power of 2.
 * 
 * The hash table supports the following operations:
 * - insert: inserts a new key-value pair into the hash table
 * - search: searches for a key in the hash table and returns its corresponding
 * value
 * - update: updates the value of a key in the hash table
 * - delete: removes a key-value pair from the hash table
 * 
 * The hash table also supports dynamic resizing, where the size of the array is
 * doubled when a Bucket becomes full.
 * This is done by splitting the Bucket into two Buckets and redistributing its
 * Nodes into the new Buckets.
 * 
 * The depth of the hash table determines the number of bits used in the hash
 * function, and it is increased when a Bucket is split.
 * 
 * This class is serializable, which means that its objects can be converted
 * into a byte stream and stored in a file or sent over a network.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
public class HashTable extends DataStruct {

    public int depth = 1;
    public Bucket[] array;
    private int arraySize;
    private static final long serialVersionUID = 1l;

    /**
     * Constructs an empty hash table with an initial size of 2.
     */
    public HashTable() {
        int size = (int) Math.pow(2, depth);
        array = new Bucket[size];
        for (int i = 0; i < size; i++) {
            array[i] = new Bucket(depth);
        }
        arraySize = size;
    }

    /**
     * calculates the hash value of a key
     * 
     * @param key
     * @return
     */
    public int hash(long key) {
        return (int) (key % Math.pow(2, depth));
    }

    /**
     * {@inheritDoc}
     * inserts a new key-value pair into the hash table
     */
    @Override
    public void insert(long key, long value) {
        int hash = hash(key);
        if (array[hash].isFull()) {
            split(array[hash], hash);
        }
        hash = hash(key);
        array[hash].insert(key, value);
        setLastId(key);
    }

    /**
     * splits a Bucket into two Buckets and redistributes its Nodes into the new
     * Buckets array according to the new hash value
     * 
     * @param bucket
     * @param hash
     */
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
            if (hash < arraySize)
                array[hash + arraySize] = new Bucket(bucketDepth);
            else
                array[hash] = new Bucket(bucketDepth);
            for (int i = 0; i < array[hash].getSize(); i++) {
                int newHash = hash(bucket.node[i].getKey());
                if (newHash != hash) {
                    insert(bucket.node[i].getKey(), bucket.node[i].getValue());
                    bucket.remove(array[hash].node[i].getKey());
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     * searches for a key in the hash table and returns its corresponding value
     */
    @Override
    public long search(long key) {

        int hash = hash(key);
        long result = array[hash].search(key);

        return result;
    }

    /**
     * {@inheritDoc}
     * updates the value of a key in the hash table
     */
    @Override
    public boolean update(long key, long newValue) {
        int hash = hash(key);
        boolean result = array[hash].update(key, newValue);

        return result;
    }

    /**
     * {@inheritDoc}
     * removes a key-value pair from the hash table
     */
    @Override
    public long delete(long key) {
        int hash = hash(key);
        long value = array[hash].remove(key);
        if (value != -1) {
            addToList(value);
        }
        return value;
    }

}