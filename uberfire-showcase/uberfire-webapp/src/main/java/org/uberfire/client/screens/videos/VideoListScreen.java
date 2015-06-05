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

package org.uberfire.client.screens.videos;

import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.events.YouTubeVideo;

@Dependent
@WorkbenchScreen( identifier = "YouTubeVideos" )
public class VideoListScreen {

    private static final List<YouTubeVideo> VIDEOS = new LinkedList<YouTubeVideo>() {{
        add( new YouTubeVideo( "Quick Tour",
                "A quick tour that shows UberFire cool features.",
                "http://www.youtube.com/embed/xnmSR62_4Us?rel=0" ) );
        add( new YouTubeVideo( "Sample App",
                "Here a good example of an application build on top of UberFire.",
                "http://www.youtube.com/embed/Y3LX4E9OKcs?rel=0" ) );
    }};

    @Inject
    protected Event<YouTubeVideo> event;

    @WorkbenchPartTitle
    public String getTitle() {
        return "UberFire Videos";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        final FlowPanel widgets = new FlowPanel();

        widgets.getElement().getStyle().setPadding( 15, Style.Unit.PX );

        for ( final YouTubeVideo video : VIDEOS ) {

            final Heading h3 = new Heading( HeadingSize.H3, video.getName() );
            final Paragraph p = new Paragraph( video.getDescription() );
            final Button play = new Button( "Play", new ClickHandler() {
                @Override
                public void onClick( final ClickEvent e ) {
                    event.fire( video );
                }
            } );
            play.setIcon( IconType.YOUTUBE_PLAY );
            play.setSize( ButtonSize.SMALL );

            widgets.add( h3 );
            widgets.add( p );
            widgets.add( play );
        }

        return widgets;
    }
}
