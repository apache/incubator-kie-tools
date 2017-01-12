/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.basicset.definition.property.background;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.FIELDDEF_BACKGROUND_COLOR;
import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.FIELDDEF_BORDER_COLOR;
import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.FIELDDEF_BORDER_SIZE;

@Portable
@Bindable
@PropertySet
public class BackgroundAndBorderSet {

    @Name
    public static final transient String propertySetName = "Background And Borders";

    @Property
    @FieldDef( label = FIELDDEF_BACKGROUND_COLOR, property = "value" )
    @Valid
    private BgColor bgColor;

    @Property
    @FieldDef( label = FIELDDEF_BORDER_COLOR, property = "value" )
    @Valid
    private BorderColor borderColor;

    @Property
    @FieldDef( label = FIELDDEF_BORDER_SIZE, property = "value" )
    @Valid
    private BorderSize borderSize;

    public BackgroundAndBorderSet() {
        this( new BgColor(),
              new BorderColor(),
              new BorderSize() );
    }

    public BackgroundAndBorderSet( @MapsTo( "bgColor" ) BgColor bgColor,
                                   @MapsTo( "borderColor" ) BorderColor borderColor,
                                   @MapsTo( "borderSize" ) BorderSize borderSize ) {
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        this.borderSize = borderSize;
    }

    public BackgroundAndBorderSet( final String bgColor,
                                   final String borderColor,
                                   final Double borderSize ) {
        this.bgColor = new BgColor( bgColor );
        this.borderColor = new BorderColor( borderColor );
        this.borderSize = new BorderSize( borderSize );
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public BgColor getBgColor() {
        return bgColor;
    }

    public BorderColor getBorderColor() {
        return borderColor;
    }

    public BorderSize getBorderSize() {
        return borderSize;
    }

    public void setBgColor( BgColor bgColor ) {
        this.bgColor = bgColor;
    }

    public void setBorderColor( BorderColor borderColor ) {
        this.borderColor = borderColor;
    }

    public void setBorderSize( BorderSize borderSize ) {
        this.borderSize = borderSize;
    }
}
