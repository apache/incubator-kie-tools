/*
 * Copyright 2012 JBoss Inc
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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to convert XLS Decision Table parameters from their "$..."
 * format to the "@{...}" format required by the Templating mechanism used with
 * the BRL editors and related DRL generators. Template Keys must be unique
 * across the entire model.
 */
public class ParameterUtilities {

    private static final Pattern patternSingleParameter  = Pattern.compile( "\\$param" );

    private static final Pattern patternIndexedParameter = Pattern.compile( "\\$\\d+" );

    private static final Pattern patternTemplateKey      = Pattern.compile( "@\\{.+?\\}" );

    private static final String  PARAMETER_PREFIX        = "param";

    private int                  parameterCounter        = 1;

    public String convertIndexedParametersToTemplateKeys(final String xlsTemplate) {
        //Replace indexed parameter place-holders
        final StringBuffer result = new StringBuffer();
        final Matcher matcherIndexedParameter = patternIndexedParameter.matcher( xlsTemplate );
        while ( matcherIndexedParameter.find() ) {
            matcherIndexedParameter.appendReplacement( result,
                                                       "@{" + PARAMETER_PREFIX + (parameterCounter++) + "}" );
        }
        matcherIndexedParameter.appendTail( result );
        return result.toString();
    }

    public String convertSingleParameterToTemplateKey(String xlsTemplate) {
        //Replace the single parameter place-holder
        StringBuffer result = new StringBuffer();
        final Matcher matcherSingleParameter = patternSingleParameter.matcher( xlsTemplate );
        while ( matcherSingleParameter.find() ) {
            matcherSingleParameter.appendReplacement( result,
                                                      "@{" + PARAMETER_PREFIX + parameterCounter + "}" );
        }
        parameterCounter++;
        matcherSingleParameter.appendTail( result );
        return result.toString();
    }

    public Set<String> extractTemplateKeys(String template) {
        //Extract Template Keys
        Set<String> result = new HashSet<String>();
        final Matcher matcherTemplateKey = patternTemplateKey.matcher( template );
        while ( matcherTemplateKey.find() ) {
            String fullKey = matcherTemplateKey.group();
            result.add( fullKey.substring( 2,
                                           fullKey.length() - 1 ) );
        }
        return result;
    }

}
