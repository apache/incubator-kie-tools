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

package org.uberfire.ext.metadata.backend.infinispan.ickl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.infinispan.ickl.converters.BooleanQueryConverter;
import org.uberfire.ext.metadata.backend.infinispan.ickl.converters.RegexpQueryConverter;
import org.uberfire.ext.metadata.backend.infinispan.ickl.converters.TermQueryConverter;
import org.uberfire.ext.metadata.backend.infinispan.ickl.converters.WildcardQueryConverter;

public class IckleConverter {

    private static final String ORDER_BY = "order by ";
    private static final String WHERE = "where ";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private Logger logger = LoggerFactory.getLogger(IckleConverter.class);

    private final FieldConverterImpl converterImpl;

    public IckleConverter() {
        converterImpl = new FieldConverterImpl();
    }

    public String convert(Query query) {
        Class<? extends Query> queryClass = query.getClass();

        if (TermQuery.class.isAssignableFrom(queryClass)) {
            return new TermQueryConverter((TermQuery) query, converterImpl).convert();

        } else if (WildcardQuery.class.isAssignableFrom(queryClass)) {
            return new WildcardQueryConverter((WildcardQuery) query, converterImpl).convert();

        } else if (BooleanQuery.class.isAssignableFrom(queryClass)) {
            return new BooleanQueryConverter((BooleanQuery) query, this).convert();

        } else if (RegexpQuery.class.isAssignableFrom(queryClass)) {
            return new RegexpQueryConverter((RegexpQuery) query).convert();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Converter not found, MatchAllDocsQuery will be used");
        }

        return "";
    }

    public String convert(Sort sort) {

        if (sort == null || sort.getSort() == null) {
            return "";
        }

        List<SortField> fields = Arrays.asList(sort.getSort());

        String sortString = fields.stream()
                .filter(sortField -> sortField.getField() != null)
                .map(sortField -> {
                    String key = this.converterImpl.convertKey(sortField.getField());
                    String sortDirection = ASC;
                    if (sortField.getReverse()) {
                        sortDirection = DESC;
                    }
                    return key + " " + sortDirection;
                }).collect(Collectors.joining(","));

        return sortString;
    }

    public String where(Query query) {
        String queryString = this.convert(query);

        if (queryString.isEmpty()) {
            return "";
        } else {
            return WHERE + queryString;
        }
    }

    public String sort(Sort sort) {

        String sortString = this.convert(sort);

        if (sortString.isEmpty()) {
            return "";
        } else {
            return ORDER_BY + sortString;
        }
    }
}
