curl 'https://api.github.com/graphql' \
  -X POST -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  --data-raw '{"query":"query RepoFiles($owner: String!, $name: String!) {\n  repository(owner: $owner, name: $name) {\n    object(expression: \"HEAD:\") {\n      ... on Tree {\n        entries {\n          name\n          type\n          mode\n          \n          object {\n            ... on Blob {\n              byteSize\n              text\n              isBinary\n            }\n          }\n        }\n      }\n    }\n  }\n}","variables":{"owner":"j4m3s-s","name":"playground"},"operationName":"RepoFiles"}' \
  -H 'Authorization: bearer $BEARER' \
  | jq .
