# docs/contrats/metier/TypeProduit.md

# Contrat local métier — TypeProduit

## 1) Classe concernée

- `levy.daniel.application.model.metier.produittype.TypeProduit`
- interface liée : `levy.daniel.application.model.metier.produittype.TypeProduitI`

## 2) Objet du contrat

Décrire le comportement métier réel attendu et observable de l’objet `TypeProduit`.

Ce contrat décrit :
- le rôle métier de l’objet ;
- les invariants de structure ;
- les relations avec `SousTypeProduit` ;
- les règles d’égalité, de comparaison et de clonage ;
- les garanties de collections, de mutabilité et de thread-safety ;
- les tests JUnit qui verrouillent ces comportements.

## 3) Rôle métier

`TypeProduit` modélise la racine métier d’une hiérarchie de produits typés.

Exemples :
- `vêtement`
- `outillage`
- `logiciel`

Un `TypeProduit` porte :
- un identifiant persistant éventuel `idTypeProduit` ;
- un libellé métier `typeProduit` ;
- une collection de `SousTypeProduit` enfants.

## 4) Source de vérité

Ordre d’interprétation :
1. `docs/ai/CONTRAT_IA.md`
2. `docs/ai/perimetre.yaml`
3. le présent contrat `docs/contrats/metier/TypeProduit.md`
4. `TypeProduit.java`
5. `TypeProduitTest.java`
6. `TypeProduitSousTypeProduitIntegrationTest.java`
7. `MetierGlobalConformiteTest.java`

En cas d’ambiguïté :
- priorité au comportement réellement verrouillé par les tests ;
- puis au présent contrat ;
- puis au code.

## 5) Invariants métier attendus

- un `TypeProduit` peut exister sans identifiant persistant ;
- un `TypeProduit` peut exister sans sous-types ;
- la liste des sous-types appartient au `TypeProduit` courant ;
- un `SousTypeProduit` enfant doit rester cohérent avec son parent ;
- la couche métier doit maintenir la cohérence bidirectionnelle `TypeProduit <-> SousTypeProduit`.

## 6) Constructeurs et état initial

Le constructeur d’arité nulle et le constructeur avec libellé doivent permettre :
- une création tolérante ;
- un état initial cohérent ;
- une collection interne de sous-types immédiatement exploitable.

Le comportement précis est verrouillé par `TypeProduitTest`.

## 7) Égalité, hashCode et comparaison

### 7.1 `equals(...)` / `hashCode()`

Le contrat métier attendu est :
- respect du contrat Java ;
- comparaison métier insensible à la casse sur le libellé ;
- robustesse multi-thread ;
- cohérence entre `equals(...)` et `hashCode()`.

### 7.2 `compareTo(...)`

Le contrat métier attendu est :
- respect du contrat Java de comparaison ;
- comparaison métier insensible à la casse ;
- absence de deadlock ;
- comportement déterministe en environnement concurrent.

## 8) Relation parent / enfants

### 8.1 Gestion des sous-types

`TypeProduit` est responsable de la cohérence de rattachement des enfants `SousTypeProduit`.

Le contrat attendu est :
- rattacher correctement un enfant valide ;
- ignorer ou traiter proprement les cas limites (`null`, doublon, mauvaise instance) ;
- permettre le re-parenting cohérent ;
- détacher proprement un enfant ;
- conserver une cohérence bidirectionnelle durable.

### 8.2 Collections exposées

Le getter de la collection enfants doit exposer un **snapshot immuable** cohérent.

La mutation interne doit rester maîtrisée par l’objet métier.

## 9) Clonage

Le contrat attendu est :
- support du clonage Java ;
- support du clonage profond ;
- support du clonage sans enfants ;
- conservation de la cohérence métier ;
- thread-safety ;
- absence de cycle infini grâce à `CloneContext`.

## 10) Export / affichage

Le contrat attendu couvre :
- `toString()` ;
- `afficherTypeProduit()` ;
- `afficherSousTypeProduits()` ;
- `getEnTeteCsv()` ;
- `toStringCsv()` ;
- `getEnTeteColonne(...)` ;
- `getValeurColonne(...)`.

Ces méthodes doivent produire une représentation stable, cohérente et testée.

## 11) Thread-safety

La classe `TypeProduit` est verrouillée par des tests dédiés sur :
- `equals(...)` / `hashCode()` ;
- `toString()` ;
- `compareTo(...)` ;
- clonage ;
- rattachement / détachement d’enfants ;
- exposition des collections.

Toute correction future doit préserver cette sûreté concurrente.

## 12) Tests de référence

Tests principaux à relire avant correction :
- `TypeProduitTest.java`
- `TypeProduitSousTypeProduitIntegrationTest.java`
- `MetierGlobalConformiteTest.java`
- `CloneContextTest.java` lorsque le scénario touche au clonage profond

## 13) Règle de lecture obligatoire avant toute action

Avant toute analyse, correction ou génération concernant `TypeProduit`, l’IA doit relire :
1. `docs/ai/CONTRAT_IA.md`
2. le présent contrat `docs/contrats/metier/TypeProduit.md`
3. `TypeProduit.java`
4. `SousTypeProduit.java` si la relation parent/enfant est concernée
5. `TypeProduitTest.java`
6. `TypeProduitSousTypeProduitIntegrationTest.java`
7. `MetierGlobalConformiteTest.java` si le scénario touche à la cohérence globale

## 14) Interdictions absolues

- ne jamais casser la cohérence bidirectionnelle `TypeProduit <-> SousTypeProduit` ;
- ne jamais exposer la collection enfants comme liste mutable partagée ;
- ne jamais dégrader les garanties de thread-safety déjà verrouillées ;
- ne jamais modifier les règles d’égalité, de comparaison ou de clonage sans ajuster les tests de référence.