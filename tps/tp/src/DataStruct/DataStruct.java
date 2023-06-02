package DataStruct;

import java.util.ArrayList;

/**
 * This class is the abstract class for all data structures.
 * 
 * @version 1.0
 * @since 2014-03-31
 * @author Pedro
 */
public abstract class DataStruct {

    private long lastId;

    private ArrayList<Long> free = new ArrayList<Long>();

    /**
     * insert a key-value pair into the dataStruct
     * 
     * @param key
     * @param value
     */
    public abstract void insert(long key, long value);

    /**
     * delete a key-value pair from the dataStruct.
     * 
     * @param key
     * @return
     */
    public abstract long search(long key);

    /**
     * update a key-value pair in the tree
     * 
     * @param key
     * @param newValue
     * @return
     */
    public abstract boolean update(long key, long newValue);

    /**
     * search for a key in the tree.
     * 
     * @param key
     * @return
     */
    public abstract long delete(long key);

    /**
     * get the list of free locations
     * 
     * @return
     */
    public ArrayList<Long> getList() {
        return free;
    }

    /**
     * get the last id
     * 
     * @return
     */
    public long getLastId() {
        return lastId;
    }

    /**
     * set the last id
     * 
     * @param lastId
     */
    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    /**
     * get the size of the list
     * 
     * @return
     */
    public int getListSize() {
        return free.size();
    }

    /**
     * add a value to the list
     * 
     * @param value
     */
    public void addToList(long value) {
        free.add(value);
    }

}
