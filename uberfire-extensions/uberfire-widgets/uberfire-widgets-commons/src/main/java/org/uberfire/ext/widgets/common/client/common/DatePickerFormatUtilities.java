/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.widgets.common.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Utilties to handle conversion between GWT's DateFormat to BS3's DateFormat
 */
public class DatePickerFormatUtilities {

    private static final String DEFAULT = "dd-M-yyyy";

    private static class Mapping {

        private final String src;
        private final String tgt;

        Mapping( final String src,
                 final String tgt ) {
            this.src = src;
            this.tgt = tgt;
        }
    }

    private static List<Mapping> mappings = new ArrayList<Mapping>() {{
        add( new Mapping( "yyyy", "yyyy" ) );
        add( new Mapping( "yy", "yy" ) );
        add( new Mapping( "MMMM", "MM" ) );
        add( new Mapping( "MMM", "M" ) );
        add( new Mapping( "MM", "mm" ) );
        add( new Mapping( "M", "m" ) );
        add( new Mapping( "dd", "dd" ) );
        add( new Mapping( "d", "d" ) );
        add( new Mapping( "HH", "hh" ) );
        add( new Mapping( "H", "h" ) );
        add( new Mapping( "hh", "HH" ) );
        add( new Mapping( "h", "H" ) );
        add( new Mapping( "mm", "ii" ) );
        add( new Mapping( "m", "i" ) );
        add( new Mapping( "ss", "ss" ) );
        add( new Mapping( "s", "s" ) );
        add( new Mapping( "G", null ) );
        add( new Mapping( "L", null ) );
        add( new Mapping( "S", null ) );
        add( new Mapping( "E", null ) );
        add( new Mapping( "c", null ) );
        add( new Mapping( "a", "p" ) );
        add( new Mapping( "k", null ) );
        add( new Mapping( "K", null ) );
        add( new Mapping( "z", null ) );
        add( new Mapping( "Z", null ) );
        add( new Mapping( "v", null ) );
    }};

    /**
     * BS3's DateFormat constants:
     * @see org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.HasFormat
     * <p/>
     * The date format, combination of p, P, h, hh, i, ii, s, ss, d, dd, m, mm, M, MM, yy, yyyy.
     * p    meridian in lower case ('am' or 'pm') - according to locale file
     * P    meridian in upper case ('AM' or 'PM') - according to locale file
     * s    seconds without leading zeros
     * ss   seconds, 2 digits with leading zeros
     * i    minutes without leading zeros
     * ii   minutes, 2 digits with leading zeros
     * h    hour without leading zeros - 24-hour format
     * hh   hour, 2 digits with leading zeros - 24-hour format
     * H    hour without leading zeros - 12-hour format
     * HH   hour, 2 digits with leading zeros - 12-hour format
     * d    day of the month without leading zeros
     * dd   day of the month, 2 digits with leading zeros
     * m    numeric representation of month without leading zeros
     * mm   numeric representation of the month, 2 digits with leading zeros
     * M    short textual representation of a month, three letters
     * MM   full textual representation of a month, such as January or March
     * yy   two digit representation of a year
     * yyyy full numeric representation of a year, 4 digits
     * <p/>
     * GWT's DateFormat constants:
     * @see com.google.gwt.i18n.client.DateTimeFormat
     * <p/>
     * G	era designator	Text	AD
     * y	year	Number	1996
     * L	standalone month in year	Text or Number	July (or) 07
     * M	month in year	Text or Number	July (or) 07
     * d	day in month	Number	10
     * h	hour in am/pm (1-12)	Number	12
     * H	hour in day (0-23)	Number	0
     * m	minute in hour	Number	30
     * s	second in minute	Number	55
     * S	fractional second	Number	978
     * E	day of week	Text	Tuesday
     * c	standalone day of week	Text	Tuesday
     * a	am/pm marker	Text	PM
     * k	hour in day (1-24)	Number	24
     * K	hour in am/pm (0-11)	Number	0
     * z	time zone	Text	Pacific Standard Time(see comment)
     * Z	time zone (RFC 822)	Text	-0800(See comment)
     * v	time zone id	Text	America/Los_Angeles(See comment)
     * '	escape for text	Delimiter	'Date='
     * ''	single quote	Literal	'o''clock'
     **/
    public static String convertToBS3DateFormat( final String gwtDateFormat ) {
        if ( !isValidGWTDateFormat( gwtDateFormat ) ) {
            return DEFAULT;
        }
        
        final StringBuilder src = new StringBuilder( gwtDateFormat );
        final StringBuilder tgt = new StringBuilder();
        int idx = 0;
        while ( idx < gwtDateFormat.length() ) {
            boolean mapped = false;
            for ( Mapping mapping : mappings ) {
                if ( src.indexOf( mapping.src ) == 0 ) {
                    if ( mapping.tgt != null ) {
                        tgt.append( mapping.tgt );
                    }
                    src.delete( 0, mapping.src.length() );
                    idx = idx + mapping.src.length() - 1;
                    mapped = true;
                    break;
                }
            }
            if ( !mapped ) {
                tgt.append( src.charAt( 0 ) );
                src.deleteCharAt( 0 );
            }
            idx++;
        }
        return tgt.toString();
    }

    private static boolean isValidGWTDateFormat( final String gwtDateFormat ) {
        try {
            DateTimeFormat.getFormat( gwtDateFormat );

        } catch ( IllegalArgumentException iae ) {
            return false;
        }
        return true;
    }

}
