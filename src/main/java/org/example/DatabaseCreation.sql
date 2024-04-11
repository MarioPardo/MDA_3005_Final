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


