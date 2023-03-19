## Commit message validation service

Provides a simple service to check for patterns in a commit message.

## Environment Variables

- `EXAMPLE_COMMIT_MESSAGE_VALIDATION_SERVICE__port <PORT_NUMBER>`

  Sets service port, otherwise it will use `env/index.js` port.

- `EXAMPLE_COMMIT_MESSAGE_VALIDATION_SERVICE__validators <validatorName1>:<validatorParameters1>;<validatorName2>:<validatorParameters2>...`

  Enables and configures validators. The value is a list of `;` separated validators that are parameterized with anything after `:`. e.g.: `Length:5-72;IssuePrefix:kie-issues#*` will enable the Lenght validator, with min 5 and max 72 characters, and will also enable the IssuePrefix validator, with the prefix pattern being `kie-issues#*`.

## API

### - `/validate`

[POST] Validates a commit message against the enabled validators.

- **Request body:**
  ```
  String with your commit message.
  ```
- **Request response:**
  ```json
  {
    "result": true | false,
    "reasons": []string | undefined
  }
  ```
