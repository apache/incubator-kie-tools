package org.drools.workbench.screens.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;

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
