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

package org.jboss.errai.databinding.client.api.handler.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.databinding.client.BindableListWrapper;

/**
 * A change handler for monitoring mutations of bindable lists (see {@link BindableListWrapper}).
 *
 * This interface contains default implementations for all methods except:
 * <ul>
 * <li>{@link #onItemsAddedAt(List, int, Collection)}
 * <li>{@link #onItemsRemovedAt(List, List)}
 * <li>{@link #onItemChanged(List, int, Object)}
 * </ul>
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface BindableListChangeHandler<M> extends ItemAddedHandler<M>,
                                                      ItemAddedAtHandler<M>,
                                                      ItemsAddedHandler<M>,
                                                      ItemsAddedAtHandler<M>,
                                                      ItemRemovedAtHandler<M>,
                                                      ItemsRemovedAtHandler<M>,
                                                      ItemsClearedHandler<M>,
                                                      ItemChangedHandler<M> {

  @Override
  default void onItemAdded(final List<M> source, final M item) {
    onItemAddedAt(source, source.size(), item);
  }

  @Override
  default void onItemsAdded(final List<M> source, final Collection<? extends M> items) {
    onItemsAddedAt(source, source.size(), items);
  }

  @Override
  default void onItemAddedAt(final List<M> source, final int index, final M item) {
    onItemsAddedAt(source, index, Collections.singleton(item));
  }

  @Override
  default void onItemsCleared(final List<M> source) {
    final List<Integer> indices = new ArrayList<>(source.size());
    for (int i = 0; i < source.size(); i++) {
      indices.add(i);
    }
    onItemsRemovedAt(source, indices);
  }

  @Override
  default void onItemRemovedAt(final List<M> source, final int index) {
    onItemsRemovedAt(source, Collections.singletonList(index));
  }
}
