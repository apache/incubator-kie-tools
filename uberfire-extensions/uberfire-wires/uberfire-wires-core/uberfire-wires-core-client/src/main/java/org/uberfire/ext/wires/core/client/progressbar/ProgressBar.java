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

package org.uberfire.ext.wires.core.client.progressbar;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.LinearGradient;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.user.client.Timer;

public class ProgressBar extends Group {

    private Timer timer;
    private static boolean infinite = true;
    private Rectangle substrate;
    private static int substrateWidth = 300;
    private static int substrateHeight = 34;
    private String substrateColor = "#666";
    private LinearGradient substrateGradient;

    private Rectangle progress;
    private int progressWidth = 0;
    private int progressHeight = 0;

    private int width = 0;
    private int height = 0;

    private LinearGradient progressGradient;

    private Text progressPercentage;

    private final int PROGRESS_MARGIN = 4;

    private Layer layer;

    public ProgressBar( int width,
                        int height,
                        Layer layer ) {
        setInfinite( true );
        this.layer = layer;

        this.width = width;
        this.height = height;

        progressHeight = substrateHeight - PROGRESS_MARGIN;

        init();

        add( substrate );
        add( progress );
        add( progressPercentage );

    }

    public void show() {
        infinite = true;
        setVisible( true );
        progressWidth = 0;
        timer.scheduleRepeating( 1 );
    }

    public void hide() {
        setVisible( false );
        timer.cancel();
    }

    public void center() {

        int x = (int) ( width / 2 - substrate.getWidth() / 2 );
        int y = (int) ( height / 2 - substrate.getHeight() / 2 );

        substrate.setX( x ).setY( y );
        progress.setX( x + 2 ).setY( y + 2 );
        progressPercentage.setX( x + substrate.getWidth() / 2 ).setY( y + 2 + progress.getHeight() / 2 );

        layer.batch();

    }

    public static int getSubstrateWidth() {
        return substrateWidth;
    }

    public void setProgressWidth( int progressWidth ) {
        this.progressWidth = progressWidth;
        progress.setWidth( progressWidth );
    }

    public int getProgressWidth() {
        return progressWidth;
    }

    public Text getProgressPercentage() {
        return progressPercentage;
    }

    public Timer getTimer() {
        return timer;
    }

    private void init() {

        substrate = new Rectangle( substrateWidth, substrateHeight );
        progress = new Rectangle( progressWidth, progressHeight );

        progressPercentage = new Text( "0 %", "Lucida Console", 12 ).setFillColor( ColorName.WHITE.getValue() )
                .setStrokeColor( substrateColor ).setTextBaseLine( TextBaseLine.MIDDLE ).setTextAlign( TextAlign.CENTER );

        substrateGradient = new LinearGradient( 0, substrateHeight, 0, 0 );
        substrateGradient.addColorStop( 0.4, "rgba(255,255,255, 0.1)" );
        substrateGradient.addColorStop( 0.6, "rgba(255,255,255, 0.7)" );
        substrateGradient.addColorStop( 0.9, "rgba(255,255,255,0.4)" );
        substrateGradient.addColorStop( 1, "rgba(189,189,189,1)" );

        substrate.setFillGradient( substrateGradient ).setShadow( new Shadow( substrateColor, 5, 3, 3 ) )
                .setStrokeColor( substrateColor ).setStrokeWidth( 1 );

        progressGradient = new LinearGradient( 0, -50, 0, 50 );
        progressGradient.addColorStop( 0.5, "#4DA4F3" );
        progressGradient.addColorStop( 0.8, "#ADD9FF" );
        progressGradient.addColorStop( 1, "#9ED1FF" );

        progress.setFillGradient( progressGradient );

        center();
        progress();

    }

    private void progress() {
        timer = new Timer() {
            @Override
            public void run() {
                if ( !isInfinite() ) {
                    hide();
                }
                progressWidth++;
                if ( progressWidth > substrateWidth - 4 ) {
                    this.cancel();
                }
                progressPercentage.setText( (int) progressWidth / 3 + " %" );
                setProgressWidth( progressWidth );
                layer.batch();
            }
        };
        timer.scheduleRepeating( 1 );

    }

    public static boolean isInfinite() {
        return infinite;
    }

    public static void setInfinite( boolean infinite ) {
        ProgressBar.infinite = infinite;
    }

}
