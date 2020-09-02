package graphs;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Vertex {

    private double xLoc;
    private double yLoc;
    private Color color;
    private ArrayList<Color> domain;
    private Graph graph;
    private ArrayList<Vertex> connectedVertices = new ArrayList<>();

    public Vertex(Graph graph) {
        Random rand = new Random();
        this.xLoc = rand.nextDouble();
        this.yLoc = rand.nextDouble();
        this.color = new Color(128, 128, 128);
        this.color = Color.GRAY;
        this.domain = new ArrayList<>();
        domain.add(Color.BLUE);
        domain.add(Color.RED);
        domain.add(Color.GREEN);
        domain.add(Color.ORANGE);
        this.graph = graph;

    }

    public double getXLoc() {
        return xLoc;
    }

    public double getYLoc() {
        return yLoc;
    }

    public Color getColor() {
        return color;
    }

    public void removeColor(Color color) {domain.removeIf(n -> n == color);}

    public void addColor(Color color) {domain.add(color);}

    public void addConnection(Vertex vertex) {connectedVertices.add(vertex);}

    public ArrayList<Vertex> getConnected() {return connectedVertices;}

    public ArrayList<Color> getDomain() {
        return domain;
    }

    public void setColor(Color color) {
        this.color = color;
        graph.repaint();
    }

    public void setDomain(ArrayList<Color> domain) {
        this.domain = domain;
    }
}
