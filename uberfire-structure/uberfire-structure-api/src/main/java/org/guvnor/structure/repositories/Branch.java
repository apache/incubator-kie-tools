package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class Branch {

    private String name;
    private Path path;

    public Branch() {
    }

    public Branch(final String name,
                  final Path path) {
        this.name = checkNotNull("name",
                                 name);
        this.path = checkNotNull("path",
                                 path);
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Branch branch = (Branch) o;

        if (name != null ? !name.equals(branch.name) : branch.name != null) {
            return false;
        }
        return path != null ? path.equals(branch.path) : branch.path == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? ~~name.hashCode() : 0;
        result = 31 * result + (path != null ? ~~path.hashCode() : 0);
        return ~~result;
    }
}
