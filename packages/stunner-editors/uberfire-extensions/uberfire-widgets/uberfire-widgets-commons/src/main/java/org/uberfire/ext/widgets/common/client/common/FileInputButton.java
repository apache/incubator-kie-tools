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

package org.uberfire.ext.widgets.common.client.common;

import jsinterop.annotations.JsConstructor;
import org.gwtproject.core.client.JavaScriptObject;
import org.gwtproject.core.client.JsArray;
import org.gwtproject.dom.client.Element;
import org.gwtproject.event.dom.client.ChangeEvent;
import org.gwtproject.event.dom.client.ChangeHandler;
import org.gwtproject.event.logical.shared.HasValueChangeHandlers;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.FileUpload;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Span;
import org.uberfire.ext.widgets.common.client.resources.CommonCss;
import org.uberfire.ext.widgets.common.client.resources.CommonResources;

public class FileInputButton extends Composite implements HasValueChangeHandlers<JsArray<FileInputButton.UploadFile>> {

    protected static final CommonCss css = CommonResources.INSTANCE.CSS();

    private Span wrapper;
    private Span textSpan;
    private FileUpload upload;
    private Icon icon;

    @JsConstructor
    public FileInputButton() {
        wrapper = new Span();
        wrapper.addStyleName(Styles.BTN);
        upload = new FileUpload();

        upload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                fireChanged();
            }
        });

        wrapper.add(upload);
        wrapper.addStyleName("btn-file");
        initWidget(wrapper);
    }

    public void setMultiple(boolean multiple) {
        if (multiple) {
            upload.getElement().setPropertyBoolean("multiple",
                                                   true);
        }
    }

    public void setType(final ButtonType additionalStyle) {
        if (additionalStyle != null) {
            addStyleName(additionalStyle.getCssName());
        }
    }

    @Override
    public void addStyleName(final String additionalStyle) {
        if (additionalStyle != null) {
            wrapper.addStyleName(additionalStyle);
        }
    }

    public String getText() {
        if (textSpan != null) {
            return textSpan.getText();
        }
        return "";
    }

    public void setText(final String text) {
        if (textSpan == null) {
            textSpan = new Span(text);
            wrapper.add(textSpan);
        } else {
            textSpan.setText(text);
        }
    }

    public void setIcon(final IconType type) {
        if (icon == null) {
            icon = new Icon(type);
            wrapper.add(icon);
        } else {
            icon.setType(type);
        }
    }

    private void fireChanged() {
        ValueChangeEvent.fire(this,
                              getFiles(upload.getElement()));
    }

    private JsArray<UploadFile> getFiles(Element el) {
        throw new Error(getClass().getCanonicalName()+".getFiles");

    }/*-{
        if (el.files) {
            return el.files;
        } else {
            return null;
        }
    }-*/;

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<JsArray<UploadFile>> handler) {
        return super.addHandler(handler,
                                ValueChangeEvent.getType());
    }

    public static class UploadFile extends JavaScriptObject {

        @JsConstructor
        protected UploadFile() {
        }

        public final int getSize() {
            throw new Error(getClass().getCanonicalName()+".getSize");

        }/*-{
            return this.fileSize != null ? this.fileSize : this.size;
        }-*/;

        public final String getName() {
            throw new Error(getClass().getCanonicalName()+".getName");

        }/*-{
            return this.fileName != null ? this.fileName : this.name;
        }-*/;

        public final String getType() {
            throw new Error(getClass().getCanonicalName()+".getType");

        }/*-{
            return this.type;
        }-*/;
    }
}
