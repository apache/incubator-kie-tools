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

package org.uberfire.metadata.backend.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.uberfire.metadata.model.KCluster;

/**
 *
 */
public interface LuceneIndex {

    public static final String CUSTOM_FIELD_FILENAME = "filename";

    KCluster getCluster();

    void indexDocument( final String id,
                        final Document doc );

    boolean deleteIfExists( final String... ids );

    void rename( final String sourceId,
                 final Document doc );

    IndexReader nrtReader();

    void nrtRelease( final IndexReader searcher );

    IndexSearcher nrtSearcher();

    void nrtRelease( final IndexSearcher searcher );

    void dispose();

    boolean freshIndex();

    void commit();

    void delete();
}
