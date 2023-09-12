/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.controls.popover;

import java.util.Optional;
import java.util.function.Consumer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;

public abstract class AbstractPopoverImpl<V extends PopoverView, B> implements HasCellEditorControls.Editor<B> {

    protected V view;
    protected Optional<B> binding = Optional.empty();

    protected AbstractPopoverImpl() {
        //CDI proxy
    }

    protected AbstractPopoverImpl(final V view) {
        this.view = view;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void bind(final B bound,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(bound);
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        binding.ifPresent(b -> view.setOnClosedByKeyboardCallback(callback));
    }

    @Override
    public void show() {
        binding.ifPresent(b -> view.show(Optional.ofNullable(getPopoverTitle())));
    }

    @Override
    public void hide() {
        binding.ifPresent(b -> view.hide());
    }
}
