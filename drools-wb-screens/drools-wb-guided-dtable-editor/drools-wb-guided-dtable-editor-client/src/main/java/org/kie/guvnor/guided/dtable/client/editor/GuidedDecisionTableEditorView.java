package org.kie.guvnor.guided.dtable.client.editor;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.models.commons.shared.workitems.PortableWorkDefinition;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * Guided Decision Table Editor View definition
 */
public interface GuidedDecisionTableEditorView extends HasBusyIndicator,
                                                       IsWidget {

    void setContent( final Path path,
                     final PackageDataModelOracle dataModel,
                     final GuidedDecisionTable52 content,
                     final Set<PortableWorkDefinition> workItemDefinitions,
                     final boolean isReadOnly );

    GuidedDecisionTable52 getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void alertReadOnly();

}
