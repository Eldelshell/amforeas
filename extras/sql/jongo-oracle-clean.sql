/**
* Copyright (C) 2011, 2012 Alejandro Ayuso
*
* This file is part of Jongo.
* Jongo is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
* 
* Jongo is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with Jongo.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
* Oracle Script to clean up a database
*/

DROP VIEW jongo_demo.MAKER_STATS_2010;
DROP PROCEDURE jongo_demo.insert_comment;
DROP PROCEDURE jongo_demo.get_year_sales;
DROP FUNCTION jongo_demo.simpleStoredProcedure;

DROP TRIGGER jongo_demo.users_trigger;
DROP TRIGGER jongo_demo.maker_trigger;
DROP TRIGGER jongo_demo.car_trigger;
DROP TRIGGER jongo_demo.comments_trigger;
DROP TRIGGER jongo_demo.pictures_trigger;
DROP TRIGGER jongo_demo.sales_stats_trigger;
DROP TRIGGER jongo_demo.maker_stats_trigger;

DROP TABLE jongo_demo.users;
DROP TABLE jongo_demo.maker;
DROP TABLE jongo_demo.car;
DROP TABLE jongo_demo.comments;
DROP TABLE jongo_demo.pictures;
DROP TABLE jongo_demo.sales_stats;
DROP TABLE jongo_demo.maker_stats;
DROP TABLE jongo_demo.empty;

DROP SEQUENCE jongo_demo.users_id_sequence;
DROP SEQUENCE jongo_demo.maker_id_sequence;
DROP SEQUENCE jongo_demo.car_id_sequence;
DROP SEQUENCE jongo_demo.comments_id_sequence;
DROP SEQUENCE jongo_demo.pictures_id_sequence;
DROP SEQUENCE jongo_demo.sales_stats_id_sequence;
DROP SEQUENCE jongo_demo.maker_stats_id_sequence;