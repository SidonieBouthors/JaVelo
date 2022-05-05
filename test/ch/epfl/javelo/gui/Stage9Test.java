package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class Stage9Test extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer computer = new RouteComputer(graph, costFunction);
        System.out.println(computer);
        RouteBean routeBean = new RouteBean(computer);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints = routeBean.getWaypoints();
        waypoints.addAll(new Waypoint(new PointCh(2532697, 1152350), 159049),
                         new Waypoint(new PointCh(2538659, 1154350), 117669));
        Consumer<String> errorConsumer = new ErrorConsumer();

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        waypoints,
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);
        RouteManager routeManager =
                new RouteManager( routeBean,
                        mapViewParametersP,
                        errorConsumer);

        StackPane mainPane =
                //new StackPane(baseMapManager.pane());
                new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) {
            System.out.println(s);
        }
    }
}