CREATE TABLE users (
  id SERIAL PRIMARY KEY NOT NULL,
  name text NOT NULL,
  password text NOT NULL
);

CREATE TABLE game (
  id SERIAL PRIMARY KEY NOT NULL,
  start timestamp,
  finish timestamp
);

CREATE TABLE game_user (
  game_id int NOT NULL,
  player_id int NOT NULL
);

ALTER TABLE game_user ADD FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE game_user ADD FOREIGN KEY (player_id) REFERENCES users (id);
