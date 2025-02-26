public class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 101;
    private int capacity; // capacity for the array
    private HashLinkedList<K, V>[] array;
    private int size; // Current number of elements

    HashTable(){
        this.capacity = DEFAULT_CAPACITY;
        this.array = new HashLinkedList[this.capacity];
    }

    /**
     * Inserts the key value pair to the hash table. If another pair with the same key already exists,
     * updates the value to the new one. Key can't be null
     */
    public void put(K key, V value){
        if (key == null)
            return;
        if(size == capacity){
            reHash();
        }
        int position = key.hashCode() % this.capacity;
        if (position < 0)
            position += this.capacity;
        if(array[position] == null){
            array[position] = new HashLinkedList<>();
        }
        if(array[position].put(key, value))
            size++;
    }

    /**
     * @return null if the key isn't in the table, otherwise the value corresponding to the key.
     */
    public V get(K key){
        if (key == null)
            return null;

        int position = key.hashCode() % this.capacity;
        if (position < 0)
            position += this.capacity;
        if (array[position] == null)
            return null;
        return array[position].get(key);
    }

    /**
     * Removes the key with its value from the hash table.
     * @param key a non-null key
     */
    public void remove(K key){
        if (key == null)
            return;

        int position = key.hashCode() % this.capacity;
        if (position < 0)
            position += this.capacity;
        if (array[position] == null)
            return;
        if(array[position].remove(key))
            size--;
    }

    /**
     * Checks whether the key is actively used in the hashtable
     * @return true if the key is in the hashtable, false if it isn't or the key is null
     */
    public boolean containsKey(K key){
        if (key == null)
            return false;

        int position = key.hashCode() % this.capacity;
        if (position < 0)
            position += this.capacity;
        if (array[position] == null)
            return false;
        return array[position].containsKey(key);
    }

    /**
     * Internal method to increase the capacity of the hashtable
     */
    private void reHash(){
        int oldCapacity = capacity;
        capacity = 2*oldCapacity;
        var oldArray = array;
        array = new HashLinkedList[capacity];
        size = 0;
        // rehash everything in the oldArray
        for (HashLinkedList list : oldArray){
            if (list == null){
                continue;
            }
            HashLinkedList.Node current = list.head.next;

            while (current != list.tail){
                this.put((K)current.key, (V)current.value);
                current = current.next;
            }
        }
    }

    /**
     * Linked list to use in separate chaining.
     */
    private class HashLinkedList<K, V> {

        private class Node<K, V> {
            public K key;
            public V value;
            public Node<K, V> next;

            public Node(K key, V value) {
                this.key = key;
                this.value = value;
                this.next = null;
            }
        }

        public Node<K, V> head;
        public Node<K, V> tail;

        public HashLinkedList() {
            head = new Node<K, V>(null, null);
            tail = new Node<K, V>(null, null);
            head.next = tail;
        }

        /**
         * Updates the key value pair if it exists in the list, appends at the end of the linked list otherwise.
         * @return false if the key existed before, true if a new key is added.
         */
        public boolean put(K key, V value) {
            Node<K, V> current = head.next;
            while(current != tail) {
                if(current.key.equals(key)) {
                    current.value = value;
                    return false;
                }
                current = current.next;
            }
            // Append at the end of the list
            tail.key = key;
            tail.value = value;
            tail.next = new Node<>(null, null);
            tail = tail.next;
            return true;
        }

        /**
         * Removes the key with its corresponding value from the list.
         * @return true if remove is successful, false if the remove failed due to nonexistent key.
         */
        public boolean remove(K key) {
            if (key == null) { // This is done in order not to remove the head and tail
                return false;
            }
            Node<K, V> current = head.next;
            while (current != tail) {
                if (current.key.equals(key)) { // Be careful at removing the node right before the tail.
                    if (current.next == tail){
                        current.key = null;
                        current.value = null;
                        current.next = null;
                        tail = current;
                        return true;
                    }
                    else {
                        current.key = current.next.key;
                        current.value = current.next.value;
                        current.next = current.next.next;
                        return true;
                    }
                }
                current = current.next;
            }
            return false;
        }

        public V get(K key) {
            Node<K, V> current = head.next;
            while(current != tail) {
                if (current.key.equals(key)) {
                    return current.value;
                }
                current = current.next;
            }
            return null;
        }

        /**
         * Check whether the value appears in the linked list
         */
        public boolean contains(V value) {
            Node<K, V> current = head.next;
            while (current != tail) {
                if (current.value.equals(value)) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        /**
         * Checks whethet the key appears in the linked list
         */
        public boolean containsKey(K key) {
            Node<K, V> current = head.next;
            while (current != tail) {
                if (current.key.equals(key)) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

    }

}
