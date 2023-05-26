package DataStruct;

import java.util.ArrayList;

public interface DataStruct {
    public void insert(long key, long value);

    public long search(long key);

    public boolean update(long key, long newValue);

    public long delete(long key);

    public long getLastId();

    public ArrayList<Long> getList();

}
