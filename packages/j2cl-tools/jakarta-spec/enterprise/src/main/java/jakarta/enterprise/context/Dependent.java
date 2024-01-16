/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.enterprise.context;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a bean belongs to the dependent pseudo-scope.
 *
 * <p>Beans declared with scope <code>@Dependent</code> behave differently to beans with other
 * built-in scope types. When a bean is declared to have scope <code>@Dependent</code>:
 *
 * <ul>
 *   <li>No injected instance of the bean is ever shared between multiple injection points.
 *   <li>Any instance of the bean injected into an object that is being created by the container is
 *       bound to the lifecycle of the newly created object.
 *   <li>When a Unified EL expression in a JSF or JSP page that refers to the bean by its EL name is
 *       evaluated, at most one instance of the bean is instantiated. This instance exists to
 *       service just a single evaluation of the EL expression. It is reused if the bean EL name
 *       appears multiple times in the EL expression, but is never reused when the EL expression is
 *       evaluated again, or when another EL expression is evaluated.
 *   <li>Any instance of the bean that receives a producer method, producer field, disposer method
 *       or observer method invocation exists to service that invocation only.
 *   <li>Any instance of the bean injected into method parameters of a disposer method or observer
 *       method exists to service the method invocation only.
 * </ul>
 *
 * <p>Every invocation of the {@link Context#get(Contextual, CreationalContext)} operation of the
 * context object for the <code>@Dependent</code> scope returns a new instance of the given bean.
 *
 * <p>Every invocation of the {@link Context#get(Contextual)} operation of the context object for
 * the <code>@Dependent</code> scope returns a null value.
 *
 * <p>The <code>@Dependent</code> scope is always active.
 *
 * <p>Many instances of beans with scope <code>@Dependent</code> belong to some other bean or Java
 * EE component class instance and are called dependent objects.
 *
 * <ul>
 *   <li>Instances of decorators and interceptors are dependent objects of the bean instance they
 *       decorate.
 *   <li>An instance of a bean with scope <code>@Dependent</code> injected into a field, bean
 *       constructor or initializer method is a dependent object of the bean or Java EE component
 *       class instance into which it was injected.
 *   <li>An instance of a bean with scope <code>@Dependent</code> injected into a producer method is
 *       a dependent object of the producer method bean instance that is being produced.
 *   <li>An instance of a bean with scope <code>@Dependent</code> obtained by direct invocation of
 *       an {@link Instance} is a dependent object of the instance of {@link Instance}.
 * </ul>
 *
 * <p>When the container destroys an instance of a bean or of any Java EE component class supporting
 * injection, the container destroys all its dependent objects, after the <code>@PreDestroy</code>
 * callback completes and after the servlet <code>destroy()</code> method is called.
 *
 * @author Gavin King
 * @author Pete Muir
 */
@Target({METHOD, TYPE, FIELD})
@Retention(RUNTIME)
@Documented
@Scope
@Inherited
public @interface Dependent {}
