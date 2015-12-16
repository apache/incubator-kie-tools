/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.dtable.client.editor;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;

/**
 * Guided Decision Table Editor View definition
 */
public interface GuidedDecisionTableEditorView extends KieEditorView,
                                                       IsWidget {

    void setContent( final PlaceRequest place,
                     final Path path,
                     final GuidedDecisionTable52 content,
                     final Set<PortableWorkDefinition> workItemDefinitions,
                     final AsyncPackageDataModelOracle oracle,
                     final Caller<RuleNamesService> ruleNamesService,
                     final boolean isReadOnly );

    GuidedDecisionTable52 getContent();

    void onFocus();

    void onClose();

}
