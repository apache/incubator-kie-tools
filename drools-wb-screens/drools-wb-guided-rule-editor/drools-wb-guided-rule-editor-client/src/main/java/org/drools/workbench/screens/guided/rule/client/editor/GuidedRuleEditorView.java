/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.commons.shared.rule.RuleModel;
import org.kie.workbench.widgets.common.client.widget.HasBusyIndicator;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.backend.vfs.Path;

public interface GuidedRuleEditorView extends HasBusyIndicator,
                                              IsWidget {

    void setContent( final Path path,
                     final RuleModel model,
                     final PackageDataModelOracle dataModel,
                     final boolean isReadOnly,
                     final boolean isDSLEnabled );

    RuleModel getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void refresh();

    void alertReadOnly();

}
