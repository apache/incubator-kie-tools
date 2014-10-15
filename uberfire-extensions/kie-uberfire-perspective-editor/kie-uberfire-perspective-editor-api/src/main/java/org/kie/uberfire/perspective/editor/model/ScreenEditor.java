package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenEditor {

    public static final String SCREEN_NAME = "Screen Name";

    private List<ScreenParameter> parameters = new ArrayList<ScreenParameter>(  );

    private String screenName;

    public ScreenEditor() {

    }

    public ScreenEditor( String screenName, List<ScreenParameter> parameters ) {
        this.screenName = screenName;
        this.parameters = parameters;
    }

    public List<ScreenParameter> getParameters() {
        return parameters;
    }

    public void setScreenName( String screenName ) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }


    public void setParameters( List<ScreenParameter> parameters ) {
        this.parameters = parameters;
    }
}
