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

package org.kie.workbench.common.forms.editor.backend.indexing;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class FormDefinitionIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(FormDefinitionIndexer.class);

    protected FormResourceTypeDefinition formResourceTypeDefinition;

    protected FormDefinitionSerializer formDefinitionSerializer;

    protected Map<Class, FormModelVisitorProvider> visitorProviders = new HashMap<>();

    @Inject
    public FormDefinitionIndexer(FormResourceTypeDefinition formResourceTypeDefinition,
                                 FormDefinitionSerializer formDefinitionSerializer,
                                 Instance<FormModelVisitorProvider<? extends FormModel>> visitorProviderInstance) {
        this.formResourceTypeDefinition = formResourceTypeDefinition;
        this.formDefinitionSerializer = formDefinitionSerializer;
        visitorProviderInstance.iterator().forEachRemaining(visitorProvider -> visitorProviders.put(visitorProvider.getModelType(),
                                                                                                    visitorProvider));
    }

    @Override
    protected IndexBuilder fillIndexBuilder(Path path) throws Exception {

        final DefaultIndexBuilder builder = getIndexBuilder(path);
        if (builder == null) {
            return null;
        }

        String formContent = ioService.readAllString(path);

        FormDefinition formDefinition = formDefinitionSerializer.deserialize(formContent);

        if (formDefinition != null) {

            Resource resParts = new Resource(formDefinition.getName(),
                                             ResourceType.FORM);

            FormDefinitionVisitor visitor = new FormDefinitionVisitor(resParts);

            visitor.visit(formDefinition);

            addReferencedResourcesToIndexBuilder(builder,
                                                 visitor);

            FormModel model = formDefinition.getModel();

            if (model != null) {
                FormModelVisitorProvider provider = getProviderForModel(model.getClass());
                if (provider != null) {
                    FormModelVisitor modelVisitor = provider.getVisitor();
                    modelVisitor.index(formDefinition,
                                       model);
                    addReferencedResourcesToIndexBuilder(builder,
                                                         modelVisitor);
                }
            }
        } else {
            logger.error("Unable to read FormDefinition on " + path.toUri().toString() + ".");
            return null;
        }

        return builder;
    }

    protected FormModelVisitorProvider getProviderForModel(Class<? extends FormModel> modelClass) {
        return visitorProviders.get(modelClass);
    }

    @Override
    public boolean supportsPath(Path path) {
        return formResourceTypeDefinition.accept(Paths.convert(path));
    }
}
