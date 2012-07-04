package org.drools.guvnor.client.editors.test3.generated;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.SupportedFormat;
import org.drools.guvnor.client.editors.test3.TestPresenter3;
import org.drools.guvnor.client.mvp.AbstractEditorScreenActivity;
import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("Test3")
@SupportedFormat("test3")
//TODO {manstis} This class should be generated. See TestPlace3.
public class TestActivity3 extends AbstractEditorScreenActivity
    implements
    EditorScreenService {

    @Inject
    private TestPresenter3 realPresenter;

    @Override
    public void onStart(Path path) {
        //This may do nothing if the real presenter does not have a @OnStart annotation
        realPresenter.onStart( path );
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
    public EditorScreenService getPresenter() {
        return this;
    }

    @Override
    public void doSave() {
        //This may do nothing if the real presenter does not have a @Title annotation
        realPresenter.doSave();
    }

    @Override
    public boolean isDirty() {
        //This may do nothing if the real presenter does not have a @Title annotation
        return realPresenter.isDirty();
    }

}
