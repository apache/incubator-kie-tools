/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.View;
import org.kie.workbench.common.screens.explorer.service.Option;

/**
 * Repository, Package, Folder and File explorer
 */
@ApplicationScoped
public class TechnicalViewPresenterImpl extends BaseViewPresenter {

    @Inject
    protected TechnicalViewWidget view;

    private Set<Option> options = new HashSet<Option>( Arrays.asList( Option.TECHNICAL_CONTENT, Option.BREADCRUMB_NAVIGATOR, Option.EXCLUDE_HIDDEN_ITEMS ) );

    @Override
    protected void setOptions( Set<Option> options ) {
        this.options = new HashSet<Option>( options  ) ;
    }

    @Override
    public Set<Option> getActiveOptions() {
        return options;
    }

    @Override
    protected View getView() {
        return view;
    }
}
