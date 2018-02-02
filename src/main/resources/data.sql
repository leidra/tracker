INSERT INTO `role` (`id`, `name`) VALUES
	(1, 'CENTRO'),
	(2, 'DOMICILIO'),
	(3, 'ADMIN');

INSERT INTO `user` (`id`, `enabled`, `password`, `username`) VALUES
	(1, '1', '$2a$10$pDUBTgoyphkrt8YwrqqiAOynr6w0a1kJYLgt.R7rlsJDBmaYkLEwW', 'centro'),
	(2, '1', '$2a$10$i18VbF6uKsz34bwnmwpSw.VyJ6wKuw4PfGW1H7cPUcH6QaWg3Asku', 'domicilio'),
	(3, '1', '$2a$10$LBwRRt9TCdS7k0o7Meqd4.As8rXII8GNwxkuTc3TCyXnoDwfGCImS', 'admin');

INSERT INTO `users_role` (`role_id`, `id`) VALUES
	(1, 1),
	(2, 2),
	(3, 3);
