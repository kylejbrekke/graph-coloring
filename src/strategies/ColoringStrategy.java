package strategies;

import graphs.Graph;

public interface ColoringStrategy {
    boolean getColoring(Graph graph, int colorCount);
    int getDecisionCount();
}
