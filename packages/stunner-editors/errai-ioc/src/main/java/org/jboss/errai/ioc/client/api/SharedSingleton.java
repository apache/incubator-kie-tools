/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ioc.client.api;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;

import jsinterop.annotations.JsType;

/**
 * Within a single compiled GWT script this scope is synonymous with
 * {@link ApplicationScoped}.
 *
 * When multiple separately compiled GWT scripts are loaded on a page, this
 * scope indicates that there should be exactly one instance of this bean used
 * to satisfy {@link Shared} injection points in all of the scripts, for all
 * injection sites using {@link JsType}s.
 *
 * If both scripts are compiled with the same {@link SharedSingleton} type, the
 * instance in the script executing first will be used.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD })
@NormalScope
@Inherited
public @interface SharedSingleton {

}
