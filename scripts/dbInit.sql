drop database noaabulletin;

######
#db
######
create database noaabulletin;
use noaabulletin;


######
#tables
######
 

  CREATE TABLE `noaabulletin`.`weatherZone` (
  `station` varchar(50) NOT NULL,
  `header` varchar(1000) NOT NULL,
  `zone_codes` varchar(100) DEFAULT NULL,	
  `zones` varchar(1000) DEFAULT NULL,	
  `station_timestamp` varchar(50) NOT NULL,
  `forecast` text NOT NULL,
  `date_created` datetime NOT NULL,
  `file_id` varchar(50) NOT NULL
 -- PRIMARY KEY (`product`,`header`,`zone`, `forecast`)
) ;

######
#procs
######

 

USE `noaabulletin`;
DROP procedure IF EXISTS `spWriteWeatherZoneData`;

DELIMITER $$
USE `noaabulletin`$$
CREATE PROCEDURE `spWriteWeatherZoneData` (station_in varchar(50), header_in varchar(1000), zone_codes_in varchar(100),  zones_in varchar(1000), station_timestamp_in varchar(50), forecast_in text, file_id_in varchar(50) )
BEGIN
	
    insert into weatherzone 
    values (station_in, 
			header_in, 
            zone_codes_in,
            zones_in,
       		station_timestamp_in,  
			forecast_in, 
			now(), 
			file_id_in);  
     
    
END$$

DELIMITER ;


