CREATE TABLE IF NOT EXISTS service_instance (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  service_instance_id VARCHAR(36) UNIQUE NOT NULL,
  description VARCHAR(255),
  operation_state VARCHAR(50),
  last_updated datetime
);

CREATE TABLE IF NOT EXISTS service_instance_binding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  service_instance_id VARCHAR(36) NOT NULL,
  binding_id VARCHAR(36) NOT NULL,
  description VARCHAR(255),
  operation_state VARCHAR(50),
  last_updated DATETIME,
  UNIQUE (service_instance_id, binding_id)
);
