package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class ImportedRoute {

    private final ObjectProperty<Route> routeProperty;
    private final ReadOnlyObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane;
    private final Polyline routeLine;

    /**
     * Builds a RouteManager with the given properties
     * @param routeProperty     : RouteBean associated the route manager
     * @param mapProperty   : Properties of the map (zoom, top left)
     */
    ImportedRoute(ObjectProperty<Route> routeProperty, ReadOnlyObjectProperty<MapViewParameters> mapProperty){
        this.routeProperty = routeProperty;
        this.mapProperty = mapProperty;
        this.pane = new Pane();

        pane.setPickOnBounds(false);

        this.routeLine = new Polyline();

        routeLine.getPoints().setAll(points());
        routeLine.setId("route");
        pane.getChildren().add(routeLine);

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

    /**
     * Returns the list of point coordinates of the route
     * @return list of point coordinates
     */
    private List<Double> points(){
        Route route = routeProperty.get();
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

        routeProperty.addListener(
                (p, oldValue, newValue) -> rebuildRouteLine());

        mapProperty.addListener( (p, oldValue, newValue) -> {
            if (oldValue.zoomLevel() != newValue.zoomLevel()) {
                rebuildRouteLine();
            }
            else {
                repositionRouteLine();
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
    }

    /**
     * Rebuilds the route line with the new route
     */
    private void rebuildRouteLine(){
        Route route = routeProperty.get();
        routeLine.setVisible(route != null);

        if (route != null){
            repositionRouteLine();
            routeLine.getPoints().setAll(points());
        }
    }

}
