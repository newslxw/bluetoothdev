
drop table if exists t_bed;
drop table if exists t_bed_temperature;
drop table if exists t_group_beds;
drop table if exists t_groups;
drop table if exists t_sys_param;
drop table if exists t_upload_history;
drop table if exists t_users;

create table t_bed
(
   _id                  integer not null primary key,
   bed_no               varchar(20)
);

create table t_bed_temperature
(
   _id                  integer not null primary key,
   bed_id               integer,
   temperature          varchar(50),
   indate               varchar(14),
   data_type			varchar(10),
   upload_id            integer
);

create table t_group_beds
(
   _id                  integer not null primary key,
   bed_id               integer,
   groups_id            integer
);

create table t_groups
(
   _id                  integer not null primary key,
   group_name           varchar(40),
   removable            integer
);

create table t_sys_param
(
   _id                  integer not null primary key,
   param_code           varchar(40),
   param_desc           varchar(100),
   param_value          varchar(100),
   configable           integer ,
   param_type           varchar(10) 
);

create table t_upload_history
(
   _id                  integer not null primary key,
   upload_name          varchar(40),
   unique_tag          	varchar(40),
   upload_time          varchar(14),
   user_name            varchar(20),
   group_name           varchar(40),
   upload_way           varchar(10),
   bluetooth_name       varchar(20)
);

create table t_users
(
   _id                  integer not null primary key,
   user_name            varchar(20),
   group_name           varchar(40),
   login_time           varchar(14)
);



------------------------------------------------------------------------------
