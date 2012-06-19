package org.drools.guvnor.client.editors.repositorieseditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("RepositoriesEditor")
public class RepositoriesEditorActivity extends AbstractStaticScreenActivity {

    @Inject
    private IOCBeanManager              iocManager;

    private RepositoriesEditorPresenter presenter;

    public RepositoriesEditorActivity() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( RepositoriesEditorPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "Repositories";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
