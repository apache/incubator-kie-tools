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


package org.jboss.errai.ui.test.extended.client.res;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Label;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("BaseComponent.html")
public class CompositeExtensionComponent extends CompositeBaseComponent implements Extension {

  @Inject
  @DataField("c3")
  private Label content3;

  @Inject
  @DataField
  private Label c2;

  @PostConstruct
  public final void init() {
    c2.getElement().setAttribute("id", "c2");
    getContent3().getElement().setAttribute("id", "c3");
  }

  @Override
  public Label getContent3() {
    return content3;
  }

  @Override
  public Label getC2() {
    return c2;
  }

}
