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
package org.kie.workbench.common.services.refactoring.model.index.terms;

/**
 * An index term provides the key for a lucene field.
 * </p>
 * In short, a document (produced by an indexer) that's added to the Lucene index, is comprised of "fields", which are key-value pairs.
 * We query the Lucene index on these key-value pairs in order to find the document, and use the info from the document to find
 * out more about the resource that the Lucene document refers to.
 * </p>
 * The term returned by the {@link #getTerm()} method returns the key used in the document field.
 */
public interface IndexTerm {

    String REFACTORING_CLASSIFIER = "refactor-info";

    String getTerm();

}
