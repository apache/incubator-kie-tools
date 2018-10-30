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

import org.apache.lucene.search.WildcardQuery;
import org.uberfire.ext.metadata.backend.infinispan.ickl.FieldConverter;

public class WildcardQueryConverter implements QueryConverter {

    private final WildcardQuery query;
    private final FieldConverter fieldConverter;

    public WildcardQueryConverter(WildcardQuery query,
                                  FieldConverter fieldConverter) {
        this.query = query;
        this.fieldConverter = fieldConverter;
    }

    @Override
    public String convert() {
        String key = this.fieldConverter.convertKey(this.query.getTerm().field());
        String value = this.fieldConverter.convertValue(this.query.getTerm().text());
        return key + ":" + "'" + value + "'";
    }
}
