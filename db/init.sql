CREATE TABLE IF NOT EXISTS offices (
                                       id SERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
                                       zip VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS shipments (
                                         id SERIAL PRIMARY KEY,
                                         office_id INT NOT NULL,
                                         shipment_type VARCHAR(20) NOT NULL,
                                         CONSTRAINT fk_office FOREIGN KEY (office_id) REFERENCES offices(id)
);
