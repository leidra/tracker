-- Volcando estructura para tabla acufade.assistance
CREATE TABLE IF NOT EXISTS `assistance` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `accuracy` double NOT NULL,
  `end` date,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  `patient_name` varchar(255) DEFAULT NULL,
  `start` date,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`)
);

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.users_role
CREATE TABLE IF NOT EXISTS `users_role` (
  `role_id` int(20) DEFAULT NULL,
  `id` int(20) NOT NULL,
  PRIMARY KEY (`id`, `role_id`),
  CONSTRAINT `FK3qjq7qsiigxa82jgk0i0wuq3g` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `FKpp1wq290hao33q6cx7kt5hrk9` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla acufade.user_assistances
CREATE TABLE IF NOT EXISTS `user_assistances` (
  `user_id` int(20) NOT NULL,
  `assistances_id` int(20) NOT NULL,
  PRIMARY KEY (`user_id`,`assistances_id`),
  UNIQUE KEY `UK_2gdsqju343emg1i5ipdyga9pd` (`assistances_id`),
  CONSTRAINT `FK4nirvxhixxvpg9ciha7gk6cig` FOREIGN KEY (`assistances_id`) REFERENCES `assistance` (`id`),
  CONSTRAINT `FKg0xco6ktgcpd5qkhm2um7y0n3` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ;
