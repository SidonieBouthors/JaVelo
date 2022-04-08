package ch.epfl.javelo;

/**
 * Bits
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
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
        Preconditions.checkArgument(start + length <= Integer.SIZE
                                        && start >= 0 && length >= 0);

        return  (value << Integer.SIZE - length - start) >> Integer.SIZE - length;
    }

    /**
     * Extracts bit sequence described by start & length from bit sequence value (32 bit)
     * @param value     : value of int to extract from
     * @param start     : index of bit to start extracting from
     * @param length    : length of sequence to extract
     * @return extracted sequence as int
     */
    public static int extractUnsigned (int value, int start, int length){
        Preconditions.checkArgument(start + length <= Integer.SIZE && start >= 0
                                        && 0 <= length && length < 32);

        return (value <<  Integer.SIZE - length - start) >>> Integer.SIZE - length;
    }
}
