#!/bin/bash
set -euo pipefail

readonly SCS_SECRETS_LAST_PASS_ID="1938546022346916823"

secrets_file=$(mktemp).yml

fetch_secrets() {
  lpass show --notes "${SCS_SECRETS_LAST_PASS_ID}" >>"${secrets_file}"
}

set_app_broker_pipeline() {
  echo "Setting app-broker-samples pipeline..."
  fly -t scs set-pipeline -p app-broker-samples -c pipeline.yml -l config-concourse-master.yml -l "${secrets_file}"
}

cleanup() {
  rm "${secrets_file}"
}

trap "cleanup" EXIT

main() {
  fly -t scs sync

  pushd "$(dirname $0)/.." >/dev/null
  fetch_secrets
  set_app_broker_pipeline
  popd >/dev/null
}

main
