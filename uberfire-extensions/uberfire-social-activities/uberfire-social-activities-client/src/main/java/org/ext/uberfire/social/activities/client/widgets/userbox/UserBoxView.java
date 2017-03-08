/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.ext.uberfire.social.activities.client.widgets.userbox;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.ext.uberfire.social.activities.client.resources.i18n.Constants;
import org.ext.uberfire.social.activities.client.user.SocialUserImageProvider;
import org.ext.uberfire.social.activities.client.widgets.utils.FollowButton;
import org.ext.uberfire.social.activities.client.widgets.utils.FollowButton.FollowType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserImageRepositoryAPI.ImageSize;
import org.gwtbootstrap3.client.ui.Caption;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ThumbnailPanel;
import org.gwtbootstrap3.client.ui.constants.ImageType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class UserBoxView extends Composite {

    private static final Constants constants = Constants.INSTANCE;
    private static HeaderViewBinder uiBinder = GWT.create(HeaderViewBinder.class);
    @UiField
    Caption caption;
    @UiField
    ThumbnailPanel panel;
    @UiField
    Heading username;
    @UiField
    Paragraph desc;
    private SocialUserImageProvider userImageProvider;

    public UserBoxView() {
        userImageProvider = IOC.getBeanManager().lookupBean(SocialUserImageProvider.class).getInstance();
    }

    public void init(final SocialUser socialUser,
                     final RelationType type,
                     final ParameterizedCommand<String> onClick,
                     final ParameterizedCommand<String> followUnfollowCommand) {
        initWidget(uiBinder.createAndBindUi(this));

        setupUserBox(socialUser,
                     userImageProvider.getImageForSocialUser(socialUser,
                                                             ImageSize.BIG),
                     onClick);
        setupFollowUnfollow(socialUser,
                            type,
                            followUnfollowCommand);
    }

    private void setupFollowUnfollow(final SocialUser socialUser,
                                     final RelationType type,
                                     final ParameterizedCommand<String> followUnfollowCommand) {
        if (type != RelationType.ME && followUnfollowCommand != null) {
            final FollowButton.FollowType followType = type == RelationType.UNFOLLOW ? FollowType.UNFOLLOW : FollowType.FOLLOW;
            final Command wrapper = new Command() {
                @Override
                public void execute() {
                    followUnfollowCommand.execute(socialUser.getUserName());
                }
            };
            final FollowButton button = new FollowButton(followType,
                                                         wrapper);
            button.addStyleName("center-block");
            caption.add(button);
        }
    }

    private void setupUserBox(final SocialUser socialUser,
                              Image userImage,
                              final ParameterizedCommand<String> onClick) {
        final org.gwtbootstrap3.client.ui.Image image = new org.gwtbootstrap3.client.ui.Image(userImage.getUrl());
        image.setType(ImageType.CIRCLE);
        image.setPixelSize(140,
                           140);
        if (onClick != null) {
            image.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    onClick.execute(socialUser.getUserName());
                }
            });
        }
        panel.insert(image,
                     0);
        createLink(socialUser,
                   onClick);
        if (socialUser.getEmail().isEmpty()) {
            //Hide element so that box is the same size for all users
            desc.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            desc.setText(".");
        } else {
            desc.setText(socialUser.getEmail());
        }
    }

    private void createLink(final SocialUser follower,
                            final ParameterizedCommand<String> command) {
        username.setText(follower.getName());
        if (command != null) {
            username.addDomHandler(new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           command.execute(follower.getUserName());
                                       }
                                   },
                                   ClickEvent.getType());
            username.addDomHandler(new MouseOverHandler() {
                                       @Override
                                       public void onMouseOver(MouseOverEvent event) {
                                           username.getElement().getStyle().setCursor(Style.Cursor.POINTER);
                                       }
                                   },
                                   MouseOverEvent.getType());
        }
    }

    public String getUserName() {
        return username.getText();
    }

    public enum RelationType {

        CAN_FOLLOW(UserBoxView.constants.Follow()),
        UNFOLLOW(UserBoxView.constants.Unfollow()),
        ME;

        private String label;

        RelationType() {

        }

        RelationType(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    interface HeaderViewBinder
            extends
            UiBinder<Widget, UserBoxView> {

    }
}