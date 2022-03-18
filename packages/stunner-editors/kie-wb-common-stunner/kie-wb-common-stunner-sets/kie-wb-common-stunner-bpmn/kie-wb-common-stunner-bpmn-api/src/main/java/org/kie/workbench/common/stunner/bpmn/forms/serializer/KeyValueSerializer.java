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
 */

package org.kie.workbench.common.stunner.bpmn.forms.serializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class KeyValueSerializer {

    protected static final String DELIMITER_ROWS = ",";
    protected static final String DELIMITER_KEY_VALUE = ":";

    private String delimiterRow;
    private String delimiterKeyValue;

    @Inject
    public KeyValueSerializer() {
        this.delimiterRow = DELIMITER_ROWS;
        this.delimiterKeyValue = DELIMITER_KEY_VALUE;
    }

    protected KeyValueSerializer(String delimiterRow, String delimiterKeyValue) {
        this.delimiterRow = delimiterRow;
        this.delimiterKeyValue = delimiterKeyValue;
    }

    public <T> List<T> deserialize(String value, BiFunction<String, String, T> builder) {
        return Optional.ofNullable(value)
                .filter(s -> !s.isEmpty())
                .map(s -> Stream.of(s.split(delimiterRow))
                        .map(entry -> entry.split(delimiterKeyValue))
                        .map(entry -> builder.apply(entry[0], entry[1]))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public <T> String serialize(Optional<List<T>> values, Function<T, Object> getKey, Function<T, Object> getValue) {
        return values.orElse(Collections.emptyList())
                .stream()
                .map(row -> getKey.apply(row) + delimiterKeyValue + getValue.apply(row))
                .collect(Collectors.joining(delimiterRow));
    }
}