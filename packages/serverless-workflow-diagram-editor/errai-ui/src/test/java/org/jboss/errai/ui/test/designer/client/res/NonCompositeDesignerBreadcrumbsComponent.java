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


package org.jboss.errai.ui.test.designer.client.res;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("DesignerTemplate.html#breadcrumbs")
public class NonCompositeDesignerBreadcrumbsComponent implements DesignerComponent {

  @DataField
  private Element breadcrumbs = DOM.createElement("h1");

  @Inject
  @DataField
  private BasicComponent1 c1;

  @Inject
  @DataField("subTemplate")
  private DesignerSubComponent something;

  @Inject
  @DataField
  private Button c2;

  @PostConstruct
  public void init() {
    c1.getElement().setAttribute("id", "basic");
    something.getElement().setAttribute("id", "somethingNew");
    c2.getElement().setAttribute("id", "btn");
  }

  public Element getRoot() {
    return breadcrumbs;
  }

  @Override
  public DesignerSubComponent getSubComponent() {
    return something;
  }

  @Override
  public Button getButton() {
    return c2;
  }
}
