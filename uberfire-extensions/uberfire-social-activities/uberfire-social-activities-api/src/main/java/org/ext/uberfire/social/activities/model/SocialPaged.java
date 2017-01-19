/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.ext.uberfire.social.activities.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class SocialPaged implements Serializable {

    private SocialPaged lastQuery;

    private Direction direction;

    private int pageSize;

    private String lastFileReaded;

    private int lastFileIndex;

    private int freshIndex;

    private boolean canIGoForward;
    private boolean canIGoBackward;
    private int numberOfEventsOnFile;

    public SocialPaged() {

    }

    public SocialPaged( int pageSize ) {
        this.pageSize = pageSize;
        this.freshIndex = 0;
        this.lastFileReaded = "";
        this.lastFileIndex = 0;
        this.numberOfEventsOnFile=-1;
        this.canIGoForward = false;
        this.canIGoBackward = false;
        this.direction = Direction.FORWARD;
    }

    public SocialPaged( SocialPaged socialPaged ) {
        if ( socialPaged.lastQuery != null ) {
            this.lastQuery = new SocialPaged( socialPaged.lastQuery );
        }

        this.direction = socialPaged.direction;

        this.pageSize = socialPaged.pageSize;

        this.lastFileReaded = socialPaged.lastFileReaded;

        this.lastFileIndex = socialPaged.lastFileIndex;

        this.freshIndex = socialPaged.freshIndex;

        this.canIGoForward = socialPaged.canIGoForward;

        this.canIGoBackward = socialPaged.canIGoBackward;

        this.numberOfEventsOnFile= socialPaged.numberOfEventsOnFile;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void updateFreshIndex() {
        freshIndex = freshIndex + 1;
    }

    public void updateLastFileIndex() {
        lastFileIndex = lastFileIndex + 1;
    }

    public int freshIndex() {
        return freshIndex;
    }

    public int lastFileIndex() {
        return lastFileIndex;
    }

    public String lastFileReaded() {
        return lastFileReaded;
    }

    public boolean firstFileRead() {
        return lastFileReaded.isEmpty();
    }

    public void setLastFileReaded( String lastFileReaded ) {
        this.lastFileReaded = lastFileReaded;
    }

    public boolean isANewQuery() {
        return lastFileReaded.isEmpty();
    }

    public String getNextFileToRead() {
        Integer lastFile = Integer.valueOf( lastFileReaded );
        Integer nextFile = lastFile - 1;
        this.lastFileReaded = nextFile.toString();
        this.lastFileIndex = 0;
        return nextFile.toString();
    }

    public boolean canIGoForward() {
        return canIGoForward;
    }

    public void setCanIGoForward( boolean canIGoForward ) {
        this.canIGoForward = canIGoForward;
    }

    public boolean canIGoBackward() {
        SocialPaged socialPaged = getLastQuery();
        if ( socialPaged != null && socialPaged.getLastQuery() != null ) {
            return true;
        }
        return false;
    }

    public void forward() {
        this.direction = Direction.FORWARD;
    }

    public void backward() {
        this.direction = Direction.BACKWARD;
    }

    public boolean isBackward() {
        return this.direction == Direction.BACKWARD;
    }

    public void setLastQuery( SocialPaged socialPaged ) {
        this.lastQuery = socialPaged;
    }

    public SocialPaged getLastQuery() {
        return lastQuery;
    }

    public SocialPaged goBackToLastQuery() {
        SocialPaged socialPaged = getLastQuery();
        if ( socialPaged.getLastQuery() != null ) {
            socialPaged = socialPaged.getLastQuery();
        }
        return socialPaged;
    }

    public boolean isLastEventFromLastFile() {
        if ( lastFileReaded.equalsIgnoreCase( "0" ) && lastFileIndex==numberOfEventsOnFile ) {
            return true;
        }
        return false;
    }

    public void setNumberOfEventsOnFile( Integer numberOfEventsOnFile ) {
        this.numberOfEventsOnFile = numberOfEventsOnFile;
    }

    public enum Direction {
        FORWARD, BACKWARD;
    }
}
