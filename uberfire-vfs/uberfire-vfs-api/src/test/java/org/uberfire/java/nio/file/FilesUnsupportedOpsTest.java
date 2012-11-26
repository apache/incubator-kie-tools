/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.file;

import java.util.Collections;

import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.attribute.PosixFilePermission;
import org.uberfire.java.nio.file.attribute.UserPrincipal;

public class FilesUnsupportedOpsTest extends AbstractBaseTest {

    @Test(expected = UnsupportedOperationException.class)
    public void newDirectoryStreamGlob() {
        Files.newDirectoryStream(newTempDir(), "*.*");
    }

    @Test(expected = NotDirectoryException.class)
    public void newDirectoryStreamGlobNotDirectoryException() {
        Files.newDirectoryStream(Files.createTempFile("foo", "bar"), "*.*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamGlobNull() {
        Files.newDirectoryStream(null, (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamGlobNull2() {
        Files.newDirectoryStream(newTempDir(), (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamGlobNull3() {
        Files.newDirectoryStream(null, "*.*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamGlobEmpty() {
        Files.newDirectoryStream(newTempDir(), "");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void probeContentType() {
        Files.probeContentType(Files.createTempFile(null, null));
    }

    @Test(expected = NoSuchFileException.class)
    public void probeContentTypeDir() {
        Files.probeContentType(newTempDir());
    }

    @Test(expected = NoSuchFileException.class)
    public void probeContentTypeNonExistent() {
        Files.probeContentType(Paths.get("/path/to/some/plaxe.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void probeContentTypeNull() {
        Files.probeContentType(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getPosixFilePermissions() {
        Files.getPosixFilePermissions(newTempDir());
    }

    @Test(expected = NoSuchFileException.class)
    public void getPosixFilePermissionsNoSuchFileException() {
        Files.getPosixFilePermissions(Paths.get("/some/path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPosixFilePermissionsNull() {
        Files.getPosixFilePermissions(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getOwner() {
        Files.getOwner(newTempDir());
    }

    @Test(expected = NoSuchFileException.class)
    public void getOwnerNoSuchFileException() {
        Files.getOwner(Paths.get("/some/path"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOwnerNull() {
        Files.getOwner(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setOwner() {
        Files.setOwner(newTempDir(), new UserPrincipal() {
            @Override
            public String getName() {
                return "name";
            }
        });
    }

    @Test(expected = NoSuchFileException.class)
    public void setOwnerNoSuchFileException() {
        Files.setOwner(Paths.get("/some/path"), new UserPrincipal() {
            @Override
            public String getName() {
                return "name";
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOwnerNull() {
        Files.setOwner(newTempDir(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOwnerNull2() {
        Files.setOwner(null, new UserPrincipal() {
            @Override
            public String getName() {
                return "name";
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void setOwnerNull3() {
        Files.setOwner(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setPosixFilePermissions() {
        Files.setPosixFilePermissions(newTempDir(), Collections.<PosixFilePermission>emptySet());
    }

    @Test(expected = NoSuchFileException.class)
    public void setPosixFilePermissionsNoSuchFileException() {
        Files.setPosixFilePermissions(Paths.get("/some/path"), Collections.<PosixFilePermission>emptySet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPosixFilePermissionsNull() {
        Files.setPosixFilePermissions(newTempDir(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPosixFilePermissionsNull2() {
        Files.setPosixFilePermissions(null, Collections.<PosixFilePermission>emptySet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull() {
        Files.newDirectoryStream(null);
    }

    @Test(expected = NotDirectoryException.class)
    public void newDirectoryStreamNonDirecotory() {
        Files.newDirectoryStream(Files.createTempFile("foo", "bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull2() {
        Files.newDirectoryStream(null, new DirectoryStream.Filter<Path>() {
            @Override public boolean accept(Path entry) throws IOException {
                return true;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull3() {
        Files.newDirectoryStream(newTempDir(), (DirectoryStream.Filter<Path>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull4() {
        Files.newDirectoryStream(null, (DirectoryStream.Filter<Path>) null);
    }

    @Test(expected = NotDirectoryException.class)
    public void newDirectoryStreamNotDirecotory2() {
        Files.newDirectoryStream(Files.createTempFile("foo", "bar"), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return false;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull5() {
        Files.newDirectoryStream(null, "*.*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull6() {
        Files.newDirectoryStream(newTempDir(), (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamEmpty() {
        Files.newDirectoryStream(newTempDir(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull7() {
        Files.newDirectoryStream(null, (String) null);
    }

    @Test(expected = NotDirectoryException.class)
    public void newDirectoryStreamNotDirecotory3() {
        Files.newDirectoryStream(Files.createTempFile("foo", "bar"), "*.*");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createSymbolicLink() {
        Files.createSymbolicLink(Paths.get("/path/some/place.link"), Files.createTempFile("foo", "bar"));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createSymbolicLinkIllegalStateException1() {
        Files.createSymbolicLink(Files.createTempFile("foo", "bar"), Files.createTempFile("foo", "bar"));
    }

    @Test(expected = IllegalStateException.class)
    public void createSymbolicLinkIllegalStateException2() {
        final Path path = Files.createTempFile("foo", "bar");
        Files.createSymbolicLink(path, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull1() {
        Files.createSymbolicLink(null, Files.createTempFile("foo", "bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull2() {
        Files.createSymbolicLink(Paths.get("/path/some/place.link"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull3() {
        Files.createSymbolicLink(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createLink() {
        Files.createLink(Paths.get("/path/some/place.link"), Files.createTempFile("foo", "bar"));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createLinkIllegalStateException1() {
        Files.createLink(Files.createTempFile("foo", "bar"), Files.createTempFile("foo", "bar"));
    }

    @Test(expected = IllegalStateException.class)
    public void createLinkIllegalStateException2() {
        final Path path = Files.createTempFile("foo", "bar");
        Files.createLink(path, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull1() {
        Files.createLink(null, Files.createTempFile("foo", "bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull2() {
        Files.createLink(Paths.get("/path/some/place.link"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull3() {
        Files.createSymbolicLink(null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readSymbolicLink() {
        Files.readSymbolicLink(Files.createTempFile("foo", "bar"));
    }

    @Test(expected = NotLinkException.class)
    public void readSymbolicLinkNotLink() {
        Files.readSymbolicLink(Paths.get("/some/file/link.lnk"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readSymbolicLinkNull() {
        Files.readSymbolicLink(null);
    }


//walkFileTree
}
