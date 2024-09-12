-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: reggie
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address_book`
--
-- 设置存储过程，检查数据库是否存在并退出，下面都是gpt生成的，检查部分
-- DELIMITER //
--
-- CREATE PROCEDURE CheckAndExit()
-- BEGIN
--   -- 检查数据库是否存在
--   IF EXISTS (SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'reggie') THEN
--     -- 数据库存在，输出消息并退出
--     SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Database reggie already exists. Exiting script.';
-- END IF;
-- END //
--
-- DELIMITER ;
--
-- -- 调用存储过程
-- CALL CheckAndExit();

-- 如果没有退出，则继续执行下面的初始化操作

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `easypan`;

-- 切换到 'reggie' 数据库
USE `easypan`;

create table IF NOT EXISTS email_code
(
    email       varchar(150) not null comment '邮箱',
    code        varchar(5)   not null comment '编号',
    create_time datetime     null comment '创建时间',
    status      tinyint(1)   null comment '0:未使用  1:已使用',
    primary key (email, code)
)
    comment '邮箱验证码';

create table file_info
(
    file_id          varchar(50)          not null comment '文件ID',
    user_id          varchar(19)          not null comment '用户ID',
    file_md5         varchar(32)          null comment 'md5值，第一次上传记录',
    file_pid         varchar(50)          null comment '父级ID',
    file_size        bigint               null comment '文件大小',
    file_name        varchar(200)         null comment '文件名称',
    file_cover       varchar(100)         null comment '封面',
    file_path        varchar(100)         null comment '文件路径',
    create_time      datetime             null comment '创建时间',
    last_update_time datetime             null comment '最后更新时间',
    folder_type      tinyint(1)           null comment '0:文件 1:目录',
    file_category    tinyint(1)           null comment '1:视频 2:音频  3:图片 4:文档 5:其他',
    file_type        tinyint(1)           null comment ' 1:视频 2:音频  3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他',
    status           tinyint(1)           null comment '0:转码中 1转码失败 2:转码成功',
    del_flag         tinyint(1) default 2 null comment '删除标记 0:删除  1:回收站  2:正常',
    update_time      datetime             null,
    recovery_time    datetime             null,
    feng_jing        int                  null comment '2正常，1封禁',
    save_sf          tinyint(1)           null comment '是否是保存过来的',
    primary key (file_id, user_id)
)
    comment '文件信息';

create index idx_create_time
    on file_info (create_time);

create index idx_del_flag
    on file_info (del_flag);

create index idx_file_pid
    on file_info (file_pid);

create index idx_md5
    on file_info (file_md5);

create index idx_user_id
    on file_info (user_id);

create table file_share
(
    share_id       varchar(20)                          not null
        primary key,
    file_id        varchar(10)                          not null,
    user_id        varchar(255)                         not null,
    valid_type     int                                  not null comment '0:1天 1:7天 2:30天 3:永久有效',
    code           varchar(5)                           not null,
    browse_count   int        default 0                 null,
    save_count     int        default 0                 null,
    download_count int        default 0                 null,
    deleted        tinyint(1) default 0                 null,
    version        int        default 1                 null,
    expire_time    datetime                             null,
    create_time    timestamp  default CURRENT_TIMESTAMP null,
    update_time    timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create index idx_file_id
    on file_share (file_id);

create index idx_user_id
    on file_share (user_id);

create table save_info
(
    save_id      varchar(15) not null
        primary key,
    xian_user_id varchar(19) null comment '原本的用户id',
    yuan_user_id varchar(19) null comment '原用户id',
    xian_file_id varchar(20) null,
    yuan_file_id varchar(20) null,
    share_id     varchar(19) null
);

create table sys_settings
(
    列_name int null
);

create table user_info
(
    id              varchar(19)      not null comment '用户ID'
        primary key,
    nick_name       varchar(20)      null comment '昵称',
    email           varchar(150)     not null comment '邮箱',
    qq_open_id      varchar(35)      null comment 'qqOpenID',
    qq_avatar       varchar(150)     null comment 'qq头像',
    password        varchar(50)      null comment '密码',
    join_time       datetime         null comment '加入时间',
    last_login_time datetime         null comment '最后登录时间',
    status          tinyint          null comment '0:禁用 1:正常',
    use_space       bigint default 0 null comment '使用空间单位byte',
    total_space     bigint           null comment '总空间',
    create_time     datetime         null,
    update_time     datetime         null,
    constraint key_email
        unique (email),
    constraint key_nick_name
        unique (nick_name),
    constraint key_qq_open_id
        unique (qq_open_id)
)
    comment '用户信息';
