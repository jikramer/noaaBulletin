drop database noaabulletin;

######
#db
######
create database noaabulletin;
use noaabulletin;


######
#tables
######

CREATE TABLE `noaabulletin`.`zone` (
  `station_id` VARCHAR(50) NOT NULL,
  `station_location` VARCHAR(100) NULL,
  `description` VARCHAR(2000) NULL,
  PRIMARY KEY (`station_id`)
  );

  CREATE TABLE `weather` (
  `station_id` varchar(50) NOT NULL,
  `special_info` varchar(1000) DEFAULT NULL,
  `station_location` varchar(100) NOT NULL,
  `station_timestamp` varchar(50) NOT NULL,
  `raw_data` text NOT NULL,
  `date_created` datetime NOT NULL,
  `file_id` varchar(50) NOT NULL,
  PRIMARY KEY (`station_id`,`station_location`,`station_timestamp`)
) ;


######
#procs
######


USE `noaabulletin`;
DROP procedure IF EXISTS `spWriteWeatherData`;

DELIMITER $$
USE `noaabulletin`$$
CREATE PROCEDURE `spWriteWeatherData` (station_id_in varchar(50), special_info_in varchar(1000), station_location_in varchar(1000), station_timestamp_in varchar(50), raw_data_in text, file_id_in varchar(50) )
BEGIN
	
    insert into weather 
    values (station_id_in, 
			special_info_in, 
			station_location_in, 
			station_timestamp_in,  
			raw_data_in, 
			now(), 
			file_id_in); 
    
    insert into zone
    values( station_id_in,
			station_location_in,
            '');
    
END$$

DELIMITER ;