package org.uberfire.ext.plugin.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TagsConverterUtil {

    private static final String SEPARATOR = "|";
    public static final String LAYOUT_PROPERTY = "LAYOUT_TAGS";

    public static String convertTagsToString( List<String> tagsList ) {
        String tags = "";
        for ( String tag : tagsList ) {
            tags = tags + tag + SEPARATOR;
        }
        return tags;
    }

    public static List<String> convertTagStringToTag( String strTags ) {
        List<String> tags = new ArrayList<String>();
        if ( strTags != null && !strTags.isEmpty() ) {
            tags.addAll( Arrays.asList( strTags.split( "\\s*\\|\\s*" ) ) );
        }
        return tags;
    }

    public static List<String> extractTags( Map<String, String> layoutProperties ) {
        String tagsStr = layoutProperties.get( LAYOUT_PROPERTY );
        return convertTagStringToTag( tagsStr );
    }
}
