package org.gwtbootstrap3.extras.respond.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2014 GwtBootstrap3
 * %%
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
 * #L%
 */

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window;

/**
 * @author Joshua Godi
 */
public class RespondEntryPoint implements EntryPoint {
    private static final String MSIE = "MSIE";
    private static final String EIGHT = "8.0";

    @Override
    public void onModuleLoad() {
        if (Window.Navigator.getUserAgent().contains(MSIE) && Window.Navigator.getUserAgent().contains(EIGHT)) {
            ScriptInjector.fromString(RespondClientBundle.INSTANCE.respond().getText()).setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
            ScriptInjector.fromString(RespondClientBundle.INSTANCE.html5Shiv().getText()).setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }
}
