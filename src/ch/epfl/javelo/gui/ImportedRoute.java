package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ImportedRoute {

    private final Route route;
    private final ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane = new Pane();
    private final Polyline routeLine = new Polyline();
    private final BooleanProperty visibleProperty = new SimpleBooleanProperty(true);
    private final String name;
    private final static String CSS_MAP = "map.css";
    private final static String FIRST_SVG_PATH_STRING = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String SECOND_SVG_PATH_STRING = "M0-23A1 1 0 000-29 1 1 0 000-23";

    private final Group waypoints;
    /**
     * Builds a RouteManager with the given properties
     * @param route     : RouteBean associated the route manager
     * @param mapProperty   : Properties of the map (zoom, top left)
     */
    ImportedRoute(Route route, ReadOnlyObjectProperty<MapViewParameters> mapProperty, String name){
        this.route = route;
        this.mapProperty = mapProperty;
        this.name = name;
        pane.setPickOnBounds(false);
        routeLine.getPoints().setAll(points());
        routeLine.setId("imported-route");
        pane.getChildren().add(routeLine);
        pane.getStylesheets().add(CSS_MAP);
        waypoints = new Group();
        waypoints.getChildren().add(fixedWaypoint("first"));
        waypoints.getChildren().add(fixedWaypoint("last"));
        pane.getChildren().add(waypoints);
        rebuildRouteLine();
        installListeners();
    }

    /**
     * Returns the pane containing the route and highlight point
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }

    public BooleanProperty visibleProperty(){ return visibleProperty; }
    public String getName(){ return name; }

    /**
     * Returns the list of point coordinates of the route
     * @return list of point coordinates
     */
    private List<Double> points(){
        if (route == null) return new ArrayList<>();
        List<PointCh> pointsCh = route.points();
        List<Double> points = new ArrayList<>();
        MapViewParameters params = mapProperty.get();

        for (PointCh pointCh:pointsCh){
            PointWebMercator point = PointWebMercator.ofPointCh(pointCh);
            points.add(point.xAtZoomLevel(params.zoomLevel()));
            points.add(point.yAtZoomLevel(params.zoomLevel()));
        }
        return points;
    }


    /**
     * Installs the listeners
     */
    private void installListeners(){

        mapProperty.addListener( (p, oldValue, newValue) -> {
            if (oldValue.zoomLevel() != newValue.zoomLevel()) {
                rebuildRouteLine();
            }
            else {
                repositionRouteLine();
            }
        });

        visibleProperty.addListener((p, oV, nV) -> {
            if (nV && !oV){
                pane.getChildren().add(routeLine);
                pane.getChildren().add(waypoints);
            } else if (!nV && oV) {
                pane.getChildren().remove(routeLine);
                pane.getChildren().remove(waypoints);
            }
        });
    }

    /**
     * Repositions the route line according to the map position
     */
    private void repositionRouteLine(){
        MapViewParameters params = mapProperty.get();
        routeLine.setLayoutX( - params.x());
        routeLine.setLayoutY( - params.y());
        waypoints.getChildren().clear();
        waypoints.getChildren().add(fixedWaypoint("first"));
        waypoints.getChildren().add(fixedWaypoint("last"));
    }

    /**
     * Rebuilds the route line with the new route
     */
    private void rebuildRouteLine(){
        routeLine.setVisible(route != null);
        if (route != null){
            repositionRouteLine();
            routeLine.getPoints().setAll(points());
        }
    }

    private Group fixedWaypoint(String style){
        SVGPath firstSVG = new SVGPath();
        SVGPath secondSVG = new SVGPath();
        firstSVG.getStyleClass().add("pin_outside");
        secondSVG.getStyleClass().add("pin_inside");
        firstSVG.setContent(FIRST_SVG_PATH_STRING);
        secondSVG.setContent(SECOND_SVG_PATH_STRING);
        Group waypointGroup = new Group();
        waypointGroup.getStyleClass().add("fixed-pin");
        waypointGroup.getStyleClass().add(style);
        waypointGroup.getChildren().addAll(firstSVG, secondSVG);

        int zoomLevel = mapProperty.get().zoomLevel();

        double position = 0;
        if (style.equals("last")){
            position = route.length();
        }
        PointWebMercator waypointWeb = PointWebMercator.ofPointCh(route.pointAt(position));

        double realX = waypointWeb.xAtZoomLevel(zoomLevel) - mapProperty.get().x();
        double realY = waypointWeb.yAtZoomLevel(zoomLevel) - mapProperty.get().y();
        waypointGroup.setLayoutX(realX);
        waypointGroup.setLayoutY(realY);
        return waypointGroup;
    }

}
