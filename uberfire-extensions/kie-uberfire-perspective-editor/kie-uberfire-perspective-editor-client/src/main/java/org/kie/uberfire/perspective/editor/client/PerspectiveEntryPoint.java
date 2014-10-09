package org.kie.uberfire.perspective.editor.client;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.uberfire.perspective.editor.client.generator.PerspectiveEditorGenerator;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditorPersistenceAPI;
import org.uberfire.client.workbench.Workbench;

@EntryPoint
public class PerspectiveEntryPoint {

    @Inject
    Workbench workbench;

    @Inject
    PerspectiveEditorGenerator perspectiveEditorGenerator;

    @Inject
    private Caller<PerspectiveEditorPersistenceAPI> remoteService;

    @PostConstruct
    public void init() {
        workbench.addStartupBlocker( this.getClass() );
    }

    @AfterInitialization
    public void setup() {
        remoteService.call( new RemoteCallback<List<PerspectiveEditor>>() {
            public void callback( List<PerspectiveEditor> editors ) {
                for ( PerspectiveEditor editor : editors ) {
                    perspectiveEditorGenerator.generate( editor );
                }
                workbench.removeStartupBlocker( PerspectiveEntryPoint.this.getClass() );
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( Object o,
                                  Throwable throwable ) {
                workbench.removeStartupBlocker( PerspectiveEntryPoint.this.getClass() );
                return false;
            }
        } ).loadAll();
    }

}
