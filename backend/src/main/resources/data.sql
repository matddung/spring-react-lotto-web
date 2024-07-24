USE `lotto`;

/*Data for the table `answer` */

/*Data for the table `question` */

/*Data for the table `token` */

/*Data for the table `user` */

insert IGNORE into `user`(`id`,`email`,`nickname`,`password`,`provider`,`provider_id`,`role`) values
(1,admin@admin.a,관리자,$2a$10$yt/QG50osgem7Ndg1tyFbOnveW1B1bJg0F4sywQqKZFqt/eXAo9Dy,0,local.admin,ADMIN),
(2,string@aa.bb,string,$2a$10$Gg1Ve5JLKYlcphuDSvzcI.wILD0FwnIsyPilBM5vGvAKnnLLHtwYa,0,NULL,USER),
(3,string1@aa.bb,string1,$2a$10$XlwI5XkCuQbtuYaN7gtae.l64xiZajesEfjsCUFl/dK1xqS3oXiWu,0,NULL,USER);

/*Data for the table `user_lotto` */