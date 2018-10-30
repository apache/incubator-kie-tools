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

package org.uberfire.ext.metadata.backend.infinispan.ickl.converters;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.TermQuery;
import org.uberfire.ext.metadata.backend.infinispan.ickl.FieldConverter;
import org.uberfire.ext.metadata.model.schema.MetaObject;

public class TermQueryConverter implements QueryConverter {

    private final TermQuery query;
    private final FieldConverter fieldConverter;
    private final List<String> basicAttrs = Arrays.asList(MetaObject.META_OBJECT_ID,
                                                          MetaObject.META_OBJECT_TYPE,
                                                          MetaObject.META_OBJECT_CLUSTER_ID,
                                                          MetaObject.META_OBJECT_SEGMENT_ID,
                                                          MetaObject.META_OBJECT_KEY);

    public TermQueryConverter(TermQuery query,
                              FieldConverter fieldConverter) {
        this.query = query;
        this.fieldConverter = fieldConverter;
    }

    @Override
    public String convert() {
        String key = this.fieldConverter.convertKey(this.query.getTerm().field());
        String value = this.fieldConverter.convertValue(this.query.getTerm().text());
        return key + this.getOperator(this.query.getTerm().field()) + this.convertValue(value);
    }

    private String getOperator(String key) {
        return this.basicAttrs.contains(key) ? "=" : ":";
    }

    private String convertValue(String value) {
        if (NumberUtils.isNumber(value) || BooleanUtils.toBooleanObject(value) != null) {
            return value;
        } else {
            return "'" + value + "'";
        }
    }
}
