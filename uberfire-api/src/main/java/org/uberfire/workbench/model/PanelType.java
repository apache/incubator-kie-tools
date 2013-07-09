package org.uberfire.workbench.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum PanelType {
    ROOT_TAB,
    ROOT_LIST,
    ROOT_SIMPLE,
    ROOT_STATIC,
    SIMPLE,
    SIMPLE_DND,
    MULTI_TAB,
    MULTI_LIST,
    STATIC
}
