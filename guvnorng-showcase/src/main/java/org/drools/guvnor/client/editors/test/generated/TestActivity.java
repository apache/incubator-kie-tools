package org.drools.guvnor.client.editors.test.generated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("Test")
//TODO {manstis} This class should be generated. See TestPlace.
public class TestActivity extends AbstractStaticScreenActivity {

    @Inject
    private TestPresenterProxy presenter;

    @Override
    public StaticScreenService getPresenter() {
        return this.presenter;
    }

    @Override
    public String getTitle() {
        return presenter.getTitle();
    }

    @Override
    public String getNameToken() {
        return "Test";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.getView();
    }

}
