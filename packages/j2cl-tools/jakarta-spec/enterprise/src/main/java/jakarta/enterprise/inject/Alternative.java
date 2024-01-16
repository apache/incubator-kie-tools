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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a bean is an alternative. May be applied to a bean class, producer method or field
 * or {@linkplain Stereotype stereotype}.
 *
 * <pre>
 * &#064;Alternative
 * public class MockOrder extends Order { ... }
 * </pre>
 *
 * <p>An alternative is not available for injection, lookup or EL resolution to classes or JSP/JSF
 * pages in a module unless the module is a bean archive and the alternative is explicitly
 * <em>selected</em> in that bean archive. An alternative is never available for injection, lookup
 * or EL resolution in a module that is not a bean archive.
 *
 * <p>By default, a bean archive has no selected alternatives. An alternative must be explicitly
 * declared using the <code>&lt;alternatives&gt;</code> element of the <code>beans.xml</code> file
 * of the bean archive. The <code>&lt;alternatives&gt;</code> element contains a list of bean
 * classes and stereotypes. An alternative is selected for the bean archive if either:
 *
 * <ul>
 *   <li>the alternative is a managed bean or session bean and the bean class of the bean is listed,
 *   <li>the alternative is a producer method, field or resource, and the bean class that declares
 *       the method or field is listed, or
 *   <li>any <code>&#064;Alternative</code> stereotype of the alternative is listed.
 * </ul>
 *
 * @author Gavin King
 * @author Pete Muir
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Alternative {}
