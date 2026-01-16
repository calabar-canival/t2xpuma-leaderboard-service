ALTER TABLE leaderboard
    ADD CONSTRAINT uq_leaderboard_phone_number UNIQUE (phone_number);
