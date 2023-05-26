CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  slack_user_id VARCHAR UNIQUE NOT NULL
);

CREATE TABLE repositories (
  id BIGSERIAL PRIMARY KEY,
  owner VARCHAR NOT NULL,
  repository VARCHAR NOT NULL,
  CONSTRAINT unique_repos UNIQUE (owner, repository)
);

CREATE TABLE subscriptions (
  user_id BIGSERIAL NOT NULL,
  repository_id BIGSERIAL NOT NULL,
  subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, repository_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (repository_id) REFERENCES repositories(id) ON DELETE CASCADE
);