/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.databinding.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Wraps a List<M> to notify change handlers of all operations that mutate the underlying list.
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 *
 * @param <M>
 */
@SuppressWarnings("unchecked")
public class BindableListWrapper<M> implements List<M>, BindableProxy<List<M>> {

  private List<M> list;

  /*
   * Must be identity set so that ListWidget is not added as a handler twice when using declarative binding.
   */
  private final Collection<BindableListChangeHandler<M>> handlers = Collections.newSetFromMap(new IdentityHashMap<>());

  private final Map<BindableProxyAgent<?>, PropertyChangeHandler<?>> elementChangeHandlers =
          new HashMap<BindableProxyAgent<?>, PropertyChangeHandler<?>>();

  private final Map<PropertyChangeHandler<?>, PropertyChangeUnsubscribeHandle> unsubscribeHandlesByHandler =
          new HashMap<PropertyChangeHandler<?>, PropertyChangeUnsubscribeHandle>();

  private final BindableProxyAgent<List<M>> agent;

  public BindableListWrapper(List<M> list) {
    Assert.notNull(list);
    if (list instanceof BindableListWrapper) {
      throw new IllegalArgumentException("Wrap a BindableListWrapper in a BindableListWrapper.");
    }
    this.list = list;

    for (int i = 0; i < this.list.size(); i++) {
      this.list.set(i, (M) convertToProxy(this.list.get(i)));
    }

    agent = new BindableProxyAgent<List<M>>(this, list);
    agent.propertyTypes.put("this", new PropertyType(List.class, true, true));
  }

  @Override
  public boolean add(M element) {
    final List<M> oldValue = new ArrayList<M>(list);

    element = (M) convertToProxy(element);
    boolean b = list.add(element);
    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemAdded(oldValue, element);
    }
    return b;
  }

  @Override
  public void add(int index, M element) {
    final List<M> oldValue = new ArrayList<M>(list);

    element = (M) convertToProxy(element);
    list.add(index, element);
    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemAddedAt(oldValue, index, element);
    }
  }

  @Override
  public boolean addAll(Collection<? extends M> c) {
    final List<M> oldValue = new ArrayList<M>(list);

    List<M> addedModels = new ArrayList<M>();
    for (M model : c) {
      addedModels.add((M) convertToProxy(model));
    }

    boolean b = list.addAll(addedModels);

    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemsAdded(oldValue, addedModels);
    }
    return b;
  }

  @Override
  public boolean addAll(int index, Collection<? extends M> c) {
    final List<M> oldValue = new ArrayList<M>(list);

    int originalSize = list.size();
    boolean b = list.addAll(index, c);
    int numAdded = list.size() - originalSize;

    for (int i = index; i < index + numAdded; i++) {
      list.set(i, (M) convertToProxy(list.get(i)));
    }

    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemsAddedAt(oldValue, index, list.subList(index, index + c.size()));
    }
    return b;
  }

  @Override
  public void clear() {
    final List<M> oldValue = new ArrayList<M>(list);
    list.clear();
    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemsCleared(oldValue);
    }
    removeElementChangeHandlers();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(convertToProxy(o));
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    boolean b = true;

    for (Object item : c) {
      if (!contains(item)) {
        b = false;
        break;
      }
    }
    return b;
  }

  @Override
  public M get(int index) {
    return list.get(index);
  }

  @Override
  public int indexOf(Object o) {
    return list.indexOf(convertToProxy(o));
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public Iterator<M> iterator() {
    return new BindableListIterator();
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(convertToProxy(o));
  }

  @Override
  public ListIterator<M> listIterator() {
    return new BindableListIterator();
  }

  @Override
  public ListIterator<M> listIterator(int index) {
    return new BindableListIterator(index);
  }

  @Override
  public boolean remove(Object o) {
    final List<M> oldValue = new ArrayList<M>(list);
    o = convertToProxy(o);

    int index = list.indexOf(o);
    boolean b = list.remove(o);
    if (b) {
      for (BindableListChangeHandler<M> handler : handlers) {
        handler.onItemRemovedAt(oldValue, index);
      }
      removeElementChangeHandler(oldValue.get(index));
    }
    return b;
  }

  @Override
  public M remove(int index) {
    final List<M> oldValue = new ArrayList<M>(list);

    M m = list.remove(index);
    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemRemovedAt(oldValue, index);
    }
    removeElementChangeHandler(m);
    return m;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    final List<M> oldValue = new ArrayList<M>(list);

    final List<Integer> indexes = new ArrayList<Integer>();
    for (Object m : c) {
      m = convertToProxy(m);
      Integer index = list.indexOf(m);
      if (!indexes.contains(index)) {
        indexes.add(index);
      }
    }
    Collections.sort(indexes, Collections.reverseOrder());

    final boolean b = list.removeAll(c);
    if (b) {
      for (BindableListChangeHandler<M> handler : handlers) {
        handler.onItemsRemovedAt(oldValue, indexes);
      }
      for (final Object m : c) {
        removeElementChangeHandler(convertToProxy(m));
      }
    }
    return b;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    List<Object> proxies = new ArrayList<Object>();
    for (Object item : c) {
      proxies.add(convertToProxy(item));
    }
    return list.retainAll(c);
  }

  @Override
  public M set(int index, M element) {
    final List<M> oldValue = new ArrayList<M>(list);

    element = (M) convertToProxy(element);
    M m = list.set(index, element);
    for (BindableListChangeHandler<M> handler : handlers) {
      handler.onItemChanged(oldValue, index, element);
    }
    removeElementChangeHandler(m);
    return m;
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public List<M> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }

  /**
   * @param handler
   *          If this handler has already been added, it will not be added again.
   */
  public HandlerRegistration addChangeHandler(final BindableListChangeHandler<M> handler) {
    Assert.notNull(handler);
    handlers.add(handler);

    return () -> handlers.remove(handler);
  }

  private Object convertToProxy(Object element) {
    if (BindableProxyFactory.isBindableType(element)) {
      element = BindableProxyFactory.getBindableProxy(element);
      final BindableProxyAgent<?> agent = ((BindableProxy<?>) element).getBindableProxyAgent();

      if (!elementChangeHandlers.containsKey(agent)) {
        // Register a property change handler on the element to fire a change
        // event for the list when the element changes
        PropertyChangeHandler<Object> handler = new PropertyChangeHandler<Object>() {
          @Override
          public void onPropertyChange(PropertyChangeEvent<Object> event) {
            final int index = list.indexOf(event.getSource());
            final List<M> source = new ArrayList<M>(list);
            if (index == -1)  return;

            // yikes! we do this to alter the source list (otherwise the change event won't get fired).
            source.add(null);

            for (BindableListChangeHandler<M> handler : handlers) {
              handler.onItemChanged(source, index, (M) event.getSource());
            }
          }
        };
        unsubscribeHandlesByHandler.put(handler, agent.addPropertyChangeHandler(handler));
        elementChangeHandlers.put(agent, handler);
      }
    }
    return element;
  }

  private void removeElementChangeHandler(Object element) {
    if (!BindableProxyFactory.isBindableType(element)) {
      return;
    }

    final BindableProxyAgent<?> agent= ((BindableProxy<?>) element).getBindableProxyAgent();
    removeElementChangeHandler(agent);
  }

  private void removeElementChangeHandler(BindableProxyAgent<?> agent) {
    Assert.notNull(agent);

    PropertyChangeHandler<?> handler = elementChangeHandlers.remove(agent);
    if (handler != null) {
      PropertyChangeUnsubscribeHandle unsubHandle = unsubscribeHandlesByHandler.remove(handler);
      if (unsubHandle == null) {
        throw new RuntimeException("No " + PropertyChangeUnsubscribeHandle.class.getSimpleName() + " was found for the removed handler.");
      }

      unsubHandle.unsubscribe();
    }
  }

  private void removeElementChangeHandlers() {
    List<BindableProxyAgent<?>> agents = new ArrayList<BindableProxyAgent<?>>(elementChangeHandlers.keySet());
    for (BindableProxyAgent<?> agent : agents) {
      removeElementChangeHandler(agent);
    }
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
   return list.equals(obj);
  }

  @Override
  public String toString() {
    return list.toString();
  }

  /**
   * Wraps an Iterator or ListIterator to notify change handlers of all operations that mutate the
   * underlying list.
   */
  public class BindableListIterator implements ListIterator<M>, Iterator<M> {

    private ListIterator<M> iterator;

    public BindableListIterator() {
      iterator = list.listIterator();
    }

    public BindableListIterator(int index) {
      iterator = list.listIterator(index);
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public M next() {
      return iterator.next();
    }

    @Override
    public boolean hasPrevious() {
      return iterator.hasPrevious();
    }

    @Override
    public M previous() {
      return iterator.previous();
    }

    @Override
    public int nextIndex() {
      return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
      return iterator.previousIndex();
    }

    @Override
    public void remove() {
      List<M> oldValue = new ArrayList<M>(list);
      iterator.remove();
      int index = iterator.previousIndex() + 1;
      for (BindableListChangeHandler<M> handler : handlers) {
        handler.onItemRemovedAt(oldValue, index);
      }
      removeElementChangeHandler(oldValue.get(index));
    }

    @Override
    public void set(M e) {
      List<M> oldValue = new ArrayList<M>(list);
      e = (M) convertToProxy(e);
      iterator.set(e);
      int index = iterator.nextIndex() - 1;
      for (BindableListChangeHandler<M> handler : handlers) {
        handler.onItemChanged(oldValue, index, e);
      }
      removeElementChangeHandler(oldValue.get(index));
    }

    @Override
    public void add(M e) {
      List<M> oldValue = new ArrayList<M>(list);
      e = (M) convertToProxy(e);
      int index = iterator.nextIndex();
      iterator.add(e);
      for (BindableListChangeHandler<M> handler : handlers) {
        handler.onItemAddedAt(oldValue, index, e);
      }
    }
  }

  @Override
  public Object unwrap() {
    return list;
  }

  @Override
  public Object get(String propertyName) {
    if ("this".equals(propertyName)) {
      return list;
    }
    else {
      throw new NonExistingPropertyException("List", propertyName);
    }
  }

  @Override
  public void set(String propertyName, Object value) {
    if ("this".equals(propertyName)) {
      if (value instanceof BindableListWrapper) {
        throw new IllegalArgumentException("Cannot nest BindableListWrapper.");
      }
      list = (List<M>) value;
    }
    else {
      throw new NonExistingPropertyException("List", propertyName);
    }
  }

  @Override
  public Map<String, PropertyType> getBeanProperties() {
    return Collections.emptyMap();
  }

  @Override
  public BindableProxyAgent<List<M>> getBindableProxyAgent() {
    return agent;
  }

  @Override
  public void updateWidgets() {
    agent.updateWidgetsAndFireEvents();
  }

  @Override
  public List<M> deepUnwrap() {
    final List<M> unwrapped = new ArrayList<>(list.size());

    for (M m : list) {
      if (m instanceof BindableProxy) {
        m = ((BindableProxy<M>) m).deepUnwrap();
      }

      unwrapped.add(m);
    }

    return unwrapped;
  }
}
