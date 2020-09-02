package strategies;

import graphs.Graph;
import graphs.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class LocalSearchStrategy implements ColoringStrategy {

    private final ArrayList<Color> COLORS = new ArrayList<>(Arrays.asList(Color.GREEN, Color.ORANGE, Color.BLUE, Color.RED));
    private final Random RANDOM = new Random();
    private final double INITIAL_TEMPERATURE = 10000;
    private final int ANNEALING_COUNT = 1000;
    private final double TAU = 1;

    private int decisions = 0;
    private String climbingMethod;

    public LocalSearchStrategy(String climbingMethod) {
        if(climbingMethod == null) {
            this.climbingMethod = "simulated annealing";
        } else if(!climbingMethod.equals("simulated annealing") && !climbingMethod.equals("genetic selection")) {
            this.climbingMethod = "simulated annealing";
        } else {
            this.climbingMethod = climbingMethod;
        }
    }

    public boolean getColoring(Graph graph, int colorCount) {
        this.decisions = 0;
        Graph coloredGraph = generateColoring(graph, colorCount);
        boolean result;
        if(this.climbingMethod.equals("simulated annealing")) {
            result = simulatedAnnealing(coloredGraph, colorCount);
        } else {
            result = geneticSelection(graph, colorCount, 10);
        }
        return result;
    }

    private boolean simulatedAnnealing(Graph graph, int colorCount) {
        Graph currentGraph = graph;
        Graph newGraph = graph;
        double temperature = INITIAL_TEMPERATURE;
        for(int i = 1; i < ANNEALING_COUNT; i++) {
            if (temperature <= 0) {
                return false;
            }
            int index = RANDOM.nextInt(newGraph.getVertices().size());
            newGraph = newGraph.assignColorAtIndex(index, COLORS.get(RANDOM.nextInt(colorCount)));
            decisions++;
            int conflicts = getConflictCount(currentGraph);
            int newConflicts = getConflictCount(newGraph);
            if(newConflicts < conflicts) {
                currentGraph = newGraph;
                if(newConflicts == 0) {
                    return true;
                }
            } else {
                double roll = RANDOM.nextDouble();
                if(roll <= Math.exp((double)(newConflicts - conflicts) / temperature)) {
                    currentGraph = newGraph;
                } else {
                    newGraph = currentGraph;
                }
                decisions++;
            }
            temperature = schedule(i);
        }
        return false;
    }

    private double schedule(int iteration) {
        return ((INITIAL_TEMPERATURE * TAU)) / (TAU + iteration);
    }

    private boolean geneticSelection(Graph graph, int colorCount, int populationSize) {
        double temperature = INITIAL_TEMPERATURE;
        ArrayList<ArrayList<Vertex>> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            ArrayList<Vertex> chromosome = graph.getVertices();
            for (int j = 0; j < graph.getVertices().size(); j++) {
                chromosome.get(j).setColor(COLORS.get(RANDOM.nextInt(colorCount)));
            }
            population.add(chromosome);
            decisions++;
        }

        for (int iteration = 1; iteration < ANNEALING_COUNT; iteration++){
            for (ArrayList<Vertex> chromosome : population) {
                graph.setVertices(chromosome);
                decisions++;
                if (getConflictCount(graph) == 0) {
                    return true;
                }
            }

            ArrayList<ArrayList<Vertex>> newPopulation = new ArrayList<>();
            for (int i = 0; i < population.size() / 2; i++) {
                ArrayList<Vertex> parent1 = selectParent(graph, population, temperature);
                ArrayList<Vertex> parent2 = selectParent(graph, population, temperature);
                ArrayList<ArrayList<Vertex>> children = crossover(parent1, parent2);
                newPopulation.add(mutate(children.get(0), colorCount));
                newPopulation.add(mutate(children.get(1), colorCount));
                decisions += 5;
            }

            population = newPopulation;
            temperature = schedule(iteration);
        }
        return false;
    }

    private ArrayList<Vertex> selectParent(Graph graph, ArrayList<ArrayList<Vertex>> population, double temperature) {
        for (int i = 0; i < 100; i++) {
            for (ArrayList<Vertex> chromosome : population) {
                double roll = RANDOM.nextDouble();
                graph.setVertices(chromosome);
                if (roll <= Math.exp(-getConflictCount(graph) / temperature)) {
                    return chromosome;
                }
            }
        }
        return population.get(RANDOM.nextInt(population.size()));
    }

    private ArrayList<ArrayList<Vertex>> crossover(ArrayList<Vertex> parent1, ArrayList<Vertex> parent2) {
        ArrayList<Vertex> child1 = new ArrayList<>();
        ArrayList<Vertex> child2 = new ArrayList<>();
        for (int i = 0; i < parent1.size(); i++) {
            int select = RANDOM.nextInt(2);
            if (select == 0) {
                child1.add(parent1.get(i));
                child2.add(parent2.get(i));
            } else {
                child1.add(parent2.get(i));
                child2.add(parent1.get(i));
            }
        }
        return new ArrayList<>(Arrays.asList(child1, child2));
    }

    private ArrayList<Vertex> mutate(ArrayList<Vertex> chromosome, int colorCount) {
        for (int i = 0; i < chromosome.size(); i++) {
            double roll = RANDOM.nextDouble();
            if (roll <= 0.01) {
                Vertex vertex = chromosome.get(i);
                vertex.setColor(COLORS.get(RANDOM.nextInt(colorCount)));
                chromosome.set(i, vertex);
            }
        }
        return chromosome;
    }

    private Graph generateColoring(Graph graph, int colorCount) {
        for(Vertex vertex: graph.getVertices()) {
           vertex.setColor(COLORS.get(RANDOM.nextInt(colorCount)));
        }
        return graph;
    }

    private int getConflictCount(Graph graph) {
        int conflictCount = 0;
        ArrayList<Vertex> checked = new ArrayList<>();

        for(Vertex vertex : graph.getVertices()) {
            Color color = vertex.getColor();

            for(Vertex neighbor : vertex.getConnected()) {
                if (!checked.contains(neighbor) && color == neighbor.getColor()) {
                    conflictCount++;
                }
                checked.add(vertex);
            }
        }
        return conflictCount;
    }

    public int getDecisionCount() {
        return this.decisions;
    }
}
