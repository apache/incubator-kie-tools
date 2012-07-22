/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.server.impl;

import java.net.URI;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.backend.util.LoggingHelper;
import org.drools.guvnor.shared.SuggestionCompletionEngineService;
import org.drools.guvnor.shared.SuggestionCompletionEngineServiceVFS;
import org.drools.guvnor.vfs.Path;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.java.nio.file.Paths;
import org.jboss.errai.bus.server.annotations.Service;

import com.google.gwt.user.client.rpc.SerializationException;

@Service
@ApplicationScoped
public class SuggestionCompletionEngineServiceImplementationVFS
        implements SuggestionCompletionEngineServiceVFS {

    private static final LoggingHelper log = LoggingHelper.getLogger(SuggestionCompletionEngineService.class);

    public SuggestionCompletionEngine loadSuggestionCompletionEngine(final Path packageRootDir) throws SerializationException {
        //No need to check role based permission here. Package auto completion suggestion should be available to everybody.
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        try {
            suggestionCompletionEngine = new SuggestionCompletionEngineLoaderInitializerVFS().loadFor( fromPath(packageRootDir) );
        } catch (Exception e) {
            log.error("An error occurred loadSuggestionCompletionEngine: " + e.getMessage());
            throw new SerializationException(e.getMessage());
        }
        return suggestionCompletionEngine;
    }

    private org.drools.java.nio.file.Path fromPath(final Path path) {
        //HACK: REVISIT: how to encode. We dont want to encode the whole URI string, we only want to encode the path element
        String pathString = path.toURI();
        pathString = pathString.replaceAll(" ", "%20");
        return Paths.get(URI.create(pathString));
    }

}
