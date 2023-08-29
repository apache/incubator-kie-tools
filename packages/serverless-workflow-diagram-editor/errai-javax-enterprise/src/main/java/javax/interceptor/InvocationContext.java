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


package javax.interceptor;

import java.lang.reflect.Method;

/**
 * The InvocationContext object provides the metadata that is required for
 * AroundInvoke interceptor methods.
 * 
 * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
 * @version $Revision: 85923 $
 */
public interface InvocationContext
{
   public Object getTarget();

   public Method getMethod();

   public Object[] getParameters();

   public void setParameters(Object[] params);

   /**
    * Returns the context data associated with this invocation or lifecycle callback. If there is no context data, an empty Map object will be returned.
    */
   public java.util.Map<String, Object> getContextData();

   /**
    * Returns the timer associated with an @AroundTimeout method.
    * 
    * @since 3.1
    */
   Object getTimer();
   
   public Object proceed() throws Exception;
}
