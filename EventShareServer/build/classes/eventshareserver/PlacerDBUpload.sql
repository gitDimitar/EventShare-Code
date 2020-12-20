DROP DATABASE IF EXISTS a8871066_placer;
CREATE DATABASE IF NOT EXISTS `a8871066_placer`;

USE `a8871066_placer`;

/*Table structure for table `a8871066_placer` */


	
	DROP TABLE IF EXISTS `PlacerLogger`;

	CREATE TABLE `PlacerLogger`(
	`lineID` int AUTO_INCREMENT,
	`fileName` varchar(64) NOT NULL,
	`city` varchar(64) NOT NULL,
    `place` varchar(64) NOT NULL,
	`recordsDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (lineID));
	
	DROP TABLE IF EXISTS `Likes`;
	
	CREATE TABLE `Likes`(
	`fileName` varchar(64) NOT NULL,
	`imgLike` int(8) NOT NULL,
	PRIMARY KEY (fileName));
	
	DROP TABLE IF EXISTS `LikesNames`;
	
	CREATE TABLE `LikesNames`(
	`fileName` varchar(64) NOT NULL,
	`userName` varchar(64) NOT NULL,
	PRIMARY KEY (fileName));
	
	DROP TABLE IF EXISTS `Comments`;
	
	CREATE TABLE `Comments`(
	`fileName` varchar(64) NOT NULL,
	`imgComment` varchar(256) NOT NULL,
	FOREIGN KEY (fileName) REFERENCES Likes(fileName)
	ON DELETE CASCADE);