# docs/contrats/metier/Produit.md

# Contrat local métier — Produit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.Produit`
- interface liée : `ProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`Produit` est l'objet métier final rattaché à un parent `SousTypeProduit`, lui-même rattaché à un `TypeProduit`.

Il porte :

- `idProduit` ;
- `produit` ;
- un parent `SousTypeProduitI` ;
- un booléen `valide` recalculé.

## 3) Ordre de lecture obligatoire

1. `CONTRAT_IA.md` ;
2. `CoucheMetier.md` ;
3. `Produit.md` ;
4. `ProduitI.java` ;
5. `Produit.java` ;
6. `SousTypeProduit.java` si le parent est concerné ;
7. `TypeProduit.java` si l'accès indirect au grand-parent est concerné ;
8. `CloneContext.java` si le clonage est concerné ;
9. `ProduitTest.java` ;
10. `MetierGlobalConformiteTest.java` ;
11. `TypeProduitSousTypeProduitIntegrationTest.java` si la hiérarchie complète est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherProduit()` ;
6. `compareTo(ProduitI)` ;
7. `compareFields(ProduitI)` ;
8. `clone()` ;
9. `cloneDeep()` ;
10. `deepClone(CloneContext)` ;
11. `cloneWithoutParent()` ;
12. `recalculerValide()` ;
13. `normalize(String)` ;
14. CSV / JTable ;
15. `getTypeProduit()` ;
16. `isValide()` ;
17. getters / setters.

## 5) Égalité et hashCode

`Produit.equals(...)` compare :

1. le parent `SousTypeProduit` ;
2. le libellé `produit`.

Règles :

- comparaison insensible à la casse sur le libellé produit ;
- le grand-parent `TypeProduit` est pris en compte uniquement via le parent `SousTypeProduit` ;
- l'identifiant persistant ne participe pas à l'égalité métier ;
- le verrouillage suit les règles concurrentes validées ;
- ne pas remplacer cette règle par une égalité sur `TypeProduit + SousTypeProduit + Produit` calculée séparément.

## 6) Comparaison

`compareTo(ProduitI)` compare d'abord le parent `SousTypeProduit`, puis le libellé `produit`.

Règles :

- comparaison insensible à la casse ;
- valeurs `null` déterministes ;
- ordre de verrouillage par `System.identityHashCode(...)` avec verrou de classe en cas de collision ;
- les tests de symétrie, transitivité et absence de deadlock sont spécification.

## 7) Parent SousTypeProduit

Règles :

- `setSousTypeProduit(...)` est le setter intelligent ;
- changer de parent retire le produit de l'ancien parent et l'ajoute au nouveau ;
- `getTypeProduit()` lit le `TypeProduit` via le parent ;
- un parent absent est autorisé dans certains états mais rend l'objet non valide ;
- le re-parenting doit éviter les deadlocks `Produit <-> SousTypeProduit`.

## 8) Validité

`valide` dépend du libellé `produit` et du parent `SousTypeProduit`.

Règles :

- `recalculerValide()` est la source du recalcul ;
- les setters doivent recalculer ;
- `cloneWithoutParent()` doit recalculer ;
- ne jamais forcer `valide` sans recalcul.

## 9) Clonage

- `clone()` produit un clone profond via `CloneContext` ;
- `cloneWithoutParent()` copie les champs propres sans parent ;
- `deepClone(CloneContext)` réutilise le contexte fourni ;
- le parent `SousTypeProduit` est cloné profondément si présent ;
- les relations bidirectionnelles doivent rester cohérentes.

## 10) CSV / JTable

- `getEnTeteCsv()` retourne exactement `idproduit;type de produit;sous-type de produit;produit;` ;
- `toStringCsv()` respecte l'ordre `idproduit`, `type de produit`, `sous-type de produit`, `produit` ;
- les valeurs absentes sont rendues par le texte `null` en CSV ;
- `getEnTeteColonne(0)` : `idproduit` ;
- `getEnTeteColonne(1)` : `type de produit` ;
- `getEnTeteColonne(2)` : `sous-type de produit` ;
- `getEnTeteColonne(3)` : `produit` ;
- autre indice : `invalide` ;
- `getValeurColonne(...)` retourne `null` pour une colonne valide sans valeur, et `invalide` pour une colonne non supportée.

## 11) Tests de référence

`ProduitTest.java` contient 39 tests validés couvrant :

- constructeurs ;
- comportement général ;
- égalité / hashCode ;
- thread-safety ;
- `toString` ;
- `compareTo` ;
- symétrie et transitivité ;
- absence de deadlock ;
- clonage ;
- validité ;
- CSV / JTable ;
- getters / setters ;
- relation au parent.

## 12) Interdictions

- ne jamais faire participer directement le grand-parent `TypeProduit` à l'égalité autrement que via le parent `SousTypeProduit` ;
- ne jamais casser `SousTypeProduit <-> Produit` ;
- ne jamais supprimer le recalcul de validité ;
- ne jamais remplacer le clonage profond par une copie superficielle ;
- ne jamais modifier les en-têtes CSV/JTable historiques.
