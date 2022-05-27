package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Javelo
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class JaVelo extends Application {

    private final static int STAGE_MIN_WIDTH = 800;
    private final static int STAGE_MIN_HEIGHT = 600;

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
        Consumer<String> errorConsumer = errorManager::displayError;

        MenuItem exportOption = new MenuItem("Exporter GPX");
        exportOption.disableProperty().bind(
                routeBean.getRouteProperty().isNull());
        exportOption.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",
                        routeBean.getRouteProperty().get(),
                        routeBean.getElevationProfileProperty().get());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        Menu menu = new Menu("Fichier");
        menu.getItems().add(exportOption);
        MenuBar menuBar = new MenuBar(menu);

        AnnotatedMapManager mapManager =
                new AnnotatedMapManager(graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager elevationProfileManager =
                new ElevationProfileManager(routeBean.getElevationProfileProperty(),
                                            routeBean.getHighlightedPositionProperty());
        StackPane mapPane = new StackPane(mapManager.pane(),errorManager.pane());
        SplitPane mainPane = new SplitPane(mapPane);
        mainPane.orientationProperty().set(Orientation.VERTICAL);

        BorderPane paneWithMenu = new BorderPane();
        paneWithMenu.setTop(menuBar);
        paneWithMenu.setCenter(mainPane);


       routeBean.getRouteProperty().addListener((p, oldE, newE) -> {
          if (newE == null) {
              mainPane.getItems().remove(elevationProfileManager.pane());
          }
          else if (oldE == null) {
              mainPane.getItems().add(1, elevationProfileManager.pane());
              SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);
          }
       });

       routeBean.getHighlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {
           if (Double.isNaN(elevationProfileManager.mousePositionOnProfileProperty().get())) {
               return mapManager.mousePositionOnRouteProperty().get();
           }
           else {
               return elevationProfileManager.mousePositionOnProfileProperty().get();
           }
       }, mapManager.mousePositionOnRouteProperty(),
            elevationProfileManager.mousePositionOnProfileProperty()));


        primaryStage.setMinWidth(STAGE_MIN_WIDTH);
        primaryStage.setMinHeight(STAGE_MIN_HEIGHT);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(paneWithMenu));
        primaryStage.show();
    }
}
