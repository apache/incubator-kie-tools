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
import org.kie.workbench.common.services.project.service.model.ClockTypeOption;
import org.kie.workbench.common.services.project.service.model.KSessionModel;
import org.kie.workbench.common.services.project.service.model.ListenerModel;
import org.kie.workbench.common.services.project.service.model.WorkItemHandlerModel;

import java.util.List;

public class KSessionConverter
        extends AbstractXStreamConverter {

    public KSessionConverter() {
        super(KSessionModel.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KSessionModel kSession = (KSessionModel) value;
        writer.addAttribute("name", kSession.getName());
        writer.addAttribute("type", kSession.getType());
        writer.addAttribute("default", Boolean.toString(kSession.isDefault()));
        if (kSession.getClockType() != null) {
            writer.addAttribute("clockType", kSession.getClockType().getClockTypeAsString());
        }
        if (kSession.getScope() != null) {
            writer.addAttribute("scope", kSession.getScope().toString());
        }

        writeObjectList(writer, context, "workItemHandlers", "workItemHandler", kSession.getWorkItemHandelerModels());

        if (!kSession.getListenerModels().isEmpty()) {
            writer.startNode("listeners");
            for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.WORKING_MEMORY_EVENT_LISTENER)) {
                writeObject(writer, context, listener.getKind().toString(), listener);
            }
            for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.AGENDA_EVENT_LISTENER)) {
                writeObject(writer, context, listener.getKind().toString(), listener);
            }
            for (ListenerModel listener : kSession.getListenerModels(ListenerModel.Kind.PROCESS_EVENT_LISTENER)) {
                writeObject(writer, context, listener.getKind().toString(), listener);
            }
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KSessionModel kSession = new KSessionModel();
        kSession.setName(reader.getAttribute("name"));
        kSession.setDefault("true".equals(reader.getAttribute("default")));

        String kSessionType = reader.getAttribute("type");
        kSession.setType(kSessionType != null ? kSessionType : "stateful");

        String clockType = reader.getAttribute("clockType");
        if (clockType != null) {
            kSession.setClockType(ClockTypeOption.get(clockType));
        }

        String scope = reader.getAttribute("scope");
        if (scope != null) {
            kSession.setScope(scope);
        }

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader,
                               String name,
                               String value) {
                if ("listeners".equals(name)) {
                    while (reader.hasMoreChildren()) {
                        reader.moveDown();
                        String nodeName = reader.getNodeName();
                        ListenerModel listener = readObject(reader, context, ListenerModel.class);
                        listener.setKSession(kSession);
                        listener.setKind(ListenerModel.Kind.fromString(nodeName));
                        kSession.addListenerModel(listener);
                        reader.moveUp();
                    }
                } else if ("workItemHandlers".equals(name)) {
                    List<WorkItemHandlerModel> wihs = readObjectList(reader, context, WorkItemHandlerModel.class);
                    for (WorkItemHandlerModel wih : wihs) {
                        wih.setKSession(kSession);
                        kSession.addWorkItemHandelerModel(wih);
                    }
                }
            }
        });

        return kSession;
    }
}
