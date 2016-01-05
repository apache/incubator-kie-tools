/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.rpc.impl;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.uberfire.rpc.SessionInfo;

@ClientMarshaller(SessionInfo.class)
@ServerMarshaller(SessionInfo.class)
public class SessionInfoMarshalller extends AbstractNullableMarshaller<SessionInfo> {

    @Override
    public SessionInfo doNotNullDemarshall( final EJValue ejValue,
                                            final MarshallingSession marshallingSession ) {

        return new SessionInfoImpl( ejValue.isObject().get( "id" ).isString().stringValue(),
                                    new UserImpl( ejValue.isObject().get( "identityId" ).isString().stringValue() ) );
    }

    @Override
    public String doNotNullMarshall( final SessionInfo sessionInfo,
                                     final MarshallingSession marshallingSession ) {
        return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + SessionInfo.class.getName() + "\"," +
                "\"" + SerializationParts.OBJECT_ID + "\":\"" + sessionInfo.hashCode() + "\"," +
                "\"" + "id" + "\":\"" + sessionInfo.getId() + "\"," +
                "\"" + "identityId" + "\":\"" + sessionInfo.getIdentity().getIdentifier() + "\"}";
    }

    @Override
    public SessionInfo[] getEmptyArray() {
        return new SessionInfo[ 0 ];
    }
}
