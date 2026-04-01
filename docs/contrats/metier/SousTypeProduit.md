# docs/contrats/metier/SousTypeProduit.md

# Contrat local métier — SousTypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.SousTypeProduit`
- interface liée : `levy.daniel.application.model.metier.produittype.SousTypeProduitI`

## 2) Objet du contrat

Décrire le comportement métier réel attendu et observable de l’objet `SousTypeProduit`.

Ce contrat décrit :
- le rôle métier de l’objet ;
- les invariants de structure ;
- ses relations avec `TypeProduit` et `Produit` ;
- les règles d’égalité, de comparaison, de validité et de clonage ;
- les garanties sur les collections et la concurrence ;
- les tests JUnit qui verrouillent ces comportements.

## 3) Rôle métier

`SousTypeProduit` modélise un sous-type métier rattaché à un `TypeProduit` et qualifiant une collection de `Produit`.

Exemples :
- `vêtement pour homme` pour le type `vêtement` ;
- `application web` pour le type `logiciel`.

Un `SousTypeProduit` porte :
- un identifiant persistant éventuel `idSousTypeProduit` ;
- un libellé métier `sousTypeProduit` ;
- un parent `TypeProduit` ;
- une collection de `Produit` enfants ;
- un indicateur métier `valide` recalculé selon l’état courant.

## 4) Source de vérité

Ordre d’interprétation :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/metier/SousTypeProduit.md`
3. `SousTypeProduit.java`
4. `SousTypeProduitTest.java`
5. `TypeProduitSousTypeProduitIntegrationTest.java`
6. `MetierGlobalConformiteTest.java`

## 5) Invariants métier attendus

- un `SousTypeProduit` peut exister sans identifiant persistant ;
- son parent `TypeProduit` peut être absent ou non encore finalisé selon le scénario ;
- la cohérence bidirectionnelle doit être maintenue avec le parent `TypeProduit` ;
- la cohérence bidirectionnelle doit être maintenue avec les enfants `Produit` ;
- l’état `valide` doit refléter l’état métier courant après recalcul.

## 6) Constructeurs et état initial

Les constructeurs doivent permettre :
- la création tolérante ;
- les variantes avec ou sans parent ;
- les variantes avec ou sans liste de produits ;
- un état interne cohérent dès l’instanciation.

Le détail du comportement nominal et des cas mixtes est verrouillé par `SousTypeProduitTest`.

## 7) Égalité, hashCode et comparaison

Le contrat attendu est :
- respect du contrat Java ;
- insensibilité à la casse sur le libellé métier ;
- robustesse multi-thread ;
- cohérence entre `equals(...)`, `hashCode()` et `compareTo(...)`.

## 8) Gestion du parent et des produits enfants

### 8.1 Parent `TypeProduit`

Le parent doit rester cohérent avec le `SousTypeProduit` courant.

Toute modification du parent doit préserver :
- la relation bidirectionnelle ;
- la cohérence des produits enfants ;
- l’absence de rattachements contradictoires.

### 8.2 Produits enfants

Le contrat attendu est :
- ajout/retrait cohérent des `Produit` ;
- prise en charge des cas limites ;
- absence de doublon par identité lorsque le code le prévoit ;
- collection cohérente et exploitable ;
- thread-safety des opérations de gestion.

## 9) Validité métier

`SousTypeProduit` expose un état `valide`.

Le contrat attendu est :
- recalcul correct via `recalculerValide()` ;
- cohérence avec le parent et la collection de produits ;
- lecture thread-safe de `isValide()`.

## 10) Clonage

Le contrat attendu est :
- support du clonage Java ;
- support du clonage profond avec `CloneContext` ;
- support du clonage sans parent ni enfants ;
- préservation de la cohérence métier ;
- sûreté concurrente.

## 11) Export / affichage

Le contrat attendu couvre :
- `toString()` ;
- `afficherSousTypeProduit()` ;
- `getEnTeteCsv()` ;
- `toStringCsv()` ;
- `getEnTeteColonne(...)` ;
- `getValeurColonne(...)`.

Ces sorties doivent rester stables, déterministes et testées.

## 12) Thread-safety

`SousTypeProduit` est verrouillé par des tests dédiés sur :
- `equals(...)` / `hashCode()` ;
- `toString()` ;
- `compareTo(...)` ;
- clonage profond ;
- gestion des produits enfants ;
- exposition et remplacement des collections ;
- validité métier.

Aucune régression concurrente n’est acceptable.

## 13) Tests de référence

Tests principaux à relire avant correction :
- `SousTypeProduitTest.java`
- `TypeProduitSousTypeProduitIntegrationTest.java`
- `MetierGlobalConformiteTest.java`
- `CloneContextTest.java` si le scénario touche au clonage profond

## 14) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `SousTypeProduit`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/metier/SousTypeProduit.md`
3. `SousTypeProduit.java`
4. `TypeProduit.java` si le parent est concerné
5. `Produit.java` si la gestion des enfants est concernée
6. `SousTypeProduitTest.java`
7. `TypeProduitSousTypeProduitIntegrationTest.java`
8. `MetierGlobalConformiteTest.java` si le scénario touche à la cohérence globale

## 15) Interdictions absolues

- ne jamais casser la cohérence bidirectionnelle avec `TypeProduit` ;
- ne jamais casser la cohérence bidirectionnelle avec `Produit` ;
- ne jamais exposer les collections internes comme listes mutables partagées ;
- ne jamais dégrader le recalcul de `valide` ;
- ne jamais modifier les règles d’égalité, de comparaison ou de clonage sans ajuster les tests de référence.