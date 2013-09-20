package org.kie.workbench.common.screens.defaulteditor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.editors.texteditor.TextResourceType;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "GuvnorTextEditor", supportedTypes = { TextResourceType.class }, priority = -1)
public class GuvnorTextEditorScreenPresenter
        extends GuvnorTextEditorPresenter {

    @Inject
    private TextResourceType type;

    @OnOpen
    public void onOpen() {
        super.onOpen();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Text Editor [" + FileNameUtil.removeExtension( path,
                                                               type ) + "]";
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }
}
