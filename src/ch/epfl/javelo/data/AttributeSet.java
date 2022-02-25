package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {

    public AttributeSet {
        Preconditions.checkArgument((bits >>> Attribute.COUNT) == 0);
    }
    public static AttributeSet of(Attribute... attributes){
        long attributeBits = 0;
        for (int i = 0; i < attributes.length; i++) {
            attributeBits += Math.pow(2, Attribute.COUNT - 1 - attributes[i].ordinal());
        }
        return new AttributeSet(attributeBits);
    }

    public boolean contains(Attribute attribute) {
        long a = bits << 64 - Attribute.COUNT + attribute.ordinal();
        a = a >>> 63;
        return a == 1;
    }

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
