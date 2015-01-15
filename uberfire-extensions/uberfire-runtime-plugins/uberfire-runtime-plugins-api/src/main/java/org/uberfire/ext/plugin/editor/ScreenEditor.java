package org.uberfire.ext.plugin.editor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenEditor {

    public static final String PLACE_NAME_KEY = "Place Name";

    private SCREEN_TYPE type = SCREEN_TYPE.DEFAULT;

    private String externalComponentFQCN;

    private Map<String, String> parameters = new HashMap<String, String>();

    private Map<String, String> lastParametersSaved = new HashMap<String, String>();

    public ScreenEditor() {
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addParameters( String key,
                               String value ) {
        parameters.put( key, value );
    }

    public void removeParameter( final String key ) {
        parameters.remove( key );
    }

    public void setParameterValue( final String key,
                                   final String value ) {
        parameters.put( key, value );
    }

    public void setPlaceName( String placeName ) {
        parameters.put( PLACE_NAME_KEY, placeName );
    }

    public String getPlaceName() {
        return parameters.get( PLACE_NAME_KEY );
    }

    public void setType( SCREEN_TYPE type ) {
        this.type = type;
    }

    public boolean isAExternalComponent() {
        return type == SCREEN_TYPE.EXTERNAL;
    }

    public void saveOriginalState() {
        lastParametersSaved = new HashMap<String, String>();
        for ( String key : parameters.keySet() ) {
            lastParametersSaved.put( key, parameters.get( key ) );
        }
    }

    public void loadOriginalState(){
        if(!lastParametersSaved.isEmpty()){
            parameters = new HashMap<String, String>(  );
            for ( String key : lastParametersSaved.keySet() ) {
                parameters.put( key, lastParametersSaved.get( key ) );
            }
        }
    }

    public enum SCREEN_TYPE {
        DEFAULT, EXTERNAL
    }

    public String getExternalComponentFQCN() {
        return externalComponentFQCN;
    }

    public void setExternalComponentFQCN( String externalComponentFQCN ) {
        this.externalComponentFQCN = externalComponentFQCN;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ScreenEditor ) ) {
            return false;
        }

        ScreenEditor that = (ScreenEditor) o;

        if ( externalComponentFQCN != null ? !externalComponentFQCN.equals( that.externalComponentFQCN ) : that.externalComponentFQCN != null ) {
            return false;
        }
        if ( parameters != null ? !parameters.equals( that.parameters ) : that.parameters != null ) {
            return false;
        }

        if ( type != that.type ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + ( externalComponentFQCN != null ? externalComponentFQCN.hashCode() : 0 );
        result = 31 * result + ( parameters != null ? parameters.hashCode() : 0 );
        return result;
    }
}
