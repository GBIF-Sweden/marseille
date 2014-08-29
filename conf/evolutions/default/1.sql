# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table collectio (
  id                        bigint auto_increment not null,
  code                      varchar(255) not null,
  name                      varchar(255) not null,
  bci_id                    varchar(255),
  web_url                   varchar(255),
  description               text,
  number_specimens          bigint,
  number_digitized_specimens bigint,
  number_objects            bigint,
  number_digitized_objects  bigint,
  earliest_year             smallint,
  latest_year               smallint,
  expeditions               text,
  remarkable_persons        text,
  administrative_contact    varchar(255),
  administrative_email      varchar(255) not null,
  creation_date             datetime,
  update_date               datetime,
  organization_id           bigint,
  constraint uq_collectio_code unique (code),
  constraint uq_collectio_name unique (name),
  constraint uq_collectio_bci_id unique (bci_id),
  constraint pk_collectio primary key (id))
;

create table dataset (
  id                        bigint auto_increment not null,
  code                      varchar(255) not null,
  name                      varchar(255) not null,
  web_url                   varchar(255),
  ws_url                    varchar(255),
  description               text,
  gbrds_id                  varchar(255),
  last_offset               bigint,
  last_offset_dt            datetime,
  nb_records                bigint,
  nb_coordinated            bigint,
  nb_specimens              bigint,
  nb_observations           bigint,
  nb_living                 bigint,
  nb_individuals            bigint,
  nb_lat_eq_long            bigint,
  nb_out_world              bigint,
  nb_without_country        bigint,
  nb_without_state          bigint,
  earliest_year             smallint,
  latest_year               smallint,
  last_stat_dt              datetime,
  encoding                  varchar(255),
  nb_original_records       bigint,
  administrative_contact    varchar(255),
  administrative_email      varchar(255) not null,
  creation_date             datetime,
  update_date               datetime,
  organization_id           bigint,
  collection_id             bigint,
  constraint uq_dataset_code unique (code),
  constraint uq_dataset_name unique (name),
  constraint uq_dataset_gbrds_id unique (gbrds_id),
  constraint pk_dataset primary key (id))
;

create table organization (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  address                   varchar(255),
  post_box                  varchar(255),
  post_code                 varchar(255),
  city                      varchar(255),
  country                   varchar(255),
  administrative_contact    varchar(255),
  web_url                   varchar(255),
  administrative_email      varchar(255) not null,
  technical_contact         varchar(255),
  technical_email           varchar(255) not null,
  bci_id                    varchar(255),
  gbrds_id                  varchar(255),
  creation_date             datetime,
  update_date               datetime,
  constraint uq_organization_name unique (name),
  constraint uq_organization_bci_id unique (bci_id),
  constraint uq_organization_gbrds_id unique (gbrds_id),
  constraint pk_organization primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  user_name                 varchar(255) not null,
  name                      varchar(255),
  email                     varchar(255) not null,
  password                  varchar(255) not null,
  rights                    tinyint not null,
  constraint uq_user_user_name unique (user_name),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

alter table collectio add constraint fk_collectio_organization_1 foreign key (organization_id) references organization (id) on delete restrict on update restrict;
create index ix_collectio_organization_1 on collectio (organization_id);
alter table dataset add constraint fk_dataset_organization_2 foreign key (organization_id) references organization (id) on delete restrict on update restrict;
create index ix_dataset_organization_2 on dataset (organization_id);
alter table dataset add constraint fk_dataset_collection_3 foreign key (collection_id) references collectio (id) on delete restrict on update restrict;
create index ix_dataset_collection_3 on dataset (collection_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table collectio;

drop table dataset;

drop table organization;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

