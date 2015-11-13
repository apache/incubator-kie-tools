/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.screens.gadgets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen( identifier = "TwitterGadget" )
@Templated
public class TwitterGadgetScreen extends Composite {

    @PostConstruct
    public void init(){
        final ScriptElement se = Document.get().createScriptElement();
        se.setId( "twitter-wjs" );
        se.setSrc( "http://platform.twitter.com/widgets.js" );
        this.getElement().appendChild( se );
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Uberfire Twitter";
    }

}
