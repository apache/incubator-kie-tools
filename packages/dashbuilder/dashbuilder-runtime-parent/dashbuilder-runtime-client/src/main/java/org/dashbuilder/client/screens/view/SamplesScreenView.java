/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.screens.view;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.dashbuilder.client.screens.SamplesScreen;
import org.dashbuilder.client.widgets.SamplesCardRow;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class SamplesScreenView implements SamplesScreen.View {

    private static final String ACTIVE_CLASS = "pf-m-current";

    private static final double MARGIN_SCROLL_COMPENSATION = 70;

    @Inject
    @DataField
    HTMLDivElement samplesScreenRoot;

    @Inject
    @DataField
    HTMLDivElement rowsContainer;

    @Inject
    @DataField
    HTMLDivElement samplesSideBar;

    @Inject
    @DataField
    @Named("ul")
    HTMLElement samplesCategoriesGroup;

    @Inject
    @DataField
    HTMLButtonElement toggleCategoriesNavigation;

    @Override
    public HTMLElement getElement() {
        return samplesScreenRoot;
    }

    @Override
    public void init(SamplesScreen presenter) {
        registerScrollListener();
    }

    @Override
    public void addRows(List<SamplesCardRow> rows) {
        rows.forEach(r -> {
            appendCategory(r.getCategory());
            rowsContainer.appendChild(r.getElement());
        });
    }

    @Override
    public void clear() {
        rowsContainer.innerHTML = "";
        samplesCategoriesGroup.innerHTML = "";
    }

    private void appendCategory(String category) {
        var li = DomGlobal.document.createElement("li");
        var a = (HTMLAnchorElement) DomGlobal.document.createElement("a");
        li.classList.add("pf-v5-c-nav__item");
        a.classList.add("pf-v5-c-nav__link");
        a.textContent = category;
        a.title = category;
        a.href = "#" + SamplesCardRow.produceCategoryTitleId(category);
        a.onclick = e -> {
            Scheduler.get().scheduleDeferred(() -> {
                clearActiveNavItems();
                a.classList.add(ACTIVE_CLASS);
            });
            return e;
        };
        li.appendChild(a);
        samplesCategoriesGroup.appendChild(li);
    }

    @EventHandler("toggleCategoriesNavigation")
    void toggleCategoriesNavigation(ClickEvent e) {
        samplesSideBar.classList.toggle("pf-m-collapsed");
    }

    private void registerScrollListener() {
        rowsContainer.addEventListener("scroll", e -> {
            var yPos = rowsContainer.scrollTop;
            var navLinks = samplesCategoriesGroup.querySelectorAll("li a");
            navLinks.forEach((item, i, listObj) -> {
                var a = (HTMLAnchorElement) item;
                var catId = a.title;
                var section = (HTMLElement) DomGlobal.document.getElementById(catId);
                var bounds = section.parentElement.getBoundingClientRect();
                var minY = section.offsetTop - MARGIN_SCROLL_COMPENSATION;
                var maxY = minY + bounds.height;

                if (yPos >= minY && yPos < maxY) {
                    a.classList.add(ACTIVE_CLASS);
                } else {
                    a.classList.remove(ACTIVE_CLASS);
                }
                return item;
            });
        });

    }

    private void clearActiveNavItems() {
        var navLinks = samplesCategoriesGroup.querySelectorAll("li > a");
        navLinks.forEach((item, i, listObj) -> {
            item.classList.remove(ACTIVE_CLASS);
            return item;
        });
    }

}
