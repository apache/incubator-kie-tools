package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;

@Dependent
public class DependencySelectorPopup
        implements DependencySelectorPresenter {

    @Inject
    private DependencySelectorPopupView view;

    @Inject
    private Caller<M2RepoService> m2RepoService;

    private ArrayList<GAVSelectionHandler> selectionHandlers = new ArrayList<GAVSelectionHandler>();

    @AfterInitialization
    public void init() {
        view.init( this );
    }

    public void show() {
        view.show();
    }

    @Override
    public void onPathSelection( String pathToDependency ) {
        m2RepoService.call( new RemoteCallback<GAV>() {
            @Override
            public void callback( GAV gav ) {
                for ( GAVSelectionHandler handler : selectionHandlers ) {
                    handler.onSelection( gav );
                }
            }
        } ).loadGAVFromJar( pathToDependency );

        view.hide();
    }

    public void addSelectionHandler( GAVSelectionHandler selectionHandler ) {
        selectionHandlers.add( selectionHandler );
    }
}
