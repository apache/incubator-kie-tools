package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Decision Table Editor View definition
 */
public interface GuidedDecisionTableEditorView extends HasBusyIndicator,
                                                       IsWidget {

    void setContent( final Path path,
                     final GuidedDecisionTable52 content,
                     final Set<PortableWorkDefinition> workItemDefinitions,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<RuleNamesService> ruleNamesService,
                     final boolean isReadOnly );

    GuidedDecisionTable52 getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
