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

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.jdt.ls.core.internal.handlers.JDTLanguageServer;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TypeHierarchyItem;
import org.eclipse.lsp4j.TypeHierarchyPrepareParams;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;

import org.kogito.core.internal.engine.ActivationChecker;
import org.kogito.core.internal.engine.BuildInformation;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HoverHandler {

    private ActivationChecker activationChecker;

    public HoverHandler(ActivationChecker activationChecker) {
        this.activationChecker = activationChecker;
    }

    public List<TypeHierarchyItem> handle(String identifier, BuildInformation buildInformation) {

        JDTLanguageServer languageServer = (JDTLanguageServer) JavaLanguageServerPlugin.getInstance().getProtocol();

        String uri = buildInformation.getPath().toUri().toASCIIString();

        JavaLanguageServerPlugin.logInfo("Opening URI:" + uri);

        DidOpenTextDocumentParams didOpenTextDocumentParams = new DidOpenTextDocumentParams();
        TextDocumentItem textDocumentItem = new TextDocumentItem();
        textDocumentItem.setLanguageId("java");
        textDocumentItem.setText(buildInformation.getText());
        textDocumentItem.setUri(uri);
        textDocumentItem.setVersion(1);
        didOpenTextDocumentParams.setTextDocument(textDocumentItem);
        languageServer.didOpen(didOpenTextDocumentParams);

        Position pos = new Position();
        pos.setLine(buildInformation.getLine());
        pos.setCharacter(buildInformation.getPosition());

        TextDocumentIdentifier textDocumentIdentifier = new TextDocumentIdentifier();
        textDocumentIdentifier.setUri(uri);

        TypeHierarchyPrepareParams typeHierarchyPrepareParams = new TypeHierarchyPrepareParams();
        typeHierarchyPrepareParams.setTextDocument(textDocumentIdentifier);
        typeHierarchyPrepareParams.setPosition(pos);

        CompletableFuture<List<TypeHierarchyItem>> javaCompletion = languageServer.prepareTypeHierarchy(typeHierarchyPrepareParams);
        try {
            return javaCompletion.get();
        } catch (Exception e) {
            JavaLanguageServerPlugin.logException("Problem with " + identifier, e);
            return null;
        } finally {
            JavaLanguageServerPlugin.logInfo("Closing URI:" + uri);

            DidCloseTextDocumentParams didCloseTextDocumentParams = new DidCloseTextDocumentParams();
            didCloseTextDocumentParams.setTextDocument(textDocumentIdentifier);
            languageServer.didClose(didCloseTextDocumentParams);
        }
    }

    public Path getActivatorPath() {
        return this.activationChecker.getActivatorPath();
    }
}
