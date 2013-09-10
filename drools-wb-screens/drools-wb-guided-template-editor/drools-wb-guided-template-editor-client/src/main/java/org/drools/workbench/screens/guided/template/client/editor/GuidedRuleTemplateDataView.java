package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;

/**
 * Guided Rule Template Data View definition
 */
public interface GuidedRuleTemplateDataView extends IsWidget {

    void setContent( final TemplateModel model,
                     final PackageDataModelOracle dataModel,
                     final EventBus eventBus,
                     final boolean isReadOnly );

}
