package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenEditorJSON {


    private List<ScreenParameter> parameters = new ArrayList<ScreenParameter>(  );

    public ScreenEditorJSON() {

    }

    public ScreenEditorJSON( List<ScreenParameter> parameters ) {
        this.parameters = parameters;
    }

    public List<ScreenParameter> getParameters() {
        return parameters;
    }
}
