package org.uberfire.spaces;

import java.net.URI;
import java.util.Optional;

import com.google.gwt.core.shared.GwtIncompatible;

public interface SpacesAPI {

    String DEFAULT_SPACE_NAME = "system";
    Space DEFAULT_SPACE = new Space(DEFAULT_SPACE_NAME);


    static String resolveFileSystemPath(Scheme scheme,
                                        Space space,
                                        String fsName) {
        String uri = scheme + "://" + space.getName() + "/" + fsName;
        return uri;
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
