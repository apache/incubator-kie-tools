/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.lucene.provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.engine.IndexManager;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.ext.metadata.search.ClusterSegment;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.toKObject;
import static org.uberfire.ext.metadata.engine.MetaIndexEngine.FULL_TEXT_FIELD;

public class LuceneIndexProvider implements IndexProvider {

    private final FieldFactory fieldFactory;
    private IndexManager indexManager;

    public LuceneIndexProvider(IndexManager indexManager,
                               FieldFactory fieldFactory) {
        this.indexManager = indexManager;
        this.fieldFactory = fieldFactory;
    }

    @Override
    public boolean isFreshIndex(final KCluster cluster) {
        final Index index = indexManager.get(cluster);
        return index == null || index.freshIndex();
    }

    @Override
    public void index(KObject object) {
        final LuceneIndex index = (LuceneIndex) indexManager.indexOf(object);
        index.indexDocument(object.getId(),
                            newDocument(object));
        index.commit();
    }

    @Override
    public void index(List<KObject> elements) {
        elements.forEach(ko -> {
            final LuceneIndex index = (LuceneIndex) indexManager.indexOf(ko);
            index.indexDocument(ko.getId(),
                                newDocument(ko));
        });
        elements.stream()
                .map(ko -> (LuceneIndex) indexManager.indexOf(ko))
                .collect(Collectors.toSet())
                .forEach(luceneIndex -> luceneIndex.commit());
    }

    @Override
    public boolean exists(String index,
                          String id) {
        return this.findById(index,
                             id).size() > 0;
    }

    @Override
    public void delete(String index) {
        indexManager.delete(new KClusterImpl(index));
    }


    @Override
    public void delete(String index,
                       String id) {
        final LuceneIndex luceneIndex = (LuceneIndex) indexManager.get(new KClusterImpl(index));
        luceneIndex.deleteIfExists(id);
        luceneIndex.commit();
    }

    @Override
    public List<KObject> findById(String index,
                                  String id) {
        List<String> indices = Arrays.asList(index);
        ScoreDoc[] docs = this.findRawByQuery(indices,
                                              new TermQuery(new Term("id",
                                                                     id)),
                                              null,
                                              0);

        return Arrays.stream(docs)
                .map(scoreDoc -> createKObject(indices,
                                               scoreDoc))
                .collect(Collectors.toList());
    }

    @Override
    public void rename(String index,
                       String id,
                       KObject to) {
        checkNotNull("from",
                     index);
        checkNotNull("to",
                     to);
        checkCondition("renames are allowed only from same cluster",
                       index.equals(to.getClusterId()));
        LuceneIndex luceneIndex = ((LuceneIndex) indexManager.get(new KClusterImpl(index)));
        luceneIndex.rename(id,
                           newDocument(to));
    }

    @Override
    public long getIndexSize(String index) {
        LuceneIndex luceneIndex = ((LuceneIndex) indexManager.get(new KClusterImpl(index)));
        return luceneIndex.nrtReader().numDocs();
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     int limit) {

        ScoreDoc[] docs = this.findRawByQuery(indices,
                                              query,
                                              null,
                                              0);

        return Arrays.stream(docs)
                .map(scoreDoc -> createKObject(indices,
                                               scoreDoc))
                .collect(Collectors.toList());
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     Sort sort,
                                     int limit) {
        ScoreDoc[] docs = this.findRawByQuery(indices,
                                              query,
                                              sort,
                                              0);

        return Arrays.stream(docs)
                .map(scoreDoc -> createKObject(indices,
                                               scoreDoc))
                .collect(Collectors.toList());
    }

    private KObject createKObject(List<String> indices,
                                  ScoreDoc scoreDoc) {
        try {
            IndexSearcher searcher = ((LuceneIndexManager) indexManager)
                    .getIndexSearcher(toClusterSegments(indices));
            return toKObject(searcher.doc(scoreDoc.doc));
        } catch (IOException e) {
            throw new RuntimeException("Can't convert document to KObject");
        }
    }

    @Override
    public long findHitsByQuery(List<String> indices,
                                Query query) {

        return this.findRawByQuery(indices,
                                   query,
                                   null,
                                   0).length;
    }

    @Override
    public List<String> getIndices() {
        return this.indexManager.getIndices();
    }

    @Override
    public void observerInitialization(Runnable runnable) {
        // Do nothing
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    public ScoreDoc[] findRawByQuery(List<String> indices,
                                     Query query,
                                     Sort sort,
                                     int limit) {

        try {
            ClusterSegment[] clusterSegments = toClusterSegments(indices);
            IndexSearcher searcher = ((LuceneIndexManager) indexManager).getIndexSearcher(clusterSegments);
            int n = Integer.MAX_VALUE;
            if (limit > 0) {
                n = limit;
            }
            TopDocs topDocs;
            if (sort != null) {
                topDocs = searcher.search(query,
                                          n,
                                          sort);
            } else {
                topDocs = searcher.search(query,
                                          n);
            }
            return topDocs.scoreDocs;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ScoreDoc[0];
    }

    private ClusterSegment[] toClusterSegments(List<String> indices) {
        return indices.stream().map(index -> new ClusterSegment() {

            @Override
            public String getClusterId() {
                return index;
            }

            @Override
            public String[] segmentIds() {
                return new String[0];
            }
        }).toArray(size -> new ClusterSegment[size]);
    }

    @Override
    public void dispose() {

    }

    private Document newDocument(final KObject object) {
        final Document doc = new Document();

        doc.add(new StringField(MetaObject.META_OBJECT_ID,
                                object.getId(),
                                Field.Store.YES));
        doc.add(new StringField(MetaObject.META_OBJECT_TYPE,
                                object.getType().getName(),
                                Field.Store.YES));
        doc.add(new TextField(MetaObject.META_OBJECT_KEY,
                              object.getKey(),
                              Field.Store.YES));
        doc.add(new StringField(MetaObject.META_OBJECT_CLUSTER_ID,
                                object.getClusterId(),
                                Field.Store.YES));
        doc.add(new StringField(MetaObject.META_OBJECT_SEGMENT_ID,
                                object.getSegmentId(),
                                Field.Store.YES));

        final StringBuilder allText = new StringBuilder(object.getKey()).append('\n');

        for (final KProperty<?> property : object.getProperties()) {
            final IndexableField[] fields = fieldFactory.build(property);
            for (final IndexableField field : fields) {
                doc.add(field);
                if (field instanceof TextField && !(property.getValue() instanceof Boolean)) {
                    allText.append(field.stringValue()).append('\n');
                }
            }
        }

        //Only create a "full text" entry if required
        if (object.fullText()) {
            doc.add(new TextField(FULL_TEXT_FIELD,
                                  allText.toString().toLowerCase(),
                                  Field.Store.NO));
        }

        return doc;
    }
}
