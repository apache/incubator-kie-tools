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

package org.drools.guvnor.client.perspective.workspace;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import org.drools.guvnor.client.common.content.multi.ContentActivityMapper;
import org.drools.guvnor.client.content.AdminArea2Presenter;
import org.drools.guvnor.client.content.AdminAreaPresenter;
import org.drools.guvnor.client.content.AdminPlace;
import org.drools.guvnor.client.content.AdminPlace2;
import org.drools.guvnor.client.content.editor.TextEditorPlace;
import org.drools.guvnor.client.content.editor.TextEditorPresenter;
import org.drools.guvnor.client.ui.EditorInput;
import org.drools.guvnor.client.ui.part.Editor;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
public class WorskpaceActivityMapper implements ContentActivityMapper {

    @Inject private IOCBeanManager manager;

    private Activity actual = null;

    public Activity getActivity(final Place place) {
        if (place instanceof AdminPlace) {
            this.actual = manager.lookupBean(AdminAreaPresenter.class).getInstance();
        }
        if (place instanceof AdminPlace2) {
            this.actual = manager.lookupBean(AdminArea2Presenter.class).getInstance();
        }
        if (place instanceof TextEditorPlace) {
            final TextEditorPresenter presenter = manager.lookupBean(TextEditorPresenter.class).getInstance();
//            presenter.setArtifactId(((TextEditorPlace) place).getFileName());
            this.actual = presenter;
        }
        if (actual instanceof Editor) {
            ((Editor) actual).init(new EditorInput() {
                @Override public String getId() {
                    return ((TextEditorPlace) place).getFileName();
                }

                @Override public String getName() {
                    return ((TextEditorPlace) place).getFileName();
                }
            });
        }
        return actual;
    }
}
