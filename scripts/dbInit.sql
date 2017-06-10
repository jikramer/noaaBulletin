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
  `product` varchar(50) NOT NULL,
  `header` varchar(1000) NOT NULL,
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
CREATE PROCEDURE `spWriteWeatherZoneData` (product_in varchar(50), header_in varchar(1000),  zones_in varchar(1000), station_timestamp_in varchar(50), forecast_in text, file_id_in varchar(50) )
BEGIN
	
    insert into weatherzone 
    values (product_in, 
			header_in, 
            zones_in,
     		station_timestamp_in,  
			forecast_in, 
			now(), 
			file_id_in); 
     
    
END$$

DELIMITER ;


