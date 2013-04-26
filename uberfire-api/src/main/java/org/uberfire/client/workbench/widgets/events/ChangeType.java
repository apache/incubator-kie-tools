package org.uberfire.client.workbench.widgets.events;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Types of change that can happen to a Resource
 */
@Portable
public enum ChangeType {
    ADD,
    DELETE,
    UPDATE
}
