/** ParkingLot is a complex class being both a node in linked and a parking lot
 *
 */
public class ParkingLot {
    boolean isActive;
    int capacityConstraint;
    int truckLimit;
    int remainingLimit;
    TruckQueue ready_queue;
    TruckQueue waiting_queue;
    public ParkingLot next;
    public ParkingLot prev;
    public ParkingLot loadNext;
    public ParkingLot loadPrev;
    public ParkingLot readyNext;
    public ParkingLot readyPrev;
    public ParkingLot addTruckNext;
    public ParkingLot addTruckPrev;

    ParkingLot(int capacity_onstraint, int truck_limit){
        capacityConstraint = capacity_onstraint;
        truckLimit = truck_limit;
        ready_queue = new TruckQueue();
        waiting_queue = new TruckQueue();
        remainingLimit = truck_limit;
        next = null;
        prev = null;
        isActive = true;
    }

    ParkingLot(){}
}
