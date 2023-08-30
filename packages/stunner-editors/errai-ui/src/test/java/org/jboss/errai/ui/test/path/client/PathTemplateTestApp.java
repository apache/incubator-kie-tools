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


package org.jboss.errai.ui.test.path.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.test.path.client.res.PathAbsoluteComponent;
import org.jboss.errai.ui.test.path.client.res.PathRelativeComponent;
import org.jboss.errai.ui.test.path.client.res.PathRelativeParentComponent;

@EntryPoint
public class PathTemplateTestApp {

  @Inject
  private RootPanel root;

  @Inject
  private PathRelativeComponent relativeComponent;
  @Inject
  private PathRelativeParentComponent relativeParentComponent;
  @Inject
  private PathAbsoluteComponent absoluteComponent;

  @PostConstruct
  public void setup() {
    root.add(relativeComponent);
    root.add(absoluteComponent);
    System.out.println(root.getElement().getInnerHTML());
  }

  public PathRelativeComponent getRelativeComponent() {
    return relativeComponent;
  }

  public PathRelativeParentComponent getRelativeParentComponent() {
    return relativeParentComponent;
  }

  public PathAbsoluteComponent getAbsoluteComponent() {
    return absoluteComponent;
  }
}
