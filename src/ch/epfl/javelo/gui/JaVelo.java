package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
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
        TileManager standardTileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer computer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(computer);

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        //TILE MANAGER OPTIONS
        Path cyclingCacheBasePath = Path.of("cycling-cache");
        String cyclingTileServerHost = "tile.waymarkedtrails.org/cycling";
        TileManager overlayTileManager =
                new TileManager(cyclingCacheBasePath, cyclingTileServerHost);

        Path cyclOSMCacheBasePath = Path.of("cyclosm-cache");
        String cyclOSMTileServerHost = "a.tile-cyclosm.openstreetmap.fr/cyclosm";
        TileManager cyclOSMTileManager =
                new TileManager(cyclOSMCacheBasePath, cyclOSMTileServerHost);

        Path watercolorCacheBasePath = Path.of("watercolor-cache");
        String watercolorTileServerHost = "stamen-tiles.a.ssl.fastly.net/watercolor";
        TileManager watercolorTileManager =
                new TileManager(watercolorCacheBasePath, watercolorTileServerHost);


        ObjectProperty<TileManager> tileManagerProperty = new SimpleObjectProperty<>(standardTileManager);
        ObjectProperty<TileManager> overlayTileManagerProperty = new SimpleObjectProperty<>();
        ObservableList<ImportedRoute> importedRoutes = FXCollections.observableArrayList();

        //OPTIONS
        MenuItem clearWaypoints = new MenuItem("Supprimer les Points");
        clearWaypoints.setOnAction(event -> routeBean.getWaypoints().clear());

        MenuItem invertRoute = new MenuItem("Inverser la Route");
        invertRoute.setOnAction(event -> Collections.reverse(routeBean.getWaypoints()));

        CheckMenuItem overlayCyclingRoutes = new CheckMenuItem("Afficher Pistes Cyclables");
        overlayTileManagerProperty.bind(Bindings.createObjectBinding(
                () -> overlayCyclingRoutes.isSelected() ? overlayTileManager : null,
                overlayCyclingRoutes.selectedProperty()));

        //SAVED WAYPOINTS
        Menu menuSavedWaypoints = new Menu("Points Sauvegardés");
        menuSavedWaypoints.disableProperty().bind(Bindings.createBooleanBinding(
                () -> routeBean.getSavedWaypoints().isEmpty(),
                routeBean.getSavedWaypoints()
        ));

        routeBean.getSavedWaypoints().addListener((MapChangeListener<? super String, ? super Waypoint>)
            l -> {
            ObservableMap<String,Waypoint> savedWaypoints = routeBean.getSavedWaypoints();

            menuSavedWaypoints.getItems().clear();

            for (String key : savedWaypoints.keySet()){

                MenuItem waypointMenu = new MenuItem();

                waypointMenu.setOnAction(event-> {
                    routeBean.getWaypoints().add(savedWaypoints.get(key));
                });

                waypointMenu.setText(key);

                menuSavedWaypoints.getItems().add(waypointMenu);
            }
        });

        //CHANGE BASE MAP
        ToggleGroup baseMap = new ToggleGroup();
        RadioMenuItem setStandardBaseMap = new RadioMenuItem("Carte Standard");
        setStandardBaseMap.setToggleGroup(baseMap);
        setStandardBaseMap.setOnAction(event -> tileManagerProperty.set(standardTileManager));
        RadioMenuItem setCyclosmBaseMap = new RadioMenuItem("Carte CyclOSM");
        setCyclosmBaseMap.setToggleGroup(baseMap);
        setCyclosmBaseMap.setOnAction(event -> tileManagerProperty.set(cyclOSMTileManager));
        RadioMenuItem setWatercolorBaseMap = new RadioMenuItem("Carte Watercolor");
        setWatercolorBaseMap.setToggleGroup(baseMap);
        setWatercolorBaseMap.setOnAction(event -> tileManagerProperty.set(watercolorTileManager));

        setStandardBaseMap.setSelected(true);

        //IMPORT / EXPORT GPX
        MenuItem importOption = new MenuItem("Importer GPX");
        MenuItem exportOption = new MenuItem("Exporter GPX");
        exportOption.disableProperty().bind(
                routeBean.getRouteProperty().isNull());
        exportOption.setOnAction(event -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx",
                        routeBean.getRouteProperty().get(),
                        routeBean.getElevationProfile().get());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        Menu menuFichier = new Menu("Fichier");
        Menu menuFondCarte = new Menu("Fond de Carte");
        Menu menuOptions = new Menu("Itinéraire");
        Menu menuSaved = new Menu("Elements");
        Menu menuImportedRoutes = new Menu("GPX Importés");
        menuImportedRoutes.disableProperty().bind(Bindings.createBooleanBinding(
                importedRoutes::isEmpty,
                importedRoutes));
        MenuBar menuBar = new MenuBar(menuFichier, menuFondCarte, menuOptions, menuSaved);

        menuSaved.getItems().add(menuImportedRoutes);
        menuSaved.getItems().add(menuSavedWaypoints);
        menuFichier.getItems().add(exportOption);
        menuFichier.getItems().add(importOption);
        menuOptions.getItems().add(clearWaypoints);
        menuOptions.getItems().add(invertRoute);
        menuOptions.getItems().add(overlayCyclingRoutes);
        menuFondCarte.getItems().add(setStandardBaseMap);
        menuFondCarte.getItems().add(setCyclosmBaseMap);
        menuFondCarte.getItems().add(setWatercolorBaseMap);


        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManagerProperty,
                routeBean, errorConsumer, overlayTileManagerProperty);
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

          }
          else if (oldE == null){
              mainPane.getItems().add(1, elevationProfileManager.pane());
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

        importOption.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select GPX File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("GPX File (*.gpx)", "*.gpx"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    Route route = GpxReader.convertGpxToRoute(file, graph);
                    if (route != null) {
                        ImportedRoute importedRoute = new ImportedRoute(route,
                                mapManager.mapViewParametersProperty(),
                                file.getName());
                        importedRoutes.add(importedRoute);
                        mapPane.getChildren().add(importedRoute.pane());
                    } else {
                        errorManager.displayError("Invalid GPX File ! ");
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });

        importedRoutes.addListener((ListChangeListener<? super ImportedRoute>)
            l -> {
                menuImportedRoutes.getItems().clear();
                for (ImportedRoute importedRoute : importedRoutes){

                    CheckMenuItem routeMenu = new CheckMenuItem();
                    routeMenu.setText(importedRoute.getName());
                    routeMenu.setSelected(importedRoute.visibleProperty().get());
                    importedRoute.visibleProperty().bind(routeMenu.selectedProperty());
                    menuImportedRoutes.getItems().add(routeMenu);
                }
        });

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.getIcons().add(new Image("bicycle.png"));
        primaryStage.setScene(new Scene(paneWithMenu));
        primaryStage.show();
    }
}
