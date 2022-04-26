package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Waypoint
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 *
 * Waypoint with the given parameters
 * @param position          : position of the waypoint in Swiss coordinates
 * @param closestNodeId     : ID of the closest node
 */
public record Waypoint(PointCh position, int closestNodeId) { }
