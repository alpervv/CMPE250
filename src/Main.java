import java.io.*;
import java.util.ArrayList;

/**
 * @author Alper Vural, ID:2023400066
 */
public class Main {
    private static Node[][] allNodes;


    public static void main(String[] args) throws IOException {
        int mapSizeX;
        int mapSizeY;


        File nodesFile = new File(args[0]);
        File edgesFile = new File(args[1]);
        File objFile = new File(args[2]);
        File outputFile = new File(args[3]);

        // Read data for nodes and types
        try(BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {
                String line = br.readLine();
                mapSizeX = Integer.parseInt(line.split(" ")[0]);
                mapSizeY = Integer.parseInt(line.split(" ")[1]);
                allNodes = new Node[mapSizeX][mapSizeY];
                while (br.ready()) {
                    line = br.readLine();
                    String[] tokens = line.split(" ");
                    int x = Integer.parseInt(tokens[0]);
                    int y = Integer.parseInt(tokens[1]);
                    int type = Integer.parseInt(tokens[2]);;
                    allNodes[x][y] = new Node(x, y, type, type==1 ? 1: 0); // Known type is 1 for type 1, 0 for all others
                }
        }

        // Read data for travel times between nodes.
        float[][] rightTravelTimes = new float[mapSizeX-1][mapSizeY];
        float[][] topTravelTimes = new float[mapSizeX][mapSizeY-1];

        try(BufferedReader br = new BufferedReader(new FileReader(edgesFile))) {
                String line;
                while (br.ready()) {
                    line = br.readLine();
                    String[] tokens = line.split(" ");
                    String [] tempNodes = tokens[0].split(",");
                    String[] firstNode = tempNodes[0].split("-");
                    int x1 = Integer.parseInt(firstNode[0]);
                    int y1 = Integer.parseInt(firstNode[1]);
                    String[] secondNode = tempNodes[1].split("-");
                    int x2 = Integer.parseInt(secondNode[0]);
                    int y2 = Integer.parseInt(secondNode[1]);
                    float travelTime = Float.parseFloat(tokens[1]);

                    if (x1<=x2 && y1<=y2) { // Ensure that the edge is from first node to the second
                        if (x2 - x1 == 0) { // Vertical edge
                            topTravelTimes[x1][y1] = travelTime;
                        } else rightTravelTimes[x1][y1] = travelTime;
                    }
                    else{ // This is the case if the edge is provided in reverse order
                        if (x2 - x1 == 0) { // Vertical edge
                            topTravelTimes[x2][y2] = travelTime;
                        } else rightTravelTimes[x2][y2] = travelTime;
                    }

            }
        }

        // Set up the specially constructed nodes. Update each node by adding right and top nodes with their travel times.
        for (int x = 0; x < mapSizeX-1; x++) {
            for (int y = 0; y < mapSizeY; y++) {
                allNodes[x][y].right = allNodes[x+1][y];
                allNodes[x][y].rightTime = rightTravelTimes[x][y];

                allNodes[x+1][y].left = allNodes[x][y];
                allNodes[x+1][y].leftTime = rightTravelTimes[x][y];
            }
        }
        for (int x = 0; x < mapSizeX; x++) {
            for (int y = 0; y < mapSizeY-1; y++) {
                allNodes[x][y].top = allNodes[x][y+1];
                allNodes[x][y].topTime = topTravelTimes[x][y];

                allNodes[x][y+1].bottom = allNodes[x][y];
                allNodes[x][y+1].bottomTime = topTravelTimes[x][y];
            }
        }

        // Read objectives and write the logs simultaneously
        try(BufferedReader br = new BufferedReader(new FileReader(objFile))) {
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                int r = Integer.parseInt(br.readLine()); // Radius of line of sight
                // Create the spherical edges for horizontal and vertical movement cases
                ArrayList<int[]> horizontalSight = new ArrayList<>();

                for (int x = 0; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        if (x*x+y*y <= r*r && (x+1)*(x+1)+y*y > r*r) {
                            horizontalSight.add(new int[]{x, y});
                        }
                    }
                }


                String[] tokens = br.readLine().split(" ");
                Node source = allNodes[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])];
                // Update the sight at the source. This will be the only full circular update.
                for (int i = -r; i <= r; i++) {
                    for (int j = (int) (-Math.sqrt(r * r - i * i)); j <= (int) (Math.sqrt(r * r - i * i)); j++) {
                        // Update the node at the position x+i, y+j
                        int inspectedX = source.x + i;
                        if (inspectedX > mapSizeX - 1) inspectedX = mapSizeX - 1;
                        else if (inspectedX < 0) inspectedX = 0;

                        int inspectedY = source.y + j;
                        if (inspectedY > mapSizeY - 1) inspectedY = mapSizeY - 1;
                        else if (inspectedY < 0) inspectedY = 0;

                        Node inspectedNode = allNodes[inspectedX][inspectedY];
                        inspectedNode.knownType = inspectedNode.type;
                    }
                }

                ArrayList<Integer> options = new ArrayList<>();
                int objCount = 0;
                while(br.ready()) {
                    tokens = br.readLine().split(" ");
                    objCount++;
                    Node target = allNodes[Integer.parseInt(tokens[0])][Integer.parseInt(tokens[1])]; // The target node


                    // There is no condition for the while loop, but is designed to terminate when target node is reached.
                    while (true) {
                        ArrayList<Node> initialPath;
                        // Wizard gives no option
                        if (options.isEmpty()){
                            initialPath = wizardDijkstra(source, target);
                        }
                        else{ // Wizard gives some options
                            int bestOption = wizardDijkstra(source, target, options);
                            bw.write("Number "+bestOption+" is chosen!");
                            bw.newLine();
                            options.clear(); // Empty options to avoid finding best option over and over
                            // Turn all nodes of the chosen type to type 0
                            for(int x = 0; x < mapSizeX; x++) {
                                for(int y = 0; y < mapSizeY; y++) {
                                    if (allNodes[x][y].type == bestOption) {
                                        allNodes[x][y].type = 0;
                                        allNodes[x][y].knownType = 0;
                                    }
                                }
                            }
                            initialPath = wizardDijkstra(source, target);

                        }

                        boolean isPassable = true;
                        // Line of sight update
                        initialPath.removeFirst();
                        for (Node node : initialPath) {
                            bw.write("Moving to "+node.x+"-"+node.y);
                            bw.newLine();

                            if(node.isSightUpdated){ // No need to update the sight as it has been updated before.
                                continue;
                            }

                            int moveType = 0;
                            if (node.x-node.parent.x == 1)
                                moveType = 1; // right movement
                            else if (node.y-node.parent.y == 1)
                                moveType = 2; // up move
                            else if (node.x-node.parent.x == -1)
                                moveType = 3; // left move
                            else if (node.y-node.parent.y == -1)
                                moveType = 4; // down move

                            // Update all nodes in the radius before proceeding to new path creation (if initial path was impassable)
                            for (int[] xy: horizontalSight) {
                                    int i=0; int j=0;
                                    if (moveType == 1){
                                        i=xy[0];
                                        j=xy[1];
                                    }
                                    else if (moveType == 2){
                                        i=xy[1];
                                        j=xy[0];
                                    }
                                    else if (moveType == 3){
                                        i=-xy[0];
                                        j=xy[1];
                                    }
                                    else if (moveType == 4){
                                        i=xy[1];
                                        j=-xy[0];
                                    }

                                    // Update the node at the position x+i, y+j
                                    int inspectedX = node.x + i;
                                    if (inspectedX > mapSizeX - 1) inspectedX = mapSizeX - 1;
                                    else if (inspectedX < 0) inspectedX = 0;

                                    int inspectedY = node.y + j;
                                    if (inspectedY > mapSizeY - 1) inspectedY = mapSizeY - 1;
                                    else if (inspectedY < 0) inspectedY = 0;

                                    Node inspectedNode = allNodes[inspectedX][inspectedY];
                                    if (inspectedNode.knownType != inspectedNode.type) {
                                        inspectedNode.knownType = inspectedNode.type;
                                        if (inspectedNode.type != 0 && initialPath.contains(inspectedNode)) {
                                            isPassable = false;
                                        }
                                    }

                            }
                            node.isSightUpdated = true; // Mark the node as sight updated.

                            // If the path is observed to be impassable, reconstruct a path with up-to-date information.
                            if (!isPassable) {
                                source = node;
                                bw.write("Path is impassable!");
                                bw.newLine();
                                break; // break out of the loop traversing the impassable path
                            }
                        }
                        if (isPassable) {
                            bw.write("Objective "+ objCount+" reached!");
                            bw.newLine();
                            break; // break out of the loop that reconstructs paths if they are impassable.
                        }
                        // Else, go back to loop and generate a new path.
                    }


                    // After the objective is reached
                    source = target;

                    // Bring in options
                    options = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i++) { // Move the options to a separate list
                        options.add(Integer.parseInt(tokens[i]));
                    }
                }
                bw.flush();
            }
        }
    } // End of main

    private static ArrayList<Node> wizardDijkstra(Node source, Node target) {
        setInitialTimes(source);

        // Create a binary heap of unvisited nodes where every node is stored with its minTime
        Entry[] tempArray = new Entry[allNodes.length*allNodes[0].length]; // temporary array of size x*y
        int index = 0;
        for(Node[] nodeArray : allNodes) {
            ;for (Node node : nodeArray) {
                tempArray[index] = new Entry(node, node.minTime);
                index++;
            }
        }
        BinaryHeap<Entry> unvisitedNodes = new BinaryHeap<Entry>(tempArray);

        while(true){
            // Find the min time unvisited node
            Entry currentEntry = unvisitedNodes.deleteMin();

            if (currentEntry == null) // This is the case where distances to all unvisited nodes are infinite
                break;
            Node current = currentEntry.node;
            if (current.isVisited){ // No need to update anything as the node was visited before
                continue;
            }
            if (current == target) // We are only interested in the path to target node
                break;

            // Continue Dijkstra's algorithm with a twist: Consider all neighbours of the current node instead of only unvisited ones
            // This won't yield an incorrect result, my implementation is more efficient this way

            // Always check if the node is null to avoid NullPointerException on edges
            // Ignore nodes which are known to be impassable, i.e. having a known non-zero knownType


            if (current.right != null &&  current.right.knownType == 0) {
                if (current.minTime + current.rightTime < current.right.minTime) {
                    current.right.minTime = current.minTime + current.rightTime;
                    current.right.parent = current;
                    unvisitedNodes.insert(new Entry(current.right, current.right.minTime));
                }
            }
            if (current.left != null && current.left.knownType == 0) {
                if (current.minTime + current.leftTime < current.left.minTime) {
                    current.left.minTime = current.minTime + current.leftTime;
                    current.left.parent = current;
                    unvisitedNodes.insert(new Entry(current.left, current.left.minTime));
                }
            }
            if (current.top != null && current.top.knownType == 0) {
                if (current.minTime + current.topTime < current.top.minTime) {
                    current.top.minTime = current.minTime + current.topTime;
                    current.top.parent = current;
                    unvisitedNodes.insert(new Entry(current.top, current.top.minTime));
                }
            }
            if (current.bottom != null && current.bottom.knownType == 0) {
                if (current.minTime + current.bottomTime < current.bottom.minTime) {
                    current.bottom.minTime = current.minTime + current.bottomTime;
                    current.bottom.parent = current;
                    unvisitedNodes.insert(new Entry(current.bottom, current.bottom.minTime));
                }
            }

            if(unvisitedNodes.isEmpty()) // Break when all nodes are visited
                break;
        }

        if (target.minTime == Float.MAX_VALUE)
            return null;
        ArrayList<Node> path = new ArrayList<>();
        Node current = target;
        while (current != source){
            path.add(current);
            current = current.parent;
        }
        path.add(source);
        return reverseArrayList(path);
    }

    // Returns the best option
    private static int wizardDijkstra(Node source, Node target, ArrayList<Integer> options){
        float bestOptionTime = Float.MAX_VALUE;
        int bestOption = -1;
        for (int option: options) {
            setInitialTimes(source);

            // Create a binary heap of unvisited nodes where every node is stored with its minTime
            Entry[] tempArray = new Entry[allNodes.length*allNodes[0].length]; // temporary array of size x*y
            int index = 0;
            for(Node[] nodeArray : allNodes) {
                ;for (Node node : nodeArray) {
                    tempArray[index] = new Entry(node, node.minTime);
                    index++;
                }
            }
            BinaryHeap<Entry> unvisitedNodes = new BinaryHeap<Entry>(tempArray);

            while(true){
                // Find the min time unvisited node
                Entry currentEntry = unvisitedNodes.deleteMin();

                if (currentEntry == null) // This is the case where distances to all unvisited nodes are infinite
                    break;
                Node current = currentEntry.node;
                if (current == target) // We are only interested in the path to target node
                    break;

                // Continue Dijkstra's algorithm with a twist: Consider all neighbours of the current node instead of only unvisited ones
                // This won't yield an incorrect result, my implementation is more efficient this way

                // Always check if the node is null to avoid NullPointerException on edges
                // Ignore nodes which are known to be impassable, i.e. having a known non-zero knownType

                if (current.right != null &&  (current.right.knownType == 0 || current.right.type == option)) {
                    if (current.minTime + current.rightTime < current.right.minTime) {
                        current.right.minTime = current.minTime + current.rightTime;
                        current.right.parent = current;
                        unvisitedNodes.insert(new Entry(current.right, current.right.minTime));
                    }
                }
                if (current.left != null && (current.left.knownType == 0 || current.left.type == option)) {
                    if (current.minTime + current.leftTime < current.left.minTime) {
                        current.left.minTime = current.minTime + current.leftTime;
                        current.left.parent = current;
                        unvisitedNodes.insert(new Entry(current.left, current.left.minTime));
                    }
                }
                if (current.top != null && (current.top.knownType == 0 || current.top.type == option)) {
                    if (current.minTime + current.topTime < current.top.minTime) {
                        current.top.minTime = current.minTime + current.topTime;
                        current.top.parent = current;
                        unvisitedNodes.insert(new Entry(current.top, current.top.minTime));
                    }
                }
                if (current.bottom != null && (current.bottom.knownType == 0 || current.bottom.type == option)) {
                    if (current.minTime + current.bottomTime < current.bottom.minTime) {
                        current.bottom.minTime = current.minTime + current.bottomTime;
                        current.bottom.parent = current;
                        unvisitedNodes.insert(new Entry(current.bottom, current.bottom.minTime));
                    }
                }

                if(unvisitedNodes.isEmpty()) // Break when all nodes are visited
                    break;
            }

            if (target.minTime == Float.MAX_VALUE)
                continue; // The option isn't suitable as it makes the target unreachable
            if (target.minTime < bestOptionTime) {
                bestOptionTime = target.minTime;
                bestOption = option;
            }
        }
        return bestOption;
    }


    private static <T> ArrayList<T> reverseArrayList(ArrayList<T> list){
        ArrayList<T> reversed = new ArrayList<>();
        reversed.ensureCapacity(list.size());
        for (int i = list.size() - 1; i >= 0; i--) {
            reversed.add(list.get(i));
        }
        return reversed;
    }

    /**
     * Sets minTime parameter of all nodes to infinity, except the source node.
     * Sets the minTime of source to 0.
     * Marks all the nodes as unvisited.
     */
    private static void setInitialTimes(Node source) {
        for(Node[] nodeArray : allNodes) {
            for(Node node : nodeArray) {
                node.minTime = Float.MAX_VALUE;
                node.isVisited = false;
            }
        }
        source.minTime = 0;
    }
}