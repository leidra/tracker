-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         5.7.21-log - MySQL Community Server (GPL)
-- SO del servidor:              Win64
-- HeidiSQL Versión:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Volcando estructura de base de datos para acufade
CREATE DATABASE IF NOT EXISTS `acufade` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `acufade`;

-- Volcando estructura para tabla acufade.assistance
CREATE TABLE IF NOT EXISTS `assistance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `accuracy` double NOT NULL,
  `end` tinyblob,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  `patient_name` varchar(255) DEFAULT NULL,
  `start` tinyblob,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.users_role
CREATE TABLE IF NOT EXISTS `users_role` (
  `role_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3qjq7qsiigxa82jgk0i0wuq3g` (`role_id`),
  CONSTRAINT `FK3qjq7qsiigxa82jgk0i0wuq3g` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKpp1wq290hao33q6cx7kt5hrk9` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.user_assistances
CREATE TABLE IF NOT EXISTS `user_assistances` (
  `user_id` bigint(20) NOT NULL,
  `assistances_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`assistances_id`),
  UNIQUE KEY `UK_2gdsqju343emg1i5ipdyga9pd` (`assistances_id`),
  CONSTRAINT `FK4nirvxhixxvpg9ciha7gk6cig` FOREIGN KEY (`assistances_id`) REFERENCES `assistance` (`id`),
  CONSTRAINT `FKg0xco6ktgcpd5qkhm2um7y0n3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- La exportación de datos fue deseleccionada.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
