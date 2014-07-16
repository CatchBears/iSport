CREATE TABLE `USER_TABLE` (
  `UserId` bigint(20) AUTO_INCREMENT NOT NULL COMMENT '�û�id',
  `UserName` varchar(100) NOT NULL COMMENT '�û���', 
  `PassWord` varchar(200) NOT NULL COMMENT '����', 
  `Location` varchar(100) COMMENT '��ǰλ��', 
  `Score` double DEFAULT '0' COMMENT '�û�����',
  `Completed_id` varchar(200) NOT NULL DEFAULT '0' COMMENT '����ɻ',
  `Uncompleted_id` varchar(200) NOT NULL DEFAULT '0' COMMENT 'δ��ɻ',
  `Sex` varchar(2) NOT NULL DEFAULT '' COMMENT '�Ա�',
  `UserImage` varchar(1024) COMMENT 'ͷ��',
  `Label` varchar(200) NOT NULL DEFAULT '0' COMMENT '״̬',
  PRIMARY KEY (`UserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�û���';

CREATE TABLE `ACTIVITY_TABLE` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '�id',
  `Category` varchar(20) NOT NULL DEFAULT '0' COMMENT '�����',
  `BeginDateTime` date COMMENT '���ʼʱ��',
  `EndDateTime` date COMMENT '�����ʱ��',
  `PeopleCount` bigint(255) COMMENT '��������',
  `PeopleJoinedIds` varchar(1024) COMMENT '�Ѿ�����������˵��û�id',
  `Location` varchar(1024) COMMENT '��ص�',
  `SubmmitPeopleId` bigint(20) NOT NULL DEFAULT '0' COMMENT '�������û�id',
  `Name` varchar(1024) COMMENT '�����',
  `Details` varchar(1024) COMMENT '�����',
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='���';
