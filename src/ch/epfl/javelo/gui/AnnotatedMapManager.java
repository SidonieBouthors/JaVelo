package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final StackPane mainPane;
    private final ObjectProperty<Point2D> currentMousePosition;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final static int MAX_CURSOR_ROUTE_DISTANCE = 15;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer){
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        this.mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints = routeBean.getWaypoints();
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        this.currentMousePosition = new SimpleObjectProperty<>();

        this.routeBean = routeBean;
        WaypointsManager waypointsManager = new WaypointsManager(graph,
                mapViewParametersP,
                waypoints,
                errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager,
                waypointsManager,
                mapViewParametersP);
        RouteManager routeManager = new RouteManager(routeBean,
                mapViewParametersP);

        this.mainPane =
                new StackPane(baseMapManager.pane(),
                        routeManager.pane(),
                        waypointsManager.pane());
        mainPane.getStylesheets().add("map.css");

        installBindings();
        installHandlers();
    }

    public StackPane pane(){
        return mainPane;
    }

    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;

    }

    private void installBindings(){
        mousePositionOnRouteProperty.bind(Bindings.createObjectBinding(() -> {
            Route route = routeBean.getRouteProperty().get();
            if (route == null || currentMousePosition.get() == null) {
                return Double.NaN;
            }
            int zoomLevel = mapViewParametersP.get().zoomLevel();
            double topLeftX = mapViewParametersP.get().x();
            double topLeftY = mapViewParametersP.get().y();

            PointWebMercator mousePosition = PointWebMercator.of(
                    zoomLevel,
                    topLeftX + currentMousePosition.get().getX(),
                    topLeftY + currentMousePosition.get().getY());
            RoutePoint closestPoint = route.pointClosestTo(mousePosition.toPointCh());
            PointWebMercator point = PointWebMercator.ofPointCh(closestPoint.point());

            double distance = Math2.squaredNorm(
                    mapViewParametersP.get().viewX(mousePosition) - mapViewParametersP.get().viewX(point),
                    mapViewParametersP.get().viewY(mousePosition) - mapViewParametersP.get().viewY(point));

            if (distance <= MAX_CURSOR_ROUTE_DISTANCE){
                return closestPoint.position();
            }
            else {
                return Double.NaN;
            }



        },routeBean.getRouteProperty(),currentMousePosition));
    }
    private void installHandlers(){
        mainPane.setOnMouseMoved(event -> {
            currentMousePosition.set(new Point2D(event.getX(), event.getY()));
        });
        mainPane.setOnMouseExited(event -> {
            currentMousePosition.set(null);
        });
    }
}
