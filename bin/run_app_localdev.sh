#!/usr/bin/env bash

# Run simple-springboot-camel-application-prototype with localdev config.

usage()
{
  cat <<USAGE_TEXT
Usage: $(basename "${BASH_SOURCE[0]}") [-h | --help] [-v | --verbose]
                       [--enable_camel_developer_console=<true|false>]
Run simple-springboot-camel-application-prototype with localdev config.
Available options:
-h, --help         Print this help and exit
-v, --verbose      Print script debug info
    --enable_camel_developer_console=<true|false>  Enable/disable the Camel developer console (default: true)
USAGE_TEXT
}

main()
{
  initialize
  parse_script_params "${@}"
  run_app_with_localdev_config
}

run_app_with_localdev_config() {
  mvn_package
  SPRING_APPLICATION_JSON=$(cat config/localdev.json) \
      java \
          -jar target/simple-springboot-camel-application-prototype.jar \
    | xargs -n1 -d $'\n' sh -c 'for arg do echo "$arg" \
    | jq -S -C || echo "Plain (non-jq) output: $arg"; done' _ \
    | tee target/output-logging.txt
}

mvn_package()
{
  if [ "${ENABLE_CAMEL_DEVELOPER_CONSOLE}" = "${TRUE_STRING}" ]; then
    mvn clean package -Penable-camel-developer-console
  else
    mvn clean package
  fi
  local last_command_return_code="$?"
  if [ "${last_command_return_code}" -gt 0 ]; then
    msg "Error: mvn clean package failed."
    abort_script
  fi
}

parse_script_params()
{
  ENABLE_CAMEL_DEVELOPER_CONSOLE="${TRUE_STRING}"
  ENABLE_CAMEL_DEVELOPER_CONSOLE_PARAM=""
  while [ "${#}" -gt 0 ]
  do
    case "${1-}" in
      --enable_camel_developer_console=*)
        ENABLE_CAMEL_DEVELOPER_CONSOLE_PARAM="${1#*=}"
        ;;
      --help | -h)
        usage
        exit
        ;;
      --verbose | -v)
        set -x
        ;;
      -?*)
        msg "Error: Unknown parameter: ${1}"
        msg "Use --help for usage help"
        abort_script
        ;;
      *) break ;;
    esac
    shift
  done
  case "${ENABLE_CAMEL_DEVELOPER_CONSOLE_PARAM}" in
    "")
      ENABLE_CAMEL_DEVELOPER_CONSOLE="${TRUE_STRING}"
      ;;
    "true")
      ENABLE_CAMEL_DEVELOPER_CONSOLE="${TRUE_STRING}"
      ;;
    "false")
      ENABLE_CAMEL_DEVELOPER_CONSOLE="${FALSE_STRING}"
      ;;
    *)
      msg "Error: Invalid enable_camel_developer_console param value: ${ENABLE_CAMEL_DEVELOPER_CONSOLE_PARAM}, expected one of: true, false"
      abort_script
      ;;
  esac
  #echo "ENABLE_CAMEL_DEVELOPER_CONSOLE is: ${ENABLE_CAMEL_DEVELOPER_CONSOLE}"
}

initialize()
{
  set -o pipefail
  THIS_SCRIPT_PROCESS_ID=$$
  THIS_SCRIPT_DIRECTORY="$(dirname "$(readlink -f "${0}")")"
  initialize_abort_script_config

  # Bash doesn't have a native true/false, just strings and numbers,
  # so this is as clear as it can be, using, for example:
  # if [ "${my_boolean_var}" = "${TRUE_STRING}" ]; then
  # where previously 'my_boolean_var' is set to either ${TRUE_STRING} or ${FALSE_STRING}
  TRUE_STRING="true_string"
  FALSE_STRING="false_string"
}

initialize_abort_script_config()
{
  # Exit shell script from within the script or from any subshell within this script - adapted from:
  # https://cravencode.com/post/essentials/exit-shell-script-from-subshell/
  # Exit with exit status 1 if this (top level process of this script) receives the SIGUSR1 signal.
  # See also the abort_script() function which sends the signal.
  trap "exit 1" SIGUSR1
}

abort_script()
{
  echo >&2 "aborting..."
  kill -SIGUSR1 ${THIS_SCRIPT_PROCESS_ID}
  exit
}

msg()
{
  echo >&2 -e "${@}"
}

# Main entry into the script - call the main() function
main "${@}"
