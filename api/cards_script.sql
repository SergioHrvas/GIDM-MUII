INSERT INTO cards (name, tipo, organ_type)
VALUES 
    -- Cartas de virus
    ('Virus Heart', 'virus',   'heart'),
    ('Virus Stomach', 'virus', 'stomach'),
    ('Virus Brain', 'virus',   'brain'),
    ('Virus Liver', 'virus',   'liver'),
    ('Virus Magic', 'virus',   'magic'),
    
    -- Cartas de cura
    ('Cure Heart', 'cure', 'heart'),
    ('Cure Stomach', 'cure', 'stomach'),
    ('Cure Brain', 'cure', 'brain'),
    ('Cure Liver', 'cure', 'liver'),
    ('Cure Magic', 'cure', 'magic'),
    
    -- Cartas de órganos
    ('Heart Organ', 'organ', 'heart'),
    ('Stomach Organ', 'organ', 'stomach'),
    ('Brain Organ', 'organ', 'brain'),
    ('Liver Organ', 'organ', 'liver'),
    ('Magic Organ', 'organ', 'magic');


INSERT INTO cards (name, tipo)
VALUES 
    -- Cartas de acción
    ('Steal Card', 'action'),           -- Robar carta
    ('Infect Player', 'action'),        -- Contagiar a otro jugador
    ('Exchange Card', 'action'),        -- Intercambiar carta
    ('Change Body', 'action'),          -- Cambiar cuerpo
    ('Discard Cards', 'action');        -- Descartar cartas