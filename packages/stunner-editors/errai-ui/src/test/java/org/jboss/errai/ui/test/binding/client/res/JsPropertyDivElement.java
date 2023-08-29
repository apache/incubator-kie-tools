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

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.Element;
import org.jboss.errai.common.client.ui.HasValue;

/**
 * This native div element wrapper allows binding to the value property. This tests that the
 * presence of {@link HasValue} overrides type inference based on element type.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Element("div")
@JsType(isNative = true, name = "HTMLDivElement", namespace = JsPackage.GLOBAL)
public abstract class JsPropertyDivElement implements HasValue<String> {

  @Override @JsProperty
  public final native String getValue();

  @Override @JsProperty
  public final native void setValue(String value);

}
