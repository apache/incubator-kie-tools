/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package javax.enterprise.event;

/**
 * <p>Distinguishes the various kinds of transactional
 * {@linkplain javax.enterprise.event.Observes observer methods}
 * from regular observer methods which are notified immediately.</p>
 * <p/>
 * <p>Transactional observer methods are observer methods which receive
 * event notifications during the before or after completion phase of the
 * transaction in which the event was fired. If no transaction is in progress
 * when the event is fired, they are notified at the same time as other
 * observers.</p>
 *
 * @author Pete Muir
 * @author Gavin King
 */
public enum TransactionPhase {

  /**
   * <p>Identifies a regular observer method, called when the event
   * is fired.</p>
   */
  IN_PROGRESS,

  /**
   * <p>Identifies a before completion observer method, called during
   * the before completion phase of the transaction.</p>
   */
  BEFORE_COMPLETION,

  /**
   * <p>Identifies an after completion observer method, called during the
   * after completion phase of the transaction.</p>
   */
  AFTER_COMPLETION,

  /**
   * <p>Identifies an after failure observer method, called during the
   * after completion phase of the transaction, only when the transaction
   * fails.</p>
   */
  AFTER_FAILURE,

  /**
   * <p>Identifies an after success observer method, called during the
   * after completion phase of the transaction, only when the transaction
   * completes successfully.</p>
   */
  AFTER_SUCCESS

}
