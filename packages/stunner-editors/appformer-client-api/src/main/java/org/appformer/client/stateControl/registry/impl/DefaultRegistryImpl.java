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


package org.appformer.client.stateControl.registry.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;

/**
 * The default generic implementation for the {@link Registry} type.
 * It's implemented for achieving an in-memory and lightweight registry approach, don't do an overuse of it.
 * Note: The Stack class behavior when using the iterator is not the expected one, so used
 * ArrayDeque instead of an Stack to provide right iteration order.
 */
public class DefaultRegistryImpl<C> implements DefaultRegistry<C> {

    private final Deque<C> items = new ArrayDeque<>();
    private int maxStackSize = 200;
    private RegistryChangeListener registryChangeListener;

    @Override
    public void setMaxSize(final int size) {
        if (size < 0) {
            throw new IllegalArgumentException("The registry size should be a positive number");
        }
        this.maxStackSize = size;
    }

    @Override
    public void register(final C item) {
        addIntoStack(item);
        notifyRegistryChange();
    }

    @Override
    public void clear() {
        items.clear();
        notifyRegistryChange();
    }

    @Override
    public List<C> getHistory() {
        return new ArrayList<>(items);
    }

    @Override
    public void setRegistryChangeListener(final RegistryChangeListener registryChangeListener) {
        this.registryChangeListener = registryChangeListener;
    }

    @Override
    public C peek() {
        return items.peek();
    }

    @Override
    public C pop() {
        C item = items.pop();
        notifyRegistryChange();
        return item;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    private void notifyRegistryChange() {
        if (registryChangeListener != null) {
            registryChangeListener.notifyRegistryChange();
        }
    }

    private void addIntoStack(final C item) {
        if (null != item) {
            if ((items.size() + 1) > maxStackSize) {
                items.removeLast();
            }
            items.push(item);
        }
    }
}
