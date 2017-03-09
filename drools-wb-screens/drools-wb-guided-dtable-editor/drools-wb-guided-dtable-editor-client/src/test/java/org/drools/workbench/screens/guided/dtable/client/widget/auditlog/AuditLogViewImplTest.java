/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.auditlog;

import java.util.HashMap;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.auditlog.AuditLog;
import org.drools.workbench.models.guided.dtable.shared.auditlog.DecisionTableAuditLogFilter;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class})
public class AuditLogViewImplTest {

    @Mock
    private User identity;

    private AuditLog auditLog;

    private AuditLogViewImpl view;

    @GwtMock
    Pagination cellTablePagination;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/MM/yyyy");
        }});

        this.auditLog = new AuditLog(new DecisionTableAuditLogFilter());

        final AuditLogViewImpl wrapped = new AuditLogViewImplFake(auditLog,
                                                                  identity);
        this.view = spy(wrapped);
    }

    @Test
    public void showingModalRefreshesDataProvider() {
        view.show();

        verify(view,
               times(1)).refreshDataProvider();
    }

    @Test
    public void checkPaginationIsSynchronizedWithDataListProvider() {
        view.show();

        verify(cellTablePagination,
               times(1)).rebuild(any(SimplePager.class));
    }

    /**
     * Fake subclass to force runtime into thinking BaseModal is already attached to the DOM.
     */
    private static class AuditLogViewImplFake extends AuditLogViewImpl {

        private AuditLogViewImplFake(final AuditLog auditLog,
                                     final User identity) {
            super(auditLog,
                  identity);
        }

        @Override
        public boolean isAttached() {
            return true;
        }
    }
}
