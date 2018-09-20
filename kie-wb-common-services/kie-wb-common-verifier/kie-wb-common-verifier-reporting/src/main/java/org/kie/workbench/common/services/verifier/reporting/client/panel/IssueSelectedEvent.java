/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.verifier.reporting.client.panel;

import org.drools.verifier.api.reporting.Issue;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.PlaceRequest;

/**
 * An event for when an {@link Issue} is selected from the Analysis Report.
 */
public class IssueSelectedEvent {

    private final PlaceRequest placeRequest;
    private final Issue issue;

    /**
     * Constructor
     * @param placeRequest The {@link PlaceRequest} for the Decision Table represented by the Analysis Report.
     * @param issue The {@link Issue} selected from the Analysis Report. Cannot be null.
     */
    public IssueSelectedEvent(final PlaceRequest placeRequest,
                              final Issue issue) {
        this.placeRequest = PortablePreconditions.checkNotNull("placeRequest",
                                                               placeRequest);
        this.issue = PortablePreconditions.checkNotNull("issue",
                                                        issue);
    }

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public Issue getIssue() {
        return issue;
    }
}
