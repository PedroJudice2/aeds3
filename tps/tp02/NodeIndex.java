public class NodeIndex {
    int size = 8;
    int[] index = new int[size - 1];
    NodeItem[] pointer = new NodeItem[size];

    NodeIndex() {
        pointer[0] = new NodeItem();
    }

    public boolean isFull() {
        for (int i = 0; i < size - 1; i++) {
            if (index[i] == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (int i = 0; i < size - 1; i++) {
            if (index[i] != 0) {
                return false;
            }
        }
        return true;
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
