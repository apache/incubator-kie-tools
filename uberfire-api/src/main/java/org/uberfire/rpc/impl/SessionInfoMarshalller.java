package org.uberfire.rpc.impl;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.impl.IdentityImpl;

@ClientMarshaller(SessionInfo.class)
@ServerMarshaller(SessionInfo.class)
public class SessionInfoMarshalller extends AbstractNullableMarshaller<SessionInfo> {

    @Override
    public SessionInfo doNotNullDemarshall( final EJValue ejValue,
                                            final MarshallingSession marshallingSession ) {

        return new SessionInfoImpl( ejValue.isObject().get( "id" ).isString().stringValue(),
                                    new IdentityImpl( ejValue.isObject().get( "identityId" ).isString().stringValue() ) );
    }

    @Override
    public String doNotNullMarshall( final SessionInfo sessionInfo,
                                     final MarshallingSession marshallingSession ) {
        return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + SessionInfo.class.getName() + "\"," +
                "\"" + SerializationParts.OBJECT_ID + "\":\"" + sessionInfo.hashCode() + "\"," +
                "\"" + "id" + "\":\"" + sessionInfo.getId() + "\"," +
                "\"" + "identityId" + "\":\"" + sessionInfo.getIdentity().getName() + "\"}";
    }

    @Override
    public SessionInfo[] getEmptyArray() {
        return new SessionInfo[ 0 ];
    }
}
