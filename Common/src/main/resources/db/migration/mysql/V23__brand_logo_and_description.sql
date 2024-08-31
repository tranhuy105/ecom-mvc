ALTER TABLE brands
ADD COLUMN logo VARCHAR(255),
ADD COLUMN description VARCHAR(512);

UPDATE brands
SET logo = 'default_logo.png', description = 'No description available';

ALTER TABLE brands
MODIFY COLUMN logo VARCHAR(255) NOT NULL,
MODIFY COLUMN description VARCHAR(512) NOT NULL;
