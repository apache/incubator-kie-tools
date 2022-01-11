/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.test.quickhandler.client.res;

import org.jboss.errai.ui.test.common.client.dom.ButtonElement;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Button;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public interface QuickHandlerComponent extends HasHandlers {

  AnchorElement getC1();

  Button getC2();

  ButtonElement getC3();

  ButtonElement getC4();

  AnchorElement getC5();

  boolean isC0EventFired();

  boolean isC1EventFired();

  boolean isC1_dupEventFired();

  boolean isC2EventFired();

  boolean isC3EventFired();

  boolean isC4EventFired();

  boolean isC5EventFired();

  boolean isThisEventFired();

}
