package org.kie.workbench.common.services.datamodeller.driver;

import org.jboss.forge.roaster.model.JavaType;
import org.kie.workbench.common.services.datamodeller.core.DataModel;

/**
 * Used for vetoing types from being loaded by the DataModelerService. Any CDI beans available at deployment time will be used,
 * and a type is vetoed if any single {@link SourceFilter} vetos it.
 */
@FunctionalInterface
public interface SourceFilter {

    /**
     * Check if the given type is vetoed by this filter.
     * @param javaType A Java type that could be processed as a {@link DataModel}.
     * @return True if this type should be vetoed.
     */
    boolean veto( JavaType<?> javaType );
}
