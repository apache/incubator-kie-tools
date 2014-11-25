package org.drools.workbench.screens.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

/**
 * Categories Editor View definition.
 */
public interface CategoriesEditorView extends KieEditorView,
                                              IsWidget {

    void setContent( final Categories categories );

    Categories getContent();

}
