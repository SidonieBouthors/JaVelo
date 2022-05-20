package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Stack;
import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final StackPane mainPane;
    private ObjectProperty<Point2D> currentMousePosition;
    private DoubleProperty mousePositionOnRouteProperty;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer){
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        this.mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints = routeBean.getWaypoints();
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty();
        this.currentMousePosition = new SimpleObjectProperty<>();

        this.routeBean = routeBean;
        this.waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                        errorConsumer);
        this.baseMapManager =
                new BaseMapManager(tileManager,
                        this.waypointsManager,
                        mapViewParametersP);
        this.routeManager =
                new RouteManager(routeBean,
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
            System.out.println("salut");
            Route route = routeBean.getRouteProperty().get();
            if (route == null || currentMousePosition.get() == null) {
                return Double.NaN;
            }
            int zoomLevel = mapViewParametersP.get().zoomLevel();
            double topLeftX = mapViewParametersP.get().x();
            double topLeftY = mapViewParametersP.get().y();

            PointCh mousePosition = PointWebMercator.of(
                    zoomLevel,
                    topLeftX + currentMousePosition.get().getX(),
                    topLeftY + currentMousePosition.get().getY()).toPointCh();
            RoutePoint closestPoint = route.pointClosestTo(mousePosition);

            if (closestPoint.distanceToReference() <= 50){
                System.out.println(closestPoint.position());
                return closestPoint.position();
            }
            else {
                System.out.println(Double.NaN);
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
