package BPlusTree;

import java.io.Serializable;
import java.util.ArrayList;

import DataStruct.DataStruct;

public class BPlusTree implements DataStruct, Serializable {

    private static ArrayList<Long> free = new ArrayList<Long>();
    private long lastId;

    private Node root;
    private int order;
    private int size = 0;
    private int count = 0;

    public BPlusTree(int order) {
        this.root = null;
        this.order = order;
    }

    @Override
    public void insert(long key, long value) {
        if (root == null) {
            root = new LeafNode();
        }
        root.insert(key, value);
        if (root.isOverflow()) {
            InternalNode newRoot = new InternalNode();
            newRoot.children[0] = root;
            newRoot.splitChild(0);
            root = newRoot;
        }
        size++;
        count++;
        lastId = key;
    }

    public void readAll() {
        if (root == null)
            throw new RuntimeException("empty tree");
        root.print();
    }

    @Override
    public long delete(long key) {
        if (root == null || root.size == 0) {
            return -1;
        }
        count--;
        return root.delete(key);
    }

    @Override
    public boolean update(long key, long newValue) {
        if (root == null || root.size == 0) {
            return false;
        }
        return root.update(key, newValue);
    }

    @Override
    public long search(long key) {
        if (root == null) {
            return -1;
        }
        return root.search(key);
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }

    @Override
    public ArrayList<Long> getList() {
        return free;
    }

    @Override
    public long getLastId() {
        return lastId;
    }

    private abstract class Node implements Serializable {

        protected long[] keys;
        protected int size;

        public Node() {
            this.keys = new long[order];
            this.size = 0;
        }

        public abstract void insert(long key, long value);

        public abstract long search(long key);

        public abstract long delete(long key);

        public abstract boolean update(long key, long value);

        public abstract void print();

        public boolean isOverflow() {
            return size == order;
        }
    }

    private class InternalNode extends Node {

        protected Node[] children;

        public InternalNode() {
            super();
            this.children = new Node[order + 1];
        }

        public void insert(long key, long value) {
            int index = findIndex(key);
            children[index].insert(key, value);
            if (children[index].isOverflow()) {
                splitChild(index);
            }
        }

        public void insert(long key, int index, Node child) {
            for (int i = size; i > index; i--) {
                keys[i] = keys[i - 1];
                children[i + 1] = children[i];
            }
            keys[index] = key;
            children[index + 1] = child;
            size++;
        }

        public long search(long key) {
            int index = findIndex(key);
            return children[index].search(key);
        }

        public long delete(long key) {
            long pointer = 0;
            int index = findIndex(key);
            pointer = children[index].delete(key);
            return pointer;
        }

        public boolean update(long key, long value) {
            boolean success = false;
            int index = findIndex(key);
            success = children[index].update(key, value);
            return success;
        }

        public void print() {
            children[0].print();
        }

        public void splitChild(int index) {
            if (children[index] instanceof LeafNode) {
                splitChild((LeafNode) children[index], index);
            } else {
                splitChild((InternalNode) children[index], index);
            }
        }

        public void splitChild(LeafNode child, int index) {
            int newSize = (int) Math.ceil((double) child.size / 2);
            LeafNode newChild = new LeafNode();
            int oldChildSize = child.size;
            for (int i = newSize; i < oldChildSize; i++) {
                newChild.keys[newChild.size] = child.keys[i];
                newChild.values[newChild.size++] = child.values[i];
                child.keys[i] = 0;
                child.values[i] = 0;
                child.size--;
            }
            child.setNext(newChild);
            insert(newChild.keys[0], index, newChild);
        }

        public void splitChild(InternalNode child, int index) {
            int newSize = ((int) Math.floor((double) child.size / 2));

            InternalNode newChild = new InternalNode();
            int oldChildSize = child.size;
            long tmp = child.keys[newSize];
            child.keys[newSize] = 0;
            child.size--;
            for (int i = newSize + 1; i < oldChildSize; i++) {
                newChild.keys[newChild.size] = child.keys[i];
                newChild.children[newChild.size++] = child.children[i];
                child.keys[i] = 0;
                child.children[i] = null;
                child.size--;
            }
            newChild.children[newChild.size] = child.children[order];
            child.children[order] = null;
            insert(tmp, index, newChild);
        }

        protected int findIndex(long key) {
            int index = 0;
            while (index < size && keys[index] <= key) {
                index++;
            }
            return index;
        }

    }

    private class LeafNode extends Node {

        private long[] values;
        private LeafNode next;

        public LeafNode() {
            super();
            this.keys = new long[order];
            this.values = new long[order];
            this.next = null;
        }

        public void setNext(LeafNode next) {
            this.next = next;
        }

        public LeafNode getNext() {
            return next;
        }

        public void insert(long key, long value) {
            int index = findIndex(key);
            for (int i = size; i > index; i--) {
                keys[i] = keys[i - 1];
                values[i] = values[i - 1];
            }
            keys[index] = key;
            values[index] = value;
            size++;
        }

        public long search(long key) {
            int index = findIndex(key);
            if (index < size && keys[index] == key) {
                if (values[index] != 0)
                    return values[index];
                else
                    return -1;
            } else {
                return -1;
            }
        }

        public long delete(long key) {
            int index = findIndex(key);
            if (index < size && keys[index] == key && values[index] != 0) {
                long value = values[index];
                values[index] = 0;
                free.add(value);
                return value;
            } else {
                return -1;
            }
        }

        public boolean update(long key, long value) {
            int index = findIndex(key);
            if (index < size && keys[index] == key) {
                values[index] = value;
                return true;
            } else {
                return false;
            }
        }

        public void print() {
            for (int i = 0; i < size; i++) {
                System.out.println("keys[" + i + "]: " + keys[i] + " values[" + i + "]: " + values[i]);
            }
            if (next != null) {
                getNext().print();
            }
        }

        public boolean isOverflow() {
            return size == order;
        }

        protected int findIndex(long key) {
            int index = 0;
            while (index < size && keys[index] < key) {
                index++;
            }
            return index;
        }

    }
}