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
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.templates.TemplateRuntime;

@Service
@ApplicationScoped
public class EnumDropdownServiceImpl implements EnumDropdownService {

    @Inject
    private LRUBuilderCache builderCache;

    @Override
    public String[] loadDropDownExpression( final String[] valuePairs,
                                            final String expression ) {
        return load( Thread.currentThread().getContextClassLoader(),
                     valuePairs,
                     expression );
    }

    @Override
    public String[] loadDropDownExpression( final KieProject project,
                                            final String[] valuePairs,
                                            final String expression ) {
        //Lookup class-loader for Project (as the helper class can be a project dependency)
        final KieModule module = builderCache.assertBuilder( project ).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData( module ).getClassLoader();

        return load( classLoader,
                     valuePairs,
                     expression );
    }

    private String[] load( final ClassLoader classLoader,
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
