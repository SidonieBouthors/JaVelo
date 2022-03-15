package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public interface Route {
    /**
     * Returns the index of the segment at given position
     * @param position  : position along the route (in meters)
     * @return index of the segment
     */
    int indexOfSegmentAt(double position);
    /**
     * Returns the length of the route
     * @return the length of the route (in meters)
     */
    double length();

    /**
     * Returns all the edges of the route
     * @return list of all the edges of the route
     */
    List<Edge> edges();

    /**
     * Returns all the points situated on the ends of the route edges
     * @return all the points on the ends of the route edges
     */
    List<PointCh> points();

    /**
     * Returns the point at a given position along the route
     * @param position  : position along the route (in meters)
     * @return point at given position
     */
    PointCh pointAt(double position);

    /**
     * Returns the altitude at the given position along the route
     * @param position  : position along the route
     * @return altitude at given position
     */
    double elevationAt(double position);

    /**
     * Returns the ID of the node belonging to the route and closest to the given position
     * @param position  : position along the route (in meters)
     * @return ID of the node of the route closest to the position
     */
    int nodeClosestTo(double position);

    /**
     * Returns the point of the route closest to the given reference point
     * @param point : reference point
     * @return point of the route closest to the reference point
     */
    RoutePoint pointClosestTo(PointCh point);
}
