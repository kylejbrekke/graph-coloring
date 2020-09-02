package graphs;

public class Edge {
    private Vertex endA;
    private Vertex endB;

    public Edge(Vertex endA, Vertex endB) {
        this.endA = endA;
        this.endB = endB;
    }

    public boolean intersects(Edge edge) {
        double x1 = this.endA.getXLoc();
        double x2 = this.endB.getXLoc();
        double x3 = edge.getEndA().getXLoc();
        double x4 = edge.getEndB().getXLoc();

        double y1 = this.endA.getYLoc();
        double y2 = this.endB.getYLoc();
        double y3 = edge.getEndA().getYLoc();
        double y4 = edge.getEndB().getYLoc();

        double numeratorA = ((y3 - y4) * (x1 - x3)) + ((x4 - x3) * (y1 - y3));
        double numeratorB = ((y1 - y2) * (x1 - x3)) + ((x2 - x1) * (y1 - y3));
        double denominator = ((x4 - x3) * (y1 - y2)) - ((x1 - x2) * (y4 - y3));

        if (denominator != 0) {
            double offsetA = numeratorA / denominator;
            double offsetB = numeratorB / denominator;
            return offsetA > 0 && offsetA < 0.999999 && offsetB > 0 && offsetB < 0.999999;
        }
        return false;
    }

    public Vertex getEndA() {
        return endA;
    }

    public Vertex getEndB() {
        return endB;
    }
}
