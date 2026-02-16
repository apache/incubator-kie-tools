import * as prettier from "prettier";
import xmlPlugin from "@prettier/plugin-xml";

// Prettier loads configuration files using dynamic `import()` calls inside `prettier.resolveConfig()`.
// However, when Playwright detects the use of `__dirname`, it opts into its CommonJS-based test runner
// (see https://github.com/microsoft/playwright/issues/37890).
//
// In this CJS-backed environment, Prettier’s internal `import()` never
// resolves nor rejects, causing test execution to hang indefinitely.
//
// To avoid this, we bypass `prettier.resolveConfig()` and inline the Prettier configuration manually.
export async function prettierFormat(content: string) {
  return prettier.format(content, {
    printWidth: 120,
    trailingComma: "es5",
    xmlWhitespaceSensitivity: "preserve",
    plugins: [xmlPlugin],
    parser: "xml",
  });
}
