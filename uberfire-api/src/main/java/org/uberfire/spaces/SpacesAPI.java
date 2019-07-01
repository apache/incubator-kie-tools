package org.uberfire.spaces;

import java.net.URI;
import java.util.Optional;

import com.google.gwt.core.shared.GwtIncompatible;

public interface SpacesAPI {

    String DEFAULT_SPACE_NAME = "system";
    Space DEFAULT_SPACE = new Space(DEFAULT_SPACE_NAME);

    String DASHBUILDER_SPACE_NAME = "dashbuilder";
    Space DASHBUILDER_SPACE = new Space(DASHBUILDER_SPACE_NAME);

    String CONFIG_FOLDER_NAME = ".config";
    String CONFIG_REPOSITORY_NAME = "config";

    static String resolveSpacePath(final Scheme scheme,
                                   final String spaceName) {
        return scheme + "://" + spaceName + "/";
    }

    static String resolveFileSystemPath(final Scheme scheme,
                                        final Space space,
                                        final String fsName) {
        return scheme + "://" + space.getName() + "/" + fsName;
    }

    static String resolveConfigFileSystemPath(final Scheme scheme,
                                              final String spaceName) {
        return resolveSpacePath(scheme, spaceName) + CONFIG_FOLDER_NAME + "/" + CONFIG_REPOSITORY_NAME;
    }

    @GwtIncompatible
    public URI resolveFileSystemURI(Scheme scheme,
                                    Space space,
                                    String fsName);

    static String sanitizeFileSystemName(final String fileSystemName) {
        // Only [A-Za-z0-9_\-.] are valid so strip everything else out
        return fileSystemName != null ? fileSystemName.replaceAll("[^A-Za-z0-9_\\-.]",
                                                                  "") : fileSystemName;
    }

    Space getSpace(String name);

    default Space getDefaultSpace() {
        return DEFAULT_SPACE;
    }

    Optional<Space> resolveSpace(String uri);

    enum Scheme {
        DEFAULT("default"),
        GIT("git"),
        FILE("file");

        private final String name;

        Scheme(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
