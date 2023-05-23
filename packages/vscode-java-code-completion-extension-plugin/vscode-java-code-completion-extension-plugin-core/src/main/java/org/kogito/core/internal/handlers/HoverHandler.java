/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.jdt.ls.core.internal.handlers.JDTLanguageServer;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.TextDocumentIdentifier;

import org.kogito.core.internal.engine.ActivationChecker;
import org.kogito.core.internal.engine.BuildInformation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HoverHandler {

    private ActivationChecker activationChecker;

    public HoverHandler(ActivationChecker activationChecker) {
        this.activationChecker = activationChecker;
    }

    public Hover handle(String identifier, BuildInformation buildInformation) {

        JDTLanguageServer languageServer = (JDTLanguageServer) JavaLanguageServerPlugin.getInstance().getProtocol();

        String uri = buildInformation.getPath().toUri().toASCIIString();

        DidOpenTextDocumentParams didOpenTextDocumentParams = new DidOpenTextDocumentParams();
        TextDocumentItem textDocumentItem = new TextDocumentItem();
        textDocumentItem.setLanguageId("java");
        textDocumentItem.setText(buildInformation.getOriginalText());
        textDocumentItem.setUri(uri);
        textDocumentItem.setVersion(1);
        didOpenTextDocumentParams.setTextDocument(textDocumentItem);
        languageServer.didOpen(didOpenTextDocumentParams);

        Position pos = new Position();
        pos.setLine(buildInformation.getLine());
        pos.setCharacter(buildInformation.getPosition());

        TextDocumentIdentifier textDocumentIdentifier = new TextDocumentIdentifier();
        textDocumentIdentifier.setUri(uri);

        HoverParams hoverParams = new HoverParams();
        hoverParams.setTextDocument(textDocumentIdentifier);
        hoverParams.setPosition(pos);

        CompletableFuture<Hover> javaCompletion = languageServer.hover(hoverParams);

        try {
            return javaCompletion.get();
        } catch (Exception e) {
            JavaLanguageServerPlugin.logException("Problem with " + identifier, e);
            return null;
        }
    }

    public Path getActivatorPath() {
        return this.activationChecker.getActivatorPath();
    }
}
