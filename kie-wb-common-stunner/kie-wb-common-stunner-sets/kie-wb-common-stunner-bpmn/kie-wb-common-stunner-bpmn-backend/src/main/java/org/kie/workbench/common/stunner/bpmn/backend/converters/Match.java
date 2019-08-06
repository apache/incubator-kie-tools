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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessageDecorator;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest.Mode;
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

    private final Class<?> outputType;
    private final LinkedList<Case<?>> cases = new LinkedList<>();
    private final LinkedList<Case<?>> strictCases = new LinkedList<>();
    private Function<In, Out> orElse;
    private Out defaultValue;
    private Optional<MarshallingMessageDecorator<In>> inputDecorator = Optional.empty();
    private Optional<MarshallingMessageDecorator<Out>> outputDecorator = Optional.empty();
    private Mode mode = Mode.AUTO;

    public Match(Class<?> outputType) {
        this.outputType = outputType;
    }

    public static <In, Out> Match<In, Out> of(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<In, Node<? extends View<? extends Out>, ?>> ofNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<Node<? extends View<? extends In>, ?>, Out> fromNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<In, Edge<? extends View<? extends Out>, ?>> ofEdge(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    private static <T, U> Function<T, Result<U>> reportMissing(Class<?> expectedClass) {
        return t ->
                Result.failure(
                        "Not yet implemented: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    private <T> Function<T, Result<Out>> ignored(Class<?> expectedClass) {
        return t ->
                Result.ignored(
                        "Ignored: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()),
                        defaultValue, MarshallingMessage.builder().message("Ignored " + t).build());
    }

    public <Sub> Match<In, Out> when(Class<Sub> type, Function<Sub, Out> then) {
        Function<Sub, Result<Out>> thenWrapped = sub -> Result.of(then.apply(sub));
        return when_(type, thenWrapped);
    }

    private <Sub> Match<In, Out> when_(Class<Sub> type, Function<Sub, Result<Out>> then) {
        cases.add(new Case(type, then));
        return this;
    }

    public <Sub> Match<In, Out> whenExactly(Class<Sub> type, Function<Sub, Out> then) {
        Function<Sub, Result<Out>> thenWrapped = sub -> Result.of(then.apply(sub));
        return whenExactly_(type, thenWrapped);
    }

    private <Sub> Match<In, Out> whenExactly_(Class<Sub> type, Function<Sub, Result<Out>> then) {
        strictCases.add(new StrictCase<>(type, then));
        return this;
    }

    /**
     * handle a type by throwing an error.
     * Use when the implementation is still missing, but expected to exist
     */
    public <Sub> Match<In, Out> missing(Class<Sub> type) {
        return when_(type, reportMissing(type));
    }

    public <Sub> Match<In, Out> ignore(Class<Sub> type) {
        return when_(type, ignored(type));
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

    public <Sub> Match<In, Out> mode(Mode mode) {
        this.mode = mode;
        return this;
    }

    private Result<Out> apply(In value, List<Case<?>> cases, Supplier<Result<Out>> fallback) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::isSuccess)
                .findFirst()
                .orElseGet(fallback);
    }

    public Result<Out> apply(In value) {
        //First apply strict cases if matches, Second the generic cases, and as default the fallback
        return apply(value,
                     strictCases,
                     () -> apply(value,
                                 cases,
                                 () -> applyFallback(value)));
    }

    private Result<Out> applyFallback(In value) {
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
            final Out result = orElse.apply(value);
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

    private MarshallingMessage getIgnoreMessage(In value) {
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

    private <T> String getValueType(T value, Optional<MarshallingMessageDecorator<T>> decorator) {
        return decorator
                .map(d -> d.getType(value))
                .orElseGet(() -> Optional.ofNullable(value)
                        .map(Object::getClass)
                        .map(Class::getSimpleName)
                        .orElse(""));
    }

    private <T> String getValueName(T value, Optional<MarshallingMessageDecorator<T>> decorator) {
        return decorator.map(d -> d.getName(value)).orElseGet(() -> String.valueOf(value));
    }

    private class Case<T> {

        public final Class<T> when;
        public final Function<T, Result<Out>> then;

        private Case(Class<T> when, Function<T, Result<Out>> then) {
            this.when = when;
            this.then = then;
        }

        public Result<Out> match(Object value) {
            return when.isAssignableFrom(value.getClass()) ?
                    then.apply((T) value) : getFailure(value);
        }
    }

    private class StrictCase<T> extends Case<T> {

        public StrictCase(Class<T> when, Function<T, Result<Out>> then) {
            super(when, then);
        }

        @Override
        public Result<Out> match(Object value) {
            return Objects.equals(when, value.getClass()) ?
                    then.apply((T) value) : getFailure(value);
        }
    }
}
