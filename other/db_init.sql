CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY NOT NULL,
  name text NOT NULL,
  password text NOT NULL
);

CREATE TABLE IF NOT EXISTS game (
  id SERIAL PRIMARY KEY NOT NULL,
  in_game_id text,
  name text,
  winner int,
  start timestamp,
  finish timestamp
);

CREATE TABLE IF NOT EXISTS game_user (
  game_id int NOT NULL,
  player_id int NOT NULL,
  score int
);

ALTER TABLE game_user ADD FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_user ADD FOREIGN KEY (player_id) REFERENCES users (id);

ALTER TABLE game ADD FOREIGN KEY (winner) REFERENCES users (id);
