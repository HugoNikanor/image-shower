-- This file is dumped from sqlite3, after auto-converting an postgres
-- database created with schema-postgres.sql. Thereby the barebones
-- schema here.

CREATE TABLE entry ([id], [post_id], [title], [slug], [text], [timestamp], [post_type], [page_id]);
CREATE TABLE media ([id], [url], [alt], [entry_id]);
CREATE TABLE page ([id], [name], [fancy_name]);
CREATE TABLE tag ([id], [text]);
CREATE TABLE tag_map ([id], [entry_id], [tag_id]);
