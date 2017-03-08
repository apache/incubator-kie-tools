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
package org.ext.uberfire.social.activities.client.widgets.item;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;
import org.ext.uberfire.social.activities.client.user.SocialUserImageProvider;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialUserImageRepositoryAPI;
import org.gwtbootstrap3.client.ui.ImageAnchor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CommentRowWidgetTest {

    CommentRowWidget commentRow;
    UpdateItem updateItem;
    ImageAnchor imageAnchorMock;

    @Before
    public void setup() {
        imageAnchorMock = mock(ImageAnchor.class);
        GwtMockito.useProviderForType(ImageAnchor.class,
                                      new FakeProvider<ImageAnchor>() {
                                          @Override
                                          public ImageAnchor getFake(Class<?> aClass) {
                                              return imageAnchorMock;
                                          }
                                      });

        commentRow = new CommentRowWidget() {
            @Override
            SocialUserImageProvider getSocialUserImageProvider() {
                final SocialUserImageProvider provider = mock(SocialUserImageProvider.class);
                when(provider.getImageForSocialUser(any(SocialUser.class),
                                                    any(
                                                            SocialUserImageRepositoryAPI.ImageSize.class))).thenReturn(mock(Image.class));
                return provider;
            }
        };
        commentRow.left = new FlowPanel();
        updateItem = new UpdateItem(new SocialActivitiesEvent(new SocialUser("dora"),
                                                              "",
                                                              new Date()));
    }

    @Test
    public void testCreateThumbNailShouldHaveClickHandler() throws Exception {

        commentRow.createThumbNail(updateItem);

        verify(imageAnchorMock).addClickHandler(any(ClickHandler.class));
    }
}