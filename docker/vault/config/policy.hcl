path "/keys/*" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}

path "/certs/*" {
  capabilities = [ "create", "read", "update", "delete", "list" ]
}