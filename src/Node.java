public class Node {
    int element; // capacity_constraint
    ParkingLot parkingLotReference;
    int h;  //for height
    public Node left;
    public Node right;

    public Node(int element, ParkingLot parkingLotReference){
        this.element = element;
        this.parkingLotReference = parkingLotReference;
        h = 0;
        left = null;
        right = null;
    }
}