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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.jdt.ls.core.internal.handlers.JDTLanguageServer;
import org.eclipse.lsp4j.CompletionContext;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.CompletionTriggerKind;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.kogito.core.internal.engine.ActivationChecker;
import org.kogito.core.internal.engine.BuildInformation;

public class AutocompleteHandler {

    private ActivationChecker activationChecker;

    public AutocompleteHandler(ActivationChecker activationChecker) {
        this.activationChecker = activationChecker;
    }

    public List<CompletionItem> handle(String identifier, BuildInformation buildInformation) {

        JDTLanguageServer languageServer = (JDTLanguageServer) JavaLanguageServerPlugin.getInstance().getProtocol();

        String uri = buildInformation.getPath().toUri().toASCIIString();
        JavaLanguageServerPlugin.logInfo(uri);

        System.setProperty("java.lsp.joinOnCompletion", "true");

        JavaLanguageServerPlugin.logInfo(buildInformation.getText());
        {
            DidOpenTextDocumentParams didOpenTextDocumentParams = new DidOpenTextDocumentParams();
            TextDocumentItem textDocumentItem = new TextDocumentItem();
            textDocumentItem.setLanguageId("java");
            textDocumentItem.setText(buildInformation.getOriginalText());
            textDocumentItem.setUri(uri);
            textDocumentItem.setVersion(1);
            didOpenTextDocumentParams.setTextDocument(textDocumentItem);
            languageServer.didOpen(didOpenTextDocumentParams);
        }

        {
            DidChangeTextDocumentParams didChangeTextDocumentParams = new DidChangeTextDocumentParams();
            TextDocumentContentChangeEvent textDocumentContentChangeEvent = new TextDocumentContentChangeEvent();
            textDocumentContentChangeEvent.setText(buildInformation.getText());
            VersionedTextDocumentIdentifier versionedTextDocumentIdentifier = new VersionedTextDocumentIdentifier();
            versionedTextDocumentIdentifier.setUri(uri);
            versionedTextDocumentIdentifier.setVersion(2);
            didChangeTextDocumentParams.setTextDocument(versionedTextDocumentIdentifier);
            didChangeTextDocumentParams.setContentChanges(Collections.singletonList(textDocumentContentChangeEvent));
            languageServer.didChange(didChangeTextDocumentParams);
        }

        CompletionContext context = new CompletionContext();
        context.setTriggerKind(CompletionTriggerKind.Invoked);

        Position pos = new Position();
        pos.setLine(buildInformation.getLine());
        pos.setCharacter(buildInformation.getPosition());

        TextDocumentIdentifier textDocumentIdentifier = new TextDocumentIdentifier();
        textDocumentIdentifier.setUri(uri);

        CompletionParams completionParams = new CompletionParams();
        completionParams.setTextDocument(textDocumentIdentifier);
        completionParams.setPosition(pos);
        completionParams.setContext(context);

        CompletableFuture<Either<List<CompletionItem>, CompletionList>> javaCompletion = languageServer
                .completion(completionParams);

        try {
            Either<List<CompletionItem>, CompletionList> completed = javaCompletion.get();
            List<CompletionItem> items = new ArrayList<>();
            if (completed.isLeft()) {
                items = completed.getLeft();
            } else if (completed.isRight()) {
                items = completed.getRight().getItems();
            }

            JavaLanguageServerPlugin.logInfo("Size: " + items.size());

            return items;
        } catch (Exception e) {
            JavaLanguageServerPlugin.logException("Problem with " + identifier, e);
            return Collections.emptyList();
        }
    }

    public Path getActivatorPath() {
        return this.activationChecker.getActivatorPath();
    }
}
