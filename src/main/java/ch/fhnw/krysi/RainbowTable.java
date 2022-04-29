package ch.fhnw.krysi;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RainbowTable {

    private final int                 passwordLength;
    private final int                 chainLength;
    private final char[]              reductionArray;
    private final Map<String, String> table;

    /**
     * Constructor with Parameters to run the RainbowTable, after
     *
     * @param passwordLength int
     * @param chainLength    int
     * @param reductionArray char[]
     */
    public RainbowTable(int passwordLength, int chainLength, char[] reductionArray) {
        this.passwordLength = passwordLength;
        this.chainLength = chainLength;
        this.reductionArray = reductionArray;
        this.table = new HashMap<>();

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Length of Passwords: " + passwordLength);
        System.out.println("Length of chains: " + chainLength);
        System.out.println("Reduction Array: " + Arrays.toString(reductionArray));
    }

    /**
     * Fill the RainbowTable Map with the passwords from the given reductionArray (Works just because ist all
     * lowercase letters and numbers. If this does not cover the password policy, given to crack, then this
     * should be changed)
     */
    public void fillTable() {
        for (int i = 0; i < chainLength; i++) {
            int    j      = 0;
            String source = createTablePassword(i);
            String result = source;
            while (j < chainLength) {
                String hashed = createMD5(result);
                result = reduction(hashed, j);
                j++;
            }
            table.put(result, source);
            i++;
        }
    }

    /**
     * Create Passwords to fill the Table
     *
     * @param num int
     * @return String
     */
    private String createTablePassword(int num) {
        StringBuilder s = new StringBuilder("0000000");
        for (int pos = passwordLength - 1; pos >= 0 && num > 0; pos--) {
            char digit = reductionArray[num % reductionArray.length];
            s.setCharAt(pos, digit);
            num = num / reductionArray.length;
        }
        return s.toString();
    }

    /**
     * We are going to search the hash aka needle in the haystack
     *
     * @param needle String
     * @return String
     */
    public String lookingForHashInTable(String needle) {
        if (table.size() > 0) {
            for (int i = chainLength; i > -1; i--) {
                String temp = needle;
                int    step = i;
                while (step < chainLength - 1) {
                    temp = reduction(temp, step);
                    temp = createMD5(temp);
                    step++;
                }

                temp = reduction(temp, chainLength - 1);

                if (table.containsKey(temp)) {
                    String possiblePassword = table.get(temp);
                    for (int p = 0; p < i; p++) {
                        possiblePassword = createMD5(possiblePassword);
                        possiblePassword = reduction(possiblePassword, p);
                    }
                    return "Password for hash [" + needle + "] ==> " + possiblePassword;
                }
            }
            return "No Password found for hash [" + needle + "]";
        } else {
            throw new RuntimeException(" You have to initialize the rainbow table first. Call fillTable() before this function");
        }
    }

    /**
     * Use the Reduction Function from Page 3.27
     *
     * @param hash String
     * @param step int
     * @return String
     */
    public String reduction(String hash, int step) {
        StringBuilder reduction = new StringBuilder();

        char[]     z       = reductionArray; // Possible characters
        BigInteger zLength = BigInteger.valueOf(z.length); // length of possible Characters
        BigInteger h       = new BigInteger(hash, 16); // given hash value
        BigInteger stufe   = BigInteger.valueOf(step); // turn the integer from step into a BigInteger

        h = h.add(stufe); // add the step to the hash

        // Run the algo from 3.27
        for (int i = 0; i < passwordLength; i++) {
            BigInteger r = h.mod(zLength);
            h = h.divide(zLength);
            reduction.insert(0, z[r.intValue()]);
        }
        return reduction.toString();
    }


    /**
     * Calculate MD5 Hash from a given String. Derived from Geeks for Geeks
     *
     * @param toMD5 String
     * @return String
     * @link <a href="https://www.geeksforgeeks.org/md5-hash-in-java/">https://www.geeksforgeeks.org/md5-hash-in-java/</a>
     */
    public String createMD5(String toMD5) {
        try {
            MessageDigest md         = MessageDigest.getInstance("MD5");
            byte[]        inputBytes = md.digest(toMD5.getBytes());
            BigInteger    signum     = new BigInteger(1, inputBytes);
            return signum.toString(16);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
