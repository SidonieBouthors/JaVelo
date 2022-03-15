package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSet;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes=nodes;
        this.sectors=sectors;
        this.edges=edges;
        this.attributeSet=attributeSets;
    }

    /**
     * Returns the JaVelo graph obtained from the files in the given directory
     * @throws IOException if there is an input/output error (ex: expected file does not exist)
     * @param basePath  : path of the directory containing the files to load from
     * @return JaVelo graph obtained from the files
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        Path nodesPath = basePath.resolve("nodes.bin");
        Path sectorPath = basePath.resolve("sectors.bin");
        Path edgesPath = basePath.resolve("edges.bin");
        Path attributesPath = basePath.resolve("attributes.bin");
        Path profileIdsPath = basePath.resolve("profile_ids.bin");
        Path elevationsPath =basePath.resolve("elevations.bin");

        ByteBuffer edgesBuffer;
        IntBuffer nodesBuffer;
        ByteBuffer sectorBuffer;

        List<AttributeSet> attributeSetList = new ArrayList<>();
        LongBuffer attributeSetLong;

        IntBuffer profilesIds;
        ShortBuffer elevations;

        try (FileChannel channel = FileChannel.open(edgesPath)) {
            edgesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        try (FileChannel channel = FileChannel.open(nodesPath)) {
            nodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        try (FileChannel channel = FileChannel.open(sectorPath)) {
            sectorBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        try (FileChannel channel = FileChannel.open(attributesPath)) {
            attributeSetLong = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
            for (int i = 0; i < attributeSetLong.capacity(); i++) {
                attributeSetList.add(new AttributeSet(attributeSetLong.get(i)));
            }
        }
        try (FileChannel channel = FileChannel.open(elevationsPath)) {
            elevations = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();
        }
        try (FileChannel channel = FileChannel.open(profileIdsPath)) {
            profilesIds = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
        }
        return new Graph(new GraphNodes(nodesBuffer),new GraphSectors(sectorBuffer),new GraphEdges(edgesBuffer,profilesIds,elevations),attributeSetList);
    }

    /**
     * Returns the total number of nodes in the graph
     * @return total number of nodes
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * Returns the position of the node of given ID
     * @param nodeId    : ID of the node
     * @return position of the node
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Returns the number of edges originating at the node of given ID
     * @param nodeId    : ID of the node
     * @return number of edges originating at the node
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     *Returns the ID of the edge of given index originating at the node of given ID
     * @param nodeId        : ID of the node
     * @param edgeIndex     : index of the edge
     * @return ID of the edge
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId,edgeIndex);
    }

    /**
     *Returns the ID of the node closest to the given point, within the given search distance, or -1 if no node corresponds to this criteria
     * @param point             : point from which to search
     * @param searchDistance    : search distance around the point (in meters)
     * @return the ID of the closest node, or -1 if there are none within search distance
     */
    public int nodeClosestTo(PointCh point, double searchDistance){
        double squaredShortestDistance = searchDistance;
        int indexWithShortestDistanceFromPoint = -1;
        List<GraphSectors.Sector> sectorsToSearch = sectors.sectorsInArea(point, searchDistance);
        for (GraphSectors.Sector sector : sectorsToSearch) {
            for (int i = sector.startNodeId(); i < sector.endNodeId(); i++) {
                double squaredDistance = Math2.squaredNorm( nodes.nodeE(i) - point.e(), nodes.nodeN(i) - point.n());
                if (squaredDistance < squaredShortestDistance) {
                    squaredShortestDistance = squaredDistance;
                    indexWithShortestDistanceFromPoint = i;
                }
            }
        }
        return indexWithShortestDistanceFromPoint;
    }

    /**
     *Returns the ID of the destination node of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return ID of the destination node of the edge
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * Returns true iff the edge of given ID goes in the opposite direction to the OSM route it is a part of
     * @param edgeId    : ID of the edge
     * @return whether the edge is inverted as compared to it's OSM route
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Returns the set of OSM attributes attached to the edge of given ID
     * @param edgeId
     * @return
     */
    public AttributeSet edgeAttributes(int edgeId) {
        int index = edges.attributesIndex(edgeId);
        return attributeSet.get(index); // CHECK
    }

    /**
     * Returns the length of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return the length (in meters) of the edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Returns the total elevation gain of the edge of given ID
     * @param edgeId    : ID of the edge
     * @return total elevation gain of the edge (in meters)
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     * Returns the profile of the edge of given ID, in the form of a function
     * if the edge does not have a profile, this function returns Double.NaN for all arguments
     * @param edgeId    : ID of the edge
     * @return the profile of the edge, or a constant Double.NaN function if it has none
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if (edges.hasProfile(edgeId)){
             double length = edges.length(edgeId);
            float[] samples = edges.profileSamples(edgeId);
            return Functions.sampled(samples, length);
        }
        else {
            return Functions.constant(Double.NaN);
        }
    }
}
