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

package org.kie.workbench.common.screens.server.management.client.remote.card;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.Severity;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.InfoTitlePresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.LinkTitlePresenter;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

@RunWith(MockitoJUnitRunner.class)
public class ContainerCardPresenterTest {

    @Mock
    ManagedInstance<Object> presenterProvider;

    @Mock
    ManagedInstance<InfoTitlePresenter> infoTitlePresenterProvider;

    @Mock
    ManagedInstance<LinkTitlePresenter> linkTitlePresenterProvider;

    @Mock
    ManagedInstance<BodyPresenter> bodyPresenterProvider;

    @Mock
    ManagedInstance<FooterPresenter> footerPresenterProvider;

    @Mock
    ManagedInstance<CardPresenter> cardPresenterProvider;


    @Mock
    org.kie.workbench.common.screens.server.management.client.container.status.card.ContainerCardPresenter.View view;

    @Spy
    Event<ContainerSpecSelected> containerSpecSelectedEvent = new EventSourceMock<ContainerSpecSelected>();

    ContainerCardPresenter presenter;

    @Before
    public void init() {
        doNothing().when( containerSpecSelectedEvent ).fire( any( ContainerSpecSelected.class ) );
        presenter = spy( new ContainerCardPresenter( presenterProvider, view, containerSpecSelectedEvent ) );
        when( presenterProvider.select( InfoTitlePresenter.class ) ).thenReturn( infoTitlePresenterProvider );
        when( presenterProvider.select( LinkTitlePresenter.class ) ).thenReturn( linkTitlePresenterProvider );
        when( presenterProvider.select( BodyPresenter.class ) ).thenReturn( bodyPresenterProvider );
        when( presenterProvider.select( FooterPresenter.class ) ).thenReturn( footerPresenterProvider );
        when( presenterProvider.select( CardPresenter.class ) ).thenReturn( cardPresenterProvider );
    }

    @Test
    public void testInit() {
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testSetup() {
        final InfoTitlePresenter infoTitlePresenter = mock( InfoTitlePresenter.class );
        when( infoTitlePresenterProvider.get() ).thenReturn( infoTitlePresenter );

        final LinkTitlePresenter linkTitlePresenter = spy( new LinkTitlePresenter( mock( LinkTitlePresenter.View.class ) ) );
        when( linkTitlePresenterProvider.get() ).thenReturn( linkTitlePresenter );

        final BodyPresenter bodyPresenter = mock( BodyPresenter.class );
        when( bodyPresenterProvider.get() ).thenReturn( bodyPresenter );
        final FooterPresenter footerPresenter = mock( FooterPresenter.class );
        when( footerPresenterProvider.get() ).thenReturn( footerPresenter );
        final CardPresenter.View cardPresenterView = mock( CardPresenter.View.class );
        final CardPresenter cardPresenter = spy( new CardPresenter( cardPresenterView ) );
        when( cardPresenterProvider.get() ).thenReturn( cardPresenter );

        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey( "templateId", "serverName", "serverInstanceId", "url" );
        final Message message = new Message( Severity.INFO, "testMessage" );
        final ReleaseId resolvedReleasedId = new ReleaseId( "org.kie", "container", "1.0.0" );
        final Container container = new Container( "containerSpecId", "containerName", serverInstanceKey, Collections.singletonList( message ), resolvedReleasedId, null );

        presenter.setup( container );

        verify( linkTitlePresenter ).setup( eq( container.getContainerName() ), any( Command.class ) );
        verify( infoTitlePresenter ).setup( container.getResolvedReleasedId() );
        verify( bodyPresenter ).setup( Arrays.asList( message ) );
        verify( footerPresenter ).setup( container.getUrl(), resolvedReleasedId.getVersion() );
        verify( cardPresenter ).addTitle( linkTitlePresenter );
        verify( cardPresenter ).addTitle( infoTitlePresenter );
        verify( cardPresenter ).addBody( bodyPresenter );
        verify( cardPresenter ).addFooter( footerPresenter );
        verify( view ).setCard( cardPresenterView );
        linkTitlePresenter.onSelect();

        final ContainerSpecKey containerSpecKey = new ContainerSpecKey( container.getContainerSpecId(),
                                                                        container.getContainerName(),
                                                                        new ServerTemplateKey( container.getServerInstanceKey().getServerTemplateId(), "" ) );

        verify( containerSpecSelectedEvent ).fire( eq( new ContainerSpecSelected( containerSpecKey ) ) );
    }

    @Test
    public void testSetupWithUnNamedContainer() {
        final InfoTitlePresenter infoTitlePresenter = mock(InfoTitlePresenter.class);
        when(infoTitlePresenterProvider.get()).thenReturn(infoTitlePresenter);

        final LinkTitlePresenter linkTitlePresenter = spy(new LinkTitlePresenter(mock(LinkTitlePresenter.View.class)));
        when(linkTitlePresenterProvider.get()).thenReturn(linkTitlePresenter);

        final BodyPresenter bodyPresenter = mock(BodyPresenter.class);
        when(bodyPresenterProvider.get()).thenReturn(bodyPresenter);
        final FooterPresenter footerPresenter = mock(FooterPresenter.class);
        when(footerPresenterProvider.get()).thenReturn(footerPresenter);
        final CardPresenter.View cardPresenterView = mock(CardPresenter.View.class);
        final CardPresenter cardPresenter = spy(new CardPresenter(cardPresenterView));
        when(cardPresenterProvider.get()).thenReturn(cardPresenter);

        final ServerInstanceKey serverInstanceKey = new ServerInstanceKey("templateId",
                                                                          "serverName",
                                                                          "serverInstanceId",
                                                                          "url");
        final Message message = new Message(Severity.INFO, "testMessage");
        final ReleaseId resolvedReleasedId = new ReleaseId("org.kie",
                                                           "container",
                                                           "1.0.0");
        final Container container = new Container("containerSpecId",
                                                  null,
                                                  serverInstanceKey,
                                                  Collections.singletonList(message),
                                                  resolvedReleasedId, null);

        presenter.setup(container);

        verify(linkTitlePresenter).setup(eq(container.getContainerSpecId()), any(Command.class));
        verify(view).setCard(cardPresenterView);
        linkTitlePresenter.onSelect();

        final ContainerSpecKey containerSpecKey = new ContainerSpecKey(container.getContainerSpecId(),
                                                                       container.getContainerSpecId(),
                                                                       new ServerTemplateKey(container.getServerInstanceKey().getServerTemplateId(), ""));

        verify(containerSpecSelectedEvent).fire(eq(new ContainerSpecSelected(containerSpecKey)));
    }

}