/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     28/01/2011 0.59.47                           */
/*==============================================================*/


alter table AUDIOGROUP
   drop primary key;

drop table if exists AUDIOGROUP;

drop index DBIBLIOTECA01 on BIBLIOTECHE;

alter table BIBLIOTECHE
   drop primary key;

drop table if exists BIBLIOTECHE;

alter table CATALOGHICOLLEGATI
   drop primary key;

drop table if exists CATALOGHICOLLEGATI;

alter table IMGGROUP
   drop primary key;

drop table if exists IMGGROUP;

alter table OCRGROUP
   drop primary key;

drop table if exists OCRGROUP;

alter table TBLAUDIO
   drop primary key;

drop table if exists TBLAUDIO;

alter table TBLCONT
   drop primary key;

drop table if exists TBLCONT;

drop index DTBLIMG04 on TBLIMG;

alter table TBLIMG
   drop primary key;

drop table if exists TBLIMG;

drop index UTBLLEGNOT01 on TBLLEGNOT;

alter table TBLLEGNOT
   drop primary key;

drop table if exists TBLLEGNOT;

drop index DTBLLEGNOTRIS01 on TBLLEGNOTRIS;

alter table TBLLEGNOTRIS
   drop primary key;

drop table if exists TBLLEGNOTRIS;

alter table TBLOAISET
   drop primary key;

drop table if exists TBLOAISET;

alter table TBLOAITOKEN
   drop primary key;

drop table if exists TBLOAITOKEN;

alter table TBLOCR
   drop primary key;

drop table if exists TBLOCR;

alter table TBLRELRIS
   drop primary key;

drop table if exists TBLRELRIS;

alter table TBLRIS
   drop primary key;

drop table if exists TBLRIS;

alter table TBLRISAUDIO
   drop primary key;

drop table if exists TBLRISAUDIO;

alter table TBLRISIMG
   drop primary key;

drop table if exists TBLRISIMG;

alter table TBLRISVIDEO
   drop primary key;

drop table if exists TBLRISVIDEO;

alter table TBLSTORIA
   drop primary key;

drop table if exists TBLSTORIA;

alter table TBLVIDEO
   drop primary key;

drop table if exists TBLVIDEO;

alter table TLKPFRUIB
   drop primary key;

drop table if exists TLKPFRUIB;

alter table TLKPHOST
   drop primary key;

drop table if exists TLKPHOST;

alter table TLKPMIME
   drop primary key;

drop table if exists TLKPMIME;

alter table TLKPTIPOREL
   drop primary key;

drop table if exists TLKPTIPOREL;

alter table TLKPTYPEDIGIT
   drop primary key;

drop table if exists TLKPTYPEDIGIT;

drop table if exists TMPFILEXML;

alter table VIDEOGROUP
   drop primary key;

drop table if exists VIDEOGROUP;

/*==============================================================*/
/* Table: AUDIOGROUP                                            */
/*==============================================================*/
create table AUDIOGROUP
(
   ID_AUDIOGROUP        int(11) not null,
   SAMPLINGFREQUENCY    varchar(50),
   BITPERSAMPLE         varchar(50),
   BITRATE              varchar(50),
   FORMAT_NAME          varchar(50),
   FORMAT_MIME          varchar(50),
   FORMAT_COMPRESSION   varchar(50),
   FORMAT_CHANNEL       varchar(50),
   SOURCE_TYPE          varchar(50)
);

alter table AUDIOGROUP comment 'InnoDB free: 12288 kB';

alter table AUDIOGROUP
   add primary key (ID_AUDIOGROUP);

/*==============================================================*/
/* Table: BIBLIOTECHE                                           */
/*==============================================================*/
create table BIBLIOTECHE
(
   SIGLA                varchar(255) not null,
   DESCRIZIONE          varchar(255) not null
);

alter table BIBLIOTECHE comment 'InnoDB free: 12288 kB';

alter table BIBLIOTECHE
   add primary key (SIGLA);

/*==============================================================*/
/* Index: DBIBLIOTECA01                                         */
/*==============================================================*/
create index DBIBLIOTECA01 on BIBLIOTECHE
(
   DESCRIZIONE
);

/*==============================================================*/
/* Table: CATALOGHICOLLEGATI                                    */
/*==============================================================*/
create table CATALOGHICOLLEGATI
(
   LEGNOTSEGNAORI       varchar(80) not null,
   LEGNOTBIDDES         varchar(30) not null,
   RELRISSEQUENZA       int(11) not null,
   LINKCOLLEGATI        varchar(200) not null
);

alter table CATALOGHICOLLEGATI comment 'InnoDB free: 12288 kB';

alter table CATALOGHICOLLEGATI
   add primary key (LEGNOTSEGNAORI, LEGNOTBIDDES, RELRISSEQUENZA, LINKCOLLEGATI);

/*==============================================================*/
/* Table: IMGGROUP                                              */
/*==============================================================*/
create table IMGGROUP
(
   ID_IMGGROUP          int(11) not null,
   SCANNER_MODEL        varchar(255) not null,
   FORMAT_NAME          varchar(255) not null,
   CAPTURE_SOFTWARE     varchar(255) not null,
   PPI                  int(11) not null,
   BITSAMPLE            varchar(50) not null
);

alter table IMGGROUP comment 'InnoDB free: 12288 kB';

alter table IMGGROUP
   add primary key (ID_IMGGROUP);

alter table IMGGROUP
   add unique UIMGGROUP (FORMAT_NAME, CAPTURE_SOFTWARE, PPI, BITSAMPLE, SCANNER_MODEL);

/*==============================================================*/
/* Table: OCRGROUP                                              */
/*==============================================================*/
create table OCRGROUP
(
   ID_OCRGROUP          int(11) not null,
   FORMAT_NAME          varchar(255) not null,
   SOFTWARE_OCR         varchar(255)
);

alter table OCRGROUP comment 'InnoDB free: 12288 kB';

alter table OCRGROUP
   add primary key (ID_OCRGROUP);

alter table OCRGROUP
   add unique UOCRGROUP01 (FORMAT_NAME, SOFTWARE_OCR);

/*==============================================================*/
/* Table: TBLAUDIO                                              */
/*==============================================================*/
create table TBLAUDIO
(
   ID_TBLAUDIO          varchar(15) not null,
   HOSTID               int(11) not null,
   MIMEID               int(11) not null,
   FRUIBID              int(11) not null,
   ID_AUDIOGROUP        int(11) not null,
   AUDIOPATHNAME        varchar(255) not null,
   AUDIOMD5             varchar(32) not null,
   AUDIODATE            varchar(16) not null,
   AUDIOSIZE            int(11) not null,
   AUDIOUSAGE           varchar(1) not null,
   AUDIODURATION        time not null
);

alter table TBLAUDIO comment 'InnoDB free: 12288 kB; (`HOSTID`) REFER `TECACSI/TLKPHOST`(`';

alter table TBLAUDIO
   add primary key (ID_TBLAUDIO);

/*==============================================================*/
/* Table: TBLCONT                                               */
/*==============================================================*/
create table TBLCONT
(
   CONTCODENTE          varchar(20) not null,
   CONTPROG             int(11)
);

alter table TBLCONT comment 'InnoDB free: 12288 kB';

alter table TBLCONT
   add primary key (CONTCODENTE);

/*==============================================================*/
/* Table: TBLIMG                                                */
/*==============================================================*/
create table TBLIMG
(
   ID_TBLIMG            varchar(15) not null,
   HOSTID               int(11),
   MIMEID               int(11),
   FRUIBID              int(11),
   ID_IMGGROUP          int(11) not null,
   IMGPATHNAME          varchar(255) not null,
   IMGMD5               varchar(32) not null,
   IMGFORMATO           varchar(20),
   IMGDATA              varchar(16),
   IMGSIZE              int(11) not null,
   IMGUSAGE             varchar(1) not null,
   IMGLENGTH            int(11) not null,
   IMGWIDTH             int(11) not null
);

alter table TBLIMG comment 'InnoDB free: 12288 kB; (`HOSTID`) REFER `TECACSI/TLKPHOST`(`';

alter table TBLIMG
   add primary key (ID_TBLIMG);

/*==============================================================*/
/* Index: DTBLIMG04                                             */
/*==============================================================*/
create index DTBLIMG04 on TBLIMG
(
   IMGMD5
);

/*==============================================================*/
/* Table: TBLLEGNOT                                             */
/*==============================================================*/
create table TBLLEGNOT
(
   ID_TBLLEGNOT         int(11) not null,
   LEGNOTBID            varchar(30) not null,
   LEGNOTSEGNA          varchar(80),
   LEGNOTINV            varchar(80),
   TYPEDIGITID          int(11),
   TMPAUTORE            varchar(80),
   TMPTITOLO            varchar(80),
   TMPNOTE_PUBBLICAZIONE varchar(80),
   TMPCHIAVE_AUTORE     varchar(80),
   TMPCHIAVE_TITOLO     varchar(80),
   LEGNOTBNI            varchar(14)
);

alter table TBLLEGNOT comment 'InnoDB free: 12288 kB; (`TYPEDIGITID`) REFER `TECACSI/TLKPTY';

alter table TBLLEGNOT
   add primary key (ID_TBLLEGNOT);

/*==============================================================*/
/* Index: UTBLLEGNOT01                                          */
/*==============================================================*/
create index UTBLLEGNOT01 on TBLLEGNOT
(
   
);

/*==============================================================*/
/* Table: TBLLEGNOTRIS                                          */
/*==============================================================*/
create table TBLLEGNOTRIS
(
   ID_TBLLEGNOT         int(11) not null,
   RISIDR               varchar(15) not null,
   HOSTID               int(11) not null,
   ID_TBLOAISET         int(11) not null,
   NOTLEVEL             varchar(1) not null,
   PIECEGR              varchar(100),
   PIECEDT              varchar(100),
   PIECEIN              varchar(100),
   FILEMAG              varchar(200),
   NOTMD5               varchar(32),
   NOTDATAMOD           timestamp not null default CURRENT_TIMESTAMP
);

alter table TBLLEGNOTRIS comment 'InnoDB free: 12288 kB; (`ID_TBLLEGNOT`) REFER `TECACSI/TBLLE';

alter table TBLLEGNOTRIS
   add primary key (ID_TBLLEGNOT, RISIDR);

/*==============================================================*/
/* Index: DTBLLEGNOTRIS01                                       */
/*==============================================================*/
create index DTBLLEGNOTRIS01 on TBLLEGNOTRIS
(
   
);

/*==============================================================*/
/* Table: TBLOAISET                                             */
/*==============================================================*/
create table TBLOAISET
(
   ID_TBLOAISET         int(11) not null,
   CODE_TBLOAISET       varchar(255) not null,
   DESCR_TBLOAISET      varchar(300) not null,
   URL_TBLOAISET        varchar(255) not null,
   ACCESSO              int(11) not null default 0,
   AVAILABLE            varchar(255) not null
);

alter table TBLOAISET comment 'InnoDB free: 12288 kB';

alter table TBLOAISET
   add primary key (ID_TBLOAISET);

/*==============================================================*/
/* Table: TBLOAITOKEN                                           */
/*==============================================================*/
create table TBLOAITOKEN
(
   ID_TBLOAITOKEN       varchar(32) not null,
   DATA_DA              varchar(10),
   DATA_A               varchar(10),
   FONDO                varchar(255),
   METADATAPREFIX       varchar(255),
   POSIZIONE            int(11) not null default 1,
   DATAMOD              timestamp not null default CURRENT_TIMESTAMP
);

alter table TBLOAITOKEN comment 'InnoDB free: 12288 kB';

alter table TBLOAITOKEN
   add primary key (ID_TBLOAITOKEN);

/*==============================================================*/
/* Table: TBLOCR                                                */
/*==============================================================*/
create table TBLOCR
(
   ID_TBLOCR            varchar(15) not null,
   ID_TBLIMG            varchar(15) not null,
   ID_OCRGROUP          int(11) not null,
   SEQUENCENUMBER       int(11) not null,
   NOMENCLATURE         varchar(255) not null,
   OCRUSAGE             varchar(1),
   OCRPATHNAME          varchar(255) not null,
   OCRMD5               varchar(32) not null,
   OCRSIZE              int(11),
   OCRDATA              varchar(20),
   HOSTID               int(11) not null
);

alter table TBLOCR comment 'InnoDB free: 12288 kB; (`ID_TBLIMG`) REFER `TECACSI/TBLIMG`(';

alter table TBLOCR
   add primary key (ID_TBLOCR);

/*==============================================================*/
/* Table: TBLRELRIS                                             */
/*==============================================================*/
create table TBLRELRIS
(
   RELRISIDRPARTENZA    char(15) not null,
   RELRISIDRARRIVO      char(15) not null,
   TIPORELID            int(11),
   RELRISSEQUENZA       int(11),
   RELRISNOTE           varchar(255)
);

alter table TBLRELRIS comment 'InnoDB free: 12288 kB; (`RELRISIDRPARTENZA`) REFER `TECACSI/';

alter table TBLRELRIS
   add primary key (RELRISIDRPARTENZA, RELRISIDRARRIVO);

alter table TBLRELRIS
   add unique UTBLRELRIS1 (RELRISIDRPARTENZA, RELRISSEQUENZA, RELRISIDRARRIVO);

/*==============================================================*/
/* Table: TBLRIS                                                */
/*==============================================================*/
create table TBLRIS
(
   RISIDR               char(15) not null,
   RISNOTAPUB           varchar(255),
   RISLIVELLO           char(1)
);

alter table TBLRIS comment 'InnoDB free: 12288 kB';

alter table TBLRIS
   add primary key (RISIDR);

/*==============================================================*/
/* Table: TBLRISAUDIO                                           */
/*==============================================================*/
create table TBLRISAUDIO
(
   RISIDR               varchar(15) not null,
   ID_TBLAUDIO          varchar(15) not null
);

alter table TBLRISAUDIO comment 'InnoDB free: 12288 kB; (`RISIDR`) REFER `TECACSI/TBLRIS`(`RI';

alter table TBLRISAUDIO
   add primary key (RISIDR, ID_TBLAUDIO);

/*==============================================================*/
/* Table: TBLRISIMG                                             */
/*==============================================================*/
create table TBLRISIMG
(
   RISIDR               varchar(15) not null,
   ID_TBLIMG            varchar(15) not null
);

alter table TBLRISIMG comment 'InnoDB free: 12288 kB; (`RISIDR`) REFER `TECACSI/TBLRIS`(`RI';

alter table TBLRISIMG
   add primary key (RISIDR, ID_TBLIMG);

/*==============================================================*/
/* Table: TBLRISVIDEO                                           */
/*==============================================================*/
create table TBLRISVIDEO
(
   RISIDR               varchar(15) not null,
   ID_TBLVIDEO          varchar(15) not null
);

alter table TBLRISVIDEO comment 'InnoDB free: 12288 kB; (`RISIDR`) REFER `TECACSI/TBLRIS`(`RI';

alter table TBLRISVIDEO
   add primary key (RISIDR, ID_TBLVIDEO);

/*==============================================================*/
/* Table: TBLSTORIA                                             */
/*==============================================================*/
create table TBLSTORIA
(
   STORIAID             int(11) not null,
   RISIDR               char(15),
   STORIADATA           char(16),
   STORIADES            varchar(255)
);

alter table TBLSTORIA comment 'InnoDB free: 12288 kB; (`RISIDR`) REFER `TECACSI/TBLRIS`(`RI';

alter table TBLSTORIA
   add primary key (STORIAID);

/*==============================================================*/
/* Table: TBLVIDEO                                              */
/*==============================================================*/
create table TBLVIDEO
(
   ID_TBLVIDEO          varchar(15) not null,
   HOSTID               int(11) not null,
   MIMEID               int(11) not null,
   FRUIBID              int(11) not null,
   ID_VIDEOGROUP        int(11) not null,
   VIDEOPATHNAME        varchar(255) not null,
   VIDEOMD5             varchar(32) not null,
   VIDEODATE            varchar(16) not null,
   VIDEOSIZE            int(11) not null,
   VIDEOUSAGE           varchar(1) not null,
   VIDEODURATION        time not null
);

alter table TBLVIDEO comment 'InnoDB free: 12288 kB; (`HOSTID`) REFER `TECACSI/TLKPHOST`(`';

alter table TBLVIDEO
   add primary key (ID_TBLVIDEO);

/*==============================================================*/
/* Table: TLKPFRUIB                                             */
/*==============================================================*/
create table TLKPFRUIB
(
   FRUIBID              int(11) not null,
   FRUIBDES             varchar(255)
);

alter table TLKPFRUIB comment 'InnoDB free: 12288 kB';

alter table TLKPFRUIB
   add primary key (FRUIBID);

/*==============================================================*/
/* Table: TLKPHOST                                              */
/*==============================================================*/
create table TLKPHOST
(
   HOSTID               int(11) not null,
   HOSTPROT             varchar(10),
   HOSTIP               varchar(80),
   HOSTPORTA            varchar(5),
   HOSTSERVERPATH       varchar(255),
   HOSTPATHDISCO        varchar(255) not null,
   HOSTLOGIN            varchar(30),
   HOSTPSW              varchar(30)
);

alter table TLKPHOST comment 'InnoDB free: 12288 kB';

alter table TLKPHOST
   add primary key (HOSTID);

/*==============================================================*/
/* Table: TLKPMIME                                              */
/*==============================================================*/
create table TLKPMIME
(
   MIMEID               int(11) not null,
   MIMEDES              varchar(255)
);

alter table TLKPMIME comment 'InnoDB free: 12288 kB';

alter table TLKPMIME
   add primary key (MIMEID);

/*==============================================================*/
/* Table: TLKPTIPOREL                                           */
/*==============================================================*/
create table TLKPTIPOREL
(
   TIPORELID            int(11) not null,
   TIPORELDESAP         varchar(255),
   TIPORELDESPA         varchar(255),
   TIPORELNAT           char(1)
);

alter table TLKPTIPOREL comment 'InnoDB free: 12288 kB';

alter table TLKPTIPOREL
   add primary key (TIPORELID);

/*==============================================================*/
/* Table: TLKPTYPEDIGIT                                         */
/*==============================================================*/
create table TLKPTYPEDIGIT
(
   TYPEDIGITID          int(11) not null,
   TYPEDIGITDES         varchar(255)
);

alter table TLKPTYPEDIGIT comment 'InnoDB free: 12288 kB';

alter table TLKPTYPEDIGIT
   add primary key (TYPEDIGITID);

/*==============================================================*/
/* Table: TMPFILEXML                                            */
/*==============================================================*/
create table TMPFILEXML
(
   RISIDR               char(15),
   RIGA                 int(11),
   SINGLETREE           varchar(1),
   FILEXML              varchar(60000)
);

alter table TMPFILEXML comment 'TMPFILEXML';

/*==============================================================*/
/* Table: VIDEOGROUP                                            */
/*==============================================================*/
create table VIDEOGROUP
(
   ID_VIDEOGROUP        int(11) not null,
   FORMAT_NAME          varchar(50) not null,
   FORMAT_MIME          varchar(50) not null,
   FORMAT_VIDEO         varchar(50) not null,
   FORMAT_ENCODETYPE    varchar(50) not null,
   FORMAT_STREAMTYPE    varchar(50) not null,
   FORMAT_CODEC         varchar(50) not null
);

alter table VIDEOGROUP comment 'InnoDB free: 12288 kB';

alter table VIDEOGROUP
   add primary key (ID_VIDEOGROUP);

alter table TBLAUDIO add constraint TBLAUDIO_ibfk_1 foreign key (HOSTID)
      references TLKPHOST (HOSTID);

alter table TBLAUDIO add constraint TBLAUDIO_ibfk_2 foreign key (MIMEID)
      references TLKPMIME (MIMEID);

alter table TBLAUDIO add constraint TBLAUDIO_ibfk_3 foreign key (FRUIBID)
      references TLKPFRUIB (FRUIBID);

alter table TBLAUDIO add constraint TBLAUDIO_ibfk_4 foreign key (ID_AUDIOGROUP)
      references AUDIOGROUP (ID_AUDIOGROUP);

alter table TBLIMG add constraint TBLIMG_ibfk_1 foreign key (HOSTID)
      references TLKPHOST (HOSTID);

alter table TBLIMG add constraint TBLIMG_ibfk_2 foreign key (MIMEID)
      references TLKPMIME (MIMEID);

alter table TBLIMG add constraint TBLIMG_ibfk_3 foreign key (FRUIBID)
      references TLKPFRUIB (FRUIBID);

alter table TBLIMG add constraint TBLIMG_ibfk_4 foreign key (ID_IMGGROUP)
      references IMGGROUP (ID_IMGGROUP);

alter table TBLLEGNOT add constraint TBLLEGNOT_ibfk_1 foreign key (TYPEDIGITID)
      references TLKPTYPEDIGIT (TYPEDIGITID);

alter table TBLLEGNOTRIS add constraint TBLLEGNOTRIS_ibfk_5 foreign key (ID_TBLLEGNOT)
      references TBLLEGNOT (ID_TBLLEGNOT);

alter table TBLLEGNOTRIS add constraint TBLLEGNOTRIS_ibfk_6 foreign key (RISIDR)
      references TBLRIS (RISIDR);

alter table TBLLEGNOTRIS add constraint TBLLEGNOTRIS_ibfk_7 foreign key (HOSTID)
      references TLKPHOST (HOSTID);

alter table TBLLEGNOTRIS add constraint TBLLEGNOTRIS_ibfk_8 foreign key (ID_TBLOAISET)
      references TBLOAISET (ID_TBLOAISET);

alter table TBLOCR add constraint TBLOCR_ibfk_1 foreign key (ID_TBLIMG)
      references TBLIMG (ID_TBLIMG);

alter table TBLOCR add constraint TBLOCR_ibfk_2 foreign key (ID_OCRGROUP)
      references OCRGROUP (ID_OCRGROUP);

alter table TBLOCR add constraint TBLOCR_ibfk_3 foreign key (HOSTID)
      references TLKPHOST (HOSTID);

alter table TBLRELRIS add constraint TBLRELRIS_ibfk_1 foreign key (RELRISIDRPARTENZA)
      references TBLRIS (RISIDR);

alter table TBLRELRIS add constraint TBLRELRIS_ibfk_2 foreign key (RELRISIDRARRIVO)
      references TBLRIS (RISIDR);

alter table TBLRELRIS add constraint TBLRELRIS_ibfk_3 foreign key (TIPORELID)
      references TLKPTIPOREL (TIPORELID);

alter table TBLRISAUDIO add constraint TBLRISAUDIO_ibfk_3 foreign key (RISIDR)
      references TBLRIS (RISIDR);

alter table TBLRISAUDIO add constraint TBLRISAUDIO_ibfk_4 foreign key (ID_TBLAUDIO)
      references TBLAUDIO (ID_TBLAUDIO);

alter table TBLRISIMG add constraint TBLRISIMG_ibfk_3 foreign key (RISIDR)
      references TBLRIS (RISIDR);

alter table TBLRISIMG add constraint TBLRISIMG_ibfk_4 foreign key (ID_TBLIMG)
      references TBLIMG (ID_TBLIMG);

alter table TBLRISVIDEO add constraint TBLRISVIDEO_ibfk_1 foreign key (RISIDR)
      references TBLRIS (RISIDR);

alter table TBLRISVIDEO add constraint TBLRISVIDEO_ibfk_2 foreign key (ID_TBLVIDEO)
      references TBLVIDEO (ID_TBLVIDEO);

alter table TBLSTORIA add constraint TBLSTORIA_ibfk_1 foreign key (RISIDR)
      references TBLRIS (RISIDR);

alter table TBLVIDEO add constraint TBLVIDEO_ibfk_15 foreign key (HOSTID)
      references TLKPHOST (HOSTID);

alter table TBLVIDEO add constraint TBLVIDEO_ibfk_16 foreign key (MIMEID)
      references TLKPMIME (MIMEID);

alter table TBLVIDEO add constraint TBLVIDEO_ibfk_17 foreign key (FRUIBID)
      references TLKPFRUIB (FRUIBID);

alter table TBLVIDEO add constraint TBLVIDEO_ibfk_18 foreign key (ID_VIDEOGROUP)
      references VIDEOGROUP (ID_VIDEOGROUP);

alter table TMPFILEXML add constraint FK_REFERENCE_1 foreign key (RISIDR)
      references TBLRIS (RISIDR) on delete restrict on update restrict;

create view VIEWRELRISUNION as
(
select TBLRIS.RISIDR AS IDR,
       TBLRIS.RISNOTAPUB AS NOTA,
       TBLRIS.RISLIVELLO AS LIV,
       TBLIMG.MIMEID AS MIME,
       TBLRELRIS.RELRISSEQUENZA AS SEQ,
       TBLRELRIS.RELRISIDRARRIVO AS RELRISIDR,
       TBLRELRIS.TIPORELID AS TIPORELID 
  from (((TBLRIS join TBLIMG) 
                 join TBLRELRIS) 
                 join TBLRISIMG) 
 where ((TBLRELRIS.RELRISIDRPARTENZA = TBLRIS.RISIDR) and 
        (TBLRIS.RISIDR = TBLRISIMG.RISIDR) and 
        (TBLRISIMG.ID_TBLIMG = TBLIMG.ID_TBLIMG)) 
union 
select TBLRIS.RISIDR AS IDR,
       TBLRIS.RISNOTAPUB AS NOTA,
       TBLRIS.RISLIVELLO AS LIV,
       TBLIMG.MIMEID AS MIME,
       TBLRELRIS.RELRISSEQUENZA AS SEQ,
       TBLRELRIS.RELRISIDRPARTENZA AS RELRISIDR,
       TBLRELRIS.TIPORELID AS TIPORELID 
  from (((TBLRIS join TBLIMG) 
                 join TBLRELRIS) 
                 join TBLRISIMG) 
 where ((TBLRELRIS.RELRISIDRARRIVO = TBLRIS.RISIDR) and 
        (TBLRIS.RISIDR = TBLRISIMG.RISIDR) and 
        (TBLRISIMG.ID_TBLIMG = TBLIMG.ID_TBLIMG))
);

create view VIEWRELUNION as
(
select distinct TBLRELRIS.TIPORELID AS ID,
                TLKPTIPOREL.TIPORELDESAP AS DES,
                'AP' AS DIREZ,
                'G' AS NAT,
                TBLRELRIS.RELRISIDRARRIVO AS RELRISIDR 
  from (TBLRELRIS join TLKPTIPOREL) 
 where ((TBLRELRIS.TIPORELID = TLKPTIPOREL.TIPORELID) and 
        (TLKPTIPOREL.TIPORELNAT = 'G')) 
union 
select distinct TBLRELRIS.TIPORELID AS ID,
                TLKPTIPOREL.TIPORELDESPA AS DES,
                'PA' AS DIREZ,
                'G' AS NAT,
                TBLRELRIS.RELRISIDRPARTENZA AS RELRISIDR 
  from (TBLRELRIS join TLKPTIPOREL) 
 where ((TBLRELRIS.TIPORELID = TLKPTIPOREL.TIPORELID) and 
        (TLKPTIPOREL.TIPORELNAT = 'G')) 
union 
select distinct TBLRELRIS.TIPORELID AS ID,
                TLKPTIPOREL.TIPORELDESAP AS DES,
                'EQ' AS DIREZ,
                'E' AS NAT,
                TBLRELRIS.RELRISIDRARRIVO AS RELRISIDR 
  from (TBLRELRIS join TLKPTIPOREL) 
 where ((TBLRELRIS.TIPORELID = TLKPTIPOREL.TIPORELID) and 
        (TLKPTIPOREL.TIPORELNAT = 'E')) 
union 
select distinct TBLRELRIS.TIPORELID AS ID,
                TLKPTIPOREL.TIPORELDESPA AS DES,
                'EQ' AS DIREZ,
                'E' AS NAT,
                TBLRELRIS.RELRISIDRPARTENZA AS RELRISIDR 
  from (TBLRELRIS join TLKPTIPOREL) 
 where ((TBLRELRIS.TIPORELID = TLKPTIPOREL.TIPORELID) and 
        (TLKPTIPOREL.TIPORELNAT = 'E'))
);