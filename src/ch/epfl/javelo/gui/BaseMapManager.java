package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;

import java.io.IOException;


public final class BaseMapManager {

    private static final int TILE_SIZE = 256;
    private final TileManager tileManager;
    private final WaypointsManager waypointManager;
    private final ObjectProperty<MapViewParameters> mapParameters;
    private boolean redrawNeeded;
    private final Canvas canvas;
    private final Pane pane;
    private PointWebMercator lastMousePosition;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointManager, ObjectProperty<MapViewParameters> mapParameters) {
        this.tileManager = tileManager;
        this.waypointManager = waypointManager;
        this.mapParameters = mapParameters;

        this.redrawNeeded = true;
        this.pane = new Pane();
        this.canvas = new Canvas(pane.getWidth(), pane.getHeight());
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                MapViewParameters params = mapParameters.get();
                double delta = event.getDeltaY()/10;
                double mouseX = event.getX();
                double mouseY = event.getY();
                saveMousePosition(mouseX, mouseY);

                int newZoom = Math2.clamp(8, (int)(params.zoomLevel() + delta), 19);
                double newX = lastMousePosition.xAtZoomLevel(newZoom) - mouseX;
                double newY = lastMousePosition.yAtZoomLevel(newZoom) - mouseY;

                mapParameters.set(new MapViewParameters(newZoom, newX, newY));
            }
        });

        pane.setOnMousePressed( event -> {
            saveMousePosition(event.getX(), event.getY());
        });
        pane.setOnMouseDragged( event -> {
            MapViewParameters params = mapParameters.get();
            PointWebMercator newMousePosition = mapParameters.get().pointAt(event.getX(), event.getY());
            int zoomLevel = params.zoomLevel();

            double xTranslate = newMousePosition.xAtZoomLevel(zoomLevel) - lastMousePosition.xAtZoomLevel(zoomLevel);
            double yTranslate = newMousePosition.yAtZoomLevel(zoomLevel) - lastMousePosition.yAtZoomLevel(zoomLevel);
            //System.out.println("xTranslate : " + xTranslate);
            //System.out.println("yTranslate : " + yTranslate);
            double newX = params.topLeft().getX() - xTranslate;
            double newY = params.topLeft().getY() - yTranslate;
            mapParameters.set(params.withMinXY(newX, newY));

            saveMousePosition(event.getX(),event.getY());
        });
        pane.setOnMouseReleased( event ->  {
            saveMousePosition(event.getX(), event.getY());
        });

        pane.setOnMouseClicked( event -> {
            if (event.isStillSincePress()){
            waypointManager.addWaypoint(event.getX(), event.getY());}
        });

        canvas.widthProperty().addListener( w -> {
            redrawOnNextPulse();
        });
        canvas.heightProperty().addListener( h -> {
            redrawOnNextPulse();
        });
        mapParameters.addListener( p -> {
            redrawOnNextPulse();
        });

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;
        //System.out.println("Redraw...");
        GraphicsContext context = canvas.getGraphicsContext2D();

        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int zoom = mapParameters.get().zoomLevel();

        Point2D topLeft = mapParameters.get().topLeft();

        int topLeftTileX = (int) topLeft.getX() / TILE_SIZE;
        int topLeftTileY = (int) topLeft.getY() / TILE_SIZE;
        int bottomRightTileX = (int) (topLeft.getX() + width) / TILE_SIZE + 1;
        int bottomRightTileY = (int) (topLeft.getY() + height) / TILE_SIZE + 1;

        double xShift = -(topLeft.getX() - TILE_SIZE * topLeftTileX);

        //System.out.println("Top Left : " + topLeftTileX + " " + topLeftTileY);
        //System.out.println("Bottom Right : " + bottomRightTileX + " " + bottomRightTileY);
        //System.out.println(zoom);

        for (int i = topLeftTileX; i < bottomRightTileX; i++) {
            double yShift = -(topLeft.getY() - TILE_SIZE * topLeftTileY);
            for (int j = topLeftTileY; j < bottomRightTileY; j++) {

                //System.out.println("Tile : " + i + " " + j);
                //System.out.println("Position : " + xShift + " " + yShift);
                try {
                    //Preconditions.checkArgument(TileManager.TileId.isValid(zoom, i, j));
                    Image tile = tileManager.imageForTileAt(new TileManager.TileId(zoom, i, j));
                    context.drawImage(tile, xShift, yShift);
                }
                catch (IOException ignored) {}
                yShift+=TILE_SIZE;
            }
            xShift+=TILE_SIZE;
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private void saveMousePosition(double x, double y){
        lastMousePosition = mapParameters.get().pointAt(x, y);
    }
}
