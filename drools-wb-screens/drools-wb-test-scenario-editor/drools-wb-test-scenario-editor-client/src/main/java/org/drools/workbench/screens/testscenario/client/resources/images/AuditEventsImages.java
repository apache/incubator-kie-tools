/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.testscenario.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AuditEventsImages
        extends
        ClientBundle {

    public static AuditEventsImages INSTANCE = GWT.create(AuditEventsImages.class);

    @Source("audit_events/misc_event.gif")
    ImageResource miscEvent();

    @Source("audit_events/1.gif")
    ImageResource image1();

    @Source("audit_events/2.gif")
    ImageResource image2();

    @Source("audit_events/3.gif")
    ImageResource image3();

    @Source("audit_events/4.gif")
    ImageResource image4();

    @Source("audit_events/5.gif")
    ImageResource image5();

    @Source("audit_events/6.gif")
    ImageResource image6();

    @Source("audit_events/7.gif")
    ImageResource image7();

}
