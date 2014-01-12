package org.brickred.socialauth;

import java.util.Properties;

/**
 * @author Romain Laboisse labrom@gmail.com
 */
public interface SessionProperties {

    public class Prefix {
        public static String withNs(String ns, String key) {
            if(ns != null)
                return ns + "." + key;
            return key;
        }
    }

    void read(String ns, Properties p);
    void write(String ns, Properties p);
}
