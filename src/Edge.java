/**
 *
 * @author TravisBonneau
 */
public class Edge implements Comparable<Edge>{
    
    public Node nodeOne;
    public Node nodeTwo;
    private double distance;

    public Edge(Node nodeOne, Node nodeTwo) {
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
        distance = nodeOne.point.distance(nodeTwo.point);
    }

    @Override
    public int compareTo(Edge o) {
        return Double.compare(distance, o.distance);
    }
    
    
}
