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

package org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Image;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PictureWidgetViewImpl extends Composite implements PictureWidgetView {

    @DataField
    private Element videoContainer = DOM.createDiv();

    @DataField
    private VideoElement videoElement = Document.get().createVideoElement();

    @Inject
    @DataField
    private Button takePicture;

    private CanvasElement canvasElement = Document.get().createCanvasElement();

    @DataField
    private Element imageContainer = DOM.createDiv();

    @DataField
    private Image imageElement = new Image();

    @Inject
    @DataField
    private Button takeAnotherPicture;

    private PictureWidgetDriver driver;

    private int width;

    private int height;

    private String pictureUrl = "";

    private PictureWidget.TakePictureCallback callback;

    @PostConstruct
    protected void doInit() {

    }

    @Override
    public void init( int width, int height, PictureWidget.TakePictureCallback callback ) {
        this.width = width;
        this.height = height;

        this.callback = callback;

        if ( driver == null ) {
            driver = PictureWidgetDriver.create( videoElement, canvasElement, imageElement.getElement() );
        }

        videoContainer.getStyle().setWidth( width, Style.Unit.PX );
        imageContainer.getStyle().setWidth( width, Style.Unit.PX );

        initDisplay();
    }

    @Override
    public String getPictureUrl() {
        return pictureUrl;
    }

    protected void initDisplay() {
        boolean isShowPicture = pictureUrl != null && !pictureUrl.isEmpty();

        if ( isShowPicture ) {
            showPicture();
        } else {
            showCapturePicture();
        }
    }

    public void showPicture() {
        videoContainer.getStyle().setDisplay( Style.Display.NONE );
        imageElement.setUrl( pictureUrl );
        imageContainer.getStyle().setDisplay( Style.Display.BLOCK );
    }

    public void showCapturePicture() {
        driver.startStreaming( width, height );

        imageContainer.getStyle().setDisplay( Style.Display.NONE );
        videoContainer.getStyle().setDisplay( Style.Display.BLOCK );
    }

    @EventHandler( "takeAnotherPicture" )
    public void takeAnoterPicture( ClickEvent clickEvent ) {
        showCapturePicture();
    }

    @EventHandler( "takePicture" )
    public void takePicture( ClickEvent clickEvent ) {
        if ( callback != null ) {
            String url = driver.takePicture();
            setPictureUrl( url );
            callback.onTakePicture( url );
        }
    }

    public void setPictureUrl( String url ) {

        if ( driver == null ) {
            GWT.log( "Cannot use component while it isn't initialized. Run the init method before set the value " );
            return;
        }

        if ( url == null ) {
            url = "";
        }

        if ( !pictureUrl.equals( url ) ) {
            pictureUrl = url;
            initDisplay();
        }
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        if ( readOnly ) {
            takePicture.setVisible( false );
            takeAnotherPicture.setVisible( false );
            showPicture();
        } else {
            takePicture.setVisible( true );
            takeAnotherPicture.setVisible( true );
            initDisplay();
        }
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if ( driver != null ) {
            driver.doStopStreaming();
        }
    }
}
