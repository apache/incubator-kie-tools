/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.dom.HTMLElement;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;

/**
 * The default implementation of a {@link ListComponent}. Accepts as argument functions for creating and destorying UI
 * components, as well as accessing the DOM elements of a UI component, allowing it to be used with or independently of
 * the Errai IoC container.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class DefaultListComponent<M, C extends TakesValue<M>> implements ListComponent<M, C> {

  private final Collection<Consumer<C>> creationHandlers = new ArrayList<>();
  private final Collection<Consumer<C>> destructionHandlers = new ArrayList<>();

  private final HTMLElement root;
  private final Supplier<C> supplier;
  private final Consumer<C> destroyer;
  private final Function<C, HTMLElement> elementAccessor;
  private final List<C> components = new ArrayList<>();
  private List<M> value;
  private Consumer<C> selector = c -> {};
  private Consumer<C> deselector = c -> {};

  private final Set<C> selected = Collections.newSetFromMap(new IdentityHashMap<>());

  public DefaultListComponent(final HTMLElement root, final Supplier<C> supplier, final Consumer<C> destroyer, final Function<C, HTMLElement> elementAccessor) {
    this.root = root;
    this.supplier = supplier;
    this.destroyer = destroyer;
    this.elementAccessor = elementAccessor;
  }

  @Override
  public HandlerRegistration addComponentCreationHandler(final Consumer<C> handler) {
    creationHandlers.add(handler);

    return () -> creationHandlers.remove(handler);
  }

  @Override
  public HandlerRegistration addComponentDestructionHandler(final Consumer<C> handler) {
    destructionHandlers.add(handler);

    return () -> destructionHandlers.remove(handler);
  }

  @Override
  public HTMLElement getElement() {
    return root;
  }

  @Override
  public void setValue(final List<M> value) {
    final boolean changed = this.value != value;
    this.value = value;

    if (changed) {
      for (int i = components.size()-1; i > -1; i--) {
        removeComponent(i);
      }
      for (int i = 0; i < this.value.size(); i++) {
        addComponent(i, this.value.get(i));
      }
    }
  }

  @Override
  public List<M> getValue() {
    return value;
  }

  @Override
  public void onItemAdded(final List<M> source, final M item) {
    addComponent(components.size(), item);
  }

  @Override
  public void onItemAddedAt(final List<M> source, final int index, final M item) {
    addComponent(index, item);
  }

  @Override
  public void onItemsAdded(final List<M> source, final Collection<? extends M> items) {
    for (final M model : items) {
      addComponent(components.size(), model);
    }
  }

  @Override
  public void onItemsAddedAt(final List<M> source, final int index, final Collection<? extends M> items) {
    int i = index;
    for (final M model : items) {
      addComponent(i++, model);
    }
  }

  @Override
  public void onItemRemovedAt(final List<M> source, final int index) {
    removeComponent(index);
  }

  @Override
  public void onItemsRemovedAt(final List<M> source, final List<Integer> indexes) {
    Collections.sort(indexes, (n,m) -> m - n);
    for (final int index : indexes) {
      removeComponent(index);
    }
  }

  @Override
  public void onItemsCleared(final List<M> source) {
    for (int i = components.size()-1; i >= 0; i--) {
      removeComponent(i);
    }
  }

  @Override
  public void onItemChanged(final List<M> source, final int index, final M item) {
    components.get(index).setValue(item);
  }

  @Override
  public C getComponent(final int index) {
    return components.get(index);
  }

  private C createComponent(final M model) {
    final C component = supplier.get();
    component.setValue(model);

    return component;
  }

  private void removeComponent(final int index) {
    final C component = components.remove(index);
    for (final Consumer<C> handler : destructionHandlers) {
      handler.accept(component);
    }
    final HTMLElement element = elementAccessor.apply(component);
    element.getParentNode().removeChild(element);
    destroyer.accept(component);
  }

  private void addComponent(final int index, final M item) {
    final C component = createComponent(item);
    final HTMLElement element = Assert.notNull(elementAccessor.apply(component));
    if (index < components.size()) {
      root.insertBefore(element, Assert.notNull(elementAccessor.apply(components.get(index))));
    }
    else {
      root.appendChild(element);
    }
    components.add(index, component);
    for (final Consumer<C> handler : creationHandlers) {
      handler.accept(component);
    }
  }

  @Override
  public void setSelector(final Consumer<C> selector) {
    this.selector = Assert.notNull(selector);
  }

  @Override
  public void setDeselector(final Consumer<C> deselector) {
    this.deselector = Assert.notNull(deselector);
  }

  @Override
  public void selectComponents(final Collection<C> components) {
    for (final C comp : components) {
      selected.add(comp);
      selector.accept(comp);
    }
  }

  @Override
  public Collection<C> getSelectedComponents() {
    return Collections.unmodifiableCollection(selected);
  }

  @Override
  public void deselectComponents(final Collection<C> components) {
    for (final C comp : components) {
      if (selected.remove(comp)) {
        deselector.accept(comp);
      }
    }
  }

}
