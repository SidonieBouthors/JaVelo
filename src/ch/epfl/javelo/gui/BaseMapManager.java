package ch.epfl.javelo.gui;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;


public final class BaseMapManager {

    private final TileManager tileManager;
    private final WaypointsManager waypointManager;
    private final Property mapParameters;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane pane;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointManager, Property mapParameters) {
        this.tileManager = tileManager;
        this.waypointManager = waypointManager;
        this.mapParameters = mapParameters;

        this.pane = new Pane();
        this.canvas = new Canvas();
        canvas.widthProperty().bind(pane.widthProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {

            }
        });

    }

    public Pane pane(){
        return null;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();

    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
