// Doubly Linked list implementation of queue
public class TruckQueue {
    private class Node {
        public Node next;
        public Node prev;
        public int[] value; // store truck in an int[3]

        public Node(int[] value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public TruckQueue() {
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.prev = head;
    }

    public int[] pop(){
        if (size > 0){
            Node returnNode = head.next;
            head.next = returnNode.next;
            returnNode.next.prev = head;
            size--;
            return returnNode.value;
        }
        else{
            return null;
        }
    }

    public void push(int[] value){
        Node newNode = new Node(value);
        newNode.next = tail;
        newNode.prev = tail.prev;
        tail.prev = newNode;
        newNode.prev.next = newNode;
        size++;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }
}
