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

package org.kie.workbench.common.forms.jbpm.client.document;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentData;
import org.kie.workbench.common.forms.dynamic.model.document.DocumentStatus;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;

@Templated
public class DocumentFieldRendererViewImpl extends Composite implements DocumentFieldRendererView {

    public final String SIZE_UNITS[] = new String[]{"bytes", "Kb", "Mb"};

    @Inject
    @DataField
    protected FlowPanel linkContainer;


    @DataField
    protected DivElement inputContainer = Document.get().createDivElement();

    @DataField
    protected Form documentForm = new Form();

    private FileUploadFormEncoder formEncoder = new FileUploadFormEncoder();

    protected FileUpload uploader;

    protected DocumentData value;

    protected DocumentFieldRenderer renderer;

    @PostConstruct
    protected void init() {
        initForm();
    }

    protected void initForm() {
        documentForm.clear();
         uploader = new FileUpload( () -> {
             if (uploader.getFilename() != null && !uploader.getFilename().isEmpty() ) {
                 documentForm.submit();
             }
         } );
        uploader.setName( "document" );
        documentForm.setEncoding( FormPanel.ENCODING_MULTIPART );
        documentForm.setMethod( FormPanel.METHOD_POST );
        documentForm.setAction( "FormsDocumentServlet" );
        formEncoder.addUtf8Charset( documentForm );
        documentForm.add( uploader );
        documentForm.addSubmitCompleteHandler( event -> {
            onSubmit( event.getResults() );
        });
    }

    @Override
    public void setRenderer( DocumentFieldRenderer renderer ) {
        this.renderer = renderer;
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        if ( readOnly ) {
            inputContainer.getStyle().setDisplay( Style.Display.NONE );
        } else {
            inputContainer.getStyle().setDisplay( Style.Display.BLOCK );
        }
    }

    @Override
    public void setValue( DocumentData value ) {
        setValue( value, false );
    }

    @Override
    public void setValue( DocumentData value, boolean fireEvents ) {
        if ( this.value == value ) {
            return;
        }
        if ( value == null || value.getStatus().equals( DocumentStatus.NEW ) || !value.equals( this.value )) {

            this.value = value;

            linkContainer.clear();
            if ( this.value != null ) {

                Anchor link = new Anchor();
                link.setText( value.getFileName() + " (" + calculateSize() + ")" );

                if ( value.getLink() == null ) {
                    link.setEnabled( false );
                    link.setHref( "#" );
                    link.addClickHandler( event -> {
                        return;
                    } );
                } else {
                    link.setHref( value.getLink() );
                    link.setTarget( "_blank" );
                }
                linkContainer.add( link );
            }

            if ( fireEvents ) {
                ValueChangeEvent.fire( this, this.value );
            }
        }
    }

    protected String calculateSize() {
        String result = "";

        if ( value != null ) {
            double size = value.getSize();
            int position;
            for (position = 0; position < SIZE_UNITS.length && size > 1024; position++) {
                size = size / 1024;
            }

            result = NumberFormat.getDecimalFormat().format( size ) + " " + SIZE_UNITS[position];
        }

        return result;
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<DocumentData> valueChangeHandler ) {
        return this.addHandler( valueChangeHandler, ValueChangeEvent.getType() );
    }

    @Override
    public DocumentData getValue() {
        return value;
    }

    public void onSubmit( String results ) {
        initForm();
        JavaScriptObject jsResponse = JsonUtils.safeEval( results );

        if ( jsResponse != null ) {
            JSONObject response = new JSONObject( jsResponse );
            if ( response.get( "document" ) != null ) {
                JSONObject document = response.get( "document" ).isObject();

                DocumentData data = new DocumentData( document.get( "fileName" ).isString().stringValue(),
                                                      new Double(document.get( "size" ).isNumber().doubleValue()).longValue(),
                                                      null );

                data.setContentId( document.get( "contentId" ).isString().stringValue() );

                setValue( data, true );

            } else if (response.get( "error" ).isNull() != null ) {
                setValue( null, true );
            }
        }
    }
}
