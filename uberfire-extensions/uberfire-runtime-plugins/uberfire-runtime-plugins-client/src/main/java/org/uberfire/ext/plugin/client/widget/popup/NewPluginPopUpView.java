/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.widget.popup;

import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.plugin.client.validation.RuleValidator;
import org.uberfire.ext.plugin.model.PluginType;

public interface NewPluginPopUpView extends UberView<NewPluginPopUpView.Presenter> {

    interface Presenter {

        void onOK( String name,
                   PluginType type );

        void onCancel();

        RuleValidator getNameValidator();
    }

    void show( final PluginType type );

    void hide();

    String emptyName();

    String invalidName();

    String duplicatedName();

    void handleNameValidationError( String errorMessage );
}
