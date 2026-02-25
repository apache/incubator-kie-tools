# KIE Tools :: Multiplying Architecture

- **_Multiplying Architecture_** is a concept in Apache KIE Tools related to micro-frontends and reuse of UI components in diverse contexts.
- The [`@kie-tools/envelope-bus`](../packages/envelope-bus) package contains the core of the **_Multiplying Architecture_**.
- This technology was originally developed so that Apache KIE BPMN, DMN, and SceSim Editors could run inside modern web apps and platforms even though its technology stack, design system, and CSS styles were completely different and conflicting with its containing web apps or platforms.
- **_Multiplying Architecture_** is extensively used for communicating custom editors on VS Code extensions with the extension's backend, and for powering KIE Sandbox's File System and Git subsystems ([`@kie-tools-core/workspaces-git-fs`](../packages/workspaces-git-fs/)).
- The term **_Multiplying Architecture_** was coined to signify the ability this technology has of making UI components, independent of their tech stack, available in _multiple_ places and platforms, also independent of their technology stack.
- Refer to [`@kie-tools/envelope-bus`'s README](../packages/envelope-bus/README.md) for more concrete details of the **_Multiplying Architecture_**.
- Because it allowed Apache KIE BPMN, DMN, and SceSim Editors to be present in _multiple_ "_distribution **`Channels`**_", the term **`Channel`** stuck, although is might be confusing at first.
- **`Envelope`** was simply a nicer way to refer to a wrapper.
