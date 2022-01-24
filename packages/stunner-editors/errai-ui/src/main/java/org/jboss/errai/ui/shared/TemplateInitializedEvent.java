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

package org.jboss.errai.ui.shared;

import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when Errai UI has successfully initialized a {@link Templated} composite and
 * attached it to the DOM.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class TemplateInitializedEvent extends GwtEvent<TemplateInitializedEvent.Handler> {

  public static Type<TemplateInitializedEvent.Handler> TYPE = new Type<TemplateInitializedEvent.Handler>();
  
  public interface Handler extends EventHandler {
    void onInitialized();
  }

  @Override
  public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onInitialized();
  }

  public static <S extends HasAttachHandlers> void fire(S source) {
    TemplateInitializedEvent event = new TemplateInitializedEvent();
    source.fireEvent(event);
  }
}
