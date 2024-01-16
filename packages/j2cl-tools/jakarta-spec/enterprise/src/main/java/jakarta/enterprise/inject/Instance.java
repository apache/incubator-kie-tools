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

import jakarta.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * Allows the application to dynamically obtain instances of beans with a specified combination of
 * required type and qualifiers.
 *
 * <p>In certain situations, injection is not the most convenient way to obtain a contextual
 * reference. For example, it may not be used when:
 *
 * <ul>
 *   <li>the bean type or qualifiers vary dynamically at runtime, or
 *   <li>depending upon the deployment, there may be no bean which satisfies the type and
 *       qualifiers, or
 *   <li>we would like to iterate over all beans of a certain type.
 * </ul>
 *
 * <p>In these situations, an instance of the <code>Instance</code> may be injected:
 *
 * <pre>
 * &#064;Inject
 * Instance&lt;PaymentProcessor&gt; paymentProcessor;
 * </pre>
 *
 * <p>Any combination of qualifiers may be specified at the injection point:
 *
 * <pre>
 * &#064;Inject
 * &#064;PayBy(CHEQUE)
 * Instance&lt;PaymentProcessor&gt; chequePaymentProcessor;
 * </pre>
 *
 * <p>Or, the {@link Any &#064;Any} qualifier may be used, allowing the application to specify
 * qualifiers dynamically:
 *
 * <pre>
 * &#064;Inject
 * &#064;Any
 * Instance&lt;PaymentProcessor&gt; anyPaymentProcessor;
 * </pre>
 *
 * <p>For an injected <code>Instance</code>:
 *
 * <ul>
 *   <li>the <em>required type</em> is the type parameter specified at the injection point, and
 *   <li>the <em>required qualifiers</em> are the qualifiers specified at the injection point.
 * </ul>
 *
 * <p>The inherited {@link jakarta.inject.Provider#get()} method returns a contextual references for
 * the unique bean that matches the required type and required qualifiers and is eligible for
 * injection into the class into which the parent <code>Instance</code> was injected, or throws an
 * {@link UnsatisfiedResolutionException} or {@link AmbiguousResolutionException}.
 *
 * <pre>
 * PaymentProcessor pp = chequePaymentProcessor.get();
 * </pre>
 *
 * <p>The inherited {@link java.lang.Iterable#iterator()} method returns an iterator over contextual
 * references for beans that match the required type and required qualifiers and are eligible for
 * injection into the class into which the parent <code>Instance</code> was injected.
 *
 * <pre>
 * for (PaymentProcessor pp : anyPaymentProcessor)
 *     pp.test();
 * </pre>
 *
 * @see jakarta.inject.Provider#get()
 * @see java.lang.Iterable#iterator()
 * @see AnnotationLiteral
 * @see TypeLiteral
 * @author Gavin King
 * @author John Ament
 * @author Martin Kouba
 * @param <T> the required bean type
 */
public interface Instance<T> extends Iterable<T>, Provider<T> {

  Instance<T> select(Annotation... var1);

  <U extends T> Instance<U> select(Class<U> var1, Annotation... var2);

  boolean isUnsatisfied();

  boolean isAmbiguous();

  void destroy(T var1);
}
