
--STEP 1

--creating customers
INSERT INTO Customer (username, password, profile_ids) VALUES ('customer1', 'password1', '{1,2}');
INSERT INTO Customer (username, password, profile_ids) VALUES ('customer2', 'password2', '{3}');

--creating health for profiles
INSERT INTO Health (weight, age, height, body_fat_percentage, health_conditions) VALUES (70.5, 25, 175, 15.0, '{}');
INSERT INTO Health (weight, age, height, body_fat_percentage, health_conditions) VALUES (65.0, 30, 168, 20.0, '{}');
INSERT INTO Health (weight, age, height, body_fat_percentage, health_conditions) VALUES (80.0, 40, 180, 18.0, '{}');
--respectively they should give have IDs of 1,2,3
    --these will be used later when linking them to their respective Profiles

--STEP 2

--creating profiles
INSERT INTO Profile (id, goal_weight, first_name, last_name, goal_date, achievements, health_id, schedules, routines)
VALUES (1, 70, 'John', 'Doe', '2024-05-01', '{}', 1, '{1}', '{2}');

INSERT INTO Profile (id, goal_weight, first_name, last_name, goal_date, achievements, health_id, schedules, routines)
VALUES (2, 75, 'Alice', 'Smith', '2024-06-01', '{}', 2, '{2}', '{1}');

INSERT INTO Profile (id, goal_weight, first_name, last_name, goal_date, achievements, health_id, schedules, routines)
VALUES (3, 80, 'Emily', 'Johnson', '2024-07-01', '{}', 3, '{3}', '{1}');


--schedules
INSERT INTO Schedule (id, classes) VALUES (1, '{3}'); --john
INSERT INTO Schedule (id, classes) VALUES (2, '{4}'); --alice
INSERT INTO Schedule (id, classes) VALUES (3, '{5}'); --emily
INSERT INTO Schedule (id, classes) VALUES (4, '{1,4,5}'); --trainer 1
INSERT INTO Schedule (id, classes) VALUES (5, '{2,3}'); --trainer 2

--classes
    --group
INSERT INTO Class (time, is_group, room_number, trainer_id, participants) VALUES ('9:00', true, 101, 1, '{}');
INSERT INTO Class (time, is_group, room_number, trainer_id, participants) VALUES ('11:00', true, 102, 2, '{}');
    --PT
INSERT INTO Class (time, is_group, room_number, trainer_id, participants) VALUES ('12:00', false, 103, 2, '{1}');
INSERT INTO Class (time, is_group, room_number, trainer_id, participants) VALUES ('13:00', false, 104, 1, '{2}');
INSERT INTO Class (time, is_group, room_number, trainer_id, participants) VALUES ('16:00', false, 105, 1, '{3}');


--trainers
INSERT INTO Trainer (first_name, last_name, schedules, clients, routines, working_hours, username, password)
VALUES ('Michael', 'Smith', '{4}', '{2,3}', '{1}', '{"09:00", "17:00"}', 'michael_smith', 'password1');

INSERT INTO Trainer (first_name, last_name, schedules, clients, routines, working_hours, username, password)
VALUES ('Emma', 'Johnson', '{5}', '{1}', '{2}', '{"08:00", "16:00"}', 'emma_johnson', 'password2');


-- Inserting routines
INSERT INTO Routine (name, exercises) VALUES ('Morning Workout', '{"Push-ups", "Sit-ups", "Squats"}');
INSERT INTO Routine (name, exercises) VALUES ('Evening Workout', '{"Bicep Curls", "Tricep Dips", "Lunges"}');

-- Inserting repair ticket
INSERT INTO RepairTicket (issue_description, ticket_date) VALUES ('Treadmill belt needs replacement', '2024-04-11');