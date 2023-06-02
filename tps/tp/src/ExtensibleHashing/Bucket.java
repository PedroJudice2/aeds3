package ExtensibleHashing;

import java.io.Serializable;

/**
 * The `Bucket` class represents a bucket in an extensible hashing table. It
 * contains an array of `Node` objects,
 * which store key-value pairs. The bucket has a maximum size of 388 nodes, and
 * can be checked for fullness using
 * the `isFull()` method. Nodes can be inserted into the bucket using the
 * `insert()` method, and removed using the
 * `remove()` method. The `search()` method can be used to find the value
 * associated with a given key in the bucket.
 * The bucket also has a depth, which is used to determine which bits of the key
 * to use when hashing. The depth can
 * be set and retrieved using the `setDepth()` and `getDepth()` methods,
 * respectively.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
class Bucket implements Serializable {
    public Node[] node;
    private int depth;
    private int Maxsize = 388;
    private int size = 0;
    private static final long serialVersionUID = 1l;

    /**
     * Constructs an empty bucket with the given depth.
     * 
     * @param depth
     */
    Bucket(int depth) {
        node = new Node[Maxsize];
        this.depth = depth;
    }

    /**
     * inserts a new key-value pair into the bucket by creating
     * a new Node object and inserting it into the node array.
     * 
     * @param key
     * @param value
     */
    public void insert(long key, long value) {
        node[size++] = new Node(key, value);
        sort();
    }

    /**
     * checks if the bucket is full
     * 
     * @return
     */
    public boolean isFull() {
        return size == Maxsize;
    }

    /**
     * sorts the node array in ascending order by key
     */
    private void sort() {
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

    /**
     * searches for a key in the bucket and returns the associated value
     * 
     * @param key
     * @return
     */
    public long search(long key) {

        if (size > 0)
            return binarySearch(key, 0, size - 1);
        else
            return -1;
    }

    /**
     * searches for a key in the bucket using binary search
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    private long binarySearch(long key, int start, int end) {
        int mid = (start + end) / 2;

        long answer;
        if (start > end) {
            answer = -1;
        } else if (node[mid].getKey() == key) {
            answer = node[mid].getValue();
        } else if (node[mid].getKey() > key) {
            answer = binarySearch(key, start, mid - 1);
        } else {
            answer = binarySearch(key, mid + 1, end);
        }
        return answer;
    }

    /**
     * removes a key-value pair from the bucket and rearranges the bucket
     * 
     * @param key
     * @return
     */
    public long remove(long key) {
        int index = findIndex(key);
        if (index != -1) {
            long value = node[index].getValue();
            for (int i = index; i < size - 1; i++) {
                node[i] = node[i + 1];
            }
            node[--size] = null;
            return value;
        }
        return -1;
    }

    /**
     * returns the depth of the bucket
     * 
     * @return
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * sets the depth of the bucket
     * 
     * @param depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * returns the size of the bucket
     * 
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * returns the index of a given key in the node array
     * 
     * @param key
     * @return
     */
    private int findIndex(long key) {
        if (size > 0)
            return indexBinarySearch(key, 0, size - 1);
        else
            return -1;
    }

    /**
     * searches for a key in the bucket using binary search and returns the index of
     * the key in the node array
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    private int indexBinarySearch(long key, int start, int end) {
        int mid = (start + end) / 2;

        int answer;
        if (start > end) {
            answer = -1;
        } else if (node[mid].getKey() == key) {
            answer = mid;
        } else if (node[mid].getKey() > key) {
            answer = indexBinarySearch(key, start, mid - 1);
        } else {
            answer = indexBinarySearch(key, mid + 1, end);
        }
        return answer;
    }

    /**
     * updates the value associated with a given key in the bucket
     * 
     * @param key
     * @param newValue
     * @return
     */
    public boolean update(long key, long newValue) {
        if (size > 0)
            return updateBinarySearch(key, newValue, 0, size - 1);
        else
            return false;
    }

    /**
     * searches for a key in the bucket using binary search and updates the value
     * 
     * @param key
     * @param newValue
     * @param start
     * @param end
     * @return
     */
    private boolean updateBinarySearch(long key, long newValue, int start, int end) {
        int mid = (start + end) / 2;

        boolean answer;
        if (start > end) {
            answer = false;
        } else if (node[mid].getKey() == key) {
            answer = true;
            node[mid].setValue(newValue);
        } else if (node[mid].getKey() > key) {
            answer = updateBinarySearch(key, newValue, start, mid - 1);
        } else {
            answer = updateBinarySearch(key, newValue, mid + 1, end);
        }
        return answer;
    }

}

/**
 * Node class that stores a key-value pair
 * 
 * @version 1.0
 * @since 2023-05-13
 * @see Bucket
 */
class Node implements Serializable {
    private long key;
    private long value;

    /**
     * constructor that takes in a key and a value
     * 
     * @param key
     * @param value
     */
    public Node(long key, long value) {
        this.key = key;
        this.value = value;
    }

    /**
     * returns the key
     * 
     * @return
     */
    public long getKey() {
        return this.key;
    }

    /**
     * sets the key
     * 
     * @param key
     */
    public void setKey(long key) {
        this.key = key;
    }

    /**
     * returns the value
     * 
     * @return
     */
    public long getValue() {
        return this.value;
    }

    /**
     * sets the value
     * 
     * @param value
     */
    public void setValue(long value) {
        this.value = value;
    }

}
