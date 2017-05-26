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
  `id` VARCHAR(20) NOT NULL,
  `name` VARCHAR(45) NULL,
  `description` VARCHAR(2000) NULL);

  
  CREATE TABLE `noaabulletin`.`weather` (
  `id` VARCHAR(20) NOT NULL,
  `day` VARCHAR(20) NULL,
  `raw_data` TEXT NULL,
  `date_created` DATE NULL,
  `file_id` VARCHAR(20) NULL,
  PRIMARY KEY (`id`));
  