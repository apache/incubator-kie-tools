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

package org.uberfire.client.mvp;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.uberfire.workbench.events.UberFireEvent;

/**
 * CDI event fired by the framework each time an Activity lifecycle method throws an exception. Observers of the event
 * can use its methods to get information about the lifecycle call that failed, and can also ask the framework to
 * suppress the default error message.
 */
public class ActivityLifecycleError implements UberFireEvent {

    private final Activity failedActivity;
    private final LifecyclePhase failedCall;
    private boolean errorMessageSuppressed = false;
    private final Throwable exception;

    /**
     * The different activity lifecycle calls that can fail.
     */
    public enum LifecyclePhase {
        STARTUP, OPEN, CLOSE, SHUTDOWN;
    }

    ActivityLifecycleError( Activity failedActivity,
                            LifecyclePhase failedCall,
                            Throwable exception ) {
        this.failedActivity = checkNotNull( "failedActivity", failedActivity );
        this.failedCall = checkNotNull( "failedCall", failedCall );
        this.exception = exception;
    }

    /**
     * Returns the Activity instance that threw the exception.
     *
     * @return the Activity that failed a lifecycle call. Never null.
     */
    public Activity getFailedActivity() {
        return failedActivity;
    }

    /**
     * Tells which lifecycle phase failed.
     *
     * @return the lifecycle phase that failed to happen. Never null.
     */
    public LifecyclePhase getFailedCall() {
        return failedCall;
    }

    /**
     * Returns the exception thrown by the failed lifecycle method, if the failure was due to a thrown exception.
     *
     * @return the exception thrown by the failed lifecycle method. May be null.
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Tells the framework that it should not mention this failure in the workbench GUI. Once this method has been
     * invoked, there is no way to flip it back. Any such mechanism would not be reliable, because observers are not
     * called in a predictable order.
     */
    public void suppressErrorMessage() {
        errorMessageSuppressed = true;
    }

    /**
     * Tells whether a previous observer has requested that the standard error message in the GUI be suppressed. This is
     * only truly useful to the originator of the event, who can examine the value after all observers have been
     * notified. Application code should not rely on the return value of this method, because there is no guarantee what
     * order observers are called in.
     *
     * @return true if any observer has invoked the {@link #suppressErrorMessage()} method on this event .
     */
    public boolean isErrorMessageSuppressed() {
        return errorMessageSuppressed;
    }

}
