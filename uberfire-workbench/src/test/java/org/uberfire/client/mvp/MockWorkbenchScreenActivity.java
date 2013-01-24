/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.mvp;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Mock WorkbenchScreenActivity
 */
public class MockWorkbenchScreenActivity extends AbstractWorkbenchScreenActivity {

    public MockWorkbenchScreenActivity(final PlaceManager placeManager) {
        super( placeManager );
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSignatureId() {
        return null;
    }

    @Override
    public Collection<String> getRoles() {
        return null;
    }

    @Override
    public Collection<String> getTraits() {
        return null;
    }

    @Override
    public IsWidget getWidget() {
        return null;
    }

}
