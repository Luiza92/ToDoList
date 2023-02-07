/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 10.4.27-MariaDB : Database - to_do_list_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`to_do_list_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `to_do_list_db`;

/*Table structure for table `access_token` */

DROP TABLE IF EXISTS `access_token`;

CREATE TABLE `access_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expired_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `refresh_token_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `access_token` */

/*Table structure for table `approve` */

DROP TABLE IF EXISTS `approve`;

CREATE TABLE `approve` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `random_id` varchar(255) NOT NULL,
  `time_expires` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `approve` */

/*Table structure for table `images` */

DROP TABLE IF EXISTS `images`;

CREATE TABLE `images` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `images` */

insert  into `images`(`id`,`file_name`,`path`) values 
(16,'50190869-c90a-4665-bff3-62407418d329_photo_2023-01-11_17-07-00.jpg','uploads/image/50190869-c90a-4665-bff3-62407418d329_photo_2023-01-11_17-07-00.jpg'),
(21,'4c77722d-b717-47c4-ae1a-cb1b52da5fa6_photo_2023-01-11_17-07-00.jpg','uploads/image/4c77722d-b717-47c4-ae1a-cb1b52da5fa6_photo_2023-01-11_17-07-00.jpg'),
(24,'e60a5313-7e04-4de8-9f61-753079563dee_photo_2023-01-11_17-07-11.jpg','uploads/image/e60a5313-7e04-4de8-9f61-753079563dee_photo_2023-01-11_17-07-11.jpg');

/*Table structure for table `permissions` */

DROP TABLE IF EXISTS `permissions`;

CREATE TABLE `permissions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `role_id` int(11) NOT NULL,
  `enable` smallint(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `permissions` */

insert  into `permissions`(`id`,`name`,`role_id`,`enable`) values 
(1,'update',1,1),
(2,'update',2,1),
(3,'add',1,1),
(4,'add',2,0),
(5,'delete',1,1),
(6,'delete',2,0),
(7,'view',1,1),
(8,'view',2,1);

/*Table structure for table `refresh_token` */

DROP TABLE IF EXISTS `refresh_token`;

CREATE TABLE `refresh_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expired_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `refresh_token` */

insert  into `refresh_token`(`id`,`user_id`,`token`,`created_at`,`expired_at`) values 
(27,13,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YSIsImV4cCI6MTY4MDM1OTc3MX0._6OolPLJ1-GASj2rorz8-4x0BVdYSlI3rfkU-h05aWF9YdIMc6nIGfnAPLYkzG7dxuJqeiIiZLJGTG3yCiauwg','2023-01-31 18:36:11','2023-04-01 18:36:11'),
(28,13,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YSIsImV4cCI6MTY4MDQzMTc2NX0.324KlMHQ8PvKWHRW84Mf1Oras5GndrBhbfcF9li23Gbw4chsnefavHa23P7-otHcgevxD8yAWrKz0jB-_7akVg','2023-02-01 14:36:05','2023-04-02 14:36:05'),
(29,13,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YSIsImV4cCI6MTY4MDQzNTM4OX0.3TdcXDwFG5GPMeGSsosDBaZBliBnXKNvYmDejmYZ6oj-Xt2V9eTvh9WjLQ3iKu_6pa1ZmnNVn5ijBIzHMFSBNA','2023-02-01 15:36:29','2023-04-02 15:36:29'),
(30,1,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YSIsImV4cCI6MTY4MDYxMDg3N30.s1nZQbYuv4Q2lFKreIlVP9B0H9KwrqW_v3i9EJpz22WIK9oYYKefaq0LSbPf2dwZl8jqS38ZAKbEcd_wNAGOeQ','2023-02-03 16:21:17','2023-04-04 16:21:17'),
(31,1,'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsdWl6YSIsImV4cCI6MTY4MDYxMjE3OH0.ikq5DPP7qsUQ9S8Nt2lP1ElQZgYhwMez7wUMqH9nfe8JCE1O7C2Hrs0TuNB1PhtxAzRW3pI6tLeQ2PE2BLryWA','2023-02-03 16:42:58','2023-04-04 16:42:58');

/*Table structure for table `roles` */

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `roles` */

insert  into `roles`(`id`,`name`) values 
(1,'administrator'),
(2,'moderator'),
(3,'user');

/*Table structure for table `to_do_list` */

DROP TABLE IF EXISTS `to_do_list`;

CREATE TABLE `to_do_list` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  `description` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expired_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_complete` smallint(6) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `to_do_list` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `image_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `status` smallint(6) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`,`username`,`email`),
  UNIQUE KEY `UNIQUE` (`username`),
  UNIQUE KEY `UNIQUE_EMAILe` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`first_name`,`last_name`,`email`,`password`,`created_at`,`updated_at`,`image_id`,`role_id`,`status`) values 
(1,'luiza','luiza','harutyunyan','luiza.harutyunyan20@gmail.com','$2a$10$e7Y6mq4zG0G4gQ2jKZ5ppOHwZYOP1edhn832z85oVSo7Fkv4ZezTG','2023-01-31 18:36:42','2023-02-03 16:42:54',24,1,1);

/*!50106 set global event_scheduler = 1*/;

/* Event structure for event `token_delete` */

/*!50106 DROP EVENT IF EXISTS `token_delete`*/;

DELIMITER $$

/*!50106 CREATE DEFINER=`root`@`localhost` EVENT `token_delete` ON SCHEDULE EVERY 1 MINUTE STARTS '2023-02-07 14:53:23' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
  DELETE
  FROM
    access_token
  WHERE  expired_at  < CURRENT_TIMESTAMP ();
   
    DELETE
    from
     refresh_token
  WHERE  expired_at  < CURRENT_TIMESTAMP ();
    END */$$
DELIMITER ;

/* Event structure for event `unauthorization_user_delete` */

/*!50106 DROP EVENT IF EXISTS `unauthorization_user_delete`*/;

DELIMITER $$

/*!50106 CREATE DEFINER=`root`@`localhost` EVENT `unauthorization_user_delete` ON SCHEDULE EVERY 1 MINUTE STARTS '2023-02-07 14:47:35' ON COMPLETION NOT PRESERVE ENABLE DO begin
  DELETE
  FROM
    users
  WHERE STATUS = 0
    AND id IN
    (SELECT
      user_id
    FROM
      approve
    WHERE approve.`time_expires` < CURRENT_TIMESTAMP ());
    delete
    FROM
      approve
    WHERE approve.`time_expires` < CURRENT_TIMESTAMP ();
    end */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
