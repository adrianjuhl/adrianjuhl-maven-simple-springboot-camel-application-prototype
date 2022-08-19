#!/usr/bin/env bash

# Build and deploy (to sonatype staging in preparation for release to maven central).

usage()
{
  cat <<USAGE_TEXT
Usage: $(basename "${BASH_SOURCE[0]}") [-h | --help] [-v | --verbose]
Build and deploy (to sonatype staging in preparation for release to maven central).
Available options:
-h, --help         Print this help and exit
-v, --verbose      Print script debug info
USAGE_TEXT
}

main()
{
  set -e  # Exit immediately if a command exits with a non-zero status.
  initialize
  parse_script_params "${@}"
  mvn_clean_deploy
}

mvn_clean_deploy()
{
  mvn \
      clean \
      deploy \
      -Pmaven-central-release-requirements
}

parse_script_params()
{
  # default values of variables set from params
  while true; do
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
  echo >&2 -e "${1-}"
}

# Main entry into the script - call the main() function
main "${@}"
