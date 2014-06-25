package org.kie.uberfire.social.activities.client;

import java.util.Date;

import org.junit.Test;
import org.kie.uberfire.social.activities.client.widgets.SocialDateFormatter;

import static org.junit.Assert.*;

public class SocialDateFormatterTest {

    private Date createDateFromNow( int daysToSubtract ) {
        Date today = new Date();
        int miliSecondsToSubtract =  daysToSubtract * 24 * 60 * 60 * 1000;
        Date targetDate = new Date( today.getTime() - miliSecondsToSubtract );
        return targetDate;
    }

    @Test
    public void printsToday() throws Exception {
        assertEquals( "today", SocialDateFormatter.format( new Date() ) );
    }

    @Test
    public void printsOneDayAgo() throws Exception {
        assertEquals( "1 day ago", SocialDateFormatter.format( createDateFromNow( 1 ) ) );
    }

    @Test
    public void printsTwoDayAgo() throws Exception {
        assertEquals( "2 days ago", SocialDateFormatter.format( createDateFromNow( 2 ) ) );
    }

    @Test
    public void printsOneWeekAgo() throws Exception {
        assertEquals( "1 week ago", SocialDateFormatter.format( createDateFromNow( 7 ) ) );
    }

    @Test
    public void printsOneWeekAgo2() throws Exception {
        assertEquals( "1 week ago", SocialDateFormatter.format( createDateFromNow( 8 ) ) );
    }

    @Test
    public void printsTwoWeekAgo1() throws Exception {
        assertEquals( "2 weeks ago", SocialDateFormatter.format( createDateFromNow( 14 ) ) );
    }

    @Test
    public void printsTwoWeekAgo() throws Exception {
        assertEquals( "2 weeks ago", SocialDateFormatter.format( createDateFromNow( 15 ) ) );
    }
}
