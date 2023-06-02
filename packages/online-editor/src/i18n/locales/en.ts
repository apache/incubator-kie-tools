/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { OnlineI18n } from "..";
import { en as en_common } from "@kie-tools/i18n-common-dictionary";
import { en as en_unitables } from "@kie-tools/unitables/dist/i18n/locales/en";
import { wrapped } from "@kie-tools-core/i18n/dist/core";

export const en: OnlineI18n = {
  ...en_common,
  editorPage: {
    textEditorModal: {
      title: (fileName: string) => `Editing ${fileName}`,
    },
    alerts: {
      setContentError: {
        title: "Error opening file. You can edit it as text and reopen the diagram after you've fixed it.",
        action: "Open as text",
      },
      copy: "Content copied to clipboard",
      updateGist: "Gist successfully updated.",
      createGist: "Gist successfully created.",
      errorPushingGist: "Failed to Push an update to your current Gist. Attempt to Push forcefully?",
      updateSnippet: "Snippet successfully updated.",
      createSnippet: "Snippet successfully created.",
      errorPushingSnippet: "Failed to Push an update to your current Snippet. Attempt to Push forcefully?",
      forcePushWarning: "WARNING: This will overwrite your Gist with the local changes!",
      invalidCurrentGist: `Your current gist ${en_common.names.url} is invalid. If you've updated its filename, it's necessary to update your ${en_common.names.url} as well.`,
      invalidGistFilename: "Invalid filename. This gist already has a file with this name.",
      error: `An error occurred trying to perform the last operation. Check if your authentication token is still valid and try again later.`,
      unsaved: {
        titleLocal: "You have new changes since your last download.",
        titleGit: "You have new changes since your last Push.",
        proceedAnyway: "Proceed anyway",
        message: "Your files are temporarily persisted on your browser, but may be erased before you come back.",
      },
    },
    error: {
      title: `${en_common.terms.oops}!`,
      explanation: `The ${en_common.names.dmnRunner} couldn't be rendered due to an error.`,
      message: [
        `This ${en_common.names.dmn} has a construct that is not supported. Please refer to `,
        wrapped("jira"),
        " and report an issue. Don't forget to upload the current file, and the used inputs",
      ],
    },
  },
  editorToolbar: {
    closeAndReturnHome: "Close and return Home",
    saveAndDownload: "Save & Download",
    sendChangesToGitHub: `Send changes to ${en_common.names.github}`,
    copySource: "Copy Source",
    downloadSVG: `${en_common.terms.download} ${en_common.names.svg}`,
    setGitHubToken: `Setup`,
    createGist: "Create Gist",
    cantCreateGistTooltip: `You can't create a Gist because you're either not logged in, or your models are in nested directories.`,
    cantUpdateGistTooltip: `You can't update your Gist because you're either not logged in, not the owner, or your models are in nested directories.`,
    createSnippet: "Create Snippet",
    cantCreateSnippetTooltip: `You can't create a Snippet because you're either not logged in, or your models are in nested directories.`,
    cantUpdateSnippetTooltip: `You can't update your Snippet because you're either not logged in, not the owner, or your models are in nested directories.`,
    share: "Share",
    embed: "Embed",
  },
  accelerators: {
    commitMessage: (appName: string, acceleratorName: string) => `${appName}: Applying ${acceleratorName} Accelerator`,
    loadingAlert: (acceleratorName: string) => `Applying ${acceleratorName} Accelerator...`,
    successAlert: (acceleratorName: string) => `Successfully applied ${acceleratorName} Accelerator`,
    failAlert: (acceleratorName: string) => `Failed to apply ${acceleratorName} Accelerator`,
    acceleratorDescription:
      "An Accelerator is a template. Applying it will move your current files according to the Accelerator specifications and create a new commit for it.",
    acceleratorDetails: "This Accelerator is hosted at",
    dmnFilesMove: "Decisions (.dmn) will be moved to:",
    dmnFilesLocation: "Decisions (.dmn) were moved to:",
    pmmlFilesMove: "Score cards (.pmml) will moved to:",
    pmmlFilesLocation: "Score cards (.pmml) were moved to:",
    bpmnFilesMove: "Workflows (.bpmn, .bpmn2) will be moved to:",
    bpmnFilesLocation: "Workflows (.bpmn, .bpmn2) were moved to:",
    otherFilesMove: "Other files will be moved to:",
    otherFilesLocation: "Other files were moved to:",
    applyAccelerator: "Apply Accelerator",
    appliedAt: "This Accelerator was applied at:",
    applyDisclaimer:
      "This action is permanent. Any changes made after applying an Accelerator may result in your files being in different directories.",
  },
  devDeployments: {
    common: {
      deployYourModel: "Deploy",
      deployInstanceInfo: "Deploy instance information",
      disclaimer:
        "When you set up the required information, you are able to create Dev deployments on your configured instance. All the information you provide is locally stored in the browser and is never shared with anyone.",
      learnMore: "Learn more",
      requiredField: "This field cannot be empty.",
      deploying: "Deploying ...",
      deleting: "Deleting ...",
      saving: "Saving ...",
      setupFirst: `Set up your ${en_common.names.devDeployments} to be able to deploy your models`,
    },
    dropdown: {
      noDeployments: "Your deployments show up here",
      connectedTo: (username: string) => `Connected to  '${username}'`,
      connectedToAction: "Change...",
      deleteDeployments: "Delete all",
      item: {
        upTooltip: "This deployment is up and running.",
        downTooltip: "This deployment is not running.",
        inProgressTooltip: "This deployment is in progress and it will be available shortly.",
        errorTooltip:
          "Some unexpected error happened during the deploy process. Check the logs in your instance for further information.",
        createdAt: (date: string) => `Created at ${date}`,
      },
    },
    configModal: {
      hostInfo: `The hostname associated with your instance.`,
      namespaceInfo: `The Namespace (project) you want your Dev deployments to be.`,
      tokenInfo: `The token associated with your instance.`,
      validationError: "You must fill out all required fields before you can proceed.",
      connectionError: "Connection refused. Please check the information provided.",
      missingPermissions:
        "Missing the required permissions for Dev Deployments (deployments, services, ingresses). Check your user permissions and try again.",
      namespaceNotFound: (namespace: string) => `The namespace ${namespace} was not found in your cluster.`,
      configExpiredWarning: "Token or account expired. Please update your configuration.",
      useOpenShiftWizard: "Configure a new Developer Sandbox for Red Hat OpenShift through the guided wizard",
      useKubernetesWizard: "Configure a new local Kubernetes cluster through the guided wizard",
    },
    deployConfirmModal: {
      title: "Deploy",
      body: "This action can take a few minutes to be completed and you will need to create a new deployment if you update your model, as Dev deployments are immutable.",
    },
    deleteConfirmModal: {
      title: "Delete Dev deployment(s)",
      body: "Are you sure you want to delete your Dev deployment(s)?",
    },
    alerts: {
      deployStartedError:
        "Something went wrong when creating your Dev deployment. Please check your configuration and try again.",
      deployStartedSuccess: "Your Dev deployment has been successfully started and will be available shortly.",
      deleteError: "Failed to delete Dev deployment(s). Please try again via OpenShift Console or CLI.",
      deleteSuccess: "Dev deployment(s) successfully deleted.",
    },
    introduction: {
      explanation: `Create Dev deployments in the cloud and share with others.`,
      disclaimer: `${
        en_common.names.devDeployments
      } is intended for ${"development".bold()} and should not be used for business-critical workloads.`,
      getStarted: "To get started, configure your instance information.",
    },
    openShiftConfigWizard: {
      header: {
        provider: "Provider",
      },
      steps: {
        first: {
          name: "Create your instance",
          introduction: `In order to create your ${en_common.names.shortDevSandbox} instance:`,
          goToGetStartedPage: "Go to the Get Started page",
          followSteps: `Follow the steps to launch your instance. You will be asked to log in with your ${en_common.names.redHat} account.`,
          informNamespace: `Once your instance is up and running, inform the Namespace (project) where you want your Dev deployments created.`,
          inputReason:
            "This information is necessary for creating your Dev deployments in the right Namespace (project).",
          namespacePlaceholder: `The Namespace (project) where you want to create your Dev deployments.`,
        },
        second: {
          name: "Set credentials",
          introduction: `In your ${en_common.names.shortDevSandbox} instance:`,
          accessLoginCommand: `Click on your username on the top right corner and then ${"'Copy login command'".bold()}.`,
          accessDisplayToken: `If asked, log in with ${"'DevSandbox'".bold()}, and then access the ${"'Display Token'".bold()} link.`,
          copyInformation: `In ${"'Log in with this token'".bold()} section, copy your ${"'--server'".bold()} and ${"'--token'".bold()} values, and paste them below.`,
          inputReason: "This information is necessary for establishing a connection with your instance.",
          hostPlaceholder: "Paste the --server value here",
          tokenPlaceholder: "Paste the --token value here",
        },
        final: {
          name: "Connect",
          connectionSuccess: "Connection successfully established.",
          connectionError: "Connection refused.",
          introduction: "Now you are able to create Dev deployments on this OpenShift instance.",
          configNote: "The token you provide is locally stored in this browser and is never shared with anyone.",
          connectionErrorLong: `A connection with your ${en_common.names.shortDevSandbox} instance could not be established.`,
          checkInfo: "Please check the information provided and try again.",
          possibleErrorReasons: {
            introduction: "Here are some possible reasons:",
            emptyField: "One or more required information are not filled.",
            instanceExpired:
              "Instances expire in 30 days. After this period, you will need to recreate it, thus receiving a new host.",
            tokenExpired: "Tokens expire on a daily basis.",
          },
        },
      },
    },
    kubernetesConfigWizard: {
      header: {
        provider: "Provider",
      },
      fields: {
        namespace: "Namespace",
        namespaceInfo: "The Namespace in the cluster where your Dev deployments will be created.",
        kubernetesApiServerUrl: "Kubernetes API Server URL",
        kubernetesApiServerUrlInfo: "The hostname associated with the Kubernetes API Server from your cluster.",
        tokenInfo: "The token associated with your Service Account.",
      },
      steps: {
        first: {
          name: "Create your Kubernetes cluster",
          introduction:
            "In order to create your local Kubernetes cluster first select the flavor you would like and follow the steps:",
          installFlavor: (flavor: string) => `Download and install ${flavor}.`,
          installKubectl: "Install Kubectl if you don't have it already.",
          runCommandsTerminal: "For this step, run the commands in a terminal.",
          createCluster: "Create your cluster:",
          installIngress: "Install the Ingress Controller and wait for it to be ready:",
          installKieSandboxYaml:
            "Install a proxy for the Kubernetes API Server and create the required Service Accounts:",
        },
        second: {
          name: "Set connection info",
          introduction:
            "With your cluster up and running, it should be available in the host prefilled below, and should have a Namespace created.",
          disclaimer:
            "Only change the values below if you have a custom Kubernetes installation, but beware that things might not go as expected.",
          hostInputReason: "This information is necessary for establishing a connection with your Kubernetes cluster.",
          namespaceInputReason:
            "This information is necessary for creating your Dev deployments in the correct Namespace.",
          namespacePlaceholder: "The Namespace where you want to create your Dev deployments.",
          hostPlaceholder: "The Kubernetes API Server URL",
        },
        third: {
          name: "Authenticate",
          introduction:
            "The Kubernetes API requires an authentication token for all requests. In this step we will get the authentication token for the Service Account we created before.",
          getToken: "Run the command below in your terminal to get the authentication token then copy it:",
          tokenPlaceholder: "Paste the token value here",
          tokenInputReason: "The token is necessary to authenticate requests to the Kubernetes API Server",
        },
        final: {
          name: "Connect",
          connectionSuccess: "Connection successfully established.",
          connectionError: "Connection refused.",
          introduction: "Now you are able to create Dev deployments on this OpenShift instance.",
          configNote: "The token you provide is locally stored in this browser and is never shared with anyone.",
          connectionErrorLong: `A connection with your Kubernetes cluster could not be established.`,
          checkInfo: "Please check the information provided and try again.",
          possibleErrorReasons: {
            introduction: "Here are some possible reasons:",
            emptyField: "One or more required information are not filled.",
            clusterNotCreatedCorrectly: "Your Kubernetes cluster might not have been created correctly.",
            tokenExpired: "Tokens might be expired, try creating a new one.",
          },
        },
      },
    },
  },
  embedModal: {
    title: "Embed",
    description:
      "Embed the editor and content in your page. Choose the options below and copy the embed code to your clipboard:",
    copy: "Copy",
    source: {
      current: {
        label: "Current content",
        description: "The embedded Editor will contain the current content, so it cannot be changed externally.",
      },
      gist: {
        alert: `You have new changes to push. Embedding as a ${en_common.names.github} gist won't show your latest changes.`,
        tooltip: `Only available when editing a file from a ${en_common.names.github} gist.`,
        label: `${en_common.names.github} gist`,
        description:
          "The embedded Editor will fetch the content from the open gist. Changes made to this gist will be reflected in the Editor.",
      },
    },
    embedCode: "Embed code",
    copiedToClipboard: "Copied to clipboard",
  },
  connectToGitModal: {
    github: {
      header: {
        title: `${en_common.names.github} ${en_common.names.oauth} ${en_common.terms.token}`,
        subtitle: `Set up your ${en_common.names.github} token so you can interact with GitHub.`,
      },
      footer: {
        createNewToken: "Generate new token",
        placeHolder: "Paste your token here",
      },
      body: {
        learnMore: `Learn more about ${en_common.names.github} tokens`,
        note: `You should provide a token with the ${"'gist'".bold()} permission.`,
      },
      validation: {
        scopes: {
          helper: "Your token must include the 'repo' and 'gist' scopes.",
        },
      },
      form: {
        token: {
          label: "Personal Access Token (classic)",
          placeHolder: "Paste your GitHub token here",
        },
      },
    },
    bitbucket: {
      header: {
        title: `${en_common.names.bitbucket} ${en_common.names.oauth} ${en_common.terms.token}`,
        subtitle: `Set up your ${en_common.names.bitbucket} App Password so you can interact with Bitbucket.`,
      },
      footer: {
        createNewToken: "Generate new App Passord",
        placeHolder: "Paste your App Password here",
      },
      body: {
        learnMore: `Learn more about ${en_common.names.bitbucket} App Passwords`,
        note: `You should provide a token with the ${"'snippet'".bold()} permission.`,
      },
      validation: {
        scopes: {
          helper: "Your token must include the 'account', 'repository' and 'snippet' scopes.",
        },
      },
      form: {
        username: {
          label: "Bitbucket username",
          placeHolder: "Paste your Bitbucket username here",
        },
        token: {
          label: "Bitbucket App Password",
          placeHolder: "Paste your Bitbucket App Password here",
        },
      },
    },
    auth: {
      disclaimer: `The token you provide is locally stored in this browser and is never shared with anyone.`,
      error: {
        alreadyLoggedIn: "You're already logged in with this Token.",
        oauthScopes: (requiredScopes: string) =>
          `Make sure your Token includes the necessary OAuth2 scopes: ${requiredScopes}`,
      },
    },
    navigation: {
      continue: "Continue",
      seeConnectedAccounts: "See connected accounts",
    },
    status: {
      loading: "Loading...",
    },
  },
  commitModal: {
    title: "Input custom commit message",
    description: "Write a brief summary of the changes made to the workspace, ideally up to 72 characters.",
    commit: "Commit",
    emptyMessageValidation: "Commit message cannot be empty",
    placeholder: "Commit message",
  },
  homePage: {
    uploadFile: {
      header: "Edit existing file",
      body: `Upload your ${en_common.names.bpmn}, ${en_common.names.dmn} or ${en_common.names.pmml} file here to start making new edits!`,
      helperText: `Upload a .${en_common.names.bpmn}, .${en_common.names.bpmn}2, .${en_common.names.dmn} or .${en_common.names.pmml} file`,
      helperInvalidText: "File extension is not supported",
      placeholder: "Drag a file or browse for it.",
    },
    openUrl: {
      validating: `Validating ${en_common.names.url}`,
      invalidGistExtension: "File type on the provided gist is not supported.",
      invalidExtension: `File type on the provided ${en_common.names.url} is not supported.`,
      invalidGist: `Enter a valid gist ${en_common.names.url}. If you're using a specific gist ${en_common.names.url} remember its name can't have whitespaces and upper-case letters.`,
      invalidUrl: `This ${en_common.names.url} is not valid (don't forget "https://"!).`,
      notFoundUrl: `This ${en_common.names.url} does not exist.`,
      corsNotAvailable: `This ${en_common.names.url} cannot be opened because it doesn't allow other websites to access it.`,
      openFromSource: "Open from source",
      description: `Paste a ${en_common.names.url} to a source code link (${en_common.names.github}, ${en_common.names.dropbox}, etc.)`,
    },
    dropdown: {
      onlineForum: "Online forum",
    },
    bpmnCard: {
      title: `Workflow (.${en_common.names.bpmn})`,
      explanation: `${en_common.names.bpmn} files are used to generate Workflows.`,
      createNew: "Create new workflow",
    },
    dmnCard: {
      title: `Decision model (.${en_common.names.dmn})`,
      explanation: `${en_common.names.dmn} files are used to generate decision models`,
      createNew: "Create new decision model",
    },
    pmmlCard: {
      title: `Scorecard model (.${en_common.names.pmml})`,
      explanation: `${en_common.names.pmml} files are used to generate scorecards`,
      createNew: "Create new Scorecard",
    },
    trySample: "Try Sample",
    chooseLocalFile: "Choose a local file",
  },
  alerts: {
    gistError: `Not able to open this Gist. If you have updated your Gist filename it can take a few seconds until the URL is available to be used.`,
    goToHomePage: "Go to Home Page",
    errorDetails: "Error details:",
    responseError: {
      title: "An error happened while fetching your file",
    },
    fetchError: {
      title: "An unexpected error happened while trying to fetch your file",
      possibleCauses: "Possible causes:",
      missingGitHubToken: `If you're trying to open a private file, make sure to set your GitHub token before. To do it use one of the Editor pages and open the "Set your GitHub token" modal under the Share dropdown.`,
      cors: "The URL to your file must allow CORS in its response, which should contain the following header:",
    },
  },
  dmnRunner: {
    error: {
      title: `${en_common.terms.oops}!`,
      explanation: `The ${en_common.names.dmnRunner} couldn't be rendered due to an error.`,
      message: [
        `This ${en_common.names.dmn} has a construct that is not supported. Please refer to `,
        wrapped("jira"),
        " and report an issue. Don't forget to upload the current file, and the used inputs",
      ],
    },
    table: { ...en_unitables },
    modal: {
      initial: {
        runDmnModels: "Run your models and see live forms and results as you edit.",
        explanation:
          "Input nodes become interactive fields on an auto-generated form, and the results are displayed as easy-to-read cards.",
        notificationPanelExplanation: [
          `The Problems panel `,
          wrapped("icon"),
          `, at the bottom-right corner of the Editor, displays live Execution messages to assist modeling your decisions.`,
        ],
      },
      wizard: {
        title: `${en_common.names.extendedServices} ${en_common.terms.setup}`,
        description: `Choose your ${en_common.terms.os.full} and follow the instructions to install and start the ${en_common.names.extendedServices}.`,
        outdatedAlert: {
          title: `${en_common.names.extendedServices} is outdated!`,
          message: `It looks like you're using an incompatible version of the ${en_common.names.extendedServices}. Follow the instructions below to update.`,
        },
        stoppedAlert: {
          title: `${en_common.names.extendedServices} has stopped!`,
          message: `It looks like the ${en_common.names.extendedServices} has suddenly stopped, please follow these instructions to start it again.`,
        },
        macos: {
          install: {
            download: ` ${en_common.names.extendedServices}.`,
            openFile: ["Open the ", wrapped("file"), " file."],
            dragFileToApplicationsFolder: ["Drag ", wrapped("file"), " to the ", wrapped("folder"), " folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.extendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${en_common.names.extendedServices} app by launching `,
                wrapped("file"),
                ".",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.extendedServices}:`,
              openApplicationsFolder: ["Open the ", wrapped("folder"), " folder."],
              again: "again",
              openAndCancel: [
                "Right-click on ",
                wrapped("file"),
                ` select "${en_common.terms.open}" and then "${en_common.terms.cancel}".`,
              ],
              openInstruction: [
                "Right-click on ",
                wrapped("file"),
                " ",
                wrapped("again"),
                ` and then select "${en_common.terms.open}".`,
              ],
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.extendedServices} before:`,
            launchExtendedServices: ["Launch the ", wrapped("file")],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on a Terminal tab to start ${en_common.names.extendedServices} on a different port:`,
            },
          },
        },
        windows: {
          install: {
            keepDownload: ` ${en_common.names.extendedServices}. Note that you'll probably have to right-click the download and choose "Keep"`,
            moveTheFile: ["Move the ", wrapped("file"), " file to your preferred folder."],
          },
          start: {
            stopped: {
              startInstruction: `If you see the ${en_common.names.extendedServices} icon on your system bar, simply click it and select "${en_common.terms.start}".`,
              launchExtendedServices: [
                `If not, start the ${en_common.names.extendedServices} by opening the `,
                wrapped("file"),
                "file.",
              ],
            },
            firstTime: {
              title: `If you just installed ${en_common.names.extendedServices}:`,
              openFolder: ["Open folder where you placed the ", wrapped("file"), " file."],
              runAnyway: `Double-click it and select "More info" then click on the "Run anyway" button.`,
            },
            alreadyRanBefore: `If you already installed and ran the ${en_common.names.extendedServices} before:`,
            launchExtendedServices: ["Open the ", wrapped("file"), " file."],
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: `Run the following command on the Command prompt to start ${en_common.names.extendedServices} on a different port:`,
            },
          },
        },
        linux: {
          install: {
            download: ` ${en_common.names.extendedServices}.`,
            installAppIndicator: "Install the AppIndicator lib for your system:",
            ubuntuDependency: [`${en_common.names.ubuntu}: `, wrapped("package")],
            fedoraDependency: [`${en_common.names.fedora}: `, wrapped("package")],
            extractContent: ["Extract the contents of ", wrapped("file"), " to your location of choice."],
            binaryExplanation: [
              `The ${en_common.names.extendedServices} binary, `,
              wrapped("file"),
              ", is a single binary file, which means you can add it to your PATH or even configure it to execute when your computer starts.",
            ],
          },
          start: {
            openTerminal: "Open a Terminal window.",
            goToFolder: ["Go to the folder where you placed the ", wrapped("file"), " binary."],
            runCommand: "Run ",
            advanced: {
              title: "Advanced Settings",
              runFollowingCommand: [
                "Open a Terminal window and run the following command on the directory where you placed the ",
                wrapped("file"),
                " binary:",
              ],
            },
          },
        },
        footerWaitingToConnect: `Waiting to connect to ${en_common.names.extendedServices}`,
        advancedSettings: {
          title: [
            `The default ${en_common.names.extendedServices} port is `,
            wrapped("port"),
            `. If you're already using this port for another application, you can change the port used to connect with the ${en_common.names.extendedServices}.`,
          ],
          label: "Port",
          helperTextInvalid: "Invalid port. Valid ports: 0 <= port <= 65353",
        },
      },
    },
    dropdown: {
      label: `${en_common.names.extendedServices}`,
      setup: `${en_common.terms.setup} ${en_common.names.extendedServices}`,
      open: `${en_common.terms.open} ${en_common.names.extendedServices} panel`,
      close: `${en_common.terms.close} ${en_common.names.extendedServices} panel`,
    },
    button: {
      available: `This is only available in ${en_common.names.chrome} at the moment`,
    },
  },
  notificationsPanel: {
    name: "Problems panel",
    tooltip: {
      retractAll: "Retract All",
      expandAll: "Expand All",
    },
  },
  extendedServices: {
    dropdown: {
      shortConnected: (port: string) => `Connected to port ${port}`,
      tooltip: {
        connected: `${en_common.names.extendedServices} is connected.`,
        install: `Setup ${en_common.names.extendedServices} to use this feature. Click to install.`,
        outdated: `${en_common.names.extendedServices} is outdated. Click to update.`,
        disconnected: `${en_common.names.extendedServices} is disconnected.`,
      },
    },
    modal: {
      initial: {
        subHeader: `Augment the ${en_common.names.dmn} editor`,
      },
    },
  },
  createGitRepositoryModal: {
    form: {
      buttonCreate: "Create",
      nameField: {
        label: "Name",
        hint: "Invalid name. Only letters, numbers, dashes (-), dots (.), and underscores (_) are allowed.",
      },
      visibility: {
        public: {
          label: "Public",
          description: "Anyone on the internet can see this repository. You choose who can commit.",
        },
        private: {
          label: "Private",
          description: "You choose who can see and commit to this repository.",
        },
      },
    },
    bitbucket: {
      repository: `${en_common.names.bitbucket} repository`,
      createRepository: `Create ${en_common.names.bitbucket} repository`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${en_common.names.bitbucket} repository.`,
      error: {
        formAlert: (error: string) => `Error creating ${en_common.names.bitbucket} repository. ${error}`,
      },
      form: {
        select: {
          label: "Pick a workspace under which the new repository will be created.",
          description: "Pick either a personal or shared workspace.",
        },
      },
    },
    github: {
      repository: `${en_common.names.github} repository`,
      createRepository: `Create ${en_common.names.github} repository`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${en_common.names.github} repository.`,
      error: {
        formAlert: (error: string) => `Error creating ${en_common.names.github} repository. ${error}`,
      },
      form: {
        select: {
          label: "The new repository will be created under the following scope",
          description: "Pick either your user account or a GitHub organization.",
        },
      },
    },
  },
  createGistOrSnippetModal: {
    form: {
      buttonCreate: "Create",
      visibility: {
        public: {
          label: "Public",
          description: "Anyone on the internet can see this repository. You choose who can commit.",
        },
        private: {
          label: "Private",
          description: "You choose who can see and commit to this repository.",
        },
      },
    },
    bitbucket: {
      gistOrSnippet: `${en_common.names.bitbucket} Snippet`,
      create: `Create ${en_common.names.bitbucket} Snippet`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${en_common.names.bitbucket} Snippet.`,
      error: {
        formAlert: (error: string) => `Error creating ${en_common.names.bitbucket} Snippet. ${error}`,
      },
      form: {
        select: {
          label: "Pick a workspace under which the new Snippet will be created.",
          description: "Pick either a personal or shared workspace.",
        },
      },
    },
    github: {
      gistOrSnippet: `${en_common.names.github} Gist`,
      create: `Create ${en_common.names.github} Gist`,
      description: (workspace: string) =>
        `The contents of '${workspace}' will be all in the new ${en_common.names.github} Gist.`,
      error: {
        formAlert: (error: string) => `Error creating ${en_common.names.github} Gist. ${error}`,
      },
      form: {
        select: {
          label: "The Gist will be created under the following user.",
          description: "Currently GitHub does not allow to create Gists in GitHub organizations.",
        },
      },
    },
  },
  loadOrganizationsSelect: {
    bitbucket: {
      user: "Bitbucket user",
      organizations: "Bitbucket workspaces",
    },
    github: {
      user: "GitHub user",
      organizations: "GitHub organizations",
    },
  },
  gitStatusIndicatorActions: {
    revert: {
      title: "Revert",
      warning: "This action is permanent",
      description: "Are you sure you want to revert local changes to:",
      confirmButtonText: "Yes, revert permanently",
    },
    revertAll: {
      title: "Revert all changes",
      warning: "This action is permanent",
      description: "Are you sure? The following files will be reverted to the last commit:",
      confirmButtonText: "Yes, revert permanently",
    },
  },
};
