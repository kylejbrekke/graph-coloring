package graphs;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Graph extends Canvas {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();

    private static final int VERTEX_RADIUS = 7;
    private static final int EDGE_WIDTH = 2;
    private static final int GRAPH_SCALE = 1000;

    public Graph(int vertexCount) {
        super();
        for (int i = 0; i < vertexCount; i++) {
            Vertex newVertex = new Vertex(this);
            for (Vertex vertex : this.vertices) {
                Edge newEdge = new Edge(newVertex, vertex);
                boolean intersect = false;
                for (Edge edge : this.edges) {
                    if (newEdge.intersects(edge)) {
                        intersect = true;
                        break;
                    }
                }
                if(!intersect) {
                    this.edges.add(newEdge);
                }
            }
            this.vertices.add(newVertex);
        }
        for(Edge edge : edges){
            edge.getEndA().addConnection(edge.getEndB());
            edge.getEndB().addConnection(edge.getEndA());
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void drawGraph(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(EDGE_WIDTH));
        for (Edge edge : this.edges) {
            graphics.draw(new Line2D.Double(edge.getEndA().getXLoc() * GRAPH_SCALE, edge.getEndA().getYLoc() * GRAPH_SCALE,
                                            edge.getEndB().getXLoc() * GRAPH_SCALE, edge.getEndB().getYLoc() * GRAPH_SCALE));
        }
        for (Vertex vertex : this.vertices) {
            graphics.setPaint(new Color(0, 0, 0));
            Shape circle = new Ellipse2D.Double((vertex.getXLoc() * GRAPH_SCALE) - VERTEX_RADIUS,
                                                (vertex.getYLoc() * GRAPH_SCALE) - VERTEX_RADIUS,
                                                2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);
            graphics.draw(circle);
            graphics.setPaint(vertex.getColor());
            graphics.fill(circle);
        }
    }

    public Graph assignColorAtIndex(int index, Color color) {
        Vertex newVertex = this.vertices.get(index);
        newVertex.setColor(color);
        this.vertices.set(index, newVertex);
        return this;
    }

    public void paint(Graphics graphics) {
        drawGraph((Graphics2D)graphics);
    }
}
