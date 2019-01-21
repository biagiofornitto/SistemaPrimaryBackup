create database db;
use db;
create table LogEntries(ID varchar(255) NOT NULL,PRIMARY KEY(ID),Timestamp varchar(255),Iface varchar(255),MTU varchar(255), Met varchar(255), RX_OK varchar(255),RX_ERR varchar(255),RX_DRP varchar(255), RX_OVR varchar(255), TX_OK varchar(255), TX_ERR varchar(255), TX_DRP varchar(255), TX_OVR varchar(255),Flg varchar(255));
