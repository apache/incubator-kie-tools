/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.search;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.ext.metadata.search.DateRange;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.ext.metadata.search.SearchIndex;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;
import static org.apache.lucene.search.LegacyNumericRangeQuery.newLongRange;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.engine.MetaIndexEngine.FULL_TEXT_FIELD;

public class InfinispanSearchIndex implements SearchIndex {

    private final IndexProvider indexProvider;
    private final Analyzer analyzer;

    public InfinispanSearchIndex(IndexProvider indexProvider,
                                 Analyzer analyzer) {
        this.indexProvider = checkNotNull(indexProvider,
                                          "indexProvider");
        this.analyzer = checkNotNull(analyzer,
                                     "analyzer");
    }

    @Override
    public List<KObject> searchByAttrs(final Map<String, ?> attrs,
                                       final IOSearchService.Filter filter,
                                       final ClusterSegment... clusterSegments) {
        if (clusterSegments == null || clusterSegments.length == 0) {
            return emptyList();
        }
        if (attrs == null || attrs.size() == 0) {
            return emptyList();
        }
        final int totalNumHitsEstimate = searchByAttrsHits(attrs,
                                                           clusterSegments);
        return search(buildQuery(attrs,
                                 clusterSegments),
                      totalNumHitsEstimate,
                      filter,
                      clusterSegments);
    }

    @Override
    public List<KObject> fullTextSearch(final String term,
                                        final IOSearchService.Filter filter,
                                        final ClusterSegment... clusterSegments) {
        if (clusterSegments == null || clusterSegments.length == 0) {
            return emptyList();
        }
        final int totalNumHitsEstimate = fullTextSearchHits(term,
                                                            clusterSegments);
        return search(buildQuery(term,
                                 clusterSegments),
                      totalNumHitsEstimate,
                      filter,
                      clusterSegments);
    }

    @Override
    public int searchByAttrsHits(final Map<String, ?> attrs,
                                 final ClusterSegment... clusterSegments) {
        if (clusterSegments == null || clusterSegments.length == 0) {
            return 0;
        }
        if (attrs == null || attrs.size() == 0) {
            return 0;
        }
        return searchHits(buildQuery(attrs,
                                     clusterSegments),
                          clusterSegments);
    }

    @Override
    public int fullTextSearchHits(final String term,
                                  final ClusterSegment... clusterSegments) {
        if (clusterSegments == null || clusterSegments.length == 0) {
            return 0;
        }
        return searchHits(buildQuery(term,
                                     clusterSegments),
                          clusterSegments);
    }

    private int searchHits(final Query query,
                           final ClusterSegment... clusterSegments) {
        return Math.toIntExact(this.indexProvider.findHitsByQuery(getIndices(clusterSegments),
                                                                  query));
    }

    private List<String> getIndices(ClusterSegment[] clusterSegments) {
        return Arrays.asList(clusterSegments)
                .stream()
                .map(clusterSegment -> format(clusterSegment.getClusterId()))
                .collect(Collectors.toList());
    }

    private List<KObject> search(final Query query,
                                 final int totalNumHitsEstimate,
                                 final IOSearchService.Filter filter,
                                 final ClusterSegment... clusterSegments) {
        List<KObject> hits = this.indexProvider.findByQuery(this.getIndices(clusterSegments),
                                                            query,
                                                            totalNumHitsEstimate);
        return hits.stream().filter(kObject -> filter.accept(kObject)).collect(Collectors.toList());
    }

    private Query buildQuery(final Map<String, ?> attrs,
                             final ClusterSegment... clusterSegments) {
        final BooleanQuery.Builder query = new BooleanQuery.Builder();
        for (final Map.Entry<String, ?> entry : attrs.entrySet()) {
            if (entry.getValue() instanceof DateRange) {
                final Long from = ((DateRange) entry.getValue()).after().getTime();
                final Long to = ((DateRange) entry.getValue()).before().getTime();
                query.add(newLongRange(entry.getKey(),
                                       from,
                                       to,
                                       true,
                                       true),
                          MUST);
            } else if (entry.getValue() instanceof String) {
                query.add(new WildcardQuery(new Term(entry.getKey(),
                                                     entry.getValue().toString())),
                          MUST);
            } else if (entry.getValue() instanceof Boolean) {
                query.add(new TermQuery(new Term(entry.getKey(),
                                                 ((Boolean) entry.getValue()) ? "0" : "1")),
                          MUST);
            }
        }
        return composeQuery(query.build(),
                            clusterSegments);
    }

    private Query buildQuery(final String term,
                             final ClusterSegment... clusterSegments) {

        Query fullText;
        try {
            fullText = new QueryParser(FULL_TEXT_FIELD,
                                       this.analyzer).parse(term);
            if (fullText.toString().isEmpty()) {
                fullText = new WildcardQuery(new Term(FULL_TEXT_FIELD,
                                                      format(term) + "*"));
            }
        } catch (ParseException ex) {
            fullText = new WildcardQuery(new Term(FULL_TEXT_FIELD,
                                                  format(term) + "*"));
        }

        return composeQuery(fullText,
                            clusterSegments);
    }

    private Query composeQuery(final Query query,
                               final ClusterSegment... clusterSegments) {
        if (clusterSegments == null || clusterSegments.length == 0) {
            return query;
        }

        final BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(query,
                         MUST);

        final BooleanClause.Occur occur = (clusterSegments.length == 1 ? MUST : SHOULD);
        for (ClusterSegment clusterSegment : clusterSegments) {
            final BooleanQuery.Builder clusterSegmentQuery = new BooleanQuery.Builder();
            addClusterIdTerms(clusterSegmentQuery,
                              clusterSegment);
            addSegmentIdTerms(clusterSegmentQuery,
                              clusterSegment);
            booleanQuery.add(clusterSegmentQuery.build(),
                             occur);
        }

        return booleanQuery.build();
    }

    private void addClusterIdTerms(final BooleanQuery.Builder query,
                                   final ClusterSegment clusterSegment) {
        if (clusterSegment.getClusterId() != null) {
            final Query cluster = new TermQuery(new Term(MetaObject.META_OBJECT_CLUSTER_ID,
                                                         clusterSegment.getClusterId()));
            query.add(cluster,
                      MUST);
        }
    }

    private void addSegmentIdTerms(final BooleanQuery.Builder query,
                                   final ClusterSegment clusterSegment) {
        if (clusterSegment.segmentIds() == null || clusterSegment.segmentIds().length == 0) {
            return;
        }
        if (clusterSegment.segmentIds().length == 1) {
            final Query segment = new TermQuery(new Term(MetaObject.META_OBJECT_SEGMENT_ID,
                                                         clusterSegment.segmentIds()[0]));
            query.add(segment,
                      MUST);
        } else {
            final BooleanQuery.Builder segments = new BooleanQuery.Builder();
            for (final String segmentId : clusterSegment.segmentIds()) {
                final Query segment = new TermQuery(new Term(MetaObject.META_OBJECT_SEGMENT_ID,
                                                             segmentId));
                segments.add(segment,
                             SHOULD);
            }
            query.add(segments.build(),
                      MUST);
        }
    }

    private String format(final String term) {
        return toProtobufFormat(term.toLowerCase());
    }
}
