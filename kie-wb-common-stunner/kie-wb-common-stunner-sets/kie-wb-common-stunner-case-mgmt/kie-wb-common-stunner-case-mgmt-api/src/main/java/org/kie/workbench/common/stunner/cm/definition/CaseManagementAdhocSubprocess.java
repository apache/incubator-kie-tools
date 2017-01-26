/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Portable
@Bindable
@CanContain(roles = {"activity"})
@Definition(graphFactory = NodeFactory.class, builder = CaseManagementAdhocSubprocess.AdhocSubprocessBuilder.class)
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class CaseManagementAdhocSubprocess extends CaseManagementBaseSubprocess {

    @Title
    public static final transient String title = "Adhoc Subprocess";

    @Description
    public static final transient String description = "An adhoc subprocess.";

    @NonPortable
    public static class AdhocSubprocessBuilder extends BaseSubprocessBuilder<CaseManagementAdhocSubprocess> {

        @Override
        public CaseManagementAdhocSubprocess build() {
            return new CaseManagementAdhocSubprocess(new BPMNGeneralSet("Adhoc subprocess"),
                                                     new BackgroundSet(COLOR,
                                                                       BORDER_COLOR,
                                                                       BORDER_SIZE),
                                                     new FontSet(),
                                                     new RectangleDimensionsSet(WIDTH,
                                                                                HEIGHT),
                                                     new SimulationSet());
        }
    }

    public CaseManagementAdhocSubprocess() {
        super();
    }

    public CaseManagementAdhocSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                                         final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                         final @MapsTo("fontSet") FontSet fontSet,
                                         final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                                         final @MapsTo("simulationSet") SimulationSet simulationSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
    }

    public String getTitle() {
        return title;
    }
}
