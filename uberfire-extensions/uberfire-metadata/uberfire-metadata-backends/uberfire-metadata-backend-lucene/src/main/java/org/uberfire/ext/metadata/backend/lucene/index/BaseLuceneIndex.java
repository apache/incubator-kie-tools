/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.backend.lucene.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public abstract class BaseLuceneIndex implements LuceneIndex {

    @Override
    public void indexDocument(final String id,
                              final Document doc) {
        try {
            deleteIfExists(id);
            writer().addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteIfExists(final String... docIds) {
        boolean deletedSomething = false;
        final IndexSearcher searcher = nrtSearcher();
        try {
            final int[] answers = lookupDocIdByPK(searcher,
                                                  docIds);
            for (final int docId : answers) {
                if (docId != -1) {
                    long result = writer().tryDeleteDocument(searcher.getIndexReader(),
                                                             docId);
                    if (result >= 0) {
                        deletedSomething = true;
                    }
                }
            }
        } catch (Exception ex) {
        } finally {
            nrtRelease(searcher);
        }
        return deletedSomething;
    }

    @Override
    public void rename(final String sourceId,
                       final Document doc) {
        final IndexSearcher searcher = nrtSearcher();
        try {
            int docId = lookupDocIdByPK(searcher,
                                        sourceId)[0];
            if (docId != -1) {
                writer().tryDeleteDocument(searcher.getIndexReader(),
                                           docId);
            }
            indexDocument(sourceId,
                          doc);
        } catch (IOException ex) {
        } finally {
            nrtRelease(searcher);
        }
    }

    protected int[] lookupDocIdByPK(final IndexSearcher searcher,
                                    final String... ids) throws IOException {
        final List<LeafReaderContext> subReaders = searcher.getIndexReader().leaves();
        final TermsEnum[] termsEnums = new TermsEnum[subReaders.size()];
        final PostingsEnum[] docsEnums = new PostingsEnum[subReaders.size()];
        for (int subIDX = 0; subIDX < subReaders.size(); subIDX++) {
            termsEnums[subIDX] = subReaders.get(subIDX).reader().fields().terms("id").iterator();
        }

        int[] results = new int[ids.length];

        for (int i = 0; i < results.length; i++) {
            results[i] = -1;
        }

        // for each id given
        for (int idx = 0; idx < ids.length; idx++) {
            int base = 0;
            final BytesRef id = new BytesRef(ids[idx]);
            // for each leaf reader..
            for (int subIDX = 0; subIDX < subReaders.size(); subIDX++) {
                final LeafReader subReader = subReaders.get(subIDX).reader();
                final TermsEnum termsEnum = termsEnums[subIDX];
                // does the enumeration of ("id") terms from our reader contain the "id" field we're looking for?
                if (termsEnum.seekExact(id)) {
                    final PostingsEnum docs = docsEnums[subIDX] = termsEnum.postings(docsEnums[subIDX],
                                                                                     0);
                    // okay, the reader contains it, get the postings ("docs+") for and check that they're there (NP check)
                    if (docs != null) {
                        final int docID = docs.nextDoc();
                        Bits liveDocs = subReader.getLiveDocs();
                        // But wait, maybe some of the docs have been deleted! Check that too..
                        if ((liveDocs == null || liveDocs.get(docID)) && docID != DocIdSetIterator.NO_MORE_DOCS) {
                            results[idx] = base + docID;
                            break;
                        }
                    }
                }
                base += subReader.maxDoc();
            }
        }

        return results;
    }

    public abstract IndexWriter writer();
}
