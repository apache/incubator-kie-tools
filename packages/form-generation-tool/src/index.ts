/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { generateForms } from "./generation/formGenerationCommand";

yargs(hideBin(process.argv))
  .scriptName("form-generation-tool")
  .usage(
    "Generates Forms for User tasks in Kogito projects. " +
      "\nGenerated Forms will be stored in the project 'src/main/resources/forms' folder." +
      "\n\nUsage: $0 [source]"
  )
  .command(
    "$0 [source]",
    "",
    (yargs) => {
      yargs
        .positional("source", {
          describe: "Path to a Kogito project.",
          normalize: true,
          demandOption: true,
          nargs: 1,
        })
        .option("type", {
          alias: "t",
          type: "string",
          default: "patternfly",
          choices: ["patternfly", "bootstrap"],
          description: 'Form type to generate. Available options are "patternfly" / "bootstrap".',
        })
        .option("overwrite", {
          alias: "o",
          type: "boolean",
          default: false,
          nargs: 0,
          description: "Deletes all the existing forms in the project and creates new ones.",
        });
    },
    (args) => {
      generateForms({
        source: args.source as string,
        type: args.type as string,
        overwrite: args.overwrite as boolean,
      });
    }
  )
  .help()
  .demandOption("source", "Please provide a path to a Kogito project")
  .showHelpOnFail(false, "Use --help for available options")
  .version(false).argv;
