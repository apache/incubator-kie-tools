package org.uberfire.ext.layout.editor.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;

@Service
@ApplicationScoped
public class LayoutServicesImpl implements LayoutServices {

    private Gson gson;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String convertLayoutToString( LayoutEditor layoutEditor ) {

        String perspectiveContent = gson.toJson( layoutEditor );

        return perspectiveContent;
    }

    @Override
    public LayoutEditor convertLayoutFromString( String layoutEditorModel ) {

        LayoutEditor layoutEditor = gson.fromJson( layoutEditorModel, LayoutEditor.class );
        return layoutEditor;
    }
}