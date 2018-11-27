/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.repositories.impl.git.event;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.stream.Stream;

/**
 * Notification type for a give FileSystemHook exitCode
 */
@Portable
public enum NotificationType {

    SUCCESS(0, 0), WARNING(1, 30), ERROR(31, Integer.MAX_VALUE);

    private int minValue;
    private int maxValue;

    NotificationType(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Determines if the given exitCode is in the range of the {@link NotificationType}
     * @param exitCode The exit code to check
     * @return True if the exit code is in the {@link NotificationType} false if not
     */
    boolean inRange(int exitCode) {
        return minValue <= exitCode && exitCode <= maxValue;
    }

    /**
     * Returns the {@link NotificationType} that matches a given exitCode
     * @param exitCode The exit code to get it's notification type
     * @return The {@link NotificationType} that has the exitCode in range, if not found it will return ERROR.
     */
    public static NotificationType fromExitCode(final int exitCode) {
        return Stream.of(NotificationType.values())
                .filter(type -> type.inRange(exitCode))
                .findAny()
                .orElse(ERROR);
    }
}
