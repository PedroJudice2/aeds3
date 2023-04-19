public class HashTable {
    public int depth = 1;
    public Bucket[] array;
    private int arraySize;
    private int lastId;
    private int count;

    HashTable() {
        int size = (int) Math.pow(2, depth);
        array = new Bucket[size];
        for (int i = 0; i < size; i++) {
            array[i] = new Bucket(depth);
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
        lastId = key;
        count++;
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

    public long search(int key) {

        int hash = hash(key);
        long result = array[hash].search(key);

        return result;
    }

    public boolean update(int key, long newValue) {
        int hash = hash(key);
        boolean result = array[hash].update(key, newValue);

        return result;
    }

    public long delete(int key) {
        int hash = hash(key);
        long value = array[hash].remove(key);
        if (value != -1) {
            count--;
        }
        return value;
    }

    public int getLastId() {
        return lastId;
    }

    public int getCount() {
        return count;
    }
}