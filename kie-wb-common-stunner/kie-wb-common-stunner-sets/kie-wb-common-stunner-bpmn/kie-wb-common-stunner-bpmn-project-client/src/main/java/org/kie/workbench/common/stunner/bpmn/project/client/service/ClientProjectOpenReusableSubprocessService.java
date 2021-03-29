/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.client.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.stunner.bpmn.project.service.ProjectOpenReusableSubprocessService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@ApplicationScoped
public class ClientProjectOpenReusableSubprocessService {

    @Inject
    PlaceManager placeManager;

    private final Promises promises;
    private final Caller<ProjectOpenReusableSubprocessService> openReusableSubprocessService;

    protected ClientProjectOpenReusableSubprocessService() {
        this(null, null);
    }

    @Inject
    public ClientProjectOpenReusableSubprocessService(final Promises promises,
                                                      final Caller<ProjectOpenReusableSubprocessService> openReusableSubprocessService) {

        this.promises = promises;
        this.openReusableSubprocessService = openReusableSubprocessService;
    }

    public Promise<List<String>> call(String processId) {
        return promises.promisify(openReusableSubprocessService,
                                  s -> {
                                      s.openReusableSubprocess(processId);
                                  });
    }

    public void openReusableSubprocess(List<String> processData) {
        PlaceRequest placeRequestImpl = new PathPlaceRequest(
                PathFactory.newPathBasedOn(processData.get(0), // "test.bpmn"
                                           processData.get(1), // "default://master@testSpace/ProjectTest/src/main/resources/com/test.bpmn"
                                           new PathFactory.PathImpl()) // not really used
        );

        placeRequestImpl.addParameter("uuid",
                                      processData.get(1));
        placeRequestImpl.addParameter("profile",
                                      "jbpm");
        this.placeManager.goTo(placeRequestImpl);
    }
}
