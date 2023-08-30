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


package org.kie.workbench.common.stunner.client.widgets.views;

import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.mvp.Command;

@Dependent
public class SelectorImpl<T> implements Selector<T> {

    private final SelectorView view;
    private Function<T, String> valueProvider;
    private Function<T, String> textProvider;
    private Function<String, T> itemProvider;
    private Command valueChangedCommand;

    @Inject
    public SelectorImpl(final SelectorView view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        this.valueProvider = Object::toString;
        this.textProvider = Object::toString;
        this.valueChangedCommand = () -> {
        };
    }

    public SelectorImpl<T> setTextProvider(final Function<T, String> textProvider) {
        this.textProvider = textProvider;
        return this;
    }

    public SelectorImpl<T> setValueProvider(final Function<T, String> valueProvider) {
        this.valueProvider = valueProvider;
        return this;
    }

    public SelectorImpl<T> setItemProvider(final Function<String, T> itemProvider) {
        this.itemProvider = itemProvider;
        return this;
    }

    public SelectorImpl<T> setValueChangedCommand(final Command valueChangedCommand) {
        this.valueChangedCommand = valueChangedCommand;
        return this;
    }

    @Override
    public Selector<T> addItem(final T item) {
        view.add(textProvider.apply(item),
                 valueProvider.apply(item));
        return this;
    }

    @Override
    public Selector<T> setSelectedItem(final T item) {
        view.setValue(valueProvider.apply(item));
        return this;
    }

    @Override
    public T getSelectedItem() {
        return itemProvider.apply(view.getValue());
    }

    @Override
    public Selector<T> clear() {
        view.clear();
        return this;
    }

    @Override
    public void onValueChanged() {
        valueChangedCommand.execute();
    }

    @PreDestroy
    public void destroy() {
        view.clear();
        valueProvider = null;
        textProvider = null;
        itemProvider = null;
        valueChangedCommand = null;
    }

    @Override
    public SelectorView getView() {
        return view;
    }
}
