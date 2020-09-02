package strategies;

import graphs.Graph;
import graphs.Vertex;

import java.awt.*;
import java.util.*;

public class BacktrackingStrategy implements ColoringStrategy {

    private int decisions = 0;
    private int strategyType;
    private Graph graph;

    public BacktrackingStrategy(int strategyType) {
        this.strategyType = strategyType;
    }

    public boolean startBacktrack() {
        boolean coloringFound;
        switch (strategyType) {
            case 1: coloringFound = simpleBacktrack();
                break;
            case 2: coloringFound = forwardChecking();
                break;
            case 3: coloringFound = arcConsistency();
                break;
            default: coloringFound = true;
        }
        return coloringFound;
    }

    private boolean simpleBacktrack() {
        ArrayList<Vertex> unvisited = findUnvisited();
        if(unvisited.isEmpty() && !checkConflict(graph.getVertices())) {
            return true;
        } else {
            if (!checkConflict(graph.getVertices())) {
                Vertex vertex = mostEdge(unvisited);
                vertex = graph.getVertices().get(graph.getVertices().indexOf(vertex));
                for (Color color : vertex.getDomain()) {
                    vertex.setColor(color);
                    decisions++;
                    if (simpleBacktrack()) {
                        return true;
                    }
                }
                vertex.setColor(Color.GRAY); //Remove any coloring and backtrack
                decisions++;
            }
        }
        return false;
    }

    private boolean forwardChecking() {
        ArrayList<Vertex> unvisited = findUnvisited();
        if(unvisited.isEmpty()) {
            return true;
        } else {
            Vertex vertex = mostEdge(unvisited);
            vertex = graph.getVertices().get(graph.getVertices().indexOf(vertex));
            decisions++;
            ArrayList<Vertex> connectedVertices = vertex.getConnected();
            boolean badColor;
            ArrayList<Color> domain = vertex.getDomain();
            for(Color color : domain) {
                badColor = false;
                ArrayList<Vertex> colorRemoved = new ArrayList<>();
                for(Vertex connected : connectedVertices) {
                    //if there is only one color left in the domain and it is the color we are checking then don't use this color
                    //Won't have to worry about already colored vertices because we would have removed that color from current domain
                    if(connected.getDomain().size() == 1 && connected.getDomain().get(0) == color) {
                        for(Vertex reset : colorRemoved) { //Adding color that may have been removed back to domain
                            reset.addColor(color);
                        }
                        badColor = true;
                        decisions++;
                        break;
                    } else {
                        if(connected.getDomain().contains(color)) { //Remove the color
                            connected.removeColor(color);
                            colorRemoved.add(connected);
                            decisions++;
                        }
                    }
                }
                if(!badColor) {
                    vertex.setColor(color);
                    decisions++;
                    if(!forwardChecking()) { //If a solution isn't found then
                        for(Vertex reset : colorRemoved) { //Adding removed colors back to domain
                            reset.addColor(color);
                        }
                    } else {
                        return true;
                    }
                }
            }
            vertex.setColor(Color.GRAY); //Remove color and backtrack
            decisions++;
        }
        return false;
    }

    private boolean arcConsistency() {
        ArrayList<Vertex> unvisited = findUnvisited();
        if(unvisited.isEmpty()) {
            return true;
        } else {
            Vertex vertex = mostEdge(unvisited);
            vertex = graph.getVertices().get(graph.getVertices().indexOf(vertex));
            decisions++;
            ArrayList<Color> domain = vertex.getDomain();
            for(Color color : domain) {
                Map<Vertex, Color> colorRemoved = new HashMap<>();
                vertex.setColor(color);
                decisions++;
                for(Vertex connected : vertex.getConnected()) {
                    if(connected.getDomain().contains(color)) {
                        connected.removeColor(color);
                        colorRemoved.put(connected, color);
                    }
                    decisions++;
                }
                boolean arcConsistent = false, noArcConsistentcy = false;
                while(!arcConsistent) {
                    arcConsistent = true;
                    for(Vertex unvistedVertex : unvisited) {
                        if(unvistedVertex.getDomain().size() == 1) {
                            Color restricted = unvistedVertex.getDomain().get(0);
                            for(Vertex connected : unvistedVertex.getConnected()) {
                                if(connected.getDomain().contains(restricted) && connected.getDomain().size() == 1) {
                                    for(Map.Entry<Vertex, Color> reset : colorRemoved.entrySet()) {
                                        reset.getKey().addColor(reset.getValue());
                                    }
                                    noArcConsistentcy = true;
                                    break;
                                } else if (connected.getDomain().contains(restricted)) {
                                    arcConsistent = false;
                                    connected.removeColor(restricted);
                                    colorRemoved.put(connected, restricted);
                                }
                                decisions++;
                            }
                            if(noArcConsistentcy) {break;}
                        }
                        decisions++;
                    }
                    if(noArcConsistentcy) {break;}
                }
                if(!noArcConsistentcy) {
                    if(arcConsistency()) {
                        return true;
                    } else {
                        for(Map.Entry<Vertex, Color> reset : colorRemoved.entrySet()) {
                            reset.getKey().addColor(reset.getValue());
                        }
                        vertex.setColor(Color.GRAY);
                        decisions++;
                    }
                }
            }
            vertex.setColor(Color.GRAY);
            decisions++;
        }
        return false;
    }

    private Vertex mostEdge(ArrayList<Vertex> vertices) {
        Vertex mostEdges = vertices.get(0);
        for(Vertex vertex : vertices) {
            if(vertex.getConnected().size() > mostEdges.getConnected().size()) {
                mostEdges = vertex;
            }
        }
        return mostEdges;
    }

    private ArrayList<Vertex> findUnvisited() {
        ArrayList<Vertex> unvisited = new ArrayList<>();
        for(Vertex vertex : graph.getVertices()) {
            if(vertex.getColor() == Color.GRAY) {
                unvisited.add(vertex);
            }
        }
        return unvisited;
    }

    private boolean checkConflict(ArrayList<Vertex> vertices) {
        ArrayList<Vertex> connectedVertices;
        for(Vertex vertex : vertices) {
            if(vertex.getColor() != Color.GRAY) {
                connectedVertices = vertex.getConnected();
                for (Vertex connected : connectedVertices) {
                    if (vertex.getColor() == connected.getColor()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean getColoring(Graph graph, int colorCount) {
        this.decisions = 0;
        this.graph = graph;
        return startBacktrack();
    }

    public int getDecisionCount() {
        return this.decisions;
    }
}
