#!/usr/bin/env bash
set -euo pipefail

api="${1:-http://localhost:8080/api}"
temporario=$(mktemp -d)
trap 'rm -r -- "$temporario"' EXIT

curl --fail --silent --show-error --retry 15 --retry-connrefused --retry-delay 2 \
  "$api/autores" >/dev/null

printf 'POST %s/autores\n' "$api"
status=$(curl --silent --show-error --output "$temporario/autor.json" --write-out '%{http_code}' \
  --header 'Content-Type: application/json' \
  --data '{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}' \
  "$api/autores")
printf 'HTTP %s\n' "$status"
python3 -m json.tool "$temporario/autor.json"
test "$status" = 201
autor_id=$(python3 -c 'import json, sys; print(json.load(sys.stdin)["id"])' < "$temporario/autor.json")

printf '\nPOST %s/livros\n' "$api"
status=$(curl --silent --show-error --output "$temporario/livro.json" --write-out '%{http_code}' \
  --header 'Content-Type: application/json' \
  --data "{\"titulo\":\"Dom Casmurro\",\"isbn\":\"978-8535910667\",\"anoPublicacao\":1899,\"genero\":\"Romance\",\"autor\":{\"id\":$autor_id}}" \
  "$api/livros")
printf 'HTTP %s\n' "$status"
python3 -m json.tool "$temporario/livro.json"
test "$status" = 201
python3 - "$temporario/livro.json" "$autor_id" <<'PY'
import json
import sys

with open(sys.argv[1]) as dados:
    livro = json.load(dados)
assert livro["autor"]["id"] == int(sys.argv[2])
assert livro["autor"]["nome"] == "Machado de Assis"
print("Vínculo entre livro e autor confirmado.")
PY
