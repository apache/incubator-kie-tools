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

package jakarta.enterprise.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.enterprise.context.Dependent;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Identifies a producer method or field. May be applied to a method or field of a bean class.
 *
 * <p>A producer method must be a non-abstract method of a managed bean class or session bean class.
 * A producer method may be either static or non-static. If the bean is a session bean, the producer
 * method must be either a business method of the EJB or a static method of the bean class.
 *
 * <pre>
 * public class Shop {
 *    &#064;Produces &#064;ApplicationScoped
 *    &#064;Catalog &#064;Named("catalog")
 *    List&lt;Product&gt; getProducts() { ... }
 *    ...
 * }
 * </pre>
 *
 * <p>A producer field must be a field of a managed bean class or session bean class. A producer
 * field may be either static or non-static. If the bean is a session bean, the producer field must
 * be a static field of the bean class.
 *
 * <pre>
 * public class Shop {
 *    &#064;Produces &#064;ApplicationScoped
 *    &#064;Catalog &#064;Named("catalog")
 *    List&lt;Product&gt; products = ...;
 *    ...
 * }
 * </pre>
 *
 * <p>If a producer method sometimes returns a null value, or if a producer field sometimes contains
 * a null value when accessed, then the producer method or field must have scope {@link Dependent
 * &#064;Dependent}.
 *
 * <p>A producer method return type or producer field type may not be a type variable.
 *
 * <p>If the producer method return type or producer field type is a parameterized type, it must
 * specify an actual type parameter or type variable for each type parameter.
 *
 * <p>If the producer method return type or producer field type is a parameterized type with a type
 * variable, it must have scope {@link Dependent &#064;Dependent}.
 *
 * <p>A producer method may have any number of parameters. All producer method parameters are
 * injection points.
 *
 * <pre>
 * public class OrderFactory {
 *
 *     &#064;Produces
 *     &#064;ConversationScoped
 *     public Order createCurrentOrder(Shop shop, @Selected Product product) {
 *         Order order = new Order(product, shop);
 *         return order;
 *     }
 *
 * }
 * </pre>
 *
 * <p>A bean may declare multiple producer methods or fields.
 *
 * <p>Producer methods and fields are not inherited by bean subclasses.
 *
 * <p>Interceptors and decorators may not declare producer methods or fields.
 *
 * @see Disposes &#064;Disposes
 * @author Gavin King
 * @author Pete Muir
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Produces {}
