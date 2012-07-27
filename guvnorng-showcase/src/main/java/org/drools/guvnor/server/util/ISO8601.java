package org.drools.guvnor.server.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ISO8601 {

    /**
     * This turns a Date into a String following the ISO8601 specification.
     *
     * @param date
     * @return
     */
    public static String format( Calendar cal ) {
        SimpleDateFormat ISO8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String text = null;
        if (cal!=null) {
            Date date = cal.getTime();
            text = ISO8601Format.format(date);
            if (text.length() < 29) {
                //add the colon if it is not there.
                text = text.substring(0, 26) + ":" + text.substring(26);
            }
        }
        return text;
    }
}
