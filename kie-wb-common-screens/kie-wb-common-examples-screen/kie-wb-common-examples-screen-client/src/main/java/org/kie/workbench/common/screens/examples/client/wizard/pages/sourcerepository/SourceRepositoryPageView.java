/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.sourcerepository;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.uberfire.client.mvp.UberView;

public interface SourceRepositoryPageView extends UberView<SourceRepositoryPage> {

    void initialise();

    void setPlaceHolder(final String placeHolder);

    void setUrlGroupType(final ValidationState state);

    void showUrlHelpMessage(final String message);

    void hideUrlHelpMessage();

    void setStockRepositoryOption();

    void setCustomRepositoryOption();

    void disableStockRepositoryOption();

    void showRepositoryUrlInputForm();

    void hideRepositoryUrlInputForm();

    String getCustomRepositoryValue();

    void setCustomRepositoryValue(final String value);

    interface Presenter {

        void setPlaygroundRepository(final ExampleRepository repository);

        void playgroundRepositorySelected();

        void onCustomRepositorySelected();

        void onCustomRepositoryValueChanged();
    }
}
