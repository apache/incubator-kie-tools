package org.drools.guvnor.client.editors.test.generated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.editors.test.TestPresenter;
import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("Test")
//TODO {manstis} This class should be generated. See TestPlace.
public class TestActivity extends AbstractStaticScreenActivity
    implements
    StaticScreenService {

    @Inject
    private TestPresenter realPresenter;

    @Override
    public String getNameToken() {
        return "Test";
    }

    @Override
    public void onStart() {
        //This may do nothing if the real presenter does not have a @OnStart annotation
        realPresenter.onStart();
    }

    @Override
    public boolean mayClose() {
        //This may do nothing if the real presenter does not have a @MayClose annotation
        return realPresenter.mayClose();
    }

    @Override
    public void onClose() {
        //This may do nothing if the real presenter does not have a @OnClose annotation
        realPresenter.onClose();
    }

    @Override
    public void onReveal() {
        //This may do nothing if the real presenter does not have a @OnReveal annotation
        realPresenter.onReveal();
    }

    @Override
    public void onLostFocus() {
        //This may do nothing if the real presenter does not have a @OnLostFocus annotation
        realPresenter.onLostFocus();
    }

    @Override
    public void onFocus() {
        //This may do nothing if the real presenter does not have a @OnFocus annotation
        realPresenter.onFocus();
    }

    @Override
    public String getTitle() {
        //This may do nothing if the real presenter does not have a @Title annotation
        return realPresenter.getTitle();
    }

    @Override
    public IsWidget getWidget() {
        //This has to be implemented by the real presenter
        return realPresenter.getView();
    }

    @Override
    public StaticScreenService getPresenter() {
        return this;
    }

}
