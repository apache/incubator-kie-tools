/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.widget;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class KieSelectElement implements KieSelectElementBase,
                                         IsElement {

    private final View view;
    private final KieSelectOptionsListPresenter optionsListPresenter;

    Consumer<String> onChange;

    @Inject
    public KieSelectElement(final View view,
                            final KieSelectOptionsListPresenter optionsListPresenter) {
        this.view = view;
        this.optionsListPresenter = optionsListPresenter;
        this.onChange = i -> {
        };
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final List<KieSelectOption> options,
                      final String initialValue,
                      final Consumer<String> onChange) {

        optionsListPresenter.setup(
                view.getSelect(),
                options,
                (item, presenter) -> presenter.setup(item, this));

        view.setValue(initialValue);
        view.initSelect();
        this.onChange = onChange;
    }

    public void clear() {
        view.clear();
    }

    public void onChange() {
        this.onChange.accept(getValue());
    }

    public void setValue(final String value) {
        view.setValue(value);
    }

    public String getValue() {
        return view.getValue();
    }

    public interface View extends UberElemental<KieSelectElement> {

        HTMLSelectElement getSelect();

        void initSelect();

        void setValue(final String value);

        String getValue();

        void clear();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
