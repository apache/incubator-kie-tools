package org.kie.workbench.common.services.shared.rulename;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RuleNamesService {

    Collection<String> getRuleNames(final Path path,
            final String packageName);
}
