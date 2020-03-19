import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.Timer;

/**
 *
 * @author Travis Bonneau
 */
public class MinimumSpanningTreeVisualizer extends JFrame {
    
    // Graphical Components
    private JPanel graphPanel;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem randomGraphMenuItem;
    private JMenuItem delayTimeMenuItem;
    private JMenuItem clearMenuItem;
    private JMenu algorithmMenu;
    private JMenuItem primsMenuItem;
    private JMenuItem kruskalsMenuItem;
    
    private AlgorithmType type;
    private ArrayList<Node> nodeList;
    private ArrayList<Edge> edgeList;
    private PriorityQueue<Edge> primsQueue;
    private HashSet<Node> primsVisited;
    private LinkedList<Edge> kruskalsEdgeList;
    private UnionFind uf;
    private Timer graphTimer;
    private boolean isDrawingGraph;
    private int delayTime;
    
    public MinimumSpanningTreeVisualizer() {
        init();
        initTimer();
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Minimum Spanning Tree Visualizer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void init(){
        graphPanel = new JPanel();
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        randomGraphMenuItem = new JMenuItem("Random Graph");
        delayTimeMenuItem = new JMenuItem("Delay Time");
        clearMenuItem = new JMenuItem("Clear");
        algorithmMenu = new JMenu("Algorithm");
        primsMenuItem = new JMenuItem("Prim's");
        kruskalsMenuItem = new JMenuItem("Kruskal's");
        
        nodeList = new ArrayList<>();
        edgeList = new ArrayList<>();
        primsQueue = new PriorityQueue<>();
        primsVisited = new HashSet<>();
        kruskalsEdgeList = new LinkedList<>();
        isDrawingGraph = false;
        delayTime = 100;
        
        // Set Up Menu Bar
        randomGraphMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        delayTimeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
        clearMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        kruskalsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_DOWN_MASK));
        primsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        
        fileMenu.add(randomGraphMenuItem);
        fileMenu.add(delayTimeMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(clearMenuItem);
        algorithmMenu.add(kruskalsMenuItem);
        algorithmMenu.add(primsMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(algorithmMenu);
        
        // Set Up JFrame
        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);
        graphPanel.setBackground(Color.WHITE);
        setJMenuBar(menuBar);
        
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                addNode(e.getX(), e.getY());
            }
        });
        randomGraphMenuItem.addActionListener((MouseEvent) -> {
            clear();
            int numOfNodes = (int)(Math.random()*25+100);
            for(int i=0; i<numOfNodes; i++){
                int x = (int)(Math.random()*graphPanel.getWidth());
                int y = (int)(Math.random()*graphPanel.getHeight());
                addNode(x, y);
            }
        });
        delayTimeMenuItem.addActionListener((MouseEvent) -> {
            String newDelayTime = JOptionPane.showInputDialog("Enter time delay in milliseconds.", delayTime);
            while(!(newDelayTime.matches("[0-9]+") && Integer.parseInt(newDelayTime)>=0 && Integer.parseInt(newDelayTime)<=10000)){
                JOptionPane.showMessageDialog(this, "Enter an integer in the range of [0, 10000].", "Incorrect Delay Time", JOptionPane.ERROR_MESSAGE);
                newDelayTime = JOptionPane.showInputDialog("Enter time delay in milliseconds.", delayTime);
            }
            delayTime = Integer.parseInt(newDelayTime);
            graphTimer.setDelay(delayTime);
        });
        primsMenuItem.addActionListener((MouseEvent) -> {
            if(!isDrawingGraph && nodeList.size()>1){
                edgeList.clear();
                primsVisited.clear();
                primsQueue.clear();
                isDrawingGraph = true;
                type = AlgorithmType.PRIMS;
                for(int i=0; i<nodeList.size()-1; i++){
                    for(int j=i+1; j<nodeList.size(); j++){
                        Node n1 = nodeList.get(i);
                        Node n2 = nodeList.get(j);
                        Edge e = new Edge(n1, n2);
                        n1.add(e);
                        n2.add(e);
                    }
                }
                Node temp = nodeList.get((int)(Math.random()*nodeList.size()));
                primsQueue.addAll(temp.getEdges());
                primsVisited.add(temp);
                graphTimer.start();
            }
        });
        kruskalsMenuItem.addActionListener((MouseEvent) -> {
            if(!isDrawingGraph && nodeList.size()>1){
                edgeList.clear();
                kruskalsEdgeList.clear();
                isDrawingGraph = true;
                type = AlgorithmType.KRUSKALS;
                for(int i=0; i<nodeList.size()-1; i++){
                    for(int j=i+1; j<nodeList.size(); j++){
                        kruskalsEdgeList.add(new Edge(nodeList.get(i), nodeList.get(j)));
                    }
                }
                uf = new UnionFind(nodeList.size());
                Collections.sort(kruskalsEdgeList);
                graphTimer.start();
            }
        });
        clearMenuItem.addActionListener((ActionEvent) -> {
            clear();
        });
    }
    
    private void initTimer(){
        graphTimer = new Timer(delayTime, (ActionEvent e) -> {
            if(isDrawingGraph){
                drawNextEdge();
            }else{
                isDrawingGraph = false;
                graphTimer.stop();
            }
        });
    }
    
    private void drawNextEdge() {
        if(type == AlgorithmType.PRIMS)
            stepPrims();
        else
            stepKruskals();
        
        drawNodesAndEdges();
    }
    
    private void stepPrims(){
        Edge e = primsQueue.remove();
        while(primsVisited.contains(e.nodeOne) && primsVisited.contains(e.nodeTwo))
            e = primsQueue.remove();
        edgeList.add(e);
        
        if(!primsVisited.contains(e.nodeOne)){
            primsQueue.addAll(e.nodeOne.getEdges());
            primsVisited.add(e.nodeOne);
        }
        if(!primsVisited.contains(e.nodeTwo)){
            primsQueue.addAll(e.nodeTwo.getEdges());
            primsVisited.add(e.nodeTwo);
        }
        
        if(primsVisited.size() == nodeList.size())
            isDrawingGraph = false;
    }
    
    private void stepKruskals(){
        Edge e = kruskalsEdgeList.removeFirst();
        while(uf.connected(e.nodeOne.index, e.nodeTwo.index))
            e = kruskalsEdgeList.removeFirst();
        edgeList.add(e);
        
        uf.union(e.nodeOne.index, e.nodeTwo.index);
        
        if(uf.count() == 1)
            isDrawingGraph = false;
    }
    
    private void addNode(int x, int y){
        if(!isDrawingGraph) {
            Point p = new Point(x, y);
            Node node = new Node(p, nodeList.size());
            nodeList.add(node);
            drawNodesAndEdges();
        }
    }
    
    private void drawNodesAndEdges() {
        Graphics g = graphPanel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, graphPanel.getWidth(), graphPanel.getHeight());
        
        g.setColor(Color.RED);
        for(Edge e : edgeList){
            Point p1 = e.nodeOne.point;
            Point p2 = e.nodeTwo.point;
            g.drawLine(p1.x+2, p1.y+2, p2.x+2, p2.y+2);
        }
        
        g.setColor(Color.BLACK);
        for(Node n : nodeList){
            g.fillRect(n.point.x, n.point.y, 4, 4);
        }
    }
    
    private void clear(){
        isDrawingGraph = false;
        nodeList.clear();
        edgeList.clear();
        primsQueue.clear();
        primsVisited.clear();
        kruskalsEdgeList.clear();
        
        Graphics g = graphPanel.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, graphPanel.getWidth(), graphPanel.getHeight());        
    }
    
    public static void main(String[] args) {
        new MinimumSpanningTreeVisualizer();
    }
    
}
