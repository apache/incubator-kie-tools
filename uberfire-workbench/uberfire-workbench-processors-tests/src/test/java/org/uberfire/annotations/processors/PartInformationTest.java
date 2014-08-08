package org.uberfire.annotations.processors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PartInformationTest {

    @Test
    public void testJustPartName() throws Exception {
        PartInformation pi = new PartInformation( "thePlace" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 0, pi.getParameters().size() );
    }

    @Test
    public void testPartNameWithTrailingQuestionMark() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 0, pi.getParameters().size() );
    }

    @Test
    public void testPartNameAndOneParam() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?oh=yeah" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "yeah", pi.getParameters().get( "oh" ) );
    }

    @Test
    public void testPartNameAndManyParams() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?oh=yeah&really=yup&areYou=sure" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 3, pi.getParameters().size() );
        assertEquals( "yeah", pi.getParameters().get( "oh" ) );
        assertEquals( "yup", pi.getParameters().get( "really" ) );
        assertEquals( "sure", pi.getParameters().get( "areYou" ) );
    }

    @Test
    public void testEscapesInAllParts() throws Exception {
        PartInformation pi = new PartInformation( "the%3fPlace?o%3dh=ye%3dah&re%21ally=y%25up" );
        assertEquals( "the?Place", pi.getPartName() );
        assertEquals( 2, pi.getParameters().size() );
        assertEquals( "ye=ah", pi.getParameters().get( "o=h" ) );
        assertEquals( "y%up", pi.getParameters().get( "re!ally" ) );
    }

    @Test
    public void testEmptyParamKey() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?=emptyString" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "emptyString", pi.getParameters().get( "" ) );
    }

    @Test
    public void testEmptyParamValue() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?lonelyKey" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "", pi.getParameters().get( "lonelyKey" ) );
    }

    @Test
    public void testEmptyParamValueTrailingEquals() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?lonelyKey=" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "", pi.getParameters().get( "lonelyKey" ) );
    }

    @Test
    public void testRepeatedParamLastValueWins() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?repeat=peat&repeat=eat&repeat=at" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "at", pi.getParameters().get( "repeat" ) );
    }

    @Test
    public void testEqualsBeforeQuestionMarkIsALiteral() throws Exception {
        PartInformation pi = new PartInformation( "strange=place=name" );
        assertEquals( "strange=place=name", pi.getPartName() );
        assertEquals( 0, pi.getParameters().size() );
    }

    @Test
    public void testQuestionMarkAfterQuestionMarkIsALiteral() throws Exception {
        PartInformation pi = new PartInformation( "thePlace?strange?param?name=strange?param?value" );
        assertEquals( "thePlace", pi.getPartName() );
        assertEquals( 1, pi.getParameters().size() );
        assertEquals( "strange?param?value", pi.getParameters().get( "strange?param?name" ) );
    }

}
