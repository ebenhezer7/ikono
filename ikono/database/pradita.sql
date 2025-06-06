CREATE DATABASE  IF NOT EXISTS `pradita` /*!40100 DEFAULT CHARACTER SET utf8mb4 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pradita`;
-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pradita
-- ------------------------------------------------------
-- Server version	8.0.27

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
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `code` varchar(5) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT '0.00',
  `quantity` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`code`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES ('AB001','Indomie Goreng',4100.00,20.00),('AB002','Indomie Kuah',4000.00,10.00);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `code` varchar(5) NOT NULL,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`code`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES ('O0001','2022-11-17 14:41:23');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `code` varchar(5) NOT NULL,
  `line` int NOT NULL,
  `itemcode` varchar(5) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `quantity` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`line`,`code`),
  KEY `itemcode_idx` (`itemcode`),
  KEY `code_idx` (`code`),
  CONSTRAINT `code` FOREIGN KEY (`code`) REFERENCES `order` (`code`),
  CONSTRAINT `itemcode` FOREIGN KEY (`itemcode`) REFERENCES `item` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
INSERT INTO `order_detail` VALUES ('O0001',1,'AB001','Indomie Goreng',4100.00,1.00),('O0001',2,'AB002','Indomie Kuah',4000.00,2.00);
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


use pradita;

delete from order_detail where code <> '';
delete from `order` where code <> '';
delete from item where code <> '';

insert into item(code, name, quantity, price) values('IM001', 'Indomie Goreng', 30, 4100);
insert into item(code, name, quantity, price) values('IM002', 'Indomie Kuah', 20, 4000);

insert into `order`(code, note) values('10001', 'Penjualan Pertama');
insert into order_detail(code, line, itemcode, name, quantity, price) 
  values('10001', 1, 'IM001', 'Indomie Goreng', 1, 4100);
insert into order_detail(code, line, itemcode, name, quantity, price) 
  values('10001', 2, 'IM002', 'Indomie Kuah', 2, 4000);
    
insert into `order`(code, note) values('10002', 'Penjualan Kedua');
insert into order_detail(code, line, itemcode, name, quantity, price) 
  values('10002', 1, 'IM001', 'Indomie Goreng', 2, 4100);
    
insert into `order`(code, note) values('10003', 'Penjualan Ketiga');
insert into order_detail(code, line, itemcode, name, quantity, price) 
  values('10003', 1, 'IM002', 'Indomie Kuah', 4, 4000);

select t1.code, t1.date, t1.note, t2.line, t2.itemcode, t2.name, t2.quantity, t2.price, 
  (t2.quantity * t2.price) linetotal
  from `order` t1, order_detail t2 where t1.code = t2.code;


--unit
-- Buat tabel unit_master jika belum ada
CREATE TABLE IF NOT EXISTS `unit_master` (
  `unit_code` VARCHAR(5) NOT NULL,
  `unit_name` VARCHAR(20) NOT NULL,
  `base_unit_category` VARCHAR(20) DEFAULT NULL, -- e.g., 'weight', 'volume', 'count'
  PRIMARY KEY (`unit_code`),
  UNIQUE KEY `unit_code_UNIQUE` (`unit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tambahkan kolom unit_code ke tabel item jika belum ada
ALTER TABLE `item`
ADD COLUMN IF NOT EXISTS `unit_code` VARCHAR(5) DEFAULT NULL;

-- Tambahkan foreign key constraint jika belum ada (opsional, tapi disarankan)
-- Hati-hati jika sudah ada data lama yang tidak cocok
ALTER TABLE `item`
ADD CONSTRAINT IF NOT EXISTS `fk_item_unit`
  FOREIGN KEY (`unit_code`)
  REFERENCES `unit_master` (`unit_code`)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- Contoh data unit_master
INSERT IGNORE INTO `unit_master` (`unit_code`, `unit_name`, `base_unit_category`) VALUES
('GRM', 'gram', 'sembako'),
('ML', 'mililiter', 'minuman'),
('SACH', 'sachet', 'renceng'),
('KG', 'kilogram', 'sembako'),
('L', 'liter', 'minuman'),
('PCS', 'pieces', 'lain-lain'); -- Tambahkan unit umum lainnya
