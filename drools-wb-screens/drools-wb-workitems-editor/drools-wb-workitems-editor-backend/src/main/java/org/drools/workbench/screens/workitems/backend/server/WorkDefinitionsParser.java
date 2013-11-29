/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.workitems.backend.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.process.core.ParameterDefinition;
import org.drools.core.process.core.WorkDefinition;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.datatype.impl.type.BooleanDataType;
import org.drools.core.process.core.datatype.impl.type.EnumDataType;
import org.drools.core.process.core.datatype.impl.type.FloatDataType;
import org.drools.core.process.core.datatype.impl.type.IntegerDataType;
import org.drools.core.process.core.datatype.impl.type.ListDataType;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.process.core.datatype.impl.type.StringDataType;
import org.drools.core.process.core.datatype.impl.type.UndefinedDataType;
import org.drools.core.process.core.impl.ParameterDefinitionImpl;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

/**
 * Class to load Work Definitions
 */
public class WorkDefinitionsParser {

    /**
     * Parse a MVEL String into WorkDefinitions
     * @param workItemDefinitions
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, WorkDefinition> parse( final List<String> workItemDefinitions ) {

        final Map<String, WorkDefinition> workDefinitions = new HashMap<String, WorkDefinition>();

        //Add Data-type imports, in-case they are missing from definition
        final ParserContext context = new ParserContext();
        context.addImport( "ObjectDataType",
                           ObjectDataType.class );
        context.addImport( "StringDataType",
                           StringDataType.class );
        context.addImport( "IntegerDataType",
                           IntegerDataType.class );
        context.addImport( "FloatDataType",
                           FloatDataType.class );
        context.addImport( "BooleanDataType",
                           BooleanDataType.class );
        context.addImport( "ListDataType",
                           ListDataType.class );
        context.addImport( "EnumDataType",
                           EnumDataType.class );
        context.addImport( "UndefinedDataType",
                           UndefinedDataType.class );

        //Compile expression and convert String
        for ( String workItemDefinition : workItemDefinitions ) {

            final Serializable compiled = MVEL.compileExpression( workItemDefinition,
                                                                  context );
            final Object result = MVELSafeHelper.getEvaluator().executeExpression( compiled,
                                                                                   new HashMap() );
            final List<Map<String, Object>> workDefinitionsMap = (List<Map<String, Object>>) result;

            //Populate model
            if ( workDefinitionsMap != null ) {
                for ( Map<String, Object> workDefinitionMap : workDefinitionsMap ) {

                    if ( workDefinitionMap != null ) {
                        final WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                        workDefinition.setName( (String) workDefinitionMap.get( "name" ) );
                        workDefinition.setDisplayName( (String) workDefinitionMap.get( "displayName" ) );
                        workDefinition.setIcon( (String) workDefinitionMap.get( "icon" ) );
                        workDefinition.setCustomEditor( (String) workDefinitionMap.get( "customEditor" ) );
                        final Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
                        if ( workDefinitionMap.get( "parameters" ) != null ) {
                            final Map<String, DataType> parameterMap = (Map<String, DataType>) workDefinitionMap.get( "parameters" );
                            if ( parameterMap != null ) {
                                for ( Map.Entry<String, DataType> entry : parameterMap.entrySet() ) {
                                    parameters.add( new ParameterDefinitionImpl( entry.getKey(),
                                                                                 entry.getValue() ) );
                                }
                            }
                            workDefinition.setParameters( parameters );
                        }

                        if ( workDefinitionMap.get( "results" ) != null ) {
                            final Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
                            final Map<String, DataType> resultMap = (Map<String, DataType>) workDefinitionMap.get( "results" );
                            if ( resultMap != null ) {
                                for ( Map.Entry<String, DataType> entry : resultMap.entrySet() ) {
                                    results.add( new ParameterDefinitionImpl( entry.getKey(),
                                                                              entry.getValue() ) );
                                }
                            }
                            workDefinition.setResults( results );
                        }
                        if ( workDefinitionMap.get( "defaultHandler" ) != null ) {
                            workDefinition.setDefaultHandler( (String) workDefinitionMap.get( "defaultHandler" ) );
                        }
                        if ( workDefinitionMap.get( "dependencies" ) != null ) {
                            workDefinition.setDependencies( ( (List<String>) workDefinitionMap.get( "dependencies" ) ).toArray( new String[ 0 ] ) );
                        }
                        workDefinitions.put( workDefinition.getName(),
                                             workDefinition );
                    }
                }
            }
        }
        return workDefinitions;
    }

}
