package org.broadleafcommerce.broadleafxmlmigrator.helper;

import java.util.Map;

/**
 * Helper class for various logging things
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class LoggingHelper {

    /**
     * Given a map that represents the mapping of old xml xpaths to their new equivalent after migration, this method
     * will build a rudimentary table using the given StringBuilder
     * 
     * @param fileName
     * @param buffer
     * @param beanMap
     */
    public static void printBeanChanges(String fileName, StringBuilder buffer, Map<String, String> beanMap) {
        if (beanMap.keySet().size() == 0) {
            return;
        }
        buffer.append("Beans that were affected in file : " + fileName + "\n");
        int maxKeyLength = -1;
        for (String key : beanMap.keySet()) {
            if (key.length() > maxKeyLength) {
                maxKeyLength = key.length();
            }
        }
        buffer.append("Old Xpath");
        for (int i = 0 ; i < (maxKeyLength - "Old Xpath".length()); i++) {
            buffer.append(" ");
        }
        buffer.append(" :: New Bean Id\n");
        for (String key : beanMap.keySet()) {
            buffer.append(key);
            for (int i = 0; i < (maxKeyLength - key.length()); i++) {
                buffer.append(" ");
            }
            buffer.append(" :: " + beanMap.get(key) + "\n");
        }
        buffer.append("\n");
        for (int i = 0; i < 100; i++) {
            buffer.append("-");
        }
    }
}
