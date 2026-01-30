-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: springgreen8
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `movie`
--

DROP TABLE IF EXISTS `movie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movie` (
  `movie_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL,
  `poster_path` varchar(255) NOT NULL,
  `active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movie`
--

LOCK TABLES `movie` WRITE;
/*!40000 ALTER TABLE `movie` DISABLE KEYS */;
INSERT INTO `movie` VALUES (21,'아바타3','posters/avata3.jpg',1),(22,'어벤져스','posters/avengers.jpg',1),(23,'뜨거운피','posters/blood.jpg',1),(24,'범죄도시3','posters/crimecity3.jpg',1),(25,'주토피아2','posters/zootopia2.jpg',1);
/*!40000 ALTER TABLE `movie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `res_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `schedule_id` int NOT NULL,
  `seat_no` varchar(10) NOT NULL,
  `reserved_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`res_id`),
  UNIQUE KEY `uq_schedule_seat` (`schedule_id`,`seat_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_schedule` (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (4,5,12,'F2','2026-01-27 17:22:19'),(5,5,12,'F3','2026-01-27 17:22:21');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `schedule_id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `theater` varchar(20) NOT NULL,
  `show_date` date NOT NULL,
  `show_time` time NOT NULL,
  `price` int NOT NULL DEFAULT '12000',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`schedule_id`),
  UNIQUE KEY `uq_schedule` (`movie_id`,`theater`,`show_date`,`show_time`),
  KEY `idx_movie_date` (`movie_id`,`show_date`),
  KEY `idx_date_time` (`show_date`,`show_time`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,1,'1관','2026-01-28','10:30:00',12000,'2026-01-27 15:40:01'),(2,1,'1관','2026-01-28','14:00:00',12000,'2026-01-27 15:40:01'),(3,1,'1관','2026-01-28','11:00:00',12000,'2026-01-27 15:40:01'),(4,2,'2관','2026-01-28','13:20:00',11000,'2026-01-27 15:40:01'),(5,2,'2관','2026-01-28','16:10:00',11000,'2026-01-27 15:40:01'),(6,3,'3관','2026-01-28','19:30:00',13000,'2026-01-27 15:40:01'),(7,5,'1관','2026-01-30','12:20:00',12000,'2026-01-27 16:12:25'),(8,6,'1관','2026-01-30','14:29:00',12000,'2026-01-27 16:30:03'),(9,6,'1관','2026-01-30','17:01:00',12000,'2026-01-27 17:01:55'),(10,6,'1관','2026-01-30','12:10:00',12000,'2026-01-27 17:03:07'),(11,1,'1관','2026-01-27','17:03:00',12000,'2026-01-27 17:03:21'),(12,1,'1관','2026-01-30','12:10:00',12000,'2026-01-27 17:03:54'),(13,7,'1관','2026-01-30','12:10:00',12000,'2026-01-27 17:04:13'),(14,1,'1관','2026-01-28','17:14:00',12000,'2026-01-27 17:14:44'),(15,9,'1관','2026-01-28','17:20:00',12000,'2026-01-27 17:17:39'),(16,9,'2관','2026-01-30','17:20:00',12000,'2026-01-27 17:18:41'),(17,1,'2관','2026-01-28','16:20:00',14000,'2026-01-27 17:22:56'),(18,10,'1관','2026-01-28','18:20:00',12000,'2026-01-27 17:24:19'),(19,11,'1관','2026-01-28','18:30:00',12000,'2026-01-27 17:27:45'),(20,12,'1관','2026-01-28','17:40:00',12000,'2026-01-27 17:40:41'),(21,17,'1관','2026-01-28','09:22:00',12000,'2026-01-28 09:22:25'),(22,18,'1관','2026-01-28','09:22:00',12000,'2026-01-28 09:22:27'),(23,19,'1관','2026-01-28','09:22:00',12000,'2026-01-28 09:22:30'),(24,20,'2관','2026-01-28','09:22:00',12000,'2026-01-28 09:22:36'),(25,21,'1관','2026-01-28','11:28:00',12000,'2026-01-28 09:28:31'),(26,22,'2관','2026-01-28','10:20:00',12000,'2026-01-28 09:28:42'),(28,24,'3관','2026-01-28','10:20:00',12000,'2026-01-28 09:29:03'),(29,25,'4관','2026-01-28','11:20:00',12000,'2026-01-28 09:29:10'),(31,21,'1관','2026-01-28','14:35:00',12000,'2026-01-28 14:38:28'),(32,23,'1관','2026-01-30','12:20:00',12000,'2026-01-29 10:35:26');
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `login_id` varchar(30) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `login_id` (`login_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'atom','1234','아톰','2026-01-27 15:38:31'),(2,'tarot','1234','타로','2026-01-27 15:38:31'),(3,'skull','1234','이태경','2026-01-27 17:00:50'),(5,'ibk','1234','김이상','2026-01-27 17:21:30'),(7,'cost','1234','을지문덕','2026-01-28 14:05:45'),(9,'taroot','1234','skul','2026-01-29 10:34:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-30 16:40:40
