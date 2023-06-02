package BPlusTree;

import java.io.Serializable;
import DataStruct.DataStruct;

/**
 * The BPlusTree class represents a B+ tree data structure that stores key-value
 * pairs.
 * It extends the DataStruct class and implements the Serializable interface.
 * 
 * The BPlusTree class has a root node, an order, a size, and a count.
 * The root node is the top node of the tree, and it can be either an internal
 * node or a leaf node.
 * The order is the maximum number of childs that can be stored in a node.
 * The size is the number of key-value pairs stored in the tree.
 * 
 * 
 * The BPlusTree class has methods for inserting, deleting, updating, and
 * searching key-value pairs.
 * It also has a method for printing all the key-value pairs in the tree.
 * 
 * The BPlusTree class has two inner classes: Node and InternalNode.
 * The Node class is an abstract class that represents a node in the tree.
 * It has methods for inserting, searching, deleting, updating, and printing
 * key-value pairs.
 * The InternalNode class extends the Node class and represents an internal node
 * in the tree.
 * It has an array of children nodes and an array of keys that represent the
 * boundaries between the children nodes.
 * The LeafNode class extends the Node class and represents a leaf node in the
 * tree and has an array of key-value pairs and a pointer to the next leaf node.
 * 
 * @version 1.0
 * @since 2023-02-24
 * @author Pedro
 */
public class BPlusTree extends DataStruct implements Serializable {

    private Node root;
    private int order;
    private int size = 0;
    private static final long serialVersionUID = 1l;

    /**
     * Constructs an empty BPlusTree with the specified order.
     * 
     * @param order
     */
    public BPlusTree(int order) {
        this.root = null;
        this.order = order;
    }

    /**
     * {@inheritDoc}
     * insert a key-value pair into the tree.
     * when
     */
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
        setLastId(key);
    }

    /**
     * print all the key-value pairs in the tree.
     */
    public void print() {
        if (root == null)
            throw new RuntimeException("empty tree");
        root.print();
    }

    /**
     * {@inheritDoc}
     * delete a key-value pair from the tree.
     */
    @Override
    public long delete(long key) {
        if (root == null || root.size == 0) {
            return -1;
        }
        return root.delete(key);
    }

    /**
     * {@inheritDoc}
     * update a key-value pair in the tree
     */
    @Override
    public boolean update(long key, long newValue) {
        if (root == null || root.size == 0) {
            return false;
        }
        return root.update(key, newValue);
    }

    /**
     * {@inheritDoc}
     * search for a key in the tree.
     */
    @Override
    public long search(long key) {
        if (root == null) {
            return -1;
        }
        return root.search(key);
    }

    /**
     * the number of key-value pairs stored in the tree
     * 
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * The Node class represents a node in the tree.
     * 
     * @version 1.0
     * @since 2023-05-13
     * @see HashTable
     */
    private abstract class Node implements Serializable {

        protected long[] keys;
        protected int size;

        /**
         * Constructs a node with the specified order.
         */
        public Node() {
            this.keys = new long[order];
            this.size = 0;
        }

        /**
         * insert a key-value pair into the node.
         * 
         * @param key
         * @param value
         */
        public abstract void insert(long key, long value);

        /**
         * search for a key in the node.
         * 
         * @param key
         * @return
         */
        public abstract long search(long key);

        /**
         * delete a key-value pair from the node.
         * 
         * @param key
         * @return
         */
        public abstract long delete(long key);

        /**
         * update a key-value pair in the node.
         * 
         * @param key
         * @param value
         * @return
         */
        public abstract boolean update(long key, long value);

        /**
         * print all the key-value pairs in the node.
         */
        public abstract void print();

        /**
         * check if the node is overflow
         * 
         * @return
         */
        public boolean isOverflow() {
            return size == order;
        }
    }

    /**
     * The InternalNode class represents an internal node in the tree.
     * 
     * @version 1.0
     * @since 2023-05-13
     * @see Node
     */
    private class InternalNode extends Node {

        protected Node[] children;

        /**
         * Constructs an internal node with the specified order + 1, for storing the
         * overflow element.
         */
        public InternalNode() {
            super();
            this.children = new Node[order + 1];
        }

        /**
         * {@inheritDoc}
         * insert a key-value pair into the tree by recursively calling the insert
         * method util reaching a leaf node.
         * when the leaf node is overflow, split the leaf node.
         */
        public void insert(long key, long value) {
            int index = findIndex(key);
            children[index].insert(key, value);
            if (children[index].isOverflow()) {
                splitChild(index);
            }
        }

        /**
         * insert a key-value pair into the internal node.
         * 
         * @param key
         * @param index
         * @param child
         */
        public void insert(long key, int index, Node child) {
            for (int i = size; i > index; i--) {
                keys[i] = keys[i - 1];
                children[i + 1] = children[i];
            }
            keys[index] = key;
            children[index + 1] = child;
            size++;
        }

        /**
         * {@inheritDoc}
         * search for a key in the tree by recursively calling the search method util
         * reaching a leaf node.
         */
        public long search(long key) {
            int index = findIndex(key);
            return children[index].search(key);
        }

        /**
         * {@inheritDoc}
         * delete a key-value pair from the tree by recursively calling the delete
         * method util reaching a leaf node.
         */
        public long delete(long key) {
            long pointer = 0;
            int index = findIndex(key);
            pointer = children[index].delete(key);
            return pointer;
        }

        /**
         * {@inheritDoc}
         * update a key-value pair in the tree by recursively calling the update method
         * util reaching a leaf node.
         */
        public boolean update(long key, long value) {
            boolean success = false;
            int index = findIndex(key);
            success = children[index].update(key, value);
            return success;
        }

        /**
         * {@inheritDoc}
         * print all the key-value pairs in the tree by recursively calling the print
         * method util reaching a leaf node.
         */
        public void print() {
            children[0].print();
        }

        /**
         * split the child node at the specified index and checking if node is internal
         * or leaf node.
         * 
         * @param index
         */
        public void splitChild(int index) {
            if (children[index] instanceof LeafNode) {
                splitChild((LeafNode) children[index], index);
            } else {
                splitChild((InternalNode) children[index], index);
            }
        }

        /**
         * split the leaf node at the specified index.
         * 
         * @param child
         * @param index
         */
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

        /**
         * split the internal node at the specified index.
         * 
         * @param child
         * @param index
         */
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

        /**
         * find the index of the key in the internal node.
         * 
         * @param key
         * @return
         */
        protected int findIndex(long key) {
            int index = 0;
            while (index < size && keys[index] <= key) {
                index++;
            }
            return index;
        }

    }

    /**
     * The LeafNode class represents a leaf node in the tree.
     * 
     * @version 1.0
     * @since 2023-05-13
     * @see Node
     */
    private class LeafNode extends Node {

        private long[] values;
        private LeafNode next;

        /**
         * Constructs a leaf node with the specified order, for storing the overflow
         * element.
         */
        public LeafNode() {
            super();
            this.keys = new long[order];
            this.values = new long[order];
            this.next = null;
        }

        /**
         * set the next pointer to the specified leaf node.
         * 
         * @param next
         */
        public void setNext(LeafNode next) {
            this.next = next;
        }

        /**
         * get the next pointer.
         * 
         * @return
         */
        public LeafNode getNext() {
            return next;
        }

        /**
         * {@inheritDoc}
         * insert a key-value pair into the leaf node.
         */
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

        /**
         * {@inheritDoc}
         * search for a key in the leaf node.
         */
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

        /**
         * {@inheritDoc}
         * delete a key-value pair from the leaf node.
         */
        public long delete(long key) {
            int index = findIndex(key);
            if (index < size && keys[index] == key && values[index] != 0) {
                long value = values[index];
                values[index] = 0;
                addToList(value);
                return value;
            } else {
                return -1;
            }
        }

        /**
         * {@inheritDoc}
         * update a key-value pair in the leaf node.
         */
        public boolean update(long key, long value) {
            int index = findIndex(key);
            if (index < size && keys[index] == key) {
                values[index] = value;
                return true;
            } else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         * print all the key-value pairs from all the leaf nodes.
         */
        public void print() {
            for (int i = 0; i < size; i++) {
                System.out.println("keys[" + i + "]: " + keys[i] + " values[" + i + "]: " + values[i]);
            }
            if (next != null) {
                getNext().print();
            }
        }

        /**
         * find the index of the key in the leaf node.
         * 
         * @param key
         * @return
         */
        protected int findIndex(long key) {
            int index = 0;
            while (index < size && keys[index] < key) {
                index++;
            }
            return index;
        }

    }
}