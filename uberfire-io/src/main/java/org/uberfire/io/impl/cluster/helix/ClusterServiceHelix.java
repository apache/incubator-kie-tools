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

package org.uberfire.io.impl.cluster.helix;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.helix.Criteria;
import org.apache.helix.HelixManager;
import org.apache.helix.InstanceType;
import org.apache.helix.NotificationContext;
import org.apache.helix.messaging.handling.HelixTaskResult;
import org.apache.helix.messaging.handling.MessageHandler;
import org.apache.helix.messaging.handling.MessageHandlerFactory;
import org.apache.helix.model.ExternalView;
import org.apache.helix.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.data.Pair;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.commons.message.AsyncCallback;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageType;
import org.uberfire.io.impl.cluster.ClusterMessageType;

import static java.util.Arrays.*;
import static java.util.UUID.*;
import static org.apache.helix.HelixManagerFactory.*;

public class ClusterServiceHelix implements ClusterService {

    private static final AtomicInteger counter = new AtomicInteger( 0 );
    private static final Logger logger = LoggerFactory.getLogger( ClusterServiceHelix.class );

    private final String clusterName;
    private final String instanceName;
    private final HelixManager participantManager;
    private final String resourceName;
    private final Map<String, MessageHandlerResolver> messageHandlerResolver = new ConcurrentHashMap<String, MessageHandlerResolver>();

    private final ReentrantLock lock = new ReentrantLock( true );

    public ClusterServiceHelix( final String clusterName,
                                final String zkAddress,
                                final String instanceName,
                                final String resourceName,
                                final MessageHandlerResolver messageHandlerResolver ) {
        this.clusterName = clusterName;
        this.instanceName = instanceName;
        this.resourceName = resourceName;
        addMessageHandlerResolver( messageHandlerResolver );
        this.participantManager = getZkHelixManager( clusterName, zkAddress, instanceName );
        PriorityDisposableRegistry.register( this );
        start();
    }

    HelixManager getZkHelixManager( String clusterName,
                                    String zkAddress,
                                    String instanceName ) {
        return getZKHelixManager( clusterName, instanceName, InstanceType.PARTICIPANT, zkAddress );
    }

    //TODO {porcelli} quick hack for now, the real solution would have a cluster per repo
    @Override
    public void addMessageHandlerResolver( final MessageHandlerResolver resolver ) {
        if ( resolver != null ) {
            this.messageHandlerResolver.put( resolver.getServiceId(), resolver );
        }
    }

    void start() {
        try {
            participantManager.getMessagingService().registerMessageHandlerFactory( Message.MessageType.USER_DEFINE_MSG.toString(), new MessageHandlerResolverWrapper().convert() );
            participantManager.getStateMachineEngine().registerStateModelFactory( "LeaderStandby", new LockTransitionalFactory() );
            participantManager.connect();
            offlinePartition();
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    String getNodeStatus() {
        final String partition = resourceName + "_0";
        final ExternalView view = getResourceExternalView();
        if ( clusterIsNotSetYet( view, partition ) ) {
            return "OFFLINE";
        }
        final Map<String, String> stateMap = view.getStateMap( partition );
        return stateMap.get( instanceName );
    }

    ExternalView getResourceExternalView() {
        return participantManager.getClusterManagmentTool().getResourceExternalView( clusterName, resourceName );
    }

    private boolean clusterIsNotSetYet( ExternalView view,
                                        String partition ) {
        //first start with fresh setup
        if ( view == null ) {
            return true;
        }
        final Map<String, String> stateMap = view.getStateMap( partition );

        return stateMap == null || stateMap.get( instanceName ) == null;
    }

    @Override
    public void dispose() {
        if ( participantManager != null && participantManager.isConnected() ) {
            participantManager.disconnect();
        }
    }

    @Override
    public void onStart( final Runnable runnable ) {
        runnable.run();
    }

    @Override
    public int getHoldCount() {
        return lock.getHoldCount();
    }

    private void offlinePartition() {
        if ( "OFFLINE".equals( getNodeStatus() ) ) {
            return;
        }
        participantManager.getClusterManagmentTool().enablePartition( false, clusterName, instanceName, resourceName, asList( resourceName + "_0" ) );
        while ( !"OFFLINE".equals( getNodeStatus() ) ) {
            try {
                Thread.sleep( 10 );
            } catch ( InterruptedException e ) {
            }
        }
    }

    private void enablePartition() {
        if ( "LEADER".equals( getNodeStatus() ) ) {
            return;
        }
        participantManager.getClusterManagmentTool().enablePartition( true, clusterName, instanceName, resourceName, asList( resourceName + "_0" ) );
        while ( !"LEADER".equals( getNodeStatus() ) ) {
            try {
                Thread.sleep( 10 );
            } catch ( InterruptedException e ) {
            }
        }
    }

    private void disablePartition() {
        String nodeStatus = getNodeStatus();
        if ( "STANDBY".equals( nodeStatus ) || "OFFLINE".equals( nodeStatus ) ) {
            return;
        }
        participantManager.getClusterManagmentTool().enablePartition( false, clusterName, instanceName, resourceName, asList( resourceName + "_0" ) );

        while ( !( "STANDBY".equals( nodeStatus ) || "OFFLINE".equals( nodeStatus ) ) ) {
            try {
                Thread.sleep( 10 );
                nodeStatus = getNodeStatus();
            } catch ( InterruptedException e ) {
            }
        }
    }

    @Override
    public void lock() {
        lock.lock();

        enablePartition();
    }

    @Override
    public void unlock() {
        disablePartition();

        lock.unlock();
    }

    @Override
    public void broadcastAndWait( final String serviceId,
                                  final MessageType type,
                                  final Map<String, String> content,
                                  int timeOut ) {
        participantManager.getMessagingService().sendAndWait( buildCriteria(), buildMessage( serviceId, type, content ), new org.apache.helix.messaging.AsyncCallback( timeOut ) {
            @Override
            public void onTimeOut() {
            }

            @Override
            public void onReplyMessage( final Message message ) {
            }
        }, timeOut );
    }

    @Override
    public void broadcastAndWait( final String serviceId,
                                  final MessageType type,
                                  final Map<String, String> content,
                                  final int timeOut,
                                  final AsyncCallback callback ) {
        int msg = participantManager.getMessagingService().sendAndWait( buildCriteria(), buildMessage( serviceId, type, content ), new org.apache.helix.messaging.AsyncCallback() {
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
    public void broadcast( final String serviceId,
                           final MessageType type,
                           final Map<String, String> content ) {
        participantManager.getMessagingService().send( buildCriteria(), buildMessage( serviceId, type, content ) );
    }

    @Override
    public void broadcast( final String serviceId,
                           final MessageType type,
                           final Map<String, String> content,
                           final int timeOut,
                           final AsyncCallback callback ) {
        participantManager.getMessagingService().send( buildCriteria(), buildMessage( serviceId, type, content ), new org.apache.helix.messaging.AsyncCallback() {
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
    public void sendTo( final String serviceId,
                        final String resourceId,
                        final MessageType type,
                        final Map<String, String> content ) {
        participantManager.getMessagingService().send( buildCriteria( resourceId ), buildMessage( serviceId, type, content ) );
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

    private Message buildMessage( final String serviceId,
                                  final MessageType type,
                                  final Map<String, String> content ) {
        return new Message( Message.MessageType.USER_DEFINE_MSG, randomUUID().toString() ) {{
            setMsgState( Message.MessageState.NEW );
            getRecord().setMapField( "content", content );
            getRecord().setSimpleField( "serviceId", serviceId );
            getRecord().setSimpleField( "type", type.toString() );
            getRecord().setSimpleField( "origin", instanceName );
        }};
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE + 200;
    }

    class MessageHandlerResolverWrapper {

        MessageHandlerFactory convert() {
            return new MessageHandlerFactory() {

                @Override
                public MessageHandler createHandler( final Message message,
                                                     final NotificationContext context ) {

                    return new MessageHandler( message, context ) {
                        @Override
                        public HelixTaskResult handleMessage() throws InterruptedException {
                            try {
                                final String serviceId = _message.getRecord().getSimpleField( "serviceId" );
                                final MessageType type = buildMessageType( _message.getRecord().getSimpleField( "type" ) );
                                final Map<String, String> map = getMessageContent( _message );

                                final MessageHandlerResolver resolver = messageHandlerResolver.get( serviceId );

                                if ( resolver == null ) {
                                    System.err.println( "serviceId not found '" + serviceId + "'" );
                                    return new HelixTaskResult() {{
                                        setSuccess( false );
                                        setMessage( "Can't find resolver" );
                                    }};
                                }

                                final org.uberfire.commons.message.MessageHandler handler = resolver.resolveHandler( serviceId, type );

                                if ( handler == null ) {
                                    System.err.println( "handler not found for '" + serviceId + "' and type '" + type.toString() + "'" );
                                    return new HelixTaskResult() {{
                                        setSuccess( false );
                                        setMessage( "Can't find handler." );
                                    }};
                                }

                                final Pair<MessageType, Map<String, String>> result = handler.handleMessage( type, map );

                                if ( result == null ) {
                                    return new HelixTaskResult() {{
                                        setSuccess( true );
                                    }};
                                }

                                return new HelixTaskResult() {{
                                    setSuccess( true );
                                    getTaskResultMap().put( "serviceId", serviceId );
                                    getTaskResultMap().put( "type", result.getK1().toString() );
                                    getTaskResultMap().put( "origin", instanceName );
                                    for ( Map.Entry<String, String> entry : result.getK2().entrySet() ) {
                                        getTaskResultMap().put( entry.getKey(), entry.getValue() );
                                    }
                                }};
                            } catch ( final Throwable e ) {
                                logger.error( "Error while processing cluster message", e );
                                return new HelixTaskResult() {{
                                    setSuccess( false );
                                    setMessage( e.getMessage() );
                                    setException( new RuntimeException( e ) );
                                }};
                            }
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
                if ( !field.getKey().equals( "serviceId" ) && !field.getKey().equals( "origin" ) && !field.getKey().equals( "type" ) ) {
                    put( field.getKey(), field.getValue() );
                }
            }
        }};
    }
}