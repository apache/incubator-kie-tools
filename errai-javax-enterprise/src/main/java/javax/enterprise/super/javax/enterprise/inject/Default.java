/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

package javax.enterprise.inject;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>The default qualifier type.</p>
 *
 * <p>If a bean does not explicitly declare a qualifier other than
 * {@link javax.inject.Named &#064;Named}, the bean has the qualifier
 * <tt>&#064;Default</tt>.</p>
 *
 * <p>If an injection point declares no qualifier, the injection point
 * has exactly one qualifier, the default qualifier
 * <tt>&#064;Default</tt>.</p>
 *
 * <p>The following are equivalent:</p>
 *
 * <pre>
 * &#064;ConversationScoped
 * public class Order {
 *
 *    private Product product;
 *    private User customer;
 *
 *    &#064;Inject
 *    public void init(&#064;Selected Product product, User customer) {
 *       this.product = product;
 *       this.customer = customer;
 *   }
 *
 * }
 * </pre>
 *
 * <pre>
 * &#064;Default &#064;ConversationScoped
 * public class Order {
 *
 *    private Product product;
 *    private User customer;
 *
 *    &#064;Inject
 *    public void init(&#064;Selected Product product, &#064;Default User customer) {
 *       this.product = product;
 *       this.customer = customer;
 *    }
 *
 * }
 * </pre>
 *
 * @author Pete Muir
 * @author Gavin King
 */

@Target({TYPE, METHOD, PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface Default {

    /**
     * Supports inline instantiation of the {@link Default} qualifier.
     *
     * @author Martin Kouba
     * @see Instance
     * @see Event
     * @since 2.0
     */
    public static final class Literal extends AnnotationLiteral<Default> implements Default {

        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;
    }
}
