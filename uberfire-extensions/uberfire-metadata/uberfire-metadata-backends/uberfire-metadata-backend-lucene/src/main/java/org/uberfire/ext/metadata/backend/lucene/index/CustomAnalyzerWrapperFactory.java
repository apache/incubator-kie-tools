/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.metadata.backend.lucene.index;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;

/**
 * This factory allows us to extend the granularity used when configuring {@link Analyzer} instances.
 * </p>
 * For example, with normal configuration, {@link Analyzer}s can only be applied to fields that start with a pre-defined prefix.
 * <ul>
 * <li>For example: use the {@link FilenameAnalyzer} for all fields with the key "projectRoot"
 * </ul>
 * However, you can not use regular expressions when configuring {@link Analyzer}s normally, which means that it is impossible
 * to configure an analyzer if you have a set of fields that have a pre-defined <em>prefix</em> but a dynamically-determined
 * suffix (and we have that for change impact).
 * </p>
 * This factory allows us to define our own {@link Analyzer} which can have its own logic to deal with this problem.
 * </p>
 * This is then the primary {@link Analyzer} defined for the Lucene engine. If the logic in this (wrapper) {@link Analyzer}
 * does not match a field, it then delegates the field to the {@link Analyzer}s defined in the <code>fieldAnalyzer</code> parameter.
 * </p>
 * (At least, that's an example of what it can and should do, but that's up to the implementation details).
 * @see LuceneConfigBuilder#withDefaultAnalyzer()
 * @see LuceneConfigBuilder#usingAnalyzerWrapperFactory(CustomAnalyzerWrapperFactory)
 */
public interface CustomAnalyzerWrapperFactory {

    public Analyzer getAnalyzerWrapper(Analyzer defaultAnalyzer,
                                       Map<String, Analyzer> fieldAnalyzers);
}
