/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client.widgets;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class FilterLabelSet implements IsElement {

    public interface View extends UberElement<FilterLabelSet> {

        void clearAll();

        void setClearAllEnabled(boolean enabled);

        void addLabel(FilterLabel label);
    }

    private View view;
    private SyncBeanManager beanManager;
    private Command onClearAllCommand;
    private int numberOfLabels = 0;

    @Inject
    public FilterLabelSet(View view, SyncBeanManager beanManager) {
        this.view = view;
        this.beanManager = beanManager;
        this.view.init(this);
        this.view.setClearAllEnabled(false);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void clear() {
        view.clearAll();
        view.setClearAllEnabled(false);
        numberOfLabels = 0;
    }

    public FilterLabel addLabel(String label) {
        FilterLabel filterLabel = beanManager.lookupBean(FilterLabel.class).newInstance();
        filterLabel.setLabel(label);
        view.addLabel(filterLabel);
        numberOfLabels++;
        view.setClearAllEnabled(numberOfLabels>1);
        return filterLabel;
    }

    public void setOnClearAllCommand(Command onClearAllCommand) {
        this.onClearAllCommand = onClearAllCommand;
    }

    void onClearAll() {
        this.clear();
        if (onClearAllCommand != null) {
            onClearAllCommand.execute();
        }
    }
}
