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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.ext.editor.commons.client.file.exports.TextContent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;

@Dependent
@Default
public class ExportToRawFormatSessionCommand extends AbstractExportSessionCommand {

    private final ClientDiagramService clientDiagramService;
    private final TextFileExport textFileExport;

    protected ExportToRawFormatSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public ExportToRawFormatSessionCommand(final ClientDiagramService clientDiagramService,
                                           final TextFileExport textFileExport) {
        super(true);
        this.clientDiagramService = clientDiagramService;
        this.textFileExport = textFileExport;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void export(final String fileName) {
        clientDiagramService.getRawContent(getSession().getCanvasHandler().getDiagram(),
                                           new ServiceCallback<String>() {
                                               @Override
                                               public void onSuccess(String rawContent) {
                                                   textFileExport.export(TextContent.create(rawContent),
                                                                         fileName);
                                               }

                                               @Override
                                               public void onError(ClientRuntimeError error) {
                                               }
                                           });
    }
}