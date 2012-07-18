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

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.guvnor.shared.SuggestionCompletionEngineService;

import org.drools.guvnor.vfs.Path;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class SuggestionCompletionEngineServiceImplementation
        implements SuggestionCompletionEngineService {

    private static final LoggingHelper log = LoggingHelper.getLogger(SuggestionCompletionEngineService.class);

    public SuggestionCompletionEngine loadSuggestionCompletionEngine(Path packageRootDir) throws SerializationException {
        //No need to check role based permission here. Package auto completion suggestion should be available to everybody.
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        try {
            suggestionCompletionEngine = new SuggestionCompletionEngineLoaderInitializer().loadFor(packageRootDir);
        } catch (Exception e) {
            log.error("An error occurred loadSuggestionCompletionEngine: " + e.getMessage());
            throw new SerializationException(e.getMessage());
        }
        return suggestionCompletionEngine;
    }
}
