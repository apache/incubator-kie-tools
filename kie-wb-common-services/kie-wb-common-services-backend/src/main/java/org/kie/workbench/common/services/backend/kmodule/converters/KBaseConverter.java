/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.kmodule.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.DeclarativeAgendaOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;

import java.util.Map;

public class KBaseConverter
        extends AbstractXStreamConverter {

    public KBaseConverter() {
        super(KBaseModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KBaseModel kBase = (KBaseModel) value;
        writer.addAttribute("name", kBase.getName());
        writer.addAttribute("default", Boolean.toString(kBase.isDefault()));
        if (kBase.getEventProcessingMode() != null) {
            writer.addAttribute("eventProcessingMode", kBase.getEventProcessingMode().getMode());
        }
        if (kBase.getEqualsBehavior() != null) {
            writer.addAttribute("equalsBehavior", kBase.getEqualsBehavior().toString());
        }
        if (kBase.getDeclarativeAgenda() != null) {
            writer.addAttribute("declarativeAgenda", kBase.getDeclarativeAgenda().toString());
        }


        if (kBase.getScope() != null) {
            writer.addAttribute("scope", kBase.getScope());
        }

        if (!kBase.getPackages().isEmpty()) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (SingleValueItemObjectModel pkg : kBase.getPackages()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(pkg.getValue());
            }
            writer.addAttribute("packages", buf.toString());
        }
        if (!kBase.getIncludes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean insertComma = false;
            for (SingleValueItemObjectModel include : kBase.getIncludes()) {
                if (insertComma) {
                    sb.append(", ");
                }
                sb.append(include.getValue());
                if (!insertComma) {
                    insertComma = true;
                }
            }
            writer.addAttribute("includes", sb.toString());
        }

        for (KSessionModel kSessionModel : kBase.getKSessions()) {
            writeObject(writer, context, "ksession", kSessionModel);
        }
    }

    private void setTypes(String stateful, Map<String, KSessionModel> sessions) {
        for (String key : sessions.keySet()) {
            sessions.get(key).setType(stateful);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KBaseModel kBase = new KBaseModel();
        kBase.setName(reader.getAttribute("name"));
        kBase.setDefault("true".equals(reader.getAttribute("default")));

        String eventMode = reader.getAttribute("eventProcessingMode");
        if (eventMode != null) {
            kBase.setEventProcessingMode(EventProcessingOption.determineEventProcessingMode(eventMode));
        }
        String equalsBehavior = reader.getAttribute("equalsBehavior");
        if (equalsBehavior != null) {
            kBase.setEqualsBehavior(AssertBehaviorOption.determineAssertBehaviorMode(equalsBehavior));
        }
        String declarativeAgenda = reader.getAttribute("declarativeAgenda");
        if (declarativeAgenda != null) {
            kBase.setDeclarativeAgenda(DeclarativeAgendaOption.determineDeclarativeAgendaMode(declarativeAgenda));
        }

        String scope = reader.getAttribute("scope");
        if (scope != null) {
            kBase.setScope(scope.trim());
        }

        String pkgs = reader.getAttribute("packages");
        if (pkgs != null) {
            for (String pkg : pkgs.split(",")) {
                kBase.addPackage(new SingleValueItemObjectModel(pkg.trim()));
            }
        }

        String includes = reader.getAttribute("includes");
        if (includes != null) {
            for (String include : includes.split(",")) {
                kBase.addInclude(new SingleValueItemObjectModel(include.trim()));
            }
        }

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("ksession".equals(name)) {
                    KSessionModel kSession = readObject(reader, context, KSessionModel.class);
                    kBase.getKSessions().add(kSession);

                } else if ("includes".equals(name)) {
                    for (String include : readList(reader)) {
                        kBase.addInclude(new SingleValueItemObjectModel(include));
                    }
                }
            }
        });
        return kBase;
    }
}
