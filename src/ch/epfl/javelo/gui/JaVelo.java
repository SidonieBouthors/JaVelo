package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer computer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(computer);

        /*
        routeBean.getWaypoints().addAll(FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152500), 159049),
                new Waypoint(new PointCh(2538659, 1154350), 117669)));
        */

        //BONUS
        Path cyclingCacheBasePath = Path.of("cycling-cache");
        String cyclingTileServerHost = "tile.waymarkedtrails.org/cycling";
        TileManager cyclingTileManager =
                new TileManager(cyclingCacheBasePath, cyclingTileServerHost);
        //

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;



        //bonus
        MenuItem clearWaypoints = new MenuItem("Clear All Waypoints");
        clearWaypoints.setOnAction(event -> routeBean.getWaypoints().clear());
        MenuItem invertRoute = new MenuItem("Invert Route");
        invertRoute.setOnAction(event -> Collections.reverse(routeBean.getWaypoints()));
        //


        MenuItem exportOption = new MenuItem("Exporter GPX");
        exportOption.disableProperty().set(
                routeBean.getRouteProperty().get() != null);//itineraire non nul
        exportOption.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",
                        routeBean.getRouteProperty().get(),
                        routeBean.getElevationProfile().get());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        Menu menu = new Menu("Fichier");
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        // bonus
        menu.getItems().add(clearWaypoints);
        menu.getItems().add(invertRoute);
        //
        //BONUS
        MenuItem overlayCyclingRoutes = new MenuItem("Overlay Cycling Routes");
        //overlayCyclingRoutes.setOnAction(event -> );
        //


        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.getElevationProfile(),
                                            routeBean.highlightedPositionProperty());
        StackPane mapPane = new StackPane(mapManager.pane(),errorManager.pane());
        SplitPane mainPane = new SplitPane(mapPane);
        mainPane.orientationProperty().set(Orientation.VERTICAL);

        BorderPane paneWithMenu = new BorderPane();
        paneWithMenu.setTop(menuBar);
        paneWithMenu.setCenter(mainPane);




       routeBean.getRouteProperty().addListener((p, oldE, newE) -> {
          if (newE == null){
              mainPane.getItems().remove(elevationProfileManager.pane());
              menu.getItems().remove(exportOption);

          }
          else if (oldE == null){
              mainPane.getItems().add(1, elevationProfileManager.pane());
              menu.getItems().add(exportOption);
              SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
          }
       });

       routeBean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {
           if (Double.isNaN(elevationProfileManager.mousePositionOnProfileProperty().get())) {
               return mapManager.mousePositionOnRouteProperty().get();
           }
           else {
               return elevationProfileManager.mousePositionOnProfileProperty().get();
           }
       }, mapManager.mousePositionOnRouteProperty(), elevationProfileManager.mousePositionOnProfileProperty()));


        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(paneWithMenu));
        primaryStage.show();
    }
}
