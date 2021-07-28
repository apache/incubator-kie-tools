```
Options:
  --help           Show help                                           [boolean]
  --version        Show version number                                 [boolean]
  --env            Name of the environment variables which value will be
                   compared to --eq.                                    [string]
  --bool           Boolean value to be used as condition                [string]
  --eq, --equals   Value to be compared with the condition supplied. Both --bool
                   and --env.                         [string] [default: "true"]
  --then           Command(s) to execute if the condition is true.
                                                              [array] [required]
  --else           Command(s) to execute if the condition is false.
                                                           [array] [default: []]
  --true-if-empty  If the environment variable is not set, makes the condition
                   be true.                           [boolean] [default: false]
  --silent         Hide info logs from output. Logs from commands will still
                   show.                              [boolean] [default: false]
  --force          Makes condition be true. Runs command(s) supplied to --then.
                                                      [boolean] [default: false]
  --catch          Command(s) to execute at the end of execution if one of the
                   commands being executed fails.          [array] [default: []]
  --finally        Command(s) to execute at the end of execution. Provided
                   commands will run even if one of the commands being executed
                   fails.                                  [array] [default: []]


CLI tool to help executing shell scripts conditionally with a friendly syntax on
Linux, macOS, and Windows.


__NOTE FOR WINDOWS USAGE__:
Because 'run-script-if' was created with Yarn/NPM scripts, environment variables
and sub-expression syntax (`$(expr)`) in mind, 'run-script-if' will force the
provided commands to be executed on PowerShell.

This is because Yarn and NPM default to the CMD shell on Windows, making it not
ideal for sub-expression-dependent commands.

Apart from using it on commands, it's also possible to use the sub-expression
syntax on boolean conditions, like:

$ run-script-if --bool "$(my-custom-command --isEnabled)" --then "echo 'Hello'"
```
