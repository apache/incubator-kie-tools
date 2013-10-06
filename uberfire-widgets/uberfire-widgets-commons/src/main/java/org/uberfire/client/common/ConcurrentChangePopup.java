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

package org.uberfire.client.common;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;
import org.uberfire.security.Identity;

public class ConcurrentChangePopup extends AbstractConcurrentChangePopup {


    private ConcurrentChangePopup( final String content,
                                   final Command onIgnore,
                                   final Command onAction,
                                   final String buttonText ) {
        super(content, onIgnore, onAction, buttonText);
    }

    private ConcurrentChangePopup( final String content,
                                   final Command onIgnore,
                                   final Command onReOpen ) {
        this(content, onIgnore, onReOpen, CommonConstants.INSTANCE.ReOpen());
    }

    private ConcurrentChangePopup( final String content,
                                   final Command onForceSave,
                                   final Command onIgnore,
                                   final Command onReOpen ) {
        super(content, onForceSave, onIgnore, onReOpen);
    }

    public static ConcurrentChangePopup newConcurrentUpdate( final Path path,
                                                             final Identity identity,
                                                             final Command onForceSave,
                                                             final Command onCancel,
                                                             final Command onReOpen ) {
        final String message = CommonConstants.INSTANCE.ConcurrentUpdate( identity.getName(), path.toURI() );

        return new ConcurrentChangePopup( message, onForceSave, onCancel, onReOpen );
    }

    public static ConcurrentChangePopup newConcurrentRename( final Path source,
                                                             final Path target,
                                                             final Identity identity,
                                                             final Command onIgnore,
                                                             final Command onReOpen ) {
        final String message = CommonConstants.INSTANCE.ConcurrentRename( identity.getName(), source.toURI(), target.toURI() );
        return new ConcurrentChangePopup( message, onIgnore, onReOpen );
    }

    public static ConcurrentChangePopup newConcurrentDelete( final Path path,
                                                             final Identity identity,
                                                             final Command onIgnore,
                                                             final Command onClose ) {
        final String message = CommonConstants.INSTANCE.ConcurrentDelete( identity.getName(), path.toURI() );
        return new ConcurrentChangePopup( message, onIgnore, onClose, CommonConstants.INSTANCE.Close() );
    }
}