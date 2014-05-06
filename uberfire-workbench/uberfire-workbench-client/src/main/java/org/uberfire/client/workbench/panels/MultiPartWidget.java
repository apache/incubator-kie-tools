package org.uberfire.client.workbench.panels;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;

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

    /**
     * Makes the given part visible if it is a direct child of this widget.
     * 
     * @param part
     *            the direct child part to select. Must not be null.
     * @return true if the part was found as a direct child of this widget, and it was therefore selected. False if the
     *         part was not found, in which case this method had no effect.
     */
    boolean selectPart( final PartDefinition part );

    /**
     * Removes the given part from this widget. If the part was currently selected (visible) when removed, another part
     * will be selected to take its place.
     * 
     * @param part
     *            the part to remove. Must not be null.
     * @return True if the given part was found as a direct child of this widget, in which case it has been removed.
     *         False if the given part was not found, in which case this method had no effect.
     */
    boolean remove( final PartDefinition part );

    void setFocus( final boolean hasFocus );

    void addOnFocusHandler( final Command command );

    int getPartsSize();
}
