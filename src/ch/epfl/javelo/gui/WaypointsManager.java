package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

public final class WaypointsManager {

    private final static String firstSVGPathString = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String secondSVGPathString = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final String errorMessageNoRoad = "Aucune route à proximité !";

    private Graph roadNetwork;
    private ObjectProperty<MapViewParameters>fxProperty;
    private ObservableList<Waypoint> wayPoints;
    private Consumer<String> errorSignal;
    private Pane pane;

    /**
     *
     * @param roadNetwork
     * @param fxProperty
     * @param wayPoints
     * @param errorSignal
     */
    public WaypointsManager(Graph roadNetwork, ObjectProperty<MapViewParameters> fxProperty,
                            ObservableList<Waypoint> wayPoints,
                            Consumer<String> errorSignal) {

        this.roadNetwork = roadNetwork;
        this.fxProperty = fxProperty;
        this.wayPoints = wayPoints;
        this.errorSignal = errorSignal;

        pane = new Pane();

        SVGPath firstSVG = new SVGPath();
        SVGPath secondSVG = new SVGPath();

        //setting style for SVGPATH
        firstSVG.getStyleClass().add("pin_outside");
        secondSVG.getStyleClass().add("pin_inside");


        //set content of SVG Path
        firstSVG.setContent(firstSVGPathString);
        secondSVG.setContent(secondSVGPathString);

        MapViewParameters map = fxProperty.get();
        int zoomLevel = map.zoomLevel();


        for (int i =0; i<wayPoints.size();i++) {

            Waypoint waypoint = wayPoints.get(i);
            //initialize each group at the beggining of the boucle t'as capté chacal ?
            Group waypointGroup = new Group();
            //setting style class first,middle and last waypoints
            if (i == 0) {
                waypointGroup.getStyleClass().add("first");
            } else if (i == wayPoints.size() - 1) {
                waypointGroup.getStyleClass().add("last");
            } else{
                waypointGroup.getStyleClass().add("middle");
            }
            //adding children the group
            waypointGroup.getChildren().addAll(firstSVG, secondSVG);
            //creating pointWebMercator for x and y coordinates
            PointWebMercator waypointWeb = PointWebMercator.ofPointCh(waypoint.position());
            double xAtZoomLevel = waypointWeb.xAtZoomLevel(zoomLevel);
            double yAtZoomLevel = waypointWeb.yAtZoomLevel(zoomLevel);
            // finding parameters of the top left map
            double topLeftX= map.topLeft().getX();
            double topLeftY = map.topLeft().getY();

            double realX = xAtZoomLevel-topLeftX;
            double realY = yAtZoomLevel - topLeftY;
            //setting x and y of the group
            waypointGroup.setLayoutX(realX);
            waypointGroup.setLayoutX(realY);
            //setting pane children
            pane.getChildren().add(waypointGroup);
        }

    }

    /**
     *  retourne le panneau contenant les points de passage
     * @return
     */
    public Pane pane() {return pane;}

    public void addWaypoint(int x, int y) {
        double lon = WebMercator.lon(x);
        double lat = WebMercator.lat(y);
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);
        int nodeId = roadNetwork.nodeClosestTo(new PointCh(e, n), 1000);
        if ( nodeId == -1) {
            errorSignal.accept(errorMessageNoRoad);
        } else {
            PointCh node = roadNetwork.nodePoint(nodeId);

        }
    }
}
