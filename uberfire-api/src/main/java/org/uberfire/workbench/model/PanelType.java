package org.uberfire.workbench.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * The types of panels that a PanelManager can create and manage.
 */
@Portable
public enum PanelType {

    /**
     * A root panel that behaves like a {@link PanelType#MULTI_TAB} panel.
     */
    ROOT_TAB,

    /**
     * A root panel that behaves like a {@link PanelType#MULTI_LIST} panel.
     */
    ROOT_LIST,

    /**
     * A root panel that behaves like a {@link PanelType#SIMPLE} panel.
     */
    ROOT_SIMPLE,

    /**
     * A root panel that behaves like a {@link PanelType#STATIC} panel.
     */
    ROOT_STATIC,

    /**
     * A panel with a title bar. Can contain one part at a time. The part's view fills the entire space not used up by
     * the title bar. Adding a new part replaces the existing part. Does not support drag-and-drop rearrangement of
     * parts.
     */
    SIMPLE,

    /**
     * A panel with a title bar and drag-and-drop support for moving parts to and from other drag-and-drop enabled
     * panels. Adding a new part replaces the existing part. The part's view fills the entire available space not used
     * up by the title bar.
     */
    SIMPLE_DND,

    /**
     * A panel with a tab bar that allows selecting among the parts it contains, and drag-and-drop for moving parts to
     * and from other drag-and-drop enabled panels. Only one part at a time is visible, and it fills the entire
     * available space not used up by the tab bar.
     */
    MULTI_TAB,

    /**
     * A panel with a title bar and drop-down list that allows selecting among the parts it contains, and drag-and-drop
     * for moving parts to and from other drag-and-drop enabled panels. Only one part at a time is visible, and it fills
     * the entire available space not used up by the title bar.
     */
    MULTI_LIST,

    /**
     * An undecorated panel that can contain one part at a time. The part's view fills the entire panel. Adding a new
     * part replaces the existing part. Does not support drag-and-drop rearrangement of parts.
     */
    STATIC,

    /**
     * Special panel type that must be used only with the <tt>uberfire-panel-manager-template</tt> module. Supports
     * perspectives and panels whose layouts are defined by an Errai UI {@code @Templated} class.
     */
    TEMPLATE
}
