package ch.epfl.javelo;

public final class Bits {
    /**
     * Non instantiable
     */
    private Bits(){}

    /**
     * Extracts signed bit sequence described by start & length from bit sequence value (32 bit)
     * @param value     : value of int to extract from
     * @param start     : index of bit to start extracting from
     * @param length    : length of sequence to extract
     * @return extracted sequence as int
     */
    public static int extractSigned (int value, int start, int length){
        Preconditions.checkArgument(start + length<32 && start>=0 && length >=0);
        return (value << start) >> 32 - length;
    }

    /**
     * Extracts bit sequence described by start & length from bit sequence value (32 bit)
     * @param value     : value of int to extract from
     * @param start     : index of bit to start extracting from
     * @param length    : length of sequence to extract
     * @return extracted sequence as int
     */
    public static int extractUnsigned (int value, int start, int length){
        Preconditions.checkArgument(start+ length<=32 && start>=0 && length >=0);
        return (value << start) >>> 32 - length;
    }
}
