package KMP;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import DataStruct.PatternSearch;
import FileOp.FileOp;

public class KMP implements PatternSearch {

    private int[] list;
    private int numberOfPatterns = 0;
    private byte[] bytePattern;

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