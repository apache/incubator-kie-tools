package org.kie.workbench.common.services.shared.builder.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * An enum to define where a Type was defined
 */
@Portable
public enum TypeSource {
    JAVA_PROJECT, //Within the project
    JAVA_DEPENDENCY, //Within a dependency
    DECLARED //From DRL
}
