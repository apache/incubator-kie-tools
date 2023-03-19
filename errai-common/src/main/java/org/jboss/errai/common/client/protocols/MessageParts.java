/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.common.client.protocols;

/**
 * The parts comprising the core messaging protocol used by ErraiBus.
 * <p/>
 * As a general rule, you should avoid using the words reserved by this protocol.
 */
public enum MessageParts {
  /**
   * Specifies the specific command within the service that is being requested.  This is an optional element,
   * and is not required for signal-only services.  However it's use is encouraged for building multi-command
   * services.
   */
  CommandType,

  /**
   * Specifies a subject being referenced for use in an command.  This should not be confused with {@link #ToSubject},
   * which is used for message routing.
   */
  Subject,

  /**
   * A list of subjects.
   */
  SubjectsList,

  /**
   *
   * @since 3.0
   */
  RemoteServices,

  /**
   * A list of capabilities (comma-seperate string) of the capabilities of the remote bus.
   */
  CapabilitiesFlags,

  /**
   * The frequency with which short-polling clients should poll the remote bus.
   */
  PollFrequency,

  /**
   * Default value payload. Used mostly for higher-level APIs.
   */
  Value,

  /**
   * A unique identifier for identifying the session with which a message is associated.
   */
  SessionID,

  /**
   * Specifies any specific message text to be communicated as part of the command being sent.
   */
  MessageText,

  /**
   * Specifies what subject which should be replied-to in response to the message being sent.  Usually handled
   * automatically with conversations.
   */
  ReplyTo,

  /**
   * Specifies the intended recipient queue for the message.
   */
  ToSubject,

  /**
   * Specifies an error message.
   */
  ErrorMessage,

  /**
   * Additional details associated with an error message (for instance: stack trace information)
   */
  AdditionalDetails,

  /**
   * Specifies the subject to send error messages to.
   */
  ErrorTo,

  /**
   * The throwable object in case of an error
   */
  Throwable,

  /**
   * Specifies stack trace data in String form.
   */
  StackTrace,

  Reason,

  /**
   * If this attribute is present, the bus should give priority to processing it and not subject it to
   * window matching.
   */
  PriorityProcessing,


  /**
   * Used mainly for web sockets to negotiate which session the web socket is associated with.
   */
  ConnectionSessionKey,

  WebSocketURL,

  WebSocketToken
}
