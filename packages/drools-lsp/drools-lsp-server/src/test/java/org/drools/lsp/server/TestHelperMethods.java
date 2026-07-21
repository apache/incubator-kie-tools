/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;

public class TestHelperMethods {

    private TestHelperMethods() {
    }

    public static DroolsLspDocumentService getDroolsLspDocumentService(String drl) {
        DroolsLspServer ls = getDroolsLspServerForDocument(drl);
        return ls.getTextDocumentService();
    }

    public static DroolsLspServer getDroolsLspServerForDocument(String drl) {
        DroolsLspServer ls = new DroolsLspServer();
        List<Diagnostic> diagnostics = new ArrayList<>();
        ls.connect(new LanguageClient() {
            @Override
            public void telemetryEvent(Object object) {
            }

            @Override
            public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
                return null;
            }

            @Override
            public void showMessage(MessageParams messageParams) {
            }

            @Override
            public void publishDiagnostics(PublishDiagnosticsParams d) {
                diagnostics.clear();
                diagnostics.addAll(d.getDiagnostics());
            }

            @Override
            public void logMessage(MessageParams message) {
            }
        });

        TextDocumentItem doc = new TextDocumentItem();
        doc.setUri("myDocument");
        doc.setText(drl);
        ls.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(doc));
        return ls;
    }
}
