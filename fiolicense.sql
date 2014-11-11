CREATE TABLE `fiolicense` (
  `serial_number` varchar(45) NOT NULL,
  `organizationn_name` varchar(45) DEFAULT NULL,
  `organization_unit` varchar(45) DEFAULT NULL,
  `contact_name` varchar(45) DEFAULT NULL,
  `locality_name` varchar(45) DEFAULT NULL,
  `state_name` varchar(45) DEFAULT NULL,
  `country_code` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `signature_algorithm` varchar(45) DEFAULT NULL,
  `validity_period` time(6) DEFAULT NULL,
  `activation_date` timestamp(6) NULL DEFAULT NULL,
  `expiration_date` timestamp(6) NULL DEFAULT NULL,
  `lic_file_byte_size` int(11) DEFAULT NULL,
  `lic_file_checksum` varchar(100) DEFAULT NULL,
  `last_agent_com_time` timestamp NULL DEFAULT NULL,
  `last_agent_com_action` varchar(45) DEFAULT NULL,
  `last_agent_com_action_result` varchar(45) DEFAULT NULL,
  `app_version` varchar(45) DEFAULT NULL,
  `current_status` varchar(45) DEFAULT NULL,
  `is_enabled` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`serial_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='poc licence table';

INSERT INTO `fiolicense` VALUES ('sn0001','Test Company',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,11358,'3b83ef96387f14655fc854ddc3c6bd57',NULL,NULL,NULL,NULL,NULL,NULL);



