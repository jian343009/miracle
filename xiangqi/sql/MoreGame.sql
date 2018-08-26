/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50614
 Source Host           : localhost
 Source Database       : miracle_weiqi

 Target Server Type    : MySQL
 Target Server Version : 50614
 File Encoding         : utf-8

 Date: 10/24/2014 15:32:33 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `MoreGame`
-- ----------------------------
DROP TABLE IF EXISTS `MoreGame`;
CREATE TABLE `MoreGame` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `channel` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `MoreGame`
-- ----------------------------
BEGIN;
INSERT INTO `MoreGame` VALUES ('1', 'a39257ab908b4f2c8eebf8bc36702838', 'http://store.nearme.com.cn/product/0000/568/123_1.html'), ('2', '3bd9a84ffb994ce4a0618e604e9fff4f', 'http://store.nearme.com.cn/search/do.html?keyword=%E5%B0%91%E5%84%BF%E5%9B%B4%E6%A3%8B&nav=index'), ('3', 'f04e820a366c4df5abed46523b43d686', 'http://www.qq.com'), ('4', '1e043f4b90a04b55ba0552c9e3853bc0', 'http://store.nearme.com.cn/search/do.html?keyword=%E5%B0%91%E5%84%BF%E5%9B%B4%E6%A3%8B&nav=index'), ('5', '8b75424f5e0141b29032ede5c49df60a', 'http://store.nearme.com.cn/search/do.html?keyword=%E5%B0%91%E5%84%BF%E5%9B%B4%E6%A3%8B&nav=index');
COMMIT;

