/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.editor.service.shared.ModuleFormFinderService;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.util.VFSScanner;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@Dependent
@Service
public class ModuleFormFinderServiceImpl implements ModuleFormFinderService {

    private static final Logger logger = LoggerFactory.getLogger(ModuleFormFinderServiceImpl.class);

    private IOService ioService;

    private KieModuleService moduleService;

    private FormDefinitionSerializer serializer;

    private BuildInfoService buildInfoService;

    @Inject
    public ModuleFormFinderServiceImpl(final @Named("ioStrategy") IOService ioService,
                                       final KieModuleService moduleService,
                                       final FormDefinitionSerializer serializer,
                                       final BuildInfoService buildInfoService) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.serializer = serializer;
        this.buildInfoService = buildInfoService;
    }

    @Override
    public List<FormDefinition> findAllForms(Path path) {
        return findForms(path, formDefinition -> true);
    }

    @Override
    public List<FormDefinition> findFormsForType(final String typeName, final Path path) {
        return findForms(path, formDefinition -> formByType(formDefinition, typeName));
    }

    private boolean formByType(final FormDefinition formDefinition, final String typeName) {
        if (formDefinition.getModel() instanceof JavaFormModel) {
            return ((JavaFormModel) formDefinition.getModel()).getType().equals(typeName);
        }

        return false;
    }

    @Override
    public FormDefinition findFormById(final String id, final Path path) {
        List<FormDefinition> forms = findForms(path, formDefinition -> formById(formDefinition, id));

        if (forms != null && !forms.isEmpty()) {
            return forms.get(0);
        }
        return null;
    }

    private boolean formById(final FormDefinition formDefinition, final String id) {
        return formDefinition.getId().equals(id);
    }

    private List<FormDefinition> findForms(final Path path, final Predicate<FormDefinition> predicate) {

        KieModule module = moduleService.resolveModule(path);

        org.uberfire.java.nio.file.Path nioPath = Paths.convert(module.getRootPath());

        List<FormDefinition> moduleForms = VFSScanner.scan(ioService, nioPath, Collections.singleton(FormResourceTypeDefinition.EXTENSION), this::convert, predicate).stream()
                .map(VFSScanner.ScanResult::getResource)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> moduleFormsIds = moduleForms.stream()
                .map(FormDefinition::getId)
                .collect(Collectors.toList());

        Map<String, String> dependenciesForms = buildInfoService.getBuildInfo(module).getKieModuleMetaDataIgnoringErrors().getForms();

        return dependenciesForms.values().stream()
                .map(serializer::deserialize)
                .filter(predicate)
                .filter(formDefinition -> !moduleFormsIds.contains(formDefinition))
                .collect(Collectors.toCollection(() -> moduleForms));
    }

    private FormDefinition convert(InputStream in) {
        try {
            String content = IOUtils.toString(in, Charset.defaultCharset());
            return serializer.deserialize(content);
        } catch (IOException e) {
            logger.warn("Cannot read parse form due to: ", e);
        }
        return null;
    }
}
