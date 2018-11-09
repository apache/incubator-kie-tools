package org.guvnor.structure.backend.pom;

import java.util.List;
import java.util.Map;

import org.guvnor.structure.pom.DependencyType;
import org.guvnor.structure.pom.DynamicPomDependency;

/**
 * Behaviour to read deps from a jsonfile
 */
public interface PomJsonReader {

    Map<DependencyType, List<DynamicPomDependency>> readDeps();
}
