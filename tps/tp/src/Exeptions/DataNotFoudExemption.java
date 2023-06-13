package Exeptions;

/**
 * This class represents a custom exception that can be thrown when the data
 * structure used in the file isn't found
 * It extends the built-in Exception class and provides a constructor that takes
 * a message as a parameter to provide more information about the exception.
 * 
 * @version 1.0
 * @since 2023-05-13
 * @author Pedro
 */
public class DataNotFoudExemption extends Exception {
    /**
     * Constructor that takes a message as a parameter to provide more information
     * about the exception.
     * 
     * @param message
     */
    public DataNotFoudExemption(String message) {
        super(message);
    }
}
