/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;

/**
 * Utility class to convert XLS Decision Table parameters from their "$..."
 * format to the "@{...}" format required by the Templating mechanism used with
 * the BRL editors and related DRL generators. Template Keys must be unique
 * across the entire model.
 */
public class ParameterUtilities {

    private static final Pattern patternSingleParameter = Pattern.compile( "\\$param" );

    private static final Pattern patternIndexedParameter = Pattern.compile( "\\$\\d+" );

    private static final Pattern patternTemplateKey = Pattern.compile( "@\\{.+?\\}" );

    private static final Pattern patternUnwrapExistingQuotes = Pattern.compile( "\"(@\\{.+?\\})\"" );

    private static final String PARAMETER_PREFIX = "param";

    private int parameterCounter = 1;

    public String convertIndexedParametersToTemplateKeys( final String xlsTemplate,
                                                          final ParameterizedValueBuilder.Part part ) {
        //Replace indexed parameter place-holders
        final StringBuffer result = new StringBuffer();
        final Matcher matcherIndexedParameter = patternIndexedParameter.matcher( xlsTemplate );
        while ( matcherIndexedParameter.find() ) {
            matcherIndexedParameter.appendReplacement( result,
                                                       "@{" + PARAMETER_PREFIX + ( parameterCounter++ ) + "}" );
        }
        matcherIndexedParameter.appendTail( result );
        return assertTemplateKeyConversion( result,
                                            part );
    }

    public String convertSingleParameterToTemplateKey( final String xlsTemplate,
                                                       final ParameterizedValueBuilder.Part part ) {
        //Replace the single parameter place-holder
        final StringBuffer result = new StringBuffer();
        final Matcher matcherSingleParameter = patternSingleParameter.matcher( xlsTemplate );
        while ( matcherSingleParameter.find() ) {
            matcherSingleParameter.appendReplacement( result,
                                                      "@{" + PARAMETER_PREFIX + parameterCounter + "}" );
        }
        parameterCounter++;
        matcherSingleParameter.appendTail( result );
        return assertTemplateKeyConversion( result,
                                            part );
    }

    private String assertTemplateKeyConversion( final StringBuffer result,
                                                final ParameterizedValueBuilder.Part part ) {
        if ( part == ParameterizedValueBuilder.Part.LHS ) {
            final String expression = addQuotesToTemplateKeys( result ).toString();
            final ViabilityVisitor visitor = new ViabilityVisitor( expression );
            if ( visitor.isViable() ) {
                return expression;
            }
        }
        return result.toString();
    }

    public Set<String> extractTemplateKeys( final String template ) {
        //Extract Template Keys
        final Set<String> result = new LinkedHashSet<String>();
        final Matcher matcherTemplateKey = patternTemplateKey.matcher( template );
        while ( matcherTemplateKey.find() ) {
            String fullKey = matcherTemplateKey.group();
            result.add( fullKey.substring( 2,
                                           fullKey.length() - 1 ) );
        }
        return result;
    }

    // Ensure LHS Template Keys are enclosed in quotes otherwise DrlParser in GuidedDecisionTablePopulater
    // reports compilation errors and the XLS Column definition becomes FreeFormLine meaning all
    // fields need to have a value for DRL to be generated.
    private StringBuffer addQuotesToTemplateKeys( final StringBuffer source ) {
        final StringBuffer unwrapped = new StringBuffer();
        final Matcher unwrappedMatcher = patternUnwrapExistingQuotes.matcher( source );
        while ( unwrappedMatcher.find() ) {
            unwrappedMatcher.appendReplacement( unwrapped,
                                                unwrappedMatcher.group( 1 ) );
        }
        unwrappedMatcher.appendTail( unwrapped );

        final StringBuffer wrapped = new StringBuffer();
        final Matcher wrappedMatcher = patternTemplateKey.matcher( unwrapped );
        while ( wrappedMatcher.find() ) {
            wrappedMatcher.appendReplacement( wrapped,
                                              "\"" + wrappedMatcher.group( 0 ) + "\"" );
        }
        wrappedMatcher.appendTail( wrapped );

        return wrapped;
    }

    // There's a legacy test that uses Template Keys as Field Names (see "GuidedDecisionTableGeneratorListenerTest.testMultipleSingleParameters")
    // This is impossible to represent as anything other than a FreeFormLine. The most accurate way to identify whether a XLS Column definition
    // uses Template Keys on the LHS of an expression is to have Drools parse the expression and check RelationalExprDescr's LHS.
    private class ViabilityVisitor {

        private final String expression;

        ViabilityVisitor( final String expression ) {
            this.expression = expression;
        }

        private boolean isViable() {
            final DrlExprParser parser = new DrlExprParser( DrlParser.DEFAULT_LANGUAGE_LEVEL );
            final ConstraintConnectiveDescr result = parser.parse( expression );
            if ( parser.hasErrors() || result == null ) {
                return false;
            }
            try {
                for ( BaseDescr descr : result.getDescrs() ) {
                    visit( descr );
                }
            } catch ( RuleModelDRLPersistenceImpl.RuleModelUnmarshallingException e ) {
                return false;
            }
            return true;
        }

        private void visit( final BaseDescr descr ) {
            if ( descr instanceof RelationalExprDescr ) {
                visit( ( (RelationalExprDescr) descr ).getLeft() );
            } else if ( descr instanceof AtomicExprDescr ) {
                visit( (AtomicExprDescr) descr );
            }
        }

        private void visit( final AtomicExprDescr descr ) {
            if ( descr.getExpression().contains( "@{" ) ) {
                throw new RuleModelDRLPersistenceImpl.RuleModelUnmarshallingException();
            }
        }

    }

}
