-- create-db-historicaltimeseries.sql: Historical time-series Master

-- design has one main document with data points handled separately
-- bitemporal versioning exists at the document level
-- each time a document is changed, a new row is written
-- with only the end instant being changed on the old row

-- Data point versioning is slightly different.
-- Data points are inserted on a daily basis with a single version instant.
-- There may be a delay between the value becoming available and the insertion
-- which the version instant models, ensuring the exact state previously viewed.
-- A new version of a point may not be created (the insertion of the point
-- is the versioned item, and treated as being at the document level).
-- A data point may however be corrected. A single instant recorded for this.
-- The actual data point is the latest matching these criteria:
--  hts_point.ver_instant <= search_version_instant &&
--  hts_point.corr_instant <= search_correction_instant

CREATE SEQUENCE hts_master_seq
    START WITH 1000 INCREMENT BY 1 NO CYCLE;
CREATE SEQUENCE hts_idkey_seq
    start with 1000 increment by 1 no cycle;
CREATE SEQUENCE hts_dimension_seq
    START WITH 1000 INCREMENT BY 1 NO CYCLE;
-- "as bigint" required by Derby, not accepted by Postgresql

CREATE TABLE hts_name (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ix_hts_name_name ON hts_name(name);

CREATE TABLE hts_data_field (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ix_hts_data_field_name ON hts_data_field(name);

CREATE TABLE hts_data_source (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ix_hts_data_source_name ON hts_data_source(name);

CREATE TABLE hts_data_provider (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ix_hts_data_provider_name ON hts_data_provider(name);

CREATE TABLE hts_observation_time (
    id bigint NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ix_hts_observation_time_name ON hts_observation_time(name);

CREATE TABLE hts_document (
    id bigint NOT NULL,
    oid bigint NOT NULL,
    ver_from_instant timestamp NOT NULL,
    ver_to_instant timestamp NOT NULL,
    corr_from_instant timestamp NOT NULL,
    corr_to_instant timestamp NOT NULL,
    name_id bigint NOT NULL,
    data_field_id bigint NOT NULL,
    data_source_id bigint NOT NULL,
    data_provider_id bigint NOT NULL,
    observation_time_id bigint NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT hts_fk_doc2doc FOREIGN KEY (oid) REFERENCES hts_document (id),
    CONSTRAINT hts_chk_doc_ver_order CHECK (ver_from_instant <= ver_to_instant),
    CONSTRAINT hts_chk_doc_corr_order CHECK (corr_from_instant <= corr_to_instant),
    CONSTRAINT hts_fk_doc2name FOREIGN KEY (name_id) REFERENCES hts_name (id),
    CONSTRAINT hts_fk_doc2data_field FOREIGN KEY (data_field_id) REFERENCES hts_data_field (id),
    CONSTRAINT hts_fk_doc2data_source FOREIGN KEY (data_source_id) REFERENCES hts_data_source (id),
    CONSTRAINT hts_fk_doc2data_provider FOREIGN KEY (data_provider_id) REFERENCES hts_data_provider (id),
    CONSTRAINT hts_fk_doc2observation_time FOREIGN KEY (observation_time_id) REFERENCES hts_observation_time (id)
);
CREATE INDEX ix_hts_hts_oid ON hts_document(oid);
CREATE INDEX ix_hts_hts_ver_from_instant ON hts_document(ver_from_instant);
CREATE INDEX ix_hts_hts_ver_to_instant ON hts_document(ver_to_instant);
CREATE INDEX ix_hts_hts_corr_from_instant ON hts_document(corr_from_instant);
CREATE INDEX ix_hts_hts_corr_to_instant ON hts_document(corr_to_instant);
CREATE INDEX ix_hts_hts_name_id ON hts_document(name_id);
CREATE INDEX ix_hts_hts_data_field ON hts_document(data_field_id);
CREATE INDEX ix_hts_hts_data_source ON hts_document(data_source_id);
CREATE INDEX ix_hts_hts_data_provider ON hts_document(data_provider_id);
CREATE INDEX ix_hts_hts_observation_time ON hts_document(observation_time_id);

CREATE TABLE hts_idkey (
    id bigint NOT NULL,
    key_scheme varchar(255) NOT NULL,
    key_value varchar(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT hts_chk_idkey UNIQUE (key_scheme, key_value)
);
CREATE INDEX ix_hts_key_scheme ON hts_idkey(key_scheme);
CREATE INDEX ix_hts_key_value ON hts_idkey(key_value);

CREATE TABLE hts_doc2idkey (
    doc_id bigint NOT NULL,
    idkey_id bigint NOT NULL,
    valid_from date NOT NULL,
    valid_to date NOT NULL,
    PRIMARY KEY (doc_id, idkey_id, valid_from, valid_to),
    CONSTRAINT hts_fk_htsidkey2doc FOREIGN KEY (doc_id) REFERENCES hts_document (id),
    CONSTRAINT hts_fk_htsidkey2idkey FOREIGN KEY (idkey_id) REFERENCES hts_idkey (id)
);
-- hts_doc2idkey is fully dependent of hts_document

CREATE TABLE hts_point (
    doc_oid bigint NOT NULL,
    point_date date NOT NULL,
    ver_instant timestamp NOT NULL,
    corr_instant timestamp NOT NULL,
    point_value double precision,
    PRIMARY KEY (doc_oid, point_date, ver_instant, corr_instant)
);
-- null value used to indicate point was deleted
