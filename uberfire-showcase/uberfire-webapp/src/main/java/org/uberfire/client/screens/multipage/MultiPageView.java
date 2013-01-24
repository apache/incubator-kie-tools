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

package org.uberfire.client.screens.multipage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Button;
import org.uberfire.client.common.MultiPageEditor;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
@Dependent
public class MultiPageView extends MultiPageEditor
        implements
        MultiPagePresenter.View {

    private MultiPagePresenter presenter;

    @Override
    public void init( final MultiPagePresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        for ( int i = 0; i < 10; i++ ) {
            addWidget( new Button( "My Button " + i ), "Page " + i );
        }
    }

}