import * as React from "react";
import { useEffect, useMemo, useRef, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";

import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { DmnEditorRoot, DmnEditorRootProps } from "../../dist/DmnEditorRoot";
import { DefaultKeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/DefaultKeyboardShortcutsService";
import { KeyBindingsHelpOverlay } from "@kie-tools-core/editor/dist/envelope/KeyBindingsHelpOverlay";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { getOperatingSystem } from "@kie-tools-core/operating-system";
import {
  EditorEnvelopeI18nContext,
  editorEnvelopeI18nDefaults,
  editorEnvelopeI18nDictionaries,
} from "@kie-tools-core/editor/dist/envelope/i18n";
import { KogitoEditorEnvelopeContext, KogitoEditorEnvelopeContextType } from "@kie-tools-core/editor/dist/api";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";

const sampleDmn = `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export type StorybookDmnEditorRootProps = DmnEditorRootProps & { showKeyBindingsOverlay: boolean };

function DevWebApp({ showKeyBindingsOverlay, ...props }: StorybookDmnEditorRootProps) {
  const editorRef = useRef<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (editorRef.current) {
      editorRef.current.setContent("example.dmn", sampleDmn);
      setLoading(false);
    }
  }, []);

  const envelopeContext: KogitoEditorEnvelopeContextType<any, any> = {
    shared: {} as any,
    channelApi: { shared: {} as any } as any,
    services: {
      keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: getOperatingSystem() }),
      i18n: new I18nService(),
    },
    supportedThemes: [],
  };

  const keyboardShortcuts = useMemo(
    () => [
      { key: "Escape", action: "Edit | Unselect" },
      { key: "Backspace", action: "Edit | Delete selection" },
      { key: "Delete", action: "Edit | Delete selection" },
      { key: "A", action: "Edit | Select/Deselect all" },
      { key: "G", action: "Edit | Create group wrapping selection" },
      { key: "X", action: "Edit | Hide from DRD" },
      { key: "Ctrl+C", action: "Edit | Copy nodes" },
      { key: "Ctrl+X", action: "Edit | Cut nodes" },
      { key: "Ctrl+V", action: "Edit | Paste nodes" },
      { key: "I", action: "Misc | Open/Close properties panel" },
      { key: "H", action: "Misc | Toggle hierarchy highlights" },
      { key: "Up", action: "Move | Move selection up" },
      { key: "Down", action: "Move | Move selection down" },
      { key: "Left", action: "Move | Move selection left" },
      { key: "Right", action: "Move | Move selection right" },
      { key: "Shift + Up", action: "Move | Move selection up a big distance" },
      { key: "Shift + Down", action: "Move | Move selection down a big distance" },
      { key: "Shift + Left", action: "Move | Move selection left a big distance" },
      { key: "Shift + Right", action: "Move | Move selection right a big distance" },
      { key: "B", action: "Navigate | Focus on selection" },
      { key: "Space", action: "Navigate | Reset position to origin" },
      { key: "Right Mouse Button", action: "Navigate | Hold and drag to Pan" },
      { key: "Ctrl", action: "Navigate | Hold and scroll to zoom in/out" },
      { key: "Shift", action: "Navigate | Hold and scroll to navigate horizontally" },
    ],
    []
  );

  useEffect(() => {
    keyboardShortcuts.forEach(({ key, action }) => {
      envelopeContext.services.keyboardShortcuts.registerKeyPress(key, action, async () => {});
    });
  }, [envelopeContext.services.keyboardShortcuts, keyboardShortcuts]);

  return (
    <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0", left: "0" }}>
      <KogitoEditorEnvelopeContext.Provider value={envelopeContext}>
        <I18nDictionariesProvider
          defaults={editorEnvelopeI18nDefaults}
          dictionaries={editorEnvelopeI18nDictionaries}
          ctx={EditorEnvelopeI18nContext}
          initialLocale={navigator.language}
        >
          {!loading && showKeyBindingsOverlay && <KeyBindingsHelpOverlay />}
          <DmnEditorRoot
            {...props}
            exposing={(api) => {
              editorRef.current = api;
            }}
            onNewEdit={(edit) => console.log("Storybook: New edit", edit)}
            workspaceRootAbsolutePosixPath={props.workspaceRootAbsolutePosixPath ?? ""}
            onRequestWorkspaceFileContent={async () => ({
              content: sampleDmn,
              normalizedPosixPathRelativeToTheWorkspaceRoot: "example.dmn",
              type: "text", // ✅ REQUIRED FIELD
            })}
            onRequestWorkspaceFilesList={async (req) => ({
              pattern: req.pattern,
              normalizedPosixPathsRelativeToTheWorkspaceRoot: ["example.dmn"],
            })}
            onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot={(path) => {
              console.log("Storybook: Request to open file", path);
            }}
            keyboardShortcutsService={new DefaultKeyboardShortcutsService({ os: getOperatingSystem() })}
            isReadOnly={props.isReadOnly ?? false}
          />
        </I18nDictionariesProvider>
      </KogitoEditorEnvelopeContext.Provider>
    </div>
  );
}

const meta: Meta<typeof DevWebApp> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<typeof DevWebApp>;

export const WebApp: Story = {
  render: (args) => <DevWebApp {...args} />,
  args: { showKeyBindingsOverlay: true },
};
