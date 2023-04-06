public class BPlusTree {

    private Node root;
    private int order;

    public BPlusTree(int order) {
        this.root = null;
        this.order = order;
    }

    public void insert(int key, long value) {
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
    }

    public long search(int key) {
        if (root == null) {
            return -1;
        }
        return root.search(key);
    }

    private abstract class Node {

        protected int[] keys;
        protected int size;

        public Node() {
            this.keys = new int[order];
            this.size = 0;
        }

        public abstract void insert(int key, long value);

        public abstract long search(int key);

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

        public void insert(int key, long value) {
            int index = findIndex(key);
            children[index].insert(key, value);
            if (children[index].isOverflow()) {
                splitChild(index);
            }
        }

        public long search(int key) {
            int index = findIndex(key);
            return children[index].search(key);
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
            child.next = newChild;
            children[index + 1] = newChild;
            keys[index] = newChild.keys[0];
            size++;
        }

        public void splitChild(InternalNode child, int index) {
            int newSize = ((int) Math.floor((double) child.size / 2));

            InternalNode newChild = new InternalNode();
            int oldChildSize = child.size;
            int tmp = child.keys[newSize];
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
            children[index + 1] = newChild;
            keys[index] = tmp;
            size++;
        }

        protected int findIndex(int key) {
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
            this.keys = new int[order];
            this.values = new long[order];
            this.next = null;

        }

        public void insert(int key, long value) {
            int index = findIndex(key);
            for (int i = size; i > index; i--) {
                keys[i] = keys[i - 1];
                values[i] = values[i - 1];
            }
            keys[index] = key;
            values[index] = value;
            size++;
        }

        public long search(int key) {
            int index = findIndex(key);
            if (index < size && keys[index] == key) {
                return values[index];
            } else {
                return -1;
            }
        }

        public boolean isOverflow() {
            return size == order;
        }

        public void setNext(LeafNode next) {
            this.next = next;
        }

        public LeafNode getNext() {
            return next;
        }

        protected int findIndex(int key) {
            int index = 0;
            while (index < size && keys[index] < key) {
                index++;
            }
            return index;
        }
    }
}