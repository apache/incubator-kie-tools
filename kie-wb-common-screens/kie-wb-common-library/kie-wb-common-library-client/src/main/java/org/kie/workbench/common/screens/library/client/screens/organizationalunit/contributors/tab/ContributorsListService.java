/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import elemental2.promise.Promise;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;

public interface ContributorsListService {

    void getContributors(Consumer<List<Contributor>> contributorsConsumer);

    void saveContributors(List<Contributor> contributors,
                          Runnable successCallback,
                          ErrorCallback<Message> errorCallback);

    Promise<Boolean> canEditContributors(List<Contributor> contributors,
                                         ContributorType type);

    void getValidUsernames(Consumer<List<String>> validUsernamesConsumer);

    void onExternalChange(final Consumer<Collection<Contributor>> contributorsConsumer);
}
