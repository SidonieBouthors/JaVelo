package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.SingleRoute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 **/
public class GpxReader {

    private static final int OPENTAG_LENGTH = 2;
    private static final int SEARCH_DISTANCE = 100;
    private GpxReader(){}

    /**
     * Returns the GPX document corresponding to the given route and profile
     * @return document corresponding to the route
     */
    public static Route convertGpxToRoute(File file, Graph graph) throws IOException{

        BufferedReader buffer = new BufferedReader(new FileReader(file));

        //check correctness??
        //String name = readTagContent(readTagContent(gpx, "metadata"), "name");

        //String rte = readTagContent(gpx, "rte");
        //if (rte == null) { return new SingleRoute(new ArrayList<>()); } // no points in gpx file

        List<Edge> edgeList = new ArrayList<>();
        List<Integer> nodeList = new ArrayList<>();
        buffer.lines().forEach(line -> {
            String latProp = readTagProperty(line, "rtept", "lat");
            String lonProp = readTagProperty(line, "rtept", "lon");

            if (latProp != null && lonProp != null) {
                System.out.println(latProp + " " + lonProp);
                double lat = Math.toRadians(Double.parseDouble(latProp));
                double lon = Math.toRadians(Double.parseDouble(lonProp));
                PointCh point = new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat));
                int nodeId = graph.nodeClosestTo(point, SEARCH_DISTANCE);
                if (nodeId != -1) {
                    nodeList.add(nodeId);
                }
            }
        });

        int fromNodeId;
        int toNodeId;
        for (int i = 0; i < nodeList.size()-1; i++) {
            fromNodeId = nodeList.get(i);
            toNodeId = nodeList.get(i+1);
            for (int j = 0; j < graph.nodeOutDegree(fromNodeId); j++){
                int edgeId = graph.nodeOutEdgeId(fromNodeId, j);
                if (graph.edgeTargetNodeId(edgeId) == toNodeId){
                    edgeList.add(Edge.of(graph, edgeId, fromNodeId, toNodeId));
                }
            }

        }
        if(edgeList.isEmpty()) { return null; }
        return new SingleRoute(edgeList);
    }

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

    private static String readTagContent(String text, String tag) {
        if (text == null) {
            text = "";
        }
        text = text.replaceAll("\\s", ""); // remove blank spaces

        int tagOpen = text.indexOf("<" + tag);
        if (tagOpen == -1) { return null; }
        int tagStart = text.substring(tagOpen).indexOf(">");
        if (tagStart == -1 ) { return null; }
        tagStart += 1;
        int tagEnd = text.indexOf("</" + tag + ">", tagStart);
        if (tagEnd == -1) { return null; }

        return text.substring(tagStart, tagEnd);
    }

    private static String readTagProperty(String text, String tag, String property){
        if(text == null){text = "";}
        text = text.replaceAll("\\s", ""); // remove blank spaces

        System.out.println(text);
        String tagProps;
        if (text.contains("<"+ tag)){
            System.out.println("tag found " + tag);
            int tagOpen = text.indexOf("<" + tag)+ tag.length() + 1;
            int tagClose = text.indexOf( ">", tagOpen);
            tagProps =  text.substring(tagOpen , tagClose);
        } else {
            tagProps = "";
        }

        if (tagProps.contains(property)){
            System.out.println("property found " + property);
            int propOpen = text.indexOf(property + "=\"")+ property.length() + 2;
            int propClose = text.indexOf("\"", propOpen );
            return text.substring(propOpen, propClose);
        } else {
            return null;
        }
    }
}
