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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.errai.databinding.client.api.Bindable;
import org.jboss.errai.databinding.client.api.DataBinder;

/**
 * <p>
 * Annotation for declaring methods as
 * {@link org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler PropertyChangeHandlers}.
 *
 * <p>
 * When a proxy for a {@link Bindable} type is created, methods with this annotation will be registered as property
 * change handlers, as if registered with
 * {@link DataBinder#addPropertyChangeHandler(String, org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler)}.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PropertyChangeHandler {
  /**
   * The name of the property for which this method is a handler. Handles changes to all properties if this value is
   * empty.
   */
  String value() default "";
}
