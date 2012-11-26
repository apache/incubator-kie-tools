/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.perspectives;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.client.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.client.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;
import org.uberfire.shared.security.AppRoles;
import org.uberfire.shared.security.ShowcaseRoles;

@ApplicationScoped
@WorkbenchPerspective(identifier = "dashboardPerspective")
@ShowcaseRoles(value = {AppRoles.DIRECTOR, AppRoles.MANAGER})
public class DashboardPerspective {

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName("Dashboard");

        p.getRoot().addPart(new PartDefinitionImpl(new DefaultPlaceRequest("Chart")));

        final PanelDefinition south = new PanelDefinitionImpl();
        south.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("StockQuotesGadget")));
        south.setHeight(340);
        south.setWidth(370);

        final PanelDefinition seast = new PanelDefinitionImpl();
        seast.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("WeatherGadget")));
        seast.setHeight(340);
        seast.setWidth(570);

        final PanelDefinition seast2 = new PanelDefinitionImpl();
        seast2.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("SportsNewsGadget")));
        seast2.setHeight(340);
        seast2.setWidth(520);

        seast.insertChild(Position.EAST, seast2);
        south.insertChild(Position.EAST, seast);

        final PanelDefinition east = new PanelDefinitionImpl();
        east.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("TodoListScreen")));
        east.setHeight(330);
        east.setWidth(700);

        final PanelDefinition eeast = new PanelDefinitionImpl();
        eeast.addPart(new PartDefinitionImpl(new DefaultPlaceRequest("IPInfoGadget")));
        eeast.setHeight(330);
        eeast.setWidth(380);

        east.insertChild(Position.EAST, eeast);

        p.getRoot().insertChild(Position.SOUTH, south);
        p.getRoot().insertChild(Position.EAST, east);

        return p;
    }
}
