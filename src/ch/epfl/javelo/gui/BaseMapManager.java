package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class BaseMapManager {

    private static final int TILE_SIZE = 256;
    private final TileManager tileManager;
    private final WaypointsManager waypointManager;
    private final ObjectProperty<MapViewParameters> mapParameters;
    private final Canvas canvas;
    private final Pane pane;
    private final SimpleLongProperty minScrollTime = new SimpleLongProperty();
    private PointWebMercator lastMousePosition;
    private boolean redrawNeeded;

    /**
     * Construct the map
     * @param tileManager       : tile manager
     * @param waypointManager   : waypoint manager
     * @param mapParameters     : property containing the map view parameters
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointManager,
                          ObjectProperty<MapViewParameters> mapParameters) {
        this.tileManager = tileManager;
        this.waypointManager = waypointManager;
        this.mapParameters = mapParameters;
        this.pane = new Pane();
        this.canvas = new Canvas();
        pane.getChildren().add(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        installListeners();
        installHandlers();
        this.redrawNeeded = true;
    }

    /**
     * Returns the pane containing the base map
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
        MapViewParameters params = mapParameters.get();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int zoom = mapParameters.get().zoomLevel();

        int topLeftTileX = (int) params.x() / TILE_SIZE;
        int topLeftTileY = (int) params.y() / TILE_SIZE;
        int bottomRightTileX = (int) (params.x() + width) / TILE_SIZE + 1;
        int bottomRightTileY = (int) (params.y() + height) / TILE_SIZE + 1;

        double xShift = -(params.x() - TILE_SIZE * topLeftTileX);
        for (int i = topLeftTileX; i < bottomRightTileX; i++) {

            double yShift = -(params.y() - TILE_SIZE * topLeftTileY);
            for (int j = topLeftTileY; j < bottomRightTileY; j++) {
                if (!TileManager.TileId.isValid(zoom,i,j)) continue;

                try {
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

    /**
     * Save mouse position for Handler Use (setOnMouseScroll, setOnMouseClicked...)
     * @param x     : mouse x position
     * @param y     : mouse y position
     */
    private void saveMousePosition(double x, double y) {
        lastMousePosition = mapParameters.get().pointAt(x, y);
    }

    private void installListeners(){
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        canvas.widthProperty().addListener((p, oldW, newW) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, oldH, newH) -> redrawOnNextPulse());
        mapParameters.addListener((p, oldM, newM) -> redrawOnNextPulse());
    }

    private void installHandlers() {
        pane.setOnScroll(event -> {

            if (event.getDeltaY() == 0d) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(event.getDeltaY());

            MapViewParameters params = mapParameters.get();
            double mouseX = event.getX();
            double mouseY = event.getY();
            saveMousePosition(mouseX, mouseY);

            int newZoom = Math2.clamp(8, params.zoomLevel() + zoomDelta, 19);
            double newX = lastMousePosition.xAtZoomLevel(newZoom) - mouseX;
            double newY = lastMousePosition.yAtZoomLevel(newZoom) - mouseY;

            mapParameters.set(new MapViewParameters(newZoom, newX, newY));
        });

        pane.setOnMousePressed( event -> saveMousePosition(event.getX(), event.getY()));
        pane.setOnMouseDragged( event -> {
            MapViewParameters params = mapParameters.get();
            PointWebMercator newMousePosition = mapParameters.get().pointAt(event.getX(), event.getY());
            int zoomLevel = params.zoomLevel();

            double xTranslate = newMousePosition.xAtZoomLevel(zoomLevel) - lastMousePosition.xAtZoomLevel(zoomLevel);
            double yTranslate = newMousePosition.yAtZoomLevel(zoomLevel) - lastMousePosition.yAtZoomLevel(zoomLevel);

            double newX = params.x() - xTranslate;
            double newY = params.y() - yTranslate;
            mapParameters.set(params.withMinXY(newX, newY));

            saveMousePosition(event.getX(),event.getY());
        });

        pane.setOnMouseReleased( event -> saveMousePosition(event.getX(), event.getY()));

        pane.setOnMouseClicked( event -> {
            if (event.isStillSincePress()){
                waypointManager.addWaypoint(event.getX(), event.getY());}
        });
    }
}