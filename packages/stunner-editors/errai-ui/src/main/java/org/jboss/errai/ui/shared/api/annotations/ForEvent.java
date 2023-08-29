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


package org.jboss.errai.ui.shared.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.errai.common.client.api.annotations.BrowserEvent;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.MouseEvent;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Many DOM Event interfaces are used for several different event types. For example, {@link MouseEvent} is used for
 * both {@code click} and {@code dblclick} events, and every DOM event inherits from the {@link Event} interface.
 *
 * <p>
 * When declaring an {@link EventHandler} method it is sometimes necessary or desired to specify a subset of event types
 * to listen for. You can do this by annotating the parameter of an {@link EventHandler} method with {@link ForEvent}
 * and declaring the observed event types in {@link ForEvent#value()}.
 *
 * <p>
 * {@link ForEvent} is mandatory if the {@link EventHandler} parameter is a {@link BrowserEvent} type where
 * {@link BrowserEvent#value()} is empty (in which case {@link ForEvent#value()} must not be empty and may contain any event
 * types).
 *
 * <p>
 * {@link ForEvent} is optional if the {@link EventHandler} parameter is a {@link BrowserEvent} type where
 * {@link BrowserEvent#value()} is non-empty. For such an event type, omitting {@link ForEvent} causes the method to be
 * registered as a listener for all event types in {@link BrowserEvent#value()}. If {@link ForEvent} is used for such a
 * {@link BrowserEvent} type, {@link ForEvent#value()} must contain a subset of values from {@link BrowserEvent#value()}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see BrowserEvent
 * @see EventHandler
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface ForEvent {

  String[] value();

}
