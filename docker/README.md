## TransactID Quickstart with Vault


We provide a very basic vault setup using a filesystem backend for storage.
Vault offers other storage backends that provide increased functionality: https://www.vaultproject.io/docs/configuration/storage

### Requirements:

- docker-compose v2

- docker >18

### Environment Variables:

Please set the following environment variable in your shell before running the steps outlined below:

- APP_INSTALL - The location you have installed our app.
_example: `export APP_INSTALL=~/projects/transactid-library-java`_

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