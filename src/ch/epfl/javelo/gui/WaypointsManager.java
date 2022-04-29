package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
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
    private SVGPath firstSVG;
    private SVGPath secondSVG;





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





        for (int i =0; i<wayPoints.size();i++) {

            Waypoint waypoint = wayPoints.get(i);
            //Creating waypoint group
            Group waypointGroup = settingGroupsParameters();
            //Setting coordinates
            settingCoordinates(waypointGroup,waypoint.position());

            System.out.println("Initial point "+waypoint.position());
            //setting style class first,middle and last waypoints
            if (i == 0) {
                waypointGroup.getStyleClass().add("first");
            } else if (i == wayPoints.size() - 1) {
                waypointGroup.getStyleClass().add("last");
            } else {
                waypointGroup.getStyleClass().add("middle");
            }
            //adding children the group

            //creating pointWebMercator for x and y coordinates

            System.out.println("first svg:"+waypointGroup.getChildren().get(0));
            System.out.println("second svg"+waypointGroup.getChildren().get(1));
            //Avoiding events of the pins create problem with map events.
            pane.setPickOnBounds(false);
            pane.getChildren().add(waypointGroup);

        }

        for (int i = 0; i < pane.getChildren().size(); i++) {
            System.out.println(i+": children x :"+pane.getChildren().get(i).getLayoutX());
            System.out.println(i+": children y :"+pane.getChildren().get(i).getLayoutY());

        }

    }

    private Group settingGroupsParameters() {
        Group wayPointGroup = new Group();
        wayPointGroup.getStyleClass().add("pin");
        firstSVG = new SVGPath();
        secondSVG = new SVGPath();


        //setting style for SVGPATH
        firstSVG.getStyleClass().add("pin_outside");
        secondSVG.getStyleClass().add("pin_inside");



        //set content of SVG Path
        firstSVG.setContent(firstSVGPathString);
        secondSVG.setContent(secondSVGPathString);
        wayPointGroup.getChildren().addAll(firstSVG, secondSVG);

        wayPointGroup.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {



                double mouseX = event.getX();
                double mouseY = event.getY();

                wayPointGroup.setLayoutX(mouseX);
                wayPointGroup.setLayoutY(mouseY);


            }
        });
        wayPointGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pane.getChildren().remove(wayPointGroup);

            }
        });
        return wayPointGroup;
    }

    private void settingCoordinates(Group wayPointGroup, PointCh node) {

        MapViewParameters map = fxProperty.get();
        int zoomLevel = map.zoomLevel();
        PointWebMercator waypointWeb = PointWebMercator.ofPointCh(node);
        double xAtZoomLevel = waypointWeb.xAtZoomLevel(zoomLevel);
        double yAtZoomLevel = waypointWeb.yAtZoomLevel(zoomLevel);
        // finding parameters of the top left map
        double topLeftX= map.topLeft().getX();
        double topLeftY = map.topLeft().getY();

        double realX = xAtZoomLevel-topLeftX;
        double realY = yAtZoomLevel - topLeftY;

        wayPointGroup.setLayoutX(realX);
        wayPointGroup.setLayoutY(realY);
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
            Group wayPointGroup = settingGroupsParameters();
            settingCoordinates(wayPointGroup,node);
            wayPointGroup.getStyleClass().add("middle");
            wayPointGroup.getChildren().addAll(firstSVG, secondSVG);
            pane.getChildren().add(wayPointGroup);

        }
    }
}
