/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;

public class ResultComposer {

    public static <T, R> Result composeResults(T value, Collection<Result<R>>... results) {
        List<MarshallingMessage> messages = Stream.of(results).flatMap(Collection::stream)
                .map(Result::messages)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return Result.success(value, messages.stream().toArray(MarshallingMessage[]::new));
    }

    @SuppressWarnings("unchecked")
    public static <T> Result compose(T value, Result... result) {
        List<Result<Object>> results = Arrays.asList(result);
        return composeResults(value, results);
    }
}
