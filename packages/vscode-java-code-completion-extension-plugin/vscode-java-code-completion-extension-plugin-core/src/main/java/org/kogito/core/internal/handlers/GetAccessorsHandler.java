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

package org.kogito.core.internal.handlers;

import java.util.List;
import java.util.Optional;
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
    private final TypeHierarchyHandler typeHierarchyHandler;

    public GetAccessorsHandler(String id, JavaEngine javaEngine, AutocompleteHandler autocompleteHandler, TypeHierarchyHandler typeHierarchyHandler) {
        super(id);
        this.javaEngine = javaEngine;
        this.autocompleteHandler = autocompleteHandler;
        this.typeHierarchyHandler = typeHierarchyHandler;
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

        return this.transformCompletionItemsToResult(parameters.getFqcn(), items);
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

    protected GetPublicResult getAccessor(CompletionItem item, String javaClassFqcn) {
        String accessorName = item.getLabelDetails().getDetail() != null ?
                item.getLabel() + item.getLabelDetails().getDetail() :
                item.getLabel();
        String accessorTypeSimpleName = item.getLabelDetails().getDescription();

        GetPublicResult result = new GetPublicResult();
        result.setFqcn(javaClassFqcn);
        result.setAccessor(accessorName);
        result.setType(retrieveAccessorTypeFqcn(javaClassFqcn, accessorName).orElse(accessorTypeSimpleName));

        return result;
    }

    private Optional<String> retrieveAccessorTypeFqcn(String javaClassFqcn, String accessorName) {
        BuildInformation hoverBuildInformation =
                javaEngine.buildVarTypePublicContent(this.typeHierarchyHandler.getActivatorPath(),
                        javaClassFqcn,
                        accessorName + ";");

        List<TypeHierarchyItem> typeHierarchyResults = this.typeHierarchyHandler.handle("GetAccessorsHandler", hoverBuildInformation);

        if (typeHierarchyResults.isEmpty() || typeHierarchyResults.size() > 1) {
            return Optional.empty();
        }

        String fullPackage = typeHierarchyResults.get(0).getDetail();
        String classSimpleName = typeHierarchyResults.get(0).getName();

        if (fullPackage == null || fullPackage.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(fullPackage + "." + classSimpleName);
    }

}
