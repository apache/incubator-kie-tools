package org.drools.guvnor.client.editors.admin2;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("MyAdminArea2")
public class MyAdminAreaActivity2 extends AbstractStaticScreenActivity {

    @Inject
    private IOCBeanManager        iocManager;

    private MyAdminAreaPresenter2 presenter;

    public MyAdminAreaActivity2() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( MyAdminAreaPresenter2.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "MyAdminArea2";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }
}
