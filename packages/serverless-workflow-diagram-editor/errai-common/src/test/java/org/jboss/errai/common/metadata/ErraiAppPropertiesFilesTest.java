package org.jboss.errai.common.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErraiAppPropertiesFilesTest {

  @Test
  public void testGetUrls() {
    final ClassLoader classLoader = ErraiAppPropertiesFilesTest.class.getClassLoader();
    final List<URL> urls = ErraiAppPropertiesFiles.getUrls(classLoader);

    // Errai Common has a properties this and Errai Common Test has other two.
    assertEquals(3, urls.size());
  }

  @Test
  public void testGetModuleUrls() {
    final List<URL> moduleUrls = ErraiAppPropertiesFiles.getModulesUrls();

    // Errai Common has a properties this and Errai Common Test has other two,
    // but since they together make two modules, only two URLs should be returned.
    assertEquals(2, moduleUrls.size());
  }

  @Test
  public void testGetModuleDirRootFile() throws MalformedURLException {
    final URL url = new URL("file:/foo/bar/ErraiApp.properties/");
    final String moduleDir = ErraiAppPropertiesFiles.getModuleDir(url);

    assertEquals("file:/foo/bar/", moduleDir);
  }

  @Test
  public void testGetModuleDirMetaInfFile() throws MalformedURLException {
    final URL url = new URL("file:/foo/bar/META-INF/ErraiApp.properties/");
    final String moduleDir = ErraiAppPropertiesFiles.getModuleDir(url);

    assertEquals("file:/foo/bar/", moduleDir);
  }

  @Test(expected = RuntimeException.class)
  public void testGetModuleDirWithInvalidFileName() throws MalformedURLException {
    final URL url = new URL("file:/foo/bar/META-INF/InvalidName.properties/");
    ErraiAppPropertiesFiles.getModuleDir(url);
  }
}
