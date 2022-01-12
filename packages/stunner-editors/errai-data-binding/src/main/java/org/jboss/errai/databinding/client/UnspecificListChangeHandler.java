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

import java.util.Collection;
import java.util.List;

import org.jboss.errai.databinding.client.api.handler.list.BindableListChangeHandler;

/**
 * A {@link BindableListChangeHandler} that can be used in case details of the underlying list
 * mutation are irrelevant (when the sole information that the list has changed is sufficient).
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 *
 * @param <M>
 */
public abstract class UnspecificListChangeHandler<M> implements BindableListChangeHandler<M> {

  @Override
  public void onItemAdded(List<M> source, M item) {
    onListChanged(source);
  }

  @Override
  public void onItemAddedAt(List<M> source, int index, M item) {
    onListChanged(source);
  }

  @Override
  public void onItemsAdded(List<M> source, Collection<? extends M> items) {
    onListChanged(source);
  }

  @Override
  public void onItemsAddedAt(List<M> source, int index, Collection<? extends M> items) {
    onListChanged(source);
  }

  @Override
  public void onItemsCleared(List<M> source) {
    onListChanged(source);
  }

  @Override
  public void onItemRemovedAt(List<M> source, int index) {
    onListChanged(source);
  }

  @Override
  public void onItemsRemovedAt(List<M> source, List<Integer> indexes) {
    onListChanged(source);
  }

  @Override
  public void onItemChanged(List<M> source, int index, M item) {
    onListChanged(source);
  }

  /**
   * Called when the monitored list has been mutated.
   *
   * @param source
   *          a list representing the state before the change. Never null.
   */
  abstract void onListChanged(List<M> source);

}
