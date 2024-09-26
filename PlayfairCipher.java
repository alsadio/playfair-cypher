import java.util.HashMap;
import java.util.Scanner;

public class PlayfairCipher {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter a key string: (all caps)");
        String keyString = in.nextLine();
        System.out.println("Enter a plaintext: (enter END when you are done)");
        String plainText = in.useDelimiter("END").next();

        // Generate the Playfair key table
        String[][] key = generateKey(keyString);

        // Encrypt and decrypt the plaintext
        String encrypted = encrypt(plainText, key);
        String decrypted = decrypt(encrypted, key);

        // Print the results
        System.out.println("CipherText: "+ encrypted);
        System.out.println("PlainText: "+decrypted);
    }


    public static String[][] generateKey(String str){
        // Create a 5x5 key table
        String[][] result = new String[5][5];

        // Start with 'A' for filling the key table
        char temp = 'A';

        // Remove spaces and duplicates from the key string
        str = str.replace(" ", "");
        str = removeDuplicates(str);

        // Fill the table: Skip if the letter is already in the key string or if the letter is 'J'
        for (int i=0; i<26; i++){
            if(str.contains(Character.toString(temp)) || temp == 'J'){
                temp++;
                continue;
            }
            str += temp;
            temp++;
        }

        // Populate the key table from the modified key string
        int stringIndex = 0;
        for (int i=0; i< result.length; i++){
            for (int j=0; j<result[0].length; j++){
                result[i][j] = Character.toString(str.charAt(stringIndex++));
            }
        }
        return result;
    }
    public static String removeDuplicates(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            // Add to the result string only if the character is encountered for the first time
            if (str.indexOf(c) == i) {
                result += c;
            }
        }
        return result;
    }
    public static String encrypt(String plainText, String[][] key){
        // Preprocessing the plaintext
        plainText = plainText.replace(" ","");
        plainText = plainText.replace(",", "");
        plainText = plainText.replace("\n", "");
        plainText = plainText.replace(".", "");
        plainText = plainText.replace("J", "I"); // Handle 'J'
        plainText = plainText.replaceAll("[^A-Z]", ""); // Keep only uppercase letters
        plainText = plainText.replaceAll("(.)\\1+", "$1X$1"); // Insert 'X' between duplicates
        if (plainText.length() % 2 != 0) {
            plainText += "Z"; // Ensure even length
        }

        // Generate the map for character lookup
        HashMap<String, Integer[]> map = generateMap(plainText, key);

        // Build the ciphertext
        StringBuilder ciphertext = new StringBuilder();
        for (int i = 0; i < plainText.length(); i += 2) {
            String pair = plainText.substring(i, i + 2);
            Integer[] pos1 = map.get(pair.substring(0, 1));
            Integer[] pos2 = map.get(pair.substring(1));

            ciphertext.append(applyPlayfairRules(pos1, pos2, key));
        }

        return ciphertext.toString();
    }


    public static String decrypt(String ciphertext, String[][] key) {
        HashMap<String, Integer[]> map = generateMap(ciphertext, key); // Use the same map for consistency
        StringBuilder plaintext = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i += 2) {
            String pair = ciphertext.substring(i, i + 2);
            Integer[] pos1 = map.get(pair.substring(0, 1));
            Integer[] pos2 = map.get(pair.substring(1));

            plaintext.append(applyPlayfairRulesForDecryption(pos1, pos2, key));
        }

        // Handle potential 'X' and odd-length cases (explained below)
        return plaintext.toString();
    }

    public static HashMap<String, Integer[]> generateMap(String str, String[][] key){
        HashMap<String, Integer[]> map = new HashMap<>();
        for(int i=0; i<str.length();i++){
            Integer[] position = new Integer[2];
            for (int row =0; row<key.length; row++){
                for (int col = 0; col<key[0].length; col++){
                    if(str.substring(i, i+1).equals(key[row][col])){
                        position[0] = row;
                        position[1] = col;
                        map.put(str.substring(i, i+1), position);
                    }
                }
            }
        }
        return map;
    }

    public static String applyPlayfairRules(Integer[] pos1, Integer[] pos2, String[][] keyTable) {
        StringBuilder result = new StringBuilder();
        // Same row
        if (pos1[0].equals(pos2[0])) {
            result.append(keyTable[pos1[0]][(pos1[1] + 1) % 5]);
            result.append(keyTable[pos2[0]][(pos2[1] + 1) % 5]);
            // Same column
        } else if (pos1[1].equals(pos2[1])) {
            result.append(keyTable[(pos1[0] + 1) % 5][pos1[1]]);
            result.append(keyTable[(pos2[0] + 1) % 5][pos2[1]]);
            // Different row and column
        } else {
            result.append(keyTable[pos1[0]][pos2[1]]);
            result.append(keyTable[pos2[0]][pos1[1]]);
        }

        return result.toString();
    }
    public static String applyPlayfairRulesForDecryption(Integer[] pos1, Integer[] pos2, String[][] keyTable) {
        StringBuilder result = new StringBuilder();
        // Same row
        if (pos1[0].equals(pos2[0])) {
            // Shift left
            result.append(keyTable[pos1[0]][(pos1[1] - 1 + 5) % 5]);
            // Shift left
            result.append(keyTable[pos2[0]][(pos2[1] - 1 + 5) % 5]);
            // Same column
        } else if (pos1[1].equals(pos2[1])) {
            // Shift up
            result.append(keyTable[(pos1[0] - 1 + 5) % 5][pos1[1]]);
            // Shift up
            result.append(keyTable[(pos2[0] - 1 + 5) % 5][pos2[1]]);
            // Different row and column
        } else {
            result.append(keyTable[pos1[0]][pos2[1]]);
            result.append(keyTable[pos2[0]][pos1[1]]);
        }

        return result.toString();
    }
}