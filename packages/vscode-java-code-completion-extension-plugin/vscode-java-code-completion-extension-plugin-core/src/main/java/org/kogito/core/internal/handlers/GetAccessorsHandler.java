/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.handlers;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.TypeHierarchyItem;
import org.kogito.core.internal.api.GetPublicParameters;
import org.kogito.core.internal.api.GetPublicResult;
import org.kogito.core.internal.engine.BuildInformation;
import org.kogito.core.internal.engine.JavaEngine;

public class GetAccessorsHandler extends Handler<List<GetPublicResult>> {

    private final JavaEngine javaEngine;
    private final AutocompleteHandler autocompleteHandler;
    private final HoverHandler hoverHandler;

    public GetAccessorsHandler(String id, JavaEngine javaEngine, AutocompleteHandler autocompleteHandler, HoverHandler hoverHandler) {
        super(id);
        this.javaEngine = javaEngine;
        this.autocompleteHandler = autocompleteHandler;
        this.hoverHandler = hoverHandler;
    }

    @Override
    public List<GetPublicResult> handle(List<Object> arguments, IProgressMonitor progress) {
        JavaLanguageServerPlugin.logInfo("Handle Accessors");
        GetPublicParameters parameters = checkParameters(arguments);
        BuildInformation autoCompleteBuildInformation =
                javaEngine.buildPublicContent(this.autocompleteHandler.getActivatorPath(),
                                              parameters.getFqcn(),
                                              parameters.getQuery());
        JavaLanguageServerPlugin.logInfo(autoCompleteBuildInformation.getText());
        List<CompletionItem> items = this.autocompleteHandler.handle("GetAccessorsHandler", autoCompleteBuildInformation);

        var publicResults = this.transformCompletionItemsToResult(parameters.getFqcn(), items);

        /* Decorating the accessor results with their FQCN type */
        publicResults.stream()
                .filter(getPublicResult -> !getPublicResult.getType().equalsIgnoreCase("void"))
                .forEach(getPublicResult -> {
                    JavaLanguageServerPlugin.logInfo("Hovering to find FQCN for " + getPublicResult.getAccessor());
                    BuildInformation hoverBuildInformation =
                            javaEngine.buildVarTypePublicContent(this.hoverHandler.getActivatorPath(),
                                                                 getPublicResult.getFqcn(),
                                                                 getPublicResult.getAccessor() + ";");

                    JavaLanguageServerPlugin.logInfo(hoverBuildInformation.getText());

                    List<TypeHierarchyItem> hoverResult = this.hoverHandler.handle("GetAccessorsHandler", hoverBuildInformation);
                    for (TypeHierarchyItem item : hoverResult) {
                        if (item.getData() != null) {
                            JavaLanguageServerPlugin.logInfo("CALL HIERARCHY DATA: " + item.getData());
                        }
                        JavaLanguageServerPlugin.logInfo("CALL HIERARCHY Name: " + item.getName());
                        if (item.getDetail() != null) {
                            JavaLanguageServerPlugin.logInfo("CALL HIERARCHY Detail: " + item.getDetail());
                        }
                        if (item.getUri() != null) {
                            JavaLanguageServerPlugin.logInfo("CALL HIERARCHY Detail: " + item.getUri());
                        }
                    }
                });

        return publicResults;
    }

    private GetPublicParameters checkParameters(List<Object> arguments) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("Not enough arguments for GetClasses command. Need one argument containing a text to be autocompleted");
        }

        GetPublicParameters parameters = new GetPublicParameters();
        parameters.setFqcn((String) arguments.get(0));
        parameters.setQuery((String) arguments.get(1));
        return parameters;
    }

    private List<GetPublicResult> transformCompletionItemsToResult(String fqcn, List<CompletionItem> items) {
        return items.stream()
                .filter(item -> item.getDetail() != null && item.getDetail().contains(":"))
                .map(item -> getAccessor(item, fqcn))
                .collect(Collectors.toList());
    }

    protected GetPublicResult getAccessor(CompletionItem item, String fqcn) {
        GetPublicResult result = new GetPublicResult();
        result.setFqcn(fqcn);
        result.setAccessor(item.getLabelDetails().getDetail() != null ?
                item.getLabel() + item.getLabelDetails().getDetail() :
                item.getLabel());
        /* Retrieving the class type SIMPLE NAME */
        result.setType(item.getLabelDetails().getDescription());

        return result;
    }
}
