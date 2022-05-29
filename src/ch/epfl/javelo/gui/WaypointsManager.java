package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final static int SEARCH_DISTANCE_WAYPOINTS = 1000;
    private final ObservableMap<String, Waypoint> savedWaypoints;


    /**
     * @param roadNetwork Graph of the roadNetwork
     * @param fxProperty property containing map parameters
     * @param waypoints Observable list of waypoints
     * @param errorSignal error signal
     */
    public WaypointsManager(Graph roadNetwork,
                            ObjectProperty<MapViewParameters> fxProperty,
                            ObservableList<Waypoint> waypoints,
                            ObservableMap<String, Waypoint> savedWaypoints,
                            Consumer<String> errorSignal) {

        this.roadNetwork = roadNetwork;
        this.fxProperty = fxProperty;
        this.waypoints = waypoints;
        this.errorSignal = errorSignal;
        this.savedWaypoints = savedWaypoints;
        pane = new Pane();
        pane.setPickOnBounds(false);
        updateWaypoints();

        waypoints.addListener((ListChangeListener<? super Waypoint>) l -> updateWaypoints());
        fxProperty.addListener((p, oldP, newP) -> updateWaypoints());
    }

    /**
     * Redrawing the waypoints on the pane
     */
    private void updateWaypoints() {

        List<Group> waypointGroups = new ArrayList<>();

        for (int i = 0; i < waypoints.size(); i++) {

            //create SVG
            SVGPath firstSVG = new SVGPath();
            SVGPath secondSVG = new SVGPath();
            firstSVG.getStyleClass().add("pin_outside");
            secondSVG.getStyleClass().add("pin_inside");
            firstSVG.setContent(FIRST_SVG_PATH_STRING);
            secondSVG.setContent(SECOND_SVG_PATH_STRING);

            Waypoint waypoint = waypoints.get(i);
            Group waypointGroup = new Group();
            waypointGroup.getStyleClass().add("pin");

            //setting style class first,middle and last waypoints
            if (i==0) {
                waypointGroup.getStyleClass().add("first");
            } else if (i == waypoints.size()-1) {
                waypointGroup.getStyleClass().add("last");
            } else {
                waypointGroup.getStyleClass().add("middle");
            }
            waypointGroup.getChildren().addAll(firstSVG, secondSVG);

            installGroupHandlers(waypoint, waypointGroup, i);
            setGroupCoordinates(waypointGroup, waypoint.position());

            waypointGroups.add(waypointGroup);
        }
        pane.getChildren().setAll(waypointGroups);
    }



    /**
     * Install handlers of the given waypoint group
     * @param waypointGroup     : group
     * @param index             : index of the waypoint
     */
    private void installGroupHandlers(Waypoint waypoint, Group waypointGroup, int index){

        SimpleObjectProperty<Point2D> point = new SimpleObjectProperty<>();

        waypointGroup.setOnMousePressed(pressed -> point.set(new Point2D(pressed.getX(), pressed.getY())));

        waypointGroup.setOnMouseDragged(dragged -> {
            double x = waypointGroup.getLayoutX();
            double y = waypointGroup.getLayoutY();
            waypointGroup.setLayoutX(dragged.getX() + x - point.get().getX());
            waypointGroup.setLayoutY(dragged.getY() + y - point.get().getY());
        });


        waypointGroup.setOnMouseReleased(event -> {
            if (!event.isStillSincePress() && event.getButton() == MouseButton.PRIMARY) {
                addWaypoint(index, waypointGroup.getLayoutX(), waypointGroup.getLayoutY());
                updateWaypoints();
            }
        });

        waypointGroup.setOnMouseClicked(event -> {
            if (event.isStillSincePress()) {

                if (event.getButton() == MouseButton.SECONDARY) {
                    saveWaypointPopup(waypoint);
                }
                if (event.getButton() == MouseButton.PRIMARY) {
                    waypoints.remove(index);
                }

            }

        });
    }

    /**
     * Setting coordinates of the Group at a given Point ch
     * @param wayPointGroup     : group
     * @param position              : position of the group
     */
    private void setGroupCoordinates(Group wayPointGroup, PointCh position) {

        MapViewParameters map = fxProperty.get();
        int zoomLevel = map.zoomLevel();
        PointWebMercator waypointWeb = PointWebMercator.ofPointCh(position);
        double xAtZoomLevel = waypointWeb.xAtZoomLevel(zoomLevel);
        double yAtZoomLevel = waypointWeb.yAtZoomLevel(zoomLevel);

        double realX = xAtZoomLevel - map.x();
        double realY = yAtZoomLevel - map.y();

        wayPointGroup.setLayoutX(realX);
        wayPointGroup.setLayoutY(realY);
    }

    /**
     * Return the pane containing the waypoints
     * @return waypoints pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Add waypoint at a given position
     * @param x     : x coordinate
     * @param y     : y coordinate
     */
    public void addWaypoint(double x, double y) {
        addWaypoint(waypoints.size(), x,y);
    }
    /**
     * Add waypoint at a given position and index in the list
     * @param index : index at which to add waypoint
     * @param x     : x coordinate
     * @param y     : y coordinate
     */
    private void addWaypoint(int index, double x, double y){

        PointCh point = fxProperty.get().pointAt(x,y).toPointCh();
        int nodeId = roadNetwork.nodeClosestTo(point, SEARCH_DISTANCE_WAYPOINTS);

        if (nodeId == -1) {
            errorSignal.accept(NO_ROAD_ERROR_MESSAGE);
        }
        else if (index >= waypoints.size()) {
            waypoints.add(index, new Waypoint(point, nodeId));
        }
        else {
            waypoints.set(index, new Waypoint(point, nodeId));
        }
    }

    private void saveWaypointPopup(Waypoint waypoint){
        if (!(waypoint == null || savedWaypoints.containsValue(waypoint))) {
            Stage saveStage = new Stage();
            saveStage.initModality(Modality.APPLICATION_MODAL);
            GridPane grid = new GridPane();
            grid.getStylesheets().add("save.css");
            grid.setId("grid");
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));
            Button save = new Button("Sauvegarder");
            Button cancel = new Button("Annuler");
            javafx.scene.control.TextField field = new TextField();
            Text errorMessage = new Text("Entrez le nom du point !");
            errorMessage.setVisible(false);
            grid.add(field, 0, 0, 2, 1);
            grid.add(save, 0, 1, 1, 1);
            grid.add(cancel, 1, 1, 1, 1);
            grid.add(errorMessage, 0, 2, 2, 1);
            save.setOnAction(event -> {
                if (!(field.getText().length() == 0 || savedWaypoints.containsValue(waypoint))) {
                    savedWaypoints.put(field.getText(),waypoint);
                    saveStage.close();
                } else {
                    errorMessage.setText("Entrez le nom du point !");
                    errorMessage.setVisible(true);
                }
            });
            cancel.setOnAction(event -> saveStage.close());
            saveStage.setTitle("Point");
            saveStage.getIcons().add(new Image("bicycle.png"));
            saveStage.setScene(new Scene(grid));
            saveStage.show();
        }
    }
}
