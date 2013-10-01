package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Rule Template Editor View definition
 */
public interface GuidedRuleTemplateEditorView extends HasBusyIndicator,
                                                      IsWidget {

    void setContent( final Path path,
                     final TemplateModel model,
                     final PackageDataModelOracle dataModel,
                     final EventBus eventBus,
                     final boolean isReadOnly );

    TemplateModel getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void refresh();

    void alertReadOnly();

}
