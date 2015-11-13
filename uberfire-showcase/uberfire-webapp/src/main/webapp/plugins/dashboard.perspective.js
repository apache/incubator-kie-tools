/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

$registerPerspective({
    id: "Dashboard",
    roles: [ "director", "manager" ],
    panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
    view: {
        parts: [
            {
                place: "GitHubCommitStats"
            }
        ],
        panels: [
            {
                width: 450,
                position: "west",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "GitHubCommitDaysStats"
                    }
                ]
            },
            {
                width: 380,
                position: "east",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "TwitterGadget"
                    }
                ]
            },
            {
                width: 570,
                height: 340,
                position: "south",
                panel_type: "org.uberfire.client.workbench.panels.impl.ClosableSimpleWorkbenchPanelPresenter",
                parts: [
                    {
                        place: "GitHubFrequencyStats"
                    }
                ]
            }
        ]
    }
});
