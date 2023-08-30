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


package org.jboss.errai.ioc.client.container.async;

import com.google.gwt.core.client.RunAsyncCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class DefaultRunAsyncCallback implements RunAsyncCallback {

  private static final Logger log = LoggerFactory.getLogger(AsyncBeanManager.class);

  @Override
  public void onFailure(final Throwable reason) {
    log.error("An error occurred while attempting to download a code fragment.", reason);
  }

}
