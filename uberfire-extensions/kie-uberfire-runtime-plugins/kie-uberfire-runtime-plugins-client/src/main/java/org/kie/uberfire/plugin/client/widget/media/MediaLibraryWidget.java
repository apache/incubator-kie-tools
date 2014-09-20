/*
 * Copyright 2012 JBoss Inc
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

package org.kie.uberfire.plugin.client.widget.media;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Caption;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Thumbnail;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.ImageType;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.FileUpload;
import org.kie.uberfire.plugin.client.config.PluginConfigService;
import org.kie.uberfire.plugin.event.MediaAdded;
import org.kie.uberfire.plugin.event.MediaDeleted;
import org.kie.uberfire.plugin.model.Media;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class MediaLibraryWidget extends Composite implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, MediaLibraryWidget> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel content;

    @UiField
    FormPanel form;

    @UiField(provided = true)
    FileUpload fileUpload;

    @UiField
    FlowPanel library;

    @UiField
    Thumbnails thumbs;

    @Inject
    private PluginConfigService pluginConfigService;

    private String pluginName;
    private ParameterizedCommand<Media> onMediaDelete;

    private Map<Path, Thumbnail> mediaRef = new HashMap<Path, Thumbnail>();

    @PostConstruct
    public void init() {
        fileUpload = createFileUpload();

        initWidget( uiBinder.createAndBindUi( this ) );

        form.setEncoding( FormPanel.ENCODING_MULTIPART );
        form.setMethod( FormPanel.METHOD_POST );

        form.addSubmitHandler( new FormPanel.SubmitHandler() {
            @Override
            public void onSubmit( final FormPanel.SubmitEvent event ) {
                final String fileName = fileUpload.getFilename();
                if ( isNullOrEmpty( fileName ) ) {
                    event.cancel();
                }
            }

            private boolean isNullOrEmpty( final String fileName ) {
                return fileName == null || "".equals( fileName );
            }
        } );

        form.addSubmitCompleteHandler( new FormPanel.SubmitCompleteHandler() {
            public void onSubmitComplete( final FormPanel.SubmitCompleteEvent event ) {
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
            final Thumbnail thumb = mediaRef.get( mediaDeleted.getMedia().getPath() );
            if ( thumb != null ) {
                thumbs.remove( thumb );
            }
        }
    }

    public void addMedia( final Media media ) {

        final Button trash = new Button();
        trash.setIcon( IconType.TRASH );

        final Thumbnail thumbnail = new Thumbnail() {{
            add( new Image( media.getExternalURI() ) {{
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
                thumbs.remove( thumbnail );
            }
        } );

        mediaRef.put( media.getPath(), thumbnail );

        thumbs.add( thumbnail );
    }
}