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


package org.jboss.errai.ui.test.binding.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.client.widget.UnOrderedList;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.test.binding.client.res.BindingItemWidget;
import org.jboss.errai.ui.test.binding.client.res.BindingTemplate;
import org.jboss.errai.ui.test.binding.client.res.CompositeBindingTemplate;
import org.jboss.errai.ui.test.binding.client.res.NonCompositeBindingItem;
import org.jboss.errai.ui.test.binding.client.res.NonCompositeBindingTemplate;
import org.jboss.errai.ui.test.common.client.TestModel;

@EntryPoint
public class BindingTemplateTestApp {

  @Inject
  private RootPanel root;

  @Inject
  private CompositeBindingTemplate compositeTemplate;

  @Inject
  private NonCompositeBindingTemplate nonCompositeTemplate;

  @Inject
  @UnOrderedList
  private ListWidget<TestModel, BindingItemWidget> ulListWidget;

  @Inject
  @Table
  private ListWidget<TestModel, BindingItemWidget> tableListWidget;

  @PostConstruct
  public void setup() {
    root.add(compositeTemplate);
    root.getElement().appendChild(TemplateUtil.asElement(nonCompositeTemplate.getRoot()));
  }

  public BindingTemplate<BindingItemWidget> getCompositeTemplate() {
    return compositeTemplate;
  }

  public BindingTemplate<NonCompositeBindingItem> getNonCompositeTemplate() {
    return nonCompositeTemplate;
  }

  public ListWidget<TestModel, BindingItemWidget> getUlListWidget() {
    return ulListWidget;
  }

  public ListWidget<TestModel, BindingItemWidget> getTableListWidget() {
    return tableListWidget;
  }
}
