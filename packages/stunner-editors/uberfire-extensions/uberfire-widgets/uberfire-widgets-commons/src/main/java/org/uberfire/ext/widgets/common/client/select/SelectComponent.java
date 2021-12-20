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
 *
 */

package org.uberfire.ext.widgets.common.client.select;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElemental;

public class SelectComponent {

    private View view;
    private ManagedInstance<SelectOptionComponent> selectOptionWidget;
    private SelectOption selected;
    private Consumer<SelectOption> callback;
    private List<SelectOptionComponent> components;

    @Inject
    public SelectComponent(final View view,
                           final ManagedInstance<SelectOptionComponent> selectOptionComponent) {
        this.view = view;
        this.selectOptionWidget = selectOptionComponent;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    public void setup(List<SelectOption> options,
                      Consumer<SelectOption> selectCallback) {
        this.setSelected(getFirstSelectOption(options));
        this.setSelectOptions(options);
        this.callback = selectCallback;
    }

    private SelectOption getFirstSelectOption(List<SelectOption> options) {
        return !options.isEmpty() ? options.get(0) : new NullSelectOption();
    }

    protected void setSelectOptions(List<SelectOption> options) {
        this.view.clear();
        this.components = new ArrayList<>();
        components.addAll(options.stream().map(this::createSelectedOptionComponent).collect(Collectors.toList()));
        this.view.addOptions(components);
    }

    protected SelectOptionComponent createSelectedOptionComponent(SelectOption selectOption) {
        SelectOptionComponent component = this.selectOptionWidget.get();
        component.initialize(selectOption,
                             selected -> {
                                 this.deactivateAll();
                                 component.activate();
                                 this.setSelected(selected);
                                 this.callback.accept(selected);
                             });
        return component;
    }

    protected void deactivateAll() {
        this.components.forEach(SelectOptionComponent::deactivate);
    }

    public void setSelected(SelectOption selected) {
        this.selected = selected;
        this.update();
    }

    private void update() {
        this.view.setSelected(this.selected.getName());
    }

    public SelectOption getSelected() {
        return this.selected;
    }

    public View getView() {
        return this.view;
    }

    public interface View extends UberElemental<SelectComponent> {

        void addOption(SelectOptionComponent selectOptionComponent);

        void setSelected(String label);

        void addOptions(List<SelectOptionComponent> components);

        void clear();
    }
}
