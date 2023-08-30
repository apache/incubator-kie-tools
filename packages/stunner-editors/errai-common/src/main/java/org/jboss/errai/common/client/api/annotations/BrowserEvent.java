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


package org.jboss.errai.common.client.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a native {@link JsType} as a wrapper for a DOM event. {@link BrowserEvent#value()} are the event type names
 * that the annotated type can be used with (i.e. click, dblclick, change, etc.). A type annotated with
 * {@code BrowserEvent} can be used with the {@code @EventHandler} annotation in Errai UI templates.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Retention(RUNTIME)
@Documented
@Target(TYPE)
public @interface BrowserEvent {

  /**
   * A list of event types supported by this event (i.e. click, dblclick, change). If this value has length 0 then the
   * annotated type supports all browser event types.
   */
  String[] value() default {};

}
