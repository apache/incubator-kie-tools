/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptAppender;
import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Paths;

/***
 * Class invoked through the main method, by the CompilerIPCCoordinatorImpl when the compile method is called to run a offProcess build
 */
public class ServerIPCImpl {

    private static Logger logger = LoggerFactory.getLogger(ServerIPCImpl.class);

    public static void main(String[] args) throws Exception {
        checksParamsNumber(args);
        String uuid = args[0];
        checksUUIDLength(uuid);
        String workingDir = args[1];
        checksWorkingDir(workingDir);
        String mavenRepo = args[2];
        checksMavenRepo(mavenRepo);
        String alternateSettingsAbsPath = args[3];
        checksSettingFile(alternateSettingsAbsPath);
        String queueName = args[4];
        checksQueueNameLenght(queueName);
        String threadName = Thread.currentThread().getName();
        QueueProvider provider = new QueueProvider(queueName, true);
        execute(workingDir, mavenRepo, alternateSettingsAbsPath, uuid, provider);
        Thread.currentThread().setName(threadName);// restore the previous name to avoid the override of the maven output
    }

    private static void checksQueueNameLenght(String queueName) {
        if(StringUtils.isEmpty(queueName) || queueName.length() < 5){
            logger.error("uuid too short, less than 5 chars:{}", queueName);
            throw new RuntimeException("uuid too short less than 5 chars:" + queueName);
        }
    }

    private static void checksMavenRepo(String mavenRepo) {
        if(!new File(mavenRepo).isDirectory()){
            logger.error("mavenRepo dir doesn't exists:{}",mavenRepo);
            throw new RuntimeException("MavenRepo dir  doesn't exists:"+mavenRepo);
        }
    }

    private static void checksUUIDLength(String uuid) {
        if(StringUtils.isEmpty(uuid) || uuid.length() < 10){
            logger.error("uuid too short, less than 10 chars:{}", uuid);
            throw new RuntimeException("uuid too short less than 10 chars:" + uuid);
        }
    }

    private static void checksSettingFile(String alternateSettingsAbsPath) {
        if(StringUtils.isNotEmpty(alternateSettingsAbsPath) && !new File(alternateSettingsAbsPath).exists()){
            logger.error("SettingsAbsPath doesn't exists:{}",alternateSettingsAbsPath);
            throw new RuntimeException("SettingsAbsPath doesn't exists:"+alternateSettingsAbsPath);
        }
    }

    private static void checksWorkingDir(String workingDir) {
        if(!new File(workingDir).exists()){
            logger.error("Working dir doesn't exists:{}",workingDir);
            throw new RuntimeException("Working dir doesn't exists:"+workingDir);
        }
    }

    private static void checksParamsNumber(String[] args) {
        if(args.length != 5){
            logger.error("Wrong number of params:{}",args.length);
            throw new RuntimeException("Wrong number of params:"+args.length);
        }
    }

    public static void execute(String workingDir, String mavenRepo, String alternateSettingsAbsPath, String uuid, QueueProvider provider) throws Exception {
        DefaultKieCompilationResponseOffProcess res = build(workingDir, mavenRepo, alternateSettingsAbsPath, uuid);
        byte[] bytez = serialize(res);
        if (bytez == null) {
            logger.warn("The serialized response is null, working dir:{}\n mavenrepo:{} \n alternateSettingsAbsPath:{} \n uuid:{}", workingDir, mavenRepo, alternateSettingsAbsPath, uuid);
            return;
        }
        writeOnQueue(bytez, provider);
    }

    private static void writeOnQueue(byte[] bytez, QueueProvider provider) {
        if(logger.isDebugEnabled()) {
            logger.debug("write On Queue");
        }
        ExcerptAppender appender = provider.getQueue().acquireAppender();
        appender.writeBytes(Bytes.allocateDirect(bytez));
        if(logger.isDebugEnabled()) {
            logger.debug("last index appended:{}", appender.lastIndexAppended());
        }
    }

    private static DefaultKieCompilationResponseOffProcess build(String prjPath, String mavenRepo, String alternateSettingsAbsPath, String uuid) {
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_LOG_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get("file://"+prjPath));
        CompilationRequest req;
        if (StringUtils.isNotEmpty(alternateSettingsAbsPath)) {
            req = new DefaultCompilationRequest(mavenRepo,
                                                info,
                                                new String[]{
                                                        MavenCLIArgs.DEPENDENCY_RESOLVE,
                                                        MavenCLIArgs.COMPILE,
                                                        MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                },
                                                Boolean.FALSE,
                                                uuid);
        } else {
            req = new DefaultCompilationRequest(mavenRepo,
                                                info,
                                                new String[]{
                                                        MavenCLIArgs.DEPENDENCY_RESOLVE,
                                                        MavenCLIArgs.COMPILE
                                                },
                                                Boolean.FALSE,
                                                uuid);
        }
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
        return new DefaultKieCompilationResponseOffProcess(res);
    }

    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }
}
