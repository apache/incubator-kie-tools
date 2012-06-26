package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractEditorScreenActivity;
import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.workbench.annotations.SupportedFormat;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("EnumEditor")
@SupportedFormat(".enumeration")
public class EnumEditorActivity extends AbstractEditorScreenActivity {

    @Inject
    private IOCBeanManager      iocManager;

    private EnumEditorPresenter presenter;

    public EnumEditorActivity() {
    }

    @Override
    public EditorScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( EnumEditorPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "EnumEditor";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }
}
