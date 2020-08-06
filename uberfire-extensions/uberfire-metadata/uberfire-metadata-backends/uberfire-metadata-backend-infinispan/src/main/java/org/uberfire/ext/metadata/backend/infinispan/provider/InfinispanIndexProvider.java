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

package org.uberfire.ext.metadata.backend.infinispan.provider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.QueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.infinispan.ickl.IckleConverter;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.provider.IndexProvider;

import static java.util.stream.Collectors.toList;
import static org.kie.soup.commons.validation.PortablePreconditions.checkCondition;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class InfinispanIndexProvider implements IndexProvider {

    private final InfinispanContext infinispanContext;
    private final InfinispanSchemaStore schemaStore;
    private final IckleConverter ickleConverter;

    private Logger logger = LoggerFactory.getLogger(InfinispanIndexProvider.class);

    public InfinispanIndexProvider(InfinispanContext infinispanContext,
                                   MappingProvider mappingProvider) {
        this.infinispanContext = infinispanContext;
        this.schemaStore = new InfinispanSchemaStore(this.infinispanContext,
                                                     mappingProvider);
        this.ickleConverter = new IckleConverter();
    }

    @Override
    public boolean isFreshIndex(KCluster cluster) {
        return this.getIndexSize(cluster.getClusterId()) == 0;
    }

    @Override
    public void index(KObject kObject) {

        this.schemaStore.updateSchema(kObject);

        this.infinispanContext.getCache(kObject.getClusterId()).put(kObject.getId(),
                                                                    kObject);
    }

    @Override
    public void index(List<KObject> elements) {
        elements.forEach(ko -> this.index(ko));
    }

    @Override
    public boolean exists(String index,
                          String id) {
        return Optional.ofNullable(this.infinispanContext.getCache(index))
                .map(i -> i.containsKey(id))
                .orElse(false);
    }

    @Override
    public void delete(String index) {
        this.infinispanContext.deleteCache(index);
    }

    @Override
    public void delete(String index,
                       String id) {
        if (this.exists(index,
                        id)) {
            this.infinispanContext.getCache(index).remove(id);
        }
    }

    @Override
    public List<KObject> findById(String index,
                                  String id) throws IOException {

        checkNotEmpty("index",
                      index);
        checkNotEmpty("id",
                      id);

        List<String> types = this.infinispanContext.getTypes(index);

        return types
                .stream()
                .map(type -> this.getQueryFactory(index)
                        .from(type)
                        .having(MetaObject.META_OBJECT_ID)
                        .eq(id)
                        .build()
                        .list())
                .flatMap(x -> x.stream())
                .map(x -> (KObject) x)
                .collect(toList());
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
        return this.infinispanContext.getCache(index).size();
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     int limit) {

        Stream<KObject> stream = this.findByQueryRaw(indices,
                                                     query,
                                                     null)
                .stream()
                .map(q -> this.checkQuery(() -> q.list()))
                .flatMap(x -> x.stream())
                .map(this::toKObject);
        if (limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.collect(toList());
    }

    @Override
    public List<KObject> findByQuery(List<String> indices,
                                     Query query,
                                     Sort sort,
                                     int limit) {

        Stream<KObject> stream = this.findByQueryRaw(indices,
                                                     query,
                                                     sort)
                .stream()
                .map(q -> this.checkQuery(() -> q.list()))
                .flatMap(x -> x.stream())
                .map(this::toKObject);
        if (limit > 0) {
            stream = stream.limit(limit);
        }
        return stream
                .collect(toList());
    }

    private List<KObject> checkQuery(Supplier<List<KObject>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("Error executing query",
                             e);
            }
            return Collections.emptyList();
        }
    }

    private Integer checkHitsQuery(Supplier<Integer> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.error("Error executing query",
                             e);
            }
            return 0;
        }
    }

    @Override
    public long findHitsByQuery(List<String> indices,
                                Query query) {

        return this.findByQueryRaw(indices,
                                   query,
                                   null)
                .stream()
                .mapToInt(q -> this.checkHitsQuery(() -> q.getResultSize()))
                .sum();
    }

    @Override
    public List<String> getIndices() {
        return this.infinispanContext.getIndices();
    }

    @Override
    public void observerInitialization(Runnable runnable) {
        this.infinispanContext.observeInitialization(runnable);
    }

    @Override
    public boolean isAlive() {
        return this.infinispanContext.isAlive();
    }

    protected QueryFactory getQueryFactory(String index) {
        return Search
                .getQueryFactory(this.infinispanContext.getCache(index.toLowerCase()));
    }

    @Override
    public void dispose() {
        this.infinispanContext.dispose();
    }

    private List<org.infinispan.query.dsl.Query> findByQueryRaw(List<String> indices,
                                                                Query query,
                                                                Sort sort) {

        this.infinispanContext.retrieveProbufSchemas();

        String whereClause = this.ickleConverter.where(query);
        String sortClause = this.ickleConverter.sort(sort);

        List<String> ind = indices;
        if (indices == null || indices.isEmpty()) {
            ind = this.getIndices();
        }

        return ind.stream().map(index -> {

            QueryFactory qf = this.getQueryFactory(index);

            return this.infinispanContext.getTypes(index)
                    .stream()
                    .map(type -> this.buildQuery(type,
                                                 whereClause,
                                                 sortClause))
                    .map(q -> {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Ickle Query: " + q);
                        }
                        return qf.create(q);
                    })
                    .collect(toList());
        })
                .flatMap(x -> x.stream())
                .collect(toList());
    }

    private KObject toKObject(Object o) {
        return (KObject) o;
    }

    private String buildQuery(String type,
                              String whereClause,
                              String sortClause) {

        StringBuilder sb = new StringBuilder();

        sb
                .append("from org.kie.")
                .append(type.trim())
                .append(" ")
                .append(whereClause.trim())
                .append(" ")
                .append(sortClause.trim());

        return sb.toString();
    }
}
