package org.kie.workbench.common.screens.explorer.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum Option {
    BUSINESS_CONTENT, TECHNICAL_CONTENT,
    TREE_NAVIGATOR, BREADCRUMB_NAVIGATOR,
    FLATTEN_DIR, COMPACT_EMPTY_DIR,
    INCLUDE_HIDDEN_ITEMS, EXCLUDE_HIDDEN_ITEMS
}
