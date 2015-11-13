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

package org.uberfire.ext.plugin.client.widget.media;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Caption;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.ThumbnailPanel;
import org.gwtbootstrap3.client.ui.base.form.AbstractForm;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.ImageType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.plugin.client.config.PluginConfigService;
import org.uberfire.ext.plugin.event.MediaAdded;
import org.uberfire.ext.plugin.event.MediaDeleted;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.ext.widgets.common.client.common.FileUpload;
import org.uberfire.ext.widgets.common.client.common.FileUploadFormEncoder;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class MediaLibraryWidget extends Composite implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, MediaLibraryWidget> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private FileUploadFormEncoder formEncoder = new FileUploadFormEncoder();

    @UiField
    FlowPanel content;

    @UiField
    Form form;

    @UiField(provided = true)
    FileUpload fileUpload;

    @UiField
    Row library;

    @Inject
    private PluginConfigService pluginConfigService;

    private String pluginName;
    private ParameterizedCommand<Media> onMediaDelete;

    private Map<Path, IsWidget> mediaRef = new HashMap<Path, IsWidget>();

    @PostConstruct
    public void init() {
        fileUpload = createFileUpload();

        initWidget( uiBinder.createAndBindUi( this ) );

        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        formEncoder.addUtf8Charset( form );

        form.addSubmitHandler( new AbstractForm.SubmitHandler() {
            @Override
            public void onSubmit( final AbstractForm.SubmitEvent event ) {
                final String fileName = fileUpload.getFilename();
                if ( isNullOrEmpty( fileName ) ) {
                    event.cancel();
                }
            }

            private boolean isNullOrEmpty( final String fileName ) {
                return fileName == null || "".equals( fileName );
            }
        } );

        form.addSubmitCompleteHandler( new AbstractForm.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete( final AbstractForm.SubmitCompleteEvent event ) {
                if ( "OK".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( "Upload Success" );
                } else if ( "FAIL".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( "Upload Failed" );
                } else if ( "FAIL - ALREADY EXISTS".equalsIgnoreCase( event.getResults() ) ) {
                    Window.alert( "File already exists" );
                }
            }
        } );
    }

    public void setup( final String pluginName,
                       final Collection<Media> mediaLibrary,
                       final ParameterizedCommand<Media> onMediaDelete ) {
        this.pluginName = pluginName;
        this.onMediaDelete = onMediaDelete;

        this.mediaRef.clear();
        this.library.clear();

        for ( final Media media : mediaLibrary ) {
            addMedia( media );
        }
    }

    private FileUpload createFileUpload() {
        return new FileUpload( new Command() {
            @Override
            public void execute() {
                form.setAction( GWT.getHostPageBaseURL().replaceAll( "/" + GWT.getModuleName(), "" ) + pluginConfigService.getMediaServletURI() + pluginName );
                form.submit();
            }
        }, true );
    }

    @Override
    public void onResize() {
        getParent().getElement().getStyle().setBackgroundColor( "#F6F6F6" );
        content.getElement().getStyle().setTop( 60, Style.Unit.PX );
    }

    public void onNewMedia( @Observes final MediaAdded mediaAddedEvent ) {
        if ( mediaAddedEvent.getPluginName().equals( pluginName ) ) {
            addMedia( mediaAddedEvent.getMedia() );
        }
    }

    public void onMediaDelete( @Observes final MediaDeleted mediaDeleted ) {
        if ( mediaDeleted.getPluginName().equals( pluginName ) ) {
            final IsWidget thumb = mediaRef.get( mediaDeleted.getMedia().getPath() );
            if ( thumb != null ) {
                library.remove( thumb );
            }
        }
    }

    public void addMedia( final Media media ) {

        final Column column = new Column( ColumnSize.XS_4 );

        final Button trash = new Button();
        trash.setIcon( IconType.TRASH );

        final ThumbnailPanel thumbnail = new ThumbnailPanel() {{
            add( new Image( media.getPreviewURI() ) {{
                setType( ImageType.CIRCLE );
                setHeight( "140px" );
                setWidth( "140px" );
            }} );
            add( new Caption() {{
                add( new Paragraph( media.getExternalURI() ) {{
                    getElement().getStyle().setProperty( "maxWidth", "180px" );
                }} );
                add( new Paragraph() {{
                    add( trash );
                }} );
            }} );
        }};

        trash.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                mediaRef.remove( media.getPath() );
                onMediaDelete.execute( media );
                library.remove( column );
            }
        } );

        column.add( thumbnail );

        mediaRef.put( media.getPath(), column );
    }
}