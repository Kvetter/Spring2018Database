package DBMS;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static javax.script.ScriptEngine.FILENAME;

public class Hashmap_DB {

    private HashMap<String ,Long> map;
    private File file;
    private RandomAccessFile raf;

    /**
     * Constructer that creates the objects needed to store our data and read and write to a file
     * @throws IOException
     */
    public Hashmap_DB () throws IOException {
        file = new File(System.getProperty("user.dir") + "/src/DB/database");
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
                Long pointer = raf.getFilePointer();
                hashmap.put(raf.readLine().split(",")[0], pointer);
            }
            return hashmap;
        } else {
            return new HashMap<>();
        }
    }

    /**
     * We set the byte offset and reads the data from our database file
     * @param key
     * @return The value as a String
     * @throws IOException
     */
    public String dbRead(String key) throws IOException {
        // Setting the offset to the value, so we know exactly where our data is stored in the database file
        raf.seek(map.get(key));
        String line = raf.readLine();
        //Removing the key, so we only show the value
        return line.replaceAll(".*,", "");
    }

    /**
     * Saving the key and byte offset in our hashmap and saves both the key and value in the database file
     * @param key
     * @param value
     * @throws IOException
     */
    public void dbWrite (String key, String value) throws IOException {
        String data = key + "," + value;
        byte[] byteArray = data.getBytes(StandardCharsets.UTF_8);
        map.put(key, raf.getFilePointer());
        raf.write(data.getBytes());
        raf.writeBytes(System.getProperty("line.separator"));
    }

}
