INSERT INTO cards (name, tipo, organ_type)
VALUES 
    -- Cartas de virus
    ('Virus Heart', 'virus',   'heart'),
    ('Virus Intestine', 'virus', 'intestine'),
    ('Virus Brain', 'virus',   'brain'),
    ('Virus Lungs', 'virus',   'lungs'),
    ('Virus Magic', 'virus',   'magic'),
    
    -- Cartas de cura
    ('Cure Heart', 'cure', 'heart'),
    ('Cure Intestine', 'cure', 'intestine'),
    ('Cure Brain', 'cure', 'brain'),
    ('Cure Lungs', 'cure', 'lungs'),
    ('Cure Magic', 'cure', 'magic'),
    
    -- Cartas de órganos
    ('Heart Organ', 'organ', 'heart'),
    ('Intestine Organ', 'organ', 'intestine'),
    ('Brain Organ', 'organ', 'brain'),
    ('Lungs Organ', 'organ', 'lungs'),
    ('Magic Organ', 'organ', 'magic');


INSERT INTO cards (name, tipo)
VALUES 
    -- Cartas de acción
    ('Steal Organ', 'action'),           -- Robar carta
    ('Infect Player', 'action'),        -- Contagiar a otro jugador
    ('Exchange Card', 'action'),        -- Intercambiar carta
    ('Change Body', 'action'),          -- Cambiar cuerpo
    ('Discard Cards', 'action');        -- Descartar cartas