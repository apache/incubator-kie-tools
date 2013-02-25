package org.uberfire.client.editors.metafile;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
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
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.client.workbench.type.DotResourceType;

@Dependent
@WorkbenchEditor(identifier = "MetaFileTextEditor", supportedTypes = { DotResourceType.class }, priority = Integer.MAX_VALUE - 100)
public class MetaFileEditorPresenter {

    @Inject
    public TextEditorPresenter.View view;

    @Inject
    private Caller<VFSService> vfsServices;

    private Path path;

    @OnStart
    public void onStart( final Path path ) {
        this.path = path;
        vfsServices.call( new RemoteCallback<String>() {
            @Override
            public void callback( String response ) {
                if ( response == null ) {
                    view.setContent( "-- empty --" );
                } else {
                    view.setContent( response );
                }
            }
        } ).readAllString( path );
    }

    @OnSave
    public void onSave() {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path response ) {
                view.setDirty( false );
            }
        } ).write( path, view.getContent() );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Meta File Editor [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
