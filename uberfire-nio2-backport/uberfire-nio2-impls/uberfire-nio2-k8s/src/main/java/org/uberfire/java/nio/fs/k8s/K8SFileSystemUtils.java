/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent.Kind;
import org.uberfire.java.nio.fs.cloud.CloudClientConstants;

import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_FSOBJ_CONTENT_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_FSOBJ_NAME_PREFIX;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_LABEL_FSOBJ_APP_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.CFG_MAP_LABEL_FSOBJ_TYPE_KEY;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_APP_DEFAULT_VALUE;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_APP_PROPERTY_NAME;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_HIDDEN_FILE_INDICATOR;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_HIDDEN_FILE_INDICATOR_SUFFIX;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NAME_MAX_LENGTH;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemConstants.K8S_FS_NAME_RESTRICATION;
import static org.uberfire.java.nio.fs.k8s.K8SFileSystemObjectType.UNKNOWN;


public class K8SFileSystemUtils {

    public static final String APP_NAME = System.getProperty(K8S_FS_APP_PROPERTY_NAME, K8S_FS_APP_DEFAULT_VALUE);
    private static final Logger logger = LoggerFactory.getLogger(K8SFileSystemUtils.class);

    static Optional<ConfigMap> createOrReplaceParentDirFSCM(KubernetesClient client, 
                                                            Path self, 
                                                            long selfSize,
                                                            boolean isUpdateForFileDeletion) {
        String selfName = getFileNameString(self);
        Path parent = Optional.ofNullable(self.getParent()).orElseThrow(IllegalArgumentException::new);
        Map<String, String> parentContent = Optional.ofNullable(getFsObjCM(client, parent))
                .filter(K8SFileSystemUtils::isDirectory)
                .map(ConfigMap::getData)
                .orElseGet(HashMap::new);
        
        if (isUpdateForFileDeletion) {
            parentContent.remove(selfName);
        } else {
            parentContent.put(selfName, String.valueOf(selfSize));
        }
        final long parentSize = parentContent.values().stream().mapToLong(Long::parseLong).sum();

        return Optional.of(createOrReplaceFSCM(client, parent,
                                               isRoot(parent) ? Optional.empty()
                                                              : createOrReplaceParentDirFSCM(client, parent, parentSize, false),
                                               parentContent,
                                               true));
    }

    static ConfigMap createOrReplaceFSCM(KubernetesClient client,
                                         Path path,
                                         Optional<ConfigMap> parentOpt,
                                         Map<String, String> content,
                                         boolean isDir) {
        String fileName = getFileNameString(path);
        long size = 0;
        Map<String, String> labels = getFsObjNameElementLabel(path);
        if (isDir) {
            if (labels.isEmpty()) {
                labels.put(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, K8SFileSystemObjectType.ROOT.toString());
            } else {
                labels.put(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, K8SFileSystemObjectType.DIR.toString());
            }
            size = content.values().stream().mapToLong(Long::parseLong).sum();
        } else {
            labels.put(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, K8SFileSystemObjectType.FILE.toString());
            size = parentOpt.map(cm -> Long.parseLong(cm.getData().get(fileName)))
                            .orElseThrow(() -> new IllegalStateException("File [" +
                                                                         fileName +
                                                                         "] is not found at parent directory [" +
                                                                         path.getParent().toString() +
                                                                         "]"));
        }
        labels.put(CFG_MAP_LABEL_FSOBJ_APP_KEY, APP_NAME);
        
        Map<String, String> annotations = new ConcurrentHashMap<>();
        annotations.put(CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY, 
                        ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        annotations.put(CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY, String.valueOf(size));
        
        String cmName = Optional.ofNullable(getFsObjCM(client, path))
                .map(cm -> cm.getMetadata().getName())
                .orElseGet(() -> CFG_MAP_FSOBJ_NAME_PREFIX + UUID.randomUUID().toString());
        return parentOpt.map(parent -> client.configMaps().createOrReplace(new ConfigMapBuilder()
                                             .withNewMetadata()
                                               .withName(cmName)
                                               .withLabels(labels)
                                               .withAnnotations(annotations)
                                               .withOwnerReferences(new OwnerReferenceBuilder()
                                                 .withApiVersion(parent.getApiVersion())
                                                 .withKind(parent.getKind())
                                                 .withName(parent.getMetadata().getName())
                                                 .withUid(parent.getMetadata().getUid())
                                                 .build())
                                             .endMetadata()
                                             .withData(content)
                                             .build()))
                        .orElseGet(() -> client.configMaps().createOrReplace(new ConfigMapBuilder()
                                               .withNewMetadata()
                                                 .withName(cmName)
                                                 .withLabels(labels)
                                                 .withAnnotations(annotations)
                                               .endMetadata()
                                               .withData(content)
                                               .build()));
    }

    static boolean deleteAndUpdateParentCM(KubernetesClient client, Path path) {
        ConfigMap cm = getFsObjCM(client, path);
        if (cm != null && client.configMaps()
                                .withName(cm.getMetadata().getName())
                                .cascading(true)
                                .delete()) {
            Optional.ofNullable(path.getParent())
                    .ifPresent(p -> createOrReplaceParentDirFSCM(client, path, 0, true));
            return true;
        } else {
            return false;
        }
    }

    static ConfigMap getFsObjCM(KubernetesClient client, Path path) {
        int nameCount = path.getNameCount();
        Map<String, String> labels = getFsObjNameElementLabel(path);
        if (labels.isEmpty()) {
            labels.put(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, K8SFileSystemObjectType.ROOT.toString());
        } 
        labels.put(CFG_MAP_LABEL_FSOBJ_APP_KEY, APP_NAME);
        Object[] configMaps = client.configMaps()
                                           .withLabels(labels)
                                           .list()
                                           .getItems()
                                           .stream()
                                           .filter(cm -> cm.getMetadata()
                                                           .getLabels()
                                                           .entrySet()
                                                           .stream()
                                                           .filter(entry -> entry.getKey().startsWith(CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX))
                                                           .count() == nameCount)
                                           .toArray();
        
        if (configMaps.length > 1) {
            throw new IllegalStateException("Ambiguous K8S FileSystem object name: [" + path.toString() +
                                            "]; should not have be associated with more than one " +
                                            "K8S FileSystem ConfigMaps.");
        }
        if (configMaps.length == 1) {
            return (ConfigMap)configMaps[0];
        }
        return null;
    }

    static byte[] getFsObjContentBytes(ConfigMap cm) {
        byte[] content = new byte[0];
        try {
            content = cm.getData().get(CFG_MAP_FSOBJ_CONTENT_KEY).getBytes(CloudClientConstants.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Invalid encoding [{}], returns zero length byte array content.",
                        CloudClientConstants.ENCODING);
        } catch (Exception e) {
            logger.error("Retrieve content from FsOjbCM [{}] failed, due to", cm, e);
        }
        return content;
    }

    static Map<String, String> getFsObjNameElementLabel(Path path) {
        Map<String, String> labels = new ConcurrentHashMap<>();
        path.iterator().forEachRemaining(
            pathElement -> validateAndBuildPathLabel(labels, pathElement));
        return labels;
    }
    
    static void validateAndBuildPathLabel(Map<String, String> labels, Path pathElement) {
        StringBuilder nameKeyBuilder = new StringBuilder(CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX); 
        String pathElementStr = pathElement.toString();
        
        nameKeyBuilder.append(labels.size());
        if (K8S_FS_NAME_RESTRICATION.matcher(pathElementStr).matches() 
                && pathElementStr.length() < K8S_FS_NAME_MAX_LENGTH) {
            if (pathElementStr.startsWith(K8S_FS_HIDDEN_FILE_INDICATOR)) {
                pathElementStr = pathElementStr.substring(1);
                nameKeyBuilder.append(K8S_FS_HIDDEN_FILE_INDICATOR).append(K8S_FS_HIDDEN_FILE_INDICATOR_SUFFIX);
            }
        } else {
            throw new InvalidPathException(pathElementStr, 
                "A valid k8s filesystem object name must be less than " +
                K8S_FS_NAME_MAX_LENGTH + 
                " characters and valid by '" +
                K8S_FS_NAME_RESTRICATION.toString() + "'"); 
        }
        labels.put(nameKeyBuilder.toString(), pathElementStr);
    }
    
    static String getFileNameString(Path path) {
        return Optional.ofNullable(path.getFileName()).map(Path::toString).orElse(K8SFileSystem.UNIX_SEPARATOR_STRING);
    }

    static long getSize(ConfigMap fileCM) {
        return Long.parseLong(fileCM.getMetadata().getAnnotations().getOrDefault(CFG_MAP_ANNOTATION_FSOBJ_SIZE_KEY, "0"));
    }

    static long getCreationTime(ConfigMap fileCM) {
        return parseTimestamp(fileCM.getMetadata().getCreationTimestamp()).getEpochSecond();
    }
    
    static long getLastModifiedTime(ConfigMap fileCM) {
        return parseTimestamp(fileCM.getMetadata().getAnnotations()
                                                  .get(CFG_MAP_ANNOTATION_FSOBJ_LAST_MODIFIED_TIMESTAMP_KEY))
                .getEpochSecond();
    }
    
    static Path getPathByFsObjCM(K8SFileSystem fs, ConfigMap cm) {
        StringBuilder pathBuilder = new StringBuilder();
        Map<String, String> labels = cm.getMetadata().getLabels();
        Map<Float, Map.Entry<String, String>> labelsToBeSorted = new ConcurrentHashMap<>();
        if (labels.isEmpty() || !labels.containsKey(CFG_MAP_LABEL_FSOBJ_TYPE_KEY)) {
            throw new IllegalArgumentException("Invalid K8SFileSystem ConfigMap - Missing required labels");
        }
        if (labels.containsValue(K8SFileSystemObjectType.ROOT.toString())) {
            return fs.getPath(fs.getSeparator());
        }
        labels.entrySet()
              .stream()
              .filter(entry -> entry.getKey().startsWith(CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX))
              .forEach(entry -> labelsToBeSorted.put(extractPathElementIndex(entry.getKey()), entry));
        
        labelsToBeSorted.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> pathBuilder.append(fs.getSeparator())
                                                     .append(extractPathElementStringWithHiddenIndicator(entry.getValue())));
        return fs.getPath(pathBuilder.toString());
    }
    
    static Float extractPathElementIndex(String pathElement) {
        return Float.parseFloat(pathElement.substring(CFG_MAP_LABEL_FSOBJ_NAME_KEY_PREFIX.length()));
    }
    
    static String extractPathElementStringWithHiddenIndicator(Map.Entry<String, String> entry) {
        String pes = entry.getValue();
        if (entry.getKey().endsWith(K8S_FS_HIDDEN_FILE_INDICATOR.concat(K8S_FS_HIDDEN_FILE_INDICATOR_SUFFIX))) {
            return K8S_FS_HIDDEN_FILE_INDICATOR.concat(pes);
        } else {
            return pes;
        }
    }

    public static boolean isRoot(Path path) {
        return path.getParent() == null;
    }
    
    static boolean isFile(ConfigMap fileCM) {
        return K8SFileSystemObjectType.FILE.toString()
                                           .equals(fileCM.getMetadata()
                                                         .getLabels()
                                                         .getOrDefault(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, UNKNOWN.toString()));
    }

    static boolean isDirectory(ConfigMap fileCM) {
        return K8SFileSystemObjectType.DIR.toString()
                                          .equals(fileCM.getMetadata()
                                                        .getLabels()
                                                        .getOrDefault(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, UNKNOWN.toString()))
               ||                           
               K8SFileSystemObjectType.ROOT.toString()
                .equals(fileCM.getMetadata()
                              .getLabels()
                              .getOrDefault(CFG_MAP_LABEL_FSOBJ_TYPE_KEY, UNKNOWN.toString()));
    }

    @SuppressWarnings("rawtypes")
    static Optional<Kind> mapActionToKind(Action action) {
        switch(action) {
            case ADDED:
                return Optional.of(StandardWatchEventKind.ENTRY_CREATE);
            case DELETED:
                return Optional.of(StandardWatchEventKind.ENTRY_DELETE);
            case MODIFIED:
                return Optional.of(StandardWatchEventKind.ENTRY_MODIFY);
            case ERROR:
            default:
                return Optional.empty();
        }
    }

    static Instant parseTimestamp(String timestamp) {
        return Optional.ofNullable(timestamp).map(ts -> ZonedDateTime.parse(ts, DateTimeFormatter.ISO_DATE_TIME)
                                                                     .toInstant())
                                             .orElse(Instant.now());
    }
}
