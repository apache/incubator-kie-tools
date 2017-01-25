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

package org.drools.workbench.client.navbar;


import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.Header;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static java.lang.Integer.*;

@ApplicationScoped
@Templated
public class LogoNavBar implements Header {

    @Inject
    private LogoWidgetView logo;

    @Inject
    @DataField
    Div header;

    @AfterInitialization
    public void setup(){
        DOMUtil.appendWidgetToElement( header, logo.asWidget() );
    }

    @Override
    public String getId() {
        return "LogoWidget";
    }

    @Override
    public int getOrder() {
        return MAX_VALUE;
    }

}
