/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.common.rendering.client.widgets.picture;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimplePanel;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidget;

@Dependent
public class PictureInput extends SimplePanel implements HasValue<String> {

    private PictureWidget widget;

    @Inject
    public PictureInput( PictureWidget widget ) {
        this.widget = widget;
    }

    @PostConstruct
    protected void init() {
        this.add( widget );
    }

    private String value;

    public void init( int width, int height ) {
        widget.init( width, height, url -> setValue( url, true ) );
    }

    public void setPicture( String url ) {
        setValue( url );
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue( String value ) {
        this.setValue( value, false );
    }

    @Override
    public void setValue( String value, boolean fireEvents ) {
        if ( value == null ) {
            value = "";
        }

        if ( value.equals( getValue() ) ) {
            return;
        }

        this.value = value;

        widget.setPictureUrl( value );

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, value );
        }
    }

    public void setReadOnly( boolean readOnly ) {
        widget.setReadOnly( readOnly );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> valueChangeHandler ) {
        return this.addHandler( valueChangeHandler, ValueChangeEvent.getType() );
    }
}
