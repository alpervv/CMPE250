
public class AVLTree {
    public Node root;

    public AVLTree() {}

    // getHeight method handles the node being null case
    private int getHeight(Node node) {
        if (node == null) return -1;
        return node.h;
    }

    private int getMaxHeight(int leftNodeHeight, int rightNodeHeight) {
        if (leftNodeHeight > rightNodeHeight) return leftNodeHeight;
        return rightNodeHeight;
    }
    
    public void insert(int element, ParkingLot parkingLot){
        if (root == null){
            root = new Node(element, parkingLot);
        }
        else insertElement(element, parkingLot, root, null);
    }

    private void insertElement(int capacity, ParkingLot parkingLot, Node node, Node parent){
        if (node.element > capacity){
            if (node.left == null){
                node.left = new Node(capacity, parkingLot);
            }
            else insertElement(capacity, parkingLot, node.left, node);
        }
        else if (node.element < capacity){
            if (node.right == null){
                node.right = new Node(capacity, parkingLot);
            }
            else insertElement(capacity, parkingLot, node.right, node);
        }
        else { // Duplicate capacity
            return;
        }


        node.h = getMaxHeight( getHeight( node.left ), getHeight( node.right ) ) + 1; // First update height
        balanceCheck(node, parent); // Check for balance

    }

    private void balanceCheck(Node node, Node parent) {
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
        } else if (parent.element > node.element) {
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

    public boolean contains(int capacity){
        Node current = root;
        while (current != null) {
            if (current.element == capacity) {
                return true;
            }
            else if (current.element < capacity) {
                current = current.right;
            }
            else {
                current = current.left;
            }
        }
        return false;
    }

    public void remove(int capacity) {
        root = removeElement(root, capacity);
    }

    private Node removeElement(Node node, int element) {
        if (node == null) {
            return null; // Element not in tree
        }

        // Traverse the tree to find the node to remove
        if (element < node.element) {
            node.left = removeElement(node.left, element);
        } else if (element > node.element) {
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
                Node successor = findMin(node.right);
                node.element = successor.element;
                node.parkingLotReference = successor.parkingLotReference;
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
}