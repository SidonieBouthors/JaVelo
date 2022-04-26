package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * MapViewParameters
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 *
 * MapView parameters containing the given parameters
 * @param zoomLevel     : level of zoom of the view
 * @param x             : x coordinate of the top left of the view
 * @param y             : y coordinate of the top left of the view
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * Returns a Point2D corresponding to
     * the top left corner of the MapView
     * @return Point2D of the top left corner
     */
    Point2D topLeft(){
        return new Point2D(x, y);
    }

    /**
     * Returns a MapViewParameters with the same zoom as this
     * but different coordinates
     * @param x
     * @param y
     * @return
     */
    MapViewParameters withMinXY(double x, double y){
        return new MapViewParameters(zoomLevel, x, y);
    }

    /**
     * Returns the PoinWebMercator corresponding to
     * the given relative coordinates
     * @param viewX     : x coordinate relative to the top left
     * @param viewY     : y coordinate relative to the top left
     * @return the corresponding PointWebMercator
     */
    PointWebMercator pointAt(double viewX, double viewY){
        return new PointWebMercator(x + viewX, y + viewY);
    }

    /**
     * Returns the x coordinate of the point relative to the top left
     * @param point     : given point
     * @return x coordinate of the point relative to the view
     */
    double viewX(PointWebMercator point){
        return point.x() - x;
    }

    /**
     * Returns the y coordinate of the point relative to the top left
     * @param point     : given point
     * @return y coordinate of the point relative to the view
     */
    double viewY(PointWebMercator point){
        return point.y() - y;
    }
}
