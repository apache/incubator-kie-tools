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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.mvp.Command;

public class FunctionSearchService implements LiveSearchService<String> {

    private Path path;

    private Map<String, String> options = new HashMap<>();

    private Map<String, FunctionDef> currentFunctions = new HashMap<>();

    private final Caller<ConditionEditorService> service;

    private final FunctionNamingService functionNamingService;

    @Inject
    public FunctionSearchService(final Caller<ConditionEditorService> service, final FunctionNamingService functionNamingService) {
        this.service = service;
        this.functionNamingService = functionNamingService;
    }

    public void init(ClientSession session) {
        this.path = session.getCanvasHandler().getDiagram().getMetadata().getPath();
    }

    public void reload(String type, Command onSuccess) {
        service.call(result -> {
            setFunctions((List<FunctionDef>) result);
            onSuccess.execute();
        }).findAvailableFunctions(path, type);
    }

    public void clear() {
        options.clear();
        currentFunctions.clear();
    }

    @Override
    public void search(String pattern, int maxResults, LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>(maxResults);
        options.entrySet().stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(pattern.toLowerCase()))
                .forEach(entry -> results.add(entry.getKey(), entry.getValue()));
        callback.afterSearch(results);
    }

    @Override
    public void searchEntry(String key, LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>();
        if (options.containsKey(key)) {
            results.add(key, options.get(key));
        }
        callback.afterSearch(results);
    }

    public FunctionDef getFunction(String function) {
        return currentFunctions.get(function);
    }

    private void setFunctions(List<FunctionDef> functions) {
        currentFunctions = functions.stream().collect(Collectors.toMap(FunctionDef::getName, Function.identity()));
        options = functions.stream().collect(Collectors.toMap(FunctionDef::getName, functionDef -> functionNamingService.getFunctionName(functionDef.getName())));
    }
}
