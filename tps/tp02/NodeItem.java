public class NodeItem {
    int size = 8;
    int[] index = new int[size - 1];
    long[] pointer = new long[size - 1];
    NodeItem next;

    NodeItem() {

    }

    NodeItem(int id, long pointer) {
        boolean confirmation = false;
        for (int i = 0; i < size - 1; i++) {
            if (index[i] == 0) {
                index[i] = id;
                this.pointer[i] = pointer;
                confirmation = true;
                i = size; // break
            }
        }
        if (!(confirmation)) {
            throw new RuntimeException("NodeItem is full!");
        }
    }

    public boolean isFull() {
        for (int i = 0; i < size - 1; i++) {
            if (index[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public NodeIndex insert(int id, long pointer, NodeIndex father) {
        NodeIndex nodeIndex = father;
        if (isFull()) {
            double up = Math.floor((double) size / 2);

        }
        boolean confirmation = false;
        for (int i = 0; i < size - 1; i++) {
            if (index[i] == 0) {
                index[i] = id;
                this.pointer[i] = pointer;
                confirmation = true;
                i = size; // break
            }
        }
        bubbleSort(index, size - 1, null);
        if (!(confirmation)) {
            throw new RuntimeException("NodeItem is full!");
        }
    }

    public static void bubbleSort(int[] array, int count, long[] pointer) {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // swap id
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    // swap pointer
                    long tempP = pointer[j];
                    pointer[j] = pointer[j + 1];
                    pointer[j + 1] = tempP;
                }
            }
        }
    }

}
