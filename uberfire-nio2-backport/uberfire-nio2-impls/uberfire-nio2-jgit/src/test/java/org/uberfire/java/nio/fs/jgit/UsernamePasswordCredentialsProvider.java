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

package org.uberfire.java.nio.fs.jgit;

import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.URIish;

/**
 * Mock CredentialsProvider that handles Yes/No requests
 */
public class UsernamePasswordCredentialsProvider extends org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider {

    public UsernamePasswordCredentialsProvider( final String username,
                                                final String password ) {
        super( username,
               password );
    }

    @Override
    public boolean get( final URIish uri,
                        final CredentialItem... items ) throws UnsupportedCredentialItem {
        try {
            return super.get( uri,
                              items );
        } catch ( UnsupportedCredentialItem e ) {
            for ( CredentialItem i : items ) {
                if ( i instanceof CredentialItem.YesNoType ) {
                    ( (CredentialItem.YesNoType) i ).setValue( true );
                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }

}
