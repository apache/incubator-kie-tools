/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.util;

import java.util.Objects;

public class Counter {

    private int counter;

    public Counter() {
        this(0);
    }

    public Counter(int value) {
        this.counter = value;
    }

    public int get() {
        return counter;
    }

    public int increment() {
        return ++counter;
    }

    public int decrement() {
        return --counter;
    }

    public boolean equalsToValue(int value) {
        return counter == value;
    }

    public String toString() {
        return String.valueOf(counter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Counter counter1 = (Counter) o;
        return counter == counter1.counter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(counter);
    }
}
