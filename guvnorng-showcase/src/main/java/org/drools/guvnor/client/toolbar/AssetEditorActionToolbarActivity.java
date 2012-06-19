package org.drools.guvnor.client.toolbar;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("Toolbar")
public class AssetEditorActionToolbarActivity extends AbstractStaticScreenActivity {

    @Inject
    private IOCBeanManager                    manager;

    private AssetEditorActionToolbarPresenter presenter;

    public AssetEditorActionToolbarActivity() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = manager.lookupBean( AssetEditorActionToolbarPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "Toolbar";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
