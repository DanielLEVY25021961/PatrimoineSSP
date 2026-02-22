/* truncate-test.sql situé dans src/test/resources/ */
/* Les tables sont vidées de la plus dépendante à la moins dépendante 
 * (d'abord PRODUITS, puis SOUS_TYPES_PRODUIT, 
 * enfin TYPES_PRODUIT). 
 * Cela évite les erreurs de violation de contraintes de clé étrangère. */
DELETE FROM PRODUITS;
DELETE FROM SOUS_TYPES_PRODUIT;
DELETE FROM TYPES_PRODUIT;
/* RESTART WITH 1 : Réinitialise le compteur 
 * d'auto-incrément pour chaque table. */
ALTER TABLE PRODUITS ALTER COLUMN ID_PRODUIT RESTART WITH 1;
ALTER TABLE SOUS_TYPES_PRODUIT ALTER COLUMN ID_SOUS_TYPE_PRODUIT RESTART WITH 1;
ALTER TABLE TYPES_PRODUIT ALTER COLUMN ID_TYPE_PRODUIT RESTART WITH 1;