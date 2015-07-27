package org.uberfire.ext.layout.editor.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Service
@ApplicationScoped
public class LayoutServicesImpl implements LayoutServices {

    private Gson gson;

    @PostConstruct
    public void init() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public String convertLayoutToString( LayoutTemplate layoutTemplate) {

        String perspectiveContent = gson.toJson(layoutTemplate);

        return perspectiveContent;
    }

    @Override
    public LayoutTemplate convertLayoutFromString( String layoutEditorModel ) {

        LayoutTemplate layoutTemplate = gson.fromJson( layoutEditorModel, LayoutTemplate.class );
        return layoutTemplate;
    }
}