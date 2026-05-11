# Contrat local — TypeProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

## 2) Rôle du port

`TypeProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `TypeProduit`.

Ce port :
- manipule exclusivement des objets métier ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur ;
- expose un contrat technique destiné à être consommé par la couche SERVICE UC.

## 3) Dépendances de contrat

### 3.1) Objet métier principal

- `levy.daniel.application.model.metier.produittype.TypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<TypeProduit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionTechniqueGateway`

## 4) Signatures exactes du port

```java
TypeProduit creer(TypeProduit pObject) throws Exception;

List<TypeProduit> rechercherTous() throws Exception;

ResultatPage<TypeProduit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

TypeProduit findByObjetMetier(TypeProduit pObject) throws Exception;

TypeProduit findByLibelle(String pLibelle) throws Exception;

List<TypeProduit> findByLibelleRapide(String pContenu) throws Exception;

TypeProduit findById(Long pId) throws Exception;

TypeProduit update(TypeProduit pObject) throws Exception;

void delete(TypeProduit pObject) throws Exception;

long count() throws Exception;
```

## 5) Contrat technique à retenir

### 5.1) Création / update / delete

Le port expose les opérations de persistance technique suivantes :
- création technique via `creer(TypeProduit)` ;
- modification technique via `update(TypeProduit)` ;
- suppression technique via `delete(TypeProduit)`.

Le port documente explicitement des cas d'erreur applicative côté Gateway :
- paramètre `null` ;
- libellé blank ;
- objet non persistant lorsque l'ID est obligatoire.

### 5.2) Recherche

Le port expose :
- la recherche exhaustive via `rechercherTous()` ;
- la recherche paginée via `rechercherTousParPage(RequetePage)` ;
- la recherche par objet métier via `findByObjetMetier(TypeProduit)` ;
- la recherche par libellé exact via `findByLibelle(String)` ;
- la recherche rapide via `findByLibelleRapide(String)` ;
- la recherche par identifiant via `findById(Long)`.

### 5.3) Comptage

Le port expose `count()` comme opération technique de comptage du stockage.

## 6) Règles d'architecture à respecter

- aucune dépendance DTO ;
- aucune logique UC ;
- aucune journalisation utilisateur ;
- aucune responsabilité de présentation ;
- aucun Raw Type dans les signatures ;
- la pagination est contractuelle via `ResultatPage<TypeProduit>`.

## 7) Fichiers à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie du PORT `TypeProduitGatewayIService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java` — 35734 octets, 1026 lignes LF, SHA-256 `5d210a35d477fd3f3bffd1f51a2020fc8c539d78b8571139dbaaa05b9ddc57f3`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java` — 33209 octets, 1158 lignes LF, SHA-256 `bca2ae80b8abc42dee1668fba19eb62bf5ca57a381c005aaa31877a2c272db6f`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java` — 262556 octets, 7057 lignes LF, SHA-256 `4ea611289c3c6d35416d18d0b3cef5f68fce0ae37086660adf1d8dd8abf405b6`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java` — 147931 octets, 4230 lignes LF, SHA-256 `b1028b48d4ad98325aad9e13c385b8ec8878fa64f41da4a85124828a27ac806c`

Ce PORT ne doit jamais être recodé de mémoire. Pour toute analyse ou génération, l'IA doit relire dans cet ordre : `CONTRAT_IA.md`, `CoucheServicesGateway.md`, le présent contrat local, le PORT Java, l'ADAPTER JPA, puis les tests Mock et intégration.

### 99.2) Rôle exact à conserver

`TypeProduitGatewayIService` porte le contrat technique Gateway de `TypeProduit`, sans parent métier.

Le PORT porte les messages techniques Gateway utilisés par les ADAPTERS et les tests. Ces constantes ne sont pas décoratives : elles verrouillent le texte des exceptions et doivent être conservées dans leur logique, leur nommage et leur ordre.

### 99.3) Constantes contractuelles exactes du PORT

```java
	String ANOMALIE_APPLICATIVE = "Anomalie applicative ";
	String OBJET_METIER_PARAM_NULL
		= "- l'objet métier passé en paramètre est null.";
	String CONTENU_PARAM_NULL
		= "- le contenu passé en paramètre est null.";
	String ID_PARAM_NULL
		= "- l'ID passé en paramètre est null.";
	String OBJET_METIER_A
		= "- l'objet métier passé en paramètre a un ";
	String LIBELLE_BLANK = "libellé blank (null ou que des espaces).";
	String MESSAGE_CREER_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_CREER_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;
	String MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL =
			ANOMALIE_APPLICATIVE
			+ "- le paramètre pObject ne doit pas être null.";
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
			ANOMALIE_APPLICATIVE
			+ "- le libellé de pObject passé en paramètre "
			+ "ne doit pas être blank.";
	String MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le libellé passé en paramètre est blank "
			+ "(null ou que des espaces).";
	String MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ CONTENU_PARAM_NULL;
	String MESSAGE_FINDBYID_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ ID_PARAM_NULL;
	String MESSAGE_UPDATE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_UPDATE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;
	String MESSAGE_UPDATE_KO_NON_PERSISTENT
		= "l'objet que vous voulez modifier n'est pas persistant "
			+ "(ID null) : ";
	String MESSAGE_DELETE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String ID_NULL = "ID null.";
	String MESSAGE_DELETE_KO_ID_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ ID_NULL;
	String MESSAGE_DELETE_KO_NON_PERSISTENT
		= "l'objet que vous voulez détruire n'existait "
			+ "pas dans le stockage : ";
	String ERREUR_TECHNIQUE_STOCKAGE
		= "Erreur Technique lors du stockage : ";
	String ERREUR_TECHNIQUE_KO_STOCKAGE
		= "Erreur Technique - Le stockage a retourné null.";
```

Règle stricte : ne pas remplacer ces constantes par des messages inline, ne pas changer leur préfixe `MESSAGE_`, ne pas supprimer les constantes de fragments, ne pas déplacer les messages vers la couche UC ou Controller.

### 99.4) Signatures et ordre exacts des méthodes du PORT

```java
TypeProduit creer(TypeProduit pObject) throws Exception
List<TypeProduit> rechercherTous() throws Exception
ResultatPage<TypeProduit> rechercherTousParPage(RequetePage pRequetePage) throws Exception
TypeProduit findByObjetMetier(TypeProduit pObject) throws Exception
TypeProduit findByLibelle(String pLibelle) throws Exception
List<TypeProduit> findByLibelleRapide(String pContenu) throws Exception
TypeProduit findById(Long pId) throws Exception
TypeProduit update(TypeProduit pObject) throws Exception
void delete(TypeProduit pObject) throws Exception
long count() throws Exception
```

L'ordre des blocs est contractuel : `creer`, `rechercherTous`, `rechercherTousParPage`, `findByObjetMetier`, `findByLibelle`, `findByLibelleRapide`, éventuel `findAllByParent`, `findById`, `update`, `delete`, `count`.

### 99.5) Tests de référence à préserver

#### Test Mock
- ligne 328 : `testCreerNull`
- ligne 361 : `testCreerLibelleBlank`
- ligne 398 : `testCreerDAOSaveRetourneNull`
- ligne 456 : `testCreerDAOSaveExceptionMessageNonNull`
- ligne 528 : `testCreerDAOSaveExceptionMessageNull`
- ligne 604 : `testCreerDoublon`
- ligne 689 : `testCreerNominal`
- ligne 761 : `testRechercherTousDAORetourneNull`
- ligne 806 : `testRechercherTousDAORetourneVide`
- ligne 856 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 924 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1003 : `testRechercherTousTriDedoublonnage`
- ligne 1078 : `testRechercherTousNominal`
- ligne 1164 : `testRechercherTousParPageNull`
- ligne 1276 : `testRechercherTousParPageDAORetourneNull`
- ligne 1339 : `testRechercherTousParPageContenuNull`
- ligne 1409 : `testRechercherTousParPageDAOExceptionMessageNonNull`
- ligne 1498 : `testRechercherTousParPageDAOExceptionMessageNull`
- ligne 1579 : `testRechercherTousParPagePageSizeZero`
- ligne 1714 : `testRechercherTousParPageAvecTri`
- ligne 1865 : `testRechercherTousParPageContenuAvecNulls`
- ligne 1967 : `testRechercherTousParPagePageVide`
- ligne 2053 : `testRechercherTousParPageNominalRequeteNeutre`
- ligne 2157 : `testRechercherTousParPageNominal`
- ligne 2278 : `testFindByObjetMetierNull`
- ligne 2311 : `testFindByObjetMetierLibelleBlank`
- ligne 2353 : `testFindByObjetMetierDAOExceptionMessageNonNull`
- ligne 2425 : `testFindByObjetMetierDAOExceptionMessageNull`
- ligne 2504 : `testFindByObjetMetierNonTrouve`
- ligne 2565 : `testFindByObjetMetierNominal`
- ligne 2628 : `testFindByLibelleNull`
- ligne 2661 : `testFindByLibelleBlank`
- ligne 2696 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 2768 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 2847 : `testFindByLibelleNonTrouve`
- ligne 2902 : `testFindByLibelleNominal`
- ligne 2959 : `testFindByLibelleRapideNull`
- ligne 2994 : `testFindByLibelleRapideBlank`
- ligne 3063 : `testFindByLibelleRapideDAORetourneNull`
- ligne 3117 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 3190 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 3269 : `testFindByLibelleRapideNonTrouve`
- ligne 3335 : `testFindByLibelleRapideNominal`
- ligne 3429 : `testFindByIdNull`
- ligne 3468 : `testFindByIdDAORetourneNull`
- ligne 3517 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 3581 : `testFindByIdDAOExceptionMessageNull`
- ligne 3646 : `testFindByIdNonTrouve`
- ligne 3703 : `testFindByIdNominal`
- ligne 3765 : `testUpdateNull`
- ligne 3797 : `testUpdateLibelleNull`
- ligne 3832 : `testUpdateLibelleBlank`
- ligne 3868 : `testUpdateIdNull`
- ligne 3906 : `testUpdateDAOFindByIdRetourneNull`
- ligne 3973 : `testUpdateAbsent`
- ligne 4043 : `testUpdateLibelleExistant`
- ligne 4133 : `testUpdateDAOFindByIdExceptionMessageNonNull`
- ligne 4225 : `testUpdateDAOFindByIdExceptionMessageNull`
- ligne 4319 : `testUpdateDAOFindByTypeProduitIgnoreCaseExceptionMessageNonNull`
- ligne 4406 : `testUpdateDAOFindByTypeProduitIgnoreCaseExceptionMessageNull`
- ligne 4493 : `testUpdateDAOSaveRetourneNull`
- ligne 4580 : `testUpdateDAOSaveExceptionMessageNonNull`
- ligne 4681 : `testUpdateDAOSaveExceptionMessageNull`
- ligne 4783 : `testUpdateSansModification`
- ligne 4859 : `testUpdateModificationCasse`
- ligne 4959 : `testUpdateNominal`
- ligne 5061 : `testDeleteNull`
- ligne 5093 : `testDeleteIdNull`
- ligne 5129 : `testDeleteDAOFindByIdRetourneNull`
- ligne 5188 : `testDeleteAbsent`
- ligne 5248 : `testDeleteDAOFindByIdExceptionMessageNonNull`
- ligne 5326 : `testDeleteDAOFindByIdExceptionMessageNull`
- ligne 5411 : `testDeleteDAODeleteExceptionMessageNonNull`
- ligne 5494 : `testDeleteDAODeleteExceptionMessageNull`
- ligne 5582 : `testDeleteNominal`
- ligne 5640 : `testCountStockageVide`
- ligne 5692 : `testCountDAOExceptionMessageNonNull`
- ligne 5760 : `testCountDAOExceptionMessageNull`
- ligne 5834 : `testCountNominal`
- ligne 5891 : `testConvertirRequetePageEnPageableNull`
- ligne 5936 : `testConvertirRequetePageEnPageableTrisNull`
- ligne 5985 : `testConvertirRequetePageEnPageableTrisVides`
- ligne 6034 : `testConvertirRequetePageEnPageableTrisValides`
- ligne 6097 : `testConvertirRequetePageEnPageableTrisInvalides`
- ligne 6166 : `testFiltrerTrierDedoublonnerNull`
- ligne 6207 : `testFiltrerTrierDedoublonnerVide`
- ligne 6248 : `testFiltrerTrierDedoublonnerAvecNulls`
- ligne 6307 : `testFiltrerTrierDedoublonnerCaseInsensitive`
- ligne 6371 : `testFiltrerTrierDedoublonnerTri`
- ligne 6430 : `testAppliquerModificationsAvecNull`
- ligne 6483 : `testAppliquerModificationsSansModification`
- ligne 6529 : `testAppliquerModificationsAvecModification`
- ligne 6575 : `testSafeEqualsAvecNull`
- ligne 6631 : `testSafeEqualsAvecObjetsEgaux`
- ligne 6668 : `testSafeEqualsAvecObjetsDifferents`
- ligne 6705 : `testIsBlankAvecNull`
- ligne 6742 : `testIsBlankAvecChaineVide`
- ligne 6779 : `testIsBlankAvecChaineBlanche`
- ligne 6816 : `testIsBlankAvecChaineValide`
- ligne 6853 : `testSafeMessageAvecNull`
- ligne 6890 : `testSafeMessageAvecObjetValide`
- ligne 6927 : `testSafeMessageAvecToStringNull`

#### Test intégration
- ligne 544 : `testCreerNull`
- ligne 601 : `testCreerBlank`
- ligne 671 : `testCreerLibelleExistant`
- ligne 771 : `testCreerNominal`
- ligne 882 : `testCreerPlusieurs`
- ligne 1010 : `testRechercherTousBaseVide`
- ligne 1058 : `testRechercherTousNominal`
- ligne 1146 : `testRechercherTousParPageNull`
- ligne 1224 : `testRechercherTousParPageAvecTri`
- ligne 1301 : `testRechercherTousParPageVide`
- ligne 1352 : `testRechercherTousParPageTailleSuperieure`
- ligne 1412 : `testRechercherTousParPageHorsBornes`
- ligne 1465 : `testRechercherTousParPageTailleZero`
- ligne 1538 : `testRechercherTousParPageNominal`
- ligne 1627 : `testFindByObjetMetierNull`
- ligne 1656 : `testFindByObjetMetierLibelleNull`
- ligne 1692 : `testFindByObjetMetierBlank`
- ligne 1730 : `testFindByObjetMetierNonTrouve`
- ligne 1795 : `testFindByObjetMetierNominal`
- ligne 1853 : `testFindByObjetMetierIdIgnoreCasseIgnoree`
- ligne 1958 : `testFindByLibelleNull`
- ligne 1987 : `testFindByLibelleBlank`
- ligne 2018 : `testFindByLibelleNonTrouve`
- ligne 2072 : `testFindByLibelleCaseInsensitive`
- ligne 2128 : `testFindByLibelleNominal`
- ligne 2182 : `testFindByLibelleRapideNull`
- ligne 2213 : `testFindByLibelleRapideBlank`
- ligne 2269 : `testFindByLibelleRapideNonTrouve`
- ligne 2315 : `testFindByLibelleRapideCaseInsensitive`
- ligne 2387 : `testFindByLibelleRapideDedoublonnage`
- ligne 2429 : `testFindByLibelleRapideNominal`
- ligne 2502 : `testFindByIdNull`
- ligne 2533 : `testFindByIdNonTrouve`
- ligne 2582 : `testFindByIdNominal`
- ligne 2639 : `testFindByIdIdCree`
- ligne 2719 : `testUpdateNull`
- ligne 2776 : `testUpdateLibelleNull`
- ligne 2841 : `testUpdateLibelleBlank`
- ligne 2907 : `testUpdateIdNull`
- ligne 2975 : `testUpdateEntityInexistante`
- ligne 3050 : `testUpdateLibelleExistant`
- ligne 3172 : `testUpdateSansModification`
- ligne 3278 : `testUpdateNominal`
- ligne 3407 : `testDeleteNull`
- ligne 3462 : `testDeleteIdNull`
- ligne 3522 : `testDeleteIdInexistant`
- ligne 3592 : `testDeleteNominal`
- ligne 3708 : `testDeleteDoubleSuppression`
- ligne 3849 : `testCountBaseVide`
- ligne 3911 : `testCountNominal`
- ligne 3976 : `testCountApresCreationPuisSuppression`

Règle d'autonomie : pour recoder le PORT, l'IA doit vérifier que chaque constante et chaque signature expliquent au moins un bloc de tests. Si un message est utilisé par un test d'exception, il doit rester dans le PORT et non dans l'ADAPTER.
