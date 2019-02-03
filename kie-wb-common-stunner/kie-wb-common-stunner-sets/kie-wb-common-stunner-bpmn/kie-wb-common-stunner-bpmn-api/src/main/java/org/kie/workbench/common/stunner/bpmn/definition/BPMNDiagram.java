/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition;

import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;

public interface BPMNDiagram<D extends BaseDiagramSet, P extends BaseProcessData> extends BPMNViewDefinition {

    D getDiagramSet();

    void setDiagramSet(final D diagramSet);

    P getProcessData();

    void setProcessData(final P processData);

    BackgroundSet getBackgroundSet();

    void setBackgroundSet(final BackgroundSet backgroundSet);

    FontSet getFontSet();

    void setFontSet(final FontSet fontSet);

    CaseManagementSet getCaseManagementSet();

    void setCaseManagementSet(CaseManagementSet caseManagementSet);
}
