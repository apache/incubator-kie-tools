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
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.ls.core.internal.handlers.CompletionResponse;
import org.eclipse.jdt.ls.core.internal.handlers.CompletionResponses;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.MarkupContent;
import org.kogito.core.internal.api.GetPublicParameters;
import org.kogito.core.internal.api.GetPublicResult;
import org.kogito.core.internal.engine.BuildInformation;
import org.kogito.core.internal.engine.JavaEngine;

import static org.eclipse.jdt.ls.core.internal.handlers.CompletionResolveHandler.DATA_FIELD_REQUEST_ID;
import static org.eclipse.jdt.ls.core.internal.handlers.CompletionResolveHandler.DATA_FIELD_PROPOSAL_ID;

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

                    Hover hoverResult = this.hoverHandler.handle("GetAccessorsHandler", hoverBuildInformation);
                    if (hoverResult != null && hoverResult.getContents() != null) {
                        JavaLanguageServerPlugin.logInfo("Hover results content found!");

                        Either<List<Either<String, MarkedString>>, MarkupContent> content = hoverResult.getContents();
                        JavaLanguageServerPlugin.logInfo("CONTENT " + content);

                        if (content.isRight()) {
                            JavaLanguageServerPlugin.logInfo("CONTENT RIGHT");
                            JavaLanguageServerPlugin.logInfo("MarkupContent " + content.getRight().getValue());
                        } else if (content.isLeft() && content != null) {
                            List<Either<String, MarkedString>> contentList = content.getLeft();
                            JavaLanguageServerPlugin.logInfo("CONTENT LEFT");
                            JavaLanguageServerPlugin.logInfo("CONTENT LEFT " + content.getRight());

                            JavaLanguageServerPlugin.logInfo("Hover result size " + contentList.size());

                            contentList.stream().forEach(item -> {

                                if (item.isLeft() && item != null) {
                                    JavaLanguageServerPlugin.logInfo("String " + content.getLeft());
                                } else if (item.isRight() && content.getRight() != null) {
                                    JavaLanguageServerPlugin.logInfo("MarkedString " + content.getRight().getValue());
                                } else {
                                    JavaLanguageServerPlugin.logInfo("Item empty right and left");
                                }
                            });
                        } else {
                            JavaLanguageServerPlugin.logInfo("Content empty right and left");
                        }
                        //getPublicResult.setType(null);
                    } else {
                        JavaLanguageServerPlugin.logInfo("No info Hovering on " + getPublicResult.getAccessor());
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
        String type = item.getLabelDetails().getDescription();
        /* Retrieving the class type FQCN */
        /* The API we used to retrieve the FQNC are no more available. To enable the Project
         * compilation, the following block is temporary commented. The impact on the feature, is
         * that the Fecthing feature will no work properly, until we found an alternative solution
         * https://github.com/kiegroup/kie-issues/issues/114
         */
        /*
        Map<String,String> data = (Map<String, String>) item.getData();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JavaLanguageServerPlugin.logInfo("ENTRY: " + entry.getKey() + " " + entry.getValue());
        }
        /*
        if (data != null && data.containsKey(DATA_FIELD_REQUEST_ID) && data.containsKey(DATA_FIELD_PROPOSAL_ID)) {
            CompletionResponse completionResponse = CompletionResponses.get(Long.getLong(data.get(DATA_FIELD_REQUEST_ID)));
            CompletionProposal proposal = completionResponse.getProposals().get(Integer.getInteger(data.get(DATA_FIELD_PROPOSAL_ID)));
            /* The `DeclarationSignature` format is: `method()Ljava.lang.String;`
            String fqcnType = String.valueOf(proposal.getDeclarationSignature());
            JavaLanguageServerPlugin.logInfo("FQCN: " + fqcnType);

            if (fqcnType != null && fqcnType.contains(")L")) {
                type = fqcnType.split("\\)L")[1];
                type = type.replaceAll(";$", "");
            }
        }*/
        result.setType(type);

        return result;
    }



    /*
            List<GetPublicResult> publicResults = this.transformCompletionItemsToResult(parameters.getFqcn(), items);

        JavaLanguageServerPlugin.logInfo("Public results found " + publicResults.size());


        for (GetPublicResult publicResult : publicResults) {
            if (publicResult.getType().equalsIgnoreCase("void")) {
                break;
            }

            BuildInformation buildInformation2 = javaEngine.buildVarTypePublicContent(this.autocompleteHandler.getActivatorPath(),
                    publicResult.getFqcn(),
                    publicResult.getAccessor());

            List<CompletionItem> completionItems = this.autocompleteHandler.handle("GetAccessorsHandler", buildInformation2);
            JavaLanguageServerPlugin.logInfo("Item found for " + publicResult.getAccessor() + ": " + items.size());
            JavaLanguageServerPlugin.logInfo("Current type " + publicResult.getType());


            for (CompletionItem completionItem : completionItems) {
                JavaLanguageServerPlugin.logInfo("===============================================");

                if (completionItem.getLabelDetails() != null) {
                    JavaLanguageServerPlugin.logInfo("Label Details - Description *: " + completionItem.getLabelDetails().getDescription());
                    JavaLanguageServerPlugin.logInfo("Label Details - Detail: " + completionItem.getLabelDetails().getDetail());
                }
                /* Retrieving the class type FQCN */
                /*Map<String,String> data = (Map<String, String>) completionItem.getData();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    JavaLanguageServerPlugin.logInfo("ENTRY: " + entry.getKey() + " " + entry.getValue());
                }
                JavaLanguageServerPlugin.logInfo("TextEditText: " + completionItem.getTextEditText());
                JavaLanguageServerPlugin.logInfo("SortText: " + completionItem.getSortText());
                JavaLanguageServerPlugin.logInfo("InsertText: " + completionItem.getInsertText());
                JavaLanguageServerPlugin.logInfo("FilterText: " + completionItem.getFilterText());
                /*
                if (completionItem.getTags() != null && completionItem.getTags().size() > 0) {
                    for (CompletionItemTag entry : completionItem.getTags()) {
                        JavaLanguageServerPlugin.logInfo("TAG: " + entry);
                    }
                }
                //JavaLanguageServerPlugin.logInfo("Label: " + completionItem.getLabel());
                /*
                Either<String, MarkupContent> documentation = completionItem.getDocumentation();
                if (documentation != null) {
                    if (documentation.isLeft()) {
                        JavaLanguageServerPlugin.logInfo("DOCUMENTATION: " + documentation.getLeft());
                    } else if (documentation.isRight()) {
                        JavaLanguageServerPlugin.logInfo("DOCUMENTATION: " + documentation.getRight().getValue());
                    }
                }

                if (completionItem.getAdditionalTextEdits() != null && completionItem.getAdditionalTextEdits().size() > 0) {
                    for (TextEdit entry : completionItem.getAdditionalTextEdits()) {
                        JavaLanguageServerPlugin.logInfo("TAG: " + entry.getNewText());
                    }
                }
}
        }


                return publicResults;
     */
}
