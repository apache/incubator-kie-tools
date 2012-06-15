package org.drools.guvnor.client.editors.texteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractEditorScreenActivity;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.EditorScreenService;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("TextEditor")
//TODO {manstis} This should not need to re-implement Activity but Errai doesn't detect it if it doesn't
public class TextEditorActivity extends AbstractEditorScreenActivity
    implements
    Activity {

    @Inject
    private IOCBeanManager      manager;

    @Inject
    private PlaceManager        placeManager;

    private TextEditorPresenter presenter;

    public TextEditorActivity() {
    }

    @Override
    public EditorScreenService getPresenter() {
        this.presenter = manager.lookupBean( TextEditorPresenter.class ).getInstance();
        return this.presenter;
    }

    @Override
    public String getTitle() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter( "path",
                                                          null );
        return "Text Editor [" + uriPath + "]";
    }

    @Override
    public IsWidget getWidget() {
        return presenter.view;
    }

}
