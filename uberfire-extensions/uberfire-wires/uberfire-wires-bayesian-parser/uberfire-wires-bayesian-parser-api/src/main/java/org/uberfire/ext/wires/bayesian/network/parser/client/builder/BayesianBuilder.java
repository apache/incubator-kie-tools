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

package org.uberfire.ext.wires.bayesian.network.parser.client.builder;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesNetwork;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesVariable;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Definition;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Network;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Variable;
import org.uberfire.ext.wires.bayesian.network.parser.client.parser.Bif;

public class BayesianBuilder {

    public BayesNetwork build( Bif bif ) {
        BayesNetwork bayesNetwork = new BayesNetwork( bif.getNetwork().getName() );
        int id = 1;
        for ( Definition def : bif.getNetwork().getDefinitions() ) {
            BayesVariable nodo = buildVariable( def, bif.getNetwork(), id );
            bayesNetwork.getNodos().add( nodo );
            id += 1;
        }
        this.setIncomingNodes( bayesNetwork );

        return bayesNetwork;
    }

    private BayesVariable buildVariable( Definition def,
                                         Network network,
                                         int id ) {
        List<String> outcomes = new ArrayList<String>();
        double[][] position = new double[ 2 ][ 2 ];
        this.getOutcomesByVariable( network, def.getName(), outcomes, position );
        return new BayesVariable( def.getName(), id, outcomes, this.getProbabilities( def.getProbabilities(), outcomes ), def.getGiven(),
                                  position );
    }

    private void getOutcomesByVariable( Network network,
                                        String nameDefinition,
                                        List<String> outcomes,
                                        double[][] position ) {
        for ( Variable var : network.getVariables() ) {
            if ( var.getName().equals( nameDefinition ) ) {
                for ( String outcome : var.getOutComes() ) {
                    outcomes.add( outcome );
                }
                // get position
                position = getPosition( var.getPosition(), position );
            }
        }
    }

    private double[][] getProbabilities( String table,
                                         List<String> outcomes ) {
        double probabilities[][] = new double[ table.split( " " ).length ][ table.split( " " ).length ];
        String[] values = table.split( " " );
        int k = 0;
        for ( int i = 0; i < values.length / outcomes.size(); i++ ) {
            for ( int j = 0; j < outcomes.size(); j++ ) {
                probabilities[ i ][ j ] = Double.valueOf( values[ k ] );
                k += 1;
            }
        }
        return probabilities;
    }

    private double[][] getPosition( String stringPosition,
                                    double[][] position ) {
        if ( stringPosition != null ) {
            stringPosition = this.clearStringPosticion( stringPosition );
            int i = 0;
            int j = 0;
            for ( String pos : stringPosition.split( "," ) ) {
                position[ i ][ j ] = Double.parseDouble( pos );
                if ( i < j ) {
                    i += 1;
                }
                j += 1;
            }
        }
        return null;
    }

    private String clearStringPosticion( String stringPosition ) {
        stringPosition = stringPosition.replace( "position", "" );
        stringPosition = stringPosition.replace( "=", "" );
        stringPosition = stringPosition.replace( "(", "" );
        stringPosition = stringPosition.replace( ")", "" );
        stringPosition = stringPosition.trim();
        return stringPosition;
    }

    private void setIncomingNodes( BayesNetwork bayesNetwork ) {
        for ( BayesVariable node : bayesNetwork.getNodos() ) {
            if ( node.getGiven() != null && !node.getGiven().isEmpty() ) {
                node.setIncomingNodes( this.getNodesByGiven( node.getGiven(), bayesNetwork.getNodos() ) );
            }
        }
    }

    private List<BayesVariable> getNodesByGiven( List<String> given,
                                                 List<BayesVariable> nodes ) {
        List<BayesVariable> listIncoming = new ArrayList<BayesVariable>();
        for ( String giv : given ) {
            for ( BayesVariable node : nodes ) {
                if ( node.getName().equals( giv ) ) {
                    listIncoming.add( node );
                    break;
                }
            }
        }
        return listIncoming;
    }
}
