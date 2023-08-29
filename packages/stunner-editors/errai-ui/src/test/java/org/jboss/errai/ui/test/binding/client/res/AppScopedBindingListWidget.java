/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.ui.test.binding.client.res;

import java.util.List;

import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.test.common.client.TestModel;

/**
 * {@link ListWidget} to test error handling for UI widgets with invalid scope.
 * 
 * @author Divya Dadlani <ddadlani@redhat.com>
 */
public class AppScopedBindingListWidget extends ListWidget<TestModel, AppScopedBindingItemWidget> {
  
  private List<TestModel> items;
  private int itemsRenderedCalled = 0;
  
  @Override
  protected Class<AppScopedBindingItemWidget> getItemComponentType() {
    return AppScopedBindingItemWidget.class;
  }

  @Override
  protected void onItemsRendered(List<TestModel> items) {
    itemsRenderedCalled++;
    this.items = items;
  }

  public int getItemsRenderedCalled() {
    return itemsRenderedCalled;
  }
  
  public List<TestModel> getItems() {
    return items;
  }
  
  public int getComponentCount() {
    return getPanel().getWidgetCount();
  }
  
}
