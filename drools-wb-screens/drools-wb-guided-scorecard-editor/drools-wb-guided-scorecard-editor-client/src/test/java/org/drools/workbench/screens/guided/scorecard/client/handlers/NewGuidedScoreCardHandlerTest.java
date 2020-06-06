/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.scorecard.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.scorecard.client.type.GuidedScoreCardResourceType;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.guvnor.common.services.project.categories.Decision;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.resources.EditorIds;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewGuidedScoreCardHandlerTest {

    @Mock
    private GuidedScoreCardEditorService service;
    private Caller<GuidedScoreCardEditorService> serviceCaller;

    @Mock
    private BusyIndicatorView indicatorView;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    @Captor
    private ArgumentCaptor<ResourceRef> refArgumentCaptor;

    private GuidedScoreCardResourceType resourceType = new GuidedScoreCardResourceType(new Decision());

    private NewGuidedScoreCardHandler handler;

    @Before
    public void setup() {
        this.serviceCaller = new CallerMock<>(service);
        this.handler = new NewGuidedScoreCardHandler(serviceCaller,
                                                     resourceType,
                                                     indicatorView,
                                                     authorizationManager,
                                                     sessionInfo);
        when(sessionInfo.getIdentity()).thenReturn(user);
    }

    @Test
    public void checkCanCreateWhenFeatureDisabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(false);

        assertFalse(handler.canCreate());
        assertResourceRef();
    }

    @Test
    public void checkCanCreateWhenFeatureEnabled() {
        when(authorizationManager.authorize(any(ResourceRef.class),
                                            eq(ResourceAction.READ),
                                            eq(user))).thenReturn(true);

        assertTrue(handler.canCreate());
        assertResourceRef();
    }

    private void assertResourceRef() {
        verify(authorizationManager).authorize(refArgumentCaptor.capture(),
                                               eq(ResourceAction.READ),
                                               eq(user));
        assertEquals(EditorIds.GUIDED_SCORE_CARD,
                     refArgumentCaptor.getValue().getIdentifier());
        assertEquals(ActivityResourceType.EDITOR,
                     refArgumentCaptor.getValue().getResourceType());
    }
}
