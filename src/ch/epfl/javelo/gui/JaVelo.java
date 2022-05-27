package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        /*
        routeBean.getWaypoints().addAll(FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152500), 159049),
                new Waypoint(new PointCh(2538659, 1154350), 117669)));
        */

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        //BONUS
        Path cyclingCacheBasePath = Path.of("cycling-cache");
        String cyclingTileServerHost = "tile.waymarkedtrails.org/cycling";
        TileManager overlayTileManager =
                new TileManager(cyclingCacheBasePath, cyclingTileServerHost);
        BooleanProperty drawOverlay = new SimpleBooleanProperty(false);

        Path cyclOSMCacheBasePath = Path.of("cyclosm-cache");
        String cyclOSMTileServerHost = "a.tile-cyclosm.openstreetmap.fr/cyclosm";
        TileManager cyclOSMTileManager =
                new TileManager(cyclOSMCacheBasePath, cyclOSMTileServerHost);

        ObjectProperty<TileManager> tileManagerProperty = new SimpleObjectProperty<TileManager>(standardTileManager);
        ObjectProperty<TileManager> overlayTileManagerProperty = new SimpleObjectProperty<TileManager>();
        //

        //BONUS
        MenuItem clearWaypoints = new MenuItem("Supprimer les Points");
        clearWaypoints.setOnAction(event -> routeBean.getWaypoints().clear());
        MenuItem invertRoute = new MenuItem("Inverser la Route");
        invertRoute.setOnAction(event -> Collections.reverse(routeBean.getWaypoints()));
        MenuItem overlayCyclingRoutes = new MenuItem("Afficher les Pistes Cyclables");
        overlayCyclingRoutes.setOnAction(event -> {
            if (overlayTileManagerProperty.get() == null){
                overlayTileManagerProperty.set(overlayTileManager);
                overlayCyclingRoutes.setText("Cacher les Pistes Cyclables");
            } else {
                overlayTileManagerProperty.set(null);
                overlayCyclingRoutes.setText("Afficher les Pistes Cyclables");
            }
        });
        Menu waypointsSaved= new Menu("Sauvegardé");
        routeBean.getSavedWaypoints().addListener((ListChangeListener<? super Pair<String, Waypoint>>) n->{
            ObservableList<Pair<String, Waypoint>> liste = routeBean.getSavedWaypoints();

            waypointsSaved.getItems().clear();
            for (int i = 0; i < liste.size(); i++) {
                MenuItem waypoint = new MenuItem();
                waypoint.setOnAction(event-> {
                            for (int j = 0; j < ; j++) {

                            }
                            routeBean.getWaypoints().add(liste.get());
                        };
                waypoint.setText(liste.get(i).getKey());
                waypointsSaved.getItems().add(waypoint);
            }

        });
        MenuItem changeBaseMap = new MenuItem("Utiliser la Carte CyclOSM");
        changeBaseMap.setOnAction(event -> {
            if(tileManagerProperty.get() != cyclOSMTileManager) {
                tileManagerProperty.set(cyclOSMTileManager);
                changeBaseMap.setText("Utiliser la Carte Standard");
            } else {
                tileManagerProperty.set(standardTileManager);
                changeBaseMap.setText("Utiliser la Carte CyclOSM");
            }
        });

        MenuItem importOption = new MenuItem("Importer GPX");
        //


        MenuItem exportOption = new MenuItem("Exporter GPX");
        exportOption.disableProperty().bind(
                routeBean.getRouteProperty().isNull());//itineraire non nul
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
        //BONUS
        Menu menuFondCarte = new Menu("Fond de Carte");
        Menu menuItineraire = new Menu("Itinéraire");
        //
        MenuBar menuBar = new MenuBar(menuFichier, menuFondCarte, menuItineraire);
        menuBar.setUseSystemMenuBar(true);

        //BONUS
        //
        menuFichier.getItems().add(exportOption);
        menuFichier.getItems().add(importOption);
        menuItineraire.getItems().add(clearWaypoints);
        menuItineraire.getItems().add(invertRoute);
        menuFondCarte.getItems().add(changeBaseMap);
        menuFondCarte.getItems().add(overlayCyclingRoutes);
        //



        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManagerProperty, routeBean, errorConsumer, overlayTileManagerProperty);
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
                System.out.println(file.getPath());
                try {
                    Route route = GpxReader.convertGpxToRoute(file, graph);
                    System.out.println(route.length());
                    ImportedRoute importedRoute = new ImportedRoute(new SimpleObjectProperty<Route>(route),
                            mapManager.mapViewParametersProperty());
                    mapPane.getChildren().add(importedRoute.pane());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        });

        routeBean.getSavedWaypoint().addListener(n->{
            System.out.println("hello1");
            Waypoint waypoint = routeBean.getSavedWaypoint().get();
            ObservableList<Pair<String, Waypoint>> liste = routeBean.getSavedWaypoints();
            if (!(waypoint == null || liste.contains(waypoint))) {
                final Stage dialog = new Stage();
                BorderPane pane = new BorderPane();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(primaryStage);
                VBox dialogVbox = new VBox(20);
                Button save = new Button("Save");
                Button cancel = new Button("Annuler");
                TextField text = new TextField();
                dialogVbox.getChildren().addAll(text,save,cancel);
                save.setOnAction(event->{

                    if (!(text.getText().length() == 0 || liste.contains(waypoint))) {
                        liste.add(new Pair<>(text.getText(),waypoint));
                        System.out.println(liste);
                        dialog.close();
                    } else {
                        save.setText("Entrez le nom de votre waypoint !");
                    }
                });
                cancel.setOnAction(event2-> dialog.close());
                pane.setCenter(dialogVbox);


                //Scene dialogScene = new Scene(dialogVbox, 300, 200);
                dialog.setScene(new Scene(pane));
                dialog.show();
            }
        });


        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("JaVelo");
        primaryStage.setScene(new Scene(paneWithMenu));
        primaryStage.show();
    }
}
