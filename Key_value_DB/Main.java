import DBMS.Hashmap_DB;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Hashmap_DB db = new Hashmap_DB();
        db.dbWrite("test", "testdata");
        System.out.println(db.dbRead("test"));
        if(args.length == 1) {
            System.out.println(db.dbRead(args[0]));
        } else if (args.length == 2) {
            db.dbWrite(args[0], args[1]);
        }
    }
}
