/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestRuleFlowGroupDataEvent;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.StreamSupport.stream;

@Dependent
public class RuleFlowGroupFormProvider implements SelectorDataProvider {

    @Inject
    Event<RequestRuleFlowGroupDataEvent> requestRuleFlowGroupDataEvent;

    private static Event<RequestRuleFlowGroupDataEvent> requestRuleFlowGroupDataEventEventSingleton = null;

    @Inject
    RuleFlowGroupDataProvider dataProvider;

    @PostConstruct
    public void populateData() {
        requestRuleFlowGroupDataEvent.fire(new RequestRuleFlowGroupDataEvent());
        requestRuleFlowGroupDataEventEventSingleton = requestRuleFlowGroupDataEvent;
    }

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    public SelectorData<String> getSelectorData(final FormRenderingContext context) {
        requestRuleFlowGroupDataEvent.fire(new RequestRuleFlowGroupDataEvent());
        return new SelectorData<>(toMap(dataProvider.getRuleFlowGroupNames()), null);
    }

    // Map<T, String> is not supported by ListBoxValue which is used for ComboBox widget
    private static Map<String, String> toMap(final Iterable<RuleFlowGroup> groups) {
        return stream(groups.spliterator(), false)
                .collect(groupingBy(RuleFlowGroup::getName)).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createDisplayNameWithPackages(entry.getValue())
                ));
    }

    private static String createDisplayNameWithPackages(List<RuleFlowGroup> groups) {
        return groups.get(0).getName()
                + " "
                + groups.stream()
                .map(group -> getProjectFromPath(group.getPathUri()))
                .distinct()
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String dropFileSystemAndGitBranchFromPath(String path) {
        return path.substring(path.indexOf('@') + 1);
    }

    private static String getProjectFromPath(String path) {
        String clearedPath = dropFileSystemAndGitBranchFromPath(path);
        //Drop space
        String pathAfterSpace = clearedPath.substring(getIndexOfFileSeparator(clearedPath) + 1);
        return pathAfterSpace.substring(0, getIndexOfFileSeparator(pathAfterSpace));
    }

    // GWT compatible way to get file separation for Windows/Unix
    private static int getIndexOfFileSeparator(String string) {
        int index = string.indexOf('/');
        return index == -1 ? string.indexOf('\\') : index;
    }

    public static void initServerData() {
        if (requestRuleFlowGroupDataEventEventSingleton != null) {
            requestRuleFlowGroupDataEventEventSingleton.fire(new RequestRuleFlowGroupDataEvent());
        }
    }

    public static Event<RequestRuleFlowGroupDataEvent> getRequestRuleFlowGroupDataEventEventSingleton() {
        return requestRuleFlowGroupDataEventEventSingleton;
    }

}
