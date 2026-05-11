# docs/contrats/metier/TypeProduit.md

# Contrat local métier — TypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.TypeProduit`
- interface liée : `TypeProduitI`
- contrat de couche : `docs/contrats/metier/CoucheMetier.md`

## 2) Rôle métier

`TypeProduit` est la racine métier de la hiérarchie `TypeProduit -> SousTypeProduit -> Produit`.

Il porte :

- `idTypeProduit` ;
- `typeProduit` ;
- une collection de `SousTypeProduitI` enfants.

## 3) Ordre de lecture obligatoire

Avant toute action :

1. `docs/ai/CONTRAT_IA.md` ;
2. `docs/contrats/metier/CoucheMetier.md` ;
3. `docs/contrats/metier/TypeProduit.md` ;
4. `TypeProduitI.java` ;
5. `TypeProduit.java` ;
6. `SousTypeProduitI.java` et `SousTypeProduit.java` si les enfants sont concernés ;
7. `CloneContext.java` si le clonage est concerné ;
8. `TypeProduitTest.java` ;
9. `TypeProduitSousTypeProduitIntegrationTest.java` si la bidirectionnalité est concernée ;
10. `MetierGlobalConformiteTest.java` si la cohérence globale est concernée.

## 4) Ordre des méthodes à conserver

Ordre observé dans la classe validée :

1. constructeurs ;
2. `hashCode()` ;
3. `equals(Object)` ;
4. `toString()` ;
5. `afficherTypeProduit()` ;
6. `afficherSousTypeProduits()` ;
7. `compareTo(TypeProduitI)` ;
8. `compareFields(TypeProduitI)` ;
9. `clone()` ;
10. `cloneDeep()` ;
11. `deepClone(CloneContext)` ;
12. `cloneWithoutChildren()` ;
13. `rattacherEnfantSTP(SousTypeProduitI)` ;
14. `rattacherSiNecessaire(SousTypeProduitI)` ;
15. `detacherEnfantSTP(SousTypeProduitI)` ;
16. `detacherSiNecessaire(SousTypeProduitI)` ;
17. méthodes internes d'ajout/retrait de sous-types ;
18. `normalize(String)` ;
19. CSV / JTable ;
20. getters / setters ;
21. méthodes de verrouillage multi-objets.

Ne jamais réordonner sans demande explicite.

## 5) Égalité et hashCode

`equals(...)` est une égalité métier sur `typeProduit` uniquement.

Règles :

- comparaison insensible à la casse ;
- deux libellés `null` sont égaux sur ce critère ;
- un libellé `null` et un libellé non `null` ne sont pas égaux ;
- la collection d'enfants ne participe pas à l'égalité ;
- l'identifiant persistant ne participe pas à l'égalité métier ;
- le verrouillage multi-objets est ordonné par `System.identityHashCode(...)` avec verrou de classe en cas de collision.

`hashCode()` doit rester cohérent avec l'égalité et utiliser la même normalisation métier.

## 6) Comparaison

`compareTo(TypeProduitI)` compare le libellé `typeProduit` de manière insensible à la casse.

Règles :

- `this` non `null` est supérieur à un objet comparé `null` selon le comportement validé ;
- les valeurs `null` sont ordonnées de manière déterministe ;
- le verrouillage suit l'ordre `System.identityHashCode(...)` pour éviter les deadlocks.

## 7) Relations parent/enfants

`TypeProduit` est responsable de la cohérence bidirectionnelle avec les `SousTypeProduit` enfants.

Règles :

- `rattacherEnfantSTP(null)` ne doit pas créer d'effet parasite ;
- un enfant blank ou non exploitable est ignoré selon le comportement validé ;
- une mauvaise instance doit être traitée conformément au code validé ;
- le rattachement utilise le setter intelligent `SousTypeProduit.setTypeProduit(this)` ;
- le détachement utilise le setter intelligent `SousTypeProduit.setTypeProduit(null)` ;
- le re-parenting retire l'enfant de l'ancien parent ;
- les doublons par identité ne doivent pas être ajoutés ;
- les méthodes internes ne doivent pas appeler `contains()` ou `remove(Object)` lorsqu'elles doivent éviter `equals(...)` sous verrou.

## 8) Collections

`getSousTypeProduits()` expose un snapshot et non la liste interne mutable partagée.

Règles :

- ne jamais retourner directement la collection interne ;
- conserver une collection interne exploitable dès la construction ;
- conserver la visibilité par interface.

## 9) Clonage

`clone()` produit un clonage profond via `CloneContext`.

Règles :

- `cloneWithoutChildren()` copie `idTypeProduit` et `typeProduit`, sans enfants ;
- `deepClone(CloneContext)` réutilise un clone déjà présent dans le contexte ;
- le clone profond clone chaque enfant et le rattache au clone parent ;
- le clonage ne doit pas créer de cycle infini ;
- le clonage doit rester thread-safe.

## 10) CSV / JTable

- `getEnTeteCsv()` retourne exactement `idTypeProduit;type de produit;` ;
- `toStringCsv()` respecte cet ordre et utilise `null` textuel quand une valeur est absente ;
- `getEnTeteColonne(0)` retourne `idTypeProduit` ;
- `getEnTeteColonne(1)` retourne `type de produit` ;
- tout autre indice retourne `invalide` ;
- `getValeurColonne(0)` retourne l'identifiant en `String` ou `null` ;
- `getValeurColonne(1)` retourne le libellé ou `null` ;
- tout autre indice retourne `invalide`.

## 11) Tests de référence

`TypeProduitTest.java` contient 38 tests validés couvrant notamment :

- constructeurs ;
- `equals` / `hashCode` ;
- insensibilité à la casse ;
- thread-safety ;
- `toString` ;
- `compareTo` ;
- clonage ;
- rattachement / détachement ;
- méthodes internes d'ajout/retrait ;
- CSV ;
- JTable.

## 12) Interdictions

- ne jamais faire participer les enfants ou l'identifiant à l'égalité métier ;
- ne jamais casser la bidirectionnalité `TypeProduit <-> SousTypeProduit` ;
- ne jamais exposer la liste interne ;
- ne jamais supprimer les verrous déterministes ;
- ne jamais transformer le clonage profond en copie superficielle.
