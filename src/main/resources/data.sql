# INSERT INTO training_type (id, name) VALUES (1, 'fitness');
# INSERT INTO training_type (id, name) VALUES (2, 'yoga');
# INSERT INTO training_type (id, name) VALUES (3, 'Zumba');
# INSERT INTO training_type (id, name) VALUES (4, 'stretching');
# INSERT INTO training_type (id, name) VALUES (5, 'resistance');
-- Insert data only if a table is empty
INSERT INTO training_type (id, name) SELECT 1, 'fitness' WHERE NOT EXISTS (SELECT 1 FROM training_type WHERE id = 1);
INSERT INTO training_type (id, name) SELECT 2, 'yoga' WHERE NOT EXISTS (SELECT 2 FROM training_type WHERE id = 2);
INSERT INTO training_type (id, name) SELECT 3, 'Zumba' WHERE NOT EXISTS (SELECT 3 FROM training_type WHERE id = 3);
INSERT INTO training_type (id, name) SELECT 4, 'stretching' WHERE NOT EXISTS (SELECT 4 FROM training_type WHERE id = 4);
INSERT INTO training_type (id, name) SELECT 5, 'resistance' WHERE NOT EXISTS (SELECT 5 FROM training_type WHERE id = 5);