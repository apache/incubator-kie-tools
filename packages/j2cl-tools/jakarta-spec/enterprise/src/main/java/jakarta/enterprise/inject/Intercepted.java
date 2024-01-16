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
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An interceptor may inject metadata about the bean it is intercepting.
 *
 * <pre>
 * &#064;Transactional &#064;Interceptor
 * public class TransactionInterceptor {
 *
 *    &#064;Inject &#064;Intercepted Bean&lt;?&gt; bean;
 *
 *    &#064;AroundInvoke
 *    public Object manageTransaction(InvocationContext ctx) throws Exception { ... }
 *
 * }
 * </pre>
 *
 * @author Pete Muir
 * @since 1.1
 */
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface Intercepted {}
