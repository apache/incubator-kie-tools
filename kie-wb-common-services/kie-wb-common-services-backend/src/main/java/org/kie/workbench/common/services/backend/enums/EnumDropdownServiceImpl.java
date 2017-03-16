/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.core.util.MVELSafeHelper;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class EnumDropdownServiceImpl implements EnumDropdownService {

    private static final Logger logger = LoggerFactory.getLogger( EnumDropdownServiceImpl.class );

    @Inject
    private BuildInfoService buildInfoService;

    @Inject
    private KieProjectService projectService;

    @Override
    public String[] loadDropDownExpression( final Path resource,
                                            final String[] valuePairs,
                                            final String expression ) {

        //Lookup class-loader for Project (as the helper class can be a project dependency)
        final KieProject project = projectService.resolveProject( resource );
        if ( project == null ) {
            logger.error( "A Project could not be resolved for path '" + resource.toURI() + "'. No enums will be returned." );
            return null;
        }
        final KieModule module = buildInfoService.getBuildInfo( project ).getKieModuleIgnoringErrors();
        if ( module == null ) {
            logger.error( "A KieModule could not be resolved for path '" + resource.toURI() + "'. No enums will be returned." );
            return null;
        }
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();

        return loadDropDownExpression( classLoader,
                                       valuePairs,
                                       expression );
    }

    protected String[] loadDropDownExpression( final ClassLoader classLoader,
                                               final String[] valuePairs,
                                               String expression ) {
        try {
            final Map<String, String> context = new HashMap<String, String>();
            for ( final String valuePair : valuePairs ) {
                if ( valuePair == null ) {
                    return new String[ 0 ];
                }
                String[] pair = valuePair.split( "=" );
                if ( pair.length == 1 ) {
                    String[] swap = new String[ 2 ];
                    swap[ 0 ] = pair[ 0 ];
                    swap[ 1 ] = "";
                    pair = swap;
                }
                context.put( pair[ 0 ],
                             pair[ 1 ] );
            }

            // first interpolate the pairs
            expression = (String) TemplateRuntime.eval( expression,
                                                        context );

            // now we can eval it for real...
            final ParserConfiguration pconf = new ParserConfiguration();
            final ParserContext pctx = new ParserContext( pconf );
            pconf.setClassLoader( classLoader );

            final Serializable compiled = MVEL.compileExpression( expression,
                                                                  pctx );
            Object result = MVELSafeHelper.getEvaluator().executeExpression( compiled,
                                                                             new HashMap<String, Object>() );

            //Handle result of evaluation
            if ( result instanceof String[] ) {
                return (String[]) result;
            } else if ( result instanceof List ) {
                List l = (List) result;
                String[] xs = new String[ l.size() ];
                for ( int i = 0; i < xs.length; i++ ) {
                    Object el = l.get( i );
                    xs[ i ] = el.toString();
                }
                return xs;
            } else {
                return null;
            }

        } catch ( Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
