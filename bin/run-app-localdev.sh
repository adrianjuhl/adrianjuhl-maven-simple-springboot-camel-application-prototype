#!/usr/bin/env bash

# Run simple-springboot-camel-application-prototype with localdev config.

usage()
{
  cat <<USAGE_TEXT
Usage: $(basename "${BASH_SOURCE[0]}") [-h | --help] [-v | --verbose]
Run simple-springboot-camel-application-prototype with localdev config.
Available options:
-h, --help         Print this help and exit
-v, --verbose      Print script debug info
USAGE_TEXT
}

main()
{
  initialize
  parse_script_params "${@}"
  run_app_with_localdev_config
}

run_app_with_localdev_config() {
  mvn clean package \
  && \
  SPRING_APPLICATION_JSON=$(cat config/localdev.json) \
      java \
          -jar target/simple-springboot-camel-application-prototype.jar \
    | xargs -n1 -d $'\n' sh -c 'for arg do echo "$arg" \
    | jq -S -C || echo "Plain (non-jq) output: $arg"; done' _ \
    | tee target/output-logging.txt
}

parse_script_params()
{
  while [ "${#}" -gt 0 ]
  do
    case "${1-}" in
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
}

initialize()
{
  set -o pipefail
  THIS_SCRIPT_PROCESS_ID=$$
  THIS_SCRIPT_DIRECTORY="$(dirname "$(readlink -f "${0}")")"
  initialize_abort_script_config
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
