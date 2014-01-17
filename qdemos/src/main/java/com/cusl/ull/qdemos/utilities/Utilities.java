package com.cusl.ull.qdemos.utilities;

/**
 * Created by Paco on 17/01/14.
 */
public class Utilities {

    public static String getCamelCase(String init){
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.equals("")) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }
}
