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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util;

import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ConverterUtils {

    @SuppressWarnings("unchecked")
    public static <T> Node<View<T>, Edge> cast(Node<?, ?> node) {
        return (Node<View<T>, Edge>) node;
    }

    public static boolean nonEmpty(final String s) {
        return !isEmpty(s);
    }

    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }

    public static Result<PropertyWriter> resultIgnored(Object o) {
        return Result.ignored("PropertyWriter ignored for [" + o.getClass().getName() + "].");
    }

    public static Result<PropertyWriter> resultNotSupported(Object o) {
        return Result.failure("No PropertyWriter found for [" + o.getClass().getName() + "].");
    }

    public static PropertyWriter notSupported(Object o) {
        throw new UnsupportedOperationException("No PropertyWriter found for [" + o.getClass().getName() + "].");
    }

    public static <U> Result<U> ignore(String name, Object o) {
        return Result.ignored(name + " [" + o.getClass().getName() + "] not supported.");
    }

    public <T> Function<T, Result> ignored(Class<?> expectedClass, final Object defaultValue) {
        return t ->
                Result.ignored(
                        "Ignored: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()),
                        defaultValue,
                        MarshallingMessage.builder().message("Ignored " + t).build());
    }
}
