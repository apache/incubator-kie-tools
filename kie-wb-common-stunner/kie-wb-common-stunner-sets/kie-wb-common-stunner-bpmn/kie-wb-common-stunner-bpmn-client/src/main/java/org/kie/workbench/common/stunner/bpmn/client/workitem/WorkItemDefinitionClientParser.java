/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.Dependencies;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;

import static org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientUtils.getDefaultIconData;

public class WorkItemDefinitionClientParser {

    private static final String NAME = "name";
    private static final String DISPLAY_NAME = "displayName";
    private static final String ICON = "icon";
    private static final String PARAMETERS = "parameters";
    private static final String RESULTS = "results";
    private static final String CATEGORY = "category";
    private static final String NEW = "new";
    private static final String STRING_DATA_TYPE = "StringDataType";
    private static final String OBJECT_DATA_TYPE = "ObjectDataType";

    public static List<WorkItemDefinition> parse(String widStr) {
        if (empty(widStr)) {
            return Collections.emptyList();
        }

        List<WorkItemDefinition> widList = new ArrayList<>();
        String[] lines = widStr.split("\r\n|\r|\n");
        Queue<String> linesQueue = new LinkedList<>(Arrays.asList(lines));
        while (!linesQueue.isEmpty()) {
            String line = linesQueue.peek().trim();
            if (!(empty(line) || isStartingObject(line) || isEndingObject(line))) {
                WorkItemDefinition wid = parseWorkItemDefinitionObject(linesQueue);
                widList.add(wid);
            }
            linesQueue.poll();
        }
        return widList;
    }

    private static boolean empty(String line) {
        return line == null || "".equals(line.trim());
    }

    private static WorkItemDefinition parseWorkItemDefinitionObject(Queue<String> objectQueue) {
        WorkItemDefinition wid = emptyWid();
        wid.setDependencies(new Dependencies());
        String line = objectQueue.poll();
        while (!isEndingObject(line) && !objectQueue.isEmpty()) {
            Map.Entry<String, String> attributes = getAttribute(line);
            switch (attributes.getKey()) {
                case NAME:
                    wid.setName(attributes.getValue());
                    break;
                case DISPLAY_NAME:
                    wid.setDisplayName(attributes.getValue());
                    break;
                case ICON:
                    wid.getIconDefinition().setUri(attributes.getValue());
                    break;
                case PARAMETERS:
                    if (!isEndingObject(attributes.getValue())) {
                        Collection<Map.Entry<String, String>> parameters = retrieveParameters(objectQueue);
                        wid.setParameters(parseParameters(parameters));
                    }
                    break;
                case RESULTS:
                    if (!isEndingObject(attributes.getValue())) {
                        Collection<Map.Entry<String, String>> results = retrieveParameters(objectQueue);
                        wid.setResults(parseParameters(results));
                    }
                    break;
                case CATEGORY:
                    wid.setCategory(attributes.getValue());
                default:
                    break;
            }
            line = objectQueue.poll();
        }

        if (empty(wid.getCategory())) {
            wid.setCategory(BPMNCategories.SERVICE_TASKS);
        }
        return wid;
    }

    private static WorkItemDefinition emptyWid() {
        WorkItemDefinition wid = new WorkItemDefinition();
        wid.setIconDefinition(new IconDefinition());
        wid.getIconDefinition().setUri("");
        wid.getIconDefinition().setIconData(getDefaultIconData());
        wid.setUri("");
        wid.setName("");
        wid.setCategory("");
        wid.setDescription("");
        wid.setDocumentation("");
        wid.setDisplayName("");
        wid.setResults("");
        wid.setDefaultHandler("");
        wid.setDependencies(new Dependencies(Collections.emptyList()));
        wid.setParameters("");
        return wid;
    }

    private static Collection<Map.Entry<String, String>> retrieveParameters(Queue<String> objectQueue) {
        String param = objectQueue.poll();
        List<Map.Entry<String, String>> params = new ArrayList<>();
        while (!(isEndingObject(param) || objectQueue.isEmpty())) {
            String[] paramsParts = param.trim().split(":");
            String paramName = cleanProp(paramsParts[0]);
            String paramType = paramsParts[1].replaceAll(NEW, "")
                    .replaceAll(",", "")
                    .replaceAll("\\(\\)", "").trim();
            params.add(new SimpleEntry<>(paramName, toJavaType(paramType)));
            param = objectQueue.poll();
        }
        return params;
    }

    private static Map.Entry<String, String> getAttribute(String value) {
        Map.Entry<String, String> attrs = new SimpleEntry<>("", "");
        if (value.indexOf(':') != -1) {
            String[] values = value.split(":");
            attrs = new SimpleEntry<>(cleanProp(values[0]), cleanProp(values[1]));
        }
        return attrs;
    }

    private static String cleanProp(String prop) {
        return prop.trim().replaceAll("\"", "").replaceAll(",", "");
    }

    private static boolean isStartingObject(String line) {
        return line.startsWith("[");
    }

    private static boolean isEndingObject(String line) {
        return line == null || line.endsWith("]") || line.endsWith("],");
    }

    private static String parseParameters(final Collection<Map.Entry<String, String>> parameters) {
        return "|" + parameters.stream()
                .map(param -> param.getKey() + ":" + param.getValue())
                .sorted(String::compareTo)
                .collect(Collectors.joining(",")) + "|";
    }

    /**
     * Converts a MVEL datatype to Java type. Could be extended for all MVEL possible types.
     * @param mvelType The MVEL type, e.g. StringDataType
     * @return The Java corresponding type e.g. String
     */
    private static String toJavaType(String mvelType) {
        switch (mvelType) {
            case STRING_DATA_TYPE:
                return "String";
            case OBJECT_DATA_TYPE:
                return "java.lang.Object";
            default:
                return mvelType;
        }
    }
}
