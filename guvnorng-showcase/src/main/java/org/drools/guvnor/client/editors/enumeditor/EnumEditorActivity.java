package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.SupportedFormat;
import org.drools.guvnor.client.mvp.AbstractEditorActivity;
import org.drools.guvnor.client.mvp.EditorService;
import org.drools.guvnor.client.mvp.NameToken;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("EnumEditor")
@SupportedFormat("enumeration")
public class EnumEditorActivity extends AbstractEditorActivity {

    @Inject
    private IOCBeanManager      iocManager;

    private EnumEditorPresenter presenter;

    public EnumEditorActivity() {
    }

    @Override
    public EditorService getPresenter() {
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
