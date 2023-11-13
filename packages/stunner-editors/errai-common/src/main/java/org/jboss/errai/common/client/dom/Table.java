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


package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.Element;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/HTMLTableElement">Web API</a>
 */
@JsType(isNative = true)
@Element("table")
@Deprecated
public interface Table extends HTMLElement {
  @JsProperty TableCaption getCaption();
  @JsProperty void setCaption(TableCaption caption);

  @JsProperty TableSection getTHead();
  @JsProperty void setTHead(TableSection tHead);

  @JsProperty TableSection getTFoot();
  @JsProperty void setTFoot(TableSection tFoot);

  @JsProperty HTMLCollection getRows();

  @JsProperty HTMLCollection getTBodies();

  @JsProperty String getAlign();
  @JsProperty void setAlign(String align);

  @JsProperty String getBgColor();
  @JsProperty void setBgColor(String bgColor);

  @JsProperty String getBorder();
  @JsProperty void setBorder(String border);

  @JsProperty String getCellPadding();
  @JsProperty void setCellPadding(String cellPadding);

  @JsProperty String getCellSpacing();
  @JsProperty void setCellSpacing(String cellSpacing);

  @JsProperty String getFrame();
  @JsProperty void setFrame(String frame);

  @JsProperty String getRules();
  @JsProperty void setRules(String rules);

  @JsProperty String getSummary();
  @JsProperty void setSummary(String summary);

  @JsProperty String getWidth();
  @JsProperty void setWidth(String width);

  HTMLElement createTHead();
  void deleteTHead();
  HTMLElement createTFoot();
  void deleteTFoot();
  HTMLElement createCaption();
  void deleteCaption();
  HTMLElement insertRow(int index);
  void deleteRow(int index);
}
