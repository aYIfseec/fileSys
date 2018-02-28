/*
Navicat MySQL Data Transfer

Source Server         : k
Source Server Version : 50627
Source Host           : 120.27.129.160:3306
Source Database       : fileSys

Target Server Type    : MYSQL
Target Server Version : 50627
File Encoding         : 65001

Date: 2018-02-28 20:13:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_area_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_area_dict`;
CREATE TABLE `t_area_dict` (
  `area_code` varchar(40) NOT NULL COMMENT '采取4位编码，每两位为一个区域。1-2位表示县级，3-4位乡镇，因为没有市级编码，特殊的0000代表整个宜春市。',
  `area_name` varchar(32) NOT NULL,
  `parent_code` varchar(40) NOT NULL COMMENT '上级区域代码',
  PRIMARY KEY (`area_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_common_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_common_dict`;
CREATE TABLE `t_common_dict` (
  `code` varchar(32) NOT NULL,
  `detail` varchar(64) DEFAULT NULL,
  `descript` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_download_record
-- ----------------------------
DROP TABLE IF EXISTS `t_download_record`;
CREATE TABLE `t_download_record` (
  `donw_file_id` varchar(36) NOT NULL,
  `down_user_id` varchar(36) NOT NULL,
  `down_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
  `file_id` varchar(40) NOT NULL,
  `file_owner` varchar(40) NOT NULL,
  `file_name` varchar(128) NOT NULL,
  `upload_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `path` varchar(128) NOT NULL,
  `file_class` smallint(6) NOT NULL COMMENT '文件归档于（党建工作文件资源库1、党建工作相关材料资源库0）',
  `file_type` varchar(20) NOT NULL COMMENT '文件级别or文件类型',
  `state` smallint(6) DEFAULT '0' COMMENT '文件状态（审核中0，）',
  PRIMARY KEY (`file_id`),
  KEY `file_owner` (`file_owner`),
  KEY `file_type` (`file_type`),
  CONSTRAINT `t_file_ibfk_1` FOREIGN KEY (`file_owner`) REFERENCES `t_user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `t_file_ibfk_2` FOREIGN KEY (`file_type`) REFERENCES `t_common_dict` (`code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `user_id` varchar(40) NOT NULL,
  `phone_num` varchar(32) NOT NULL,
  `login_name` varchar(32) NOT NULL,
  `login_pwd` varchar(32) NOT NULL,
  `area` varchar(40) NOT NULL,
  `real_name` varchar(32) DEFAULT NULL,
  `work_place` varchar(32) DEFAULT NULL,
  `check_privilege` smallint(6) DEFAULT '0' COMMENT '是否有审核权限（1有，0无）',
  PRIMARY KEY (`user_id`),
  KEY `area` (`area`),
  CONSTRAINT `t_user_ibfk_1` FOREIGN KEY (`area`) REFERENCES `t_area_dict` (`area_code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
