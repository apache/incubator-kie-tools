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

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public interface Result<T> {

    static <R> Result<R> of(R value) {
        return new Success<>(value);
    }

    static <R> Result<R> success(R value) {
        return new Success<>(value);
    }

    static <R> Result<R> failure(String reason) {
        return new Failure<>(reason);
    }

    static <U> Result<U> ignored(String reason) {
        return new Ignored<>(reason);
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

    default T value() {
        return asSuccess().value();
    }

    default void ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(asSuccess().value());
        }
    }

    default void ifFailure(Consumer<String> consumer) {
        if (isFailure()) {
            consumer.accept(asFailure().reason());
        }
    }

    Success<T> asSuccess();

    Failure<T> asFailure();

    Ignored<T> asIgnored();

    class Success<T> implements Result<T> {

        private final T value;

        Success(T value) {
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

    class Ignored<T> implements Result<T> {

        private final String reason;

        Ignored(String reason) {
            this.reason = reason;
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
    }

    class Failure<T> implements Result<T> {

        private final String reason;

        Failure(String reason) {
            this.reason = reason;
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
    }
}


