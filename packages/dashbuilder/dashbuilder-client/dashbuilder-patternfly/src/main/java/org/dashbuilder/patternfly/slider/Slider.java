/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.patternfly.slider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class Slider {

    @Inject
    View view;

    public interface View extends UberElemental<Slider> {

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        
    }

    public void setShowTooltip(boolean enabled) {
        // TODO Auto-generated method stub
        
    }

    public void setMin(double min) {
        // TODO Auto-generated method stub
        
    }

    public void setMax(double max) {
        // TODO Auto-generated method stub
        
    }

    public void setValue(double minSelected, double maxSelected) {
        // TODO Auto-generated method stub
        
    }

    public void setStep(double step) {
        // TODO Auto-generated method stub
        
    }

}
