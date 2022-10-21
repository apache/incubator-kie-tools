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
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CompletionItem;
import org.kogito.core.internal.api.GetPublicParameters;
import org.kogito.core.internal.api.GetPublicResult;
import org.kogito.core.internal.engine.BuildInformation;
import org.kogito.core.internal.engine.JavaEngine;

import static org.eclipse.jdt.ls.core.internal.handlers.CompletionResolveHandler.DATA_FIELD_SIGNATURE;

public class GetAccessorsHandler extends Handler<List<GetPublicResult>> {

    private final JavaEngine javaEngine;
    private final AutocompleteHandler autocompleteHandler;

    public GetAccessorsHandler(String id, JavaEngine javaEngine, AutocompleteHandler autocompleteHandler) {
        super(id);
        this.javaEngine = javaEngine;
        this.autocompleteHandler = autocompleteHandler;
    }

    @Override
    public List<GetPublicResult> handle(List<Object> arguments, IProgressMonitor progress) {
        JavaLanguageServerPlugin.logInfo("Handle Accessors");
        GetPublicParameters parameters = checkParameters(arguments);
        BuildInformation buildInformation = javaEngine.buildPublicContent(this.autocompleteHandler.getActivatorPath(),
                                                                          parameters.getFqcn(),
                                                                          parameters.getQuery());
        JavaLanguageServerPlugin.logInfo(buildInformation.getText());
        List<CompletionItem> items = this.autocompleteHandler.handle("GetAccessorsHandler", buildInformation);
        return this.transformCompletionItemsToResult(parameters.getFqcn(), items);
    }

    private GetPublicParameters checkParameters(List<Object> arguments) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("Not enough arguments for GetClasses command. Need one argument containing a text to be autocompleted");
        } else {
            JavaLanguageServerPlugin.logInfo("Arguments[0]: " + arguments.get(0));
            JavaLanguageServerPlugin.logInfo("Arguments[1]: " + arguments.get(1));
        }

        GetPublicParameters parameters = new GetPublicParameters();
        parameters.setFqcn((String) arguments.get(0));
        parameters.setQuery((String) arguments.get(1));
        return parameters;
    }

    private List<GetPublicResult> transformCompletionItemsToResult(String fqcn, List<CompletionItem> items) {
        return items.stream()
                .filter(item -> item.getLabel().contains(":"))
                .map(item -> getAccessor(item, fqcn))
                .collect(Collectors.toList());
    }

    protected GetPublicResult getAccessor(CompletionItem item, String fqcn) {
        GetPublicResult result = new GetPublicResult();
        result.setFqcn(fqcn);
        if (item.getLabel().contains(":")) {
            JavaLanguageServerPlugin.logInfo(item.getLabel());
            String[] label = item.getLabel().split(":");
            result.setAccessor(label[0].trim());
            String type = label[1].trim();
            Map<String,String> data = (Map<String, String>) item.getData();
            if (data != null && data.containsKey(DATA_FIELD_SIGNATURE)) {
                String fqcnType = data.get(DATA_FIELD_SIGNATURE);
                /* The DATA_FIELD_SIGNATURE format is: `method()Ljava.lang.String;` */
                if (fqcnType != null && fqcnType.contains(")L")) {
                    type = fqcnType.split("\\)L")[1];
                    type = type.replaceAll(";$", "");
                }
            }
            result.setType(type);
        } else {
            result.setAccessor("");
            result.setType("");
        }
        return result;
    }
}
