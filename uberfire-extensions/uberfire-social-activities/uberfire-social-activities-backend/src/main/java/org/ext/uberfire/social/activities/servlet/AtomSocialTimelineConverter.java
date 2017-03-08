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

package org.ext.uberfire.social.activities.servlet;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;

public class AtomSocialTimelineConverter {

    public static String generate(List<SocialActivitiesEvent> eventTimeline,
                                  String type) {
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();

        feed.setId("tag:org.uberfire,2014:/" + type);
        feed.setTitle("Social Activities Feed");
        feed.setUpdated(new Date());
        feed.addAuthor("Red Hat JBoss");

        for (SocialActivitiesEvent event : eventTimeline) {
            Entry entry = feed.addEntry();
            entry.setTitle(event.getType());
            entry.setSummary(event.getSocialUser().getUserName() + "  " + event.toString());
            entry.setUpdated(event.getTimestamp());
            entry.setPublished(event.getTimestamp());
        }
        return feed.toString();
    }
}
