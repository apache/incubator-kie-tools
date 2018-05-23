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

import elemental2.dom.Element;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class KieSelectElement {

    private final View view;
    private final OptionsListPresenter optionsListPresenter;
    private final Elemental2DomUtil elemental2DomUtil;

    Consumer<String> onChange;

    @Inject
    public KieSelectElement(final View view,
                            final OptionsListPresenter optionsListPresenter,
                            final Elemental2DomUtil elemental2DomUtil) {
        this.view = view;
        this.optionsListPresenter = optionsListPresenter;
        this.elemental2DomUtil = elemental2DomUtil;
        this.onChange = i -> {
        };
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final Element element,
                      final List<Option> options,
                      final String initialValue,
                      final Consumer<String> onChange) {

        elemental2DomUtil.removeAllElementChildren(element);
        element.appendChild(view.getElement());

        optionsListPresenter.setup(
                view.getSelect(),
                options,
                (item, presenter) -> presenter.setup(item, this));

        view.setValue(initialValue);

        this.onChange = onChange;

        view.initSelect();
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
    }

    public static class Option {

        public final String value;
        public final String label;

        public Option(final String label, final String value) {
            this.label = label;
            this.value = value;
        }
    }

    @Dependent
    public static class OptionElement extends ListItemPresenter<Option, KieSelectElement, KieSelectElementView.Option> {

        private Option option;

        @Inject
        public OptionElement(final KieSelectElementView.Option view) {
            super(view);
        }

        @Override
        public OptionElement setup(final Option option,
                                   final KieSelectElement parentPresenter) {
            this.option = option;

            view.init(this);
            view.setLabel(option.label);
            view.setValue(option.value);

            return this;
        }

        @Override
        public Option getObject() {
            return option;
        }
    }

    @Dependent
    public static class OptionsListPresenter extends ListPresenter<Option, OptionElement> {

        @Inject
        public OptionsListPresenter(final ManagedInstance<OptionElement> itemPresenters) {
            super(itemPresenters);
        }
    }
}
