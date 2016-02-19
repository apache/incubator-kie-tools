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

package org.kie.workbench.common.screens.server.management.client.widget.card;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.TitlePresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CardPresenterTest {

    @Mock
    CardPresenter.View view;

    @InjectMocks
    CardPresenter presenter;

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testAddTitle() {
        TitlePresenter titlePresenter = mock( TitlePresenter.class );
        presenter.addTitle( titlePresenter );

        verify( titlePresenter ).getView();
        verify( view ).add( titlePresenter.getView() );
    }

    @Test
    public void testAddBody() {
        BodyPresenter bodyPresenter = mock( BodyPresenter.class );
        presenter.addBody( bodyPresenter );

        verify( bodyPresenter ).getView();
        verify( view ).add( bodyPresenter.getView() );
    }

    @Test
    public void testAddFooter() {
        FooterPresenter footerPresenter = mock( FooterPresenter.class );
        presenter.addFooter( footerPresenter );

        verify( footerPresenter ).getView();
        verify( view ).add( footerPresenter.getView() );
    }

}