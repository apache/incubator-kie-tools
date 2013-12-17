/*
 * Copyright 2013 JBoss Inc
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

package org.uberfire.backend.repositories.impl;

import java.util.Date;

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

    public PortableVersionRecord() {

    }

    public PortableVersionRecord( final String id,
                                  final String author,
                                  final String email,
                                  final String comment,
                                  final Date date,
                                  final String uri ) {
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
}
