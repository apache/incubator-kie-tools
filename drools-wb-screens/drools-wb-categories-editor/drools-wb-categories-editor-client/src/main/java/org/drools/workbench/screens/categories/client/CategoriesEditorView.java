package org.drools.workbench.screens.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.kie.guvnor.services.metadata.model.Categories;

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
