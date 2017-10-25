/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.source.git.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.source.git.config.GitConfig;

public class GitConfigImpl implements GitConfig,
                                      CloneableConfig<GitConfig> {

    private String outPath;
    private String branch;
    private String origin;
    private String repoName;
    private String createRepo;

    public GitConfigImpl() {
        this.outPath = GitConfig.super.getOutPath();
        this.branch = GitConfig.super.getBranch();
        this.origin = GitConfig.super.getOrigin();
        this.repoName = GitConfig.super.getRepoName();
        this.createRepo = GitConfig.super.getCreateRepo();
    }

    public GitConfigImpl(final String outPath,
                         final String branch,
                         final String origin,
                         final String repoName,
                         final String createRepo) {
        this.outPath = outPath;
        this.branch = branch;
        this.origin = origin;
        this.repoName = repoName;
        this.createRepo = createRepo;
    }

    @Override
    public String getOutPath() {
        return outPath;
    }

    @Override
    public String getBranch() {
        return branch;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public String getRepoName() {
        return repoName;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getCreateRepo() {
        return createRepo;
    }

    public void setCreateRepo(String createRepo) {
        this.createRepo = createRepo;
    }

    @Override
    public String toString() {
        return "GitConfigImpl{" + "outPath=" + outPath + ", branch=" + branch + ", origin=" + origin + ", repoName=" + repoName + ", createRepo=" + createRepo + '}';
    }

    @Override
    public GitConfig asNewClone(final GitConfig source) {
        return new GitConfigImpl(source.getOutPath(),
                                 source.getBranch(),
                                 source.getOrigin(),
                                 source.getRepoName(),
                                 source.getCreateRepo());
    }
}
