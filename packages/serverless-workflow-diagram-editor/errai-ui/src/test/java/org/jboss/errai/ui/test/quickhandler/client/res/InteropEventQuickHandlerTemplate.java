/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.ButtonElement;
import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.common.client.logging.util.StringFormat;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Templated
public class InteropEventQuickHandlerTemplate {

  public static class ObservedEvent {
    public final String dataField;
    public final String eventType;

    public ObservedEvent(final String dataField, final String eventType) {
      this.dataField = dataField;
      this.eventType = eventType;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof ObservedEvent) {
        final ObservedEvent other = (ObservedEvent) obj;
        return Objects.equals(dataField, other.dataField) && Objects.equals(eventType, other.eventType);
      }
      else {
        return false;
      }
    }

    @Override
    public String toString() {
      return StringFormat.format("[data-field=%s, eventType=%s]", dataField, eventType);
    }
  }

  @Inject
  @DataField
  public HTMLDivElement root;

  @Inject
  @DataField
  public HTMLAnchorElement anchor;

  @Inject
  @DataField
  public HTMLButtonElement button;

  @Inject
  @DataField
  public HTMLButtonElement privateHandler;

  @Inject
  @DataField
  public HTMLInputElement input;

  @Inject
  @DataField
  public com.google.gwt.user.client.ui.Button buttonWidget;

  @Inject
  @DataField
  public ButtonElement buttonGwtElement;

  public List<ObservedEvent> observed = new ArrayList<>();

  @PostConstruct
  public void init() {
    input.type = "text";
  }

  @EventHandler("anchor")
  public void onAnchorSingleOrDoubleClicked(final @ForEvent({"click", "dblclick"}) MouseEvent evt) {
    observed.add(new ObservedEvent("anchor", evt.type));
  }

  @EventHandler("button")
  public void onButtonSingle(final @ForEvent("click") MouseEvent evt) {
    observed.add(new ObservedEvent("button", evt.type));
  }

  @EventHandler("input")
  public void onInputChanged(final @ForEvent("change") Event evt) {
    observed.add(new ObservedEvent("input", evt.type));
  }

  @EventHandler("buttonWidget")
  public void onButtonWidgetSingle(final @ForEvent("click") MouseEvent evt) {
    observed.add(new ObservedEvent("buttonWidget", evt.type));
  }

  @EventHandler("buttonGwtElement")
  public void onButtonGwtElementSingle(final @ForEvent("click") MouseEvent evt) {
    observed.add(new ObservedEvent("buttonGwtElement", evt.type));
  }

  @EventHandler("noFieldButton")
  public void onNoFieldButtonSingle(final @ForEvent("click") MouseEvent evt) {
    observed.add(new ObservedEvent("noFieldButton", evt.type));
  }

  @EventHandler("privateHandler")
  private void onButtonDoubleClick(final @ForEvent("dblclick") MouseEvent evt) {
    observed.add(new ObservedEvent("privateHandler", evt.type));
  }

}
