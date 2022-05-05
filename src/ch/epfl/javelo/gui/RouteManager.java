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

public final class RouteManager {

    private static final String ERROR_POINT_PRESENT = "Un point de passage est déjà present à cet endroit !";
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final Consumer<String> errorSignal;
    private final Pane pane;
    private final Polyline routeLine;
    private final Circle highlightDisc;

    RouteManager (RouteBean routeBean, ObjectProperty<MapViewParameters> mapProperty, Consumer<String> errorSignal){
        this.routeBean = routeBean;
        this.mapProperty = mapProperty;
        this.errorSignal = errorSignal;
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
                routeBean.routeProperty().get().pointAt(routeBean.highlightedPosition()));

        this.highlightDisc = new Circle(5);

        highlightDisc.setId("highlight");
        pane.getChildren().add(highlightDisc);

        installHandlers();
        installListeners();
        repositionRouteLine();
    }

    public Pane pane(){
        return pane;
    }

    private List<Double> points(){
        Route route = routeBean.routeProperty().get();
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


    private void installHandlers() {
        highlightDisc.setOnMouseClicked( event -> {
            MapViewParameters params = mapProperty.get();
            Route route = routeBean.routeProperty().get();

            Point2D point = highlightDisc.localToParent(event.getX(), event.getY());
            PointWebMercator pointWebMercator = params.pointAt(point.getX(), point.getY());
            PointCh pointCh = pointWebMercator.toPointCh();
            double position = routeBean.highlightedPosition();

            ObservableList<Waypoint> waypoints = routeBean.getWaypoints();

            for (Waypoint waypoint:waypoints){
                if (route.nodeClosestTo(position) == waypoint.closestNodeId()){
                    errorSignal.accept(ERROR_POINT_PRESENT);
                    return;
                }
            }
            Waypoint newWaypoint = new Waypoint(pointCh, route.nodeClosestTo(position));
            waypoints.add(route.indexOfSegmentAt(position)+1, newWaypoint);

        });
    }

    private void installListeners(){
        routeBean.highlightedPositionProperty().addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            repositionHighlightCircle();
        });
        routeBean.routeProperty().addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            //reconstruire totalement et/ou rendre (in)visible la polyligne
            rebuildRouteLine();
        });
        mapProperty.addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            // SI LE ZOOM CHANGE
            //reconstruire totalement et/ou rendre (in)visible la polyligne
            if (oldValue.zoomLevel() != newValue.zoomLevel()) {
                rebuildRouteLine();
            }
            // SI LA CARTE EST GLISSE MAIS LE ZOOM NE CHANGE PAS
            // repositionner - sans la reconstruire - la polyligne représentant l'itinéraire
            else {
                repositionRouteLine();
            }
        });
    }

    private void repositionRouteLine(){
        MapViewParameters params = mapProperty.get();
        routeLine.setLayoutX( - params.topLeft().getX());
        routeLine.setLayoutY( - params.topLeft().getY());
        repositionHighlightCircle();
    }

    private void rebuildRouteLine(){
        Route route = routeBean.routeProperty().get();
        routeLine.setVisible(route != null);
        if (route != null){
            repositionRouteLine();
            routeLine.getPoints().setAll(points());
        }
        repositionHighlightCircle();
    }

    private void repositionHighlightCircle(){
        Route route = routeBean.routeProperty().get();
        MapViewParameters params = mapProperty.get();
        highlightDisc.setVisible(route != null);
        System.out.println(highlightDisc.isVisible());
        if (route != null) {
            PointWebMercator highlightPoint =
                    PointWebMercator.ofPointCh(
                        routeBean.routeProperty().get().pointAt(
                            routeBean.highlightedPosition()));
            highlightDisc.setLayoutX(params.viewX(highlightPoint));
            highlightDisc.setLayoutY(params.viewY(highlightPoint));
            System.out.println(highlightDisc.getLayoutX() + "  " + highlightDisc.getLayoutY());
        }
    }
}
