package org.uberfire.client.workbench.panels;

import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

public interface MultiPartWidget extends IsWidget,
                                         RequiresResize,
                                         HasBeforeSelectionHandlers<PartDefinition>,
                                         HasSelectionHandlers<PartDefinition> {

    void setPresenter( final WorkbenchPanelPresenter presenter );

    void setDndManager( final WorkbenchDragAndDropManager dndManager );

    void clear();

    void addPart( final WorkbenchPartPresenter.View view );

    void changeTitle( final PartDefinition part,
                      final String title,
                      final IsWidget titleDecoration );

    void selectPart( final PartDefinition part );

    void remove( final PartDefinition part );

    void setFocus( final boolean hasFocus );

    void addOnFocusHandler( final Command command );

    int getPartsSize();
}
