CREATE DATABASE Company DEFAULT CHARACTER SET utf8;

CREATE TABLE Company.CompanyInfo (
  `Id` int(11) NOT NULL auto_increment,
  `CompanyName` varchar(64) default NULL,
  `CompanyDetailUrl` varchar(256) default NULL,
  `CompanyStar` varchar(3) default NULL,
  `CompanyArea` varchar(64) default NULL,
  `CompanyUrl` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8