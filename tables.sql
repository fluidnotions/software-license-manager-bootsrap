CREATE TABLE `fiolicense` (
  `serial_number` varchar(45) NOT NULL,
  `contact_name` varchar(45) DEFAULT NULL,
  `organization_unit` varchar(45) DEFAULT NULL,
  `organizationn_name` varchar(45) DEFAULT NULL,
  `locality_name` varchar(45) DEFAULT NULL,
  `state_name` varchar(45) DEFAULT NULL,
  `country_code` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `signature_algorithm` varchar(45) DEFAULT NULL,
  `validity_period` varchar(45) DEFAULT NULL,
  `activation_date` timestamp(6) NULL DEFAULT NULL,
  `expiration_date` timestamp(6) NULL DEFAULT NULL,
  `lic_file_byte_size` int(11) DEFAULT NULL,
  `lic_file_checksum` varchar(100) DEFAULT NULL,
  `last_agent_com_time` timestamp NULL DEFAULT NULL,
  `last_agent_com_action` varchar(45) DEFAULT NULL,
  `last_agent_com_action_result` varchar(45) DEFAULT NULL,
  `app_version` varchar(45) DEFAULT NULL,
  `current_status` varchar(45) DEFAULT NULL,
  `is_enabled` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`serial_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='poc licence table';

CREATE TABLE `fiolicenseadminevent` (
  `eventtimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `eventtype` varchar(45) DEFAULT NULL,
  `eventdetails` varchar(45) DEFAULT NULL,
  `eventcomment` varchar(100) DEFAULT NULL,
  `applicationtimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `hasbeenapplied` tinyint(1) DEFAULT NULL,
  `byusername` varchar(45) DEFAULT NULL,
  `serialnumber` varchar(45) NOT NULL,
  `pk` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;







