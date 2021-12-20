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

package javax.enterprise.context;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>Specifies that an annotation type is a normal scope type.</p>
 *
 * @author Gavin King
 * @author Pete Muir
 *
 * @see javax.inject.Scope &#064;Scope is used to declare pseudo-scopes.
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
@Documented
public @interface NormalScope
{

   /**
    * <p>Determines whether the normal scope type is a passivating scope.</p>
    *
    * <p>A bean is called passivation capable if the container is able to
    * temporarily transfer the state of any idle instance to secondary
    * storage. A passivating scope requires that beans with the scope are
    * passivation capable.</p>
    *
    * @return <tt>true</tt> if the scope type is a passivating scope type
    */
   boolean passivating() default false;

}
