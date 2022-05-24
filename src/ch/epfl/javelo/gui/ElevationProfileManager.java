package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final  class ElevationProfileManager {

    private static final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private static final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
    private static final int MIN_HORIZONTAL_SPACING = 50;
    private static final int MIN_VERTICAL_SPACING = 25;

    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final ReadOnlyDoubleProperty positionAlongTheProfile;
    private final BorderPane borderPane;
    private final Pane pane;
    private final Path path;
    private final Polygon polygon;
    private final Insets rectInsets = new Insets(10, 10, 20, 40);
    private final ObjectProperty<Rectangle2D> rectProperty;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final Line line;
    private final Text textVbox;
    private final Group group;
    private final DoubleProperty mousePositionOnProfile;


    public ElevationProfileManager (ReadOnlyObjectProperty<ElevationProfile> elevationProfile,
                                    ReadOnlyDoubleProperty positionAlongTheProfile){
        this.elevationProfile = elevationProfile;
        this.positionAlongTheProfile = positionAlongTheProfile;
        this.rectProperty = new SimpleObjectProperty<>();
        this.screenToWorld = new SimpleObjectProperty<>();
        this.worldToScreen = new SimpleObjectProperty<>();
        this.mousePositionOnProfile = new SimpleDoubleProperty(Double.NaN);


        //Text Vbox
        this.textVbox = new Text();
        //Vbox
        VBox vbox = new VBox();
        vbox.setId("profile_data");
        vbox.getChildren().add(textVbox);
        //Group
        this.group = new Group();
        //Path
        this.path = new Path();
        path.setId("grid");
        //Polygon
        this.polygon = new Polygon();
        polygon.setId("profile");
        //Line
        this.line = new Line();
        //Pane
        this.pane = new Pane();
        pane.getChildren().addAll(group, path,  polygon, line);
        //Rectangle
        rectProperty.set(new Rectangle2D(0,0,10,10));
        //BorderPane
        this.borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        borderPane.setCenter(pane);
        borderPane.setBottom(vbox);
        //borderPane.getChildren().add(pane);



        installHandler();
        installListeners();
        if (elevationProfile.get() != null) {
            createTransforms();
            installBindings();
        }
    }

    public Pane pane() {
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return mousePositionOnProfile;
    }

    private void createTransforms(){
        double profileLength = elevationProfile.get().length();
        double altMin = elevationProfile.get().minElevation();
        double altMax = elevationProfile.get().maxElevation();
        double rectWidth = rectProperty.get().getWidth();
        double rectHeight = rectProperty.get().getHeight();

        Affine worldToScreenTransform = new Affine();
        worldToScreenTransform.prependTranslation(
                0, - altMax);
        worldToScreenTransform.prependScale(
                rectWidth/profileLength, rectHeight/(altMin-altMax));
        worldToScreenTransform.prependTranslation(
                rectInsets.getLeft(), rectInsets.getTop());

        this.worldToScreen.set(worldToScreenTransform);
        try { this.screenToWorld.set(worldToScreenTransform.createInverse());}
        catch (NonInvertibleTransformException ignore) {}
    }

    private void createProfile(){
        double rectWidth = rectProperty.get().getWidth();
        double rectHeight = rectProperty.get().getHeight();
        ElevationProfile profile = elevationProfile.get();

        List<Double> points = new ArrayList<>();
        for (int i = 0; i <= profile.length(); i++) {
            Point2D point = worldToScreen.get().transform(i, profile.elevationAt(i));
            points.add(point.getX());
            points.add(point.getY());
        }
        points.add(rectWidth + rectInsets.getLeft());
        points.add(rectHeight + rectInsets.getTop());

        points.add(rectInsets.getLeft());
        points.add(rectHeight + rectInsets.getTop());

        polygon.getPoints().setAll(points);
    }

    /**
     * create the elevation profile statistics with length (in Km) and the rest in meters.
     */
    private void createStatistics() {
        String b = "Longueur : %1$.1f km" +
                "     Montée : %2$.0f m" +
                "     Descente : %3$.0f m" +
                "     Altitude : de %4$.0f m à %5$.0f m";
        ElevationProfile elevation = elevationProfile.get();
        b = String.format(b,elevation.length()/1000,elevation.totalAscent(),elevation.totalDescent(),elevation.minElevation(),elevation.maxElevation());
        textVbox.setText(b);
    }

    private void createGrid(){
        double profileLength = elevationProfile.get().length();
        double altRange = elevationProfile.get().maxElevation() - elevationProfile.get().minElevation();
        double rectWidth = rectProperty.get().getWidth();
        double rectHeight = rectProperty.get().getHeight();
        Transform toScreen = worldToScreen.get();

        double xNumberOfSteps = 0;
        double xStep;
        double xStepWorld = 0;
        for (int step: POS_STEPS) {
            xStep = Math.abs(toScreen.deltaTransform(step, 0).getX());
            if ( xStep >= MIN_HORIZONTAL_SPACING){
                xStepWorld = step;
                xNumberOfSteps = profileLength / step;
                break;
            }
        }
        double yNumberOfSteps = 0;
        double yStep;
        double yStepWorld = 0;
        for (int step: ELE_STEPS) {
            yStep = Math.abs(toScreen.deltaTransform(0, step).getY());
            if (yStep >= MIN_VERTICAL_SPACING){
                yStepWorld = step;
                yNumberOfSteps = altRange / step;
                break;
            }
        }
        List<PathElement> lines = new ArrayList<>();
        List<Text> labels = new ArrayList<>();
        // vertical lines
        for (int i = 0; i < xNumberOfSteps; i++) {
            double lineX = worldToScreen.get().transform(i * xStepWorld, 0).getX();
            //line
            lines.add(new MoveTo(lineX, rectInsets.getTop()));
            lines.add(new LineTo(lineX, rectInsets.getTop() + rectHeight));
            //label
            Text textHorizon = new Text();
            textHorizon.setTextOrigin(VPos.TOP);
            textHorizon.setFont(Font.font("Avenir", 10));
            textHorizon.setText("" + (int)((i * xStepWorld)/1000));
            textHorizon.getStyleClass().addAll("grid_label", "horizontal");
            textHorizon.setX(lineX - (textHorizon.prefWidth(0)/2));
            textHorizon.setY(rectHeight + textHorizon.prefHeight(0));
            labels.add(textHorizon);
        }
        // horizontal lines
        double minAlt = elevationProfile.get().minElevation();
        double firstLine = Math.ceil(minAlt / yStepWorld) * yStepWorld;
        for (int j = 0; j < yNumberOfSteps; j++) {
            double lineY = worldToScreen.get().transform(0, j * yStepWorld + firstLine).getY();
            if (lineY > rectInsets.getTop()) {
                //line
                lines.add(new MoveTo(rectInsets.getLeft(), lineY));
                lines.add(new LineTo(rectInsets.getLeft() + rectWidth, lineY));
                // label
                Text textVertical = new Text();
                textVertical.setTextOrigin(VPos.CENTER);
                textVertical.setFont(Font.font("Avenir", 10));
                textVertical.setText("" + (int) (j * yStepWorld + firstLine));
                textVertical.getStyleClass().addAll("grid_label", "vertical");
                textVertical.setY( lineY );
                textVertical.setX( rectInsets.getLeft() - (textVertical.prefWidth(0) + 2) );
                labels.add(textVertical);
            }
        }
        path.getElements().setAll(lines);
        group.getChildren().setAll(labels);
    }

    private void installBindings(){
        if (elevationProfile == null) {
            rectProperty.unbind();
            line.layoutXProperty().unbind();
            line.startYProperty().unbind();
            line.endYProperty().unbind();
            line.visibleProperty().unbind();
            return;
        }

        rectProperty.bind(Bindings.createObjectBinding(() -> {
                double rectWidth = Math.max(0d, pane.getWidth() - rectInsets.getLeft() - rectInsets.getRight());
                double rectHeight = Math.max(0d, pane.getHeight() - rectInsets.getTop() - rectInsets.getBottom());
                return new Rectangle2D(rectInsets.getLeft(), rectInsets.getTop(),
                    rectWidth, rectHeight);
                },
                pane.heightProperty(), pane.widthProperty()
        ));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(()->
                worldToScreen.get().transform(positionAlongTheProfile.get(),0).getX(),
                positionAlongTheProfile));
        line.startYProperty().bind(Bindings.select(rectProperty,"minY"));
        line.endYProperty().bind(Bindings.select(rectProperty,"maxY"));
        line.visibleProperty().bind(line.layoutXProperty().greaterThanOrEqualTo(0));

    }

    private void installHandler(){
        pane.setOnMouseMoved(event ->{
            if (event == null
                    || event.getX() < rectInsets.getLeft()
                    || event.getY() < rectInsets.getTop()
                    || event.getX() > rectInsets.getLeft() + rectProperty.get().getWidth()
                    || event.getY() > rectInsets.getTop() + rectProperty.get().getHeight()) {
                mousePositionOnProfile.set(Double.NaN);
            } else {
                double x = screenToWorld.get().transform(event.getX(), 0).getX();
                mousePositionOnProfile.set(x);
            }
        });

        pane.setOnMouseExited(event -> mousePositionOnProfile.set(Double.NaN));

    }
    private void installListeners(){
        rectProperty.addListener((p, oldR, newR) -> {
            createTransforms();
            createGrid();
            createProfile();
        });

        elevationProfile.addListener((p, oldP, newP) -> {
            installBindings();
            if(newP != null) {
                createTransforms();
                createStatistics();
                createProfile();
                createGrid();
            }
        });

    }
}
