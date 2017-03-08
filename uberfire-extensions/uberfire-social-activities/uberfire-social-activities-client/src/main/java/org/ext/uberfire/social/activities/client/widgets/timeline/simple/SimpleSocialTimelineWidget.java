/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.client.widgets.timeline.simple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.ext.uberfire.social.activities.client.resources.i18n.Constants;
import org.ext.uberfire.social.activities.client.widgets.item.SimpleItemWidget;
import org.ext.uberfire.social.activities.client.widgets.item.bundle.SocialBundleHelper;
import org.ext.uberfire.social.activities.client.widgets.item.model.SimpleItemWidgetModel;
import org.ext.uberfire.social.activities.client.widgets.pagination.Pager;
import org.ext.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.model.PagedSocialQuery;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialTypeTimelinePagedRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserTimelinePagedRepositoryAPI;
import org.gwtbootstrap3.client.ui.MediaList;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

public class SimpleSocialTimelineWidget extends Composite {

    static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    MediaList itemsPanel;

    @UiField
    FlowPanel pagination;
    private SimpleSocialTimelineWidgetModel model;

    public SimpleSocialTimelineWidget(SimpleSocialTimelineWidgetModel model) {
        initWidget(uiBinder.createAndBindUi(this));
        this.model = model;
        setupPaginationLinks();
        refreshTimelineWidget();
    }

    private void refreshTimelineWidget() {
        itemsPanel.clear();
        createWidgets();
    }

    private void createWidgets() {
        pagination.clear();
        if (model.isSocialTypeWidget()) {
            createSociaTypelItemsWidget();
        } else {
            createUserTimelineItemsWidget();
        }
    }

    private void createUserTimelineItemsWidget() {
        MessageBuilder.createCall(new RemoteCallback<PagedSocialQuery>() {
                                      public void callback(PagedSocialQuery paged) {
                                          createTimeline(paged);
                                      }
                                  },
                                  SocialUserTimelinePagedRepositoryAPI.class).getUserTimeline(model.getSocialUser(),
                                                                                              model.getSocialPaged(),
                                                                                              model.getPredicate());
    }

    private void createSociaTypelItemsWidget() {
        MessageBuilder.createCall(new RemoteCallback<PagedSocialQuery>() {
                                      public void callback(PagedSocialQuery paged) {
                                          createTimeline(paged);
                                      }
                                  },
                                  SocialTypeTimelinePagedRepositoryAPI.class).getEventTimeline(model.getSocialEventType().name(),
                                                                                               model.getSocialPaged(),
                                                                                               model.getPredicate());
    }

    private void createTimeline(PagedSocialQuery paged) {
        if (thereIsNoEvents(paged)) {
            displayNoEvents();
        } else {
            displayEvents(paged);
        }
    }

    private void displayNoEvents() {
        pagination.add(new Paragraph(Constants.INSTANCE.NoSocialEvents()));
    }

    private boolean thereIsNoEvents(PagedSocialQuery paged) {
        return paged.socialEvents().isEmpty() && !paged.socialPaged().canIGoBackward();
    }

    private void displayEvents(PagedSocialQuery paged) {
        model.updateSocialPaged(paged.socialPaged());
        for (final SocialActivitiesEvent event : paged.socialEvents()) {
            if (event.hasLink()) {
                createSimpleWidgetWithLink(event);
            } else {
                createSimpleWidget(event);
            }
        }
        setupPaginationButtonsSocial();
    }

    private void createSimpleWidgetWithLink(final SocialActivitiesEvent event) {

        final SimpleItemWidgetModel itemModel = new SimpleItemWidgetModel(model,
                                                                          event.getType(),
                                                                          event.getTimestamp(),
                                                                          event.getLinkLabel(),
                                                                          event.getLinkTarget(),
                                                                          event.getLinkType(),
                                                                          SocialBundleHelper.getItemDescription(event.getAdicionalInfos()),
                                                                          event.getSocialUser())
                .withLinkCommand(model.getLinkCommand())
                .withLinkParams(event.getLinkParams());

        if (event.isVFSLink()) {
            MessageBuilder.createCall(new RemoteCallback<Path>() {
                                          public void callback(Path path) {
                                              itemModel.withLinkPath(path);
                                              addItemWidget(itemModel);
                                          }
                                      },
                                      VFSService.class).get(event.getLinkTarget());
        } else {
            addItemWidget(itemModel);
        }
    }

    private void addItemWidget(SimpleItemWidgetModel model) {
        final SimpleItemWidget item = GWT.create(SimpleItemWidget.class);
        item.init(model);
        itemsPanel.add(item);
    }

    private void createSimpleWidget(SocialActivitiesEvent event) {
        final SimpleItemWidgetModel rowModel = new SimpleItemWidgetModel(event.getType(),
                                                                         event.getTimestamp(),
                                                                         event.getDescription(),
                                                                         SocialBundleHelper.getItemDescription(event.getAdicionalInfos()),
                                                                         event.getSocialUser())
                .withLinkParams(event.getLinkParams());
        addItemWidget(rowModel);
    }

    private void setupPaginationButtonsSocial() {
        final Pager pager = new Pager();
        if (canICreateLessLink()) {
            pager.add(model.getLess());
        }
        if (canICreateMoreLink()) {
            pager.add(model.getMore());
        }
        if (canICreateLessLink() || canICreateMoreLink()) {
            pagination.add(pager);
        }
    }

    private boolean canICreateMoreLink() {
        return model.getSocialPaged().canIGoForward() && model.getMore() != null;
    }

    private boolean canICreateLessLink() {
        return model.getSocialPaged().canIGoBackward() && model.getLess() != null;
    }

    private void setupPaginationLinks() {
        if (model.getLess() != null) {
            createLessLink();
        }
        if (model.getMore() != null) {
            createMoreLink();
        }
    }

    private void createMoreLink() {
        model.getMore().addDomHandler(new ClickHandler() {
                                          @Override
                                          public void onClick(ClickEvent event) {
                                              model.getSocialPaged().forward();
                                              refreshTimelineWidget();
                                          }
                                      },
                                      ClickEvent.getType());
    }

    private void createLessLink() {
        model.getLess().addDomHandler(new ClickHandler() {
                                          @Override
                                          public void onClick(ClickEvent event) {
                                              model.getSocialPaged().backward();
                                              refreshTimelineWidget();
                                          }
                                      },
                                      ClickEvent.getType());
    }

    interface MyUiBinder extends UiBinder<Widget, SimpleSocialTimelineWidget> {

    }
}
