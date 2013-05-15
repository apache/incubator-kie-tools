/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.services.backend.metadata.attribute;

import java.util.HashMap;
import java.util.Map;

import static org.kie.commons.validation.Preconditions.checkNotEmpty;

/**
 *
 */
public final class OtherMetaAttributesUtil {

    private OtherMetaAttributesUtil() {
    }

    public static Map<String, Object> cleanup( final Map<String, Object> _attrs ) {
        final Map<String, Object> attrs = new HashMap<String, Object>( _attrs );

        for ( final String key : _attrs.keySet() ) {
            if ( key.startsWith( OtherMetaView.CATEGORY ) || key.equals( OtherMetaView.MODE ) ) {
                attrs.put( key, null );
            }
        }

        return attrs;
    }

    public static Map<String, Object> toMap( final OtherMetaAttributes attrs,
                                             final String... attributes ) {
        return new HashMap<String, Object>() {
            {
                for ( final String attribute : attributes ) {
                    checkNotEmpty( "attribute", attribute );

                    if ( attribute.equals( "*" ) || attribute.equals( OtherMetaView.CATEGORY ) ) {
                        for ( int i = 0; i < attrs.categories().size(); i++ ) {
                            put( buildAttrName( OtherMetaView.CATEGORY, i ), attrs.categories().get( i ) );
                        }
                    }
                    if ( attribute.equals( "*" ) ) {
                        break;
                    }
                }
            }
        };
    }

    private static String buildAttrName( final String title,
                                         final int i ) {
        return title + "[" + i + "]";
    }

}
