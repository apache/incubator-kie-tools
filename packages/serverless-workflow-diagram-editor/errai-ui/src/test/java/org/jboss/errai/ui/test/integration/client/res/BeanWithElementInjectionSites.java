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


package org.jboss.errai.ui.test.integration.client.res;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.TableCellElement;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Dependent
public class BeanWithElementInjectionSites {

  @Inject
  public AnchorElement anchor;

  @Inject
  public DivElement div;

  @Inject
  public ButtonElement button;

  @Inject
  @Named(TableCellElement.TAG_TD)
  public TableCellElement td;

  @Inject
  public TextInputElement textInput;

  @Inject
  public NumberInputElement numberInput;

  @Inject
  public InputElementWithClassAnnotations inputWithClassAnnotation;

  @Inject
  public InputElementWithClassAndPropertyAnnotations inputWithClassAnnotationAndProperty;

  @Inject
  public Elemental2InputWithClassAndPropertiesAnnotations elemental2InputWithClassAndPropertiesAnnotations;

}
