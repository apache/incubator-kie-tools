/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.service;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;

@Remote
public interface POMService extends SupportsRead<POM>,
                                    SupportsUpdate<POM> {

    /**
     * @param projectRoot Root of the project
     * @param pom Model for the pom.xml
     * @return
     */
    Path create(final Path projectRoot,
                final POM pom);

    Path save(final Path path,
              final POM content,
              final Metadata metadata,
              final String comment,
              final boolean updateModules);
}
