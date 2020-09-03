## TransactID Quickstart with Vault


We provide a very basic vault setup using a filesystem backend for storage.
Vault offers other storage backends that provide increased functionality: https://www.vaultproject.io/docs/configuration/storage

### Requirements:

- docker-compose v2

- docker >18

### Environment Variables:

Please set the following environment variable in your shell before running the steps outlined below:

- LOCAL_STORAGE - The location on host to store secrets (certs & keys) for data persistence.
_example: `export LOCAL_STORAGE=~/projects/transactid-library-java/keys`_

### Build and launch Vault container
`docker-compose up -d --build`

## Update bind mount permissions
`docker exec -d vault chown vault.vault keys/`

## Initialize Vault (First-time setup)
`docker exec -it vault vault operator init`

_Please save your unseal keys, as there is no way to recover these._

## Done!

---

## Garbage Collection
`docker-compose stop && docker-compose rm -f`