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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.server.management.client.widget.card.body.BodyPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.footer.FooterPresenter;
import org.kie.workbench.common.screens.server.management.client.widget.card.title.TitlePresenter;
import org.uberfire.client.mvp.UberView;

@Dependent
public class CardPresenter {

    public interface View extends UberView<CardPresenter> {

        void add( IsWidget view );
    }

    private final View view;

    @Inject
    public CardPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void addTitle( final TitlePresenter titlePresenter ) {
        view.add( titlePresenter.getView() );
    }

    public void addBody( final BodyPresenter bodyPresenter ) {
        view.add( bodyPresenter.getView() );
    }

    public void addFooter( final FooterPresenter footerPresenter ) {
        view.add( footerPresenter.getView() );
    }

}
