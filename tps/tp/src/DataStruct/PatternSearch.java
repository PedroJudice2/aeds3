package DataStruct;

/**
 * The PatternSearch interface provides a method for finding a pattern in a
 * file.
 * Implementations of this interface should define the behavior of the
 * findPattern method
 * to return the number of occurrences of the pattern in the file, or -1 in case
 * of error.
 * 
 * @version 1.0
 * @since 2014-03-31
 * @author Pedro
 */
public interface PatternSearch {
    /**
     * Find the pattern in the file.
     * 
     * @param pattern
     * @return
     */
    public int findPattern(String pattern);
}
