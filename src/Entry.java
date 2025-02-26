public class Entry implements Comparable<Entry> {
    public Node node;
    public float minTime;

    Entry(Node node, float minTime) {
        this.node = node;
        this.minTime = minTime;
    }

    public int compareTo(Entry o) {
        return this.minTime < o.minTime ? -1 : 1;
    }
}