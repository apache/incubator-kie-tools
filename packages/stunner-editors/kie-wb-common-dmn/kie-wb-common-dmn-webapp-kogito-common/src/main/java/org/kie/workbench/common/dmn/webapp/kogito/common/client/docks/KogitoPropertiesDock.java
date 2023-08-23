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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.kogito.client.docks.DiagramEditorPropertiesDock;
import org.uberfire.client.workbench.docks.UberfireDocks;

@Specializes
@ApplicationScoped
public class KogitoPropertiesDock extends DiagramEditorPropertiesDock {

    public KogitoPropertiesDock() {
        // CDI proxy
    }

    @Inject
    public KogitoPropertiesDock(final UberfireDocks uberfireDocks,
                                final TranslationService translationService) {
        super(uberfireDocks, translationService);
    }

    @Override
    public void init() {
        if (uberfireDock == null) {
            this.uberfireDock = makeUberfireDock();

            uberfireDocks.add(getUberfireDock());
            uberfireDocks.show(position());
        }
    }

    @Override
    public void open() {
        if (isOpened()) {
            return;
        }

        isOpened = true;
        uberfireDocks.open(getUberfireDock());
    }

    @Override
    public void close() {
        if (!isOpened()) {
            return;
        }

        isOpened = false;
        uberfireDocks.close(getUberfireDock());
    }

    @Override
    public void destroy() {
        if (uberfireDock != null) {
            uberfireDocks.remove(getUberfireDock());
            uberfireDock = null;
        }
    }
}
