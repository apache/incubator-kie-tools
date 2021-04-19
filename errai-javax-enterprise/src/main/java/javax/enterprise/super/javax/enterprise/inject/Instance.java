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

import java.lang.annotation.Annotation;

import javax.inject.Provider;


/**
 * <p>Allows the application to dynamically obtain instances of
 * beans with a specified combination of required type and
 * qualifiers.</p>
 *
 * <p>In certain situations, injection is not the most convenient
 * way to obtain a contextual reference. For example, it may not
 * be used when:</p>
 *
 * <ul>
 * <li>the bean type or qualifiers vary dynamically at runtime, or</li>
 * <li>depending upon the deployment, there may be no bean which
 * satisfies the type and qualifiers, or</li>
 * <li>we would like to iterate over all beans of a certain type.</li>
 * </ul>
 *
 * <p>In these situations, an instance of the <tt>Instance</tt> may
 * be injected:</p>
 *
 * <pre>
 * &#064;Inject Instance&lt;PaymentProcessor&gt; paymentProcessor;
 * </pre>
 *
 * <p>Any combination of qualifiers may be specified at the injection
 * point:</p>
 *
 * <pre>
 * &#064;Inject &#064;PayBy(CHEQUE) Instance&lt;PaymentProcessor&gt; chequePaymentProcessor;
 * </pre>
 *
 * <p>Or, the {@link javax.enterprise.inject.Any &#064;Any} qualifier may
 * be used, allowing the application to specify qualifiers dynamically:</p>
 *
 * <pre>
 * &#064;Inject &#064;Any Instance&lt;PaymentProcessor&gt; anyPaymentProcessor;
 * </pre>
 *
 * <p>Finally, the {@link javax.enterprise.inject.New &#064;New} qualifier
 * may be used, allowing the application to obtain a
 * {@link javax.enterprise.inject.New &#064;New} qualified bean:</p>
 *
 * <pre>
 * &#064;Inject &#064;New(ChequePaymentProcessor.class)
 * Instance&lt;PaymentProcessor&gt; chequePaymentProcessor;
 * </pre>
 *
 * <p>For an injected <tt>Instance</tt>:</p>
 *
 * <ul>
 * <li>the <em>required type</em> is the type parameter specified at the
 * injection point, and</li>
 * <li>the <em>required qualifiers</em> are the qualifiers specified at
 * the injection point.</li>
 * </ul>
 *
 * <p>The inherited {@link javax.inject.Provider#get()} method returns a
 * contextual references for the unique bean that matches the required
 * type and required qualifiers and is eligible for injection into the
 * class into which the parent <tt>Instance</tt> was injected, or throws
 * an {@link javax.enterprise.inject.UnsatisfiedResolutionException} or
 * {@link javax.enterprise.inject.AmbiguousResolutionException}.</p>
 *
 * <pre>PaymentProcessor pp = chequePaymentProcessor.get();</pre>
 *
 * <p>The inherited {@link java.lang.Iterable#iterator()} method returns
 * an iterator over contextual references for beans that match the required
 * type and required qualifiers and are eligible for injection into the class
 * into which the parent <tt>Instance</tt> was injected.</p>
 *
 * <pre>for (PaymentProcessor pp: anyPaymentProcessor) pp.test();</pre>
 *
 * @see javax.inject.Provider#get()
 * @see java.lang.Iterable#iterator()
 * @see javax.enterprise.util.AnnotationLiteral
 * @see javax.enterprise.util.TypeLiteral
 *
 * @author Gavin King
 *
 * @param <T> the required bean type
 */

public interface Instance<T> extends Iterable<T>, Provider<T>
{

   /**
    * <p>Obtains a child <tt>Instance</tt> for the given additional
    * required qualifiers.</p>
    *
    * @param qualifiers the additional required qualifiers
    * @return the child <tt>Instance</tt>
    * @throws IllegalArgumentException if passed two instances of the
    * same qualifier type, or an instance of an annotation that is not
    * a qualifier type
    */
   public Instance<T> select(Annotation... qualifiers);

   /**
    * <p>Obtains a child <tt>Instance</tt> for the given required type and
    * additional required qualifiers.</p>
    *
    * @param <U> the required type
    * @param subtype a {@link java.lang.Class} representing the required type
    * @param qualifiers the additional required qualifiers
    * @return the child <tt>Instance</tt>
    * @throws IllegalArgumentException if passed two instances of the
    * same qualifier type, or an instance of an annotation that is not
    * a qualifier type
    */
   public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers);

   /**
    * <p>Determines if there is no bean that matches the required type and
    * qualifiers and is eligible for injection into the class into which the parent
    * <tt>Instance</tt> was injected.</p>
    *
    * @return <tt>true</tt> if there is no bean that matches the required type and
    * qualifiers and is eligible for injection into the class into which the parent
    * <tt>Instance</tt> was injected, or <tt>false</tt> otherwise.
    */
   public boolean isUnsatisfied();

   /**
    * <p>Determines if there is more than one bean that matches the required type and
    * qualifiers and is eligible for injection into the class into which the parent
    * <tt>Instance</tt> was injected.</p>
    *
    * @return <tt>true</tt> if there is more than one bean that matches the required
    * type and qualifiers and is eligible for injection into the class into which the
    * parent <tt>Instance</tt> was injected, or <tt>false</tt> otherwise.
    */
   public boolean isAmbiguous();

    /**
     * <p>
     * When called, the container destroys the instance if the active context object for the scope
     * type of the bean supports destroying bean instances. All normal scoped built-in contexts support destroying bean
     * instances.
     * </p>
     *
     * <p>
     * The instance passed should either be a dependent scoped bean instance, or the client proxy for a normal scoped bean
     * instance.
     * </p>
     *
     *
     * @since 1.1
     * @param instance the instance to destroy
     * @throws UnsupportedOperationException if the active context object for the scope type of the bean does not support
     *         destroying bean instances
     */
    public void destroy(T instance);

}
