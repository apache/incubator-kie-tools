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

package org.drools.guvnor.server.impl;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.vfs.FileSystem;
import org.drools.guvnor.vfs.JGitRepositoryConfigurationVO;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.backend.VFSService;
import org.drools.guvnor.vfs.impl.DirectoryStreamImpl;
import org.drools.guvnor.vfs.impl.FileSystemImpl;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileSystemAlreadyExistsException;
import org.drools.java.nio.file.FileSystems;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.Paths;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.ProviderNotFoundException;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.drools.java.nio.fs.file.JGitRepositoryConfiguration;
import org.jboss.errai.bus.server.annotations.Service;

import static java.util.Arrays.*;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Map<String, JGitRepositoryConfiguration> repositories = new HashMap<String, JGitRepositoryConfiguration>();

    @PostConstruct
    public void init() throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, SecurityException, java.io.IOException {
        loadJGitConfiguration();
    }

    @Override
    public Path get(final String first, final String... more) throws IllegalArgumentException {
        return convert(Paths.get(first, more));
    }

    @Override
    public Path get(final Path path) throws IllegalArgumentException {
        return convert(Paths.get(URI.create(path.toURI())));
    }

    //TODO xxx
    private void loadJGitConfiguration() throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException, SecurityException, java.io.IOException {
        //TODO: load guvnorng-configuration git repo configuration info from a property file.
        String repositoryName = "guvnorng-configuration";
        String gitURL = "https://github.com/guvnorngtestuser1/guvnorng-configuration.git";
        String userName = "guvnorngtestuser1";
        String password = "test1234";

        //this will create guvnorng-configuration git repo if it does not exist yet
        cloneJGitFileSystem(repositoryName, gitURL, userName, password);

        PathImpl p = new PathImpl("jgit:///guvnorng-configuration");
        DirectoryStream<Path> response = newDirectoryStream(p);
        for (final Path path : response) {
            String fileName = path.getFileName();
            if (fileName.endsWith(".properties")) {
                JGitRepositoryConfiguration jGitRepositoryConfiguration = new JGitRepositoryConfiguration();
                InputStream is = Files.newInputStream(fromPath(path), null);
                Properties properties = new Properties();
                properties.load(is);

                //Clone or fetch the repo
                cloneJGitFileSystem(properties.getProperty("repositoryname"), properties.getProperty("giturl"), properties.getProperty("username"), properties.getProperty("password"));

                //Add this newly created repository configuration info to the in-memory cache of repository configuration list
                jGitRepositoryConfiguration.setRepositoryName(properties.getProperty("repositoryname"));
                jGitRepositoryConfiguration.setGitURL(properties.getProperty("giturl"));
                jGitRepositoryConfiguration.setUserName(properties.getProperty("username"));
                jGitRepositoryConfiguration.setPassword(properties.getProperty("password"));
                jGitRepositoryConfiguration.setRootURI(URI.create(properties.getProperty("rooturi")));
                addRepositoryConfiguration(jGitRepositoryConfiguration.getRepositoryName(), jGitRepositoryConfiguration);
            }
        }
    }

    private void cloneJGitFileSystem(String repositoryName, String gitURL, String userName, String password) {
        if (getRepositoryConfiguration().get(repositoryName) != null) {
            throw new FileSystemAlreadyExistsException("JGitFileSystem identifed by repositoryName: " + repositoryName + " already exists");
        }

        //Create the JGitFileSystem
        Map<String, String> env = new HashMap<String, String>();
        env.put("username", userName);
        env.put("password", password);
        env.put("giturl", gitURL);
        URI rootURI = URI.create("jgit:///" + repositoryName);

        try {
            //This either clones the repository or creates a new git repository (locally). 
            org.drools.java.nio.file.FileSystem fileSystem = FileSystems.newFileSystem(rootURI, env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Add this newly created repository configuration info to the in-memory cache of repository configuration list
        JGitRepositoryConfiguration jGitRepositoryConfiguration = new JGitRepositoryConfiguration();
        jGitRepositoryConfiguration.setRepositoryName(repositoryName);
        jGitRepositoryConfiguration.setUserName(userName);
        jGitRepositoryConfiguration.setPassword(password);
        jGitRepositoryConfiguration.setRootURI(rootURI);
        addRepositoryConfiguration(repositoryName, jGitRepositoryConfiguration);

        //TODO:Save this newly created repository's configuration info to guvnorng-config git repository using a property file whose name is "${repositoryName}.properties"
        Properties properties = new Properties();
        properties.setProperty("repositoryname", repositoryName);
        properties.setProperty("username", userName);
        properties.setProperty("password", password);
        properties.setProperty("rooturi", rootURI.toString());

        //return fileSystem;
    }

    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, Object> env)
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        return newFileSystem(path.toURI(), env);
    }

    @Override
    public FileSystem newFileSystem(final String uri, final Map<String, Object> env)
            throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        final org.drools.java.nio.file.FileSystem newFileSystem;
        System.out.println("ABOUT TO CREA A NEW FILE SYSTEM!");
        try {
            newFileSystem = FileSystems.newFileSystem(URI.create(uri), env);
        } catch (java.io.IOException e) {
            throw new RuntimeException();
        }

        return new FileSystemImpl(asList(new Path[]{new PathImpl(uri)}));
    }

    public void createJGitFileSystem(String repositoryName, String description, String userName, String password) {
        if (getRepositoryConfiguration().get(repositoryName) != null) {
            throw new FileSystemAlreadyExistsException("JGitFileSystem identifed by repositoryName: " + repositoryName + " already exists");
        }

        //Create the JGitFileSystem
        Map<String, String> env = new HashMap<String, String>();
        env.put("userName", userName);
        env.put("password", password);
        URI rootURI = URI.create("jgit:///" + repositoryName);

        try {
            //This either clones the repository or creates a new git repository (locally). 
            org.drools.java.nio.file.FileSystem fileSystem = FileSystems.newFileSystem(rootURI, env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Add this newly created repository configuration info to the in-memory cache of repository configuration list
        JGitRepositoryConfiguration jGitRepositoryConfiguration = new JGitRepositoryConfiguration();
        //jGitRepositoryConfiguration.setGitURL(gitURL);
        jGitRepositoryConfiguration.setRepositoryName(repositoryName);
        jGitRepositoryConfiguration.setUserName(userName);
        jGitRepositoryConfiguration.setPassword(password);
        jGitRepositoryConfiguration.setRootURI(rootURI);
        addRepositoryConfiguration(repositoryName, jGitRepositoryConfiguration);

        //TODO:Save this newly created repository's configuration info to guvnorng-config git repository using a property file whose name is "${repositoryName}.properties"
        Properties properties = new Properties();
        properties.setProperty("repositoryname", repositoryName);
        //properties.setProperty("giturl", gitURL);
        properties.setProperty("username", userName);
        properties.setProperty("password", password);
        properties.setProperty("rooturi", rootURI.toString());
    }

    public List<JGitRepositoryConfigurationVO> listJGitRepositories() {
        Map<String, JGitRepositoryConfiguration> repo = getRepositoryConfiguration();
        List<JGitRepositoryConfigurationVO> result = new ArrayList<JGitRepositoryConfigurationVO>();

        for (JGitRepositoryConfiguration j : repo.values()) {
            JGitRepositoryConfigurationVO jGitRepositoryConfigurationVO = new JGitRepositoryConfigurationVO();
            jGitRepositoryConfigurationVO.setGitURL(j.getGitURL());
            jGitRepositoryConfigurationVO.setRepositoryName(j.getRepositoryName());
            jGitRepositoryConfigurationVO.setRootURI(j.getRootURI().toString());

            result.add(jGitRepositoryConfigurationVO);
        }

        return result;
    }

    @Override
    public JGitRepositoryConfigurationVO loadJGitRepository(String repositoryName) {
        Map<String, JGitRepositoryConfiguration> repo = getRepositoryConfiguration();
        JGitRepositoryConfiguration j = repo.get(repositoryName);

        JGitRepositoryConfigurationVO jGitRepositoryConfigurationVO = new JGitRepositoryConfigurationVO();
        jGitRepositoryConfigurationVO.setGitURL(j.getGitURL());
        jGitRepositoryConfigurationVO.setRepositoryName(j.getRepositoryName());
        jGitRepositoryConfigurationVO.setRootURI(j.getRootURI().toString());

        return jGitRepositoryConfigurationVO;
    }

//    public List<FileSystem> listJGitFileSystems() {
//        List<FileSystem> fileSystems = new ArrayList<FileSystem>();
//
//        Map<String, JGitRepositoryConfiguration> repositories = getRepositoryConfiguration();
//        for (String repositoryName : repositories.keySet()) {
//            URI uri = URI.create("jgit:///" + repositoryName);
//            FileSystem f = FileSystems.getFileSystem(uri);
//            fileSystems.add(f);
//        }
//
//        return fileSystems;
//    }

    private Map<String, JGitRepositoryConfiguration> getRepositoryConfiguration() {
        return repositories;
    }

    private void addRepositoryConfiguration(String repositoryName, JGitRepositoryConfiguration j) {
        repositories.put(repositoryName, j);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream()
            throws IllegalArgumentException, NotDirectoryException, IOException {
        Path p = new PathImpl("jgit:///guvnorng");

        return newDirectoryStream(Files.newDirectoryStream(fromPath(p)).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir) throws IllegalArgumentException, NotDirectoryException, IOException {
        return newDirectoryStream(Files.newDirectoryStream(fromPath(dir)).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IllegalArgumentException, NotDirectoryException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createLink(Path link, Path existing) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(Path path) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteIfExists(Path path) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path readSymbolicLink(Path link) throws IllegalArgumentException, UnsupportedOperationException, NotLinkException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String probeContentType(Path path) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> readAttributes(final Path path) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return Files.readAttributes(fromPath(path), "*", null);
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long size(Path path) throws IllegalArgumentException, IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean notExists(Path path, LinkOption... options) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IllegalArgumentException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExecutable(Path path) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String readAllString(Path path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, Charset.forName(charset));
    }

    @Override
    public String readAllString(final Path path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, UTF_8);
    }

    private String readAllString(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException {

        final List<String> result = Files.readAllLines(fromPath(path), cs);
        if (result == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : result) {
            sb.append(s).append('\n');
        }
        return sb.toString();

    }

    @Override
    public List<String> readAllLines(Path path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> readAllLines(Path path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public Path write(Path path, byte[] bytes) throws IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, Iterable<? extends CharSequence> lines, String charset) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, Iterable<? extends CharSequence> lines) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public Path write(Path path, String content, String charset) throws IllegalArgumentException, IOException, UnsupportedOperationException {
//        return convert(Files.write(fromPath(path), content, Charset.forName(charset), null));
//    }

    @Override
    public Path write(Path path, String content) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert(Files.write(fromPath(path), content, UTF_8, null));
    }

    private Path convert(final org.drools.java.nio.file.Path path) {
        final Map<String, Object> attributes = Files.readAttributes(path, "*");

        //REVISIT - JLIU: Path.toUri constructs an absolute URI with a scheme equal to the URI scheme that identifies the provider. In order to support
        //relative path, shall we avoid using Path.toUri? or shall we always use absolute path?
        return new PathImpl(path.getFileName().toString(), path.toString(), attributes);
        //return new PathImpl(path.getFileName().toString(), path.toUri().toString(), attributes);
    }

    private DirectoryStream<Path> newDirectoryStream(final Iterator<org.drools.java.nio.file.Path> iterator) {
        final List<Path> content = new LinkedList<Path>();
        while (iterator.hasNext()) {
            content.add(convert(iterator.next()));
        }
        return new DirectoryStreamImpl(content);
    }

    private org.drools.java.nio.file.Path fromPath(final Path path) {
        //HACK: REVISIT: how to encode. We dont want to encode the whole URI string, we only want to encode the path element
        String pathString = path.toURI();
        pathString = pathString.replaceAll(" ", "%20");
        return Paths.get(URI.create(pathString));
    }
}
