package aims.scraper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Test {

    public static void main(String[] args) {

        Map<String, String> map = new HashMap();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        System.out.println(iterator.hasNext());
    }
}
