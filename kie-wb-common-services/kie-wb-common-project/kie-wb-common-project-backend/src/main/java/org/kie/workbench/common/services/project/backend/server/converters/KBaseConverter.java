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

package org.kie.workbench.common.services.project.backend.server.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.core.util.AbstractXStreamConverter;
import org.kie.workbench.common.services.project.service.model.AssertBehaviorOption;
import org.kie.workbench.common.services.project.service.model.EventProcessingOption;
import org.kie.workbench.common.services.project.service.model.KBaseModel;
import org.kie.workbench.common.services.project.service.model.KSessionModel;

import java.util.HashMap;
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


        if (kBase.getScope() != null) {
            writer.addAttribute("scope", kBase.getScope());
        }

        if (!kBase.getPackages().isEmpty()) {
            StringBuilder buf = new StringBuilder();
            boolean first = true;
            for (String pkg : kBase.getPackages()) {
                if (first) {
                    first = false;
                } else {
                    buf.append(", ");
                }
                buf.append(pkg);
            }
            writer.addAttribute("packages", buf.toString());
        }
        if (!kBase.getIncludes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            boolean insertComma = false;
            for (String include : kBase.getIncludes()) {
                if (insertComma) {
                    sb.append(", ");
                }
                sb.append(include);
                if (!insertComma) {
                    insertComma = true;
                }
            }
            writer.addAttribute("includes", sb.toString());
        }

        Map<String, KSessionModel> join = new HashMap<String, KSessionModel>();
        setTypes("stateful", kBase.getStatefulSessions());
        setTypes("stateless", kBase.getStatelessSessions());
        join.putAll(kBase.getStatefulSessions());
        join.putAll(kBase.getStatelessSessions());
        for (KSessionModel kSessionModel : join.values()) {
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

        String scope = reader.getAttribute("scope");
        if (scope != null) {
            kBase.setScope(scope.trim());
        }

        String pkgs = reader.getAttribute("packages");
        if (pkgs != null) {
            for (String pkg : pkgs.split(",")) {
                kBase.addPackage(pkg.trim());
            }
        }

        String includes = reader.getAttribute("includes");
        if (includes != null) {
            for (String include : includes.split(",")) {
                kBase.addInclude(include.trim());
            }
        }

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("ksession".equals(name)) {
                    KSessionModel kSession = readObject(reader, context, KSessionModel.class);
                    if (kSession.getType().equals("stateless")) {
                        kBase.getStatelessSessions().put(kSession.getName(), kSession);
                    } else {
                        kBase.getStatefulSessions().put(kSession.getName(), kSession);
                    }

                } else if ("includes".equals(name)) {
                    for (String include : readList(reader)) {
                        kBase.addInclude(include);
                    }
                }
            }
        });
        return kBase;
    }
}
