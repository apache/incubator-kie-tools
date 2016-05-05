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
