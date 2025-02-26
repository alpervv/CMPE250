import java.io.*;

/**
 * Main class to create the output file given an input file
 * <br>
 * - Store each truck in an integer array of length 3: [Truck_ID, truck_capacity, truck_load].
 * This is more efficient than storing each truck in a Truck object because arrays are faster, and it is easy to find 3 consecutive empty cells in the heap.
 * <br>
 * - Store each parking lot in a ParkingLot object: ParkingLot(capacity_constraint, truck_limit, ready_queue, waiting_queue)
 * <br>
 * -We have to locate parking lots with their capacities. We also have to access other elements with order relation.
 * Order relation is best achievable with linked lists. Nodes will have increasing capacity from head to tail.
 * <br>
 * - We also need to find a certain parking lot with a specified order relation if the requested parking lot can't be found.
 * Binary trees are suitable to search with this extra condition. We will store references of parking lots in binary trees as well.
 * @author Alper Vural 2023400066
 * @since 11/4/2024
 */
public class Main {
    // Head has the smallest capacity_constraint whereas the tail has the largest.
    public static ParkingLot head = new ParkingLot(0,0);
    public static ParkingLot tail = new ParkingLot(Integer.MAX_VALUE,0);

    // Create AVL trees to mapping capacityConstraints to parking lots. Each tree stores parking lots suitable for a certain action.
    public static AVLTree capToParkTree = new AVLTree(); // Tree containing all parking lots
    public static AVLTree addTruckCapToParkTree = new AVLTree(); // Only contains parking lots with available slots, i.e. a truck can be added
    public static AVLTree readyCapToParkTree = new AVLTree(); // Only contains parking lots suitable for a ready order, i.e. non-empty waiting section
    public static AVLTree loadCapToParkTree = new AVLTree();  // Only contains parking lots suitable for a load order, i.e. non-empty ready section


    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();

        // There are 4 abstract doubly linked lists. Their elements are the same nodes (good for less capacity).
        // ParkingLots linked to each other with actionNameNext and actionNamePrev form LLs suitable for an actionName order.
        head.next = tail; // Set the head and tail pointers
        tail.prev = head;
        head.loadNext = tail;
        tail.loadPrev = head;
        head.readyNext = tail;
        tail.readyPrev = head;
        head.addTruckNext = tail;
        tail.addTruckPrev = head;

        // Insert the head and tail to AVL trees.
        capToParkTree.insert(0,head);
        capToParkTree.insert(Integer.MAX_VALUE,tail);
        loadCapToParkTree.insert(0,head);
        loadCapToParkTree.insert(Integer.MAX_VALUE,tail);
        readyCapToParkTree.insert(0,head);
        readyCapToParkTree.insert(Integer.MAX_VALUE,tail);
        addTruckCapToParkTree.insert(0,head);
        addTruckCapToParkTree.insert(Integer.MAX_VALUE,tail);

        File inputFile = new File("type4-large.txt");
        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
                while (br.ready()) {
                    String line = br.readLine();
                    String[] tokens = line.split(" ");
                    if (tokens[0].equals("create_parking_lot")){
                        createParkingLot(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                    }
                    if (tokens[0].equals("delete_parking_lot")){
                        deleteParkingLot(Integer.parseInt(tokens[1]));
                    }
                    if (tokens[0].equals("add_truck")){
                        String outputStr = addTruck(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), 0);
                        bw.write(outputStr);
                        bw.newLine();
                        bw.flush();
                    }
                    if (tokens[0].equals("ready")) {
                        String outputStr = ready(Integer.parseInt(tokens[1]));
                        bw.write(outputStr);
                        bw.newLine();
                        bw.flush();
                    }
                    if (tokens[0].equals("load")) {
                        String outputStr = load(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                        bw.write(outputStr);
                        bw.newLine();
                        bw.flush();

                    }
                    if (tokens[0].equals("count")){
                        String outputStr= count(Integer.parseInt(tokens[1]));
                        bw.write(outputStr);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            System.out.println("Error reading file");
        }
        System.out.println(System.currentTimeMillis() - currentTime);
    }

    public static void createParkingLot(int capacityConstraint, int truckLimit){
        ParkingLot parkingLot = new ParkingLot(capacityConstraint,truckLimit);

        //Place the parking lot in the tree and LL of all parking lots
        ParkingLot current = findClosest(capacityConstraint, capToParkTree);

        if (capacityConstraint < current.capacityConstraint) { // Place before the current parking lot
            parkingLot.prev = current.prev;
            current.prev.next = parkingLot;
            current.prev = parkingLot;
            parkingLot.next = current;
            capToParkTree.insert(capacityConstraint, parkingLot);
        }
        else if (capacityConstraint > current.capacityConstraint) {// Place after the current parking lot
            parkingLot.prev = current;
            parkingLot.next = current.next;
            current.next.prev = parkingLot;
            current.next = parkingLot;
            capToParkTree.insert(capacityConstraint, parkingLot);
        }
        else return; // Existing parking lot

        // Place the parking lot in the tree and LL of parking lots suitable for adding truck
        current = findClosest(capacityConstraint, addTruckCapToParkTree);
        if (capacityConstraint < current.capacityConstraint) { // Place before the current parking lot
            parkingLot.addTruckPrev = current.addTruckPrev;
            current.addTruckPrev.addTruckNext = parkingLot;
            current.addTruckPrev = parkingLot;
            parkingLot.addTruckNext = current;
            addTruckCapToParkTree.insert(capacityConstraint, parkingLot);
        }
        else if (capacityConstraint > current.capacityConstraint) { // Place after the current parking lot
            parkingLot.addTruckPrev = current;
            parkingLot.addTruckNext = current.addTruckNext;
            current.addTruckNext.addTruckPrev = parkingLot;
            current.addTruckNext = parkingLot;
            addTruckCapToParkTree.insert(capacityConstraint, parkingLot);
        }
    }

    public static void deleteParkingLot(int capacityConstraint){
        Node currentNode = capToParkTree.root;
        while (currentNode != null){
            if (capacityConstraint < currentNode.element){
                currentNode = currentNode.left;
            }
            else if (capacityConstraint > currentNode.element){
                currentNode = currentNode.right;
            }
            else {
                ParkingLot current = currentNode.parkingLotReference;
                // Remove the parking lot from all trees and LLs.
                capToParkTree.remove(capacityConstraint);
                current.prev.next = current.next;
                current.next.prev = current.prev;
                if (addTruckCapToParkTree.contains(capacityConstraint)){
                    current.addTruckPrev.addTruckNext = current.addTruckNext;
                    current.addTruckNext.addTruckPrev = current.addTruckPrev;
                    addTruckCapToParkTree.remove(capacityConstraint);
                }
                if (readyCapToParkTree.contains(capacityConstraint)){
                    current.readyPrev.readyNext = current.readyNext;
                    current.readyNext.readyPrev = current.readyPrev;
                    readyCapToParkTree.remove(capacityConstraint);
                }
                if (loadCapToParkTree.contains(capacityConstraint)){
                    current.loadPrev.loadNext = current.loadNext;
                    current.loadNext.loadPrev = current.loadPrev;
                    loadCapToParkTree.remove(capacityConstraint);
                }
                return;
            }
        }
    }

    public static String addTruck(int truckId, int truckCapacity, int truckLoad){
        int[] truck = new int[]{truckId,truckCapacity,truckLoad};
        int capacityConstraint = truckCapacity-truckLoad;

        // Find the first suitable parking lot to add a truck
        ParkingLot current = findClosest(capacityConstraint, addTruckCapToParkTree);
        if (capacityConstraint < current.capacityConstraint){ // The node with the smallest capacity that is larger than capacityConstraint is found.
            current = current.addTruckPrev;
        }

        if (current == head || current == null)
            return String.valueOf(-1); // No available parking lot

        current.waiting_queue.push(truck);
        current.remainingLimit--;

        if (current.remainingLimit == 0){ // Remove current parking lot from addTruck tree and LL
            addTruckCapToParkTree.remove(current.capacityConstraint);
            current.addTruckNext.addTruckPrev = current.addTruckPrev;
            current.addTruckPrev.addTruckNext = current.addTruckNext;
        }

        if (current.waiting_queue.size() == 1){ // current parking lot wasn't suitable for a ready order, but now it is.
            ParkingLot current1 = findClosest(current.capacityConstraint, readyCapToParkTree);
            // Insertion logic to doubly linked list
            if (current.capacityConstraint < current1.capacityConstraint) { // Place before the current parking lot
                current.readyPrev = current1.readyPrev;
                current1.readyPrev.readyNext = current;
                current1.readyPrev = current;
                current.readyNext = current1;
            }
            else if (current.capacityConstraint > current1.capacityConstraint) {// Place after the current parking lot
                current.readyPrev = current1;
                current.readyNext = current1.readyNext;
                current1.readyNext.readyPrev = current;
                current1.readyNext = current;
            }
            readyCapToParkTree.insert(current.capacityConstraint, current);

        }
        return String.valueOf(current.capacityConstraint);
    }

    public static String ready(int capacityConstraint){
        ParkingLot current = findClosest(capacityConstraint, readyCapToParkTree);
        if (capacityConstraint > current.capacityConstraint){ // The node with the largest capacity that is smaller than capacityConstraint is found.
            current = current.readyNext;
        }

        if (current != tail && current != null){ // Don't issue ready orders on tail
            int[] truck = current.waiting_queue.pop();
            current.ready_queue.push(truck);
            if (current.waiting_queue.size() == 0){ // No trucks in waiting section, remove current parking lot from ready LL and tree
                readyCapToParkTree.remove(current.capacityConstraint);
                current.readyPrev.readyNext = current.readyNext;
                current.readyNext.readyPrev = current.readyPrev;
            }
            if (current.ready_queue.size() == 1){ // current parking lot  wasn't suitable for a load order, but now it would be.
                ParkingLot current1 = findClosest(current.capacityConstraint, loadCapToParkTree);
                // Insertion logic to doubly linked list
                if (current.capacityConstraint < current1.capacityConstraint) { // Place before the current parking lot
                    current.loadPrev = current1.loadPrev;
                    current1.loadPrev.loadNext = current;
                    current1.loadPrev = current;
                    current.loadNext = current1;
                }
                else if (current.capacityConstraint > current1.capacityConstraint) {// Place after the current parking lot
                    current.loadPrev = current1;
                    current.loadNext = current1.loadNext;
                    current1.loadNext.loadPrev = current;
                    current1.loadNext = current;
                }
                loadCapToParkTree.insert(current.capacityConstraint, current);
            }
            return truck[0] + " " + current.capacityConstraint;
        }
        return String.valueOf(-1); // No available truck
    }


    public static String load(int capacity, int loadAmount){
        ParkingLot current = findClosest(capacity, loadCapToParkTree);
        if (capacity > current.capacityConstraint){ // The node with the largest capacity that is smaller than capacityConstraint is found.
            current = current.loadNext;
        }

        String output = "";
        while (current != tail && current != null){ // Don't issue orders on tail
            while (!current.ready_queue.isEmpty()){
                int[] truck = current.ready_queue.pop();
                current.remainingLimit++;

                if (current.remainingLimit == 1){ // Add current to addTruck LL and tree if it isn't there. (1 slot opened by popping)
                    int capacityConstraint = current.capacityConstraint;
                    ParkingLot current1 = findClosest(capacityConstraint, addTruckCapToParkTree);
                    // Insertion logic to doubly linked list
                    if (capacityConstraint < current1.capacityConstraint) { // Place before the current1 parking lot
                        current.addTruckPrev = current1.addTruckPrev;
                        current1.addTruckPrev.addTruckNext = current;
                        current1.addTruckPrev = current;
                        current.addTruckNext = current1;
                    }
                    else if (capacityConstraint > current1.capacityConstraint) {// Place after the current1 parking lot
                        current.addTruckPrev = current1;
                        current.addTruckNext = current1.addTruckNext;
                        current1.addTruckNext.addTruckPrev = current;
                        current1.addTruckNext = current;
                    }
                    addTruckCapToParkTree.insert(capacityConstraint, current);
                }

                if (loadAmount > current.capacityConstraint) { // If load can't be finished in one move
                    loadAmount -= current.capacityConstraint;
                    if (truck[2]+current.capacityConstraint == truck[1]){ // If the truck is full
                        String out = addTruck(truck[0], truck[1], 0);
                        output += truck[0] + " " + out + " - ";
                    } else {
                        String out = addTruck(truck[0], truck[1], truck[2]+current.capacityConstraint);
                        output += truck[0] + " " + out + " - ";
                    }

                } else { // If the load will finish after one move
                    if (truck[2]+loadAmount == truck[1]){ // If the truck is full
                        String out = addTruck(truck[0], truck[1], 0);
                        output += truck[0] + " " + out;
                    } else {
                        String out = addTruck(truck[0], truck[1], truck[2]+loadAmount);
                        output += truck[0] + " " + out;
                    }
                    if (current.ready_queue.isEmpty()){ //load order can no longer be issued on current
                        loadCapToParkTree.remove(current.capacityConstraint);
                        current.loadPrev.loadNext = current.loadNext;
                        current.loadNext.loadPrev = current.loadPrev;
                    }
                    return output;
                }
            }
            // Empty ready_queue granted, load order can't be issued on current
            loadCapToParkTree.remove(current.capacityConstraint);
            current.loadPrev.loadNext = current.loadNext;
            current.loadNext.loadPrev = current.loadPrev;

            current = current.loadNext; // Increase capacity constraint
        }

        if (output.equals("")){// If no truck has been loaded
            return String.valueOf(-1);
        }
        return output.substring(0,output.length()-3); // Some load wasted. Remove the " - " at the end.
    }

    public static String count(int capacity){
        int count = 0;

        // Approach: For parking lots in load LL, only count trucks in ready section; for parking lots in ready LL only count trucks in waiting section.
        // This ensures all trucks are counted exactly once.

        // Locate the first parking lot having a capacityConstraint higher than "capacity"
        ParkingLot current = findClosest(capacity, loadCapToParkTree);
        if (current.capacityConstraint <= capacity){
            current = current.loadNext;
        }
        if (!(current == tail && current == null)) { // "capacity" shouldn't be higher than the capacityConstraint of any parking lot
            while (current != tail) {
                count += current.ready_queue.size();
                current = current.loadNext;
            }
        }

        // Locate the first parking lot having a capacityConstraint higher than "capacity"
        current = findClosest(capacity, readyCapToParkTree);
        if (current.capacityConstraint <= capacity){
            current = current.readyNext;
        }
        if (!(current == tail && current == null)) { // "capacity" shouldn't be higher than the capacityConstraint of any parking lot
            while (current != tail) {
                count += current.waiting_queue.size();
                current = current.readyNext;
            }
        }
        return String.valueOf(count);
    }

    /**
     * Finds the parking lot whose "capacityConstraint" is closest to capacityConstraint
     * @return If capacityConstraint is existent in the tree, returns the node with that capacityConstraint.
     * Otherwise, either returns the ParkingLot with the largest capacity smaller than capacityConstraint or
     * the ParkingLot with the smallest capacity larger than capacityConstraint.
     */
    public static ParkingLot findClosest(int capacityConstraint, AVLTree searchTree){
        Node currentNode = searchTree.root;
        while (true) {
            if (capacityConstraint < currentNode.element) {
                if (currentNode.left == null) { // The node with the smallest capacity that is larger than capacityConstraint is found.
                    return currentNode.parkingLotReference;
                }
                currentNode = currentNode.left;
            } else if (capacityConstraint > currentNode.element) {
                if (currentNode.right == null) { // The node with the largest capacity that is smaller than capacityConstraint is found.
                    return currentNode.parkingLotReference;
                }
                currentNode = currentNode.right;
            } else { // Node with same capacity
                return currentNode.parkingLotReference;
            }
        }
    }
}