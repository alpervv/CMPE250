import java.util.ArrayList;

/**
 * Min heap implementation of regular binary heap.
 * @param <T> A comparable java class
 */
public class BinaryHeap<T extends Comparable<T>> {
    private T[] heapArray;
    private int size; // The number of elements in the binary array, excluding the space at heapArray[0]
    private static final int DEFAULT_SIZE = 10;

    // Default constructor
    BinaryHeap() {
        this.heapArray = (T[]) new Object[DEFAULT_SIZE];
    }

    /**
     * Create a binary heap with a certain capacity. The capacity is automatically doubled if an element is inserted when fulll.
     * @param size Initial capacity of the heap
     */
    BinaryHeap(int size) {
        this.heapArray = (T[]) new Comparable[size];
    }

    /**
     * Creates a binary heap from the given array
     * @param array
     */
    BinaryHeap(T[] array) {
        // Copy the contents of the array to the heapArray with leaving the position 0 empty.
        this.heapArray = (T[]) new Comparable[array.length+1];
        for (int i = 0; i < array.length; i++) {
            this.heapArray[i+1] = array[i];
        }
        this.size = array.length;

        // Percolate down necessary number of elements.
        for (int i = size/2; i > 0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Doubles the capacity of heapArray
     */
    private void increaseSize() {
        T[] temp = (T[]) java.lang.reflect.Array.newInstance(heapArray.getClass().getComponentType(), heapArray.length * 2);
        System.arraycopy(heapArray, 0, temp, 0, heapArray.length);
        heapArray = temp;
    }


    /**
     * Inserts the element in the heap and preserves the min heap property.
     * @param element
     */
    public void insert(T element) {
        if (heapArray.length == size+1) {
            increaseSize();
        }
        // Insertion logic to a binary heap
        size++;
        heapArray[size] = element;
        percolateUp(size);
    }

    /**
     * @return minimum element in the heap
     */
    public T min(){
        return heapArray[1];
    }

    /**
     * Deletes the minimum element from the heap and restores the heap order property.
     * @return the minimum element that was deleted
     */
    public T deleteMin(){
        if (size == 0) { // Return null if the heap is empty
            return null;
        }
        T temp = heapArray[1];
        heapArray[1] = heapArray[size];
        size--;
        percolateDown(1); // Restore the min heap property
        return temp;
    }

    /**
     * Percolates up the i-th position in the heapArray to restore the heap order property
     * @param hole the index at which the percolation begins.
     */
    private void percolateUp(int hole) {
        T x = heapArray[hole];
        for( heapArray[ 0 ] = x; x.compareTo( heapArray[ hole / 2 ] ) < 0; hole /= 2 )
            heapArray[ hole ] = heapArray[ hole / 2 ];
        heapArray[ hole ] = x;
    }

    /**
     18 * Percolates down the i-th position in the heapArray to restore the heap order property
     19 * @param hole the index at which the percolation begins.
     20 */
    private void percolateDown( int hole ) {
        int child;
        T tmp = heapArray[ hole ];
        for(; hole * 2 <= size; hole = child ) {
            child = hole * 2;
            if( child != size && heapArray[ child + 1 ].compareTo( heapArray[ child ] ) < 0 )
                child++;
            if( heapArray[ child ].compareTo( tmp ) < 0 )
                heapArray[ hole ] = heapArray[ child ];
            else
                break;
        }
        heapArray[ hole ] = tmp;
    }

    /**
     * Creates a sorted array from the given array and returns it.
     */
    public static<T extends Comparable<T>> T[] heapSort(T[] arr){
        BinaryHeap<T> toSort = new BinaryHeap(arr);
        T[] sortedArray = (T[]) java.lang.reflect.Array.newInstance(arr.getClass().getComponentType(), arr.length);
        for (int i = 0; i < arr.length; i++) {
            sortedArray[i] = toSort.deleteMin();
        }
        return sortedArray;
    }

    /**
     * Creates a sorted ArrayList from the given ArrayList and returns it
     */
    public static <T extends Comparable<T>> ArrayList<T> heapSort(ArrayList<T> list) {
        BinaryHeap<T> toSort = new BinaryHeap<>(list.toArray((T[]) new Comparable[0]));
        ArrayList<T> sortedList = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {
            sortedList.add(toSort.deleteMin());
        }

        return sortedList;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
