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
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CompletionItem;
import org.kogito.core.internal.api.GetClassesResult;
import org.kogito.core.internal.engine.BuildInformation;
import org.kogito.core.internal.engine.JavaEngine;

public class GetClassesHandler extends Handler<List<GetClassesResult>> {

    private final JavaEngine javaEngine;
    private final AutocompleteHandler autocompleteHandler;

    public GetClassesHandler(String id, JavaEngine javaEngine, AutocompleteHandler autocompleteHandler) {
        super(id);
        this.javaEngine = javaEngine;
        this.autocompleteHandler = autocompleteHandler;
    }

    public List<GetClassesResult> handle(List<Object> arguments, IProgressMonitor progress) {
        checkParameters(arguments);
        String completeText = (String) arguments.get(0);
        BuildInformation buildInformation = javaEngine.buildImportClass(this.autocompleteHandler.getActivatorPath(), completeText);
        List<CompletionItem> items = this.autocompleteHandler.handle("GetClassesHandler", buildInformation);
        return this.transformCompletionItemsToResult(items);
    }

    private void checkParameters(List<Object> arguments) {
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException("Not enough arguments for GetClasses command. Need one argument containing a text to be autocompleted");
        } else {
            JavaLanguageServerPlugin.logError("Arguments: " + arguments.get(0));
        }
    }

    private List<GetClassesResult> transformCompletionItemsToResult(List<CompletionItem> items) {
        return items.stream()
                .map(item -> {
                    GetClassesResult result = new GetClassesResult();
                    result.setFqcn(item.getDetail());
                    return result;
                })
                .collect(Collectors.toList());
    }
}
