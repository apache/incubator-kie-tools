/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.fs.jgit;

import java.util.Date;
import java.util.TimeZone;

import org.uberfire.java.nio.file.OpenOption;

public class JGitOp implements OpenOption {

    final String name;
    final String email;
    final String message;
    final Date when;
    final TimeZone timeZone;

    public JGitOp(final String name) {
        this(name, null, null, null, null);
    }

    public JGitOp(final String name, final String message) {
        this(name, null, message, null, null);
    }

    public JGitOp(final String name, final String email, final String message) {
        this(name, email, message, null, null);
    }

    public JGitOp(final String name, final String email, final String message, final Date when) {
        this(name, email, message, when, null);
    }

    public JGitOp(final String name, final String email, final String message, final Date when, final TimeZone timeZone) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.when = when;
        this.timeZone = timeZone;
    }

}
