/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.datamodel.backend.server.cache;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.drools.compiler.lang.dsl.DSLMappingEntry;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.workbench.models.datamodel.oracle.DSLActionSentence;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.kie.soup.project.datamodel.oracle.ExtensionKind;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.DirectoryStream.Filter;
import org.uberfire.java.nio.file.Path;

@Dependent
public class DSLExtension implements DataModelExtension {

    public static final DSLFileFilter DSL_FILE_FILTER = new DSLFileFilter();
    private static final Logger log = LoggerFactory.getLogger(DSLExtension.class);

    private static class DSLMapping implements ExtensionMapping<DSLSentence> {

        private ExtensionKind<DSLSentence> kind;
        private List<DSLSentence> sentences;

        DSLMapping(ExtensionKind<DSLSentence> kind, List<DSLSentence> sentences) {
            this.kind = kind;
            this.sentences = sentences;
        }

        @Override
        public ExtensionKind<DSLSentence> getKind() {
            return kind;
        }

        @Override
        public List<DSLSentence> getValues() {
            return sentences;
        }
    }

    @Override
    public Filter<Path> getFilter() {
        return DSL_FILE_FILTER;
    }

    @Override
    public List<ExtensionMapping<?>> getExtensions(Path path, String content) {
        final DSLTokenizedMappingFile dslLoader = new DSLTokenizedMappingFile();
        List<DSLSentence> actionSentences = new ArrayList<>();
        List<DSLSentence> conditionSentences = new ArrayList<>();
        try {
            if (dslLoader.parseAndLoad(new StringReader(content))) {
                for (DSLMappingEntry entry : dslLoader.getMapping().getEntries()) {
                    if (entry.getSection() == DSLMappingEntry.CONDITION) {
                        final DSLMappingEntry definition = entry;
                        final DSLSentence sentence = new DSLSentence();
                        sentence.setDrl(definition.getMappingValue());
                        sentence.setDefinition(definition.getMappingKey());
                        conditionSentences.add(sentence);
                    } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
                        final DSLMappingEntry definition = entry;
                        final DSLSentence sentence = new DSLSentence();
                        sentence.setDrl(definition.getMappingValue());
                        sentence.setDefinition(definition.getMappingKey());
                        actionSentences.add(sentence);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return Arrays.asList(new DSLMapping(DSLActionSentence.INSTANCE, actionSentences),
                             new DSLMapping(DSLConditionSentence.INSTANCE, conditionSentences));
    }
}
