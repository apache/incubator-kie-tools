/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.editors.factmodel;

import java.util.Collection;
import java.util.List;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.shared.common.vo.assets.factmodel.AnnotationMetaModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A pop-up editor for a single Fact annotation
 */
public class AnnotationEditorPopup {

    // A valid Annotation name
    private static final RegExp             VALID_NAME = RegExp.compile( "^[a-zA-Z][a-zA-Z\\d]*$" );

    private final AnnotationMetaModel       annotation;
    private final List<AnnotationMetaModel> annotations;

    private final TextBox                   txtName    = new TextBox();
    private final TextBox                   txtKey     = new TextBox();
    private final TextBox                   txtValue   = new TextBox();

    private Command                         okCommand;

    public AnnotationEditorPopup(List<AnnotationMetaModel> annotations) {
        this( new AnnotationMetaModel(),
              annotations );
    }

    public AnnotationEditorPopup(AnnotationMetaModel annotation,
                                 List<AnnotationMetaModel> annotations) {
        this.annotation = annotation;
        this.annotations = annotations;
    }

    public AnnotationMetaModel getAnnotation() {
        return annotation;
    }

    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void show() {

        final FormStylePopup pop = new FormStylePopup();

        VerticalPanel vp = new VerticalPanel();

        Grid g = new Grid( 2,
                           3 );

        txtName.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        txtKey.addKeyPressHandler( new NoSpaceKeyPressHandler() );
        g.setWidget( 0,
                     0,
                     new HTML( "<b>Name</b>" ) );
        g.setWidget( 1,
                     0,
                     txtName );

        g.setWidget( 0,
                     1,
                     new HTML( "<b>Key</b>" ) );
        g.setWidget( 1,
                     1,
                     txtKey );

        g.setWidget( 0,
                     2,
                     new HTML( "<b>Value</b>" ) );
        g.setWidget( 1,
                     2,
                     txtValue );

        setControlValues( annotation );

        Button btnOK = new Button( Constants.INSTANCE.OK() );

        btnOK.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                String name = txtName.getText();
                if ( !isNameValid( name ) ) {
                    Window.alert( Constants.INSTANCE.InvalidModelName( name ) );
                    return;
                }
                if ( doesTheNameExist( name ) ) {
                    Window.alert( Constants.INSTANCE.NameTakenForModel( name ) );
                    return;
                }
                if ( annotationAlreadyHasAName() && annotationNameHasChanged( name ) ) {
                    if ( isTheUserSureHeWantsToChangeTheName() ) {
                        setNameAndClose();
                    }
                } else {
                    setNameAndClose();
                }
            }

            private boolean isNameValid(String name) {
                if ( name == null || "".equals( name ) ) {
                    return false;
                }
                return VALID_NAME.test( name );
            }

            private boolean annotationAlreadyHasAName() {
                return annotation.name != null && annotation.name.length() > 0;
            }

            private boolean annotationNameHasChanged(String name) {
                return !name.equals( annotation.name );
            }

            private void setNameAndClose() {
                String name = txtName.getText();
                String key = txtKey.getText();
                if ( key == null || key.length() == 0 ) {
                    //This is the default annotation key constructed by AnnotationDescr when none is provided
                    //e.g. @smurf( Pupa ) -> @smurf( value = Pupa ). We explicitly set it to keep the user
                    //experience consistent between what they enter and what is parsed.
                    key = "value";
                }
                String value = txtValue.getText();

                annotation.name = name;
                annotation.getValues().clear();
                annotation.getValues().put( key,
                                            value );

                okCommand.execute();

                pop.hide();
            }

            private boolean isTheUserSureHeWantsToChangeTheName() {
                return Window.confirm( Constants.INSTANCE.ModelNameChangeWarning() );
            }

            private boolean doesTheNameExist(String name) {
                for ( AnnotationMetaModel a : annotations ) {
                    if ( a != annotation ) {
                        if ( a.name.equals( name ) ) {
                            return true;
                        }
                    }
                }
                return false;
            }
        } );

        vp.add( g );
        vp.add( btnOK );
        pop.addRow( vp );

        pop.show();
    }

    //This is a simplified annotation editor and thus we only
    //allow creation of a single key-value pair. The underlying
    //parser implementation allows for multiple key-value pairs
    //for the same annotation but it's implementation is incomplete.
    private void setControlValues(AnnotationMetaModel annotation) {
        String name = (annotation.name == null ? "" : annotation.name);
        String key = getFirstEntry( annotation.getValues().keySet() );
        String value = getFirstEntry( annotation.getValues().values() );
        txtName.setText( name );
        txtKey.setText( key );
        txtValue.setText( value );
    }

    private String getFirstEntry(Collection<String> values) {
        if ( values.isEmpty() ) {
            return "";
        }
        return values.iterator().next();
    }

}
