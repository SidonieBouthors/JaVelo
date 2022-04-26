package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
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

    private Graph roadNetwork;
    private ObjectProperty<MapViewParameters>fxProperty;
    ObservableList<Waypoint> wayPoints;
    Consumer<String> errorSignal;

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
    }

    /**
     *  retourne le panneau contenant les points de passage
     * @return
     */
    public Pane pane() {
        //Creating components
        Pane pane = new Pane();
        Group waypoint = new Group();
        SVGPath firstSVG = new SVGPath();
        SVGPath secondSVG = new SVGPath();

        //setting style for SVGPATH
        firstSVG.getStyleClass().add("pin_outside");
        secondSVG.getStyleClass().add("pin_inside");

        //setting children
        waypoint.getChildren().addAll(firstSVG, secondSVG);

        //set content of SVG Path
        firstSVG.setContent(firstSVGPathString);
        secondSVG.setContent(secondSVGPathString);

        for (int i = 0; i<wayPoints.size(); i++) {

        }







    return null;
    }
}
