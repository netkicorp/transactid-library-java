FROM vault:1.4.1
COPY config/config.hcl /vault/config/config.hcl
COPY config/policy.hcl /vault/config/policy.hcl
ENV VAULT_ADDR='http://127.0.0.1:8200'
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["vault", "server", "-config=/vault/config/config.hcl"]