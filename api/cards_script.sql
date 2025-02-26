INSERT INTO cards (name, tipo)
VALUES 
    -- Cartas de virus
    ('Virus Heart', 'virus'),
    ('Virus Stomach', 'virus'),
    ('Virus Brain', 'virus'),
    ('Virus Liver', 'virus'),
    ('Virus Magic', 'virus'),
    
    -- Cartas de cura
    ('Cure Heart', 'cure'),
    ('Cure Stomach', 'cure'),
    ('Cure Brain', 'cure'),
    ('Cure Liver', 'cure'),
    ('Cure Magic', 'cure'),
    
    -- Cartas de órganos
    ('Heart Organ', 'organ'),
    ('Stomach Organ', 'organ'),
    ('Brain Organ', 'organ'),
    ('Liver Organ', 'organ'),
    ('Magic Organ', 'organ'),

    -- Cartas de acción
    ('Steal Card', 'action'),           -- Robar carta
    ('Infect Player', 'action'),        -- Contagiar a otro jugador
    ('Exchange Card', 'action'),        -- Intercambiar carta
    ('Change Body', 'action'),          -- Cambiar cuerpo
    ('Discard Cards', 'action');        -- Revivir a un jugador