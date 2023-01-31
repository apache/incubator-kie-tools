## Commit message validation service

Provides a simple service to check for patterns in a commit message.

## Environment Variables

- `COMMIT_MESSAGE_VALIDATOR__port <PORT_NUMBER>`

  Sets service port, otherwise it will use `env/index.js` port.

- `COMMIT_MESSAGE_VALIDATOR__validators <validatorName1>:<validatorParameters1>;<validatorName2>:<validatorParameters2>...`

  Enables and configures validators.

## API

### - `/validate`

[POST] Validates a commit message againts the enabled validators.

- **Request body:**
  ```
  String with your commit message.
  ```
- **Request response:**
  ```json
  {
    "result": true | false,
    "reason": string | undefined
  }
  ```
