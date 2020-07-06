/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dashbuilder.backend.navigation.RuntimeNavigationBuilder;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.model.DataSetContent;
import org.dashbuilder.shared.model.DataSetContentType;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.RuntimeModelParser;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.dashbuilder.shared.model.ImportDefinitions.DATASET_DEF_PREFIX;
import static org.dashbuilder.shared.model.ImportDefinitions.NAVIGATION_FILE;
import static org.dashbuilder.shared.model.ImportDefinitions.PERSPECTIVE_SUFFIX;

/**
 * Parses an exported zip file from Transfer Services into RuntimeModel.
 *
 */
@ApplicationScoped
public class RuntimeModelParserImpl implements RuntimeModelParser {

    @Inject
    Event<NewDataSetContentEvent> newDataSetContentEvent;

    @Inject
    RuntimeNavigationBuilder runtimeNavigationBuilder;

    Gson gson;

    @PostConstruct
    void init() {
        gson = new GsonBuilder().create();
    }

    @Override
    public RuntimeModel parse(InputStream is) {
        try {
            return retrieveRuntimeModel(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected RuntimeModel retrieveRuntimeModel(final InputStream is) throws IOException {
        List<DataSetContent> datasetContents = new ArrayList<>();
        List<LayoutTemplate> layoutTemplates = new ArrayList<>();
        Optional<String> navTreeOp = Optional.empty();
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    if (entryName.startsWith(DATASET_DEF_PREFIX)) {
                        datasetContents.add(retrieveDataSetContent(entry, zis));
                    }

                    if (entryName.endsWith(PERSPECTIVE_SUFFIX)) {
                        layoutTemplates.add(retrieveLayoutTemplate(zis));
                    }

                    if (entryName.equalsIgnoreCase(NAVIGATION_FILE)) {
                        navTreeOp = Optional.of(nextEntryContent(zis));
                    }
                }
            }
        }

        if (!datasetContents.isEmpty()) {
            newDataSetContentEvent.fire(new NewDataSetContentEvent(datasetContents));
        }
        NavTree navTree = runtimeNavigationBuilder.build(navTreeOp, layoutTemplates);

        return new RuntimeModel(navTree, layoutTemplates);
    }

    private LayoutTemplate retrieveLayoutTemplate(final ZipInputStream zis) {
        String content = nextEntryContent(zis);
        return gson.fromJson(content, LayoutTemplate.class);
    }

    private DataSetContent retrieveDataSetContent(final ZipEntry entry, final ZipInputStream zis) {
        String fileName = entry.getName().split("/")[3];
        String[] nameParts = fileName.split("\\.");
        String id = nameParts[0];
        String ext = nameParts[1];
        String content = nextEntryContent(zis);
        return new DataSetContent(id, content, DataSetContentType.fromFileExtension(ext));
    }

    private String nextEntryContent(final ZipInputStream zis) {
        try {
            final int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = 0;
            String output = "";
            while ((read = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                output = output.concat(new String(buffer, 0, read));
            }
            return output.trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}