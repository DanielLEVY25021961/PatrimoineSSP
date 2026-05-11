# Contrat local de couche - Couche services UC

## 1) Intention de la couche

La sous-couche `couche_services.uc` porte les services métier de cas d'usage du domaine `produittype`.

Elle expose :
- les PORTS UC métier ;
- les ADAPTERS UC métier ;
- les exceptions applicatives de SERVICE UC ;
- les tests Mock UC ;
- les tests d'intégration UC ;
- les contrats locaux nécessaires à la relecture de la sous-couche.

Cette sous-couche appartient à `couche_services`.

## 2) Frontière architecturale

La sous-couche `couche_services.uc` :
- manipule des `InputDTO` et `OutputDTO` lorsque le scénario l'exige ;
- est le point d'entrée dans la logique métier dialoguant directement avec le controller appelant ;
- produit le message observable côté appelant via `getMessage()` ;
- ne manipule pas directement les entités JPA ;
- ne porte pas la logique Controller ;
- ne porte pas la logique View.

Les DTO relèvent de `couche_dto`.
Les Gateways relèvent de `couche_services.gateway`.
Les entités JPA relèvent de `couche_persistance`.

## 3) Fichiers inclus dans le périmètre sacralisé

### 3.1) PORTS UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/cu/ProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/SousTypeProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/TypeProduitICuService.java`

### 3.2) ADAPTERS UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuService.java`

### 3.3) Exceptions services UC métier

- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionDoublon.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionNonPersistant.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionStockageVide.java`

### 3.4) Tests UC métier

- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceMockTest.java`

### 3.5) Contrats locaux de la sous-couche

- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`

## 4) Règles de cohérence obligatoires

### 4.1) Règle DTO

Les méthodes UC manipulent les DTO applicatifs quand le contrat de service l'exige.
Le service UC ne doit pas dériver vers des entités JPA ou des structures de persistance.

### 4.2) Règle Gateway

Le service UC ne réalise pas lui-même les opérations techniques de stockage.
Il délègue ces opérations à `couche_services.gateway`.

### 4.3) Règle message observable

Le service UC est responsable du message observable côté appelant.
Le `GATEWAY` ne doit jamais produire ce message utilisateur.

### 4.4) Règle exceptionsservices

Les exceptions de `exceptionsservices` appartiennent à `couche_services.uc`.
Elles ne doivent pas être rattachées à `couche_services.gateway`.

### 4.5) Règle de preuve

Les tests Mock verrouillent le contrat observable et la délégation.
Les tests d'intégration prouvent le comportement UC réel, le message final et, lorsque pertinent, la preuve dans le stockage.

### 4.6) Règle de contrats locaux

Les contrats locaux de cette sous-couche servent de pivots de relecture avant toute analyse, tout diagnostic ou toute génération de code portant sur UC.

## 5) Définition de la sacralisation

La sous-couche `couche_services.uc` est considérée sacralisée lorsque :
- le présent contrat local est présent ;
- le périmètre IA référence exactement les fichiers de cette sous-couche ;
- les exceptions `exceptionsservices` sont rattachées à UC et non à Gateway ;
- les tests Mock et d'intégration UC sont dans le périmètre validé ;
- la séparation avec `couche_services.gateway` est explicite ;
- la séparation avec `couche_dto`, `couche_persistance`, `couche_controllers` et `couche_vues` est explicite.

## 6) Exclusions explicites

Ne font pas partie de `couche_services.uc` :
- les Gateways ;
- les exceptions de `exceptionsgateway` ;
- les entités JPA et convertisseurs de persistance ;
- les controllers ;
- les vues.

## 7) Référence Gateway validée pour le formalisme des tests UC

Le package Gateway validé `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl` sert de référence de formalisme pour professionnaliser les tests UC.

Cette référence est utile pour :
- la Javadoc de tête des classes de test ;
- les constantes, tags, display names et commentaires didactiques ;
- l'organisation par blocs correspondant aux méthodes du PORT ;
- l'ordre des cas dans un bloc : exceptions, cas alternatifs, cas nominaux ;
- la distinction entre tests contractuels et tests didactiques non contractuels ;
- l'espacement inter-tests validé par l'utilisateur : 3 lignes entre méthodes ou blocs de tests ;
- le vocabulaire stable : `stockage`, `objet métier`, `parent`, `DTO`, `SERVICE METIER UC`, `PORT UC`, `ADAPTER UC`.

Cette référence Gateway ne doit pas être copiée mécaniquement.
Les tests UC doivent toujours partir du contrat du PORT UC et du comportement réel de l'ADAPTER UC.

## 8) Définition métier simple du SERVICE METIER UC

Le SERVICE METIER UC est le point d'entrée dans la logique métier dialoguant directement avec le controller appelant.

Conséquences :
- le controller appelant dialogue avec le SERVICE METIER UC ;
- le SERVICE METIER UC dialogue avec les Gateways lorsque l'accès au stockage est nécessaire ;
- le SERVICE METIER UC manipule les DTO lorsque le contrat UC l'exige ;
- le SERVICE METIER UC produit le message observable côté appelant via `getMessage()` ;
- le SERVICE METIER UC ne doit pas dériver vers la logique Controller, View ou persistance JPA directe.

La formule `orchestration applicative observable` est interdite dans les règles UC : elle est trop vague et ne doit pas être utilisée.

## 9) RT-LECTURE-TESTS-UC-01 — Ordre de lecture obligatoire

Avant toute analyse, correction, validation ou génération de code portant sur un test UC, l'IA doit lire strictement, dans l'ordre :

1. le présent contrat local `CoucheServicesUC.md` ;
2. le contrat local du PORT UC ciblé : `TypeProduitICuService.md`, `SousTypeProduitICuService.md` ou `ProduitICuService.md` ;
3. le PORT Java UC ciblé ;
4. l'ADAPTER UC réel ciblé ;
5. les DTO utilisés par la méthode ;
6. les convertisseurs DTO utiles ;
7. les exceptions `exceptionsservices` utiles ;
8. le PORT Gateway appelé ;
9. l'ADAPTER Gateway réel si le comportement UC dépend d'un comportement Gateway réel ;
10. les objets métier utiles ;
11. les tests Gateway validés, uniquement comme référence de formalisme ;
12. le test UC ciblé.

Si une dépendance utile n'a pas été relue, l'IA doit déclarer la lecture incomplète et la compléter avant toute conclusion.

## 10) RT-CONTROLE-BLOC-TEST-UC-01 — Travail méthode par méthode

Les tests UC se contrôlent méthode du PORT UC par méthode du PORT UC.

Pour chaque méthode UC, l'IA doit produire ou vérifier une matrice :

`cas contractuel du PORT UC -> méthode de test attendue -> preuve attendue -> verdict`

La matrice doit distinguer :
- les cas contractuels obligatoires ;
- les cas didactiques non contractuels ;
- les cas déjà prouvés par les tests Gateway et seulement repris au niveau UC si le contrat UC le justifie.

L'IA ne doit pas inventer de nouveaux critères à chaque passe.
Les critères de contrôle d'un bloc UC doivent être déduits du contrat UC lu, du comportement réel de l'ADAPTER UC et des règles du présent document.

## 11) RT-FORMALISME-TESTS-UC-01 — Formalisme obligatoire

Les tests UC doivent reprendre le formalisme validé des tests Gateway lorsque ce formalisme est utile :

- Javadoc de tête complète et didactique ;
- constantes centralisées pour tags, display names, messages, libellés et valeurs techniques ;
- blocs visibles par méthode du PORT ;
- Javadoc HTML par méthode de test ;
- commentaires internes didactiques `ARRANGE`, `ACT`, `ASSERT` ou équivalents ;
- nommage simple : `test` + méthode sous test + garantie principale ;
- pas de snippet, pas d'ellipse, pas de méthode tronquée ;
- espacement inter-tests de 3 lignes selon le formalisme utilisateur ;
- vocabulaire stable et concret.

Avant de créer un nouveau commentaire, l'IA doit vérifier s'il existe déjà un commentaire exploitable dans une méthode validée de la même classe ou d'une classe de référence.
L'IA ne doit pas réinventer un commentaire différent pour chaque méthode lorsque le même modèle didactique est déjà validé.

## 12) RT-CODAGE-TEST-MOCKITO-UC-01 — Tests Mock UC

Un test Mock UC prouve le comportement du SERVICE METIER UC côté appelant, sans prouver directement le stockage.

Il doit vérifier selon le cas :
- la validation des paramètres d'entrée ;
- les DTO d'entrée et de sortie ;
- les conversions DTO vers objet métier ou objet métier vers DTO ;
- l'exception `exceptionsservices` exacte ;
- le message final observable via `getMessage()` ;
- la délégation exacte au Gateway ;
- l'absence d'interaction avec le Gateway lorsque le contrat l'impose ;
- le comportement lorsque le Gateway retourne `null`, une liste vide, un résultat nominal ou jette une exception ;
- la règle du dernier message observable lorsque la méthode enchaîne plusieurs opérations.

Un test Mock UC ne doit pas utiliser le DAO ni prouver directement le stockage.
Le Gateway est un collaborateur mocké : les interactions avec lui sont la preuve principale.

### 12.1) RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01 — Application locale UC

Cette règle est l'application locale UC de la règle Mockito transverse définie dans `CONTRAT_IA.md`.
Elle ne limite pas la règle aux UC : le même principe s'applique à tout test Mockito du projet.

Dans un test Mock UC, chaque stubbing Mockito doit être strictement justifié par le chemin réellement exécuté par le scénario.

Sont concernés :
- `when(...)` ;
- `doThrow(...)` ;
- `doReturn(...)` ;
- tout comportement configuré sur un mock.

Règle obligatoire :
- relire la méthode UC testée et l'ordre réel des appels avant de configurer les mocks ;
- identifier le point exact où le scénario s'arrête : exception attendue, retour anticipé, branche nominale ou interaction vérifiée ;
- ne stubber que les appels consommés avant ce point ;
- supprimer tout stubbing non consommé avant livraison.

Interdictions :
- ne jamais stubber un mock mécaniquement pour le rendre « complet » ;
- ne jamais ajouter un getter mocké si le scénario échoue avant sa lecture ;
- ne jamais conserver un stubbing préventif, décoratif ou supposé utile ;
- ne jamais ignorer un risque de `UnnecessaryStubbingException`.

Exemple validé dans `SousTypeProduitCuServiceMockTest.java` : dans les tests de conversion `OutputDTO` de `findAllByParent(...)`, si l'exception est déclenchée par `sousTypeProduit.getTypeProduit()`, il ne faut pas stubber `sousTypeProduit.getSousTypeProduit()` si ce getter n'est pas consommé avant l'exception.

Cette règle complète les règles de commentaires alignés : un commentaire ne doit jamais annoncer une préparation ou une configuration qui ne correspond pas à un appel réellement utilisé par le test.

## 13) RT-CODAGE-TEST-INTEGRATION-UC-01 — Tests d'intégration UC

Un test d'intégration UC prouve le comportement réel du SERVICE METIER UC avec ses collaborateurs réels utiles au scénario.

Il doit vérifier selon le cas :
- le message final observable via `getMessage()` ;
- les DTO retournés ;
- les exceptions `exceptionsservices` attendues ;
- la délégation fonctionnelle réelle vers les Gateways, sans refaire inutilement toutes les preuves techniques des tests Gateway Mock ;
- l'effet réel dans le stockage lorsque la méthode écrit ou supprime ;
- l'absence d'effet de bord pour les lectures ;
- la cohérence `count()`, `rechercherTous()`, pagination, parent/enfant lorsque le contrat UC le rend pertinent.

En intégration UC, la preuve dans le stockage est autorisée et utile lorsqu'elle sert le contrat UC.
Elle ne doit pas transformer le test UC en test DAO ou en recontrôle exhaustif du Gateway.

## 14) RT-VOCABULAIRE-UC-01 — Vocabulaire stable

Utiliser :
- `SERVICE METIER UC` ;
- `service UC` ;
- `PORT UC` ;
- `ADAPTER UC` ;
- `DTO` ;
- `controller appelant` ;
- `message observable côté appelant` ;
- `getMessage()` ;
- `exceptionsservices` ;
- `délégation au Gateway` ;
- `stockage` ;
- `preuve dans le stockage`.

Éviter :
- `orchestration applicative observable` ;
- `base` ;
- `BDD` ;
- `persistance directe côté UC` ;
- `DAO dans un test Mock UC` ;
- `réécriture du contrat Gateway dans le test UC`.

## 15) RT-FENETRE-AUDIT-UC-PRODUIT-COMPLET-01 — Fenêtre d'audit UC

La fenêtre `Fenêtre audit_uc_produit_complet` est le périmètre de travail destiné à auditer et corriger les tests UC `produittype` sans relire GitHub à chaque passe, tant que le SHA courant et le périmètre ne changent pas.

Elle doit inclure au minimum :
- le présent contrat local UC ;
- les contrats locaux des PORTS UC ;
- les PORTS UC Java ;
- les ADAPTERS UC ;
- les exceptions `exceptionsservices` ;
- les DTO et convertisseurs DTO utiles ;
- les objets métier utiles ;
- les PORTS Gateway appelés ;
- les ADAPTERS Gateway lorsque nécessaires à l'intégration ;
- les tests UC Mock et intégration ;
- les tests Gateway validés comme référence de formalisme ;
- les scripts et ressources de test utiles à l'intégration.

Une fois installée et activée depuis la baseline consolidée, cette fenêtre devient la source de travail locale pour les audits UC jusqu'à nouveau SHA, nouveau périmètre ou demande explicite de relecture GitHub.
## 16) RT-FORMALISME-TYPEPRODUIT-CU-MOCK-REFERENCE-02 — Référence autonome TypeProduitCuServiceMockTest

`TypeProduitCuServiceMockTest.java` corrigé au dernier SHA courant fourni par l'utilisateur est la référence complète de formalisme Mockito UC pour le SERVICE METIER UC `TypeProduitCuService`.

Cette règle locale complète `RT-FORMALISME-TESTS-UC-01` et `RT-CODAGE-TEST-MOCKITO-UC-01`.

### 16.1 Matrice obligatoire

L'IA doit savoir recoder la classe complète avec `101` tests répartis en `12` blocs, dans l'ordre suivant :

| Bloc | Nombre de tests |
|---|---:|
| `creer` | 11 |
| `rechercherTous` | 7 |
| `rechercherTousString` | 8 |
| `rechercherTousParPage` | 8 |
| `findByLibelle` | 8 |
| `findByLibelleRapide` | 9 |
| `findByDTO` | 9 |
| `findById` | 7 |
| `update` | 14 |
| `delete` | 10 |
| `count` | 5 |
| `getMessage` | 5 |

Cette matrice est une spécification de formalisme pour cette classe. Toute modification de cette matrice nécessite une demande explicite de l'utilisateur.

### 16.2 Javadoc de tête et vocabulaire

La Javadoc de tête doit rappeler que la classe teste le SERVICE METIER UC `TypeProduitCuService`, point d'entrée dans la logique métier dialoguant directement avec le controller appelant, pour l'objet métier `TypeProduit` et le PORT `TypeProduitICuService`.

Elle doit aussi mentionner : validations locales, messages `getMessage()`, conversions DTO / objet métier / DTO, délégations Gateway, absence de délégation lorsque le SERVICE METIER UC bloque localement, propagation des exceptions techniques et rationalisation des messages observables.

Le vocabulaire obligatoire reste : `SERVICE METIER UC`, `objet métier`, `stockage`, `controller appelant`, `PORT UC`, `Gateway mocké`.

### 16.3 Tags, display names et annotations

La classe doit utiliser uniquement des tags dédiés par bloc : `TAG_CREER`, `TAG_RECHERCHER_TOUS`, `TAG_RECHERCHER_TOUS_STRING`, `TAG_RECHERCHER_TOUS_PAR_PAGE`, `TAG_FIND_BY_LIBELLE`, `TAG_FIND_BY_LIBELLE_RAPIDE`, `TAG_FIND_BY_DTO`, `TAG_FIND_BY_ID`, `TAG_UPDATE`, `TAG_DELETE`, `TAG_COUNT`, `TAG_GET_MESSAGE`.

La constante générique `TAG = "cu-mock"` ne doit pas être réintroduite.

Tous les display names doivent passer par des constantes `DISPLAY_NAME_*`. Les `@DisplayName("...")` inline sont interdits.

L'ordre d'annotations validé est :

```java
@Tag(...)
@DisplayName(...)
@Test
```

### 16.4 Commentaires alignés avec le code

Les commentaires internes doivent utiliser les libellés validés `ARRANGE`, `Configuration du Mock`, `ACT`, `ACT - ASSERT`, `ASSERT`.

Le commentaire doit annoncer exactement le code qui suit immédiatement.

Exemple correct :

```java
/* ARRANGE :
 * prépare un comptage Gateway cohérent indiquant
 * que plusieurs objets métier sont présents dans le stockage.
 */
final long comptageAttendu = 42L;
```

Le bloc standard de création du Gateway mocké et du service UC doit être repris tel quel lorsqu'il s'applique :

```java
/* 
 * Mocke un service Gateway et le passe 
 * à un service UC instancié dans le test. 
 */
final TypeProduitGatewayIService gateway 
	= mock(TypeProduitGatewayIService.class);
final TypeProduitCuService service 
	= new TypeProduitCuService(gateway);
```

L'IA ne doit ni le reformuler, ni le déplacer loin des lignes qu'il documente.

### 16.5 Ordre interne validé

L'ordre interne d'un test doit suivre la logique du scénario et des commentaires déjà validés : données du scénario, mock Gateway + service UC, configuration du Mock, action, assertions.

Il est interdit de réorganiser mécaniquement les tests en plaçant toujours le mock Gateway en premier lorsque les commentaires et les méthodes validées préparent d'abord les données du scénario.

### 16.6 Non-réinvention

Avant de coder ou corriger un bloc de `TypeProduitCuServiceMockTest.java`, l'IA doit relire les méthodes déjà validées de la classe et reprendre leur structure.

L'IA ne doit pas créer un nouveau commentaire ou une nouvelle organisation lorsqu'un modèle équivalent est déjà validé dans la classe.
