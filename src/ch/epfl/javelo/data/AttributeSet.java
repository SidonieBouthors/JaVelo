package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {

    /**
     * Constructs an AttributeSet
     * @throws IllegalArgumentException if there are bits of value 1 outside those representing attributes
     * @param bits  : binary representation of attributes
     */
    public AttributeSet {
        Preconditions.checkArgument((bits >>> Attribute.COUNT) == 0);
    }

    /**
     * Returns an AttributeSet with the specified attributes
     * @param attributes    : list of attributes
     * @return an Attribute set with these attributes
     */
    public static AttributeSet of(Attribute... attributes){
        long attributeBits = 0;
        for (int i = 0; i < attributes.length; i++) {
            attributeBits += 1L << attributes[i].ordinal();
        }
        return new AttributeSet(attributeBits);
    }

    /**
     * Returns true iff this contains the attribute
     * @param attribute     : attribute that is being checked
     * @return whether this contains the attribute
     */
    public boolean contains(Attribute attribute) {
        long a = bits << Long.SIZE - Attribute.COUNT + attribute.ordinal();
        a = a >>> Long.SIZE-1;
        return a == 1;
    }

    /**
     * Returns true iff this and that have one or more common attributes
     * @param that  : AttributeSet to compare to
     * @return whether the AttributeSets intersect
     */
    public boolean intersects(AttributeSet that){
        if ((this.bits() & that.bits()) == 0){
            return false;
        } else { return true; }
    }

    @Override
    public String toString(){
        StringJoiner a = new StringJoiner(",","{","}");
        for (Attribute attribute: Attribute.ALL) {
            if (contains(attribute)){
                a.add(attribute.toString());
            }
        }
        return a.toString();
    }

}
