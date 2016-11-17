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
package org.uberfire.mvp.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConditionalPlaceRequestTest {

    @Test
    public void conditionalTestTruePredicate() {
        DefaultPlaceRequest other = new DefaultPlaceRequest( "other" );
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest( "dora" ).when( p -> true )
                .orElse( other );
        assertEquals( dora, dora.resolveConditionalPlaceRequest() );
    }

    @Test
    public void conditionalTestFalsePredicate() {
        DefaultPlaceRequest other = new DefaultPlaceRequest( "other" );
        ConditionalPlaceRequest dora =  (ConditionalPlaceRequest) new ConditionalPlaceRequest( "dora" ).when( p -> false )
                .orElse( other );
        assertEquals( other, dora.resolveConditionalPlaceRequest() );
    }

    @Test
    public void incompleteConditionalShouldReturnDefaultPlaceRequest() {
        ConditionalPlaceRequest dora =  new ConditionalPlaceRequest( "dora" );
        assertEquals( "dora", dora.resolveConditionalPlaceRequest().getIdentifier() );
    }

    @Test
    public void incompleteConditionalWithNullsShouldReturnDefaultPlaceRequest() {
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest)  new ConditionalPlaceRequest( "dora" ).when( null ).orElse( null );
        assertEquals( "dora", dora.resolveConditionalPlaceRequest().getIdentifier() );
    }

    @Test
    public void conditionalTestChainingPredicate() {
        DefaultPlaceRequest my = new DefaultPlaceRequest( "my" );
        ConditionalPlaceRequest bento = (ConditionalPlaceRequest)  new ConditionalPlaceRequest( "bento" ).when( p -> false )
                .orElse( my );
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest( "dora" ).when( p -> false )
                .orElse( bento );
        assertEquals( my, dora.resolveConditionalPlaceRequest() );
    }

    @Test
    public void conditionalTestChainingPredicateReturningConditional() {
        DefaultPlaceRequest my = new DefaultPlaceRequest( "my" );
        ConditionalPlaceRequest bento = (ConditionalPlaceRequest) new ConditionalPlaceRequest( "bento" ).when( p -> true )
                .orElse( my );
        ConditionalPlaceRequest dora = (ConditionalPlaceRequest) new ConditionalPlaceRequest( "dora" ).when( p -> false )
                .orElse( bento );
        assertEquals( "bento", dora.resolveConditionalPlaceRequest().getIdentifier() );
    }

}