package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;


public final class BaseMapManager {

    private final TileManager tileManager;
    private final WaypointsManager waypointManager;
    private final ObjectProperty<MapViewParameters> mapParameters;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane pane;

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
            System.out.println(oldS==null);
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

            }
        });

        canvas.widthProperty().addListener((w) -> {
            redrawOnNextPulse();
        });

    }

    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {
        System.out.println("Redraw if needed...");
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();

        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int zoom = mapParameters.get().zoomLevel();
        System.out.println("W :" + width + " H:" + height);
        System.out.println("Calculating Tiles...");
        Point2D topLeft = mapParameters.get().topLeft();
        int topLeftTileX = (int) topLeft.getX() / 256;
        int topLeftTileY = (int) topLeft.getY() / 256;
        int bottomRightTileX = (int) (topLeft.getX() + width) / 256;
        int bottomRightTileY = (int) (topLeft.getY() + height) / 256;
        double xShift = -(topLeft.getX() - 256 * topLeftTileX);
        double yShift = -(topLeft.getY() - 256 * topLeftTileY);

        System.out.println(topLeft.getX()+ "  " + topLeft.getY());
        System.out.println(topLeftTileX+ "  " + topLeftTileY);
        System.out.println(bottomRightTileX+ "  " + bottomRightTileY);
        for (int i = topLeftTileX; i < bottomRightTileX; i++) {
            for (int j = topLeftTileY; j < bottomRightTileY; j++) {
                System.out.println("Tile : " + i + " " + j);
                try {
                    Image tile = tileManager.imageForTileAt(new TileManager.TileId(zoom, i, j));
                    context.drawImage(tile, xShift, yShift);
                    System.out.println("Tile drawn");
                }
                catch (IOException e) {}
                yShift+=256;
            }
            xShift+=256;
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
