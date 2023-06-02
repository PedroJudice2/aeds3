public class Bucket {
    public Node[] node;
    private int depth;
    private int Maxsize = 388;
    private int size = 0;

    Bucket(int depth) {
        node = new Node[Maxsize];
        this.depth = depth;
    }

    public void insert(int key, long value) {
        node[size++] = new Node(key, value);
        sort();
    }

    public boolean isFull() {
        return size == Maxsize;
    }

    void sort() {
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

    public long search(int key) {

        if (size > 0)
            return binarySearch(key, 0, size - 1);
        else
            return -1;
    }

    private long binarySearch(int key, int start, int end) {
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

    public long remove(int key) {
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

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getSize() {
        return size;
    }

    private int findIndex(int key) {
        if (size > 0)
            return indexBinarySearch(key, 0, size - 1);
        else
            return -1;
    }

    private int indexBinarySearch(int key, int start, int end) {
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

    public boolean update(int key, long newValue) {
        if (size > 0)
            return updateBinarySearch(key, newValue, 0, size - 1);
        else
            return false;
    }

    private boolean updateBinarySearch(int key, long newValue, int start, int end) {
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

class Node {
    private int key;
    private long value;

    public Node(int key, long value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public long getValue() {
        return this.value;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
