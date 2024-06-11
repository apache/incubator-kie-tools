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


package org.kie.workbench.common.stunner.bpmn.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.appformer.kogito.bridge.client.dmneditor.marshaller.DmnLanguageServiceApi;
import org.appformer.kogito.bridge.client.dmneditor.marshaller.DmnLanguageServiceServiceProducer;
import org.appformer.kogito.bridge.client.dmneditor.marshaller.model.DmnDocumentData;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.client.promise.Promises;

@Singleton
public class DmnResourceContentFetcher {
    private final ResourceContentService resourceContentService;
    private final Promises promises;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;
    private final SessionManager sessionManager;
    private final DmnLanguageServiceServiceProducer dmnLanguageServiceServiceProducer;
    private Map<String, String> fileNames = new HashMap<>();
    private List<String> decisions = new ArrayList<>();
    private final static String fileMatcher = "**/*.dmn";

    @Inject
    public DmnResourceContentFetcher(final ResourceContentService resourceContentService,
                                     final Promises promises,
                                     final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                     final SessionManager sessionManager,
                                     final DmnLanguageServiceServiceProducer dmnLanguageServiceServiceProducer) {
        this.resourceContentService = resourceContentService;
        this.promises = promises;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.sessionManager = sessionManager;
        this.dmnLanguageServiceServiceProducer = dmnLanguageServiceServiceProducer;
    }

    @PostConstruct
    protected void fetchFileNames() {
        resourceContentService
                .list(fileMatcher)
                .then(paths -> {
                    if (paths.length != 0) {
                        fileNames.clear();
                        for (String path : paths) {
                            fileNames.put(path, path);
                        }
                        // Update forms in case the fileName field has a value, it needs to add it and the results returned
                        refreshForms();
                    }
                    return promises.resolve();
                });
    }

    public void refreshForms() {
        refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession()));
    }

    public void fetchFile(String fileName, Consumer<DmnDocumentData> consumer) {

        resourceContentService
                .get(fileName)
                .then(content -> {
                    DmnLanguageServiceApi produce = dmnLanguageServiceServiceProducer.produce();
                    DmnDocumentData dmnDocumentData = produce.getDmnDocumentData(content);
                    consumer.accept(dmnDocumentData);
                    refreshForms();
                    return promises.resolve();
                });
    }

    public Map<String, String> getFileNames() {
        return fileNames;
    }

    public List<String> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<String> decisions) {
        this.decisions = decisions;
    }
}
