/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.user.management.client.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserManagementUtilsTest {

    @Test
    public void testNullSet() {
        assertEquals( "",
                      UserManagementUtils.convertUserRoles( (Set<String>) null ) );
    }

    @Test
    public void testEmptySet() {
        assertEquals( "",
                      UserManagementUtils.convertUserRoles( new HashSet<String>() ) );
    }

    @Test
    public void testSingleMemberSet() {
        assertEquals( "admin",
                      UserManagementUtils.convertUserRoles( new HashSet<String>() {{
                          add( "admin" );
                      }} ) );
    }

    @Test
    public void testMultipleMembersSet() {
        assertEquals( "admin, analyst",
                      UserManagementUtils.convertUserRoles( new TreeSet<String>() {{
                          add( "admin" );
                          add( "analyst" );
                      }} ) );
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void testMultipleMembersSetWithNullMember1() {
        //TreeSet doesn't allow null values, so this really tests TreeSet!
        assertEquals( "admin, analyst",
                      UserManagementUtils.convertUserRoles( new TreeSet<String>() {{
                          add( "admin" );
                          add( "analyst" );
                          add( null );
                      }} ) );
    }

    @Test
    public void testMultipleMembersSetWithNullMember2() {
        //HashSet doesn't guarantee order so we an only reliably test with one non-null member
        assertEquals( "admin",
                      UserManagementUtils.convertUserRoles( new HashSet<String>() {{
                          add( null );
                          add( "admin" );
                      }} ) );
    }

    @Test
    public void testMultipleMembersSetWithEmptyMember() {
        assertEquals( "admin, analyst",
                      UserManagementUtils.convertUserRoles( new TreeSet<String>() {{
                          add( "admin" );
                          add( "analyst" );
                          add( "" );
                      }} ) );
    }

    @Test
    public void testNullString() {
        assertEquals( 0,
                      UserManagementUtils.convertUserRoles( (String) null ).size() );
    }

    @Test
    public void testEmptyString() {
        assertEquals( 0,
                      UserManagementUtils.convertUserRoles( "" ).size() );
    }

    @Test
    public void testSingleRoleString() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( "admin" ) );
        assertEquals( 1,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
    }

    @Test
    public void testMultipleRolesString() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( "admin,analyst" ) );
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
        assertTrue( results.contains( "analyst" ) );
    }

    @Test
    public void testMultipleRolesStringWithWhiteSpace() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( " admin , analyst " ) );
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
        assertTrue( results.contains( "analyst" ) );
    }

    @Test
    public void testMultipleRolesStringEmptyEntry1() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( "admin,,analyst" ) );
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
        assertTrue( results.contains( "analyst" ) );
    }

    @Test
    public void testMultipleRolesStringEmptyEntry2() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( ",admin,,analyst," ) );
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
        assertTrue( results.contains( "analyst" ) );
    }

    @Test
    public void testMultipleRolesStringEmptyEntryWithWhiteSpace() {
        final TreeSet<String> results = new TreeSet<String>( UserManagementUtils.convertUserRoles( " , admin , , analyst , " ) );
        assertEquals( 2,
                      results.size() );
        assertTrue( results.contains( "admin" ) );
        assertTrue( results.contains( "analyst" ) );
    }

}
