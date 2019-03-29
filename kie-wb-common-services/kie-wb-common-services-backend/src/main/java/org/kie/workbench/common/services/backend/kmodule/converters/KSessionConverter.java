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
import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.ConsoleLogger;
import org.kie.workbench.common.services.shared.kmodule.FileLogger;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;

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

        if (kSession.getLogger() instanceof ConsoleLogger) {
            writeObject(writer, context, "consoleLogger", kSession.getLogger());
        } else if (kSession.getLogger() instanceof FileLogger) {
            writeObject(writer, context, "fileLogger", kSession.getLogger());
        }

        if (!kSession.getListeners().isEmpty()) {
            writer.startNode("listeners");

            for (ListenerModel listener : kSession.getListeners()) {
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
                        kSession.getListeners().add(readObject(reader, context, ListenerModel.class));
                        reader.moveUp();
                    }
                } else if ("workItemHandlers".equals(name)) {
                    kSession.getWorkItemHandelerModels().addAll(readObjectList(reader, context, WorkItemHandlerModel.class));
                } else if ("consoleLogger".equals(name)) {
                    kSession.setLogger(readObject(reader, context, ConsoleLogger.class));
                } else if ("fileLogger".equals(name)) {
                    kSession.setLogger(readObject(reader, context, FileLogger.class));
                }
            }
        });

        return kSession;
    }
}
