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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.uberfire.client.promise.Promises;

import static java.util.stream.Collectors.toList;

@Dependent
public class SectionManager<T> {

    private final MenuItemsListPresenter<T> menuItemsListPresenter;
    private final Promises promises;
    private final Elemental2DomUtil elemental2DomUtil;

    List<Section<T>> sections;
    private HTMLElement menuItemsContainer;
    private HTMLElement contentContainer;

    Map<Section<T>, Integer> originalHashCodes;
    private Section<T> currentSection;

    @Inject
    public SectionManager(final MenuItemsListPresenter<T> menuItemsListPresenter,
                          final Promises promises,
                          final Elemental2DomUtil elemental2DomUtil) {

        this.menuItemsListPresenter = menuItemsListPresenter;
        this.promises = promises;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    public void init(final List<Section<T>> sections,
                     final HTMLElement menuItemsContainer,
                     final HTMLElement contentContainer) {

        this.sections = new ArrayList<>(sections);
        this.currentSection = sections.get(0);
        this.menuItemsContainer = menuItemsContainer;
        this.contentContainer = contentContainer;
        this.originalHashCodes = new HashMap<>();
        this.currentSection.setActive();
        setupMenuItems();
    }

    private void setupMenuItems() {

        final List<MenuItem<T>> menuItems = sections.stream()
                .peek(section -> section.getMenuItem().setup(section, this))
                .map(Section::getMenuItem).collect(toList());

        menuItemsListPresenter.setupWithPresenters(
                menuItemsContainer,
                menuItems,
                (section, menuItem) -> menuItem.setup(section, this));
    }

    public Promise<Void> goTo(final Section<T> section) {
        currentSection = section;
        elemental2DomUtil.removeAllElementChildren(contentContainer);
        contentContainer.appendChild(section.getView().getElement());
        return promises.resolve();
    }

    public Promise<Void> goToFirstAvailable() {
        return goTo(sections.get(0));
    }

    public Promise<Void> goToCurrentSection() {
        return goTo(currentSection);
    }

    public Promise<Object> validateAll() {
        return promises.reduceLazily(sections, Section::validate);
    }

    public void remove(final Section<T> section) {
        sections.remove(section);
    }

    public Promise<Void> resetAllDirtyIndicators() {
        sections.forEach(this::resetDirtyIndicator);
        return promises.resolve();
    }

    public void resetDirtyIndicator(final Section<T> section) {
        originalHashCodes.put(section, section.currentHashCode());
        updateDirtyIndicator(section);
    }

    public void updateDirtyIndicator(final Section<T> section) {
        section.setDirty(isDirty(section));
    }

    private boolean isDirty(final Section<T> section) {
        return originalHashCodes.containsKey(section) && !originalHashCodes.get(section).equals(section.currentHashCode());
    }

    public boolean manages(final Section<T> section) {
        return sections.contains(section);
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public List<Section<T>> getSections() {
        return sections;
    }

    public Section<T> getCurrentSection() {
        return currentSection;
    }

    public boolean hasDirtySections() {
        return sections.stream().anyMatch(this::isDirty);
    }
}
