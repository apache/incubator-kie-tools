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

package org.uberfire.ext.metadata.backend.elastic.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzerWrapper;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaObject;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticMetaProperty;
import org.uberfire.ext.metadata.backend.elastic.metamodel.ElasticSearchMappingStore;
import org.uberfire.ext.metadata.backend.elastic.provider.ElasticSearchContext;
import org.uberfire.ext.metadata.backend.elastic.provider.MappingFieldFactory;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.provider.IndexProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ElasticSearchIndexProvider implements IndexProvider {

    public static final int ELASTICSEARCH_MAX_SIZE = 10000;
    public static final String ES_TEXT_TYPE = "text";
    public static final String ES_KEYWORD_TYPE = "keyword";
    private final ElasticSearchContext elasticSearchContext;
    private final MappingFieldFactory fieldFactory;
    private final ElasticSearchMappingStore elasticMetaModel;
    private final MetaModelStore metaModelStore;
    private final Analyzer analyzer;
    private Logger logger = LoggerFactory.getLogger(ElasticSearchIndexProvider.class);

    public ElasticSearchIndexProvider(MetaModelStore metaModelStore,
                                      ElasticSearchContext elasticSearchContext,
                                      Analyzer analyzer) {
        this.elasticMetaModel = new ElasticSearchMappingStore(this);
        this.metaModelStore = metaModelStore;
        this.elasticSearchContext = elasticSearchContext;
        this.analyzer = analyzer;
        this.fieldFactory = new MappingFieldFactory(metaModelStore);
    }

    public Client getClient() {
        return this.elasticSearchContext.getTransportClient();
    }

    @Override
    public boolean isFreshIndex(KCluster cluster) {
        return this.getIndexSize(cluster.getClusterId()) == 0;
    }

    @Override
    public void index(KObject object) {
        MetaObject metaObject = fieldFactory.build(object);
        elasticMetaModel.updateMetaModel(object,
                                         metaObject);
        this.deleteIfExists(object);
        this.createIndexRequest((ElasticMetaObject) metaObject)
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .execute().actionGet();
    }

    private void deleteIfExists(KObject object) {
        if (this.exists(object.getClusterId(),
                        object.getId())) {
            this.delete(object.getClusterId(),
                        object.getId());
        }
    }

    @Override
    public void index(List<KObject> elements) {

        elements.forEach(kObject -> this.deleteIfExists(kObject));

        BulkRequestBuilder bulk = this.getClient().prepareBulk();
        elements.forEach(elem -> {
            MetaObject metaObject = fieldFactory.build(elem);
            elasticMetaModel.updateMetaModel(elem,
                                             metaObject);
            bulk.add(this.createIndexRequest((ElasticMetaObject) metaObject));
        });
        bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .execute().actionGet();
    }

    public IndexRequestBuilder createIndexRequest(ElasticMetaObject object) {
        String clusterId = ((ElasticMetaProperty) object.getProperty(MetaObject.META_OBJECT_CLUSTER_ID).get()).getValue().toLowerCase();
        String type = ((ElasticMetaProperty) object.getProperty(MetaObject.META_OBJECT_TYPE).get()).getValue().toLowerCase();

        Map<String, Object> document = object.getProperties().stream()
                .map(metaProperty -> ((ElasticMetaProperty) metaProperty))
                .collect(Collectors.toMap(ElasticMetaProperty::getName,
                                          ElasticMetaProperty::getValue,
                                          (mp1, mp2) -> mp2));

        return this.getClient().prepareIndex(clusterId,
                                             type.toLowerCase()).setSource(document);
    }

    @Override
    public boolean exists(String index,
                          String id) {
        return this.findHitsByQuery(Collections.singletonList(index),
                                    new TermQuery(new Term(MetaObject.META_OBJECT_ID,
                                                           id))) > 0;
    }

    @Override
    public void delete(String index) {
        this.getClient().admin().indices().prepareDelete(index).get();
    }

    @Override
    public void delete(String index,
                       String id) {

        Optional<SearchResponse> found = findByQueryRaw(Collections.singletonList(index),
                                                        new TermQuery(new Term(MetaObject.META_OBJECT_ID,
                                                                               id)),
                                                        null,
                                                        1);

        if (found.isPresent()) {
            SearchResponse response = found.get();
            SearchHit[] hits = response.getHits().getHits();
            if (hits.length > 0) {

                this.getClient().prepareDelete(hits[0].getIndex(),
                                               hits[0].getType(),
                                               hits[0].getId())
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE).get();
            }
        }
    }

    @Override
    public List<KObject> findById(String index,
                                  String id) throws IOException {
        return this.findByQuery(Collections.singletonList(index),
                                new TermQuery(new Term(MetaObject.META_OBJECT_ID,
                                                       id)),
                                1);
    }

    @Override
    public void rename(String index,
                       String id,
                       KObject to) {

        checkNotEmpty("from",
                      index);
        checkNotEmpty("id",
                      id);
        checkNotNull("to",
                     to);
        checkNotEmpty("clusterId",
                      to.getClusterId());

        checkCondition("renames are allowed only from same cluster",
                       to.getClusterId().equals(index));

        if (this.exists(index,
                        id)) {
            this.delete(index,
                        id);
            this.index(to);
        }
    }

    @Override
    public long getIndexSize(String index) {
        return this.findHitsByQuery(Collections.singletonList(index),
                                    new MatchAllDocsQuery());
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     int limit) {
        return this.findByQuery(indices,
                                query,
                                null,
                                limit);
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     Sort sort,
                                     int limit) {
        Optional<SearchResponse> response = this.findByQueryRaw(indices,
                                                                query,
                                                                sort,
                                                                limit);

        return response.map(this::hitsToKObjects).orElse(Collections.emptyList());
    }

    private List<KObject> hitsToKObjects(SearchResponse response) {
        return Arrays.stream(response.getHits().getHits())
                .map(searchHit -> fieldFactory.fromDocument(searchHit.getSource()))
                .collect(Collectors.toList());
    }

    private Optional<SearchResponse> findByQueryRaw(List<String> indices,
                                                    Query query,
                                                    Sort sort,
                                                    int limit) {
        try {

            List<String> indexes = indices;
            if (indices.isEmpty()) {
                indexes = this.getIndices();
            }
            QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(escapeSpecialCharacters(query.toString()));
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(queryBuilder);
            if (sort != null) {
                Arrays.stream(sort.getSort()).forEach(sortField -> {
                    String field = sortField.getField();
                    searchSourceBuilder.sort(field);
                });
            }
            if (limit > 0 && limit <= ELASTICSEARCH_MAX_SIZE) {
                searchSourceBuilder.size(limit);
            } else {
                searchSourceBuilder.size(ELASTICSEARCH_MAX_SIZE);
            }
            return Optional.of(this.getClient()
                                       .prepareSearch(indicesToLowerCase(indexes).toArray(new String[indexes.size()]))
                                       .setSource(searchSourceBuilder).get());
        } catch (ElasticsearchException e) {
            logger.debug(MessageFormat.format("Can't perform search: {0}",
                                              e.getMessage()));
        }
        return Optional.empty();
    }

    protected String escapeSpecialCharacters(String queryString) {
        List<String> splittedTokens = Arrays.asList(queryString.split(" "));
        return splittedTokens.stream().map(query -> {
            if (query.chars().filter(ch -> ch == ':').count() >= 0) {
                int separationChar = query.indexOf(':') + 1;
                return query.substring(0,
                                       separationChar) + escape(query.substring(separationChar));
            } else {
                return query;
            }
        }).collect(Collectors.joining(" "));
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '!' || c == ':'
                    || c == '^' || c == '\"' || c == '/'
                    || c == '|' || c == '&') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private List<String> indicesToLowerCase(List<String> indices) {
        return indices.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    @Override
    public long findHitsByQuery(List<String> indices,
                                Query query) {
        Optional<SearchResponse> response = this.findByQueryRaw(indices,
                                                                query,
                                                                null,
                                                                0);

        return response.map(res -> res.getHits().getTotalHits()).orElse(0L);
    }

    @Override
    public List<String> getIndices() {
        String[] indices = this.getClient().admin().indices().prepareGetIndex().get().getIndices();
        return Arrays.asList(indices).stream().filter(index -> !index.startsWith(".")).collect(Collectors.toList());
    }

    public void putMapping(String index,
                           String type,
                           MetaObject metaObject) {
        checkNotEmpty("index",
                      index);
        checkNotEmpty("type",
                      type);
        checkNotNull("metaObject",
                     metaObject);
        try {
            this.getClient().admin().indices().prepareCreate(index.toLowerCase()).get();
        } catch (ResourceAlreadyExistsException ex) {
            logger.debug("Resource Already exists: " + ex.getMessage());
        }
        Map<String, Object> properties = this.createMappingMap(metaObject.getProperties());
        this.getClient().admin().indices()
                .preparePutMapping(index.toLowerCase())
                .setType(type.toLowerCase())
                .setSource(properties).get();
    }

    public Optional<MappingMetaData> getMapping(String index,
                                                String type) {
        checkNotEmpty("index",
                      index);
        checkNotEmpty("type",
                      type);
        try {
            GetMappingsResponse mappingResponse = this.getClient().admin().indices().prepareGetMappings(index.toLowerCase()).addTypes(type.toLowerCase()).get();
            return Optional.ofNullable(mappingResponse.getMappings().getOrDefault(index,
                                                                                  ImmutableOpenMap.of()).getOrDefault(type,
                                                                                                                      null));
        } catch (IndexNotFoundException ex) {
            if (logger.isDebugEnabled()) {
                logger.error(MessageFormat.format("Index not found trying to get mapping for {0}:{1}",
                                                  index,
                                                  type),
                             ex);
            }
            return Optional.empty();
        }
    }

    public void putMapping(String index,
                           String type,
                           List<MetaProperty> metaProperties) {
        checkNotEmpty("index",
                      index);
        checkNotEmpty("type",
                      type);
        Map<String, Object> properties = this.createMappingMap(metaProperties);
        this.getClient().admin().indices().preparePutMapping(index.toLowerCase()).setType(type.toLowerCase()).setSource(properties).get();
    }

    private Map<String, Object> createMappingMap(Collection<MetaProperty> metaProperties) {
        checkNotNull("metaProperties",
                     metaProperties);
        Map<String, Object> properties = new HashMap<>();
        metaProperties.forEach(metaProperty -> {
            ElasticMetaProperty elasticProperty = (ElasticMetaProperty) metaProperty;
            Map<String, String> configuration = new HashMap<>();
            configuration.put("type",
                              this.createElasticType(elasticProperty));
            this.createAnalyzerField(configuration,
                                     elasticProperty);
            properties.put(metaProperty.getName(),
                           configuration);
        });
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties",
                    properties);
        return mapping;
    }

    private void createAnalyzerField(Map<String, String> configuration,
                                     ElasticMetaProperty elasticProperty) {

        if (this.analyzer instanceof ElasticSearchAnalyzerWrapper) {
            Class<?> type = elasticProperty.getTypes().iterator().next();
            if (type == String.class && elasticProperty.isSearchable()) {
                ElasticSearchAnalyzerWrapper elasticSearchAnalyzerWrapper = (ElasticSearchAnalyzerWrapper) analyzer;
                configuration.put("analyzer",
                                  elasticSearchAnalyzerWrapper.getFieldAnalyzer(elasticProperty.getName()));
            }
        } else {
            throw new IllegalArgumentException("ElasticSearchAnalyzerWrapper is expected to be compatible with Elasticsearch");
        }
    }

    protected String createElasticType(MetaProperty metaProperty) {
        Class<?> type = metaProperty.getTypes().iterator().next();
        if (type == String.class && metaProperty.isSearchable()) {
            return ES_TEXT_TYPE;
        } else if (type == String.class && !metaProperty.isSearchable()) {
            return ES_KEYWORD_TYPE;
        } else {
            return type.getSimpleName().toLowerCase();
        }
    }

    @Override
    public void dispose() {
        this.metaModelStore.dispose();
    }
}
