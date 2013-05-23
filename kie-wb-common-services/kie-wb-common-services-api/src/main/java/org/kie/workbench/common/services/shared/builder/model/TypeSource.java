package org.kie.workbench.common.services.shared.builder.model;

/**
 * An enum to define where a Type was defined
 */
public enum TypeSource {
    JAVA_PROJECT, //Within the project
    JAVA_DEPENDENCY, //Within a dependency
    DECLARED //From DRL
}
