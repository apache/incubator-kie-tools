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


package javax.enterprise.event;

/**
 * <p>Distinguishes conditional
 * {@linkplain javax.enterprise.event.Observes observer methods} from observer
 * methods which are always notified.</p>
 * <p/>
 * <p>A conditional observer method is an observer method which is notified
 * of an event only if an instance of the bean that defines the observer
 * method already exists in the current context.</p>
 * <p/>
 * <p>Beans with scope
 * {@link javax.enterprise.context.Dependent &#064;Dependent} may not
 * have conditional observer methods.</p>
 *
 * @author Gavin King
 * @author Dan Allen
 * @author David Allen
 */
public enum Reception {
  /**
   * Specifies that an observer method is only called if the current instance
   * of the bean declaring the observer method already exists.
   */
  IF_EXISTS,

  /**
   * Specifies that an observer method always receives event notifications.
   */
  ALWAYS
}
