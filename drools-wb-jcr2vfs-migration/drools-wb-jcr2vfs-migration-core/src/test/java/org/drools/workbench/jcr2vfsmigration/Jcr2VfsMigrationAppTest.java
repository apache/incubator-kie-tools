package org.drools.workbench.jcr2vfsmigration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.drools.workbench.jcr2vfsmigration.vfs.IOServiceFactory;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertNotNull;

public class Jcr2VfsMigrationAppTest {

    @Test
    public void migrateMortgageExample() throws IOException {
        migrate("mortgageExample");
    }

    private void verifyResult() {

    }

    private void migrate(String datasetName) throws IOException {
        File testBaseDir = new File("target/test/" + datasetName);
        if (testBaseDir.exists()) {
            FileUtils.deleteDirectory(testBaseDir);
        }
        testBaseDir.mkdirs();
        testBaseDir = testBaseDir.getCanonicalFile();
        File inputJcrRepository = new File(testBaseDir, "inputJcr");
        inputJcrRepository.mkdir();
        unzip(getClass().getResource(datasetName + ".jcr.zip"), inputJcrRepository);
        File outputVfsRepository = new File(testBaseDir, "outputVfs");

        //Hack: Force JGitFileSystemProvider to reload git root dir due to JUnit class loader problem
        System.setProperty("org.kie.nio.git.dir", outputVfsRepository.getCanonicalPath());
        JGitFileSystemProvider.loadConfig();
        //Hack: Force to create a new FileSystem
        IOServiceFactory.DEFAULT_MIGRATION_FILE_SYSTEM = "drools-wb-jcr2vfs-migration";

        new Jcr2VfsMigrationApp().run(
                "-i", inputJcrRepository.getCanonicalPath(),
                "-o", outputVfsRepository.getCanonicalPath());
    }

    private void unzip(URL resource, File outputDir) throws IOException {
        assertNotNull(resource);
        File tmpFile = new File(outputDir, resource.getFile().replaceAll(".*/", ""));
        copyAndClose(resource.openStream(), new FileOutputStream(tmpFile));
        ZipFile zipFile = new ZipFile(tmpFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File entryDestination = new File(outputDir, entry.getName());
            entryDestination.getParentFile().mkdirs();
            if (entryDestination.isDirectory()) {
                entryDestination.mkdir();
            } else {
                copyAndClose(zipFile.getInputStream(entry), new FileOutputStream(entryDestination));
            }
        }
        tmpFile.delete();
    }

    private void copyAndClose(InputStream in, OutputStream out) throws IOException {
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

}
