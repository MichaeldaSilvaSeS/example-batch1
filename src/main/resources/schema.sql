
CREATE TABLE IF NOT EXISTS tbl_proposal (
    id_proposal INT NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS tbl_client (
    id_proposal INT NOT NULL,
    client_name VARCHAR(50) NOT NULL
);