package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

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

    // for listener in RouteBean Constructor
    private static final int MINIMAL_SIZE = 2;
    //for elevation profile computer
    private static final int MAX_STEP_LENGTH = 5;

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
        this.elevationProfile = new SimpleObjectProperty<>();
        this.computer = computer;
        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((ListChangeListener<? super Waypoint>) o -> {
            if (waypoints.size() < MINIMAL_SIZE) {
                route.set(null);
                elevationProfile.set(null);
            }else {
                computeRoute();
                computeElevationProfile();
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
            int startNodeId = waypoints.get(i).nodeId();
            int endNodeId = waypoints.get(i + 1).nodeId();
            Pair<Integer,Integer> routePair = new Pair<>(startNodeId, endNodeId);


            if (cacheMemoire.containsKey(routePair) ) {

                miniRoute = cacheMemoire.get(routePair);
                routeList.add(miniRoute);
            } else if (startNodeId != endNodeId){

                miniRoute = computer.bestRouteBetween(startNodeId, endNodeId);
                if (miniRoute != null) {
                    cacheMemoire.put(routePair, miniRoute);
                    routeList.add(miniRoute);
                } else {
                    //elevationProfile.set(null); (pas besoin c'est fait dans l'autre methode?
                    route.set(null);
                    return;
                    /*routeList.clear();
                    break;*/
                }

            }
        }

        if (!routeList.isEmpty()) {
            route.set(new MultiRoute(routeList));
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
        elevationProfile.set(ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH));
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

    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}
