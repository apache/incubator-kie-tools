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

package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface GuvnorImages
        extends
        ClientBundle {

    GuvnorImages INSTANCE = GWT.create( GuvnorImages.class );

    @Source("images/monitoring.png")
    ImageResource monitoringScreenshot();

    @Source("images/hdrlogo_drools.gif")
    ImageResource hdrlogoDrools();

    @Source("images/edit.gif")
    ImageResource edit();

    @Source("images/analyze.gif")
    ImageResource analyze();

    @Source("images/information.gif")
    ImageResource information();

    @Source("images/config.png")
    ImageResource config();

    @Source("images/collapse.gif")
    ImageResource collapse();

    @Source("images/collapseall.gif")
    ImageResource collapseAll();

    @Source("images/expand.gif")
    ImageResource expand();

    @Source("images/expandall.gif")
    ImageResource expandAll();

    @Source("images/close.gif")
    ImageResource close();

    @Source("images/new_item.gif")
    ImageResource newItem();

    @Source("images/new_item_disabled.gif")
    ImageResource newItemDisabled();

    @Source("images/delete_item_small.gif")
    ImageResource deleteItemSmall();

    @Source("images/shuffle_down.gif")
    ImageResource shuffleDown();

    @Source("images/shuffle_up.gif")
    ImageResource shuffleUp();
    
    @Source("images/backup_large.png")
    ImageResource backupLarge();
    
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("images/jbossrulesBlue.gif")
    ImageResource jbossrulesBlue();

}
