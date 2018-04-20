/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.sections;

import java.util.function.Supplier;

import javax.enterprise.event.Event;

import elemental2.promise.Promise;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.uberfire.client.promise.Promises;

public abstract class Section<T> {

    protected final Promises promises;
    private final Event<SettingsSectionChange<T>> settingsSectionChangeEvent;
    private final MenuItem<T> menuItem;

    protected Section(final Event<SettingsSectionChange<T>> settingsSectionChangeEvent,
                      final MenuItem<T> menuItem,
                      final Promises promises) {

        this.promises = promises;
        this.settingsSectionChangeEvent = settingsSectionChangeEvent;
        this.menuItem = menuItem;
    }

    public abstract SectionView<?> getView();

    public abstract int currentHashCode();

    public void setDirty(final boolean dirty) {
        menuItem.markAsDirty(dirty);
    }

    public void fireChangeEvent() {
        settingsSectionChangeEvent.fire(new SettingsSectionChange<>(this));
    }

    public MenuItem<T> getMenuItem() {
        return menuItem;
    }

    public void setActive() {
        menuItem.setActive();
    }
    //Lifecycle

    public Promise<Void> save(final String comment, final Supplier<Promise<Void>> chain) {
        return promises.resolve();
    }

    public Promise<Object> validate() {
        return promises.resolve();
    }

    public Promise<Void> setup(final T model) {
        return promises.resolve();
    }

}
