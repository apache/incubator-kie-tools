package org.uberfire.client.screens.iframe;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.mvp.PlaceRequest;

@Dependent
@WorkbenchScreen(identifier = "IFrameScreen")
public class IFrameScreenPresenter {

    private String title;

    public interface View extends IsWidget {

        void setURL( final String url );
    }

    @Inject
    public IFrameScreenPresenter.View view;

    @OnStart
    public void onStart( final PlaceRequest placeRequest ) {
        this.view.setURL( placeRequest.getParameter( "url", "none" ) );
        this.title = placeRequest.getParameter( "title", "iframe" );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return title;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
