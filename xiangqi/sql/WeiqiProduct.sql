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

 Date: 09/16/2014 18:16:08 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `WeiqiProduct`
-- ----------------------------
DROP TABLE IF EXISTS `WeiqiProduct`;
CREATE TABLE `WeiqiProduct` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `productIdentifier` varchar(255) DEFAULT NULL,
  `uniqueUsed` int(11) DEFAULT NULL,
  `used` int(11) DEFAULT NULL,
  `uniqueBuy` int(11) DEFAULT NULL,
  `buy` int(11) DEFAULT NULL,
  `testUniBuy` int(11) DEFAULT NULL,
  `testBuy` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT '0',
  `lesson` int(11) DEFAULT '1',
  `price` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `WeiqiProduct`
-- ----------------------------
BEGIN;
INSERT INTO `WeiqiProduct` VALUES ('1', '第一课', 'com.miracle.lesson1.paylock', '1', '8', '0', '0', '0', '0', '0', '1', '2'), ('2', '第二课', 'com.miracle.lesson2.paylock', '0', '0', '0', '0', '0', '0', '0', '2', '2'), ('3', '第三课', 'com.miracle.lesson3.paylock', '0', '0', '0', '0', '0', '0', '0', '3', '2'), ('4', '第四课', 'com.miracle.lesson4.paylock', '0', '0', '0', '0', '0', '0', '0', '4', '2'), ('5', '第五课', 'com.miracle.lesson5.paylock', '0', '0', '0', '0', '0', '0', '0', '5', '2'), ('6', '第六课', 'com.miracle.lesson6.paylock', '0', '0', '0', '0', '0', '0', '0', '6', '2'), ('7', '第七课', 'com.miracle.lesson7.paylock', '0', '0', '0', '0', '0', '0', '0', '7', '2'), ('8', '第八课', 'com.miracle.lesson8.paylock', '0', '0', '0', '0', '0', '0', '0', '8', '2'), ('9', '第九课', 'com.miracle.lesson9.paylock', '0', '0', '0', '0', '0', '0', '0', '9', '2'), ('10', '第十课', 'com.miracle.lesson10.paylock', '0', '0', '0', '0', '0', '0', '0', '10', '2'), ('11', '第十一课', 'com.miracle.lesson11.paylock', '0', '0', '0', '0', '0', '0', '0', '11', '2'), ('12', '第十二课', 'com.miracle.lesson12.paylock', '0', '0', '0', '0', '0', '0', '0', '12', '2'), ('13', '第十三课', 'com.miracle.lesson13.paylock', '0', '0', '0', '0', '0', '0', '0', '13', '2'), ('14', '第十四课', 'com.miracle.lesson14.lockApp', '0', '0', '0', '0', '0', '0', '0', '14', '2'), ('15', '第十五课', 'com.miracle.lesson15.lockApp', '0', '0', '0', '0', '0', '0', '0', '15', '2'), ('16', '第十六课', 'com.miracle.lesson16.paylock', '0', '0', '0', '0', '0', '0', '0', '16', '2'), ('17', '第十七课', 'com.miracle.lesson17.paylock', '0', '0', '0', '0', '0', '0', '0', '17', '2'), ('18', '第十八课', 'com.miracle.lesson18.paylock', '0', '0', '0', '0', '0', '0', '0', '18', '2'), ('19', '第二课小积分包', 'com.miracle.lesson2.buyScoreA', '0', '0', '0', '0', '0', '0', '600', '0', '2'), ('20', '第二课中积分包', 'com.miracle.lesson2.buyScoreB', '0', '0', '0', '0', '0', '0', '9600', '0', '2'), ('21', '第二课大积分包', 'com.miracle.lesson2.buyScoreC', '0', '0', '0', '0', '0', '0', '19800', '0', '2'), ('22', '第14课小积分包', 'com.miracle.lesson14.BuyScoreA', '0', '0', '0', '0', '0', '0', '600', '0', '2'), ('23', '第14课中积分包', 'com.miracle.lesson14.BuyScoreB', '0', '0', '0', '0', '0', '0', '9600', '0', '2'), ('24', '第14课大积分包', 'com.miracle.lesson14.BuyScoreC', '0', '0', '0', '0', '0', '0', '19800', '0', '2'), ('25', '第15课小积分包', 'com.miracle.lesson15.BuyScoreA', '0', '0', '0', '0', '0', '0', '600', '0', '2'), ('26', '第15课中积分包', 'com.miracle.lesson15.BuyScoreB', '0', '0', '0', '0', '0', '0', '9600', '0', '2'), ('27', '第15课大积分包', 'com.miracle.lesson15.BuyScoreC', '0', '0', '0', '0', '0', '0', '19800', '0', '2');
COMMIT;

