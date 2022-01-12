/*
 * Copyright (C) 2011 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.api.interceptor;

import java.lang.annotation.*;

/**
 * Indicates that calls to the annotated method will be intercepted by the specified interceptor(s). When used on
 * a class or interface, all methods of the corresponding type will be intercepted.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptedCall {

  /**
   * The interceptor type(s) to use. Interceptor execution is guaranteed to be in declaration order.
   */
  Class<? extends CallInterceptor<? extends CallContext>>[] value();
}
