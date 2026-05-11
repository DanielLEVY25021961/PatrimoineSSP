# docs/contrats/metier/SousTypeProduit.md

# Contrat local métier — SousTypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.SousTypeProduit`
- interface liée : `SousTypeProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`SousTypeProduit` est l'objet métier intermédiaire entre `TypeProduit` et `Produit`.

Il porte :

- `idSousTypeProduit` ;
- `sousTypeProduit` ;
- un parent `TypeProduitI` ;
- une collection de `ProduitI` enfants ;
- un booléen `valide` recalculé.

## 3) Ordre de lecture obligatoire

1. `CONTRAT_IA.md` ;
2. `CoucheMetier.md` ;
3. `SousTypeProduit.md` ;
4. `SousTypeProduitI.java` ;
5. `SousTypeProduit.java` ;
6. `TypeProduit.java` si le parent est concerné ;
7. `Produit.java` si les enfants sont concernés ;
8. `CloneContext.java` si le clonage est concerné ;
9. `SousTypeProduitTest.java` ;
10. `TypeProduitSousTypeProduitIntegrationTest.java` si la relation parent est concernée ;
11. `MetierGlobalConformiteTest.java` si la cohérence globale est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherSousTypeProduit()` ;
6. `compareTo(SousTypeProduitI)` ;
7. `compareFields(SousTypeProduitI)` ;
8. `clone()` ;
9. `cloneDeep()` ;
10. `deepClone(CloneContext)` ;
11. `cloneWithoutParentAndChildren()` ;
12. `recalculerValide()` ;
13. `normalize(String)` ;
14. CSV / JTable ;
15. `ajouterSTPauProduit(...)` ;
16. `retirerSTPauProduit(...)` ;
17. méthodes internes d'ajout/retrait par identité ;
18. `isValide()` ;
19. getters / setters ;
20. remplacement de collections.

## 5) Égalité et hashCode

`SousTypeProduit.equals(...)` compare :

1. le parent `TypeProduit` ;
2. le libellé `sousTypeProduit`.

Règles :

- comparaison insensible à la casse sur les libellés ;
- l'identifiant persistant ne participe pas à l'égalité ;
- la collection de produits ne participe pas à l'égalité ;
- le parent est comparé comme objet métier, pas seulement comme chaîne isolée ;
- verrouillage déterministe par `System.identityHashCode(...)` avec verrou de classe en cas de collision.

## 6) Comparaison

`compareTo(SousTypeProduitI)` compare d'abord le parent `TypeProduit`, puis le libellé `sousTypeProduit`.

Les valeurs `null` sont ordonnées de manière déterministe selon le code validé. La comparaison des libellés est insensible à la casse.

## 7) Parent TypeProduit

Règles :

- `setTypeProduit(...)` est le setter intelligent de rattachement au parent ;
- changer de parent doit retirer l'objet de l'ancien parent et l'ajouter au nouveau ;
- le re-parenting doit éviter les deadlocks ;
- les opérations doivent recalculer `valide` ;
- un parent `null` est autorisé dans certains états, mais rend l'objet non valide.

## 8) Enfants Produit

Règles :

- `ajouterSTPauProduit(...)` rattache un `Produit` au présent objet métier ;
- `retirerSTPauProduit(...)` détache un `Produit` ;
- les méthodes internes utilisent la comparaison par identité lorsque nécessaire ;
- ne pas utiliser `contains()` ou `remove(Object)` si cela déclenche `equals(...)` sous verrou ;
- les doublons par identité ne doivent pas être ajoutés ;
- `setProduits(...)` doit reconstruire la cohérence avec les produits fournis.

## 9) Validité

`valide` est vrai uniquement lorsque l'objet dispose des éléments métier requis par le code validé, notamment parent et libellé.

Règles :

- `recalculerValide()` est la source du recalcul ;
- les setters et clones partiels doivent recalculer ;
- `cloneWithoutParentAndChildren()` doit produire un clone sans parent ni enfants, avec validité recalculée ;
- ne jamais forcer `valide` sans recalcul.

## 10) Clonage

- `clone()` produit un clone profond via `CloneContext` ;
- `cloneWithoutParentAndChildren()` copie les champs propres sans parent ni enfants ;
- `deepClone(CloneContext)` réutilise le contexte, clone le parent si nécessaire, clone les produits enfants et reconstruit les relations ;
- le clonage ne doit pas dupliquer une source déjà présente dans le contexte.

## 11) CSV / JTable

- `getEnTeteCsv()` retourne l'en-tête validé `idSousTypeProduit;type de produit;sous-type de produit;` ;
- `toStringCsv()` respecte l'ordre `idSousTypeProduit`, `type de produit`, `sous-type de produit` ;
- les valeurs absentes sont rendues par le texte `null` en CSV ;
- `getEnTeteColonne(0)` : `idSousTypeProduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(1)` délègue au parent via `getValeurColonne(1)`.

## 12) Tests de référence

`SousTypeProduitTest.java` contient 36 tests validés couvrant :

- constructeurs ;
- égalité et comparaison ;
- insensibilité à la casse ;
- thread-safety ;
- clonage ;
- CSV / JTable ;
- gestion des produits ;
- validité ;
- clone sans parent ni enfants.

## 13) Interdictions

- ne jamais parler de `SousTypeProduit` comme d'un simple couple ; c'est l'objet métier testé ;
- ne jamais casser `TypeProduit <-> SousTypeProduit` ;
- ne jamais casser `SousTypeProduit <-> Produit` ;
- ne jamais faire participer l'identifiant ou la collection de produits à l'égalité ;
- ne jamais exposer une collection interne mutable partagée ;
- ne jamais supprimer le recalcul de validité.
