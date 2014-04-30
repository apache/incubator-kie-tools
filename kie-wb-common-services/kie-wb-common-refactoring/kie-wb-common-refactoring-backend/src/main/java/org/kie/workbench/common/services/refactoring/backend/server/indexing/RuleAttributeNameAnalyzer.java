/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.util.Version;
import org.uberfire.metadata.backend.lucene.analyzer.LowerCaseTokenizer;

/**
 * A specialise analyzer to include special characters in attribute names, such as "-"
 */
public class RuleAttributeNameAnalyzer extends Analyzer {

    private final Version matchVersion;

    public RuleAttributeNameAnalyzer( final Version matchVersion ) {
        super();
        this.matchVersion = matchVersion;
    }

    @Override
    protected TokenStreamComponents createComponents( final String fieldName,
                                                      final Reader reader ) {
        final LowerCaseTokenizer src = new LowerCaseTokenizer( matchVersion,
                                                               reader );
        final TokenStream tok = new LowerCaseFilter( matchVersion,
                                                     src );

        return new TokenStreamComponents( src,
                                          tok );
    }
}
