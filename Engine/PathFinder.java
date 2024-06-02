package Engine;

import Engine.TileHandler;
import Engine.Tile.Tile;

import java.util.*;
import MathLib.*;
/**
 * The PathFinder class is responsible for finding a path between two positions on a grid.
 * @author joey
 */
public class PathFinder {

    private final int MAX_ITERATIONS = 1000;

    /**
     * Represents a node in the pathfinding algorithm.
     */
    public class Node implements Comparable<Node> {
        public Vector2 position;
        public Node parent;
        public double g;
        public double h;
        public double f;

        /**
         * Constructs a new Node object.
         *
         * @param position The position of the node.
         * @param parent   The parent node.
         * @param g        The cost from the start node to this node.
         * @param h        The estimated cost from this node to the end node.
         */
        public Node(Vector2 position, Node parent, double g, double h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return Objects.equals(position, node.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }

    TileHandler handler;

    /**
     * Constructs a new PathFinder object.
     *
     * @param handler The TileHandler object used for accessing tiles.
     */
    public PathFinder(TileHandler handler) {
        this.handler = handler;
    }

    /**
     * Checks if the given position is out of bounds.
     *
     * @param pos The position to check.
     * @return True if the position is out of bounds, false otherwise.
     */

    // public boolean outOfBounds(Vector2 pos){
    //     Tile tile = handler.getTile((int)pos.X, (int)pos.Y);
    //     return tile == null || tile instanceof VoidTile || tile.CanCollide;
    // }

    /**
     * Finds a path between the given start and end positions.
     *
     * @param startPosition The start position.
     * @param endPosition   The end position.
     * @return A list of positions representing the path, or null if no path is found. 
     */
    public List<Vector2> findPath(Vector2 startPosition, Vector2 endPosition) {
        // if ( outOfBounds(endPosition)) {
        //     return null;
        // }
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        HashMap<Vector2, Node> allNodes = new HashMap<>();

        startPosition = startPosition.floor();
        endPosition = endPosition.floor();
        Node startNode = new Node(startPosition, null, 0, startPosition.distanceTo(endPosition));
        openSet.add(startNode);
        allNodes.put(startPosition, startNode);

        int iterations = 0;

        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            Node currentNode = openSet.poll();

            if (currentNode.position.equals(endPosition)) {
                return reconstructPath(currentNode);
            }

            Vector2[] neighbors = getNeighbors(currentNode.position);

            for (Vector2 neighborPos : neighbors) {
                Tile tile = handler.getTile((int) neighborPos.X, (int) neighborPos.Y);
                if (tile == null || !tile.CanCollide) {
                    double gCost = currentNode.g + 1;
                    Node neighborNode = allNodes.getOrDefault(neighborPos,
                            new Node(neighborPos, null, Double.MAX_VALUE, neighborPos.distanceTo(endPosition)));

                    if (gCost < neighborNode.g) {
                        neighborNode.g = gCost;
                        neighborNode.f = gCost + neighborNode.h;
                        neighborNode.parent = currentNode;

                        if (!openSet.contains(neighborNode)) {
                            openSet.add(neighborNode);
                            allNodes.put(neighborPos, neighborNode);
                        }
                    }
                }
            }

            iterations++;
        }

        return null;
    }

    private List<Vector2> reconstructPath(Node endNode) {
        List<Vector2> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(current.position);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private Vector2[] getNeighbors(Vector2 position) {
        return new Vector2[]{
                new Vector2(position.X + 1, position.Y),
                new Vector2(position.X, position.Y - 1),
                new Vector2(position.X - 1, position.Y),
                new Vector2(position.X, position.Y + 1)
        };
    }
}
