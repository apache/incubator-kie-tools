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

package org.jboss.errai.databinding.client.api.handler.list;

import java.util.List;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@FunctionalInterface
public interface ItemAddedHandler<M> {

  /**
   * Called when a single item has been appended to the list.
   *
   * @param source
   *          a list representing the state before the item was appended (equal to the old value of the
   *          list). Never null.
   * @param item
   *          the added item. May be null.
   */
  public void onItemAdded(List<M> source, M item);

}
