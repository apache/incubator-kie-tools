package org.drools.guvnor.client.editors.admin1;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("MyAdminArea")
//TODO {manstis} This should not need to re-implement Activity but Errai doesn't detect it if it doesn't
public class MyAdminAreaActivity extends AbstractStaticScreenActivity
    implements
    Activity {

    @Inject
    private IOCBeanManager       iocManager;

    private MyAdminAreaPresenter presenter;

    public MyAdminAreaActivity() {
    }

    @Override
    public StaticScreenService getPresenter() {
        this.presenter = iocManager.lookupBean( MyAdminAreaPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return "MyAdminArea";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
