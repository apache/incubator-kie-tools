/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.headfoot;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.api.ActivatedBy;
import org.uberfire.client.workbench.Header;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
@ActivatedBy( HeaderFooterActivator.class )
public class BottomHeader implements Header {

    private final Label label = new Label( "This is the bottom header (order=5)" );

    @Override
    public String getId() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public Widget asWidget() {
        return label;
    }

}
