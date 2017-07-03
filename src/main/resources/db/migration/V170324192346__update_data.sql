ALTER TABLE competitions
ADD COLUMN alias_key VARCHAR(50) NOT NULL;

ALTER TABLE competitions ADD CONSTRAINT unique_alias_key UNIQUE(alias_key);
