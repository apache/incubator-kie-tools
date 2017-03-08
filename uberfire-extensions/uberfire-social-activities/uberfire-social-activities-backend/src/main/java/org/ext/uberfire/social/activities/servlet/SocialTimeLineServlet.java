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

package org.ext.uberfire.social.activities.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.service.SocialAdapter;
import org.ext.uberfire.social.activities.service.SocialRouterAPI;
import org.ext.uberfire.social.activities.service.SocialTimeLineRepositoryAPI;

public class SocialTimeLineServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    SocialTimeLineRepositoryAPI timeLineRepositoryAPI;

    @Inject
    SocialRouterAPI socialRouter;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException,
            IOException {

        try {
            SocialAdapter socialAdapter = socialRouter.getSocialAdapterByPath(request.getPathInfo());
            Map commandsMap = request.getParameterMap();
            List<SocialActivitiesEvent> eventTimeline = timeLineRepositoryAPI.getLastEventTimeline(socialAdapter,
                                                                                                   commandsMap);

            response.setContentType("application/atom+xml");

            String url = "/social" + request.getPathInfo();
            response.getWriter().println(createFeed(eventTimeline,
                                                    url));
        } catch (SocialRouter.SocialAdapterNotFound e) {
            throw e;
        }
    }

    private String createFeed(List<SocialActivitiesEvent> eventTimeline,
                              String url) {
        return AtomSocialTimelineConverter.generate(eventTimeline,
                                                    url);
    }
}
