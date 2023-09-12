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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

    private final ConditionEditorAvailableFunctionsService availableFunctionsService;

    private final FunctionNamingService functionNamingService;

    @Inject
    public FunctionSearchService(final ConditionEditorAvailableFunctionsService availableFunctionsService,
                                 final FunctionNamingService functionNamingService) {
        this.availableFunctionsService = availableFunctionsService;
        this.functionNamingService = functionNamingService;
    }

    public void init(final ClientSession session) {
        this.path = session.getCanvasHandler().getDiagram().getMetadata().getPath();
    }

    public void reload(final String type,
                       final Command onSuccess) {
        availableFunctionsService
                .call(new ConditionEditorAvailableFunctionsService.Input(path, type))
                .then(result -> {
                    setFunctions(result);
                    onSuccess.execute();
                    return null;
                });
    }

    public void clear() {
        options.clear();
        currentFunctions.clear();
    }

    @Override
    public void search(final String pattern,
                       final int maxResults,
                       final LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>(maxResults);
        options.entrySet().stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(pattern.toLowerCase()))
                .forEach(entry -> results.add(entry.getKey(), entry.getValue()));
        callback.afterSearch(results);
    }

    @Override
    public void searchEntry(final String key,
                            final LiveSearchCallback<String> callback) {
        LiveSearchResults<String> results = new LiveSearchResults<>();
        if (options.containsKey(key)) {
            results.add(key, options.get(key));
        }
        callback.afterSearch(results);
    }

    public FunctionDef getFunction(final String function) {
        return currentFunctions.get(function);
    }

    private void setFunctions(final List<FunctionDef> functions) {
        currentFunctions = functions.stream().collect(Collectors.toMap(FunctionDef::getName, Function.identity()));
        options = functions.stream().collect(Collectors.toMap(FunctionDef::getName, functionDef -> functionNamingService.getFunctionName(functionDef.getName())));
    }
}
