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


package org.jboss.errai.ui.test.basic.client.res;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jboss.errai.common.client.dom.Window.getDocument;
import static org.jboss.errai.ui.shared.api.annotations.DataField.ConflictStrategy.USE_BEAN;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated("Attributes.html")
public class BeanStrategy {

  public BeanStrategy() {
    div1 = (Div) getDocument().createElement("div");
    div2 = (Div) getDocument().createElement("div");

    div1.setTitle("bean");
    div2.setTitle("bean");

    div1.setLang("bean");
    div2.setLang("bean");

    div1.setClassName("bean");
    div2.setClassName("bean");

    div1.getStyle().setCssText("width: 100px; height: 100px;");
    div2.getStyle().setCssText("width: 100px; height: 100px");
  }

  @DataField(defaultStrategy = USE_BEAN)
  public Div div1;

  @DataField(defaultStrategy = USE_BEAN)
  public Div div2;

}
