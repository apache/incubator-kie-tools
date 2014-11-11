package org.uberfire.ext.plugin.exception;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PluginAlreadyExists extends RuntimeException {

    public PluginAlreadyExists() {
    }
}
