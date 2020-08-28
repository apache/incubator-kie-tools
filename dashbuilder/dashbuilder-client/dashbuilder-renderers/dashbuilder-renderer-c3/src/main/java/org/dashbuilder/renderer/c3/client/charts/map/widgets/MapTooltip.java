/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.renderer.c3.client.charts.map.widgets;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.D3;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;

@Templated
@ApplicationScoped
public class MapTooltip implements IsElement {
    
    @Inject @DataField
    HTMLDivElement mapTooltipContainer;
    
    @Inject 
    @DataField
    @Named("strong")
    HTMLElement lblTooltipTitle;
    
    @Inject 
    @DataField
    @Named("span")
    HTMLElement lblTooltipCategory;
    
    @Inject 
    @DataField
    @Named("strong")
    HTMLElement lblTooltipValue;
    
    private D3 d3 = D3.Builder.get();
    
    @PostConstruct
    public void init() {
        mapTooltipContainer.style.visibility = "hidden";
    }

    @Override
    public HTMLElement getElement() {
        return mapTooltipContainer;
    }
    
    public void show(String title, String category, Optional<Double> data, Function<Double, String> formatter) {
        d3.select(mapTooltipContainer).transition().duration(400).style("opacity", "0.9");
        lblTooltipTitle.textContent = title;
        if (data.isPresent()) {
            lblTooltipValue.style.visibility = "visible";
            lblTooltipCategory.textContent = category + ": ";
            lblTooltipValue.textContent = formatter.apply(data.get());
        } else {
            lblTooltipCategory.textContent = "No data.";
            lblTooltipValue.style.visibility = "hidden";
        }
    }
    
    public void hide() {
        d3.select(mapTooltipContainer).transition().duration(500).style("opacity", "0");
    }
    
    public void move() {
        int x = d3.getEvent().getPageX() + 10;
        int y = d3.getEvent().getPageY() - 40;
        d3.select(mapTooltipContainer).style("left", x + "px").style("top", y + "px");
    }
}