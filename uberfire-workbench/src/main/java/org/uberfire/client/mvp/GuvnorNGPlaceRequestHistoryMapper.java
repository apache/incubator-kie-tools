package org.uberfire.client.mvp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.http.client.URL;

import org.uberfire.shared.mvp.PlaceRequest;

public class GuvnorNGPlaceRequestHistoryMapper
    implements
    PlaceRequestHistoryMapper {

    @Override
    public PlaceRequest getPlaceRequest(String fullIdentifier) {
        String identifier = fullIdentifier.indexOf( "?" ) != -1 ? fullIdentifier.substring( 0,
                                                                                            fullIdentifier.indexOf( "?" ) ) : fullIdentifier;
        String query = fullIdentifier.indexOf( "?" ) != -1 ? fullIdentifier.substring( fullIdentifier.indexOf( "?" ) + 1 ) : "";
        Map<String, String> parameters = getParameters( query );

        PlaceRequest placeRequest = new PlaceRequest( identifier );
        for ( String parameterName : parameters.keySet() ) {
            placeRequest.addParameter( parameterName,
                                       parameters.get( parameterName ) );
        }

        return placeRequest;
    }

    @Override
    public String getIdentifier(PlaceRequest placeRequest) {
        StringBuilder token = new StringBuilder();
        token.append( placeRequest.getIdentifier() );

        if ( placeRequest.getParameterNames().size() > 0 ) {
            token.append( "?" );
        }
        for ( String name : placeRequest.getParameterNames() ) {
            token.append( name ).append( "=" )
                    .append( placeRequest.getParameter( name,
                                                        null ) );
            token.append( "&" );
        }

        if ( token.length() != 0 && token.lastIndexOf( "&" ) + 1 == token.length() ) {
            token.deleteCharAt( token.length() - 1 );
        }

        return token.toString();
    }

    private static Map<String, String> getParameters(String query) {
        Map<String, String> parameters = new HashMap<String, String>();

        if ( query != null && !"".equalsIgnoreCase( query ) ) {
            List<String> parts = Arrays.asList( query.split( "&" ) );
            for ( String part : parts ) {
                int index = part.indexOf( '=' );
                String name = null;
                String value = null;
                if ( index == -1 ) {
                    name = part;
                    value = "";
                } else {
                    name = part.substring( 0,
                                           index );
                    value = index < part.length() ? part.substring( index + 1 ) : "";
                    value = urlDecode( value );
                }
                parameters.put( urlDecode( name ),
                                value );
            }
        }

        return parameters;
    }

    private static String urlDecode(String value) {
        return URL.decode( value );
    }
}
