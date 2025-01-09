CREATE ROLE "hotel-user" WITH
    LOGIN
    SUPERUSER
    INHERIT
    CREATEDB
    CREATEROLE
    NOREPLICATION
    PASSWORD 'hotel-pass';

CREATE DATABASE hotels
    WITH
    OWNER = "hotel-user"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE ROLE "traveler-user" WITH
    LOGIN
    SUPERUSER
    INHERIT
    CREATEDB
    CREATEROLE
    NOREPLICATION
    PASSWORD 'traveler-pass';

CREATE DATABASE travelers
    WITH
    OWNER = "traveler-user"
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

GRANT ALL PRIVILEGES ON DATABASE postgres TO "hotel-user";
GRANT ALL PRIVILEGES ON DATABASE hotels TO "hotel-user";
GRANT ALL PRIVILEGES ON DATABASE hotels TO postgres;

GRANT ALL PRIVILEGES ON DATABASE postgres TO "traveler-user";
GRANT ALL PRIVILEGES ON DATABASE travelers TO postgres;
