<!-- README.md -->
# PatrimoineSSP

Application Java de gestion de patrimoine structurée selon une architecture hexagonale et gouvernée par un workflow technique strict orienté SHA-only.

## Stack technique

- Java 21
- Maven
- Spring Boot
- GitHub Actions
- JUnit 5
- Mockito

## Gouvernance technique

Le projet est piloté selon une hiérarchie de vérité explicite :

1. baseline consolidée
2. GitHub au SHA fourni
3. bundle OFFLINE validé

Le fonctionnement de l’IA et le périmètre technique du projet sont gouvernés par :

- `docs/ai/CONTRAT_IA.md`
- `docs/ai/MANIFEST_IA.yaml`
- `docs/ai/perimetre.yaml`

## Architecture canonique

Le projet est organisé selon les couches canoniques suivantes :

1. `couche_ia`
2. `couche_configuration_tests`
3. `couche_metier`
4. `couche_dto`
5. `couche_persistance`
6. `couche_services`
7. `couche_controllers`
8. `couche_vues`

### Détail synthétique

- `couche_ia` : gouvernance IA, découverte du périmètre, outils de lecture et de bundle offline
- `couche_configuration_tests` : racine du dépôt, build Maven, CI, script des couches validées
- `couche_metier` : objets métier, utilitaires métier et tests associés
- `couche_dto` : DTO, convertisseurs DTO et tests associés
- `couche_persistance` : entités JPA, convertisseurs JPA ↔ métier, DAO JPA et tests associés
- `couche_services` :
  - `couche_services.gateway`
  - `couche_services.uc`
- `couche_controllers` : couche réservée / en construction
- `couche_vues` : couche réservée / en construction

## CI

La CI GitHub Actions est configurée sur :

### Push

- `main`
- `develop`
- `feature/uc`

### Pull request

- `main`
- `develop`

Le workflow CI exécute le script canonique suivant :

- `scripts/test_couches_validees.sh`

Ce script a vocation à lancer les tests des couches déjà validées et vertes, puis à intégrer plus tard les tests des controllers et des vues lorsqu’ils seront créés et validés.

## État du projet

Le projet est en construction progressive, avec sacralisation couche par couche.

À ce stade, les couches suivantes sont déjà structurées et gouvernées explicitement :

- couche IA
- configuration / build / CI
- métier
- DTO
- persistance
- services gateway
- services UC

Les couches controllers et vues restent en cours de construction.

## Objectif du dépôt

Fournir un projet Java hexagonal :

- lisible
- testable
- traçable
- reproductible
- auditable au SHA

avec un workflow permettant de relire, comparer, consolider et corriger le projet de manière strictement déterministe.