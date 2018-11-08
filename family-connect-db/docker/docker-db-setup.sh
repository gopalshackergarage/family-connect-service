#!/usr/bin/env bash
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE family;

    CREATE ROLE admin WITH LOGIN PASSWORD 'admin';

    GRANT ALL ON DATABASE family TO admin;
    GRANT ALL ON ALL TABLES IN SCHEMA public to admin;
EOSQL