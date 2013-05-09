/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.uberfire.security.Identity;

/**
 * Presenter for AuditLog
 */
public class AuditLog {

    private AuditLogView view;

    public AuditLog(final GuidedDecisionTable52 dtable,
                    final Identity identity) {
        this.view = new AuditLogViewImpl( dtable.getAuditLog(),
                                          identity );
    }

    public void show() {
        this.view.show();
    }

}
