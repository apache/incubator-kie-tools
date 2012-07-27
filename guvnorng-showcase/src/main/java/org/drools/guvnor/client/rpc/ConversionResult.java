/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.rpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * A single result of a conversion process
 */
public class ConversionResult
    implements
        Serializable {

    private static final long       serialVersionUID = 540L;

    private List<ConversionAsset>   newAssets        = new ArrayList<ConversionAsset>();

    private List<ConversionMessage> messages         = new ArrayList<ConversionMessage>();

    public boolean isConverted() {
        for ( ConversionMessage message : messages ) {
            if ( message.getMessageType() == ConversionMessageType.ERROR ) {
                return false;
            }
        }
        return true;
    }

    public void addNewAsset(ConversionAsset newAsset) {
        this.newAssets.add( newAsset );
    }

    public List<ConversionAsset> getNewAssets() {
        return this.newAssets;
    }

    public void addMessage(String message,
                           ConversionMessageType messageType) {
        messages.add( new ConversionMessage( message,
                                             messageType ) );
    }

    /**
     * Get all messages of all types
     * 
     * @return
     */
    public List<ConversionMessage> getMessages() {
        return messages;
    }

    /**
     * Get all messages of a particular type
     * 
     * @param messageType
     * @return
     */
    public List<ConversionMessage> getMessages(ConversionMessageType messageType) {
        List<ConversionMessage> messages = new ArrayList<ConversionMessage>();
        for ( ConversionMessage message : this.messages ) {
            if ( message.getMessageType() == messageType ) {
                messages.add( message );
            }
        }
        return messages;
    }

    /**
     * Types of message
     */
    public enum ConversionMessageType {
        INFO,
        WARNING,
        ERROR
    }

    /**
     * A message resulting from the conversion process
     */
    public static class ConversionMessage
        implements
            Serializable {

        private static final long     serialVersionUID = 540L;

        private String                message;

        private ConversionMessageType messageType;

        public ConversionMessage() {
        }

        public ConversionMessage(String message,
                                 ConversionMessageType messageType) {
            this.message = message;
            this.messageType = messageType;
        }

        public String getMessage() {
            return this.message;
        }

        public ConversionMessageType getMessageType() {
            return this.messageType;
        }

    }

    /**
     * A container for a new Asset created during the conversion process
     */
    public static class ConversionAsset
        implements
            Serializable {

        private static final long serialVersionUID = 540L;

        private String            uuid;
        private String            format;

        public ConversionAsset() {
        }

        public ConversionAsset(final String uuid,
                               final String format) {
            this.uuid = uuid;
            this.format = format;
        }

        public String getUUID() {
            return uuid;
        }

        public String getFormat() {
            return format;
        }

    }

}
