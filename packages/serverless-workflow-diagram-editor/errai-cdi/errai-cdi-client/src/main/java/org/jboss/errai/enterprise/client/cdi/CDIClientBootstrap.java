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


package org.jboss.errai.enterprise.client.cdi;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.events.BusReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GWT entry point for the Errai CDI module.
 *
 * @author Mike Brock <cbrock@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class CDIClientBootstrap implements EntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(CDIClientBootstrap.class);

  @Override
  public void onModuleLoad() {
    logger.debug("Starting CDI module...");
    if (!EventQualifierSerializer.isSet()) {
      EventQualifierSerializer.set(GWT.create(EventQualifierSerializer.class));
    }

    InitVotes.registerPersistentPreInitCallback(() -> {});
    InitVotes.waitFor(CDI.class);

    CDI.activate();
    CDI.fireEvent(new BusReadyEvent());
  }
}
