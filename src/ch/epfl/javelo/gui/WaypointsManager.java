package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */

public final class WaypointsManager {

    private final static String FIRST_SVG_PATH_STRING = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String SECOND_SVG_PATH_STRING = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String NO_ROAD_ERROR_MESSAGE = "Aucune route à proximité !";

    private final Graph roadNetwork;
    private final ObjectProperty<MapViewParameters> fxProperty;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorSignal;
    private final Pane pane;
    private SVGPath firstSVG;
    private SVGPath secondSVG;

    /**
     * @param roadNetwork Graph of the roadNetwork
     * @param fxProperty property containing map parameters
     * @param waypoints Observable list of waypoints
     * @param errorSignal error signal
     */
    public WaypointsManager(Graph roadNetwork, ObjectProperty<MapViewParameters> fxProperty, ObservableList<Waypoint> waypoints,
                            Consumer<String> errorSignal) {

        this.roadNetwork = roadNetwork;
        this.fxProperty = fxProperty;
        this.waypoints = waypoints;
        this.errorSignal = errorSignal;


        pane = new Pane();
        settingWayPointsTab();
        waypoints.addListener((ListChangeListener<? super Waypoint>) listen -> {
            settingWayPointsTab();
        });
        fxProperty.addListener( listen -> {
            settingWayPointsTab();
        });
    }

    /**
     * Setting and Updating waypoints on the pane
     */
    private void settingWayPointsTab() {
        pane.getChildren().clear();
        for (int i = 0; i < waypoints.size(); i++) {
            int index = i;

            Waypoint waypoint = waypoints.get(i);
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
            } else if (i == waypoints.size()-1) {
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
                System.out.println("mouse dragging");
            });

            wayPointGroup.setOnMouseReleased(event -> {
                if (!event.isStillSincePress()) {
                    addWaypoint(index, wayPointGroup.getLayoutX(), wayPointGroup.getLayoutY());
                    settingWayPointsTab();
                }
            });

            wayPointGroup.setOnMouseClicked(event -> {
                if (event.isStillSincePress()) {
                    waypoints.remove(index);
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

    /**
     * setting coordinates of the Group at a given Point ch
     * @param wayPointGroup
     * @param node position to set the group
     */
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
     * return the pane containing the waypoints
     *
     * @return
     */
    public Pane pane() {
        return pane;
    }

    /**
     * add waypoint at a given position x,y
     * @param x
     * @param y
     */
    public void addWaypoint(double x, double y) {
        addWaypoint(waypoints.size(), x,y);
    }
    private void addWaypoint(int index, double x, double y){

        PointCh point = fxProperty.get().pointAt(x,y).toPointCh();

        int nodeId = roadNetwork.nodeClosestTo(point, 1000);

        if (nodeId == -1) {
            errorSignal.accept(NO_ROAD_ERROR_MESSAGE);
        }
        else {
            if (index >= waypoints.size()) {
                waypoints.add(index, new Waypoint(point, nodeId));
            }
            else {
                waypoints.set(index, new Waypoint(point, nodeId));
            }
        }
    }
}
