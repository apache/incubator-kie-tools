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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageDecorator;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.Violation;

/**
 * Creates a pattern matching function.
 * <p>
 * Example usage:
 *
 * <pre>
 *     // let be T1 a class, and let be T1a, T1b subclasses of T1
 *     // then, let be T2 a class, and let be T2a, T2b subclasses of T2
 *     // such that T1a corresponds to T2a, T1b corresponds to T2b:
 *     Match<T1, T2> m =
 *         Match.of(T1.class, T2.class)
 *           .when(T1a.class, t1aInstance -> ... create an equivalent t2a instance)
 *           .when(T1b.class, t1bInstance -> ... create an equivalent t2b instance)
 *     T1 myT1 = ...;
 *
 *     Result<T2> result = myT1.apply(myT1);
 *     // unwrap the result on success, raise an exception otherwise
 *     T2 t2 = result.value();
 * </pre>
 * @param <In> the input type of the match
 * @param <Out> the type of the result of the match
 */
public class Match<In, Out> {

    private final LinkedList<Case> cases = new LinkedList<>();
    private final LinkedList<Case> strictCases = new LinkedList<>();
    private Function<In, Out> orElse;
    private Out defaultValue;
    private Optional<MarshallingMessageDecorator> inputDecorator = Optional.empty();
    private Optional<MarshallingMessageDecorator> outputDecorator = Optional.empty();
    private Mode mode = Mode.AUTO;

    public Match() {
    }

    public static <In, Out> Match<In, Out> of() {
        return new Match<>();
    }

    public static <In, Out> Match<In, Node<? extends View<? extends Out>, ?>> ofNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>();
    }

    public static <In, Out> Match<Node<? extends View<?>, ?>, Out> fromNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>();
    }

    public static <In, Out> Match<In, Edge<? extends View<? extends Out>, ?>> ofEdge(Class<In> inputType, Class<Out> outputType) {
        return new Match<>();
    }

    private static <T, U> Function<T, Result<U>> reportMissing(Class<?> expectedClass) {
        return t ->
                Result.failure(
                        "Not yet implemented: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    private <T extends In> Function<T, Result<Out>> ignored(Class<?> expectedClass) {
        return t ->
                Result.ignored(
                        "Ignored: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()),
                        defaultValue, MarshallingMessage.builder().message("Ignored " + t).build());
    }

    public <T extends In> Match<In, Out> when(Function<?, Boolean> type, Function<T, Out> then) {
        Function<T, Result<Out>> thenWrapped = sub -> Result.of(then.apply(sub));
        return when_(type, thenWrapped);
    }

    private <T extends In> Match<In, Out> when_(Function<?, Boolean> type, Function<T, Result<Out>> then) {
        cases.add(new Case(type, then));
        return this;
    }

    /**
     * handle a type by throwing an error.
     * Use when the implementation is still missing, but expected to exist
     */
    public <T extends In> Match<In, Out> missing(Function<In, Boolean> type, Class<T> clazz) {
        return when_(type, reportMissing(clazz));
    }

    public <T extends In> Match<In, Out> ignore(Function<T, Boolean> type, Class<T> clazz) {
        return when_(type, ignored(clazz));
    }

    public Match<In, Out> orElse(Function<In, Out> then) {
        this.orElse = then;
        return this;
    }

    public Match<In, Out> inputDecorator(MarshallingMessageDecorator<In> decorator) {
        this.inputDecorator = Optional.ofNullable(decorator);
        return this;
    }

    public Match<In, Out> outputDecorator(MarshallingMessageDecorator<Out> decorator) {
        this.outputDecorator = Optional.ofNullable(decorator);
        return this;
    }

    public Match<In, Out> defaultValue(Out value) {
        this.defaultValue = value;
        return this;
    }

    public Match<In, Out> mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    private <T extends In> Result<Out> apply(T value, List<Case> cases, Supplier<Result<Out>> fallback) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::isSuccess)
                .findFirst()
                .orElseGet(fallback);
    }

    public <T extends In>Result<Out> apply(T value) {
        //First apply strict cases if matches, Second the generic cases, and as default the fallback
        return apply(value,
                     strictCases,
                     () -> apply(value,
                                 cases,
                                 () -> applyFallback(value)));
    }

    private <T extends In> Result<Out> applyFallback(T value) {
        if (Mode.ERROR.equals(mode)) {
            //throw an Exception in case the mode is set to ERROR, avoid to apply the fallback
            //throw new IllegalStateException("Element has no match on marshalling: " + value);
            return getFailure(value);
        }

        if (orElse == null || Mode.IGNORE.equals(mode)) {
            return Stream.concat(cases.stream(), strictCases.stream())
                    .map(c -> c.match(value))
                    .filter(Result::isIgnored)
                    .map(r -> {
                        //handling value as Result setting the specific ignore message
                        if (r.value() instanceof Result) {
                            ((Result) r.value()).setMessages(getIgnoreMessage(value));
                        }
                        return r;
                    })
                    .findFirst()
                    .orElseGet(() -> Result.failure(value == null ? "Null value" : value.getClass().getName(),
                                                    defaultValue,
                                                    getIgnoreMessage(value)));
        } else {
            //fallback is applied only in case of AUTO mode.
            final Out result = orElse.apply((In)value);
            return Result.of(result,
                             MarshallingMessage.builder()
                                     .message("Converted element: " + value + "to: " + result)
                                     .messageKey(MarshallingMessageKeys.convertedElement)
                                     .messageArguments(getValueName(value, inputDecorator),
                                                       getValueType(value, inputDecorator),
                                                       getValueType(result, outputDecorator))
                                     .type(Violation.Type.WARNING)
                                     .build());
        }
    }

    private <T extends In> MarshallingMessage getIgnoreMessage(T value) {
        return MarshallingMessage.builder()
                .message("Ignored element " + value)
                .messageKey(MarshallingMessageKeys.ignoredElement)
                .messageArguments(getValueName(value, inputDecorator),
                                  getValueType(value, inputDecorator))
                .type(Violation.Type.WARNING)
                .build();
    }

    private Result<Out> getFailure(Object value) {
        return Result.failure(value.getClass().getName(), defaultValue,
                              MarshallingMessage.builder()
                                      .type(Violation.Type.ERROR)
                                      .message("Failure there is no match for element " + value)
                                      .messageKey(MarshallingMessageKeys.elementFailure)
                                      .messageArguments(String.valueOf(value))
                                      .build());
    }

    private <T> String getValueType(T value, Optional<MarshallingMessageDecorator> decorator) {
        return decorator
                .map(d -> d.getType(value))
                .orElseGet(() -> Optional.ofNullable(value)
                        .map(Object::getClass)
                        .map(Class::getSimpleName)
                        .orElse(""));
    }

    private <T extends In> String getValueName(T value, Optional<MarshallingMessageDecorator> decorator) {
        return decorator.map(d -> d.getName(value)).orElseGet(() -> String.valueOf(value));
    }

    private class Case<In> {

        public final Function<In, Boolean> when;
        public final Function<In, Result<Out>> then;

        private Case(Function<In, Boolean> when, Function<In, Result<Out>> then) {
            this.when = when;
            this.then = then;
        }

        public Result<Out> match(In value) {
            return when.apply(value) ?
                    then.apply(value) : getFailure(value);
        }
    }
}
