-- Table: public."table"

-- DROP TABLE public."table";

CREATE TABLE public."table"
(
    id integer NOT NULL DEFAULT nextval('table_id_seq'::regclass),
    name text COLLATE pg_catalog."default" DEFAULT 'Items'::text,
    CONSTRAINT table1_pk PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public."table"
    OWNER to postgres;