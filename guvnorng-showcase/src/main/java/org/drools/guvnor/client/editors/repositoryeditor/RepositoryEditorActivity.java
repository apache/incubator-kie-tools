package org.drools.guvnor.client.editors.repositoryeditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("RepositoryEditor")
public class RepositoryEditorActivity extends AbstractStaticScreenActivity {

    @Inject
    private IOCBeanManager            iocManager;
    
    @Inject
    private PlaceManager        placeManager;
    
    private RepositoryEditorPresenter presenter;

    public RepositoryEditorActivity() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( RepositoryEditorPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String repositoryName = placeRequest.getParameter( "repositoryName",
                                                          "RepositoryEditor" );
        return repositoryName;
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
