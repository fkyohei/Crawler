CREATE DATABASE Company DEFAULT CHARACTER SET utf8;

/**
 * Crawler_2.scala
 */
CREATE TABLE Company.CompanyInfo (
  `Id` int(11) NOT NULL auto_increment,
  `CompanyName` varchar(64) default NULL,
  `CompanyDetailUrl` varchar(256) default NULL,
  `CompanyStar` varchar(3) default NULL,
  `CompanyArea` varchar(64) default NULL,
  `CompanyUrl` varchar(256) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

/**
 * Crawler_3.scala
 */
CREATE TABLE Company.CompanyDetailInfo (
  `Id` int(11) NOT NULL auto_increment,
  `CompanyName` varchar(64) default NULL,
  `CompanyDetailUrl` varchar(256) default NULL,
  `CompanyStar` varchar(3) default NULL,
  `CompanyArea` varchar(64) default NULL,
  `CompanyUrl` varchar(256) default NULL,
  `PayLevelStar` varchar(3) default NULL,
  `StabilityStar` varchar(3) default NULL,
  `GrowthStar` varchar(3) default NULL,
  `WorthwhileStar` varchar(3) default NULL,
  `IdeaStar` varchar(3) default NULL,
  `BrandStar` varchar(3) default NULL,
  `AtmosphereStar` varchar(3) default NULL,
  `EntranceStar` varchar(3) default NULL,
  `WelfareStar` varchar(3) default NULL,
  `TrainingStar` varchar(3) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

/**
 * Crawler_4.scala
 */
CREATE TABLE Company.CompanyDetailInfo2 (
  `Id` int(11) NOT NULL auto_increment,
  `CompanyName` varchar(64) default NULL,
  `CompanyDetailUrl` varchar(256) default NULL,
  `CompanyArea` varchar(64) default NULL,
  `CompanyUrl` varchar(256) default NULL,
  `Tel` varchar(20) default NULL,
  `Fax` varchar(20) default NULL,
  `Representative` varchar(64) default NULL,
  `Industry` varchar(64) default NULL,
  `Capital` varchar(64) default NULL,
  `Settlement` varchar(64) default NULL,
  `Employee` varchar(64) default NULL,
  `Establish` varchar(64) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE Company.CompanyStarInfo (
  `Id` int(11) NOT NULL,
  `CompanyStar` varchar(3) default NULL,
  `PayLevelStar` varchar(3) default NULL,
  `StabilityStar` varchar(3) default NULL,
  `GrowthStar` varchar(3) default NULL,
  `WorthwhileStar` varchar(3) default NULL,
  `IdeaStar` varchar(3) default NULL,
  `BrandStar` varchar(3) default NULL,
  `AtmosphereStar` varchar(3) default NULL,
  `EntranceStar` varchar(3) default NULL,
  `WelfareStar` varchar(3) default NULL,
  `TrainingStar` varchar(3) default NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8