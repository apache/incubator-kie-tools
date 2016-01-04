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

package org.uberfire.ext.editor.commons.version.impl;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.java.nio.base.version.VersionRecord;

@Portable
public class PortableVersionRecord implements VersionRecord {

    private String id;
    private String author;
    private String email;
    private String comment;
    private Date date;
    private String uri;

    public PortableVersionRecord( @MapsTo("id") final String id,
                                  @MapsTo("author") final String author,
                                  @MapsTo("email") final String email,
                                  @MapsTo("comment") final String comment,
                                  @MapsTo("date") final Date date,
                                  @MapsTo("uri") final String uri ) {
        this.id = id;
        this.author = author;
        this.email = email;
        this.comment = comment;
        this.date = date;
        this.uri = uri;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String author() {
        return author;
    }

    @Override
    public String email() {
        return null;
    }

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public Date date() {
        return date;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PortableVersionRecord that = (PortableVersionRecord) o;

        if ( author != null ? !author.equals( that.author ) : that.author != null ) {
            return false;
        }
        if ( comment != null ? !comment.equals( that.comment ) : that.comment != null ) {
            return false;
        }
        if ( date != null ? !date.equals( that.date ) : that.date != null ) {
            return false;
        }
        if ( email != null ? !email.equals( that.email ) : that.email != null ) {
            return false;
        }
        if ( id != null ? !id.equals( that.id ) : that.id != null ) {
            return false;
        }
        if ( uri != null ? !uri.equals( that.uri ) : that.uri != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( author != null ? author.hashCode() : 0 );
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        result = 31 * result + ( comment != null ? comment.hashCode() : 0 );
        result = 31 * result + ( date != null ? date.hashCode() : 0 );
        result = 31 * result + ( uri != null ? uri.hashCode() : 0 );
        return result;
    }
}
