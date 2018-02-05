package DBMS;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class Hashmap_DB {

    private HashMap<String ,Long> map;
    private File file;
    private RandomAccessFile raf;

    /**
     * Constructer that creates the objects needed to store our data and read and write to a file
     * @throws IOException
     */
    public Hashmap_DB () throws IOException {
        file = new File(System.getProperty("user.dir") + "/DB/database");
        raf = new RandomAccessFile(file, "rw");
        map = createHashMap();
    }

    /**
     * Checks weather our database file exists and reads the data from that file while saving the key and
     * the byte offset in our hashmap
     * @return A hashmap, either generated with data from our database, or a new empty one
     * @throws IOException
     */
    public HashMap createHashMap() throws IOException {
        if (file.exists()) {
            HashMap<String, Long> hashmap = new HashMap<>();
            // A loop that reads a line from our file, splitting it so we only get the key
            // and getting the byte offset to store in our hashmap
            for (Long i = Long.valueOf(0); i < raf.length(); i = raf.getFilePointer()) {
                //String to store data from our file
                String keyBuilder = new String();
                //Getting the byte offset
                Long pointer = raf.getFilePointer();
                //Reading a line from our database, where we split on the byte value for a comma (,)
                String[] line = raf.readLine().split("00101100 ");
                //Parsing the byte value to a string so it can be read
                String[] keyData = line[0].split("\\s+");
                for (String key : keyData) {
                    keyBuilder += (char) Integer.parseInt(key, 2);
                }
                //Saving the key and byte offset in our hashmap
                hashmap.put(keyBuilder, pointer);
            }
            return hashmap;
        } else {
            return new HashMap<>();
        }
    }

    /**
     * We set the byte offset and reads the data from our database file
     * @param key
     * @return String
     * @throws IOException
     */
    public String dbRead(String key) throws IOException {
        // Setting the offset to the value, so we know exactly where our data is stored in the database file
        raf.seek(map.get(key));
        //Reads the data and splitting it on the byte value for a comma (,)
        String[] dataLine = raf.readLine().split("00101100 ");

        // Convert the binary string to a human readable value
        String data = new String();
        String[] valueData = dataLine[1].split("\\s+");
        for (String value : valueData) {
                data += (char) Integer.parseInt(value, 2);
        }
        //Removing the key, so we only show the value
        return data.toString().replaceAll(".*,", "");
    }

    /**
     * Saving the key and byte offset in our hashmap and saves both the key and value in the database file
     * @param key
     * @param value
     * @throws IOException
     */
    public void dbWrite (String key, String value) throws IOException {
        String data = key + "," + value;
        // Creating a byte array to store our key and value
        byte[] byteArray = data.getBytes(StandardCharsets.UTF_8);
        //Saving the key and byte offset in our hashmap
        map.put(key, raf.getFilePointer());
        // Convert the value to binary
        String byteData = "";
        for (byte b : byteArray) {
            byteData += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0').concat(" ");
        }
        //Saves the binary data in the db file
        raf.writeBytes(byteData);
        //Creating a newline in the db file
        raf.writeBytes(System.getProperty("line.separator"));
    }

}
