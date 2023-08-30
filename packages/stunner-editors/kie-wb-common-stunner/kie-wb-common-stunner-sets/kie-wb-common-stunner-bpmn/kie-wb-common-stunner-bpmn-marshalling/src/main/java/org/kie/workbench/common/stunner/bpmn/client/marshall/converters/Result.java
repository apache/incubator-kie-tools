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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;

public interface Result<T> {

    static <R> Result<R> of(R value, MarshallingMessage... messages) {
        return success(value, messages);
    }

    static <R> Result<R> success(R value, MarshallingMessage... messages) {
        return new Success<>(value, messages);
    }

    static <R> Result<R> failure(String reason, MarshallingMessage... messages) {
        return new Failure<>(reason, messages);
    }

    static <R> Result<R> failure(String reason, R defaultValue, MarshallingMessage... messages) {
        return new Failure<>(reason, defaultValue, messages);
    }

    static <U> Result<U> ignored(String reason, MarshallingMessage... messages) {
        return new Ignored<>(reason, messages);
    }

    static <U> Result<U> ignored(String reason, U defaultValue, MarshallingMessage... messages) {
        return new Ignored<>(reason, defaultValue, messages);
    }

    boolean isFailure();

    boolean isIgnored();

    boolean isSuccess();

    default boolean nonFailure() {
        return !isFailure();
    }

    default boolean notIgnored() {
        return !isIgnored();
    }

    T value();

    List<MarshallingMessage> messages();

    Result<T> setMessages(MarshallingMessage... messages);

    default void ifSuccess(Consumer<T> consumer) {
        Optional.ofNullable(value()).ifPresent(consumer::accept);
    }

    default void ifFailure(Consumer<String> consumer) {
        if (isFailure()) {
            consumer.accept(asFailure().reason());
        }
    }

    Success<T> asSuccess();

    Failure<T> asFailure();

    Ignored<T> asIgnored();

    abstract class AbstractResult<T> implements Result<T> {

        private List<MarshallingMessage> messages;

        AbstractResult(MarshallingMessage... messages) {
            this.messages = new ArrayList<>();
            this.messages.addAll(Arrays.asList(messages));
        }

        @Override
        public List<MarshallingMessage> messages() {
            return messages;
        }

        @Override
        public Result<T> setMessages(MarshallingMessage... messages) {
            this.messages = Stream.of(messages).collect(Collectors.toList());
            return this;
        }
    }

    class Success<T> extends AbstractResult<T> {

        private final T value;

        Success(T value, MarshallingMessage... messages) {
            super((messages));
            this.value = value;
        }

        public T value() {
            return value;
        }

        public Success<T> asSuccess() {
            return this;
        }

        public Ignored<T> asIgnored() {
            throw new ClassCastException("Could not convert Success to Ignored");
        }

        public Failure<T> asFailure() {
            throw new ClassCastException("Could not convert Success to Failure");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        public boolean isIgnored() {
            return false;
        }

        public boolean isFailure() {
            return false;
        }
    }

    class Ignored<T> extends AbstractResult<T> {

        private final String reason;
        private final T defaultValue;

        Ignored(String reason, MarshallingMessage... messages) {
            this(reason, null, messages);
        }

        Ignored(String reason, T defaultValue, MarshallingMessage... messages) {
            super(messages);
            this.reason = reason;
            this.defaultValue = defaultValue;
        }

        public String reason() {
            return reason;
        }

        public Success<T> asSuccess() {
            throw new NoSuchElementException(reason);
        }

        public Ignored<T> asIgnored() {
            return this;
        }

        public Failure<T> asFailure() {
            throw new ClassCastException("Could not convert Ignored to Success");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        public boolean isIgnored() {
            return true;
        }

        public boolean isFailure() {
            return false;
        }

        @Override
        public T value() {
            return defaultValue;
        }
    }

    class Failure<T> extends AbstractResult<T> {

        private final String reason;
        private final T defaultValue;

        Failure(String reason, MarshallingMessage... messages) {
            this(reason, null, messages);
        }

        Failure(String reason, T defaultValue, MarshallingMessage... messages) {
            super(messages);
            this.reason = reason;
            this.defaultValue = defaultValue;
        }

        public String reason() {
            return reason;
        }

        public Success<T> asSuccess() {
            throw new NoSuchElementException(reason);
        }

        public Ignored<T> asIgnored() {
            throw new ClassCastException("Could not convert Failure to Ignored");
        }

        public Failure<T> asFailure() {
            return this;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        public boolean isIgnored() {
            return false;
        }

        public boolean isFailure() {
            return true;
        }

        @Override
        public T value() {
            return defaultValue;
        }
    }
}


