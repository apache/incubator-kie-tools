package org.kie.workbench.common.services.backend.rulename;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class RuleNamesServiceImpl
        implements RuleNamesService {

    private ProjectService projectService;
    private RuleNameObserver ruleNameObserver;

    // List of available rule names per project and package
    private final Map<Project, Map<String, Collection<String>>> ruleNames = new HashMap<Project, Map<String, Collection<String>>>();

    public RuleNamesServiceImpl() {
        // Boilerplate sacrifice for Weld
    }

    @Inject
    public RuleNamesServiceImpl(final ProjectService projectService, RuleNameObserver ruleNameObserver) {
        this.projectService = projectService;
        this.ruleNameObserver = ruleNameObserver;
    }

    @Override
    public Collection<String> getRuleNames(
            final Path path,
            final String packageName) {

        final Project project = projectService.resolveProject(path);

        if (project == null) {
            return Collections.emptyList();
        } else {
            return ruleNameObserver.getRuleNames(project, packageName);
        }
    }

}
