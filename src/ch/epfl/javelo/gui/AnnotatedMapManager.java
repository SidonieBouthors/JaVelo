package ch.epfl.javelo.gui;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

/**
 * AnnotatedMapManager
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class AnnotatedMapManager {

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final StackPane mainPane;
    private final ObjectProperty<Point2D> currentMousePosition;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final static int MAX_CURSOR_ROUTE_DISTANCE = 15;
    private final static int INIT_ZOOM = 12;
    private final static int INIT_X = 543200;
    private final static int INIT_Y = 370650;
    private final static String CSS_MAP = "map.css";
    /**
     * Construct a full AnnotedMap
     * @param graph : graph
     * @param tileManager : tileManager
     * @param routeBean : routeBean
     * @param errorConsumer : errorConsumer
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean,
                               Consumer<String> errorConsumer, TileManager overlayTileManager, BooleanProperty drawOverlay){
        MapViewParameters mapViewParameters =
                new MapViewParameters(INIT_ZOOM, INIT_X,INIT_Y);
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
                overlayTileManager,
                drawOverlay,
                waypointsManager,
                mapViewParametersP);
        RouteManager routeManager = new RouteManager(routeBean,
                mapViewParametersP);

        this.mainPane =
                new StackPane(baseMapManager.pane(),
                        routeManager.pane(),
                        waypointsManager.pane());
        mainPane.getStylesheets().add(CSS_MAP);

        installBindings();
        installHandlers();
    }

    /**
     *
     * @return the pane
     */
    public StackPane pane(){
        return mainPane;
    }

    /**
     *
     * @return mousePosition
     */
    public DoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

    private void installBindings(){
        mousePositionOnRouteProperty.bind(Bindings.createObjectBinding(() -> {
            Route route = routeBean.getRouteProperty().get();
            if (route == null || currentMousePosition.get() == null) {
                return Double.NaN;
            }

            PointWebMercator mousePosition = PointWebMercator.of(
                    mapViewParametersP.get().zoomLevel(),
                    mapViewParametersP.get().x() + currentMousePosition.get().getX(),
                    mapViewParametersP.get().y() + currentMousePosition.get().getY());

            if (mousePosition.toPointCh() == null){
                return Double.NaN;
            }
            RoutePoint closestPoint = route.pointClosestTo(mousePosition.toPointCh());
            PointWebMercator point = PointWebMercator.ofPointCh(closestPoint.point());

            double distance = Math2.norm(
                    currentMousePosition.get().getX() - mapViewParametersP.get().viewX(point),
                    currentMousePosition.get().getY() - mapViewParametersP.get().viewY(point));

            /*
            mapViewParametersP.get().viewX(mousePosition) - mapViewParametersP.get().viewX(point),
            mapViewParametersP.get().viewY(mousePosition) - mapViewParametersP.get().viewY(point));
             */
            if (distance <= MAX_CURSOR_ROUTE_DISTANCE){
                return closestPoint.position();
            }
            else {
                return Double.NaN;
            }
        },routeBean.getRouteProperty(), currentMousePosition));
    }

    private void installHandlers(){
        mainPane.setOnMouseMoved(event ->
            currentMousePosition.set(new Point2D(event.getX(), event.getY()))
        );
        mainPane.setOnMouseExited(event ->
            currentMousePosition.set(null)
        );
    }
}
