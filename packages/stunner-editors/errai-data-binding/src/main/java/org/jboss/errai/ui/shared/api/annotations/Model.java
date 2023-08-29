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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import org.jboss.errai.databinding.client.api.DataBinder;

/**
 * Indicates that the annotated model should be managed by a {@link DataBinder} and therefore
 * automatically bound to all enclosing widgets.
 * <p>
 * The widgets are inferred from all enclosing fields and methods annotated with {@link Bound} of
 * the class that defines the {@link Model} and all its super classes.
 * <p>
 * The annotated model can be a field and a method or constructor parameter. The following
 * example shows all use cases for the {@link Model} annotation.
 *
 * <pre>
 *      public class MyBean {
 *        {@code @Inject} {@code @Model}
 *        private MyModel model;
 *
 *        {@code @Inject}
 *        public MyBean({@code @Model} MyModel model) {
 *          this.model = model;
 *        }
 *
 *        {@code @Inject}
 *        public void setModel({@code @Model} MyModel model) {
 *          this.model = model;
 *        }
 *      }
 * </pre>
 *
 * There can only be one {@link Model} per class.
 * <p>
 *
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Mike Brock
 */
@Qualifier
@Documented
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Model {

}
