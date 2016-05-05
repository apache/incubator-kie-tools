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
package org.uberfire.ext.wires.bayesian.network.client.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.ext.wires.bayesian.network.client.events.RenderBayesianNetworkEvent;
import org.uberfire.ext.wires.bayesian.network.client.shapes.EditableBayesianNode;
import org.uberfire.ext.wires.bayesian.network.client.utils.BayesianUtils;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesNetwork;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesVariable;
import org.uberfire.ext.wires.bayesian.network.parser.client.service.BayesianService;
import org.uberfire.ext.wires.core.client.progressbar.ProgressBar;

@ApplicationScoped
public class BayesianFactory extends BaseFactory {

    @Inject
    private Caller<BayesianService> bayesianService;

    @Inject
    private Event<RenderBayesianNetworkEvent> readyEvent;

    private String[][] colors;
    private List<EditableBayesianNode> bayesianNodes = new ArrayList<EditableBayesianNode>();

    public void init( final String xml03File ) {
        bayesianService.call( new RemoteCallback<BayesNetwork>() {
                                  @Override
                                  public void callback( final BayesNetwork response ) {
                                      bayesianNodes.clear();
                                      for ( BayesVariable bay : response.getNodos() ) {
                                          drawBayesianNode( bay );
                                      }
                                      readyEvent.fire( new RenderBayesianNetworkEvent( bayesianNodes ) );

                                  }
                              }, new ErrorCallback<Object>() {

                                  @Override
                                  public boolean error( Object message,
                                                        Throwable throwable ) {
                                      Window.alert( "Sorry.. the " + xml03File + " could not be read.." );
                                      ProgressBar.setInfinite( false );
                                      return false;
                                  }
                              }
                            ).buildXml03( BayesianUtils.XML3_RESOURCE_PATH + xml03File );
    }

    private void drawBayesianNode( BayesVariable node ) {
        colors = BayesianUtils.getNodeColors();
        double position[][] = node.getPosition();
        int positionX = (int) ( BayesianUtils.POSITION_X_BASE + Math.round( position[ 0 ][ 0 ] ) );
        int positionY = (int) ( BayesianUtils.POSITION_Y_BASE + Math.round( position[ 0 ][ 1 ] ) );
        String fillNodeColor = colors[ 0 ][ 0 ];

        EditableBayesianNode bayesianNode = new EditableBayesianNode( BayesianUtils.WIDTH_NODE,
                                                                      BayesianUtils.HEIGHT_NODE,
                                                                      positionX,
                                                                      positionY,
                                                                      fillNodeColor,
                                                                      node );

        this.setHeader( node,
                        bayesianNode );
        this.setPorcentualBar( node,
                               bayesianNode );

        bayesianNode.buildNode();

        bayesianNodes.add( bayesianNode );
    }

    private void setHeader( BayesVariable node,
                            EditableBayesianNode bayesianNode ) {
        bayesianNode.setHeader( new Rectangle( bayesianNode.getWidth(),
                                               BayesianUtils.HEIGHT_HEADER ) );
        bayesianNode.getHeader().setFillColor( colors[ 0 ][ 1 ] );
        bayesianNode.getHeader().setX( bayesianNode.getHeader().getX() );
        bayesianNode.setTextHeader( drawText( node.getName(),
                                              BayesianUtils.FONT_SIZE_HEADER_NODE,
                                              BayesianUtils.LABEL_POSITION_X_DEFAULT,
                                              BayesianUtils.LABEL_POSITION_Y_DEFAULT ) );
    }

    private void setPorcentualBar( BayesVariable node,
                                   EditableBayesianNode bayesianNode ) {
        String fillColor = colors[ 0 ][ 1 ];
        int widthFill;
        int positionY = 18;
        positionY = ( node.getOutcomes().size() > 3 ) ? positionY - 10 : positionY;
        String borderColor = fillColor;

        List<Rectangle> componentsProgressBar = Lists.newArrayList();
        Text labelPorcentual;
        Map<Text, List<Rectangle>> porcentualsBar = Maps.newHashMap();
        for ( int i = 0; i < node.getOutcomes().size(); i++ ) {
            // Porcentual bar
            positionY += 14;
            labelPorcentual = this.drawText( node.getOutcomes().get( i ),
                                             BayesianUtils.FONT_SIZE_PORCENTUAL_BAR,
                                             BayesianUtils.LABEL_POSITION_X_DEFAULT,
                                             positionY + 7 );
            componentsProgressBar.add( this.drawComponent( Color.rgbToBrowserHexColor( 255,
                                                                                       255,
                                                                                       255 ),
                                                           BayesianUtils.POSITION_X_PORCENTUAL_BAR,
                                                           positionY,
                                                           BayesianUtils.WIDTH_PORCENTUAL_BAR,
                                                           BayesianUtils.HEIGHT_PORCENTUAL_BAR,
                                                           borderColor,
                                                           3 ) );
            // fill bar
            widthFill = calculatePorcentage( node.getProbabilities(),
                                             BayesianUtils.WIDTH_PORCENTUAL_BAR, i );
            componentsProgressBar.add( drawComponent( fillColor,
                                                      BayesianUtils.POSITION_X_PORCENTUAL_BAR,
                                                      positionY,
                                                      widthFill,
                                                      BayesianUtils.HEIGHT_PORCENTUAL_BAR,
                                                      borderColor,
                                                      0 ) );
            bayesianNode.getPorcentualsBar().put( labelPorcentual,
                                                  componentsProgressBar );

            porcentualsBar.put( labelPorcentual,
                                componentsProgressBar );
        }
        bayesianNode.setPorcentualBars( porcentualsBar );
    }

    private int calculatePorcentage( double probabilities[][],
                                     int maxWidthFill,
                                     int position ) {
        double porcentual = 0;
        if ( position == 0 ) {
            porcentual = probabilities[ 0 ][ 0 ];
        } else if ( position == 1 ) {
            porcentual = probabilities[ 0 ][ 1 ];
        }
        porcentual *= 100;
        return (int) ( ( porcentual * maxWidthFill ) / 100 );
    }

}