/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import elemental2.dom.Element;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;

import static java.util.stream.Collectors.toList;

public abstract class ListPresenter<T, P extends ListItemPresenter<T, ?, ?>> {

    private static final Elemental2DomUtil elemental2DomUtil = new Elemental2DomUtil();

    private final ManagedInstance<P> itemPresenters;

    private List<T> objects;
    private List<P> presenters;
    private Element listElement;
    private BiConsumer<T, P> itemPresenterConfigurator;

    public ListPresenter(final ManagedInstance<P> itemPresenters) {
        this.itemPresenters = itemPresenters;
    }

    public void setup(final Element listElement,
                      final List<T> objects,
                      final BiConsumer<T, P> itemPresenterConfigurator) {

        this.objects = objects;
        this.presenters = new ArrayList<>();
        this.listElement = listElement;
        this.itemPresenterConfigurator = itemPresenterConfigurator;

        elemental2DomUtil.removeAllElementChildren(this.listElement);
        this.objects.forEach(this::addToListElement);
    }

    public void setupWithPresenters(final Element listElement,
                                    final List<P> presenters,
                                    final BiConsumer<T, P> itemPresenterConfigurator) {

        this.objects = presenters.stream().map(p -> p.getObject()).collect(toList());
        this.presenters = presenters;
        this.listElement = listElement;
        this.itemPresenterConfigurator = itemPresenterConfigurator;

        elemental2DomUtil.removeAllElementChildren(this.listElement);
        presenters.forEach(this::addPresenter);
    }

    public void add(final T o) {
        addToListElement(o);
        objects.add(o);
    }

    protected void addToListElement(final T o) {
        addPresenter(newPresenterFor(o));
    }

    protected void addPresenter(final P presenter) {
        presenters.add(presenter);
        listElement.appendChild(presenter.getView().getElement());
    }

    protected P newPresenterFor(final T o) {
        final P listItemPresenter = this.itemPresenters.get();
        listItemPresenter.setListPresenter(this);
        itemPresenterConfigurator.accept(o, listItemPresenter);
        return listItemPresenter;
    }

    public void remove(final ListItemPresenter<T, ?, ?> listItemPresenter) {
        objects.remove(listItemPresenter.getObject());
        listElement.removeChild(listItemPresenter.getView().getElement());
    }

    public List<T> getObjectsList() {
        return objects;
    }

    public List<P> getPresenters() {
        return presenters;
    }

    @Override
    public int hashCode() {
        return objects.hashCode();
    }
}
