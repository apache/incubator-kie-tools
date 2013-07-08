package org.uberfire.workbench.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum PanelType {
    ROOT_TAB,
    ROOT_LIST,
    ROOT_STACK,
    ROOT_SIMPLE,
    SIMPLE,
    MULTI_TAB,
    MULTI_LIST,
    MULTI_STACK,
    STATIC
}
