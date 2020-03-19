import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Travis Bonneau
 */
public class Node {
    
    public Point point;
    public int index;
    private ArrayList<Edge> edges;
    
    public Node(Point p, int index){
        point = p;
        this.index = index;
        edges = new ArrayList<>();
    }
    
    public void add(Edge e){
        edges.add(e);
    }
    
    public ArrayList<Edge> getEdges(){
        return edges;
    }
    
    @Override
    public boolean equals(Object o) {
        Node n = (Node) o;
        return point.equals(n.point);
    }
}
