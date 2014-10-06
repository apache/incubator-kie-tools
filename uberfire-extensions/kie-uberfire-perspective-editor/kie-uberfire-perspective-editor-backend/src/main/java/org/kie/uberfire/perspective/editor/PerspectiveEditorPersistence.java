package org.kie.uberfire.perspective.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorJSON;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorPersistenceAPI;

@Service
@ApplicationScoped
public class PerspectiveEditorPersistence implements PerspectiveEditorPersistenceAPI {

    private Gson gson;

    private static String lastJSON;

    @PostConstruct
    public void setup() {
        gsonFactory();
    }

    @Override
    public PerspectiveEditorJSON load( String perspectiveName ) {
        PerspectiveEditorJSON perspectiveEditorJSON = gson.fromJson( lastJSON, PerspectiveEditorJSON.class );
        //ederign load
        return perspectiveEditorJSON;
    }

    @Override
    public void save( PerspectiveEditorJSON perspective ) {
        //ederign save
        lastJSON = gson.toJson( perspective );
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

    }
}
