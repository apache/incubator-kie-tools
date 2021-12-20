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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * <p>The built-in qualifier type.</p>
 *
 * <p>Every bean has the qualifier <tt>&#064;Any</tt>,
 * even if it does not explicitly declare this qualifier,
 * except for the special
 * {@link javax.enterprise.inject.New &#064;New qualified beans}.</p>
 *
 * <p>Every event has the qualifier <tt>&#064;Any</tt>,
 * even if it was raised without explicitly declaration
 * of this qualifier.</p>
 *
 * <p>The <tt>&#064;Any</tt> qualifier allows an injection
 * point to refer to all beans or all events of a certain
 * bean type.</p>
 *
 * <pre>
 * &#064;Inject &#064;Any Instance&lt;PaymentProcessor&gt; anyPaymentProcessor;
 * </pre>
 *
 * <pre>
 * &#064;Inject &#064;Any Event&lt;User&gt; anyUserEvent;
 * </pre>
 *
 * <pre>
 * &#064;Inject &#064;Delegate &#064;Any Logger logger;
 * </pre>
 *
 * @author Gavin King
 * @author David Allen
 */

@Qualifier
@Retention(RUNTIME)
@Target( { TYPE, METHOD, FIELD, PARAMETER })
@Documented
public @interface Any
{

}
