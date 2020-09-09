storage "file" {
    path    = "/keys"
    default_lease_ttl = "168h"
    max_lease_ttl = "750h"
    log_format = "standard"
    log_level = "Info"
    disable_mlock = 0
  }
  
  listener "tcp" {
    address     = "0.0.0.0:8200"
    tls_disable = 1
  }

  api_addr = "http://0.0.0.0:8200"