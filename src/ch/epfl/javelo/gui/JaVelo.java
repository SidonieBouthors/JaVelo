package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
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

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                errorManager.displayError(s);
            }
        };

        DoubleProperty positionAlongTheProfile = new SimpleDoubleProperty();

        MenuItem exportOption = new MenuItem("Exporter GPX");
        exportOption.disableProperty().set(
                routeBean.getRouteProperty().get() != null);//itineraire non nul
        exportOption.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",
                        routeBean.getRouteProperty().get(),
                        x -> routeBean.getElevationProfile().get().elevationAt(x));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        Menu menu = new Menu("Fichier");
        menu.getItems().add(exportOption);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);
        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.getElevationProfile(), routeBean.highlightedPositionProperty());
        StackPane mapPane = new StackPane(mapManager.pane(), errorManager.pane(), menuBar);
        SplitPane mainPane = new SplitPane(mapPane);//, elevationProfileManager.pane()
        mainPane.orientationProperty().set(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

        BorderPane paneWithMenu = new BorderPane();
        paneWithMenu.setTop(menuBar);
        paneWithMenu.setCenter(mainPane);

       routeBean.getElevationProfile().addListener((p, oldValue, newValue) -> {
          if (newValue == null){
             mainPane.getItems().setAll(mapPane);
          }
          else {
              mainPane.getItems().setAll(mapPane, elevationProfileManager.pane());
          }
       });

        mainPane.setMaxHeight(600);
        mainPane.setMaxWidth(800);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(paneWithMenu));
        primaryStage.show();
    }
}
