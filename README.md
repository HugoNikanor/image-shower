# Image Shower

A web-service similar to Tumblr, which means that it displays multiple
posts, with optional image and text. Also has support for keywords on
posts.

## Usage

Either run `image-shower.main` (through `lein run`), compile a jar and
run that, or run `lein ring server-headless`. If run through main
loads a configuration file from
`$XDG_CONFIG_HOME/image-shower/config.clj`, or
`/etc/image-shower/config.clj` if the first file is missing.

Currently no configuration file (or command line flags) are handled
when running through through the ring plugin.

**Configuration keys**

    :port 3000
    :host "0.0.0.0"

## Setup
An SQL database is required, which has to be manually set up and
populated from somewhere else. The schema for the database is in
`db/schema-{postgres,sqlite}.sql`.

Currently only postgresql and sqlite3 are supported, but it should be
trivial to add any other that the underlying system supports.

**Configuration keys**

    :db-type 'sqlite3
    :db-args {:db "database.db"} ;; passed directly to kormas method

----------------------------------------

A directory for static media is also required.

**Configuration keys**

    :data-path "/usr/local/var/image-shower"


## License

Copyright © 2018-2020 Hugo Hornquist

Distributed under the MIT License.
