package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenEditor {

    public static final String PLACE_NAME_KEY = "Place Name";

    private SCREEN_TYPE type = SCREEN_TYPE.DEFAULT;

    private String externalComponentFQCN;

    private List<ScreenParameter> parameters = new ArrayList<ScreenParameter>();

    private String placeName;

    public ScreenEditor() {

    }

    public ScreenEditor( String screenName,
                         List<ScreenParameter> parameters ) {
        this.placeName = screenName;
        this.parameters = parameters;
    }

    public List<ScreenParameter> getParameters() {
        return parameters;
    }

    public HashMap<String, String> toParametersMap() {

        HashMap<String, String> parametersMap = new HashMap<String, String>();
        for ( ScreenParameter parameter : parameters ) {
            parametersMap.put( parameter.getKey(), parameter.getValue() );
        }
        return parametersMap;
    }

    public void addParameters( ScreenParameter param ) {
        if(param.getKey().equalsIgnoreCase( PLACE_NAME_KEY )){
            this.placeName = param.getValue();
        }
        else{
            parameters.add( param );
        }
    }

    public void setParameters( List<ScreenParameter> parameters ) {
        for ( ScreenParameter param : parameters ) {
            if(param.getKey().equalsIgnoreCase( PLACE_NAME_KEY )){
                this.placeName = param.getValue();
            }
            else{
                this.parameters = parameters;
            }
        }
    }

    public void setPlaceName( String placeName ) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setType( SCREEN_TYPE type ) {
        this.type = type;
    }

    public boolean isAExternalComponent() {
        return type == SCREEN_TYPE.EXTERNAL;
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
}
