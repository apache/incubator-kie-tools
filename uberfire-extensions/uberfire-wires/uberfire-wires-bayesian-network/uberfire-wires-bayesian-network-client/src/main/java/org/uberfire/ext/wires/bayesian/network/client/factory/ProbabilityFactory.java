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

import java.util.Map;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.Color;
import com.google.common.collect.Maps;
import org.uberfire.ext.wires.bayesian.network.client.shapes.EditableBayesianProbability;
import org.uberfire.ext.wires.bayesian.network.client.utils.BayesianUtils;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesVariable;

public class ProbabilityFactory extends BaseFactory {

    private int positionXPorc;

    public EditableBayesianProbability init( final BayesVariable node ) {
        int positionX = 350;
        int positionY = 80;
        int width = 65;
        int height = 100;

        EditableBayesianProbability bayesianProbabilityGrid = new EditableBayesianProbability( 1200,
                                                                                               300,
                                                                                               0,
                                                                                               0 );

        // draw parent node
        drawNodeSelected( node,
                          positionX,
                          positionY,
                          width,
                          height,
                          bayesianProbabilityGrid );

        // draw porcentual options
        int positionXOptions = positionX + width;
        int heightOptions = height / node.getOutcomes().size();
        int widthOptions = width - 10;
        drawPorcentualOptions( node,
                               positionXOptions,
                               positionY,
                               widthOptions,
                               heightOptions,
                               bayesianProbabilityGrid );

        // draw porcentual values
        int positionXValues = positionXOptions + widthOptions;
        int positionYValues = positionY;
        int heightPorcentualValue = height / node.getOutcomes().size();
        int widthPorcentualValue = widthOptions - 5;
        drawPorcentualValues( node,
                              positionXValues,
                              positionYValues,
                              widthPorcentualValue,
                              heightPorcentualValue,
                              positionYValues,
                              bayesianProbabilityGrid );

        // draw incoming nodes
        int widthIncoming = width + widthOptions;
        int heightIncoming = 25;
        int positionYIncoming = positionY - heightIncoming;
        drawIncomingNodes( node,
                           positionX,
                           positionYIncoming,
                           widthIncoming,
                           heightIncoming,
                           widthPorcentualValue,
                           widthOptions,
                           heightOptions,
                           bayesianProbabilityGrid );

        bayesianProbabilityGrid.buildGrid();

        return bayesianProbabilityGrid;
    }

    private void drawNodeSelected( final BayesVariable node,
                                   final int positionX,
                                   final int positionY,
                                   final int width,
                                   final int height,
                                   final EditableBayesianProbability bayesianProbabilityGrid ) {
        Map<Text, Rectangle> parentNode = Maps.newHashMap();
        Rectangle nodeSelected = super.drawComponent( Color.rgbToBrowserHexColor( 183,
                                                                                  198,
                                                                                  201 ),
                                                      positionX,
                                                      positionY,
                                                      width,
                                                      height,
                                                      Color.rgbToBrowserHexColor( 183,
                                                                                  198,
                                                                                  201 ),
                                                      2 );

        Text label = super.drawText( node.getName(),
                                     BayesianUtils.FONT_SIZE_TEXT_LABEL,
                                     positionX + 7,
                                     positionY + 54 );
        parentNode.put( label,
                        nodeSelected );
        bayesianProbabilityGrid.setParentNode( parentNode );
    }

    private void drawPorcentualOptions( final BayesVariable node,
                                        final int positionXOptions,
                                        final int positionYOptions,
                                        final int widthOptions,
                                        final int heightOptions,
                                        final EditableBayesianProbability bayesianProbabilityGrid ) {
        int _positionYOptions = positionYOptions;
        Map<Text, Rectangle> porcentualOptions = Maps.newHashMap();
        for ( String outcome : node.getOutcomes() ) {
            Rectangle porcentualOption = super.drawComponent( Color.rgbToBrowserHexColor( 200,
                                                                                          216,
                                                                                          203 ),
                                                              positionXOptions,
                                                              _positionYOptions,
                                                              widthOptions,
                                                              heightOptions,
                                                              Color.rgbToBrowserHexColor( 200,
                                                                                          216,
                                                                                          203 ),
                                                              0 );

            Text porcentualLabel = super.drawText( outcome,
                                                   BayesianUtils.FONT_SIZE_TEXT_LABEL,
                                                   positionXOptions + 7,
                                                   _positionYOptions + ( 20 / node.getOutcomes().size() ) + 19 );

            _positionYOptions += heightOptions;
            porcentualOptions.put( porcentualLabel, porcentualOption );
        }
        bayesianProbabilityGrid.setPorcentualOptions( porcentualOptions );
    }

    private void drawPorcentualValues( final BayesVariable node,
                                       final int positionXValues,
                                       final int positionYValues,
                                       final int widthPorcentualValue,
                                       final int heightPorcentualValue,
                                       final int positionY,
                                       final EditableBayesianProbability bayesianProbabilityGrid ) {
        int _positionXValues = positionXValues;
        int _positionYValues = positionYValues;
        double probabilities[][] = ( node.getGiven() != null && node.getGiven().size() > 1 ) ? BayesianUtils.orderListValues( node,
                                                                                                                              node.getOutcomes().size() ) : node.getProbabilities();
        Map<Text, Rectangle> porcentualValues = Maps.newHashMap();
        for ( int i = 0; i < probabilities.length / node.getOutcomes().size(); i++ ) {
            for ( int j = 0; j < node.getOutcomes().size(); j++ ) {
                Rectangle porcentual = super.drawComponent( Color.rgbToBrowserHexColor( 255,
                                                                                        255,
                                                                                        255 ),
                                                            _positionXValues,
                                                            _positionYValues,
                                                            widthPorcentualValue,
                                                            heightPorcentualValue,
                                                            Color.rgbToBrowserHexColor( 183,
                                                                                        198,
                                                                                        201 ),
                                                            0 );

                Text porcentualLabel = super.drawText( String.valueOf( probabilities[ i ][ j ] ),
                                                       BayesianUtils.FONT_SIZE_TEXT_LABEL,
                                                       _positionXValues + 7,
                                                       _positionYValues + ( 20 / node.getOutcomes().size() ) + 19 );

                _positionYValues += heightPorcentualValue;
                porcentualValues.put( porcentualLabel, porcentual );
            }
            _positionYValues = positionY;
            _positionXValues += widthPorcentualValue;
        }
        bayesianProbabilityGrid.setPorcentualValues( porcentualValues );
    }

    private void drawIncomingNodes( final BayesVariable node,
                                    final int positionXIncoming,
                                    final int positionYIncoming,
                                    final int widthIncoming,
                                    final int heightIncoming,
                                    final int widthPorcentualValue,
                                    final int widthOptions,
                                    final int heightOptions,
                                    final EditableBayesianProbability bayesianProbabilityGrid ) {
        int _positionYIncoming = positionYIncoming;
        String color = Color.rgbToBrowserHexColor( 182,
                                                   199,
                                                   191 );
        int incomingPosition = 0;
        Map<Map<Text, Rectangle>, Map<Text, Rectangle>> porcentualIncoming = Maps.newHashMap();
        int acountIterations = 0;
        int widthNode = 0;
        if ( node.getIncomingNodes() != null && !node.getIncomingNodes().isEmpty() ) {
            for ( BayesVariable nod : node.getIncomingNodes() ) {
                Map<Text, Rectangle> incomingNodes = Maps.newHashMap();
                Map<Text, Rectangle> porcentualValues = Maps.newHashMap();

                // draw label
                Rectangle incomingNode = super.drawComponent( color,
                                                              positionXIncoming,
                                                              _positionYIncoming,
                                                              widthIncoming,
                                                              heightIncoming,
                                                              color,
                                                              0 );
                Text incomingLabel = super.drawText( nod.getName(),
                                                     BayesianUtils.FONT_SIZE_TEXT_LABEL,
                                                     positionXIncoming + 10,
                                                     _positionYIncoming + 19 );

                incomingNodes.put( incomingLabel,
                                   incomingNode );

                // draw porcentual options
                positionXPorc = positionXIncoming + widthIncoming;

                if ( incomingPosition == 0 ) {
                    for ( int i = 0; i < ( node.getProbabilities().length / node.getOutcomes().size() ) / nod.getOutcomes().size(); i++ ) {
                        drawPorcentualIncoming( nod,
                                                _positionYIncoming,
                                                heightIncoming,
                                                widthPorcentualValue,
                                                heightOptions,
                                                bayesianProbabilityGrid,
                                                porcentualValues );
                        acountIterations += 1;
                    }
                } else {
                    int sizeOutcomesPrevIncomingNode = node.getIncomingNodes().get( incomingPosition - 1 ).getOutcomes().size();
                    int iter = acountIterations / nod.getOutcomes().size();
                    acountIterations = 0;

                    widthNode = ( widthNode == 0 ) ? widthPorcentualValue * sizeOutcomesPrevIncomingNode : widthNode * sizeOutcomesPrevIncomingNode;

                    for ( int i = 0; i < iter; i++ ) {
                        drawPorcentualIncoming( nod,
                                                _positionYIncoming,
                                                heightIncoming,
                                                widthNode,
                                                heightOptions,
                                                bayesianProbabilityGrid,
                                                porcentualValues );
                        acountIterations += 1;
                    }
                }
                incomingPosition += 1;

                _positionYIncoming -= heightIncoming;
                color = Color.rgbToBrowserHexColor( 210,
                                                    204,
                                                    229 );
                porcentualIncoming.put( incomingNodes,
                                        porcentualValues );
            }
            bayesianProbabilityGrid.setIncomingNodes( porcentualIncoming );
        }

    }

    private void drawPorcentualIncoming( final BayesVariable nod,
                                         final int positionYIncoming,
                                         final int heightIncoming,
                                         final int widthPorcentualValue,
                                         final int heightOptions,
                                         final EditableBayesianProbability bayesianProbabilityGrid,
                                         final Map<Text, Rectangle> porcentualValues ) {
        for ( String out : nod.getOutcomes() ) {
            Rectangle porcentual = super.drawComponent( Color.rgbToBrowserHexColor( 200,
                                                                                    216,
                                                                                    203 ),
                                                        positionXPorc,
                                                        positionYIncoming,
                                                        widthPorcentualValue,
                                                        heightIncoming,
                                                        Color.rgbToBrowserHexColor( 200,
                                                                                    216,
                                                                                    203 ),
                                                        0 );

            Text porcentualLabel = super.drawText( out,
                                                   BayesianUtils.FONT_SIZE_TEXT_LABEL, positionXPorc + 7,
                                                   positionYIncoming + 19 );

            positionXPorc += widthPorcentualValue;
            porcentualValues.put( porcentualLabel,
                                  porcentual );
        }
    }

}
