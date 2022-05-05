package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.projection.WebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class RouteManager {

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final Consumer<String> errorSignal;
    private final Pane pane;

    RouteManager (RouteBean routeBean, ObjectProperty<MapViewParameters> mapProperty, Consumer<String> errorSignal){
        this.routeBean = routeBean;
        this.mapProperty = mapProperty;
        this.errorSignal = errorSignal;
        this.pane = new Pane();
        pane.setPickOnBounds(false);

        MapViewParameters params = mapProperty.get();

        Polyline routeLine = new Polyline();
        routeLine.setLayoutX(params.topLeft().getX());
        routeLine.setLayoutY(params.topLeft().getY());
        routeLine.getPoints().setAll(points());

        routeLine.setId("route");

        Circle highlightDisc = new Circle(5);
        highlightDisc.setId("highlight");

        pane.getChildren().add(routeLine);
        pane.getChildren().add(highlightDisc);

        highlightDisc.setOnMouseClicked( event -> {

        });

        routeBean.highlightedPositionProperty().addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            if (routeBean.routeProperty() == null) {

            }
        });
        routeBean.routeProperty().addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            //reconstruire totalement et/ou rendre (in)visible la polyligne
        });
        mapProperty.addListener( (p, oldValue, newValue) -> {
            //positionner et/ou rendre (in)visible le disque
            // SI LE ZOOM CHANGE
            //reconstruire totalement et/ou rendre (in)visible la polyligne
            // SI LA CARTE EST GLISSE MAIS LE ZOOM NE CHANGE PAS
            // repositionner - sans la reconstruire - la polyligne représentant l'itinéraire
        });


    }

    public Pane pane(){
        return pane;
    }

    private List<Double> points(){
        Route route = routeBean.routeProperty().get();
        if (route == null) return new ArrayList<>();
        List<PointCh> pointsCh = route.points();
        List<Double> points = new ArrayList<>();
        MapViewParameters params = mapProperty.get();

        for (PointCh pointCh:pointsCh){
            PointWebMercator point = PointWebMercator.ofPointCh(pointCh);
            points.add(point.xAtZoomLevel(params.zoomLevel()));
            points.add(point.yAtZoomLevel(params.zoomLevel()));
        }
        return points;
    }
}
