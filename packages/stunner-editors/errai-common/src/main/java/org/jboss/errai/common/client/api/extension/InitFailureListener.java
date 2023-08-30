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


package org.jboss.errai.common.client.api.extension;

import java.util.Set;

/**
 * An <tt>InitFailureListener</tt> is used to listen for components which do not initialize as part of the
 * framework bootstrap at runtime. Generally, framework components are bootstrapped based on class-based
 * topic. The appearance of one of
 * these topics in the {@link Set} passed to the {@link #onInitFailure(java.util.Set)} method indicates
 * this component timed out and did not initialize.
 *
 * @author Mike Brock
 */
public interface InitFailureListener {

  /**
   * Called when an initialization failure occurs.
   *
   * @param failedTopics
   *     Represents a set of initialization topics which did not initialize. These are typically
   *     based on the fully-qualified class names of components.
   */
  public void onInitFailure(Set<String> failedTopics);
}
