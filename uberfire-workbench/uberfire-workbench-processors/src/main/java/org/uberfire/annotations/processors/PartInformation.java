package org.uberfire.annotations.processors;

import java.util.LinkedHashMap;
import java.util.Map;

public class PartInformation {

    private final String partName;
    private final Map<String, String> parameters;

    public PartInformation( CharSequence partNameAndParams ) {
        parameters = new LinkedHashMap<String, String>();

        StringBuilder nextToken = new StringBuilder( 50 );
        String foundPartName = null;
        String key = null;
        for ( int i = 0; i < partNameAndParams.length(); i++ ) {
            char ch = partNameAndParams.charAt( i );
            switch ( ch ) {
                case '%':
                    StringBuilder hexVal = new StringBuilder( 2 );
                    hexVal.append( partNameAndParams.charAt( i + 1 ) );
                    hexVal.append( partNameAndParams.charAt( i + 2 ) );
                    nextToken.append( (char) Integer.parseInt( hexVal.toString(), 16 ) );
                    i += 2;
                    break;

                case '?':
                    if ( foundPartName == null ) {
                        foundPartName = nextToken.toString();
                        nextToken = new StringBuilder( 50 );
                    } else {
                        nextToken.append( '?' );
                    }
                    break;

                case '=':
                    if ( foundPartName == null ) {
                        nextToken.append( '=' );
                    } else {
                        key = nextToken.toString();
                        nextToken = new StringBuilder( 50 );
                    }
                    break;

                case '&':
                    parameters.put( key, nextToken.toString() );
                    nextToken = new StringBuilder( 50 );
                    key = null;
                    break;

                default:
                    nextToken.append( ch );
            }
        }

        if ( foundPartName == null ) {
            foundPartName = nextToken.toString();
        } else if ( key != null ) {
            parameters.put( key, nextToken.toString() );
        } else if ( nextToken.length() > 0 ) {
            parameters.put( nextToken.toString(), "" );
        }

        this.partName = foundPartName;
    }

    public String getPartName() {
        return partName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
