# docs/contrats/metier/Produit.md

# Contrat local métier — Produit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.Produit`
- interface liée : `levy.daniel.application.model.metier.produittype.ProduitI`

## 2) Objet du contrat

Décrire le comportement métier réel attendu et observable de l’objet `Produit`.

Ce contrat décrit :
- le rôle métier de l’objet ;
- les invariants de structure ;
- sa relation avec `SousTypeProduit` et `TypeProduit` ;
- les règles d’égalité, de comparaison, de validité et de clonage ;
- les garanties de thread-safety ;
- les tests JUnit qui verrouillent ces comportements.

## 3) Rôle métier

`Produit` modélise l’objet métier final qualifié par un `SousTypeProduit`, lui-même rattaché à un `TypeProduit`.

Exemples :
- `chemise manches longues pour homme` ;
- `application mobile bancaire`.

Un `Produit` porte :
- un identifiant persistant éventuel `idProduit` ;
- un libellé métier `produit` ;
- un parent `SousTypeProduit` ;
- un `TypeProduit` accessible indirectement via son parent ;
- un indicateur métier `valide` recalculé selon l’état courant.

## 4) Source de vérité

Ordre d’interprétation :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/metier/Produit.md`
3. `Produit.java`
4. `ProduitTest.java`
5. `MetierGlobalConformiteTest.java`
6. `TypeProduitSousTypeProduitIntegrationTest.java` lorsque la relation métier complète est concernée

## 5) Invariants métier attendus

- un `Produit` peut exister sans identifiant persistant ;
- un `Produit` peut exister sans parent finalisé selon le scénario ;
- la cohérence avec `SousTypeProduit` doit être maintenue ;
- l’accès indirect à `TypeProduit` doit rester cohérent via le parent ;
- l’état `valide` doit refléter l’état métier courant après recalcul.

## 6) Constructeurs et état initial

Les constructeurs doivent permettre :
- la création tolérante ;
- la création avec libellé ;
- la création avec parent ;
- un état interne cohérent dès l’instanciation.

Le comportement exact attendu est verrouillé par `ProduitTest`.

## 7) Égalité, hashCode et comparaison

Le contrat attendu est :
- respect du contrat Java ;
- comparaison métier insensible à la casse sur le libellé ;
- robustesse multi-thread ;
- cohérence entre `equals(...)`, `hashCode()` et `compareTo(...)`.

## 8) Relation avec le parent

Le parent `SousTypeProduit` doit rester cohérent avec le `Produit` courant.

Le contrat attendu est :
- rattachement cohérent au parent ;
- exposition cohérente du parent ;
- exposition cohérente du `TypeProduit` via le parent ;
- absence de deadlock sur les scénarios concurrents `SousTypeProduit <-> Produit`.

## 9) Validité métier

`Produit` expose un état `valide`.

Le contrat attendu est :
- recalcul correct via `recalculerValide()` ;
- lecture cohérente de `isValide()` ;
- stabilité concurrente.

## 10) Clonage

Le contrat attendu est :
- support du clonage Java ;
- support du clonage profond avec `CloneContext` ;
- support du clonage sans parent ;
- préservation de la cohérence métier ;
- sûreté concurrente.

## 11) Export / affichage

Le contrat attendu couvre :
- `toString()` ;
- `afficherProduit()` ;
- `getEnTeteCsv()` ;
- `toStringCsv()` ;
- `getEnTeteColonne(...)` ;
- `getValeurColonne(...)`.

Ces méthodes doivent produire une sortie stable, déterministe et testée.

## 12) Thread-safety

`Produit` est verrouillé par des tests dédiés sur :
- `equals(...)` / `hashCode()` ;
- `toString()` ;
- `compareTo(...)` ;
- clonage ;
- recalcul de validité ;
- getters / setters ;
- interactions concurrentes avec `SousTypeProduit`.

Toute correction future doit préserver ces garanties.

## 13) Tests de référence

Tests principaux à relire avant correction :
- `ProduitTest.java`
- `MetierGlobalConformiteTest.java`
- `TypeProduitSousTypeProduitIntegrationTest.java` lorsque la hiérarchie complète est concernée
- `CloneContextTest.java` si le scénario touche au clonage profond

## 14) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `Produit`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/metier/Produit.md`
3. `Produit.java`
4. `SousTypeProduit.java` si le parent est concerné
5. `TypeProduit.java` si l’accès indirect au type parent est concerné
6. `ProduitTest.java`
7. `MetierGlobalConformiteTest.java`
8. `TypeProduitSousTypeProduitIntegrationTest.java` si la cohérence globale est concernée

## 15) Interdictions absolues

- ne jamais casser la cohérence avec `SousTypeProduit` ;
- ne jamais casser l’accès cohérent au `TypeProduit` via le parent ;
- ne jamais dégrader les garanties de thread-safety déjà verrouillées ;
- ne jamais modifier les règles d’égalité, de comparaison ou de clonage sans ajuster les tests de référence.