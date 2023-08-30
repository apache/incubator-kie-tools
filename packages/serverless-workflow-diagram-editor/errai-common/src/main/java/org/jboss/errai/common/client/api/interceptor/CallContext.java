/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.common.client.api.interceptor;

import java.lang.annotation.Annotation;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.RemoteCallback;

/**
 * Represents the context of an intercepted method call.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
abstract class CallContext {
  private Object[] parameters;

  /**
   * Provides access to the intercepted method's parameters.
   * 
   * @return Array of method parameters in declaration order. An empty array if the intercepted method has no
   *         parameters.
   */
  public Object[] getParameters() {
    return parameters;
  }

  /**
   * Overrides the parameters that are passed to the method for which the interceptor was invoked.
   * 
   * @param parameters
   *          the parameters to use when invoking the intercepted method. Must not be null.
   */
  public void setParameters(Object[] parameters) {
    this.parameters = Assert.notNull(parameters);
  }

  /**
   * Returns the name of the intercepted method.
   * 
   * @return the name of the method for which the interceptor was invoked.
   */
  public abstract String getMethodName();

    /**
     * Returns the return type of the intercepted method.
     *
     * @return the return type of the method for which the interceptor was invoked.
     */
    public abstract Class getReturnType();

  /**
   * Returns the annotations of the intercepted method.
   *
   * @return the annotations of the method of which the interceptor was invoked.
   */
  public abstract Annotation[] getAnnotations();
  
  /**
   * Get the annotations of the intercepted type.
   * 
   * @return the annotations of the type on which the interceptor was invoked.
   */
  public abstract Annotation[] getTypeAnnotations();

  /**
   * Proceeds to the next interceptor in the chain or with the execution of the intercepted method if all
   * interceptors have been executed.
   * <p>
   * This method can also be called to proceed with an asynchronous call (e.g. when intercepting a remote procedure
   * call), but only if the call's result is not required in the interceptor logic. If access to the result of an
   * asynchronous method call is needed in the interceptor, one of the overloaded versions of this method accepting a
   * {@link RemoteCallback} has to be used instead.
   * 
   * @return the return value of the intercepted method. Always null for asynchronous methods.
   */
  public abstract Object proceed();
}
