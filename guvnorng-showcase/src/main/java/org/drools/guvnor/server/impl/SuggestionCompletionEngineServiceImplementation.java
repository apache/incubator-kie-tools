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

import org.drools.guvnor.shared.SuggestionCompletionEngineService;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.util.LoggingHelper;
import org.uberfire.java.nio.fs.jgit.JGitRepositoryConfiguration;

@Service
@ApplicationScoped
public class SuggestionCompletionEngineServiceImplementation
        implements SuggestionCompletionEngineService {

    private static final LoggingHelper log = LoggingHelper.getLogger(SuggestionCompletionEngineService.class);

    //@Inject
    private RulesRepository rulesRepository = null;

    
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) {
   	
                JGitRepositoryConfiguration jGitRepositoryConfiguration = new JGitRepositoryConfiguration();
                //Add this newly created repository configuration info to the in-memory cache of repository configuration list
                jGitRepositoryConfiguration.setRepositoryName("guvnorng-playground");
                jGitRepositoryConfiguration.setGitURL("https://github.com/guvnorngtestuser1/guvnorng-playground.git");
                jGitRepositoryConfiguration.setUserName("guvnorngtestuser1");
                jGitRepositoryConfiguration.setPassword("test1234");
                jGitRepositoryConfiguration.setRootURI(URI.create("jgit:///guvnorng-playground"));
                
                rulesRepository = new RulesRepository(jGitRepositoryConfiguration);
 
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        
        try {
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
            suggestionCompletionEngine = new SuggestionCompletionEngineLoaderInitializer().loadFor(packageItem);
        } catch (RulesRepositoryException e) {
            log.error("An error occurred loadSuggestionCompletionEngine: " + e.getMessage());
            //throw new SerializationException(e.getMessage());
        }
        return suggestionCompletionEngine;
    }
    
    public static void main(String[] args) throws Exception {
    	SuggestionCompletionEngineServiceImplementation sceService = new SuggestionCompletionEngineServiceImplementation();
    	SuggestionCompletionEngine sce = sceService.loadSuggestionCompletionEngine("mortgagesSample");
    	sce.getFactTypes();
    }
}
