package ch.epfl.sigcheck;

// Attention : cette classe n'est *pas* un test JUnit, et son code n'est
// pas destiné à être exécuté. Son seul but est de vérifier, autant que
// possible, que les noms et les types des différentes entités à définir
// pour cette étape du projet sont corrects.

final class SignatureChecks_2 {
    private SignatureChecks_2() {}

    void checkPreconditions() throws Exception {
        ch.epfl.javelo.Preconditions.checkArgument(v01);
    }

    void checkMath2() throws Exception {
        v02 = ch.epfl.javelo.Math2.asinh(v02);
        v03 = ch.epfl.javelo.Math2.ceilDiv(v03, v03);
        v03 = ch.epfl.javelo.Math2.clamp(v03, v03, v03);
        v02 = ch.epfl.javelo.Math2.clamp(v02, v02, v02);
        v02 = ch.epfl.javelo.Math2.dotProduct(v02, v02, v02, v02);
        v02 = ch.epfl.javelo.Math2.interpolate(v02, v02, v02);
        v02 = ch.epfl.javelo.Math2.norm(v02, v02);
        v02 = ch.epfl.javelo.Math2.projectionLength(v02, v02, v02, v02, v02, v02);
        v02 = ch.epfl.javelo.Math2.squaredNorm(v02, v02);
    }

    void checkCh1903() throws Exception {
        v02 = ch.epfl.javelo.projection.Ch1903.e(v02, v02);
        v02 = ch.epfl.javelo.projection.Ch1903.lat(v02, v02);
        v02 = ch.epfl.javelo.projection.Ch1903.lon(v02, v02);
        v02 = ch.epfl.javelo.projection.Ch1903.n(v02, v02);
    }

    void checkSwissBounds() throws Exception {
        v02 = ch.epfl.javelo.projection.SwissBounds.HEIGHT;
        v02 = ch.epfl.javelo.projection.SwissBounds.MAX_E;
        v02 = ch.epfl.javelo.projection.SwissBounds.MAX_N;
        v02 = ch.epfl.javelo.projection.SwissBounds.MIN_E;
        v02 = ch.epfl.javelo.projection.SwissBounds.MIN_N;
        v02 = ch.epfl.javelo.projection.SwissBounds.WIDTH;
        v01 = ch.epfl.javelo.projection.SwissBounds.containsEN(v02, v02);
    }

    void checkPointCh() throws Exception {
        v04 = new ch.epfl.javelo.projection.PointCh(v02, v02);
        v02 = v04.distanceTo(v04);
        v02 = v04.e();
        v01 = v04.equals(v05);
        v03 = v04.hashCode();
        v02 = v04.lat();
        v02 = v04.lon();
        v02 = v04.n();
        v02 = v04.squaredDistanceTo(v04);
        v06 = v04.toString();
    }

    void checkWebMercator() throws Exception {
        v02 = ch.epfl.javelo.projection.WebMercator.lat(v02);
        v02 = ch.epfl.javelo.projection.WebMercator.lon(v02);
        v02 = ch.epfl.javelo.projection.WebMercator.x(v02);
        v02 = ch.epfl.javelo.projection.WebMercator.y(v02);
    }

    void checkPointWebMercator() throws Exception {
        v07 = new ch.epfl.javelo.projection.PointWebMercator(v02, v02);
        v07 = ch.epfl.javelo.projection.PointWebMercator.of(v03, v02, v02);
        v07 = ch.epfl.javelo.projection.PointWebMercator.ofPointCh(v04);
        v01 = v07.equals(v05);
        v03 = v07.hashCode();
        v02 = v07.lat();
        v02 = v07.lon();
        v04 = v07.toPointCh();
        v06 = v07.toString();
        v02 = v07.x();
        v02 = v07.xAtZoomLevel(v03);
        v02 = v07.y();
        v02 = v07.yAtZoomLevel(v03);
    }

    void checkBits() throws Exception {
        v03 = ch.epfl.javelo.Bits.extractSigned(v03, v03, v03);
        v03 = ch.epfl.javelo.Bits.extractUnsigned(v03, v03, v03);
    }

    void checkQ28_4() throws Exception {
        v02 = ch.epfl.javelo.Q28_4.asDouble(v03);
        v08 = ch.epfl.javelo.Q28_4.asFloat(v03);
        v03 = ch.epfl.javelo.Q28_4.ofInt(v03);
    }

    void checkAttributeSet() throws Exception {
        v09 = new ch.epfl.javelo.data.AttributeSet(v10);
        v09 = ch.epfl.javelo.data.AttributeSet.of(v11);
        v10 = v09.bits();
        v01 = v09.contains(v12);
        v01 = v09.equals(v05);
        v03 = v09.hashCode();
        v01 = v09.intersects(v09);
        v06 = v09.toString();
    }

    void checkFunctions() throws Exception {
        v13 = ch.epfl.javelo.Functions.constant(v02);
        v13 = ch.epfl.javelo.Functions.sampled(v14, v02);
    }

    boolean v01;
    double v02;
    int v03;
    ch.epfl.javelo.projection.PointCh v04;
    java.lang.Object v05;
    java.lang.String v06;
    ch.epfl.javelo.projection.PointWebMercator v07;
    float v08;
    ch.epfl.javelo.data.AttributeSet v09;
    long v10;
    ch.epfl.javelo.data.Attribute[] v11;
    ch.epfl.javelo.data.Attribute v12;
    java.util.function.DoubleUnaryOperator v13;
    float[] v14;
}
