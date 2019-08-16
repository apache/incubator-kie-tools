/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.service;

import java.io.InputStream;

import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;

@Remote
public interface DecisionTableXLSService
        extends ValidationService<Path>,
                SupportsDelete,
                SupportsCopy,
                SupportsRename {

    ConversionResult convert( final Path path );

    DecisionTableXLSContent loadContent( final Path path );

    String getSource( final Path path );

    Path create( final Path resource,
                 final InputStream content,
                 final String sessionId,
                 final String comment );

}
