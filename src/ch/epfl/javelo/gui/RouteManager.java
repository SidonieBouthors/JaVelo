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
    private final static double CIRCLE_RADIUS = 5d;

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

        this.routeLine = new Polyline();

        routeLine.getPoints().setAll(points());
        routeLine.setId("route");
        pane.getChildren().add(routeLine);

        highlightDisc = new Circle(CIRCLE_RADIUS);
        highlightDisc.setId("highlight");
        pane.getChildren().add(highlightDisc);
        rebuildRouteLine();
        installHandlers();
        installListeners();

        highlightDisc.visibleProperty().bind(
                routeBean.getHighlightedPositionProperty().greaterThanOrEqualTo(0));
        routeLine.visibleProperty().bind(routeBean.getElevationProfileProperty().isNotNull());
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

    /**
     * Installs the handlers
     */
    private void installHandlers() {
        highlightDisc.setOnMouseClicked( event -> {
            MapViewParameters params = mapProperty.get();
            Route route = routeBean.getRouteProperty().get();

            Point2D point = highlightDisc.localToParent(event.getX(), event.getY());
            PointWebMercator pointWebMercator = params.pointAt(point.getX(), point.getY());
            PointCh pointCh = pointWebMercator.toPointCh();
            double position = routeBean.getHighlightedPosition();

            ObservableList<Waypoint> waypoints = routeBean.getWaypoints();
            Waypoint newWaypoint = new Waypoint(pointCh, route.nodeClosestTo(position));
            waypoints.add(routeBean.indexOfNonEmptySegmentAt(position) + 1, newWaypoint);
        });
    }

    /**
     * Installs the listeners
     */
    private void installListeners(){

        routeBean.getHighlightedPositionProperty().addListener(
                (p, oldValue, newValue) -> repositionHighlightCircle());

        routeBean.getRouteProperty().addListener(
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
        repositionHighlightCircle();
    }

    /**
     * Rebuilds the route line with the new route
     */
    private void rebuildRouteLine(){
        Route route = routeBean.getRouteProperty().get();

        if (route != null){
            repositionRouteLine();
            routeLine.getPoints().setAll(points());
        }
        repositionHighlightCircle();
    }

    /**
     * Repositions the highlight circle on the route line
     */
    private void repositionHighlightCircle(){

        Route route = routeBean.getRouteProperty().get();
        MapViewParameters params = mapProperty.get();

        if (route != null && (!routeBean.getHighlightedPosition().isNaN())) {
            PointWebMercator highlightPoint =
                    PointWebMercator.ofPointCh(
                        routeBean.getRouteProperty().get().pointAt(
                            routeBean.getHighlightedPosition()));

            highlightDisc.setLayoutX(params.viewX(highlightPoint));
            highlightDisc.setLayoutY(params.viewY(highlightPoint));
        }
    }
}
