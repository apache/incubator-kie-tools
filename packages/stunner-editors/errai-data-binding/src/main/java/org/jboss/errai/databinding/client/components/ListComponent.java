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
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A component that binds a list of {@link Bindable} models to a displayed list of UI components. Supports selection of
 * displayed components.
 *
 * Instances can be obtained via {@link #forIsElementComponent(Supplier, Consumer)} and
 * {@link #forIsWidgetComponent(Supplier, Consumer)}, or by injection with Errai IoC.
 *
 * When injecting a {@link ListComponent}, individual displayed components will be looked up via Errai IoC. This lookup
 * will use any qualifiers on the {@link ListComponent} injection site (with the exception of the {@link ListContainer}
 * qualifier explained below).
 *
 * The type of element used to contain list items can be specified with the {@link ListContainer} qualifier. The
 * {@link ListContainer#value()} will be the tag name of the element used.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface ListComponent<M, C extends TakesValue<M>> extends IsElement, TakesValue<List<M>>, BindableListChangeHandler<M> {

  /**
   * @param index
   *          A valid index for a model in the list of models.
   * @return The UI component displaying the model at the given index in the list of models. Never null.
   * @throws IndexOutOfBoundsException
   *           If the given index is invalid.
   */
  C getComponent(int index);

  /**
   * @param model
   *          Never null.
   * @return An optional containing a UI component associated with the given model, iff the given model is displayed by
   *         this {@link ListComponent}. Otherwise {@link Optional#empty()}.
   */
  default Optional<C> getComponent(final M model) {
    final int index = getValue().indexOf(model);

    if (index == -1) {
      return Optional.empty();
    }
    else {
      return Optional.ofNullable(getComponent(index));
    }
  }

  /**
   * @param handler
   *          A handler that is called for every new UI component created for this list.
   * @return A {@link HandlerRegistration} for removing the added handler.
   */
  HandlerRegistration addComponentCreationHandler(Consumer<C> handler);

  /**
   * @param handler
   *          A handler that is called for every UI component removed for this list.
   * @return A {@link HandlerRegistration} for removing the added handler.
   */
  HandlerRegistration addComponentDestructionHandler(Consumer<C> handler);

  /**
   * @param selector
   *          A {@link Consumer} called for every component that is selected via {@link #selectComponent(TakesValue)} or
   *          {@link #selectComponents(Collection)}.
   */
  void setSelector(Consumer<C> selector);

  /**
   * @param selector
   *          A {@link Consumer} called for every component that is unselected via
   *          {@link #deselectComponent(TakesValue)}, {@link #deselectComponents(Collection)}, or {@link #deselectAll()}
   *          .
   */
  void setDeselector(Consumer<C> deselector);

  /**
   * A convenience method for selecting components via their respective models. This is equivalent to calling
   * {@link #getComponent(Object)} for each model and then calling {@link #selectComponents(Collection)} on the
   * resulting collection.
   *
   * @param models
   *          A collection of models whose respective components will be selected.
   */
  default void selectModels(final Collection<M> models) {
    for (final M model: models) {
      getComponent(model).ifPresent(c -> selectComponent(c));
    }
  }

  /**
   * A convenience method for selecting a component via its respective model. This is equivalent to calling
   * {@code selectComponent(getComponent(model))}.
   *
   * @param model
   *          A model whose respective component is to be selected.
   */
  default void selectModel(final M model) {
    selectModels(Collections.singleton(model));
  }

  /**
   * @param components
   *          A collection of UI components in this list to be selected. After this call, every component in this list
   *          will be part of the collection returned by {@link #getSelectedComponents()}. If a selector has been set
   *          via {@link #setSelector(Consumer)} it will be called for each of the components.
   */
  void selectComponents(Collection<C> components);

  /**
   * @param component
   *          A UI components in this list to be selected. After this call the component will be part of the collection
   *          returned by {@link #getSelectedComponents()}. If a selector has been set via
   *          {@link #setSelector(Consumer)} it will be called for this component.
   */
  default void selectComponent(final C component) {
    selectComponents(Collections.singleton(component));
  }

  /**
   * @return A collection of the currently selected components. Components can be added to this collection via the
   *         {@code select*} methods and removed via the {@code deselect*} methods.
   */
  Collection<C> getSelectedComponents();

  /**
   * A convenience method for getting all of the models for the selected components.
   *
   * @return A collection of models where for each model, the component in this list bound to that model is part of the
   *         collection returned by {@link #getSelectedComponents()}.
   */
  default Collection<M> getSelectedModels() {
    final Collection<C> components = getSelectedComponents();
    final Collection<M> models = new ArrayList<>(components.size());

    for (final C comp : components) {
      models.add(comp.getValue());
    }

    return models;
  }

  /**
   * @param components
   *          A collection of UI components in this list to be unselected. After this call, every component in this list
   *          will not be part of the collection returned by {@link #getSelectedComponents()}. If a deselector has been
   *          set via {@link #setDeselector(Consumer)} it will be called for each of the components that was previously
   *          selected.
   */
  void deselectComponents(Collection<C> components);

  /**
   * @param component
   *          A UI component in this list to be unselected. After this call the component will not be part of the
   *          collection returned by {@link #getSelectedComponents()}. If a deselector has been set via
   *          {@link #setDeselector(Consumer)} it will be called for this component iff the component was previously
   *          selected.
   */
  default void deselectComponent(final C component) {
    deselectComponents(Collections.singleton(component));
  }

  /**
   * A convenience method for deselecting components by their respective models.
   *
   * @param models
   *          A collection of models. After this call, any component having one of these models will be deslected as if
   *          {@link #deselectComponent(TakesValue)} were called for it.
   */
  default void deselectModels(final Collection<M> models) {
    for (final M model : models) {
      getComponent(model).ifPresent(c -> deselectComponent(c));
    }
  }

  /**
   * A convenience method for deselecting a component by its respective model.
   *
   * @param model
   *          A model that, after this call, the component having this model will be deslected as if
   *          {@link #deselectComponent(TakesValue)} were called for it.
   */
  default void deselectModel(final M model) {
    deselectModels(Collections.singleton(model));
  }

  /**
   * A convenience method for deselcting all currently selected components. Equivalent to
   * {@code deselectComponents(getSelectedComponenets)}.
   */
  default void deselectAll() {
    deselectComponents(getSelectedComponents());
  }

  /**
   * @param supplier
   *          Supplies new UI components for displaying models added to the value of this {@link ListComponent}.
   * @param destroyer
   *          Performs any required clean-up for UI components after their respective model has been remove from the
   *          value of this {@link ListComponent}.
   * @return A {@link Builder} for a {@link ListComponent} displaying UI components which implement {@link IsElement}.
   */
  static <M, C extends TakesValue<M> & IsElement> Builder<M, C> forIsElementComponent(final Supplier<C> supplier,
          final Consumer<C> destroyer) {
    return new Builder<>(root -> new DefaultListComponent<>(root, supplier, destroyer, c -> c.getElement()));
  }

  /**
   * @param supplier
   *          Supplies new UI components for displaying models added to the value of this {@link ListComponent}.
   * @param destroyer
   *          Performs any required clean-up for UI components after their respective model has been remove from the
   *          value of this {@link ListComponent}.
   * @return A {@link Builder} for a {@link ListComponent} displaying UI components which implement {@link IsWidget}.
   */
  static <M, C extends TakesValue<M> & IsWidget> Builder<M, C> forIsWidgetComponent(final Supplier<C> supplier,
          final Consumer<C> destroyer) {
    return new Builder<>(root -> new DefaultListComponent<>(root, supplier, destroyer, c -> (HTMLElement) c.asWidget().getElement()));
  }

  /**
   * Allows for building {@link ListComponent ListComponents} with different kinds of container elements.
   */
  static class Builder<M, C extends TakesValue<M>> {
    private final Function<HTMLElement, ListComponent<M, C>> factory;

    private Builder(final Function<HTMLElement, ListComponent<M, C>> factory) {
      this.factory = factory;
    }

    /**
     * @param tagName
     *          The tag name of a DOM element.
     * @return A list component that displays UI components for individual models in an element with the given tag name.
     */
    public ListComponent<M, C> inElement(final String tagName) {
      return factory.apply((HTMLElement) Document.get().createElement(tagName));
    }

    /**
     * @return A list component that displays UI components for individual models in a {@code div} tag.
     */
    public ListComponent<M, C> inDiv() {
      return inElement(DivElement.TAG);
    }

    /**
     * @return A list component that displays UI components for individual models in a {@code tbody} tag.
     */
    public ListComponent<M, C> inTBody() {
      return inElement(TableSectionElement.TAG_TBODY);
    }
  }

}
