import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> {
    private class Node<T> {
        T element; // capacity_constraint
        int h;  //for height
        public Node left;
        public Node right;

        public Node(T element){
            this.element = element;
            h = 0;
            left = null;
            right = null;
        }
    }

    public Node root;

    public AVLTree() {
        root = null;
    }

    // getHeight method handles the node being null case
    private int getHeight(Node node) {
        if (node == null) return -1;
        return node.h;
    }

    private int getMaxHeight(int leftNodeHeight, int rightNodeHeight) {
        if (leftNodeHeight > rightNodeHeight) return leftNodeHeight;
        return rightNodeHeight;
    }

    public void insert(T element){
        if (root == null){
            root = new Node(element);
        }
        else insertElement(element, root, null);
    }

    private void insertElement(T element, Node<T> node, Node<T> parent){
        if (node.element.compareTo(element) > 0){
            if (node.left == null){
                node.left = new Node(element);
            }
            else insertElement(element, node.left, node);
        }
        else if (node.element.compareTo(element) < 0){
            if (node.right == null){
                node.right = new Node(element);
            }
            else insertElement(element, node.right, node);
        }
        else { // Duplicate capacity
            return;
        }


        node.h = getMaxHeight( getHeight( node.left ), getHeight( node.right ) ) + 1; // First update height
        balanceCheck(node, parent); // Check for balance

    }

    private void balanceCheck(Node<T> node, Node<T> parent) {
        // Balance checks
        if( getHeight(node.left) - getHeight(node.right) == 2 ){
            if(getHeight(node.left.left) > getHeight(node.left.right)){ // Left left case
                node = leftRotation(node);
            }
            else // Left right  case
                node = doubleLeftRotation(node);
        }

        if( getHeight(node.right) - getHeight(node.left) == 2 ){
            if( getHeight(node.right.right) > getHeight(node.right.left)){ // Right right case
                node = rightRotation(node);
            }
            else // Right left case
                node = doubleRightRotation( node );
        }

        // Link the rotated node to its parent
        if (parent == null) { // Case for imbalance at the root.
            root = node;
        } else if (parent.element.compareTo(node.element ) > 0) {
            parent.left = node;
        } else parent.right = node;
    }

    private Node leftRotation(Node node2){
        Node node1 = node2.left;
        node2.left = node1.right;
        node1.right = node2;
        node2.h = getMaxHeight( getHeight( node2.left ), getHeight( node2.right ) ) + 1;
        node1.h = getMaxHeight( getHeight( node1.left ), node2.h ) + 1;
        return node1;
    }

    // creating rightRotation() method to perform rotation of binary tree node with right child
    private Node rightRotation(Node node1) {
        Node node2 = node1.right;
        node1.right = node2.left;
        node2.left = node1;
        node1.h = getMaxHeight( getHeight( node1.left ), getHeight( node1.right ) ) + 1;
        node2.h = getMaxHeight( getHeight( node2.right ), node1.h ) + 1;
        return node2;
    }

    private Node doubleLeftRotation(Node node) {
        node.left = rightRotation( node.left );
        return leftRotation(node);
    }

    private Node doubleRightRotation(Node node) {
        node.right = leftRotation( node.right );
        return rightRotation( node );
    }

    public boolean contains(T element){
        Node<T> current = root;
        while (current != null) {
            if (current.element.equals(element)){
                return true;
            }
            else if (current.element.compareTo(element) < 0) {
                current = current.right;
            }
            else {
                current = current.left;
            }
        }
        return false;
    }

    public void remove(T element) {
        root = removeElement(root, element);
    }

    private Node<T> removeElement(Node<T> node, T element) {
        if (node == null) {
            return null; // Element not in tree
        }

        // Traverse the tree to find the node to remove
        if (element.compareTo(node.element) < 0) {
            node.left = removeElement(node.left, element);
        } else if (element.compareTo(node.element) > 0) {
            node.right = removeElement(node.right, element);
        } else {
            // Node with the element found
            if (node.left == null && node.right == null) { //Leaf node
                node = null;
            } else if (node.left == null) { // One child (right)
                node = node.right;
            } else if (node.right == null) { // One child (left)
                node = node.left;
            } else {
                // Two children, perform standard BST deletion and balance later
                Node<T> successor = findMin(node.right);
                node.element = successor.element;
                node.right = removeElement(node.right, successor.element);
            }
        }

        // If node is null after deletion, return null
        if (node == null) {
            return null;
        }

        node.h = getMaxHeight(getHeight(node.left), getHeight(node.right)) + 1;
        return balanceCheckReturn(node);
    }

    // Balance check with return type of Node
    private Node balanceCheckReturn(Node node) {
        // Balance checks
        if(getHeight( node.left ) > getHeight( node.right )+1){
            if(getHeight(node.left.left) > getHeight(node.left.right)){
                node = leftRotation(node);
            }
            else
                node = doubleLeftRotation( node );
        }

        else if(getHeight(node.right) > getHeight( node.left )+1){
            if( getHeight(node.right.right) > getHeight(node.right.left)){
                node = rightRotation(node);
            }
            else
                node = doubleRightRotation( node );
        }

        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void inOrder(){
        inOrder(root);
    }

    private void inOrder(Node node){
        if(node == null){
            return;
        }
        inOrder(node.left);
        System.out.println(node.element);
        inOrder(node.right);
    }
    public void preOrder(){
        preOrder(root);
    }

    private void preOrder(Node node){
        if(node == null){
            return;
        }
        System.out.print(node.element+" ");
        preOrder(node.left);
        preOrder(node.right);
    }

    public void postOrder(){
        postOrder(root);
    }

    private void postOrder(Node node){
        if(node == null){
            return;
        }
        postOrder(node.left);
        postOrder(node.right);
        System.out.print(node.element+" ");
    }

    public ArrayList<T> getDataAsArray() {
        ArrayList<T> toReturn = new ArrayList<>();
        createDataAsArray(root, toReturn);
        return toReturn;
    }

    private void createDataAsArray(Node<T> node, ArrayList<T> toReturn) {
        if (node == null) {
            return;
        }
        toReturn.add(node.element);
        createDataAsArray(node.left, toReturn);
        createDataAsArray(node.right, toReturn);
    }
}
