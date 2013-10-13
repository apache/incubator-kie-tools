package org.uberfire.io.impl.cluster.helix;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.helix.Criteria;
import org.apache.helix.HelixManager;
import org.apache.helix.InstanceType;
import org.apache.helix.NotificationContext;
import org.apache.helix.messaging.handling.HelixTaskResult;
import org.apache.helix.messaging.handling.MessageHandler;
import org.apache.helix.messaging.handling.MessageHandlerFactory;
import org.apache.helix.model.Message;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.data.Pair;
import org.uberfire.io.impl.cluster.ClusterMessageType;
import org.uberfire.commons.message.AsyncCallback;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;

import static java.util.Arrays.*;
import static java.util.UUID.*;
import static org.apache.helix.HelixManagerFactory.*;

public class ClusterServiceHelix implements ClusterService {

    private final String clusterName;
    private final String instanceName;
    private final HelixManager participantManager;
    private final SimpleLock lock = new SimpleLock();
    private final String resourceName;
    private int stackSize = 0;
    private final MessageHandlerResolver messageHandlerResolver;
    private AtomicBoolean started = new AtomicBoolean(false);

    public ClusterServiceHelix( final String clusterName,
                                final String zkAddress,
                                final String instanceName,
                                final String resourceName,
                                final MessageHandlerResolver messageHandlerResolver ) {
        this.clusterName = clusterName;
        this.instanceName = instanceName;
        this.resourceName = resourceName;
        this.messageHandlerResolver = messageHandlerResolver;

        this.participantManager = getZKHelixManager( clusterName, instanceName, InstanceType.PARTICIPANT, zkAddress );
    }
    @Override
    public void start() {
        if (isStarted()) {
            return;
        }
        try {
            this.participantManager.connect();
            disablePartition();
            this.participantManager.getStateMachineEngine().registerStateModelFactory( "LeaderStandby", new LockTransitionalFactory( lock ) );
            this.participantManager.getMessagingService().registerMessageHandlerFactory( Message.MessageType.USER_DEFINE_MSG.toString(), new MessageHandlerResolverWrapper( messageHandlerResolver ).convert() );
            started.set(true);
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public boolean isStarted() {
        return started.get();
    }

    @Override
    public void dispose() {
        if (this.participantManager != null) {
            this.participantManager.disconnect();
        }
    }

    private void enablePartition() {
        if (!isStarted()) {
            return;
        }
        participantManager.getClusterManagmentTool().enablePartition( true, clusterName, instanceName, resourceName, asList( resourceName + "_0" ) );
    }

    private void disablePartition() {
        if (!isStarted()) {
            return;
        }
        participantManager.getClusterManagmentTool().enablePartition( false, clusterName, instanceName, resourceName, asList( resourceName + "_0" ) );
    }

    @Override
    public void lock() {
        if (!isStarted()) {
            return;
        }
        stackSize++;
        if ( lock.isLocked() ) {
            return;
        }
        enablePartition();

        while ( !lock.isLocked() ) {
            try {
                Thread.sleep( 10 );
            } catch ( InterruptedException e ) {
            }
        }
    }

    @Override
    public void unlock() {
        if (!isStarted()) {
            return;
        }
        stackSize--;
        if ( !lock.isLocked() ) {
            stackSize = 0;
            return;
        }

        if ( stackSize == 0 ) {
            disablePartition();

            while ( lock.isLocked() ) {
                try {
                    Thread.sleep( 10 );
                } catch ( InterruptedException e ) {
                }
            }
        }
    }

    @Override
    public boolean isLocked() {
        if (!isStarted()) {
            return true;
        }
        return lock.isLocked();
    }

    @Override
    public void broadcastAndWait( MessageType type,
                                  Map<String, String> content,
                                  int timeOut ) {
        if (!isStarted()) {
            return;
        }
        participantManager.getMessagingService().sendAndWait( buildCriteria(), buildMessage( type, content ), new org.apache.helix.messaging.AsyncCallback( timeOut ) {
            @Override
            public void onTimeOut() {
            }

            @Override
            public void onReplyMessage( final Message message ) {
            }
        }, timeOut );
    }

    @Override
    public void broadcastAndWait( final MessageType type,
                                  final Map<String, String> content,
                                  final int timeOut,
                                  final AsyncCallback callback ) {
        if (!isStarted()) {
            return;
        }
        int msg = participantManager.getMessagingService().sendAndWait( buildCriteria(), buildMessage( type, content ), new org.apache.helix.messaging.AsyncCallback() {
            @Override
            public void onTimeOut() {
                callback.onTimeOut();
            }

            @Override
            public void onReplyMessage( final Message message ) {
                final MessageType type = buildMessageTypeFromReply( message );
                final Map<String, String> map = getMessageContentFromReply( message );

                callback.onReply( type, map );
            }
        }, timeOut );
        if ( msg == 0 ) {
            callback.onTimeOut();
        }
    }

    @Override
    public void broadcast( final MessageType type,
                           final Map<String, String> content ) {
        if (!isStarted()) {
            return;
        }
        participantManager.getMessagingService().send( buildCriteria(), buildMessage( type, content ) );
    }

    @Override
    public void broadcast( final MessageType type,
                           final Map<String, String> content,
                           final int timeOut,
                           final AsyncCallback callback ) {
        if (!isStarted()) {
            return;
        }
        participantManager.getMessagingService().send( buildCriteria(), buildMessage( type, content ), new org.apache.helix.messaging.AsyncCallback() {
            @Override
            public void onTimeOut() {
                callback.onTimeOut();
            }

            @Override
            public void onReplyMessage( final Message message ) {
                final MessageType type = buildMessageTypeFromReply( message );
                final Map<String, String> map = getMessageContent( message );

                callback.onReply( type, map );
            }
        }, timeOut );
    }

    @Override
    public void sendTo( String resourceId,
                        MessageType type,
                        Map<String, String> content ) {
        if (!isStarted()) {
            return;
        }
        participantManager.getMessagingService().send( buildCriteria( resourceId ), buildMessage( type, content ) );
    }

    private Criteria buildCriteria( final String resourceId ) {
        return new Criteria() {{
            setInstanceName( resourceId );
            setRecipientInstanceType( InstanceType.PARTICIPANT );
            setResource( resourceName );
            setSelfExcluded( true );
            setSessionSpecific( true );
        }};
    }

    private Criteria buildCriteria() {
        return buildCriteria( "%" );
    }

    private Message buildMessage( final MessageType type,
                                  final Map<String, String> content ) {
        return new Message( Message.MessageType.USER_DEFINE_MSG, randomUUID().toString() ) {{
            setMsgState( Message.MessageState.NEW );
            getRecord().setMapField( "content", content );
            getRecord().setSimpleField( "type", type.toString() );
            getRecord().setSimpleField( "origin", instanceName );
        }};
    }

    class MessageHandlerResolverWrapper {

        private final MessageHandlerResolver resolver;

        MessageHandlerResolverWrapper( final MessageHandlerResolver resolver ) {
            this.resolver = resolver;
        }

        MessageHandlerFactory convert() {
            return new MessageHandlerFactory() {

                @Override
                public MessageHandler createHandler( final Message message,
                                                     final NotificationContext context ) {

                    return new MessageHandler( message, context ) {
                        @Override
                        public HelixTaskResult handleMessage() throws InterruptedException {
                            final MessageType type = buildMessageType( _message.getRecord().getSimpleField( "type" ) );
                            final Map<String, String> map = getMessageContent( _message );

                            final Pair<MessageType, Map<String, String>> result = resolver.resolveHandler( type ).handleMessage( type, map );

                            if ( result == null ) {
                                return new HelixTaskResult() {{
                                    setSuccess( true );
                                }};
                            }

                            return new HelixTaskResult() {{
                                setSuccess( true );
                                getTaskResultMap().put( "type", result.getK1().toString() );
                                getTaskResultMap().put( "origin", instanceName );
                                for ( Map.Entry<String, String> entry : result.getK2().entrySet() ) {
                                    getTaskResultMap().put( entry.getKey(), entry.getValue() );
                                }
                            }};
                        }

                        @Override
                        public void onError( final Exception e,
                                             final ErrorCode code,
                                             final ErrorType type ) {
                        }
                    };
                }

                @Override
                public String getMessageType() {
                    return Message.MessageType.USER_DEFINE_MSG.toString();
                }

                @Override
                public void reset() {
                }
            };
        }
    }

    private MessageType buildMessageType( final String _type ) {
        if ( _type == null ) {
            return null;
        }

        MessageType type;
        try {
            type = ClusterMessageType.valueOf( _type );
        } catch ( Exception ex ) {
            type = new MessageType() {
                @Override
                public String toString() {
                    return _type;
                }

                @Override
                public int hashCode() {
                    return _type.hashCode();
                }
            };
        }

        return type;
    }

    private MessageType buildMessageTypeFromReply( Message message ) {
        final Map<String, String> result = message.getRecord().getMapField( Message.Attributes.MESSAGE_RESULT.toString() );
        return buildMessageType( result.get( "type" ) );
    }

    private Map<String, String> getMessageContent( final Message message ) {
        return message.getRecord().getMapField( "content" );
    }

    private Map<String, String> getMessageContentFromReply( final Message message ) {
        return new HashMap<String, String>() {{
            for ( final Map.Entry<String, String> field : message.getRecord().getMapField( Message.Attributes.MESSAGE_RESULT.toString() ).entrySet() ) {
                if ( !field.getKey().equals( "origin" ) && !field.getKey().equals( "type" ) ) {
                    put( field.getKey(), field.getValue() );
                }
            }
        }};
    }

}
