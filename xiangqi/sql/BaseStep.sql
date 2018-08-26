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

 Date: 08/20/2014 09:33:16 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `BaseStep`
-- ----------------------------
DROP TABLE IF EXISTS `BaseStep`;
CREATE TABLE `BaseStep` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `info` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `BaseStep`
-- ----------------------------
BEGIN;
INSERT INTO `BaseStep` VALUES ('0', '运行'), ('1', '程序被激活'), ('2', '程序被挂起'), ('3', '点“学习”按钮'), ('4', '学习中弹支付窗口'), ('5', '支付窗口点支付'), ('6', '支付窗口点我已拥有'), ('7', '弹家长输入窗口'), ('8', '家长输入窗口点确定-结果正确'), ('9', '家长输入窗口点确定-结果错误'), ('10', '家长输入窗口点取消'), ('11', '支付失败'), ('12', '支付成功'), ('13', '点“做练习”按钮'), ('14', '做练习弹支付窗口'), ('15', '点“更多”按钮'), ('16', '恢复交易失败'), ('17', '恢复交易成功'), ('18', '购买成功与后台切尔西失败'), ('19', '点击公告'), ('20', '点击广告'), ('21', '点搜狐分享'), ('22', '点腾讯分享'), ('23', '点新浪分享'), ('24', '关闭支付窗口'), ('25', '点分享按钮'), ('26', '点击退出游戏'), ('27', '点击帮助按钮'), ('28', '点击知识点按钮'), ('29', '点击商店按钮'), ('30', '支付窗口点击积分购买按钮'), ('31', '点击登录'), ('32', '点击注册'), ('33', '点击忘记密码'), ('34', '点击提交邮箱找回密码'), ('35', '点击立即注册'), ('36', '注册成功后点击登录'), ('37', '注册成功后点击返回'), ('38', '点击解锁'), ('39', '点击购买-小积分包'), ('40', '点击购买-中积分包'), ('41', '点击购买-大积分包'), ('42', '点击积分墙'), ('43', '退出商城');
COMMIT;

