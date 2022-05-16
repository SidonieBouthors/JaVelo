package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.geom.Point2D;
import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final Pane mainPane;
    private Point2D currentMousePosition;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> errorConsumer){
        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints = routeBean.getWaypoints();

        this.waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                        errorConsumer);
        this.baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);
        this.routeManager =
                new RouteManager(routeBean,
                        mapViewParametersP);

        this.mainPane =
                new StackPane(baseMapManager.pane(),
                        waypointsManager.pane(),
                        routeManager.pane());
        mainPane.getStylesheets().add("map.css");

    }

    public Pane pane(){
        return mainPane;
    }

    public DoubleProperty mousePositionOneRouteProperty(){
        return new SimpleDoubleProperty();
    }
}
