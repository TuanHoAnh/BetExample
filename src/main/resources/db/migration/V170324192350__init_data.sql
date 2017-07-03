
INSERT INTO competitions(id, name, logo, rounds, alias_key)
  VALUES (1, 'WC2017', '/images/worldcup.jpg', '1/8,1/16', 'WC');

INSERT INTO competitors(id, name, logo, competition_id)
  VALUES (1, 'Brazil', '/images/brazil.png', 1);

INSERT INTO competitors(id, name, logo, competition_id)
  VALUES (2, 'Germany', '/images/germany.png', 1);

INSERT INTO matches(id, competition_id, round, competitor1_id, competitor2_id, start_time, location, score1, score2)
  VALUES (1, 1, '1/16', 1, 2, now(), 'Berlin', 0, 1);

INSERT INTO matches(id, competition_id, round, competitor1_id, competitor2_id, start_time, location)
  VALUES (2, 1, '1/8', 1, 2, now(), 'Rio-de-Janeiro');
