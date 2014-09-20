package org.kie.uberfire.plugin.exception;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PluginAlreadyExists extends RuntimeException {

    public PluginAlreadyExists() {
    }
}
