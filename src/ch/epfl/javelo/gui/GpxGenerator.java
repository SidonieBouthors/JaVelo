package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 **/
public class GpxGenerator {

    private GpxGenerator(){}

    /**
     * Returns the GPX document corresponding to the given route and profile
     * @param route     : route
     * @param profile   : profile of the route
     * @return document corresponding to the route
     */
    public static Document createGpx(Route route, ElevationProfile profile){
        Document doc = newDocument(); // voir plus bas

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        int position = 0;

        for (Edge edge : route.edges()) {
            PointCh point = edge.fromPoint();

            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat", String.format(Locale.ENGLISH, "%.5f",Math.toDegrees(point.lat())));
            rtept.setAttribute("lon", String.format(Locale.ENGLISH,"%.5f",Math.toDegrees(point.lon())));

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.format(Locale.ENGLISH, "%.2f", profile.elevationAt(position)));

            position += edge.length();
        }
        return doc;
    }

    /**
     * Writes the GPX document corresponding to the given route and profile
     * @param fileName      : file name
     * @param route     : route
     * @param profile   : profile of the route
     * @throws IOException in case of an input/output error
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route, profile);
        Writer w = Files.newBufferedWriter(Path.of(fileName));

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        }
        catch (TransformerException e) {
            throw new Error(e); //Should never happen
        }
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
}
