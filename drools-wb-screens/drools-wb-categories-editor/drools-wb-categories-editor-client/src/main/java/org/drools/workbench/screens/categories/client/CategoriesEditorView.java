package org.drools.workbench.screens.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.widgets.common.client.widget.HasBusyIndicator;
import org.kie.workbench.services.shared.metadata.model.Categories;

/**
 * Categories Editor View definition.
 */
public interface CategoriesEditorView extends HasBusyIndicator,
                                              IsWidget {

    void setContent( final Categories categories );

    Categories getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

}
