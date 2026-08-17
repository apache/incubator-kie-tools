/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.completion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguredGroupingResolverTest {

    private static final String KMODULE_HEADER =
            "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\">\n";

    private static Path writeDrl(Path root, String relativePath, String packageName) throws IOException {
        Path file = root.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, "package " + packageName + ";\n\nrule \"r\" when then end\n");
        return file.toAbsolutePath().normalize();
    }

    private static void write(Path root, String relativePath, String content) throws IOException {
        Path file = root.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    private static ConfiguredGroupingResolver resolverFor(Path workspace) {
        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(workspace);
        return resolver;
    }

    // ── tier 2: kmodule.xml, no editor-specific configuration at all ─────────

    @Test
    void groupsByKModulePackagesWithNoOtherConfiguration(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "mod/src/main/resources/com/example/validation/A.drl", "com.example.validation");
        Path b = writeDrl(ws, "mod/src/main/resources/com/example/validation/B.drl", "com.example.validation");
        Path shared = writeDrl(ws, "mod/src/main/resources/com/example/shared/S.drl", "com.example.shared");
        write(ws, "mod/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"validation\" packages=\"com.example.validation.*\"/>\n"
                + "  <kbase name=\"shared\" packages=\"com.example.shared.*\"/>\n"
                + "</kmodule>\n");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
        assertThat(resolver.resolveSiblings(shared)).isEmpty();
        assertThat(resolver.resolveAllGroups()).containsOnlyKeys("validation", "shared");
    }

    @Test
    void kModuleIncludesPullInTheIncludedGroupsFiles(@TempDir Path ws) throws Exception {
        Path app = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path types = writeDrl(ws, "m/src/main/resources/com/example/shared/Types.drl", "com.example.shared");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"shared\" packages=\"com.example.shared.*\"/>\n"
                + "  <kbase name=\"app\" packages=\"com.example.app.*\" includes=\"shared\"/>\n"
                + "</kmodule>\n");

        assertThat(resolverFor(ws).resolveSiblings(app)).containsExactly(types);
    }

    @Test
    void aFileInNoKBaseFallsBackToSameDirectorySiblings(@TempDir Path ws) throws Exception {
        writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path loose = writeDrl(ws, "scratch/Loose.drl", "com.scratch");
        Path looseNeighbour = writeDrl(ws, "scratch/Neighbour.drl", "com.scratch");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        assertThat(resolverFor(ws).resolveSiblings(loose)).containsExactly(looseNeighbour);
    }

    @Test
    void buildOutputCopiesAreNotGrouped(@TempDir Path ws) throws Exception {
        Path source = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        writeDrl(ws, "m/target/classes/com/example/app/A.drl", "com.example.app");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        assertThat(resolverFor(ws).resolveAllGroups().get("app").files()).containsExactly(source);
    }

    // ── grouping declared in the editor's settings, with nothing on disk ─────

    @Test
    void groupsCanBeDeclaredEntirelyInSettings(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        writeDrl(ws, "other/C.drl", "com.other");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(ws);
        resolver.setSettingsConfig(
                "{\"kbases\":[{\"name\":\"fromSettings\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]}]}");

        // Relative entries anchor at the workspace root, there being no config
        // file to resolve them against.
        assertThat(resolver.resolveAllGroups().get("fromSettings").files()).containsExactly(a, b);
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    @Test
    void settingsTakePrecedenceOverAConfigFileOnDisk(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        writeDrl(ws, "rules/B.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"fromFile\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]}]}");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(ws);
        resolver.setSettingsConfig("{\"kbases\":[{\"name\":\"fromSettings\",\"files\":[\"rules/A.drl\"]}]}");

        assertThat(resolver.resolveAllGroups()).containsKeys("fromSettings", "fromFile");
        // Both claim A.drl; settings are indexed first, so they win the lookup.
        assertThat(resolver.resolveSiblings(a)).isEmpty();
    }

    @Test
    void clearingTheSettingFallsBackToDisk(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"fromFile\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]}]}");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(ws);
        resolver.setSettingsConfig("{\"kbases\":[{\"name\":\"fromSettings\",\"files\":[\"rules/A.drl\"]}]}");
        assertThat(resolver.resolveSiblings(a)).isEmpty();

        resolver.setSettingsConfig(null);

        assertThat(resolver.resolveAllGroups()).containsOnlyKeys("fromFile");
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    @Test
    void aHalfTypedSettingIsIgnoredRatherThanLosingTheWorkspace(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"fromFile\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]}]}");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(ws);
        resolver.setSettingsConfig("{\"kbases\":[{\"name\":");

        assertThat(resolver.resolveAllGroups()).containsOnlyKeys("fromFile");
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    // ── files supplied by the client instead of discovered ───────────────────

    @Test
    void aClientSuppliedFileListReplacesDiscovery(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path b = writeDrl(ws, "m/src/main/resources/com/example/app/B.drl", "com.example.app");
        // On disk but withheld by the client — its user excluded it, so it must
        // not be grouped even though a walk would have found it.
        writeDrl(ws, "m/src/main/resources/com/example/app/Hidden.drl", "com.example.app");
        Path kmodule = ws.resolve("m/src/main/resources/META-INF/kmodule.xml");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceFiles(List.of(a, b, kmodule.toAbsolutePath().normalize()));
        resolver.setWorkspaceRoot(ws);

        assertThat(resolver.resolveAllGroups().get("app").files()).containsExactlyInAnyOrder(a, b);
    }

    @Test
    void buildOutputIsFilteredOutOfASuppliedListToo(@TempDir Path ws) throws Exception {
        Path source = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path copy = writeDrl(ws, "m/target/classes/com/example/app/A.drl", "com.example.app");
        Path kmodule = ws.resolve("m/src/main/resources/META-INF/kmodule.xml");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        // A client with no exclude configured may well hand us the build copy.
        resolver.setWorkspaceFiles(List.of(source, copy, kmodule.toAbsolutePath().normalize()));
        resolver.setWorkspaceRoot(ws);

        assertThat(resolver.resolveAllGroups().get("app").files()).containsExactly(source);
    }

    @Test
    void withdrawingTheListRestoresDiscovery(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path b = writeDrl(ws, "m/src/main/resources/com/example/app/B.drl", "com.example.app");
        Path kmodule = ws.resolve("m/src/main/resources/META-INF/kmodule.xml");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        // The list has to carry the descriptors as well as the rules: what the
        // client withholds does not exist as far as grouping is concerned.
        resolver.setWorkspaceFiles(List.of(a, kmodule.toAbsolutePath().normalize()));
        resolver.setWorkspaceRoot(ws);
        assertThat(resolver.resolveAllGroups().get("app").files()).containsExactly(a);

        resolver.setWorkspaceFiles(null);

        assertThat(resolver.resolveAllGroups().get("app").files()).containsExactlyInAnyOrder(a, b);
    }

    // ── provenance ───────────────────────────────────────────────────────────

    @Test
    void aGroupFromAKModuleDescriptorIsReportedAsAKieBase(@TempDir Path ws) throws Exception {
        writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"app\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");

        WorkspaceSiblingResolver.Group group = resolverFor(ws).resolveAllGroups().get("app");

        assertThat(group.kind()).isEqualTo("KIE base");
        assertThat(group.declaredIn())
                .isEqualTo(ws.resolve("m/src/main/resources/META-INF/kmodule.xml").toAbsolutePath().normalize());
    }

    @Test
    void aGroupFromTheEditorsOwnConfigStaysGeneric(@TempDir Path ws) throws Exception {
        writeDrl(ws, "rules/A.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"legacy\",\"files\":[\"rules/A.drl\"]}]}");

        WorkspaceSiblingResolver.Group group = resolverFor(ws).resolveAllGroups().get("legacy");

        // Null kind, so the client uses its own neutral wording rather than
        // claiming a kmodule concept the project never declared.
        assertThat(group.kind()).isNull();
        assertThat(group.declaredIn())
                .isEqualTo(ws.resolve("drl-lsp-kbases.json").toAbsolutePath().normalize());
    }

    @Test
    void anAdoptedManifestIsNamedAsTheGroupsSource(@TempDir Path ws) throws Exception {
        writeDrl(ws, "mod/src/main/resources/com/a/A.drl", "com.a");
        write(ws, "cfg/A_Rule-Configs.json",
                "{\"rule.config.list\":[{\"rule.config.type\":\"groupA\","
                        + "\"relative.path.list\":[\"com/a/A.drl\"]}]}");
        write(ws, "drl-lsp-kbases.json",
                "{\"sources\":[{\"include\":\"**/*_Rule-Configs.json\","
                        + "\"pathsRelativeTo\":[\"**/src/main/resources\"],"
                        + "\"aliases\":{\"kbases\":\"rule.config.list\",\"name\":\"rule.config.type\","
                        + "\"files\":[\"relative.path.list\",\"absolute.path.list\"]}}]}");

        WorkspaceSiblingResolver.Group group = resolverFor(ws).resolveAllGroups().get("groupA");

        assertThat(group.kind()).isNull();
        // The manifest itself, not the config file that pointed at it — that is
        // the file a user would open to change the grouping.
        assertThat(group.declaredIn())
                .isEqualTo(ws.resolve("cfg/A_Rule-Configs.json").toAbsolutePath().normalize());
    }

    // ── tier 1: the editor's own config file ─────────────────────────────────

    @Test
    void explicitFileListsFormAGroupInDeclarationOrder(@TempDir Path ws) throws Exception {
        Path types = writeDrl(ws, "rules/Types.drl", "com.example");
        Path enums = writeDrl(ws, "rules/Enums.drl", "com.example");
        writeDrl(ws, "rules/Unrelated.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{ \"kbases\": [ { \"name\": \"legacy\","
                        + " \"files\": [\"rules/Types.drl\", \"rules/Enums.drl\"] } ] }");

        assertThat(resolverFor(ws).resolveSiblings(types)).containsExactly(enums);
        assertThat(resolverFor(ws).resolveAllGroups().get("legacy").files()).containsExactly(types, enums);
    }

    @Test
    void aFilesOnlyGroupClaimsNothingItDidNotList(@TempDir Path ws) throws Exception {
        // An empty "packages" means "everything" only in kmodule's dialect; a
        // group that lists files must not silently swallow the workspace.
        Path listed = writeDrl(ws, "rules/Listed.drl", "com.example");
        Path other = writeDrl(ws, "elsewhere/Other.drl", "com.other");
        Path otherNeighbour = writeDrl(ws, "elsewhere/Neighbour.drl", "com.other");
        write(ws, "drl-lsp-kbases.json",
                "{ \"kbases\": [ { \"name\": \"listed\", \"files\": [\"rules/Listed.drl\"] } ] }");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        assertThat(resolver.resolveAllGroups().get("listed").files()).containsExactly(listed);
        assertThat(resolver.resolveSiblings(other)).containsExactly(otherNeighbour);
    }

    @Test
    void configFilePackagesTakePrecedenceOverKModule(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "m/src/main/resources/com/example/app/A.drl", "com.example.app");
        Path b = writeDrl(ws, "m/src/main/resources/com/example/app/B.drl", "com.example.app");
        write(ws, "m/src/main/resources/META-INF/kmodule.xml", KMODULE_HEADER
                + "  <kbase name=\"fromKModule\" packages=\"com.example.app.*\"/>\n"
                + "</kmodule>\n");
        write(ws, "drl-lsp-kbases.json",
                "{ \"kbases\": [ { \"name\": \"fromConfig\", \"packages\": [\"com.example.app.*\"] } ] }");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        assertThat(resolver.resolveAllGroups()).containsKeys("fromConfig", "fromKModule");
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
        // Both groups claim the file; the first declared one wins the lookup.
        assertThat(resolver.resolveAllGroups().get("fromConfig").files()).contains(a, b);
    }

    // ── adopting a manifest the project already has ──────────────────────────

    @Test
    void adoptsAnExistingManifestThroughKeyAliases(@TempDir Path ws) throws Exception {
        Path types = writeDrl(ws, "mod/src/main/resources/com/example/validation/Types.drl", "com.example.validation");
        Path enums = writeDrl(ws, "mod/src/main/resources/com/example/validation/Enums.drl", "com.example.validation");
        Path shared = writeDrl(ws, "mod/src/main/resources/com/example/common/Shared.drl", "com.example.common");

        // The project's own manifest, in its own vocabulary, left untouched.
        write(ws, "config/Validation_Rule-Configs.json",
                "{\n"
                        + "  \"rule.config.list\": [\n"
                        + "    { \"rule.config.type\": \"validation\",\n"
                        + "      \"relative.path.list\": [\n"
                        + "        \"com/example/validation/Types.drl\",\n"
                        + "        \"com/example/validation/Enums.drl\"\n"
                        + "      ],\n"
                        + "      \"absolute.path.list\": [\"" + shared.toString().replace('\\', '/') + "\"]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}\n");

        write(ws, "drl-lsp-kbases.json",
                "{\n"
                        + "  \"sources\": [\n"
                        + "    { \"include\": \"**/*_Rule-Configs.json\",\n"
                        + "      \"pathsRelativeTo\": [\"**/src/main/resources\"],\n"
                        + "      \"aliases\": {\n"
                        + "        \"kbases\": \"rule.config.list\",\n"
                        + "        \"name\": \"rule.config.type\",\n"
                        + "        \"files\": [\"relative.path.list\", \"absolute.path.list\"]\n"
                        + "      } }\n"
                        + "  ]\n"
                        + "}\n");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        // Both aliased keys collapse onto one ordered file list.
        assertThat(resolver.resolveAllGroups().get("validation").files()).containsExactly(types, enums, shared);
        assertThat(resolver.resolveSiblings(types)).containsExactly(enums, shared);
    }

    @Test
    void severalAdoptedManifestsContributeSeparateGroups(@TempDir Path ws) throws Exception {
        writeDrl(ws, "mod/src/main/resources/com/a/A.drl", "com.a");
        writeDrl(ws, "mod/src/main/resources/com/b/B.drl", "com.b");
        write(ws, "cfg/A_Rule-Configs.json",
                "{\"rule.config.list\":[{\"rule.config.type\":\"groupA\","
                        + "\"relative.path.list\":[\"com/a/A.drl\"]}]}");
        write(ws, "cfg/B_Rule-Configs.json",
                "{\"rule.config.list\":[{\"rule.config.type\":\"groupB\","
                        + "\"relative.path.list\":[\"com/b/B.drl\"]}]}");
        write(ws, "drl-lsp-kbases.json",
                "{\"sources\":[{\"include\":\"**/*_Rule-Configs.json\","
                        + "\"pathsRelativeTo\":[\"**/src/main/resources\"],"
                        + "\"aliases\":{\"kbases\":\"rule.config.list\",\"name\":\"rule.config.type\","
                        + "\"files\":[\"relative.path.list\",\"absolute.path.list\"]}}]}");

        assertThat(resolverFor(ws).resolveAllGroups()).containsOnlyKeys("groupA", "groupB");
    }

    @Test
    void aGroupNameDeclaredTwiceIsMergedRatherThanShadowed(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "mod/src/main/resources/com/x/A.drl", "com.x");
        Path b = writeDrl(ws, "mod/src/main/resources/com/x/B.drl", "com.x");
        write(ws, "cfg/One_Rule-Configs.json",
                "{\"rule.config.list\":[{\"rule.config.type\":\"shared\","
                        + "\"relative.path.list\":[\"com/x/A.drl\"]}]}");
        write(ws, "cfg/Two_Rule-Configs.json",
                "{\"rule.config.list\":[{\"rule.config.type\":\"shared\","
                        + "\"relative.path.list\":[\"com/x/B.drl\"]}]}");
        write(ws, "drl-lsp-kbases.json",
                "{\"sources\":[{\"include\":\"**/*_Rule-Configs.json\","
                        + "\"pathsRelativeTo\":[\"**/src/main/resources\"],"
                        + "\"aliases\":{\"kbases\":\"rule.config.list\",\"name\":\"rule.config.type\","
                        + "\"files\":[\"relative.path.list\",\"absolute.path.list\"]}}]}");

        assertThat(resolverFor(ws).resolveAllGroups().get("shared").files()).containsExactlyInAnyOrder(a, b);
    }

    // ── user pinning ─────────────────────────────────────────────────────────

    @Test
    void pinningAFileMovesItToTheChosenGroup(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        Path c = writeDrl(ws, "other/C.drl", "com.other");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"one\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]},"
                        + "{\"name\":\"two\",\"files\":[\"other/C.drl\"]}]}");

        ConfiguredGroupingResolver resolver = resolverFor(ws);
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);

        resolver.setGroupOverride(a, "two");
        assertThat(resolver.resolveSiblings(a)).containsExactly(c);

        resolver.setGroupOverride(a, null);
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    @Test
    void pinningToAnUnknownGroupFallsBackRatherThanEmptyingScope(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        write(ws, "drl-lsp-kbases.json",
                "{\"kbases\":[{\"name\":\"one\",\"files\":[\"rules/A.drl\",\"rules/B.drl\"]}]}");

        ConfiguredGroupingResolver resolver = resolverFor(ws);
        resolver.setGroupOverride(a, "deleted-group");

        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    // ── degenerate workspaces ────────────────────────────────────────────────

    @Test
    void anUnconfiguredWorkspaceBehavesExactlyLikeTheSameDirectoryDefault(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        assertThat(resolver.resolveAllGroups()).isEmpty();
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    @Test
    void aMalformedConfigIsSkippedWithoutTakingTheWorkspaceDown(@TempDir Path ws) throws Exception {
        Path a = writeDrl(ws, "rules/A.drl", "com.example");
        Path b = writeDrl(ws, "rules/B.drl", "com.example");
        write(ws, "drl-lsp-kbases.json", "{ this is not json");

        ConfiguredGroupingResolver resolver = resolverFor(ws);

        assertThat(resolver.resolveAllGroups()).isEmpty();
        assertThat(resolver.resolveSiblings(a)).containsExactly(b);
    }

    @Test
    void nullWorkspaceRootIsTolerated() {
        ConfiguredGroupingResolver resolver = new ConfiguredGroupingResolver();
        resolver.setWorkspaceRoot(null);

        assertThat(resolver.resolveAllGroups()).isEmpty();
        assertThat(resolver.resolveSiblings(null)).isEmpty();
    }
}
