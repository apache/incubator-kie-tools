package org.drools.repository.utils;

import org.drools.repository.RulesRepositoryException;

/*
 * Copyright 2011 JBoss Inc
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

/**
 * Utilities relating to a ModuleItem.
 */
public class NodeUtils {

    /**
     * Construct a validate JCR Node path name per JSR-170 from an asset name.
     * Only the following characters are valid: char ::= nonspace | ' ' nonspace
     * ::= (* Any Unicode character except: '/', ':', '[', ']', '*', ''', '"',
     * '|' or any whitespace character *). Invalid characters are replaced with
     * an underscore ("_").
     * 
     * @param assetName
     * @return a JSR-170 complaint path
     */
    public static String makeJSR170ComplaintName(String assetName) {
        if ( assetName == null || assetName.length() == 0 ) {
            throw new RulesRepositoryException( "An Asset's name cannot be null or empty." );
        }
        assetName = assetName.replaceAll( "[/:/*\\[\\]'\"|]", "_" );
        return assetName;
    }

}
