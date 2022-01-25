/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.client.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.ui.ValueChangeManager;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.databinding.client.BindableListWrapper;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncToAsyncBeanManagerAdapter;
import org.jboss.errai.ioc.client.container.async.AsyncBeanDef;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManager;
import org.jboss.errai.ui.client.local.spi.InvalidBeanScopeException;
import org.jboss.errai.ui.shared.TemplateWidget;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A type of widget that displays and manages a child component for each item in a
 * list of model objects. The widget instances are managed by Errai's IOC
 * container and are arranged in a {@link ComplexPanel}. By default, a
 * {@link FlowPanel} is used, but an alternative can be specified using
 * {@link #ListWidget(ComplexPanel)}.
 *
 * @param <M>
 *          the model type
 * @param <C>
 *          the item component type, needs to implement {@link HasModel} for
 *          associating the widget instance with the corresponding model
 *          instance. This component must be either a {@link Widget} or
 *          a {@link Templated} bean.
 * @deprecated Replaced by {@link ListComponent}.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Deprecated
public abstract class ListWidget<M, C extends HasModel<M>> extends Composite
  implements HasValue<List<M>>, BindableListChangeHandler<M> {

  private final ComplexPanel panel;
  private BindableListWrapper<M> items;
  private final List<BindableListChangeHandler<M>> handlers = new ArrayList<>();
  private final Collection<HandlerRegistration> registrations = new ArrayList<>();

  private final List<ComponentCreationalCallback> callbacks = new LinkedList<>();
  private int pendingCallbacks;

  private final ValueChangeManager<List<M>, ListWidget<M, C>> valueChangeManager = new ValueChangeManager<>(this);

  protected ListWidget() {
    this(new FlowPanel());
  }

  protected ListWidget(ComplexPanel panel) {
    this.panel = Assert.notNull(panel);
    handlers.add(this);
    initWidget(panel);
  }

  /**
   * Returns the class object for the item component type {@code C} to look up new
   * instances of the widget using the client-side bean manager.
   *
   * @return the item widget type.
   */
  protected abstract Class<C> getItemComponentType();

  /**
   * Called after all item components have been rendered. By default, this is a
   * NOOP, but subclasses can add behaviour if needed.
   * <p>
   * Using the standard synchronous bean manager this method is invoked before
   * {@link #setItems(List)} returns. However, when using the asynchronous bean
   * manager and declaring @LoadAsync on the item component, this method might be
   * called after {@link #setItems(List)} returns and after the corresponding
   * JavaScript code has been downloaded.
   *
   * @param items
   *          the rendered item list. Every change to this list will update the
   *          corresponding rendered item components.
   */
  protected void onItemsRendered(List<M> items) {
  }

  /**
   * Returns the panel that contains all item widgets.
   *
   * @return the item widget panel, never null.
   */
  protected ComplexPanel getPanel() {
    return panel;
  }

  /**
   * Sets the list of model objects. A component instance of type {@code C} will be added
   * to the panel for each object in the list. The list will be wrapped in an
   * {@link BindableListWrapper} to make direct changes to the list observable.
   * <p>
   * If the standard synchronous bean manager is used it is guaranteed that all
   * components have been added to the panel when this method returns. In case the
   * asynchronous bean manager is used this method might return before the
   * widgets have been added to the panel. See {@link #onItemsRendered(List)}.
   *
   * @param items
   *          The list of model objects. If null or empty all existing child
   *          components will be removed.
   */
  public void setItems(final List<M> items) {
    final boolean changed = this.items != items;

    if (items instanceof BindableListWrapper) {
      this.items = (BindableListWrapper<M>) items;
    }
    else {
      if (items != null) {
        this.items = new BindableListWrapper<M>(items);
      }
      else {
        this.items = new BindableListWrapper<M>(new ArrayList<M>());
      }
    }

    if (changed) {
      initializeHandlers();
      init();
    }
  }

  private void initializeHandlers() {
    for (final HandlerRegistration reg : registrations) {
      reg.removeHandler();
    }
    registrations.clear();
    for (final BindableListChangeHandler<M> handler : handlers) {
      registrations.add(this.items.addChangeHandler(handler));
    }
  }

  private void init() {
    // The AsyncBeanManager API works in both synchronous and asynchronous IOC mode
    AsyncBeanManager bm = IOC.getAsyncBeanManager();

    // In the case that this method is executed before the first call has
    // successfully processed all of its callbacks, we must cancel those
    // uncompleted callbacks in flight to prevent duplicate data in the
    // ListWidget.
    for (ComponentCreationalCallback callback : callbacks) {
      callback.discard();
    }
    callbacks.clear();
    pendingCallbacks = 0;

    // clean up the old widgets before we add new ones (this will eventually
    // become a feature of the framework: ERRAI-375)
    Iterator<Widget> it = panel.iterator();
    while (it.hasNext()) {
      bm.destroyBean(getComponentFromWidget(it.next()));
      it.remove();
    }

    if (items == null)
      return;

    pendingCallbacks = items.size();
    AsyncBeanDef<C> itemBeanDef = bm.lookupBean(getItemComponentType());

    if (!itemBeanDef.getScope().equals(Dependent.class))
      throw new InvalidBeanScopeException("ListWidget cannot contain ApplicationScoped widgets");

    for (final M item : items) {
      final ComponentCreationalCallback callback = new ComponentCreationalCallback(item);
      callbacks.add(callback);
      itemBeanDef.getInstance(callback);
    }
  }

  /**
   * Returns the component at the specified index.
   *
   * @param index
   *          the index to be retrieved
   *
   * @return the widget at the specified index
   *
   * @throws IndexOutOfBoundsException
   *           if the index is out of range
   */
  public C getComponent(int index) {
    final C component = getComponentFromWidget(panel.getWidget(index));
    return component;
  }

  /**
   * Returns the component currently displaying the provided model.
   *
   * @param model
   *          the model displayed by the widget
   *
   * @return the widget displaying the provided model instance, null if no
   *         widget was found for the model.
   */
  public C getComponent(M model) {
    int index = items.indexOf(model);
    return getComponent(index);
  }

  /**
   * Returns the number of components currently being displayed.
   *
   * @return the number of widgets.
   */
  public int getComponentCount() {
    return getPanel().getWidgetCount();
  }

  @SuppressWarnings("unchecked")
  private C getComponentFromWidget(final Widget widget) {
    if (widget instanceof TemplateWidget) {
      return (C) TemplateWidgetMapper.reverseGet((TemplateWidget) widget);
    } else {
      return (C) widget;
    }
  }

  public HandlerRegistration addBindableListChangeHandler(final BindableListChangeHandler<M> handler) {
    ensureItemsInitialized();
    handlers.add(handler);
    final HandlerRegistration wrapperHandlerRegistration = items.addChangeHandler(handler);

    return new HandlerRegistration() {
      @Override
      public void removeHandler() {
        ensureItemsInitialized();
        handlers.remove(handler);
        wrapperHandlerRegistration.removeHandler();
      }
    };
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<M>> handler) {
    return valueChangeManager.addValueChangeHandler(handler);
  }

  @Override
  public List<M> getValue() {
    ensureItemsInitialized();
    return items;
  }

  private void ensureItemsInitialized() {
    if (items == null) {
      items = new BindableListWrapper<M>(new ArrayList<M>());
      initializeHandlers();
    }
  }

  @Override
  public void setValue(List<M> value) {
    setValue(value, false);
  }

  @Override
  public void setValue(List<M> value, boolean fireEvents) {
    List<M> oldValue = getValue();
    setItems(value);
    if (fireEvents) {
      ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
    }
  }

  /**
   * A callback invoked by the {@link AsyncBeanManager} or
   * {@link SyncToAsyncBeanManagerAdapter} when the component instance was created.
   * It will associate the corresponding model instance with the component and add
   * the component to the panel.
   */
  private class ComponentCreationalCallback implements CreationalCallback<C> {
    private boolean discard;
    private final M item;

    private ComponentCreationalCallback(M item) {
      this.item = item;
    }

    @Override
    public void callback(C component) {
      if (!discard) {
        component.setModel(item);
        final IsWidget widget;
        widget = getWidgetForComponent(component);
        panel.add(widget);

        if (--pendingCallbacks == 0) {
          onItemsRendered(items);
        }
      }
    }

    public void discard() {
      this.discard = true;
    }
  }

  private IsWidget getWidgetForComponent(final C component) {
    final IsWidget widget;
    if (component instanceof IsWidget) {
      widget = (IsWidget) component;
    }
    else if (TemplateWidgetMapper.containsKey(component)) {
      widget = TemplateWidgetMapper.get(component);
    }
    else {
      throw new RuntimeException("Cannot display component of type " + getItemComponentType().getName()
              + ". Must be a Widget, a native element, or @Templated.");
    }

    return widget;
  }

  @Override
  public void onItemAdded(List<M> oldList, M item) {
    addWidget(item);
  }

  @Override
  public void onItemAddedAt(List<M> oldList, int index, M item) {
    if (panel instanceof InsertPanel) {
      addWidgetAt(index, items.get(index));
    }
    else {
      for (int i = index; i < items.size(); i++) {
        addAndReplaceWidget(index, i);
      }
    }
  }

  @Override
  public void onItemsAdded(List<M> oldList, Collection<? extends M> items) {
    for (M m : items) {
      addWidget(m);
    }
  }

  @Override
  public void onItemsAddedAt(List<M> oldList, int index, Collection<? extends M> item) {
    if (panel instanceof InsertPanel.ForIsWidget) {
      for (int i = index; i < index + item.size(); i++) {
        addWidgetAt(i, items.get(i));
      }
    }
    else {
      for (int i = index; i < items.size(); i++) {
        addAndReplaceWidget(index, i);
      }
    }
  }

  @Override
  public void onItemsCleared(List<M> oldList) {
    AsyncBeanManager bm = IOC.getAsyncBeanManager();
    Integer widgetCount = panel.getWidgetCount();

    Collection<Widget> widgets = new ArrayList<Widget>(widgetCount);
    for (int i = 0; i < widgetCount; i++) {
      widgets.add(panel.getWidget(i));
    }

    panel.clear();

    Iterator<Widget> itr = widgets.iterator();
    while (itr.hasNext()) {
      Widget w = itr.next();
      bm.destroyBean(getComponentFromWidget(w));
    }
  }

  @Override
  public void onItemRemovedAt(List<M> oldList, int index) {
    Widget widget = panel.getWidget(index);
    panel.remove(index);
    IOC.getAsyncBeanManager().destroyBean(getComponentFromWidget(widget));
  }

  @Override
  public void onItemsRemovedAt(List<M> oldList, List<Integer> indexes) {
    for (Integer index : indexes) {
      Widget widget = panel.getWidget(index);
      panel.remove(index);
      IOC.getAsyncBeanManager().destroyBean(getComponentFromWidget(widget));
    }
  }

  @Override
  public void onItemChanged(List<M> oldList, int index, M item) {
    if (oldList.get(index) == item)
      return;

    for (int i = index; i < items.size(); i++) {
      addAndReplaceWidget(index, i);
    }
  }

  private void addAndReplaceWidget(final int startIndex, final int index) {
    if (index < panel.getWidgetCount()) {
      panel.remove(startIndex);
    }
    addWidget(items.get(index));
  }

  private void addWidget(final M m) {
    AsyncBeanDef<C> itemBeanDef = IOC.getAsyncBeanManager().lookupBean(getItemComponentType());
    itemBeanDef.getInstance(new CreationalCallback<C>() {
      @Override
      public void callback(C component) {
        component.setModel(m);
        panel.add(getWidgetForComponent(component));
      }
    });
  }

  private void addWidgetAt(final int index, final M m) {
    if (!(panel instanceof InsertPanel.ForIsWidget)) {
      throw new RuntimeException("Method only supported for panels that implement: "
              + InsertPanel.ForIsWidget.class.getName());
    }

    AsyncBeanDef<C> itemBeanDef = IOC.getAsyncBeanManager().lookupBean(getItemComponentType());
    itemBeanDef.getInstance(new CreationalCallback<C>() {
      @Override
      public void callback(C component) {
        component.setModel(m);
        ((InsertPanel.ForIsWidget) panel).insert(getWidgetForComponent(component), index);
      }
    });
  }

}
