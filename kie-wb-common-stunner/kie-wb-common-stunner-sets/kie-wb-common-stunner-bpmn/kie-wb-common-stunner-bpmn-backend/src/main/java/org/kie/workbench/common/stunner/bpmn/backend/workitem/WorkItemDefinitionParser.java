/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.jbpm.util.WidMVELEvaluator;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.backend.util.ImageDataUriGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemDefinitionParser {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionParser.class.getName());

    // TODO: Implement other data type serializers (ENUM).
    public static final Map<Class<?>, Function<Object, String>> DATA_TYPE_FORMATTERS =
            new HashMap<Class<?>, Function<Object, String>>(2) {{
                put(String.class, value -> value.toString().trim().length() > 0 ? value.toString() : null);
                put(EnumDataType.class, Object::toString);
            }};

    public static final String ENCODING = StandardCharsets.UTF_8.name();
    private static final Pattern UNICODE_WORDS_PATTERN = Pattern.compile("\\p{L}+",
                                                                         Pattern.UNICODE_CHARACTER_CLASS);

    public static Collection<WorkItemDefinition> parse(final String serviceRepoUrl,
                                                       final String[] taskNames) {
        final String defaultServiceRepo = null != serviceRepoUrl && serviceRepoUrl.trim().length() > 0 ? serviceRepoUrl : null;
        if (defaultServiceRepo != null && taskNames.length > 0) {
            final Map<String, WorkDefinitionImpl> workItemsMap = WorkItemRepository.getWorkDefinitions(defaultServiceRepo);
            if (!workItemsMap.isEmpty()) {
                return Arrays.stream(taskNames)
                        .filter(workItemsMap::containsKey)
                        .map(workItemsMap::get)
                        .map(wid -> parse(wid,
                                          WorkItemDefinitionParser::buildDataURIFromURL))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptySet();
    }

    public static Collection<WorkItemDefinition> parse(final String content,
                                                       final Function<String, String> dataUriProvider) throws Exception {
        final Map<String, WorkDefinitionImpl> definitionMap = parseJBPMWorkItemDefinitions(content,
                                                                                           dataUriProvider);
        return definitionMap.values().stream()
                .map(wid -> parse(wid, dataUriProvider))
                .collect(Collectors.toList());
    }

    public static WorkItemDefinition parse(final WorkDefinitionImpl workDefinition,
                                           final Function<String, String> dataUriProvider) {
        final WorkItemDefinition workItem = new WorkItemDefinition();
        // Attributes..
        workItem.setName(workDefinition.getName());
        workItem.setCategory(workDefinition.getCategory());
        workItem.setDocumentation(workDefinition.getDocumentation());
        workItem.setDescription(workDefinition.getDescription());
        workItem.setDefaultHandler(workDefinition.getDefaultHandler());
        workItem.setDisplayName(workDefinition.getDisplayName());
        // Icon.
        final String iconEncoded = workDefinition.getIconEncoded();
        final String icon = workDefinition.getIcon();
        String iconData = null;
        if (null != iconEncoded && iconEncoded.trim().length() > 0) {
            iconData = iconEncoded;
        } else if (null != icon && icon.trim().length() > 0) {
            final String iconUrl = workDefinition.getPath() + "/" + icon;
            iconData = dataUriProvider.apply(iconUrl);
        }
        workItem.setIconData(iconData);
        // Parameters.
        workItem.setParameters(parseParameters(workDefinition.getParameters()));
        // Results..
        workItem.setResults(parseParameters(workDefinition.getResults()));
        return workItem;
    }

    private static String parseParameters(final Collection<ParameterDefinition> parameters) {
        return "|" + parameters.stream()
                .map(param -> param.getName() + ":" + param.getType().getStringType())
                .sorted(String::compareTo)
                .collect(Collectors.joining(",")) + "|";
    }

    @SuppressWarnings("unchecked")
    private static Map<String, WorkDefinitionImpl> parseJBPMWorkItemDefinitions(final String content,
                                                                                final Function<String, String> dataUriProvider) throws Exception {
        final List<Map<String, Object>> workDefinitionsMaps = (List<Map<String, Object>>) WidMVELEvaluator.eval(content);
        final Map<String, WorkDefinitionImpl> result = new HashMap<>(workDefinitionsMaps.size());
        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                String origWidName = ((String) workDefinitionMap.get("name")).replaceAll("\\s", "");
                Matcher widNameMatcher = UNICODE_WORDS_PATTERN.matcher(origWidName);
                if (widNameMatcher.matches()) {
                    final WorkDefinitionImpl workDefinition = parseMVELWorkItemDefinition(workDefinitionMap,
                                                                                          dataUriProvider,
                                                                                          widNameMatcher.group());
                    result.put(workDefinition.getName(),
                               workDefinition);
                } else {
                    LOG.error("The work item has an invalid name [ " +
                                      workDefinitionMap.get("name") + "]." +
                                      "It must contain words only, skipping it.");
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static WorkDefinitionImpl parseMVELWorkItemDefinition(final Map<String, Object> workDefinitionMap,
                                                                  final Function<String, String> dataUriProvider,
                                                                  final String name) throws Exception {
        final WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();

        // Name.
        workDefinition.setName(name);

        // Display name.
        set(workDefinitionMap,
            "displayName",
            workDefinition::setDisplayName);

        // Category.
        set(workDefinitionMap,
            "category",
            BPMNCategories.SERVICE_TASKS,
            workDefinition::setCategory);

        // Icon.
        set(workDefinitionMap,
            "icon",
            workDefinition::setIcon);

        // Icon data-uri.
        if (!isEmpty(workDefinition.getIcon())) {
            final String iconData = dataUriProvider.apply(workDefinition.getIcon());
            workDefinition.setIconEncoded(iconData);
        }

        // Custom editor.
        set(workDefinitionMap,
            "customEditor",
            workDefinition::setCustomEditor);

        // Parameters.
        setParameters(workDefinitionMap,
                      "parameters",
                      workDefinition::setParameters);

        // Results.
        setParameters(workDefinitionMap,
                      "results",
                      workDefinition::setResults);

        // Parameter values.
        final Map<String, Object> values = (Map<String, Object>) workDefinitionMap.get("parameterValues");
        if (null != values) {
            final Map<String, Object> parameterValues = new HashMap<>(values.size());
            values.entrySet().forEach(entry -> {
                final Object value = entry.getValue();
                final Function<Object, String> dataTypeFormatter = DATA_TYPE_FORMATTERS.get(value.getClass());
                if (null != dataTypeFormatter) {
                    parameterValues.put(entry.getKey(),
                                        dataTypeFormatter.apply(value));
                } else {
                    LOG.error("The work item's parameter type [" +
                                      value.getClass() +
                                      "] is not supported. Skipping this parameter.");
                }
            });
            workDefinition.setParameterValues(parameterValues);
        }

        // Default Handler.
        set(workDefinitionMap,
            "defaultHandler",
            "",
            workDefinition::setDefaultHandler);

        // Dependencies.
        setArray(workDefinitionMap,
                 "dependencies",
                 workDefinition::setDependencies);

        // Documentation.
        set(workDefinitionMap,
            "documentation",
            "",
            workDefinition::setDocumentation);

        // Version.
        set(workDefinitionMap,
            "version",
            "",
            workDefinition::setVersion);

        // Description.
        set(workDefinitionMap,
            "description",
            "",
            workDefinition::setDescription);

        // Maven dependencies.
        setArray(workDefinitionMap,
                 "mavenDependencies",
                 workDefinition::setMavenDependencies);

        return workDefinition;
    }

    private static void set(final Map<String, Object> map,
                            final String key,
                            final Consumer<String> consumer) {
        set(map,
            key,
            null,
            consumer);
    }

    private static void set(final Map<String, Object> map,
                            final String key,
                            final String defaultValue,
                            final Consumer<String> consumer) {
        final String value = (String) map.get(key);
        if (!isEmpty(value)) {
            consumer.accept(value);
            ;
        } else if (null != defaultValue) {
            consumer.accept(defaultValue);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setParameters(final Map<String, Object> map,
                                      final String key,
                                      final Consumer<Set<ParameterDefinition>> consumer) {
        final Map<String, DataType> parameterMap = (Map<String, DataType>) map.get(key);
        if (null != parameterMap) {
            consumer.accept(parameterMap.entrySet().stream()
                                    .map(entry -> new ParameterDefinitionImpl(entry.getKey(),
                                                                              entry.getValue()))
                                    .collect(Collectors.toSet()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setArray(final Map<String, Object> map,
                                 final String key,
                                 final Consumer<String[]> consumer) {
        final List<String> values = (List<String>) map.get(key);
        if (null != values) {
            consumer.accept(values.toArray(new String[values.size()]));
        } else {
            consumer.accept(new String[0]);
        }
    }

    private static String buildDataURIFromURL(final String url) {
        try {
            return ImageDataUriGenerator.buildDataURIFromURL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isEmpty(final String s) {
        return null == s || s.trim().length() == 0;
    }
}
