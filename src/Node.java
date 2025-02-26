// A special implementation of the Node class for wizard map project
public class Node {
    public int x;
    public int y;
    public int type;
    public Node right;
    public Node left;
    public Node top;
    public Node bottom;
    public float rightTime;
    public float leftTime;
    public float topTime;
    public float bottomTime;
    public float minTime;
    public Node parent;
    public int knownType;
    public boolean isVisited; // Only used for dijkstra's algorithm
    public boolean isSightUpdated;

    public Node() {}

    public Node(int x, int y, int type, float minTime) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.minTime = minTime;
    }

    public Node(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.minTime = Float.MAX_VALUE;
    }

    public Node(int x, int y, int type, int knownType) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.knownType = knownType;
        this.isVisited = false;
        this.isSightUpdated = false;
        this.minTime = Float.MAX_VALUE;
    }

    public Node(int x, int y, int type, Node right, Node top, int rightTime, int topTime) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.right = right;
        this.top = top;
        this.rightTime = rightTime;
        this.topTime = topTime;
        this.minTime = Float.MAX_VALUE;
    }
}
