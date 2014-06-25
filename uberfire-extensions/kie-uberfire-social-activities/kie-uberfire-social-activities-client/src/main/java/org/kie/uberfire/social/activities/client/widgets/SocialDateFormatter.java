package org.kie.uberfire.social.activities.client.widgets;

import java.util.Date;

public class SocialDateFormatter {

    public static String format( Date date ) {
        int diffInDays = diffInDaysFromNow( date );

        if ( diffInDays < 7 ) {
            return formatInDays( diffInDays );
        } else {
            return formatInWeeks( diffInDays );
        }

    }

    private static int diffInDaysFromNow( Date date ) {
        int diffInDays = (int) ( ( new Date().getTime() - date.getTime() )
                / ( 1000 * 60 * 60 * 24 ) );
        return Math.abs( diffInDays );
    }

    private static String formatInWeeks( int diffInDays ) {
        int numberOfWeeks = diffInDays / 7;
        if ( numberOfWeeks == 1 || numberOfWeeks == 0 ) {
            return "1 week ago";
        } else {
            return numberOfWeeks + " weeks ago";
        }
    }

    private static String formatInDays( int diffInDays ) {
        if ( diffInDays == 0 ) {
            return "today";
        } else if ( diffInDays == 1 ) {
            return diffInDays + " day ago";
        } else {
            return diffInDays + " days ago";
        }
    }

}
