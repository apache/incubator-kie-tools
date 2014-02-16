package org.uberfire.client.editors.defaulteditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;

@Dependent
@WorkbenchEditor(identifier = "DefaultFileEditor", supportedTypes = {AnyResourceType.class}, priority = Integer.MIN_VALUE)
public class DefaultFileEditorPresenter {

    interface View {

        void setPath(Path path);

    }

    @Inject
    public DefaultFileEditorView view;

    @Inject
    private Caller<VFSService> vfsServices;

    private Path path;

    @OnStartup
    public void onStartup( final ObservablePath path ) {
        this.path = path;
        view.setPath(path);
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.DefaultEditor()+" [" + path.getFileName() + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

}
