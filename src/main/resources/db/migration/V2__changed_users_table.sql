alter table users alter column points type integer using points::integer;
alter table users drop column status;
alter table users add column channel varchar(255);