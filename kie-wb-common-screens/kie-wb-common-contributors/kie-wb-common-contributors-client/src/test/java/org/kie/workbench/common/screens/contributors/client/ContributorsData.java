/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.contributors.client;

import java.util.Date;

import org.dashbuilder.dataset.RawDataSet;

import static org.kie.workbench.common.screens.contributors.model.ContributorsDataSetColumns.*;

public class ContributorsData extends RawDataSet {

    public static final ContributorsData INSTANCE = new ContributorsData(
            new String[] {COLUMN_ORG, COLUMN_REPO, COLUMN_AUTHOR, COLUMN_DATE, COLUMN_MSG},
            new Class[] {String.class, String.class, String.class, Date.class, String.class}, new String[][] {
            {"org1", "repo1", "user1", "01/01/19 12:00", "Commit 1"},
            {"org1", "repo1", "user2", "03/02/19 12:00", "Commit 2"},
            {"org1", "repo2", "user3", "04/03/19 12:00", "Commit 3"},
            {"org1", "repo2", "user4", "06/04/19 12:00", "Commit 4"},
            {"org2", "repo3", "user5", "07/05/19 12:00", "Commit 5"},
            {"org2", "repo3", "user6", "09/06/19 12:00", "Commit 6"},
            {"org2", "repo4", "user7", "11/07/19 12:00", "Commit 7"},
            {"org2", "repo4", "user8", "02/08/20 12:00", "Commit 8"},
            {"emptyOrg", null, null, null, null}});

    public ContributorsData(String[] columnIds, Class[] types, String[][] data) {
        super(columnIds, types, data);
    }

}
