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

import java.util.Arrays;
import java.util.function.Consumer;

public final class WaypointsManager {

    private final static String firstSVGPathString = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String secondSVGPathString = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final String errorMessageNoRoad = "Aucune route à proximité !";

    private Graph roadNetwork;
    private ObjectProperty<MapViewParameters> fxProperty;
    private ObservableList<Waypoint> wayPoints;
    private Consumer<String> wayPointGroup;
    private Pane pane;
    private SVGPath firstSVG;
    private SVGPath secondSVG;
    private double lastMousePositionX, lastMousePositionY;

    /**
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
        this.wayPointGroup = errorSignal;


        pane = new Pane();
        settingWayPointsTab();
        wayPoints.addListener((ListChangeListener<? super Waypoint>) listen -> {
            settingWayPointsTab();
        });
        fxProperty.addListener( listen -> {
            settingWayPointsTab();
        });




    }

    private void settingWayPointsTab() {
        pane.getChildren().clear();
        for (int i = 0; i < wayPoints.size(); i++) {
            int index = i;

            Waypoint waypoint = wayPoints.get(i);
            //Creating waypoint group

            //Setting coordinates


            //setting style class first,middle and last waypoints
            //adding children the group
            Group wayPointGroup = new Group();
            wayPointGroup.getStyleClass().add("pin");
            firstSVG = new SVGPath();
            secondSVG = new SVGPath();

            if (i==0) {
                wayPointGroup.getStyleClass().add("first");
            } else if (i == wayPoints.size()-1) {
                wayPointGroup.getStyleClass().add("last");
            } else {
                wayPointGroup.getStyleClass().add("middle");
            }


            //setting style for SVGPATH
            firstSVG.getStyleClass().add("pin_outside");
            secondSVG.getStyleClass().add("pin_inside");

            //set content of SVG Path
            firstSVG.setContent(FIRST_SVG_PATH_STRING);
            secondSVG.setContent(SECOND_SVG_PATH_STRING);
            wayPointGroup.getChildren().addAll(firstSVG, secondSVG);

            SimpleObjectProperty<Point2D> point = new SimpleObjectProperty<>();
            wayPointGroup.setOnMousePressed(pressed -> {
                Point2D pt = new Point2D(pressed.getX(), pressed.getY());
                point.set(pt);
            });

            wayPointGroup.setOnMouseDragged(dragged -> {
                double x = wayPointGroup.getLayoutX();
                double y = wayPointGroup.getLayoutY();
                wayPointGroup.setLayoutX(dragged.getX() + x - point.get().getX());
                wayPointGroup.setLayoutY(dragged.getY() + y - point.get().getY());
            });

            wayPointGroup.setOnMouseReleased(event -> {
                if (!event.isStillSincePress()) {
                    addWaypoint(waypoint,wayPointGroup.getLayoutX(), wayPointGroup.getLayoutY());
                    settingWayPointsTab();
                }
            });

            wayPointGroup.setOnMouseClicked(event -> {
                if (event.isStillSincePress()) {
                    wayPoints.remove(index);
                }
            });

            //creating pointWebMercator for x and y coordinates
            ;
            settingCoordinates(wayPointGroup, waypoint.position());


            //Avoiding events of the pins create problem with map events.
            pane.setPickOnBounds(false);
            pane.getChildren().add(wayPointGroup);

        }
    }




    private void settingCoordinates(Group wayPointGroup, PointCh node) {

        MapViewParameters map = fxProperty.get();
        int zoomLevel = map.zoomLevel();
        PointWebMercator waypointWeb = PointWebMercator.ofPointCh(node);
        double xAtZoomLevel = waypointWeb.xAtZoomLevel(zoomLevel);
        double yAtZoomLevel = waypointWeb.yAtZoomLevel(zoomLevel);
        // finding parameters of the top left map
        double topLeftX = map.topLeft().getX();
        double topLeftY = map.topLeft().getY();

        double realX = xAtZoomLevel - topLeftX;
        double realY = yAtZoomLevel - topLeftY;


        wayPointGroup.setLayoutX(realX);
        wayPointGroup.setLayoutY(realY);

    }

    /**
     * retourne le panneau contenant les points de passage
     *
     * @return
     */
    public Pane pane() {
        return pane;
    }

    public void addWaypoint(double x, double y) {


        PointCh a = fxProperty.get().pointAt(x,y).toPointCh();

        int nodeId = roadNetwork.nodeClosestTo(a, 1000);
        if (nodeId == -1) {
            wayPointGroup.accept(NO_ROAD_ERROR_MESSAGE);
        } else {
            PointCh node = roadNetwork.nodePoint(nodeId);
            wayPoints.add(new Waypoint(node, nodeId));
        }
    }
    private void addWaypoint(Waypoint waypoint, double x, double y){
        int oldSize = wayPoints.size();
        addWaypoint(x,y);

        if (wayPoints.size() != oldSize) {
            int index = wayPoints.indexOf(waypoint);

            wayPoints.set(index,wayPoints.get(wayPoints.size()-1));
            wayPoints.remove(wayPoints.size()-1);

        }
    }
}
