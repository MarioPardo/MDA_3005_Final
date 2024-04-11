CREATE TABLE Customer (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    profile_ids INT[]
);

CREATE TABLE Health (
    id SERIAL PRIMARY KEY,
    weight FLOAT,
    age INT,
    height FLOAT,
    body_fat_percentage FLOAT,
    health_conditions TEXT[] -- Array of health conditions
);

CREATE TABLE Profile (
    id SERIAL PRIMARY KEY,
    goal_weight INT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    goal_date DATE,
    achievements TEXT[],
    health_id INT,
    schedules INT[],
    routines INT[],
    FOREIGN KEY (health_id) REFERENCES Health(id)
);

CREATE TABLE Schedule (
    id SERIAL PRIMARY KEY,
    classes INT[] DEFAULT '{}'
);

CREATE TABLE Class (
    id SERIAL PRIMARY KEY,
    time TIME,
    is_group BOOLEAN,
    room_number INT DEFAULT NULL,
    trainer_id INT,
    participants INT[]
);

CREATE TYPE int_tuple AS (
    x INTEGER,
    y INTEGER
);

CREATE TABLE Routine (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    exercises TEXT[] -- Array of exercise names
);



