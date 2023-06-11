package KMP;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import DataStruct.PatternSearch;
import FileOp.FileOp;

/**
 * The KMP class implements the PatternSearch interface and provides a method to
 * find a pattern in a text file using the Knuth-Morris-Pratt algorithm.
 * The class calculates the Longest Proper Prefix which is also a Suffix (LPS)
 * of the pattern and uses it to search for the pattern in the text file.
 * The findPattern method takes a pattern string as input and returns the number
 * of times the pattern appears in the text file.
 * If an IOException occurs, the method returns -1.
 * 
 * @version 1.0
 * @since 2023-02-24
 * @author Pedro
 */
public class KMP implements PatternSearch {

    private int[] list;
    private int numberOfPatterns = 0;
    private byte[] bytePattern;

    /**
     * Find the pattern in the file.
     * 
     * @param pattern
     * @return
     */
    public int findPattern(String pattern) {
        bytePattern = pattern.getBytes(StandardCharsets.UTF_8);
        list = calculateLPS(bytePattern);
        try {
            searchText();
        } catch (IOException e) {
            return -1;
        }
        return numberOfPatterns;
    }

    /**
     * Search for the pattern in the text file.
     * 
     * @throws IOException
     */
    private void searchText() throws IOException {
        RandomAccessFile arq = FileOp.arq;
        long i = 0; // index for txt[]
        int j = 0; // index for pat[]
        arq.seek(i);

        long endPosition = arq.length();
        int patternLenght = list.length;
        byte ba;
        while ((endPosition - i) >= (patternLenght - j)) {
            ba = arq.readByte();
            arq.seek(i);
            if (bytePattern[j] == ba) {
                j++;
                if (++i < endPosition) {
                    arq.seek(i);
                    ba = arq.readByte();
                    arq.seek(i);
                }
            }
            if (j == patternLenght) {
                j = list[j - 1];
                numberOfPatterns++;
            }

            // mismatch after j matches
            else if (i < endPosition && bytePattern[j] != ba) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = list[j - 1];
                else {
                    arq.seek(++i);
                    ba = arq.readByte();
                    arq.seek(i);
                }
            }
        }
    }

    /**
     * Calculate the Longest Proper Prefix which is also a Suffix (LPS) of the
     * pattern.
     * 
     * @param ba
     * @return
     */
    private int[] calculateLPS(byte[] ba) {
        int[] lps = new int[ba.length];
        int i = 1, j = 0;
        lps[0] = 0;
        while (i < ba.length) {
            if (ba[i] == ba[j]) {
                lps[i] = j + 1;
                i++;
                j++;
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}