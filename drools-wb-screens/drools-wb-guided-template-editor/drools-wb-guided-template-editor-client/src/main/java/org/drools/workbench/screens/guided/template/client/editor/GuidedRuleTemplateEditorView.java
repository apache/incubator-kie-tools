package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Rule Template Editor View definition
 */
public interface GuidedRuleTemplateEditorView extends HasBusyIndicator,
                                                      IsWidget {

    void setContent( final Path path,
                     final TemplateModel model,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<RuleNamesService> ruleNamesService,
                     final EventBus eventBus,
                     final boolean isReadOnly );

    TemplateModel getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void refresh();

    void alertReadOnly();

}
