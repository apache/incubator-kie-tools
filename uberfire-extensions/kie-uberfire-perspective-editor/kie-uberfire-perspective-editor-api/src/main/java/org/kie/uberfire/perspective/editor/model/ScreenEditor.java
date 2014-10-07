package org.kie.uberfire.perspective.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ScreenEditor {


    private List<ScreenParameter> parameters = new ArrayList<ScreenParameter>(  );

    public ScreenEditor() {

    }

    public ScreenEditor( List<ScreenParameter> parameters ) {
        this.parameters = parameters;
    }

    public List<ScreenParameter> getParameters() {
        return parameters;
    }
}
