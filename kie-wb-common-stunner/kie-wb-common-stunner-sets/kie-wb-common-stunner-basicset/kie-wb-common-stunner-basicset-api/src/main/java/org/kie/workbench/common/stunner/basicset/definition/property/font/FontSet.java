/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.basicset.definition.property.font;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Name;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

import javax.validation.Valid;

@Portable
@Bindable
@PropertySet
public class FontSet {

    @Name
    public static final transient String propertySetName = "Font";

    @Property
    @FieldDef( label = FIELDDEF_FONT_FAMILY, property = "value" )
    @Valid
    private FontFamily fontFamily;

    @Property
    @FieldDef( label = FIELDDEF_FONT_COLOR, property = "value" )
    @Valid
    private FontColor fontColor;

    @Property
    @FieldDef( label = FIELDDEF_FONT_SIZE, property = "value" )
    @Valid
    private FontSize fontSize;

    @Property
    @FieldDef( label = FIELDDEF_FONT_BORDER_SIZE, property = "value" )
    @Valid
    private FontBorderSize fontBorderSize;

    public FontSet() {
        this( new FontFamily(),
                new FontColor(),
                new FontSize(),
                new FontBorderSize() );
    }

    public FontSet( @MapsTo( "fontFamily" ) FontFamily fontFamily,
                    @MapsTo( "fontColor" ) FontColor fontColor,
                    @MapsTo( "fontSize" ) FontSize fontSize,
                    @MapsTo( "fontBorderSize" ) FontBorderSize fontBorderSize ) {
        this.fontFamily = fontFamily;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.fontBorderSize = fontBorderSize;
    }

    public FontSet( final String fontFamily,
                    final String fontColor,
                    final Double fontSize,
                    final Double fontBorderSize ) {
        this.fontFamily = new FontFamily( fontFamily );
        this.fontColor = new FontColor( fontColor );
        this.fontSize = new FontSize( fontSize );
        this.fontBorderSize = new FontBorderSize( fontBorderSize );
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    public FontFamily getFontFamily() {
        return fontFamily;
    }

    public FontColor getFontColor() {
        return fontColor;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public FontBorderSize getFontBorderSize() {
        return fontBorderSize;
    }

    public void setFontFamily( FontFamily fontFamily ) {
        this.fontFamily = fontFamily;
    }

    public void setFontColor( FontColor fontColor ) {
        this.fontColor = fontColor;
    }

    public void setFontSize( FontSize fontSize ) {
        this.fontSize = fontSize;
    }

    public void setFontBorderSize( FontBorderSize fontBorderSize ) {
        this.fontBorderSize = fontBorderSize;
    }
}
