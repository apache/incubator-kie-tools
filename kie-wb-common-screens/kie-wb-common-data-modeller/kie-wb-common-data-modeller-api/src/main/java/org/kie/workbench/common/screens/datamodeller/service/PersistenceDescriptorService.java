/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.service;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorModel;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsUpdate;

@Remote
public interface PersistenceDescriptorService
        extends ViewSourceService<PersistenceDescriptorModel>,
                ValidationService<PersistenceDescriptorModel>,
                SupportsRead<PersistenceDescriptorModel>,
                SupportsUpdate<PersistenceDescriptorModel, Metadata> {

    public static final String PERSISTENCE_DESCRIPTOR_PATH = "src/main/resources/META-INF/persistence.xml";

    PersistenceDescriptorModel createModuleDefaultDescriptor(final Path path);

    PersistenceDescriptorModel load(final Module module);

    Path calculatePersistenceDescriptorPath(final Module module);
}
