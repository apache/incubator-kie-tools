package org.kie.workbench.common.widgets.client.popups.file;

/**
 * ValueObject for details needed to copy or rename files
 */
public class FileNameAndCommitMessage {

    private final String newFileName;
    private final String commitMessage;

    public FileNameAndCommitMessage( final String newFileName,
                                     final String commitMessage ) {
        this.newFileName = newFileName;
        this.commitMessage = commitMessage;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public String getCommitMessage() {
        return commitMessage;
    }
}
