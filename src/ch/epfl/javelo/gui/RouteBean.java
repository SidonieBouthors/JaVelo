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

    private static ObservableList<Waypoint> waypoints;
    private final DoubleProperty highlightedPosition;

    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final RouteComputer computer;
    private final int cacheMemoireSize = 50;


    LinkedHashMap<Pair<Integer,Integer>, Route> cacheMemoire =
            new LinkedHashMap<Pair<Integer,Integer>, Route>() {
                protected boolean removeEldestEntry(Map.Entry<Pair<Integer,Integer>, Route> eldest)
                    {
                    return cacheMemoire.size() > cacheMemoireSize;
                }
            };

    public RouteBean(RouteComputer computer) {
        this.route = new SimpleObjectProperty<Route>();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.elevationProfile = new SimpleObjectProperty();
        this.computer = computer;

        //test
        highlightedPosition.set(7000);

        int minimalSize = 2;
        waypoints = FXCollections.observableArrayList();
        waypoints.addListener((ListChangeListener<? super Waypoint>) o -> {


            if (waypoints.size() < minimalSize) {
                System.out.println("Listener put road to null");

                route.set(null);
                elevationProfile.set(null);
            }else {
                routeComputer();
                elevationProfileComputer();

            }

        });
    }

    private void routeComputer() {
        Route miniRoute;
        List<Route> routeList = new ArrayList<>();

        for (int i = 0; i < waypoints.size()-1; i++) {
            int startNodeId = waypoints.get(i).closestNodeId();
            int endNodeId = waypoints.get(i + 1).closestNodeId();
            Pair<Integer,Integer> routePair = new Pair<>(startNodeId, endNodeId);
            System.out.println(routePair);


            //
            if (cacheMemoire.containsKey(routePair) ) {
                System.out.println("the key is :" + cacheMemoire.get(routePair));
                miniRoute = cacheMemoire.get(routePair);
                routeList.add(miniRoute);
            } else if (startNodeId != endNodeId){

                miniRoute = computer.bestRouteBetween(startNodeId, endNodeId);
                if (miniRoute != null) {
                    cacheMemoire.put(routePair, miniRoute);
                    routeList.add(miniRoute);
                } else {
                    System.out.println("road was null in routecomputer");
                    elevationProfile.set(null);
                    route.set(null);
                }

            }

        }
        if (!routeList.isEmpty()) {
            route.set(new MultiRoute(routeList));
        } else {
            System.out.println("routeList was empty in route computer");
            route.set(null);
        }
        System.out.println(routeList);
    }
    private void elevationProfileComputer() {
        if (route.get() == null) {
            elevationProfile.set(null);
            return;
        }
        int maxStepLength = 5;
        ElevationProfileComputer.elevationProfile(route.get(),5);
    }

    /**
     * retournant la propriété elle-même, de type DoubleProperty
     * @return
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * retournant le contenu de la propriété, de type double ;
     * @return
     */
    public Double highlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * prenant une valeur de type double et la stockant dans la propriété.
     * @param highlightedPosition
     */
    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }

    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    public static ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }
}
