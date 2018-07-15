-- For postgresql

CREATE TYPE ptype AS ENUM
    ('photo', 'text', 'video');

CREATE TABLE entry (
	id SERIAL PRIMARY KEY NOT NULL,
    post_id   BIGINT UNIQUE,
	title     TEXT,
    slug      TEXT,
	text      TEXT,
	timestamp TIMESTAMP NOT NULL DEFAULT now(),
    post_type ptype NOT NULL
);

COMMENT ON COLUMN entry.post_id IS 'Old ID from Tumblr, will probably be removed';
COMMENT ON COLUMN entry.title   IS 'Post Title!';
COMMENT ON COLUMN entry.slug    IS 'post-title';
COMMENT ON COLUMN entry.text    IS 'Extra text in post, might contain HTML';

CREATE TABLE media (
	id  SERIAL PRIMARY KEY NOT NULL,
    url TEXT NOT NULL,
    alt TEXT,
    entry_id INT NOT NULL REFERENCES entry(id)
);

COMMENT ON COLUMN media.url IS 'filename.png';
COMMENT ON COLUMN media.alt IS 'HTML image alt.';

CREATE TABLE tag (
	id   SERIAL PRIMARY KEY NOT NULL,
	text TEXT NOT NULL UNIQUE
);

CREATE TABLE tag_map (
	id SERIAL PRIMARY KEY NOT NULL,
	entry_id INT NOT NULL REFERENCES entry(id),
	tag_id   INT NOT NULL REFERENCES tag(id),
    UNIQUE (entry_id, tag_id)
);
