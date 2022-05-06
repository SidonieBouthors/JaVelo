package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final class RouteBean {

    private static final int CACHE_MEMOIRE_SIZE = 50;
    private final ObservableList<Waypoint> waypoints;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer computer;



    private final LinkedHashMap<Pair<Integer,Integer>, Route> cacheMemoire =
            new LinkedHashMap<>() {
                protected boolean removeEldestEntry(Map.Entry<Pair<Integer,Integer>, Route> eldest) {
                    return cacheMemoire.size() > CACHE_MEMOIRE_SIZE;
                }
            };


    /**
     * Construct Route Bean
     * @param computer Route Computer
     */
    public RouteBean(RouteComputer computer) {
        this.route = new SimpleObjectProperty<>();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.elevationProfile = new SimpleObjectProperty();
        this.computer = computer;

        //test
        highlightedPosition.set(5000);

        int minimalSize = 2;
        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((ListChangeListener<? super Waypoint>) o -> {

            if (waypoints.size() < minimalSize) {
                route.set(null);
                elevationProfile.set(null);
            }else {
                routeComputer();
                elevationProfileComputer();
            }
        });
    }

    /**
     * compute route
     *
     */

    private void routeComputer() {
        Route miniRoute;
        List<Route> routeList = new ArrayList<>();

        for (int i = 0; i < waypoints.size()-1; i++) {
            int startNodeId = waypoints.get(i).closestNodeId();
            int endNodeId = waypoints.get(i + 1).closestNodeId();
            Pair<Integer,Integer> routePair = new Pair<>(startNodeId, endNodeId);

            //
            if (cacheMemoire.containsKey(routePair) ) {
                miniRoute = cacheMemoire.get(routePair);
                routeList.add(miniRoute);
            } else if (startNodeId != endNodeId){

                miniRoute = computer.bestRouteBetween(startNodeId, endNodeId);
                if (miniRoute != null) {
                    cacheMemoire.put(routePair, miniRoute);
                    routeList.add(miniRoute);
                } else {
                    elevationProfile.set(null);
                    route.set(null);
                    routeList.clear();
                    break;
                }

            }

        }
        if (!routeList.isEmpty()) {
            route.set(new MultiRoute(routeList));
        } else {
            route.set(null);
        }
    }

    /**
     * compute Elevation Profile
     */
    private void elevationProfileComputer() {
        if (route.get() == null) {
            elevationProfile.set(null);
            return;
        }
        int maxStepLength = 5;
        ElevationProfileComputer.elevationProfile(route.get(),5);
    }

    /**
     * return the  highlighted DoubleProperty
     * @return highlighted DoubleProperty
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * return the Highlighted Position
     * @return Highlighted Position
     */
    public Double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Setter HighLighted Position
     * @param highlightedPosition
     */
    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    /**
     * getter Elevation Profile property
     * @return elevation profile property
     */

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }

    /**
     * getter Route property
     * @return route property
     */

    public ReadOnlyObjectProperty<Route> getRouteProperty() {
        return route;
    }

    /**
     * getter Observable List of waypoints
     * @return observable list of waypoints
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }
}
