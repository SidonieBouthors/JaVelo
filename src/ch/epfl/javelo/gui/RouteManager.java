package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class RouteManager {

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final Pane pane;
    private final Polyline routeLine;
    private final Circle highlightDisc;

    /**
     * Builds a RouteManager with the given properties
     * @param routeBean     : RouteBean associated the route manager
     * @param mapProperty   : Properties of the map (zoom, top left)
     */
    RouteManager (RouteBean routeBean, ObjectProperty<MapViewParameters> mapProperty){
        this.routeBean = routeBean;
        this.mapProperty = mapProperty;
        this.pane = new Pane();

        pane.setPickOnBounds(false);
        MapViewParameters params = mapProperty.get();

        this.routeLine = new Polyline();
        routeLine.setLayoutX( - params.topLeft().getX());
        routeLine.setLayoutY( - params.topLeft().getY());
        routeLine.getPoints().setAll(points());
        routeLine.setId("route");
        pane.getChildren().add(routeLine);

        PointWebMercator highlightPoint = PointWebMercator.ofPointCh(
                routeBean.getRouteProperty().get().pointAt(routeBean.highlightedPosition()));

        this.highlightDisc = new Circle(5);
        highlightDisc.setId("highlight");
        pane.getChildren().add(highlightDisc);

        repositionRouteLine();
        installHandlers();
        installListeners();
    }

    /**
     * Returns the pane containing the route and highlight point
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }

    // Returns the list of point coordinates of the route
    private List<Double> points(){
        Route route = routeBean.getRouteProperty().get();
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

    // installs the event handlers
    private void installHandlers() {
        highlightDisc.setOnMouseClicked( event -> {
            MapViewParameters params = mapProperty.get();
            Route route = routeBean.getRouteProperty().get();

            Point2D point = highlightDisc.localToParent(event.getX(), event.getY());
            PointWebMercator pointWebMercator = params.pointAt(point.getX(), point.getY());
            PointCh pointCh = pointWebMercator.toPointCh();
            double position = routeBean.highlightedPosition();

            ObservableList<Waypoint> waypoints = routeBean.getWaypoints();

            /*
            for (Waypoint waypoint:waypoints){
                if (route.nodeClosestTo(position) == waypoint.nodeId()){
                    errorSignal.accept(ERROR_POINT_PRESENT);
                    return;
                }
            }*/
            Waypoint newWaypoint = new Waypoint(pointCh, route.nodeClosestTo(position));
            waypoints.add(routeBean.indexOfNonEmptySegmentAt(position) + 1, newWaypoint);
        });
    }

    //installs the listeners
    private void installListeners(){
        routeBean.highlightedPositionProperty().addListener( (p, oldValue, newValue) -> {
            repositionHighlightCircle();
        });
        routeBean.getRouteProperty().addListener( (p, oldValue, newValue) -> {
            rebuildRouteLine();
        });
        mapProperty.addListener( (p, oldValue, newValue) -> {
            if (oldValue.zoomLevel() != newValue.zoomLevel()) {
                rebuildRouteLine();
            }
            else {
                repositionRouteLine();
            }
        });
    }

    // repositions the route line according to the map
    private void repositionRouteLine(){
        MapViewParameters params = mapProperty.get();

        routeLine.setLayoutX( - params.topLeft().getX());
        routeLine.setLayoutY( - params.topLeft().getY());

        repositionHighlightCircle();
    }

    // rebuilds the route line with the new route
    private void rebuildRouteLine(){
        Route route = routeBean.getRouteProperty().get();
        routeLine.setVisible(route != null);

        if (route != null){
            repositionRouteLine();
            routeLine.getPoints().setAll(points());
        }
        repositionHighlightCircle();
    }

    // repositions the highlight circle on the route line
    private void repositionHighlightCircle(){

        Route route = routeBean.getRouteProperty().get();
        MapViewParameters params = mapProperty.get();
        highlightDisc.setVisible(route != null);

        if (route != null) {
            PointWebMercator highlightPoint =
                    PointWebMercator.ofPointCh(
                        routeBean.getRouteProperty().get().pointAt(
                            routeBean.highlightedPosition()));

            highlightDisc.setLayoutX(params.viewX(highlightPoint));
            highlightDisc.setLayoutY(params.viewY(highlightPoint));
        }
    }
}
