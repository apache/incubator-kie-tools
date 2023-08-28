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
package org.dashbuilder.patternfly.date;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DatePicker {

    @Inject
    View view;

    public interface View extends UberElemental<DatePicker> {

    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void addValueChangeHandler(Runnable action) {
        // TODO Auto-generated method stub

    }

    public void addBlurHandler(Runnable action) {
        // TODO Auto-generated method stub

    }

    public void addShowHandler(Runnable action) {
        // TODO Auto-generated method stub

    }

    public void addHideHandler(Runnable action) {
        // TODO Auto-generated method stub
        
    }

    public void setValue(Date value) {
        // TODO Auto-generated method stub
        
    }

    public Date getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

}
