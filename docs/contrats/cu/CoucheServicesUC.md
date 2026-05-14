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

## 17) RT-AUTONOMIE-UC-PROGRESSIVE-01 — Sacralisation progressive des SERVICES METIER UC

La couche `couche_services.uc` est en cours de correction et de validation. L'IA ne doit donc pas déclarer la couche UC définitivement autonome avant validation finale par l'utilisateur.

En revanche, l'IA doit déjà appliquer une sacralisation progressive stricte pour éviter toute improvisation pendant les corrections UC.

Règles obligatoires :

1. partir du présent contrat local, du contrat local du PORT UC, du PORT Java, de l'ADAPTER UC, des Gateways appelés, des DTO, convertisseurs, exceptionsservices, objets métier et tests concernés ;
2. utiliser les tests Gateway validés comme référence de formalisme, sans les copier mécaniquement ;
3. utiliser `TypeProduitCuServiceMockTest.java` comme référence Mockito UC validée ;
4. classer toute correction par bloc correspondant à une méthode du PORT UC ;
5. respecter l'ordre des méthodes du PORT, l'ordre réel de l'ADAPTER et l'ordre des tests validés ;
6. ne jamais figer comme définitif un test UC non encore validé par l'utilisateur ;
7. après validation finale de la couche UC, reconfronter à nouveau les contrats IA UC à tous les fichiers validés.

Objectif actuel : permettre à l'IA d'anticiper et de corriger avec fiabilité, pas proclamer prématurément l'autonomie complète de la couche UC.

## 18) RT-ORDRE-BLOCS-UC-01 — Blocs PORT UC obligatoires

### `TypeProduitICuService`

Ordre des blocs :

1. `creer(...)`
2. `rechercherTous()`
3. `rechercherTousString()`
4. `rechercherTousParPage(...)`
5. `findByLibelle(...)`
6. `findByLibelleRapide(...)`
7. `findByDTO(...)`
8. `findById(...)`
9. `update(...)`
10. `delete(...)`
11. `count()`
12. `getMessage()`

### `SousTypeProduitICuService`

Ordre des blocs :

1. `creer(...)`
2. `rechercherTous()`
3. `rechercherTousString()`
4. `rechercherTousParPage(...)`
5. `findByLibelle(...)`
6. `findByLibelleRapide(...)`
7. `findAllByParent(...)`
8. `findByDTO(...)`
9. `findById(...)`
10. `update(...)`
11. `delete(...)`
12. `count()`
13. `getMessage()`

### `ProduitICuService`

Ordre des blocs :

1. `creer(...)`
2. `rechercherTous()`
3. `rechercherTousString()`
4. `rechercherTousParPage(...)`
5. `findByLibelle(...)`
6. `findByLibelleRapide(...)`
7. `findAllByParent(...)`
8. `findByDTO(...)`
9. `findById(...)`
10. `update(...)`
11. `delete(...)`
12. `count()`
13. `getMessage()`

L'ordre des tests Mock et intégration doit suivre ces blocs, sauf correction utilisateur explicitement validée.

## 19) RT-EXCEPTIONSSERVICES-UC-01 — Exceptions services à conserver

Les exceptions suivantes appartiennent à `couche_services.uc` et ne doivent pas être déplacées vers Gateway, persistance ou Controller.

| Exception | Héritage | Constructeurs validés |
|---|---|---|
| `ExceptionDoublon` | `Exception` | `public ExceptionDoublon()`<br>`public ExceptionDoublon(final String pMessage)` |
| `ExceptionNonPersistant` | `Exception` | `public ExceptionNonPersistant()`<br>`public ExceptionNonPersistant(final String pMessage)` |
| `ExceptionParametreBlank` | `Exception` | `public ExceptionParametreBlank()`<br>`public ExceptionParametreBlank(final String pMessage)` |
| `ExceptionParametreNull` | `Exception` | `public ExceptionParametreNull()`<br>`public ExceptionParametreNull(final String pMessage)` |
| `ExceptionStockageVide` | `Exception` | `public ExceptionStockageVide()`<br>`public ExceptionStockageVide(final String pMessage)` |

Toutes ces exceptions portent `private static final long serialVersionUID = 1L;`.

Règle de recodage : conserver les deux constructeurs, l'héritage, le `serialVersionUID`, la Javadoc historique et le rattachement au package `exceptionsservices`.

## 20) RT-ADAPTERS-UC-ORDRE-ET-HELPERS-01 — ADAPTERS UC à relire avant tout code

### `TypeProduitCuService`

#### Constructeur validé

- `public TypeProduitCuService( @Qualifier("TypeProduitGatewayJPAService") final TypeProduitGatewayIService pGateway)`

Règle : conserver cette injection telle que validée. Ne pas remplacer par une injection de champ, un constructeur vide ou une dépendance inventée.

#### Attributs structurants

- `private final TypeProduitGatewayIService gateway;`

#### Ordre exact des méthodes publiques validées

1. `public TypeProduitDTO.OutputDTO creer( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
2. `public List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception`
3. `public List<String> rechercherTousString() throws Exception`
4. `public ResultatPage<OutputDTO> rechercherTousParPage( final RequetePage pRequetePage) throws Exception`
5. `public OutputDTO findByLibelle(final String pLibelle) throws Exception`
6. `public List<OutputDTO> findByLibelleRapide( final String pContenu) throws Exception`
7. `public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception`
8. `public OutputDTO findById(final Long pId) throws Exception`
9. `public TypeProduitDTO.OutputDTO update( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
10. `public void delete(final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
11. `public long count() throws Exception`
12. `public String getMessage()`

#### Helpers privés / internes à ne pas oublier

- `private boolean isDoublon( final TypeProduitDTO.InputDTO pInputDTO) throws Exception`
- `private TypeProduit convertirInputDTOEnMetier( final TypeProduitDTO.InputDTO pInputDTO)`
- `private List<TypeProduit> filtrerEtTrier( final List<TypeProduit> pSource)`
- `private List<TypeProduitDTO.OutputDTO> convertirEtDedoublonner( final List<TypeProduit> pMetiers)`
- `private void alimenterMessageDepuisGateway()`
- `private long safeTotalElements( final ResultatPage<TypeProduit> pResultatPage)`
- `private <T> T traiterErreur( final String pMessage, final String pMethode, final Exception pE) throws Exception`
- `private boolean isErreurMetierAttendue( final String pMessage, final Exception pException)`

Ces helpers sont contractuels pour l'autonomie IA : ils ne doivent pas être supprimés, fusionnés ou remplacés par une version approximative sans relire le code validé et les tests concernés.

### `SousTypeProduitCuService`

#### Constructeur validé

- `public SousTypeProduitCuService( final SousTypeProduitGatewayIService pGateway, final TypeProduitGatewayIService pTypeProduitGateway)`

Règle : conserver cette injection telle que validée. Ne pas remplacer par une injection de champ, un constructeur vide ou une dépendance inventée.

#### Attributs structurants

- `private final TypeProduitGatewayIService typeProduitGateway;`
- `private final SousTypeProduitGatewayIService gateway;`

#### Ordre exact des méthodes publiques validées

1. `public SousTypeProduitDTO.OutputDTO creer( final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception`
2. `public List<SousTypeProduitDTO.OutputDTO> rechercherTous() throws Exception`
3. `public List<String> rechercherTousString() throws Exception`
4. `public ResultatPage<OutputDTO> rechercherTousParPage( final RequetePage pRequetePage) throws Exception`
5. `public List<OutputDTO> findByLibelle( final String pLibelle) throws Exception`
6. `public List<OutputDTO> findByLibelleRapide( final String pContenu) throws Exception`
7. `public List<SousTypeProduitDTO.OutputDTO> findAllByParent( final TypeProduitDTO.InputDTO pTypeProduit) throws Exception`
8. `public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception`
9. `public OutputDTO findById(final Long pId) throws Exception`
10. `public SousTypeProduitDTO.OutputDTO update( final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception`
11. `public void delete(final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception`
12. `public long count() throws Exception`
13. `public String getMessage()`

#### Helpers privés / internes à ne pas oublier

- `private boolean isDoublon( final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception`
- `private SousTypeProduit convertirInputDTOEnMetier( final SousTypeProduitDTO.InputDTO pInputDTO)`
- `private TypeProduit convertirInputDTOEnMetier( final TypeProduitDTO.InputDTO pInputDTO)`
- `private List<SousTypeProduit> filtrerEtTrier( final List<SousTypeProduit> pSource)`
- `private List<SousTypeProduitDTO.OutputDTO> convertirEtDedoublonner( final List<SousTypeProduit> pMetiers)`
- `private void alimenterMessageDepuisGateway()`
- `private long safeTotalElements( final ResultatPage<SousTypeProduit> pResultatPage)`
- `private <T> T traiterErreur( final String pMessage, final String pMethode, final Exception pE) throws Exception`
- `private boolean isErreurMetierAttendue( final String pMessage, final Exception pException)`

Ces helpers sont contractuels pour l'autonomie IA : ils ne doivent pas être supprimés, fusionnés ou remplacés par une version approximative sans relire le code validé et les tests concernés.

### `ProduitCuService`

#### Constructeur validé

- `public ProduitCuService( final ProduitGatewayIService pGateway, final SousTypeProduitGatewayIService pSousTypeProduitGateway)`

Règle : conserver cette injection telle que validée. Ne pas remplacer par une injection de champ, un constructeur vide ou une dépendance inventée.

#### Attributs structurants

- `private final ProduitGatewayIService gateway;`
- `private final SousTypeProduitGatewayIService sousTypeProduitGateway;`

#### Ordre exact des méthodes publiques validées

1. `public OutputDTO creer( final InputDTO pInputDTO) throws Exception`
2. `public List<OutputDTO> rechercherTous() throws Exception`
3. `public List<String> rechercherTousString() throws Exception`
4. `public ResultatPage<OutputDTO> rechercherTousParPage( final RequetePage pRequetePage) throws Exception`
5. `public List<ProduitDTO.OutputDTO> findByLibelle( final String pLibelle) throws Exception`
6. `public List<OutputDTO> findByLibelleRapide( final String pContenu) throws Exception`
7. `public List<OutputDTO> findAllByParent( final SousTypeProduitDTO.InputDTO pSousTypeProduit) throws Exception`
8. `public OutputDTO findByDTO( final InputDTO pInputDTO) throws Exception`
9. `public OutputDTO findById( final Long pId) throws Exception`
10. `public OutputDTO update( final InputDTO pInputDTO) throws Exception`
11. `public void delete( final InputDTO pInputDTO) throws Exception`
12. `public long count() throws Exception`
13. `public String getMessage()`
14. `public int compare(final Produit o1, final Produit o2)`

#### Helpers privés / internes à ne pas oublier

- `private boolean isDoublon( final ProduitDTO.InputDTO pInputDTO) throws Exception`
- `private SousTypeProduit rechercherParentPersistant( final ProduitDTO.InputDTO pInputDTO) throws Exception`
- `private Produit convertirInputDTOEnMetier( final ProduitDTO.InputDTO pInputDTO)`
- `private List<Produit> filtrerEtTrier( final List<Produit> pListe)`
- `private long safeTotalElements( final ResultatPage<?> rp)`
- `private String safeParentLibelle( final Produit pProduit)`
- `private <T> T traiterErreur( final String pMessage, final String pMethode, final Exception pE) throws Exception`
- `private boolean isErreurMetierAttendue( final String pMessage, final Exception pException)`

Ces helpers sont contractuels pour l'autonomie IA : ils ne doivent pas être supprimés, fusionnés ou remplacés par une version approximative sans relire le code validé et les tests concernés.

## 21) RT-FORMALISME-MOCK-UC-REFERENCE-GATEWAY-01 — Transposition contrôlée du formalisme Gateway

Les tests Gateway validés fournissent le formalisme : Javadoc de tête, blocs par méthode, commentaires didactiques, ordre des cas, preuve par interactions Mockito ou preuve dans le stockage selon le type de test.

Transposition UC obligatoire :

- un test Mock UC prouve le SERVICE METIER UC côté controller appelant, pas le stockage ;
- la preuve principale est le message observable via `getMessage()`, le DTO retourné ou l'exception attendue, et les interactions avec le Gateway mocké ;
- un test d'intégration UC prouve le comportement réel du SERVICE METIER UC, sans recontrôler exhaustivement les clauses techniques déjà prouvées dans Gateway ;
- la preuve dans le stockage est utilisée seulement lorsqu'elle sert le contrat UC ;
- le vocabulaire reste `SERVICE METIER UC`, `PORT UC`, `ADAPTER UC`, `Gateway`, `DTO`, `objet métier`, `parent`, `stockage`.

## 22) RT-TESTS-UC-MATRICE-PROGRESSIVE-01 — Matrice des tests UC actuellement observée

Cette matrice est un état de travail issu de la baseline consolidée au SHA courant. Elle ne transforme pas les tests UC non encore validés en vérité définitive, sauf `TypeProduitCuServiceMockTest.java` déjà déclaré référence stable.

#### `TypeProduitCuServiceMockTest.java` — 101 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 11 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerDoublon`<br>`testCreerControleDoublonKOAvecMessage`<br>`testCreerControleDoublonKOSansMessage`<br>`testCreerGatewayCreerKOAvecMessage`<br>`testCreerGatewayCreerKOSansMessage`<br>`testCreerGatewayCreerKORetourNull`<br>`testCreerConversionOutputDTOKOAvecMessage`<br>`testCreerConversionOutputDTOKOSansMessage`<br>`testCreerNominal` |
| `rechercherTous` | 7 | `testRechercherTousGatewayRetourNull`<br>`testRechercherTousGatewayKOAvecMessage`<br>`testRechercherTousGatewayKOSansMessage`<br>`testRechercherTousConversionOutputDTOKOAvecMessage`<br>`testRechercherTousConversionOutputDTOKOSansMessage`<br>`testRechercherTousVideApresFiltrage`<br>`testRechercherTousNominal` |
| `rechercherTousString` | 8 | `testRechercherTousStringGatewayRetourNull`<br>`testRechercherTousStringGatewayKOAvecMessage`<br>`testRechercherTousStringGatewayKOSansMessage`<br>`testRechercherTousStringConversionStringKOAvecMessage`<br>`testRechercherTousStringConversionStringKOSansMessage`<br>`testRechercherTousStringVideApresFiltrage`<br>`testRechercherTousStringVideApresLibellesBlank`<br>`testRechercherTousStringNominal` |
| `rechercherTousParPage` | 8 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageGatewayKOAvecMessage`<br>`testRechercherTousParPageGatewayKOSansMessage`<br>`testRechercherTousParPageGatewayRetourNull`<br>`testRechercherTousParPageConversionOutputDTOKOAvecMessage`<br>`testRechercherTousParPageConversionOutputDTOKOSansMessage`<br>`testRechercherTousParPageVideApresFiltrage`<br>`testRechercherTousParPageNominal` |
| `findByLibelle` | 8 | `testFindByLibelleNull`<br>`testFindByLibelleBlank`<br>`testFindByLibelleGatewayRetourNull`<br>`testFindByLibelleGatewayKOAvecMessage`<br>`testFindByLibelleGatewayKOSansMessage`<br>`testFindByLibelleConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleConversionOutputDTOKOSansMessage`<br>`testFindByLibelleNominal` |
| `findByLibelleRapide` | 9 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideGatewayKOAvecMessage`<br>`testFindByLibelleRapideGatewayKOSansMessage`<br>`testFindByLibelleRapideGatewayRetourNull`<br>`testFindByLibelleRapideConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleRapideConversionOutputDTOKOSansMessage`<br>`testFindByLibelleRapideVideApresFiltrage`<br>`testFindByLibelleRapideNominal` |
| `findByDTO` | 9 | `testFindByDTONull`<br>`testFindByDTOLibelleNull`<br>`testFindByDTOBlank`<br>`testFindByDTOGatewayRetourNull`<br>`testFindByDTOGatewayKOAvecMessage`<br>`testFindByDTOGatewayKOSansMessage`<br>`testFindByDTOConversionOutputDTOKOAvecMessage`<br>`testFindByDTOConversionOutputDTOKOSansMessage`<br>`testFindByDTONominal` |
| `findById` | 7 | `testFindByIdNull`<br>`testFindByIdGatewayRetourNull`<br>`testFindByIdGatewayKOAvecMessage`<br>`testFindByIdGatewayKOSansMessage`<br>`testFindByIdConversionOutputDTOKOAvecMessage`<br>`testFindByIdConversionOutputDTOKOSansMessage`<br>`testFindByIdNominal` |
| `update` | 14 | `testUpdateNull`<br>`testUpdateLibelleNull`<br>`testUpdateBlank`<br>`testUpdateRechercheKOAvecMessage`<br>`testUpdateRechercheKOSansMessage`<br>`testUpdateIntrouvable`<br>`testUpdateNonPersistant`<br>`testUpdateModificationKOAvecMessage`<br>`testUpdateModificationKOSansMessage`<br>`testUpdateModificationRetourNull`<br>`testUpdateModificationRetourNonPersistant`<br>`testUpdateConversionOutputDTOKOAvecMessage`<br>`testUpdateConversionOutputDTOKOSansMessage`<br>`testUpdateNominal` |
| `delete` | 10 | `testDeleteNull`<br>`testDeleteLibelleNull`<br>`testDeleteBlank`<br>`testDeleteRechercheKOAvecMessage`<br>`testDeleteRechercheKOSansMessage`<br>`testDeleteIntrouvable`<br>`testDeleteNonPersistant`<br>`testDeleteDestructionKOAvecMessage`<br>`testDeleteDestructionKOSansMessage`<br>`testDeleteNominal` |
| `count` | 5 | `testCountGatewayKOAvecMessage`<br>`testCountGatewayKOSansMessage`<br>`testCountRetourNegatif`<br>`testCountZero`<br>`testCountNominal` |
| `getMessage` | 5 | `testGetMessageInitialNull`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageApresCountZero`<br>`testGetMessageApresCountNominal`<br>`testGetMessageDernierMessageGagne` |

#### `SousTypeProduitCuServiceMockTest.java` — 127 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 16 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerParentBlank`<br>`testCreerControleTechniqueKoAvecMessage`<br>`testCreerControleTechniqueKoSansMessage`<br>`testCreerDoublon`<br>`testCreerParentTechniqueKoAvecMessage`<br>`testCreerParentTechniqueKoSansMessage`<br>`testCreerParentAbsent`<br>`testCreerParentNonPersistant`<br>`testCreerCreationTechniqueKoAvecMessage`<br>`testCreerCreationTechniqueKoSansMessage`<br>`testCreerGatewayRetourneNull`<br>`testCreerConversionTechniqueKoAvecMessage`<br>`testCreerConversionTechniqueKoSansMessage`<br>`testCreerNominal` |
| `rechercherTous` | 7 | `testRechercherTousGatewayRetourNull`<br>`testRechercherTousGatewayKOAvecMessage`<br>`testRechercherTousGatewayKOSansMessage`<br>`testRechercherTousConversionOutputDTOKOAvecMessage`<br>`testRechercherTousConversionOutputDTOKOSansMessage`<br>`testRechercherTousVideApresFiltrage`<br>`testRechercherTousNominal` |
| `rechercherTousString` | 8 | `testRechercherTousStringGatewayRetourNull`<br>`testRechercherTousStringGatewayKOAvecMessage`<br>`testRechercherTousStringGatewayKOSansMessage`<br>`testRechercherTousStringConversionStringKOAvecMessage`<br>`testRechercherTousStringConversionStringKOSansMessage`<br>`testRechercherTousStringVideApresFiltrage`<br>`testRechercherTousStringVideApresLibellesBlank`<br>`testRechercherTousStringNominal` |
| `rechercherTousParPage` | 8 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageGatewayKOAvecMessage`<br>`testRechercherTousParPageGatewayKOSansMessage`<br>`testRechercherTousParPageGatewayRetourNull`<br>`testRechercherTousParPageConversionOutputDTOKOAvecMessage`<br>`testRechercherTousParPageConversionOutputDTOKOSansMessage`<br>`testRechercherTousParPageVideApresFiltrage`<br>`testRechercherTousParPageNominal` |
| `findByLibelle` | 9 | `testFindByLibelleNull`<br>`testFindByLibelleBlank`<br>`testFindByLibelleGatewayRetourNull`<br>`testFindByLibelleGatewayKOAvecMessage`<br>`testFindByLibelleGatewayKOSansMessage`<br>`testFindByLibelleConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleConversionOutputDTOKOSansMessage`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleNominal` |
| `findByLibelleRapide` | 9 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideGatewayKOAvecMessage`<br>`testFindByLibelleRapideGatewayKOSansMessage`<br>`testFindByLibelleRapideGatewayRetourNull`<br>`testFindByLibelleRapideConversionOutputDTOKOAvecMessage`<br>`testFindByLibelleRapideConversionOutputDTOKOSansMessage`<br>`testFindByLibelleRapideVideApresFiltrage`<br>`testFindByLibelleRapideNominal` |
| `findAllByParent` | 13 | `testFindAllByParentNull`<br>`testFindAllByParentParentBlank`<br>`testFindAllByParentParentGatewayKOAvecMessage`<br>`testFindAllByParentParentGatewayKOSansMessage`<br>`testFindAllByParentParentAbsent`<br>`testFindAllByParentParentNonPersistant`<br>`testFindAllByParentEnfantsGatewayKOAvecMessage`<br>`testFindAllByParentEnfantsGatewayKOSansMessage`<br>`testFindAllByParentGatewayRetourNull`<br>`testFindAllByParentConversionOutputDTOKOAvecMessage`<br>`testFindAllByParentConversionOutputDTOKOSansMessage`<br>`testFindAllByParentVideApresFiltrage`<br>`testFindAllByParentNominal` |
| `findByDTO` | 10 | `testFindByDTONull`<br>`testFindByDTOParentBlank`<br>`testFindByDTOErreurTechniqueRechercheParentAvecMessage`<br>`testFindByDTOErreurTechniqueRechercheParentSansMessage`<br>`testFindByDTOParentNonPersistant`<br>`testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage`<br>`testFindByDTOErreurTechniqueRechercheEnfantsSansMessage`<br>`testFindByDTOVide`<br>`testFindByDTOIntrouvableDansListe`<br>`testFindByDTOOk` |
| `findById` | 5 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdErreurTechniqueAvecMessage`<br>`testFindByIdErreurTechniqueSansMessage`<br>`testFindByIdOk` |
| `update` | 17 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateParentBlank`<br>`testUpdateRechercheParentTechniqueKoAvecMessage`<br>`testUpdateRechercheParentTechniqueKoSansMessage`<br>`testUpdateParentAbsent`<br>`testUpdateParentNonPersistant`<br>`testUpdateRechercheEnfantsTechniqueKoAvecMessage`<br>`testUpdateRechercheEnfantsTechniqueKoSansMessage`<br>`testUpdateStockageNullPendantReidentification`<br>`testUpdateIntrouvable`<br>`testUpdateNonPersistant`<br>`testUpdateModificationTechniqueKoAvecMessage`<br>`testUpdateModificationTechniqueKoSansMessage`<br>`testUpdateGatewayNull`<br>`testUpdateRetourNonPersistant`<br>`testUpdateOk` |
| `delete` | 15 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteParentBlank`<br>`testDeleteRechercheParentTechniqueKoAvecMessage`<br>`testDeleteRechercheParentTechniqueKoSansMessage`<br>`testDeleteParentAbsent`<br>`testDeleteParentNonPersistant`<br>`testDeleteRechercheEnfantsTechniqueKoAvecMessage`<br>`testDeleteRechercheEnfantsTechniqueKoSansMessage`<br>`testDeleteStockageNullPendantReidentification`<br>`testDeleteIntrouvable`<br>`testDeleteNonPersistant`<br>`testDeleteTechniqueKoAvecMessage`<br>`testDeleteTechniqueKoSansMessage`<br>`testDeleteOk` |
| `count` | 5 | `testCountTechniqueKoAvecMessage`<br>`testCountTechniqueKoSansMessage`<br>`testCountRetourNegatifIncoherent`<br>`testCountZero`<br>`testCountPositif` |
| `getMessage` | 5 | `testGetMessageInitialNull`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageApresCountZero`<br>`testGetMessageApresCountPositif`<br>`testGetMessageDernierMessageGagne` |

#### `ProduitCuServiceMockTest.java` — 71 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 10 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerParentBlank`<br>`testCreerControleTechniqueKoAvecMessage`<br>`testCreerDoublon`<br>`testCreerParentTechniqueKoAvecMessage`<br>`testCreerParentAbsent`<br>`testCreerCreationTechniqueKoAvecMessage`<br>`testCreerRetourGatewayNull`<br>`testCreerOk` |
| `rechercherTous` | 5 | `testRechercherTousStockageNull`<br>`testRechercherTousKoTechniqueAvecMessage`<br>`testRechercherTousKoTechniqueSansMessage`<br>`testRechercherTousVideApresFiltrage`<br>`testRechercherTousOk` |
| `rechercherTousString` | 2 | `testRechercherTousStringVide`<br>`testRechercherTousStringOk` |
| `rechercherTousParPage` | 5 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageKoTechniqueAvecMessage`<br>`testRechercherTousParPageKoTechniqueSansMessage`<br>`testRechercherTousParPageRetourNull`<br>`testRechercherTousParPageOk` |
| `findByLibelle` | 4 | `testFindByLibelleBlank`<br>`testFindByLibelleGatewayRetourNull`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleOk` |
| `findByLibelleRapide` | 5 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideGatewayRetourNull`<br>`testFindByLibelleRapideIntrouvable`<br>`testFindByLibelleRapideOk` |
| `findAllByParent` | 6 | `testFindAllByParentNull`<br>`testFindAllByParentParentBlank`<br>`testFindAllByParentPasParent`<br>`testFindAllByParentGatewayRetourNull`<br>`testFindAllByParentIntrouvable`<br>`testFindAllByParentOk` |
| `findByDTO` | 5 | `testFindByDTONull`<br>`testFindByDTOParentBlank`<br>`testFindByDTOParentAbsent`<br>`testFindByDTOIntrouvable`<br>`testFindByDTOOk` |
| `findById` | 3 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdOk` |
| `update` | 7 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateParentBlank`<br>`testUpdateParentAbsent`<br>`testUpdateIntrouvable`<br>`testUpdateNonPersistant`<br>`testUpdateOk` |
| `delete` | 10 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteParentBlank`<br>`testDeleteRechercheParentTechniqueKoAvecMessage`<br>`testDeleteParentAbsent`<br>`testDeleteStockageNull`<br>`testDeleteIntrouvable`<br>`testDeleteNonPersistant`<br>`testDeleteTechniqueKoAvecMessage`<br>`testDeleteOkAvecPreuveCoupleParentLibelle` |
| `count` | 5 | `testCountTechniqueKoAvecMessage`<br>`testCountTechniqueKoSansMessage`<br>`testCountRetourNegatifIncoherent`<br>`testCountZero`<br>`testCountPositif` |
| `getMessage` | 4 | `testGetMessageInitialNull`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageApresCountZero`<br>`testGetMessageDernierMessageGagne` |

#### `TypeProduitCuServiceIntegrationTest.java` — 43 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 6 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerOkAvecPreuveBdEtRoundTrip`<br>`testCreerDoublonAvecPreuveBd`<br>`testCreerOkFindByLibelleOkFindByIdOk`<br>`testCreerDoublon` |
| `rechercherTous` | 3 | `testRechercherTous`<br>`testRechercherTousOkAvecPreuveBd`<br>`testRechercherTousVide` |
| `rechercherTousString` | 3 | `testRechercherTousString`<br>`testRechercherTousStringOkAvecPreuveBd`<br>`testRechercherTousStringVide` |
| `rechercherTousParPage` | 3 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageOk`<br>`testRechercherTousParPageOkAvecPreuveBd` |
| `findByLibelle` | 3 | `testFindByLibelleBlank`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleOkAvecPreuveBd` |
| `findByLibelleRapide` | 4 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideIntrouvable`<br>`testFindByLibelleRapideOkAvecPreuveBd` |
| `findByDTO` | 4 | `testFindByDTONull`<br>`testFindByDTOBlank`<br>`testFindByDTOIntrouvable`<br>`testFindByDTOOkAvecPreuveBd` |
| `findById` | 3 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdOkAvecPreuveBd` |
| `update` | 4 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateIntrouvable`<br>`testUpdateOkAvecPreuveBdEtIdConserve` |
| `delete` | 4 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteIntrouvable`<br>`testDeleteOkAvecPreuveBd` |
| `count` | 2 | `testCountRetourneLeNombrePhysiqueEtLeMessageObservable`<br>`testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage` |
| `getMessage` | 4 | `testGetMessageInitialNull`<br>`testGetMessageApresSuccesReel`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageDernierMessageGagne` |

#### `SousTypeProduitCuServiceIntegrationTest.java` — 53 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 6 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerParentBlank`<br>`testCreerPasParent`<br>`testCreerOkAvecPreuveBdEtRoundTrip`<br>`testCreerDoublonAvecPreuveBd` |
| `rechercherTous` | 3 | `testRechercherTous`<br>`testRechercherTousOkAvecPreuveBd`<br>`testRechercherTousVide` |
| `rechercherTousString` | 3 | `testRechercherTousString`<br>`testRechercherTousStringOkAvecPreuveBd`<br>`testRechercherTousStringVide` |
| `rechercherTousParPage` | 3 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageOk`<br>`testRechercherTousParPageOkAvecPreuveBd` |
| `findByLibelle` | 3 | `testFindByLibelleBlank`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleOk` |
| `findByLibelleRapide` | 4 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideIntrouvable`<br>`testFindByLibelleRapideOkAvecPreuveBd` |
| `findAllByParent` | 5 | `testFindAllByParentNull`<br>`testFindAllByParentParentBlank`<br>`testFindAllByParentPasParent`<br>`testFindAllByParentVide`<br>`testFindAllByParentOkAvecPreuveBd` |
| `findByDTO` | 5 | `testFindByDTONull`<br>`testFindByDTOParentBlank`<br>`testFindByDTOParentAbsent`<br>`testFindByDTOCoupleIntrouvableAvecPreuveBd`<br>`testFindByDTOOkAvecPreuveCoupleParentLibelle` |
| `findById` | 3 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdOkAvecPreuveBd` |
| `update` | 6 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateParentBlank`<br>`testUpdateParentAbsent`<br>`testUpdateIntrouvable`<br>`testUpdateOkAvecPreuveCoupleParentLibelleEtIdConserve` |
| `delete` | 6 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteParentBlank`<br>`testDeleteParentAbsent`<br>`testDeleteIntrouvable`<br>`testDeleteOkAvecPreuveCoupleParentLibelle` |
| `count` | 2 | `testCountRetourneLeNombrePhysiqueEtLeMessageObservable`<br>`testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage` |
| `getMessage` | 4 | `testGetMessageInitialNull`<br>`testGetMessageApresSuccesReel`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageDernierMessageGagne` |

#### `ProduitCuServiceIntegrationTest.java` — 48 tests détectés

| Bloc PORT UC | Nombre | Méthodes de test validées ou en cours de validation |
|---|---:|---|
| `creer` | 5 | `testCreerNull`<br>`testCreerBlank`<br>`testCreerParentBlank`<br>`testCreerDoublon`<br>`testCreerOk` |
| `rechercherTous` | 3 | `testRechercherTous`<br>`testRechercherTousOkAvecCohherenceCount`<br>`testRechercherTousVide` |
| `rechercherTousString` | 2 | `testRechercherTousStringOk`<br>`testRechercherTousStringVide` |
| `rechercherTousParPage` | 2 | `testRechercherTousParPageNull`<br>`testRechercherTousParPageOk` |
| `findByLibelle` | 3 | `testFindByLibelleBlank`<br>`testFindByLibelleIntrouvable`<br>`testFindByLibelleOk` |
| `findByLibelleRapide` | 4 | `testFindByLibelleRapideNull`<br>`testFindByLibelleRapideBlank`<br>`testFindByLibelleRapideIntrouvable`<br>`testFindByLibelleRapideOk` |
| `findAllByParent` | 4 | `testFindAllByParentNull`<br>`testFindAllByParentPasParent`<br>`testFindAllByParentIntrouvable`<br>`testFindAllByParentOk` |
| `findByDTO` | 4 | `testFindByDTONull`<br>`testFindByDTOParentBlank`<br>`testFindByDTOIntrouvable`<br>`testFindByDTOOk` |
| `findById` | 3 | `testFindByIdNull`<br>`testFindByIdIntrouvable`<br>`testFindByIdOk` |
| `update` | 6 | `testUpdateNull`<br>`testUpdateBlank`<br>`testUpdateParentBlank`<br>`testUpdateParentAbsent`<br>`testUpdateIntrouvable`<br>`testUpdateOkAvecPreuveBdEtJdbcTemplate` |
| `delete` | 6 | `testDeleteNull`<br>`testDeleteBlank`<br>`testDeleteParentBlank`<br>`testDeleteParentAbsent`<br>`testDeleteIntrouvable`<br>`testDeleteOkAvecPreuveCoupleParentLibelle` |
| `count` | 2 | `testCountRetourneLeNombrePhysiqueEtLeMessageObservable`<br>`testCountCoherentAvecMessagesAvantApresCreationsPuisNettoyage` |
| `getMessage` | 4 | `testGetMessageInitialNull`<br>`testGetMessageApresErreurLocale`<br>`testGetMessageApresCount`<br>`testGetMessageDernierMessageGagne` |


## 23) RT-NON-FIGEAGE-UC-01 — Interdiction de figer prématurément

Tant que l'utilisateur n'a pas déclaré la couche UC terminée et validée :

- ne pas annoncer que la couche IA UC permet de recoder définitivement toute la couche UC ;
- ne pas transformer les tests UC encore en correction en référence finale ;
- ne pas inventer des règles nouvelles à chaque passe ;
- s'appuyer sur les règles déjà validées, les tests Gateway sacralisés et `TypeProduitCuServiceMockTest.java` ;
- après chaque validation utilisateur, relire les corrections et consolider progressivement.

Quand la couche UC sera terminée, l'IA devra refaire la confrontation complète : contrats IA UC, PORTS, ADAPTERS, exceptionsservices, DTO/convertisseurs, Gateway utiles, objets métier et tests Mock/intégration définitifs.
---

## 24) RT-WORKFLOW-AUDIT-TEST-01 — Workflow d'audit des blocs de tests UC/JUnit

Cette règle complète la sacralisation progressive UC. Elle s'applique à tout audit ou codage d'un bloc de tests JUnit UC.

### 24.1 Préambule obligatoire

Avant toute opération, l'IA relit obligatoirement `CONTRAT_IA.md`.

Quand l'Utilisateur demande l'audit d'un test JUnit UC, l'IA détermine si nécessaire le périmètre utile, contrôle que les fichiers du périmètre sont corrects et à jour dans la baseline, puis installe ou active une fenêtre de travail si tout est OK.

Une fois la fenêtre active, l'IA travaille depuis cette fenêtre sans relire GitHub au SHA à chaque bloc, sauf nouveau SHA, changement de périmètre, demande explicite, incident de lecture ou fenêtre suspecte.

### 24.2 Périmètre utile UC

Pour un bloc de tests UC, le périmètre utile comprend au minimum :

- `CONTRAT_IA.md` ;
- le présent contrat `CoucheServicesUC.md` ;
- le PORT UC concerné ;
- le SERVICE METIER UC testé ;
- les Gateways collaborateurs utiles ;
- les DTO, convertisseurs et exceptions utiles ;
- la classe de test cible ;
- les méthodes déjà validées dans la classe de test ;
- `TypeProduitCuServiceMockTest.java` comme référence stable Mockito UC quand elle est pertinente ;
- les tests Gateway sacralisés lorsque leur formalisme sert de référence transposable.

### 24.3 Rapport d'audit sans code

Si l'Utilisateur n'a pas écrit `coder`, l'IA rend un rapport d'audit sans code.

Le rapport doit préciser :

- la preuve de lecture ;
- le bloc contrôlé ;
- les tests présents ;
- l'ordre actuel ;
- les comportements couverts ;
- les manques ;
- les tests à ajouter, renommer ou déplacer ;
- les problèmes de Javadoc ou commentaires ;
- l'ordre recommandé ;
- le verdict du bloc.

L'IA ne doit pas livrer de code pendant cette étape.

### 24.4 Correction de l'analyse par l'Utilisateur

Si l'Utilisateur corrige l'analyse, le vocabulaire ou le formalisme proposé par l'IA, cette correction prévaut immédiatement.

L'IA doit :

- reconnaître la correction comme règle utile si elle est durable ;
- la mémoriser comme règle candidate à intégrer dans la couche IA au prochain `coder` ;
- l'appliquer aux blocs suivants ;
- neutraliser la formulation ou habitude fautive.

### 24.5 Livraison après `coder`

Si l'Utilisateur écrit `coder`, la livraison d'un bloc de tests UC doit respecter le format autonome suivant :

1. bloc autonome des constantes TAG à ajouter ;
2. bloc autonome des constantes DISPLAY_NAME à ajouter ;
3. bloc autonome des constantes DN uniquement si elles sont réellement nécessaires ;
4. bloc autonome complet des tests.

Les blocs doivent être séparés, clairement titrés dans la réponse et directement copiables dans STS.

Interdictions :

- livrer uniquement des méthodes isolées ;
- noyer les constantes TAG dans les DISPLAY_NAME ;
- noyer les constantes DN dans les DISPLAY_NAME ;
- créer des constantes DN artificielles quand une constante existante suffit ;
- changer le formalisme déjà validé dans la classe cible.

### 24.6 Règle sur les constantes DN

Les constantes DN ne doivent être créées que si elles améliorent réellement la lisibilité, évitent une duplication lourde ou correspondent déjà au formalisme établi de la classe.

Si une constante existante suffit à exprimer le scénario, elle doit être réutilisée.

Exemples :

- pour tester un parent avec libellé blank, il suffit d'utiliser une valeur blank déjà existante ;
- pour tester un parent existant, réutiliser une constante métier existante comme `VETEMENT`, `BAZAR`, `OUTILLAGE` ou toute constante déjà validée si elle exprime clairement le cas ;
- ne pas créer de constante de type `DN_FIND_BY_DTO_PARENT` par réflexe.

### 24.7 Javadocs et commentaires de tests UC

Les Javadocs et commentaires doivent dire concrètement ce que le test vérifie.

Interdictions :

- « vise la branche locale » ;
- « non exploitable » ;
- « objet exploitable » ;
- « réponse exploitable » ;
- toute formulation abstraite qui ne décrit pas le fait testé.

Formulations attendues :

- teste le cas où le DTO transmis est null ;
- teste le cas où le parent a un libellé blank ;
- teste le cas où le parent est absent du stockage ;
- teste le cas où le parent n'a pas d'identifiant persistant ;
- teste le cas où le Gateway retourne null ;
- teste le cas où la liste retournée est vide ;
- teste le cas où la conversion OutputDTO échoue avec message ;
- teste le cas où la conversion OutputDTO échoue sans message.

### 24.8 Conversion finale protégée par `try/catch`

Pour un bloc Mock UC, dès que la méthode du service contient une conversion finale protégée par `try/catch`, le bloc de tests doit envisager explicitement les deux cas suivants :

- conversion OutputDTO KO avec message ;
- conversion OutputDTO KO sans message.

La règle ne s'écarte que si une justification contractuelle explicite démontre que le cas est impossible ou non pertinent.

Le stubbing Mockito doit rester strictement consommé : ne jamais stubber un getter qui n'est pas appelé avant l'exception.

### 24.9 Retour utilisateur après intégration STS

Après livraison, l'Utilisateur intègre dans STS et teste.

S'il renvoie le fichier corrigé, l'IA doit relire le dernier fichier joint réel selon `CONTRAT_IA.md`, détecter les corrections utilisateur, apprendre le formalisme corrigé, puis consolider baseline et fenêtre active si les tests sont verts.

### 24.10 Recontrôle automatique

Après consolidation d'un bloc testé vert, l'IA doit automatiquement recontrôler la totalité du bloc jusqu'à pouvoir le déclarer conforme aux critères de qualité des tests.

Ce recontrôle porte sur :

- complétude ;
- ordre des tests ;
- constantes TAG / DISPLAY_NAME / DN ;
- Javadocs ;
- commentaires ;
- comportement réel du service ;
- messages ;
- interactions Mockito ;
- formalisme déjà validé dans la classe.

### 24.11 Cycle bloc par bloc

Si tout est OK, l'Utilisateur passe au bloc suivant et le cycle reprend : audit du bloc, rapport sans code, correction éventuelle de l'analyse, `coder` si demandé, intégration STS, retour utilisateur, relecture du fichier joint, consolidation, recontrôle automatique.

## 25) RT-FORMALISME-UC-INTEGRATION-CREER-01 — Règles locales issues de `TypeProduitCuServiceIntegrationTest.creer(...)`

### 25.1 Objet

Cette section relaie localement, pour la couche `couche_services.uc`, les règles validées pendant la correction du bloc `creer(...)` de `TypeProduitCuServiceIntegrationTest.java`.

Elle complète les règles générales du `CONTRAT_IA.md` et s'applique aux tests UC d'intégration comparables.

### 25.2 Livraison STS en trois blocs

Pour un bloc de tests UC corrigé ou créé, l'IA doit livrer séparément :

1. la constante `TAG_...` du bloc de tests ;
2. les constantes `DN_...` ou `DISPLAY_NAME_...` du bloc, selon le formalisme local ;
3. le bloc complet des méthodes de test.

Ces trois éléments ne vont pas au même endroit dans STS.

La livraison ne doit pas mélanger la constante de tag, les constantes de display name et le code des tests.

### 25.3 Tags et display names

Lorsqu'un test de référence utilise des constantes, l'IA doit reproduire ce formalisme :

```java
@Tag(TAG_CREER)
@DisplayName(DN_CREER_NULL)
@Test
```

Les `@DisplayName("...")` inline sont interdits dans un bloc qui utilise déjà des constantes `DN_...` ou `DISPLAY_NAME_...`.

L'IA ne doit pas changer seule la qualité de livraison en revenant à des chaînes en dur.

### 25.4 Commentaires internes des tests

Les commentaires internes doivent rester simples, factuels et proches du code exécuté.

Ils doivent nommer clairement :

- l'appel testé ;
- l'exception ou le retour attendu ;
- le message contractuel attendu ;
- la preuve réalisée dans le stockage.

Formulation validée :

```java
/* ACT - ASSERT :
 * Garantit que this.service.creer(libellé blank)
 * - jette une ExceptionParametreBlank
 * - avec un message MESSAGE_CREER_LIBELLE_BLANK_KO.
 */
```

L'IA ne doit pas compliquer les commentaires avec des formulations abstraites ou décoratives.

### 25.5 Javadocs des constantes de test

Pour une constante simple, la Javadoc doit d'abord permettre au développeur de voir la valeur littérale dans STS.

Forme attendue :

```java
/**
 * "Outil".
 */
public static final String OUTIL = "Outil";
```

Forme à éviter si elle n'apporte rien :

```java
/**
 * TypeProduit IT : "IT-TP-PAGE-03".
 */
```

La mention `TypeProduit IT :` n'est pas utile si elle ne précise ni un scénario, ni une contrainte, ni un usage.

### 25.6 Valeurs seedées et non seedées dans les tests de création

Dans un test d'intégration UC de création, il faut distinguer :

- une valeur déjà seedée dans le stockage ;
- une valeur créée par le test.

Pour tester le scénario `créer puis recréer le même libellé afin de provoquer un doublon`, la première création doit utiliser un libellé non seedé.

Exemple validé :

```java
public static final String OUTIL = "Outil";
public static final String VETEMENT = "Vêtement";
public static final String NON_SEEDE = "Eléctronique";
```

`NON_SEEDE` est utilisé pour installer le doublon par le test lui-même.

Une valeur déjà présente comme `Vêtement` ne doit pas être utilisée pour la première création d'un scénario `créer puis recréer`, car le premier `service.creer(...)` échouerait déjà en `ExceptionDoublon`.

### 25.7 Configuration autonome des tests d'intégration UC

Le modèle validé pour un test d'intégration UC avec stockage JPA/H2 est :

- `@DataJpaTest` ;
- `@Import` explicite du SERVICE UC testé et des Gateways utiles ;
- `@ContextConfiguration` avec configuration locale ;
- `@SpringBootConfiguration(proxyBeanMethods = false)` ;
- `@AutoConfigurationPackage(basePackageClasses = ...)` ;
- pas de `@SpringBootTest` si le slice suffit ;
- pas de `@ComponentScan` large ;
- pas de `@EnableJpaRepositories` manuel ;
- pas de `@EntityScan` manuel.

Si le SERVICE UC porte un état local observable par `getMessage()`, la classe de test doit conserver un `@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)` commenté, afin de recréer un SERVICE UC neuf entre les tests.

### 25.8 Preuve SQL après suppression JPA

Lorsqu'un test d'intégration UC supprime un objet via JPA puis prouve la suppression par `JdbcTemplate`, il doit synchroniser le contexte JPA avant la lecture directe du stockage.

Règle validée :

```java
this.service.delete(input);
this.entityManager.flush();
```

Le `flush()` se place après l'appel au SERVICE UC et avant les preuves SQL directes.

### 25.9 Homogénéité des méthodes UC `creer(...)`

Pour les PORTS et ADAPTERS UC `TypeProduit`, `SousTypeProduit` et `Produit`, l'IA doit recopier ligne à ligne tout ce qui peut l'être.

Elle ne doit modifier que ce que l'objet métier impose réellement :

- types DTO, objet métier et Gateway ;
- libellé métier ;
- parent pour `SousTypeProduit` et `Produit` ;
- recherche du parent ;
- parent absent ;
- parent non persistant.

Les constantes `MESSAGE_CREER_xxx` et `PREFIX_MESSAGE_CREER_xxx` se déduisent des branches réelles de `creer(...)` et ne doivent pas être inventées.

