-- For postgresql
	
CREATE TABLE entries (
	id INT PRIMARY KEY NOT NULL,
	title     TEXT NOT NULL,
	text      TEXT,
	media_url TEXT,
	image_alt TEXT,
	timestamp TIMESTAMP
);

COMMENT ON COLUMN entries.title     IS 'Post Title!';
COMMENT ON COLUMN entries.text      IS 'Extra text in post';
COMMENT ON COLUMN entries.media_url IS 'filename.png';
COMMENT ON COLUMN entries.image_alt IS 'HTML image alt.';
	
CREATE TABLE tags (
	id   INT PRIMARY KEY NOT NULL,
	text TEXT NOT NULL UNIQUE
);
	
CREATE TABLE tag_map (
	id INT PRIMARY KEY NOT NULL,
	entry_id INT NOT NULL REFERENCES entries(id),
	tag_id   INT NOT NULL REFERENCES tags(id),
    UNIQUE (entry_id, tag_id)
);
