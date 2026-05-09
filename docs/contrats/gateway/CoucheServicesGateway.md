# Contrat local de couche - Couche services Gateway - CoucheServicesGateway.md

## 1) Intention de la couche

La sous-couche `couche_services.gateway` porte les services techniques d'accès au stockage du domaine `produittype`.

Elle expose :
- les PORTS Gateway ;
- les ADAPTERS Gateway JPA ;
- les exceptions techniques et applicatives propres au niveau Gateway ;
- le socle transverse de pagination utilisé par les Gateways ;
- les tests Mock Gateway ;
- les tests d'intégration Gateway ;
- les contrats locaux nécessaires à la relecture de la couche.

Cette sous-couche appartient à `couche_services`.

## 2) Frontière architecturale

La sous-couche `couche_services.gateway` :
- manipule les objets métier ;
- ne manipule pas les DTO ;
- ne porte pas la logique UC ;
- ne porte pas de logique Controller ;
- ne porte pas de logique View ;
- ne porte pas les exceptions de SERVICE UC.

Les DTO relèvent de `couche_dto`.
Les services UC relèvent de `couche_services.uc`.
Les exceptions de service UC relèvent du périmètre UC, pas du périmètre Gateway.

## 3) Fichiers inclus dans le périmètre sacralisé

### 3.1) PORTS Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

### 3.2) ADAPTERS Gateway JPA

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`

### 3.3) Exceptions Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliLibelleBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNonPersistent.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParentNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGateway.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGatewayNonPersistent.java`

### 3.4) Pagination transverse Gateway

- `src/main/java/levy/daniel/application/model/services/produittype/pagination/DirectionTri.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/RequetePage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/ResultatPage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/TriSpec.java`

### 3.5) Tests Gateway

- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`

### 3.6) Tests de pagination

- `src/test/java/levy/daniel/application/model/services/produittype/pagination/DirectionTriTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/RequetePageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/ResultatPageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/TriSpecTest.java`

### 3.7) Contrats locaux de la sous-couche

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`

## 4) Règles de cohérence obligatoires

### 4.1) Règle DTO

Aucun fichier de cette sous-couche ne doit dépendre des DTO de `couche_dto`.

### 4.2) Règle UC

Aucun fichier de cette sous-couche ne doit porter des messages utilisateur, des retours UC observables ou des exceptions de SERVICE UC.

### 4.3) Règle métier

Les Gateways manipulent les objets métier et assurent la traduction technique vers la persistance.

### 4.4) Règle pagination

Le socle de pagination appartient au périmètre sacralisé de Gateway tant qu'il est utilisé comme support contractuel des recherches paginées des Gateways.

### 4.5) Règle de preuve

Les tests Mock verrouillent le contrat technique.
Les tests d'intégration prouvent le comportement réel JPA/stockage.

### 4.6) Règle de contrats locaux

Les contrats locaux de cette sous-couche servent de pivots de relecture avant toute analyse, tout diagnostic ou toute génération de code portant sur Gateway.

## 5) RT-CODAGE-TEST-MOCKITO-GATEWAY-01 — Codage des tests Mockito Gateway

### 5.1) Objet

Cette règle définit la méthode obligatoire de l'IA pour analyser, auditer, corriger ou coder les tests Mockito des SERVICE GATEWAY du projet PatrimoineSSP.

Elle s'applique à tous les tests Mockito Gateway, notamment :
- `TypeProduitGatewayJPAServiceMockTest.java` ;
- `SousTypeProduitGatewayJPAServiceMockTest.java` ;
- `ProduitGatewayJPAServiceMockTest.java` ;
- tout futur test Mockito Gateway équivalent.

`TypeProduitGatewayJPAServiceMockTest.java` est le test validé de référence pour le formalisme, mais la règle n'est pas limitée à TypeProduit.

### 5.2) Source de vérité

Avant toute analyse, audit, validation, correction ou génération de code sur un test Mockito Gateway, l'IA doit travailler strictement sur pièces.

Ordre obligatoire :
1. lire le présent contrat local Gateway ;
2. lire le contrat du PORT Gateway concerné ;
3. lire la méthode correspondante dans l'ADAPTER Gateway réel ;
4. lire toutes les dépendances utiles à la compréhension réelle ;
5. lire le test Mockito cible ;
6. lire les tests Mockito de référence déjà validés si le formalisme local doit être confirmé.

Règle centrale :
- le PORT définit les cas contractuels ;
- l'ADAPTER Gateway définit les collaborateurs réels et les interactions réelles ;
- les tests déjà validés définissent le formalisme ;
- l'IA ne doit jamais raisonner de mémoire, ni par analogie non relue.

### 5.3) Structure complète d'une classe Mockito Gateway

Un test Mockito Gateway complet doit rester un test Mockito pur.

Règles obligatoires :
- utiliser `@ExtendWith(MockitoExtension.class)` ;
- ne pas utiliser `@SpringBootTest` ;
- ne pas utiliser `@DataJpaTest` ;
- ne pas utiliser `@ActiveProfiles` ;
- ne pas démarrer de contexte Spring ;
- ne pas utiliser l'injection Spring ;
- déclarer avec `@Mock` uniquement les collaborateurs réels de l'ADAPTER Gateway ;
- instancier le SERVICE GATEWAY testé directement dans une méthode `@BeforeEach init()` ;
- ne jamais déclarer un mock correspondant à un collaborateur absent de l'ADAPTER réel.

Exemple de principe :
- `TypeProduitGatewayJPAServiceMockTest.java` mocke `TypeProduitDaoJPA` uniquement ;
- `SousTypeProduitGatewayJPAServiceMockTest.java` mocke seulement les collaborateurs réellement utilisés par `SousTypeProduitGatewayJPAService` ;
- `ProduitGatewayJPAServiceMockTest.java` mocke seulement les collaborateurs réellement utilisés par `ProduitGatewayJPAService`.

### 5.4) Javadoc de tête de classe

Tout test Mockito Gateway complet doit comporter une javadoc de tête de classe.

Cette javadoc doit préciser :
- le nom de la classe de test ;
- le SERVICE GATEWAY testé ;
- le PORT Gateway dont le contrat est vérifié ;
- les collaborateurs mockés ;
- le fait qu'aucun profil Spring n'est activé ;
- le fait qu'aucun contexte Spring n'est démarré ;
- le fait que les collaborateurs sont mockés à la main ;
- le fait que le SERVICE GATEWAY est instancié directement dans `init()` ;
- le rôle du test dans la preuve du contrat technique Gateway.

La javadoc de tête doit conserver le formalisme documentaire déjà validé dans les tests Mockito Gateway existants, avec les mentions utiles `@author`, `@version` et `@since` lorsqu'elles sont présentes dans le fichier de référence.

### 5.5) Constantes, tags et bannières

Les constantes de test doivent être centralisées en tête de classe et réutilisées dans les méthodes de test.

Règles :
- déclarer une constante `TAG_*` par bloc de méthode PORT ;
- déclarer les constantes de libellés métier utiles ;
- déclarer les constantes d'identifiants utiles ;
- déclarer les constantes de messages techniques utiles ;
- déclarer les constantes de propriétés de tri utiles ;
- déclarer les constantes de noms de méthodes privées utiles ;
- éviter les chaînes magiques dispersées dans les méthodes de test ;
- ne pas créer de constante inutile si la valeur n'est utilisée qu'une fois et ne porte pas de sens métier ou technique durable.

Les blocs de test doivent être matérialisés par des bannières visibles, homogènes avec les fichiers déjà validés.

Exemple de bannières pour `TypeProduitGatewayJPAServiceMockTest.java` :

```java
// =============================== CREER ==============================
// ======================== RechercherTous ============================
// ================== rechercherTousParPage ===========================
// ======================== findByObjetMetier =========================
// ========================== findByLibelle ===========================
// ======================== findByLibelleRapide =======================
// ============================ findById ==============================
// ============================= update ===============================
// ============================= delete ===============================
// ============================== Count ===============================
// ============================== TRIS ================================
// ========================= DEDOUBLONNAGE =============================
// ================ Tests des Methodes PRIVATE ========================
// ============================== OUTILS ===============================
```

Règles :
- respecter l'ordre du PORT ;
- placer les blocs didactiques après les blocs publics ;
- placer les outils en fin de fichier ;
- ne pas dupliquer les bannières ;
- ne pas renommer arbitrairement une bannière déjà validée.

### 5.6) Raisonnement par bloc PORT

L'IA doit toujours raisonner par bloc de méthodes de test.

Chaque bloc correspond à une méthode du PORT Gateway.

Exemple de blocs pour `TypeProduitGatewayIService` :
- `creer(...)` ;
- `rechercherTous()` ;
- `rechercherTousParPage(...)` ;
- `findByObjetMetier(...)` ;
- `findByLibelle(...)` ;
- `findByLibelleRapide(...)` ;
- `findById(...)` ;
- `update(...)` ;
- `delete(...)` ;
- `count()`.

Exemple de blocs pour les Gateways avec parent métier :
- `creer(...)` ;
- `rechercherTous()` ;
- `rechercherTousParPage(...)` ;
- `findByObjetMetier(...)` ;
- `findByLibelle(...)` ;
- `findByLibelleRapide(...)` ;
- `findAllByParent(...)` ;
- `findById(...)` ;
- `update(...)` ;
- `delete(...)` ;
- `count()`.

Le PORT décide des blocs. L'IA ne doit pas inventer un bloc absent du PORT.

### 5.7) Déduction des tests contractuels

Pour chaque bloc, l'IA doit d'abord lire le contrat de la méthode dans le PORT, puis établir la liste des tests contractuels.

Un test est contractuel s'il vérifie directement un cas prévu ou impliqué par le PORT, par exemple :
- paramètre `null` ;
- libellé `null` ;
- libellé blank ;
- identifiant `null` ;
- objet métier non persistant ;
- parent `null` ;
- parent à libellé blank ;
- parent sans identifiant ;
- parent absent du stockage ;
- objet absent du stockage ;
- retour DAO `null` ;
- contenu de page `null` ;
- exception DAO avec message non null ;
- exception DAO avec message null ;
- doublon ou collision métier ;
- absence de modification ;
- modification de casse ;
- modification du parent ;
- cas nominal OK.

La liste des tests contractuels doit être déduite en une seule passe complète. L'IA ne doit pas découvrir progressivement de nouveaux cas qui étaient déjà déductibles du PORT et de l'ADAPTER relus.

### 5.8) Tests didactiques non contractuels

Après les tests contractuels, l'IA peut ajouter des tests didactiques non contractuels uniquement s'ils documentent un mécanisme réel et utile de l'ADAPTER Gateway.

Exemples acceptables :
- conversion `RequetePage` vers `Pageable` ;
- conversion des tris `TriSpec` vers `Sort` Spring ;
- filtrage des valeurs `null` ;
- tri métier ;
- dédoublonnage métier ;
- `appliquerModifications(...)` ;
- `safeEquals(...)` ;
- `isBlank(...)` ;
- `safeMessage(...)`.

Ces tests restent didactiques. Ils ne créent pas de nouveau contrat métier et ne remplacent jamais les tests publics contractuels.

### 5.9) Ordre des méthodes dans un bloc

Dans chaque bloc, l'ordre obligatoire est :
1. cas d'exception applicative ;
2. cas d'exception technique ;
3. cas alternatifs métier ;
4. cas nominaux OK.

Ordre type, à adapter à la méthode réelle :
- `Null` ;
- `LibelleNull` ;
- `LibelleBlank` ;
- `IdNull` ;
- `ParentNull` ;
- `ParentLibelleBlank` ;
- `ParentIdNull` ;
- `ParentAbsent` ;
- `DAORetourneNull` ;
- `ContenuNull` ;
- `DAOExceptionMessageNonNull` ;
- `DAOExceptionMessageNull` ;
- `DAOSaveRetourneNull` ;
- `DAOSaveExceptionMessageNonNull` ;
- `DAOSaveExceptionMessageNull` ;
- `Absent` ou `NonTrouve` ;
- `SansModification` ;
- `ModificationCasse` ;
- `ParentModifie` ;
- `Nominal`.

Le cas `Nominal` reste en fin de bloc.

### 5.10) Nommage des méthodes de test

Chaque méthode de test doit être nommée simplement selon la forme :

`test` + nom de la méthode testée + garantie simple du test.

Exemples :
- `testFindByIdNull()` ;
- `testFindByIdDAORetourneNull()` ;
- `testFindByIdDAOExceptionMessageNonNull()` ;
- `testFindByIdDAOExceptionMessageNull()` ;
- `testFindByIdNonTrouve()` ;
- `testFindByIdNominal()`.

Pour un Gateway avec parent :
- `testCreerParentNull()` ;
- `testCreerParentLibelleBlank()` ;
- `testCreerParentIdNull()` ;
- `testCreerParentAbsent()` ;
- `testCreerParentDAOExceptionMessageNonNull()` ;
- `testCreerParentDAOExceptionMessageNull()` ;
- `testCreerNominal()`.

Règles :
- utiliser `DAO` en majuscules ;
- utiliser `MessageNonNull` et `MessageNull` ;
- utiliser `StockageVide`, jamais `Zero` ;
- utiliser `Nominal` pour le dernier OK du bloc ;
- s'inspirer des fichiers déjà validés ;
- ne pas créer de nouvelle grammaire de nommage sans validation utilisateur.

### 5.11) Javadocs des méthodes de test

Chaque méthode de test doit avoir une javadoc HTML immédiatement placée avant les annotations.

Forme obligatoire :

```java
/**
 * <div>
 * <p>garantit que methode(cas) :</p>
 * <ul>
 * <li>premier comportement vérifié ;</li>
 * <li>deuxième comportement vérifié ;</li>
 * <li>dernier comportement vérifié.</li>
 * </ul>
 * </div>
 */
```

Règles :
- commencer par `garantit que ... :` ;
- utiliser `div`, `p`, `ul`, `li` ;
- chaque élément `<li>` doit se terminer par ` ;`, sauf le dernier élément `<li>` de la liste qui doit se terminer par `.` ;
- la règle de ponctuation porte sur l'élément HTML logique, même si son contenu est réparti sur plusieurs lignes physiques ;
- employer un vocabulaire simple ;
- rester proche du code réellement exécuté ;
- ne pas employer de termes vagues, décoratifs ou philosophiques.

Lorsque le scénario dépend d'une condition de mock Mockito, la javadoc doit privilégier la formulation conditionnelle qui décrit ce que le test prépare réellement.

Exemples à privilégier :
- `garantit que si DAO.findAll() retourne null :` ;
- `garantit que si DAO.findAll() retourne une liste vide :` ;
- `garantit que si DAO.findAll() jette une exception technique avec message null :` ;
- `garantit que si le DAO retourne une Page Spring non null contenant une liste vide :`.

Cette formulation est préférable à une formulation du type `garantit que methode(DAO...) :` lorsqu'elle décrit mieux la condition préparée par Mockito.

### 5.12) Annotations JUnit

Chaque méthode de test doit comporter, dans l'ordre local validé :
1. `@Tag(...)` ;
2. `@DisplayName("... : ...")` ;
3. `@Test`.

Le `@Tag` doit correspondre au bloc testé.

Le `@DisplayName` doit :
- reprendre la méthode testée ;
- contenir ` : ` ;
- décrire exactement ce que le test prouve ;
- distinguer `message non null` et `message null` ;
- mentionner `message sûr non null` lorsque la source technique a un message null ;
- mentionner `KO_STOCKAGE` lorsque le DAO retourne `null` ou une page à contenu `null` ;
- utiliser `OK` pour les cas nominaux, tout en conservant le nom de méthode `test...Nominal` lorsque ce nom est la convention validée.

### 5.13) Commentaires de bloc et commentaires historiques

Si l'IA code une correction, elle doit conserver au maximum les commentaires de bloc de l'utilisateur.

Règles :
- conserver les commentaires justes, utiles ou déjà validés ;
- ne les effacer que s'ils sont faux ;
- ne pas remplacer un commentaire utilisateur juste par un commentaire IA plus vague ;
- réutiliser les commentaires déjà validés dans le même fichier ;
- standardiser seulement si cela ne dégrade pas la précision.

Les commentaires doivent être simples, directs et proches du code.

Préférer :
- `appel DAO.findById(ID_1)` ;
- `DAO.save(...) retourne null` ;
- `aucun appel au DAO` ;
- `appel EntityManager.remove(entity)` ;
- `message sûr non null`.

Éviter :
- `sollicite la recherche par identifiant` ;
- `vérifie le comportement nominal global` ;
- `contrôle le bon fonctionnement du mécanisme`.

Précision obligatoire pour développeur tiers humain :
- ne pas écrire seulement `techniquement exploitable` ; préciser les éléments observables, par exemple `techniquement exploitable (numéro de page, taille, ...)` ;
- ne pas écrire seulement `requête neutre` ; préciser l'objet Java exact, par exemple `requête neutre (RequetePage sans aucun paramètre : new RequetePage())` ;
- ne pas écrire seulement `l'appel paginé` ; préciser l'appel réellement exécuté, par exemple `l'appel paginé DAO.findAll(Pageable)` ;
- ne pas écrire seulement `ce scénario paginé` ; préférer une formulation proche du code testé, par exemple `service.rechercherTousParPage(...) avec un DAO qui retourne un ResultatPage vide` ;
- plus généralement, lorsqu'un commentaire contient une formule abstraite, l'IA doit la compléter par les paramètres observables, l'appel Java exact ou le comportement Mockito réellement préparé.

Formalisme obligatoire pour les commentaires `ACT - ASSERT` qui vérifient un appel au SERVICE GATEWAY :
- lorsqu'un test vérifie une exception ou un retour contractuel à partir d'un appel au service, le commentaire doit nommer explicitement l'appel testé sous la forme `service.<methode>(...)` ;
- le commentaire doit ensuite préciser la condition métier ou technique exacte introduite par le test ;
- le commentaire doit indiquer l'exception attendue ou le retour attendu ;
- lorsque le PORT impose un message, le commentaire doit citer la constante de message vérifiée ;
- lorsque le cas est contractuel, le commentaire doit se terminer par `(contrat du port)`.

Forme à privilégier :

`vérifie que l'appel service.<methode>(...) avec <condition précise> jette <ExceptionAttendue> avec le message <CONSTANTE_MESSAGE> (contrat du port).`

Formes validées :
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier null jette une ExceptionAppliParamNull avec le message MSG_FINDBYOBJETMETIER_KO_PARAM_NULL (contrat du port).` ;
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier avec un libellé blank jette une ExceptionAppliLibelleBlank avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK (contrat du port).` ;
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier avec un parent null jette une ExceptionAppliParentNull avec le message MSG_FINDBYOBJETMETIER_KO_PARENT_NULL (contrat du port).` ;
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier dont le parent a un libellé blank jette une ExceptionAppliLibelleBlank avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK (contrat du port).` ;
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier dont le parent est non persistant car son identifiant est null jette une ExceptionTechniqueGatewayNonPersistent avec le message MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT complété par le libellé du parent (contrat du port).` ;
- `vérifie que l'appel service.findByObjetMetier(...) avec un objet métier dont le parent est absent du stockage jette une ExceptionTechniqueGatewayNonPersistent avec le message MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT complété par le libellé du parent (contrat du port).`.

Règles de rédaction associées :
- écrire `service.<methode>(...)` dans le commentaire, sans `this.`, pour garder une formulation didactique simple ;
- ne pas écrire seulement `vérifie que l'appel avec ...` si la méthode testée n'est pas nommée ;
- ne pas écrire seulement `vérifie que la méthode jette ...` si la condition exacte du test n'est pas nommée ;
- ne pas remplacer la condition métier précise par une formule vague comme `cas invalide`, `mauvais paramètre` ou `scénario KO` ;
- découper le commentaire sur plusieurs lignes si nécessaire, mais conserver l'ordre logique : appel testé, condition, exception ou retour, message, contrat.

Clause de tolérance :
- lorsqu'un commentaire existant a été validé historiquement par l'utilisateur, il est toléré même s'il est moins direct que le nouveau standard de commentaire, à condition qu'il soit juste et non trompeur ;
- l'IA doit signaler l'écart éventuel, mais ne doit pas remplacer ce commentaire sans demande explicite de codage ou sans fausseté avérée ;
- si le commentaire est faux, l'IA doit le corriger lorsque l'utilisateur demande de coder ;
- si le commentaire est juste mais moins direct, l'IA le conserve et signale seulement l'écart ;
- si le commentaire est juste et que l'utilisateur demande explicitement de coder la modernisation, l'IA peut le remplacer par un commentaire plus direct en respectant le style validé.

### 5.14) Structure ARRANGE / ACT / ASSERT

Chaque test doit rendre visibles les phases de test lorsque c'est possible :
- `ARRANGE` ;
- `ACT` ;
- `ASSERT`.

Variantes acceptées lorsque le test le justifie :
- `ARRANGE - ACT` puis `ASSERT` ;
- `ARRANGE - ACT - ASSERT`.

La structure doit servir la lisibilité et ne doit pas devenir artificielle.

### 5.15) Assertions et interactions Mockito

Les tests doivent utiliser AssertJ et Mockito de façon explicite.

Règles d'assertions :
- vérifier le résultat métier attendu ;
- vérifier le type exact d'exception ;
- vérifier le message attendu ;
- vérifier le message sûr non null lorsque le message source est null ;
- vérifier la cause propagée lorsque le scénario technique l'exige ;
- vérifier les invariants métier ou techniques utiles.

Règles Mockito :
- chaque `when(...)` doit correspondre à un appel réellement exécuté ;
- aucun stub inutile n'est toléré ;
- vérifier les appels attendus avec `verify(...)` ;
- vérifier les appels interdits avec `never()` ;
- utiliser `verifyNoInteractions(...)` lorsqu'aucun collaborateur ne doit être appelé ;
- ne pas utiliser `verifyNoMoreInteractions(...)`, sauf décision explicite future validée par l'utilisateur.

### 5.16) Collaborateurs réels

L'IA doit identifier les collaborateurs réels de l'ADAPTER Gateway avant de coder les tests.

Exemples :
- `TypeProduitGatewayJPAService` utilise `TypeProduitDaoJPA` uniquement ;
- `SousTypeProduitGatewayJPAService` peut utiliser `SousTypeProduitDaoJPA`, `TypeProduitDaoJPA` et `EntityManager` selon la méthode réelle ;
- `ProduitGatewayJPAService` peut utiliser `ProduitDaoJPA` et `SousTypeProduitDaoJPA` selon la méthode réelle.

Interdictions :
- ne jamais inventer un DAO parent absent de l'ADAPTER ;
- ne jamais inventer un `EntityManager` absent de l'ADAPTER ;
- ne jamais inventer un `flush()` si la méthode réelle ne l'appelle pas ;
- ne jamais inventer une vérification post-suppression si la méthode réelle ne l'implémente pas.

### 5.17) Adaptation au Gateway réel

La règle générique ne signifie pas copier mécaniquement `TypeProduitGatewayJPAServiceMockTest`.

`TypeProduitGatewayJPAServiceMockTest` sert de référence de formalisme, mais chaque Gateway doit être testé selon son PORT et son ADAPTER réel.

Pour `TypeProduit` :
- pas de parent ;
- pas d'`EntityManager` ;
- pas de `findAllByParent(...)` ;
- pas de `flush()` dans `delete(...)`.

Pour `SousTypeProduit` :
- parent `TypeProduit` réel ;
- DAO parent réel ;
- `findAllByParent(...)` contractuel ;
- `EntityManager` réel dans `delete(...)` si l'ADAPTER l'utilise ;
- vérification post-suppression réelle si l'ADAPTER l'implémente.

Pour `Produit` :
- parent `SousTypeProduit` réel ;
- DAO parent réel ;
- `findAllByParent(...)` contractuel ;
- suppression par DAO Produit ou autre collaborateur uniquement selon l'ADAPTER réel ;
- `flush()` seulement si la méthode réelle l'appelle.

### 5.18) Helpers de test

L'IA doit coder tous les helpers nécessaires au bon fonctionnement autonome des tests.

Règles :
- aucun helper manquant ;
- aucune méthode à compléter manuellement ;
- aucun snippet incomplet ;
- aucune dépendance implicite non livrée ;
- les helpers sont regroupés dans le bloc d'outils validé du fichier ;
- les helpers sont adaptés à l'objet métier testé.

Exemples :
- `fabriquerTypeProduit(...)` ;
- `fabriquerSousTypeProduit(...)` ;
- `fabriquerProduit(...)` ;
- `fabriquerTypeProduitJPA(...)` ;
- `fabriquerSousTypeProduitJPA(...)` ;
- `fabriquerProduitJPA(...)` ;
- `creerPage(...)` ;
- `invokePrivateMethod(...)`.

### 5.19) Tests des méthodes private

La réflexion est autorisée pour tester des méthodes `private` de l'ADAPTER seulement si cela est vraiment nécessaire.

Ces tests sont acceptables lorsqu'ils verrouillent un comportement réel utilisé par les méthodes publiques.

Règles :
- utiliser un helper unique de réflexion ;
- placer ces tests après les blocs publics ;
- ne pas remplacer les tests publics par les tests privés ;
- ne pas transformer un helper privé en nouveau contrat métier ;
- ne pas inventer de nouveaux critères à partir des tests privés.

### 5.20) Cohérence interne dans un même test Mockito Gateway

Dans un même test Mockito Gateway, les méthodes similaires doivent rester cohérentes entre elles.

La cohérence interne porte sur :
- le nommage ;
- la javadoc ;
- le `@DisplayName` ;
- les commentaires ;
- la structure `ARRANGE` / `ACT` / `ASSERT` ;
- le style Mockito ;
- le style AssertJ ;
- le niveau de détail des assertions ;
- le niveau de détail des vérifications d'interactions.

Exemple dans `TypeProduitGatewayJPAServiceMockTest.java` :
- `testCreerNull()` ;
- `testFindByObjetMetierNull()` ;
- `testFindByLibelleNull()`.

Ces méthodes ne vérifient pas nécessairement la même exception, mais leur formalisme doit rester homogène lorsque leur structure de test est comparable.

Règles :
- ne pas avoir une javadoc très détaillée pour une méthode similaire et une javadoc pauvre pour une autre ;
- ne pas varier inutilement les commentaires de phase ;
- ne pas varier inutilement le style AssertJ ou Mockito ;
- ne pas inventer une formulation nouvelle si une formulation validée existe déjà dans la classe ;
- adapter seulement ce qui dépend réellement du contrat de la méthode testée.

### 5.21) Cohérence transversale entre tests Mockito Gateway liés

Les méthodes similaires dans plusieurs tests Mockito Gateway portant sur des objets métier liés doivent rester cohérentes entre elles.

Objets métier liés dans le périmètre actuel :
- `TypeProduit` ;
- `SousTypeProduit` ;
- `Produit`.

Exemple de cohérence transversale :
- `testCreerNull()` dans `TypeProduitGatewayJPAServiceMockTest.java` ;
- `testCreerNull()` dans `SousTypeProduitGatewayJPAServiceMockTest.java` ;
- `testCreerNull()` dans `ProduitGatewayJPAServiceMockTest.java`.

La cohérence transversale porte sur :
- le nommage ;
- la javadoc ;
- le `@DisplayName` ;
- les commentaires ;
- la structure `ARRANGE` / `ACT` / `ASSERT` ;
- les assertions ;
- les interactions Mockito ;
- le niveau de détail ;
- l'ordre relatif des cas similaires dans le bloc.

Règles :
- reprendre le formalisme validé du test le plus proche ;
- adapter seulement aux différences réelles du PORT ;
- adapter seulement aux différences réelles de l'ADAPTER ;
- ne jamais copier un parent dans un Gateway qui n'en a pas ;
- ne jamais copier un `EntityManager` dans un Gateway qui ne l'utilise pas ;
- ne jamais copier un scénario absent du contrat réel ;
- signaler les asymétries de formalisme entre tests liés lorsqu'elles ne sont pas justifiées par le PORT ou l'ADAPTER.

### 5.22) Critères fixes de conformité d'une méthode de test

Une méthode de test Mockito Gateway est conforme seulement si tous les critères applicables sont respectés :
1. elle appartient au bon bloc PORT ;
2. elle est justifiée par le contrat, l'ADAPTER ou un besoin didactique réel ;
3. elle porte un nom simple et stable ;
4. elle est placée dans le bon ordre du bloc ;
5. elle possède une javadoc HTML conforme ;
6. elle possède `@Tag(...)` ;
7. elle possède `@DisplayName("... : ...")` ;
8. elle possède `@Test` ;
9. elle rend visibles les phases ARRANGE / ACT / ASSERT lorsque c'est possible ;
10. ses commentaires sont simples, proches du code, suffisamment précis pour un développeur tiers humain, ou historiquement validés et justes ;
11. elle vérifie le résultat métier attendu ;
12. elle vérifie le type exact d'exception attendu ;
13. elle vérifie le message ou le message sûr non null ;
14. elle vérifie la cause propagée lorsque c'est nécessaire ;
15. elle vérifie les interactions Mockito attendues ;
16. elle vérifie les interactions Mockito interdites ;
17. elle utilise `verifyNoInteractions(...)` lorsque rien ne doit être appelé ;
18. elle n'utilise pas `verifyNoMoreInteractions(...)` ;
19. elle ne crée pas de collaborateur inexistant ;
20. elle ne teste pas un comportement absent du PORT ou de l'ADAPTER ;
21. elle conserve les commentaires utilisateur justes ;
22. elle reste homogène avec les méthodes similaires du même fichier ;
23. elle reste homogène avec les méthodes similaires des tests Mockito Gateway liés ;
24. elle reste homogène avec les fichiers déjà validés.

Cette grille est stable. L'IA ne doit pas inventer de nouveaux critères de qualité à chaque passe.

### 5.23) Rapport attendu avant conclusion

Avant de conclure qu'un bloc est complet ou avant de coder une correction, l'IA doit produire ou avoir établi la matrice :

`cas du contrat -> test correspondant -> verdict`

Le rapport doit distinguer :
- cas contractuels couverts ;
- cas contractuels manquants ;
- cas didactiques utiles ;
- doublons fonctionnels inutiles ;
- asymétries entre variantes voisines ;
- asymétries internes dans un même test Mockito Gateway ;
- asymétries transversales entre tests Mockito Gateway liés ;
- collaborateurs réels vérifiés ;
- collaborateurs interdits vérifiés.

L'IA ne peut conclure `OK` que si tous les points détectables sont traités en une seule passe complète.

### 5.24) Interdictions absolues

L'IA ne doit jamais :
- coder sans message utilisateur contenant explicitement `coder` ;
- raisonner depuis la mémoire sans relecture ;
- copier mécaniquement TypeProduit dans SousTypeProduit ou Produit ;
- inventer un parent ;
- inventer un DAO ;
- inventer un `EntityManager` ;
- inventer un `flush()` ;
- inventer une vérification post-suppression ;
- déplacer les tests nominaux avant les KO ;
- fusionner `MessageNonNull` et `MessageNull` ;
- renommer `StockageVide` en `Zero` ;
- écrire `Dao` dans un nom de test là où `DAO` est validé ;
- effacer un commentaire utilisateur juste ;
- remplacer un commentaire précis ou historiquement validé par une formulation vague ;
- livrer un snippet incomplet ;
- changer de stratégie de conformité après validation utilisateur.

### 5.25) Formule opérationnelle

Pour tout test Mockito de Gateway, l'IA travaille méthode du PORT par méthode du PORT. Elle lit d'abord le contrat du PORT, puis l'ADAPTER réel, puis les dépendances et les tests validés. Elle déduit les tests contractuels, ajoute seulement les tests didactiques nécessaires, nomme simplement les méthodes, ordonne les cas en Exception / KO, alternatifs puis nominaux OK, conserve les commentaires utilisateur justes, tolère les commentaires historiques validés, précise les commentaires vagues par les paramètres observables, l'appel Java exact ou le comportement Mockito réellement préparé, structure ARRANGE / ACT / ASSERT, respecte la javadoc HTML validée, code tous les helpers nécessaires, teste les méthodes privées par réflexion seulement si utile, vérifie la cohérence interne et transversale, et applique toujours la même grille de conformité sans inventer de nouveaux critères.

### 5.26) Workflow rapide et verrouillé de contrôle Gateway Mockito

Le contrôle d’un bloc de tests Mockito Gateway doit être rapide, stable et reproductible lorsque la fenêtre active est installée.

Lorsque le SHA courant est consolidé et que la fenêtre active contient le périmètre Gateway complet, l’IA travaille directement depuis la fenêtre active. Elle ne relit pas GitHub à chaque contrôle de bloc, sauf nouveau SHA ou demande explicite de l’Utilisateur.

Préflight obligatoire avant tout verdict :

1. relire le présent contrat local Gateway ;
2. relire les règles stables de contrôle applicables ;
3. relire le contrat du PORT de la méthode contrôlée ;
4. relire la méthode correspondante dans l’ADAPTER réel ;
5. relire les dépendances utiles strictement nécessaires ;
6. relire le bloc de test cible ;
7. établir en interne la matrice `cas du contrat -> test correspondant -> verdict` ;
8. établir en interne la whitelist des critères autorisés.

La whitelist des critères autorisés est strictement limitée :
- aux critères inscrits dans `CONTRAT_IA.md` ;
- aux critères inscrits dans le présent contrat local Gateway ;
- aux cas du PORT ;
- au comportement réel de l’ADAPTER ;
- aux formalismes locaux déjà validés ;
- aux corrections utilisateur relues et consolidées.

Tout critère absent de cette whitelist est interdit comme défaut bloquant.

L’IA ne doit pas imposer comme obligatoire, sauf preuve explicite dans la whitelist :
- un nouveau cas de test ;
- un nouvel `ArgumentCaptor` ;
- une nouvelle variante DAO ;
- une nouvelle vérification d’`EntityManager` ;
- une nouvelle vérification Mockito plus forte ;
- une nouvelle assertion ;
- une nouvelle formulation de commentaire ;
- un nouveau formalisme de nommage.

Ces éléments peuvent seulement être signalés comme améliorations optionnelles, ou faire l’objet d’une proposition de modification du contrat.

Workflow utilisateur obligatoire :

1. l’Utilisateur demande : `contrôle le bloc xxx` ou `vérifier bloc xxx` ;
2. l’IA effectue le préflight et le contrôle en interne ;
3. si le bloc est parfait, l’IA répond uniquement : `bloc OK` ;
4. sinon, l’IA liste uniquement les corrections obligatoires à faire ;
5. l’IA ne code rien tant que l’Utilisateur n’a pas écrit explicitement `coder` ;
6. après `coder`, l’IA livre le bloc ou la méthode complète directement dans le chat ;
7. l’Utilisateur intègre dans STS et exécute les tests ;
8. si les tests passent verts, l’Utilisateur transmet le fichier Java corrigé ;
9. l’IA relit le dernier fichier joint réel ;
10. l’IA vérifie que les corrections sont intégrées ;
11. l’IA recontrôle automatiquement le même bloc avec la même whitelist ;
12. si tout est conforme, l’IA répond uniquement : `bloc OK`.

Interdiction absolue :
- ne jamais réauditer un bloc avec une grille différente de celle utilisée à la passe précédente, sauf changement explicite des contrats ;
- ne jamais ajouter de nouveaux défauts bloquants après coup si ces défauts ne sont pas issus des critères stables déjà relus ;
- ne jamais confondre amélioration optionnelle et correction obligatoire ;
- ne jamais donner un verdict si le préflight est incomplet.

Réponse obligatoire en cas de préflight incomplet :

`contrôle impossible — lecture/preflight incomplet`


## 6) RT-CODAGE-TEST-INTEGRATION-GATEWAY-01 — Codage et contrôle des tests d'intégration Gateway

### 6.1) Objet

Cette règle définit la méthode obligatoire de l'IA pour analyser, auditer, corriger ou coder les tests d'intégration des SERVICE GATEWAY du projet PatrimoineSSP.

Elle s'applique à tous les tests d'intégration Gateway, notamment :
- `TypeProduitGatewayJPAServiceIntegrationTest.java` ;
- `SousTypeProduitGatewayJPAServiceIntegrationTest.java` ;
- `ProduitGatewayJPAServiceIntegrationTest.java` ;
- tout futur test d'intégration Gateway équivalent.

Les tests Mockito Gateway validés restent la référence pour la complétude technique fine.
Les tests d'intégration Gateway ne les remplacent pas : ils prouvent le comportement réel Spring/JPA/H2 et l'état observable du stockage.

### 6.2) Source de vérité

Avant toute analyse, audit, validation, correction ou génération de code sur un test d'intégration Gateway, l'IA doit travailler strictement sur pièces.

Ordre obligatoire :
1. lire le présent contrat local Gateway ;
2. lire le contrat du PORT Gateway concerné ;
3. lire la méthode correspondante dans l'ADAPTER Gateway réel ;
4. lire les dépendances utiles à la compréhension réelle ;
5. lire le test d'intégration cible ;
6. lire les tests d'intégration de référence déjà validés si le formalisme local doit être confirmé ;
7. tenir compte des tests Mockito Gateway validés pour ne pas exiger à nouveau en intégration des branches techniques déjà prouvées.

Règle centrale :
- le PORT définit les cas contractuels ;
- l'ADAPTER Gateway définit le comportement réel attendu ;
- le test Mockito Gateway validé verrouille les branches techniques internes ;
- le test d'intégration Gateway prouve le comportement observable réel, l'écriture, la lecture, la non-écriture, la suppression et la cohérence du stockage ;
- l'IA ne doit jamais raisonner de mémoire, ni par analogie non relue.

### 6.3) Différence de rôle entre Mockito et intégration

Un test Mockito Gateway vérifie finement :
- les collaborateurs exacts ;
- les interactions exactes ;
- les retours DAO `null` ;
- les exceptions DAO avec message non null et message null ;
- les conversions et branches techniques internes ;
- les non-interactions Mockito.

Un test d'intégration Gateway vérifie prioritairement :
- le contrat public du PORT ;
- le comportement réel Spring/JPA/H2 ;
- l'état physique observable du stockage ;
- la cohérence entre le service et la lecture SQL directe ;
- les écritures réelles ;
- les absences d'écriture ;
- les suppressions réelles ;
- l'isolation des tests ;
- la conservation des données seedées.

L'IA ne doit donc pas exiger, dans un test d'intégration, toutes les variantes techniques déjà verrouillées en Mockito, sauf si le PORT, l'ADAPTER ou un comportement observable réel du stockage l'impose.

### 6.4) Structure complète d'une classe d'intégration Gateway

Un test d'intégration Gateway complet doit :
- utiliser une configuration Spring/JPA réelle adaptée au périmètre testé ;
- utiliser le SERVICE GATEWAY réel ;
- utiliser les DAO réels via le contexte JPA ;
- utiliser `JdbcTemplate` ou une lecture SQL directe équivalente lorsque la preuve physique du stockage est nécessaire ;
- utiliser les scripts SQL de préparation nécessaires ;
- éviter les mocks Mockito pour les collaborateurs du Gateway testé ;
- contrôler uniquement les effets réellement observables en intégration ;
- ne pas dépendre d'un ordre implicite non prouvé par le test.

### 6.5) Blocs à contrôler

Les blocs de tests d'intégration suivent les méthodes du PORT Gateway.

Ordre de référence pour `TypeProduitGatewayIService` :
- `creer(...)` ;
- `rechercherTous()` ;
- `rechercherTousParPage(...)` ;
- `findByObjetMetier(...)` ;
- `findByLibelle(...)` ;
- `findByLibelleRapide(...)` ;
- `findById(...)` ;
- `update(...)` ;
- `delete(...)` ;
- `count()`.

Ordre de référence pour les Gateways avec parent métier :
- `creer(...)` ;
- `rechercherTous()` ;
- `rechercherTousParPage(...)` ;
- `findByObjetMetier(...)` ;
- `findByLibelle(...)` ;
- `findByLibelleRapide(...)` ;
- `findAllByParent(...)` ;
- `findById(...)` ;
- `update(...)` ;
- `delete(...)` ;
- `count()`.

Le PORT décide des blocs. L'IA ne doit pas inventer un bloc absent du PORT.

### 6.6) Critères contractuels d'intégration

Pour chaque bloc, l'IA doit contrôler au minimum les catégories applicables suivantes :
- cas applicatifs principaux du PORT (`null`, libellé `null`, libellé blank, identifiant `null`, parent `null`, parent à libellé blank, parent sans identifiant) ;
- cas parent absent du stockage lorsque le Gateway possède un parent métier ;
- cas non trouvé ;
- cas nominal ;
- cas stockage vide lorsque le comportement public doit rester non null et exploitable ;
- cas pagination par défaut, pagination explicite, tri, page vide, page hors bornes et taille normalisée lorsque la méthode est paginée ;
- cas écriture réelle pour `creer(...)` ;
- cas absence d'altération pour les échecs applicatifs ;
- cas modification réelle et absence de modification pour `update(...)` ;
- cas suppression réelle, identifiant absent et double suppression lorsque le PORT l'autorise ;
- cohérence de `count()` avec la lecture SQL directe et avec les méthodes publiques de lecture.

Cette liste est une grille stable. Elle doit être adaptée aux différences réelles entre `TypeProduit`, `SousTypeProduit` et `Produit`, sans copier mécaniquement un cas qui n'a pas de sens pour le PORT contrôlé.

### 6.7) Preuve physique du stockage

Lorsqu'un test d'intégration prétend prouver une écriture, une non-écriture, une modification, une suppression ou une absence de résultat, il doit s'appuyer sur une preuve observable.

Preuves attendues selon le cas :
- lecture SQL directe du nombre de lignes avant/après ;
- lecture SQL directe de l'identifiant créé ou ciblé ;
- lecture SQL directe du libellé ou des colonnes métier utiles ;
- comparaison avec la méthode publique du service ;
- contrôle que les données seedées restent présentes ;
- contrôle que la ligne créée est nettoyée ;
- contrôle que la ligne supprimée n'est plus retrouvable.

Les helpers historiques dont le nom contient `EnBase` sont tolérés lorsqu'ils existent déjà et qu'ils ont été validés, mais les nouveaux commentaires, Javadocs et DisplayName doivent employer le vocabulaire `stockage`.

### 6.8) Transactions, scripts SQL et nettoyage

Un test d'intégration Gateway qui crée, modifie ou supprime physiquement une donnée dédiée au test doit garantir l'isolation.

Règles :
- utiliser les scripts SQL de préparation déclarés par la classe ;
- utiliser `@Transactional(propagation = Propagation.NOT_SUPPORTED)` lorsque le test doit prouver une écriture ou une suppression réellement observable hors transaction de test ;
- nettoyer explicitement en `finally` toute ligne créée lorsque l'isolation ne dépend pas uniquement du script SQL de préparation ;
- restaurer explicitement en `finally` toute donnée seedée modifiée ;
- prévoir un nettoyage défensif lorsque le test peut échouer après une écriture réelle ;
- ne pas interroger JPA après un échec transactionnel si le contexte peut être marqué en erreur ; privilégier alors une lecture SQL directe.

### 6.9) Javadocs, commentaires et vocabulaire

Chaque méthode de test d'intégration Gateway doit posséder une javadoc HTML immédiatement placée avant les annotations.

Règles :
- commencer par `garantit que ... :` ou par une formulation conditionnelle équivalente lorsque le scénario est plus clair ainsi ;
- lister uniquement les garanties observables ;
- décrire le comportement réel du service et du stockage ;
- expliquer les lectures SQL directes lorsqu'elles servent de preuve indépendante du contexte Hibernate ;
- conserver les commentaires utilisateur justes ;
- réutiliser le formalisme local déjà validé ;
- ne pas remplacer un commentaire précis par une formulation plus vague.

Vocabulaire obligatoire :
- utiliser `stockage` dans les Javadocs, commentaires et nouveaux DisplayName ;
- éviter les formulations qui masquent le geste réel : préciser l'appel Java, la lecture SQL, l'écriture, la suppression, la restauration ou le nettoyage.

Formalisme validé :
- le nom de méthode peut utiliser `Nominal` lorsque ce nom est le formalisme local validé ;
- le `@DisplayName` peut conserver une constante contenant `OK`, par exemple `@DisplayName(DN_CREER_OK)` ;
- `testCreerNominal()` avec `@DisplayName(DN_CREER_OK)` est conforme.

### 6.10) Ordre des méthodes dans un bloc d'intégration

Dans chaque bloc d'intégration, l'ordre cible est :
1. cas d'exception applicative ;
2. cas d'absence physique ou non trouvé ;
3. cas alternatifs observables ;
4. cas de stockage vide ou pagination particulière lorsque pertinent ;
5. cas nominaux ;
6. cas d'écriture multiple ou scénario enrichi lorsque ce cas complète le nominal.

Le cas nominal principal reste placé après les KO du bloc.
Les scénarios enrichis comme créations multiples, double suppression ou création puis suppression peuvent suivre le nominal s'ils documentent une preuve d'intégration complémentaire.

### 6.11) Critères fixes de conformité d'une méthode d'intégration Gateway

Une méthode de test d'intégration Gateway est conforme seulement si tous les critères applicables sont respectés :
1. elle appartient au bon bloc PORT ;
2. elle est justifiée par le contrat, par le comportement réel de l'ADAPTER ou par une preuve d'intégration utile ;
3. elle porte un nom simple et stable ;
4. elle est placée dans le bon ordre du bloc ;
5. elle possède une javadoc HTML conforme ;
6. elle possède `@Tag(...)` ;
7. elle possède `@DisplayName(...)` ;
8. elle possède `@Test` ;
9. elle rend visibles les phases ARRANGE / ACT / ASSERT lorsque c'est possible ;
10. elle vérifie le résultat métier ou l'exception publique attendue ;
11. elle vérifie l'état observable du stockage lorsque le scénario l'exige ;
12. elle vérifie l'absence d'altération du stockage lorsque le scénario l'exige ;
13. elle nettoie ou restaure les données créées ou modifiées lorsque nécessaire ;
14. elle reste homogène avec les méthodes similaires du même fichier ;
15. elle reste homogène avec les méthodes similaires des tests d'intégration Gateway liés ;
16. elle ne réexige pas les branches techniques internes déjà prouvées en Mockito, sauf nécessité observable.

Cette grille est stable. L'IA ne doit pas inventer de nouveaux critères de qualité à chaque passe.

### 6.12) Homogénéité entre les trois tests d'intégration Gateway

La comparaison transversale doit porter sur :
- l'ordre des blocs ;
- la présence des méthodes contractuelles équivalentes ;
- les écarts justifiés par la structure métier ;
- la cohérence des noms de méthodes ;
- la cohérence des Javadocs ;
- la cohérence des `@DisplayName` ;
- l'usage homogène de `JdbcTemplate` ;
- l'usage homogène de `@Sql` ;
- l'usage homogène de `@Transactional(propagation = Propagation.NOT_SUPPORTED)` pour les écritures physiques ;
- le nettoyage physique défensif en `finally` ;
- le vocabulaire `stockage`.

Écarts structurels justifiés :
- `TypeProduit` est racine et ne possède pas de parent métier ;
- `SousTypeProduit` possède un parent `TypeProduit` ;
- `Produit` possède un parent `SousTypeProduit` ;
- un test lié au parent ne doit pas être copié dans `TypeProduit` ;
- une preuve liée à un collaborateur absent ne doit pas être inventée.

### 6.13) Workflow rapide et verrouillé de contrôle Gateway intégration

Lorsque le SHA courant est consolidé et que la fenêtre active contient le périmètre Gateway complet, l'IA travaille directement depuis la fenêtre active. Elle ne relit pas GitHub à chaque contrôle de bloc, sauf nouveau SHA ou demande explicite de l'Utilisateur.

Préflight obligatoire avant tout verdict :
1. relire le présent contrat local Gateway ;
2. relire les règles stables de contrôle applicables ;
3. relire le contrat du PORT de la méthode contrôlée ;
4. relire la méthode correspondante dans l'ADAPTER réel ;
5. relire les dépendances utiles strictement nécessaires ;
6. relire le bloc de test d'intégration cible ;
7. établir en interne la matrice `cas du contrat -> test correspondant -> verdict` ;
8. établir en interne la whitelist des critères autorisés.

La whitelist des critères autorisés est strictement limitée :
- aux critères inscrits dans `CONTRAT_IA.md` ;
- aux critères inscrits dans le présent contrat local Gateway ;
- aux cas du PORT ;
- au comportement réel de l'ADAPTER ;
- au comportement observable du stockage ;
- aux formalismes locaux déjà validés ;
- aux corrections utilisateur relues et consolidées.

Tout critère absent de cette whitelist est interdit comme défaut bloquant.

### 6.14) Workflow après tests verts STS

Après tests verts STS et fichier d'intégration corrigé joint par l'Utilisateur, l'IA doit automatiquement :
1. prendre le fichier joint comme dernier état réel STS ;
2. le relire directement ;
3. comparer avec la baseline consolidée précédente ;
4. détecter les corrections utilisateur ;
5. apprendre le formalisme corrigé ;
6. consolider la baseline et la fenêtre active ;
7. relancer automatiquement le contrôle complet du bloc ou de la méthode concernée avec la même whitelist ;
8. répondre uniquement avec le verdict du recontrôle et les informations de consolidation utiles.

Interdiction absolue :
- ne jamais s'arrêter à la consolidation sans recontrôler le bloc impacté ;
- ne jamais réauditer le bloc avec une grille différente ;
- ne jamais introduire de nouveau défaut bloquant après coup si ce défaut ne provient pas de la whitelist stable ;
- ne jamais confondre amélioration optionnelle et correction obligatoire.

Réponse obligatoire en cas de préflight incomplet :

`contrôle impossible — lecture/preflight incomplet`


### 6.15) RT-REFERENCE-PACKAGE-GATEWAY-IMPL-VALIDEE-01 — Package validé comme référence

Le package suivant est validé comme référence officielle pour les tests Gateway `produittype` :

`src/test/java/levy/daniel/application/model/services/produittype/gateway/impl`

Référence validée au SHA : `dc478be7a24b69d41072fbcf210126f922526090`.

Répartition validée :

| Fichier | Rôle | Tests |
|---|---:|---:|
| `ProduitGatewayJPAServiceIntegrationTest.java` | Produit intégration | 74 |
| `ProduitGatewayJPAServiceMockTest.java` | Produit Mockito | 114 |
| `SousTypeProduitGatewayJPAServiceIntegrationTest.java` | SousTypeProduit intégration | 75 |
| `SousTypeProduitGatewayJPAServiceMockTest.java` | SousTypeProduit Mockito | 123 |
| `TypeProduitGatewayJPAServiceIntegrationTest.java` | TypeProduit intégration | 51 |
| `TypeProduitGatewayJPAServiceMockTest.java` | TypeProduit Mockito | 102 |

Total validé : 539 tests.

Cette référence devient la source de formalisme pour les autres tests Gateway, sans remplacer l'obligation première de lire le PORT, l'ADAPTER et les dépendances utiles.

### 6.16) Références spécialisées par structure métier

Pour générer ou contrôler une classe de test Gateway, l'IA doit choisir la référence spécialisée pertinente :

- `TypeProduit` : objet métier racine sans parent métier ;
- `SousTypeProduit` : objet métier avec parent `TypeProduit` ;
- `Produit` : objet métier avec parent `SousTypeProduit` et graphe complet `Produit -> SousTypeProduit -> TypeProduit` ;
- test Mockito : preuve fine des branches techniques, exceptions DAO, retours `null`, interactions, `verify(...)`, `verifyNoInteractions(...)` ;
- test d'intégration : preuve observable Spring/JPA/H2, scripts SQL, `JdbcTemplate`, transactions, nettoyage et cohérence du stockage.

L'IA doit adapter le modèle de référence à la structure métier réelle. Elle ne doit pas copier un cas parent dans `TypeProduit`, ni inventer un collaborateur absent.

### 6.17) Matrice autonome de génération complète

Pour réécrire une classe complète de tests Gateway en autonomie, l'IA doit produire et contrôler la matrice suivante avant de coder :

| Élément | Question obligatoire |
|---|---|
| PORT | Quelles méthodes contractuelles existent ? |
| Cas contractuels | Quels paramètres `null`, blank, non persistants, absents, nominaux, pagination, suppression, count sont spécifiés ? |
| ADAPTER | Quels collaborateurs réels sont appelés et dans quel ordre observable ? |
| Métier | Quelle est la clé métier réelle ? |
| Parent | L'objet a-t-il un parent métier ? Le parent doit-il être complet ou seulement persistant ? |
| Mockito | Quels retours DAO, exceptions techniques, interactions et absences d'interactions doivent être prouvés ? |
| Intégration | Quelles preuves SQL directes et quels nettoyages sont nécessaires ? |
| Commentaires | La méthode explique-t-elle concrètement ce qu'elle arrange, agit et vérifie ? |
| Homogénéité | La méthode reste-t-elle alignée avec le package validé ? |

L'IA doit ensuite coder dans l'ordre : constantes utiles, attributs, configuration, blocs de tests dans l'ordre du PORT, helpers utiles, tests de sanity/invariants si nécessaires.

### 6.18) Commentaires didactiques obligatoires dans tous les tests Gateway

Chaque méthode `@Test` Gateway doit contenir :

- une Javadoc HTML immédiatement avant les annotations ;
- des commentaires de corps expliquant les phases `ARRANGE`, `ACT`, `ASSERT` ou leur équivalent ;
- une explication concrète des mocks configurés ;
- une explication concrète des lectures SQL directes ;
- une explication du nettoyage/restauration lorsqu'un test modifie réellement le stockage ;
- une explication de la clé métier lorsque le test la manipule ;
- une explication du graphe complet lorsque le test vérifie un parent ou un grand-parent.

Une méthode qui possède une Javadoc mais aucun commentaire didactique dans le corps n'est pas conforme pour un test Gateway complet.

### 6.19) Acceptations lexicales stabilisées

Les acceptations suivantes sont stabilisées :

- `PERSISTANT` et `PERSISTENT` sont tous deux autorisés ;
- `wipe` est autorisé si la formulation explique que les données seedées sont conservées ;
- `béton` est autorisé si l'intitulé ou le commentaire explique le rôle du test, par exemple `TESTS BETON (sanity / invariants)`.

Ces points ne doivent pas être signalés comme résidus actionnables lorsqu'ils sont expliqués. L'IA doit se concentrer sur les défauts de contrat, de comportement, de couverture, de compilation, de preuve ou de commentaires réellement insuffisants.

### 6.20) Rejeu automatique après consolidation de fichiers joints

Après tests verts STS et fichiers Gateway joints par l'Utilisateur, l'IA doit automatiquement :

1. relire chaque dernier `file_id` joint ;
2. contrôler les métriques et le contenu réel ;
3. détecter les corrections utilisateur ;
4. mémoriser le formalisme corrigé ;
5. consolider baseline et fenêtre active ;
6. produire un rapport de consolidation ;
7. rejouer le contrôle strict du package validé lorsque c'est la dernière commande ayant produit des corrections.

Commande de rejeu de référence :

`contrôle strict de l'ensemble des tests situés dans le package src/test/java/levy/daniel/application/model/services/produittype/gateway/impl`

## 7) Définition de la sacralisation

La sous-couche `couche_services.gateway` est considérée sacralisée lorsque :
- le présent contrat local est présent ;
- le périmètre IA référence exactement les fichiers de cette sous-couche ;
- les contrats locaux Gateway sont présents dans les fichiers IA ;
- les contrats locaux JPA Gateway existent pour `Produit`, `SousTypeProduit` et `TypeProduit` ;
- les tests Gateway et pagination sont dans le périmètre validé ;
- la séparation avec `couche_services.uc` est explicite ;
- la séparation avec `couche_dto` est explicite ;
- la règle `RT-CODAGE-TEST-MOCKITO-GATEWAY-01` est appliquée avant toute analyse, audit, correction ou génération de test Mockito Gateway ;
- la règle `RT-CODAGE-TEST-INTEGRATION-GATEWAY-01` est appliquée avant toute analyse, audit, correction ou génération de test d'intégration Gateway.

## 8) Exclusions explicites

Ne font pas partie de `couche_services.gateway` :
- les services UC ;
- les DTO ;
- les convertisseurs DTO ;
- les exceptions de `exceptionsservices` ;
- les controllers ;
- les vues.
