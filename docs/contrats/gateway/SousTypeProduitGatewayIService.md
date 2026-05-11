# Contrat local — SousTypeProduitGatewayIService

## 1) Port concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`

## 2) Rôle du port

`SousTypeProduitGatewayIService` est le PORT GATEWAY technique chargé des opérations d'accès au stockage pour l'objet métier `SousTypeProduit`.

Ce port :
- manipule exclusivement des objets métier ;
- reste strictement technique ;
- ne manipule aucun DTO ;
- ne produit aucun message utilisateur.

## 3) Dépendances de contrat

### 3.1) Objets métier principaux

- `levy.daniel.application.model.metier.produittype.SousTypeProduit`
- `levy.daniel.application.model.metier.produittype.TypeProduit`

### 3.2) Pagination

- `levy.daniel.application.model.services.produittype.pagination.RequetePage`
- `levy.daniel.application.model.services.produittype.pagination.ResultatPage<SousTypeProduit>`

### 3.3) Exceptions Gateway importées par le port

- `ExceptionAppliLibelleBlank`
- `ExceptionAppliParamNonPersistent`
- `ExceptionAppliParamNull`
- `ExceptionAppliParentNull`
- `ExceptionTechniqueGateway`
- `ExceptionTechniqueGatewayNonPersistent`

## 4) Signatures exactes du port

```java
SousTypeProduit creer(SousTypeProduit pObject) throws Exception;

List<SousTypeProduit> rechercherTous() throws Exception;

ResultatPage<SousTypeProduit> rechercherTousParPage(
        RequetePage pRequetePage) throws Exception;

SousTypeProduit findByObjetMetier(SousTypeProduit pObject) throws Exception;

List<SousTypeProduit> findByLibelle(String pLibelle) throws Exception;

List<SousTypeProduit> findByLibelleRapide(String pContenu) throws Exception;

List<SousTypeProduit> findAllByParent(TypeProduit pParent) throws Exception;

SousTypeProduit findById(Long pId) throws Exception;

SousTypeProduit update(SousTypeProduit pObject) throws Exception;

void delete(SousTypeProduit pObject) throws Exception;

long count() throws Exception;
```

## 5) Contrat technique à retenir

### 5.1) Création / update / delete

Le port expose :
- `creer(SousTypeProduit)` ;
- `update(SousTypeProduit)` ;
- `delete(SousTypeProduit)`.

Ces opérations restent techniques et peuvent signaler des préconditions invalides au niveau Gateway :
- objet `null` ;
- libellé blank ;
- objet non persistant ;
- parent `null` ;
- parent non persistant selon le scénario.

### 5.2) Recherche

Le port expose :
- la recherche exhaustive ;
- la recherche paginée ;
- la recherche par objet métier ;
- la recherche par libellé exact ;
- la recherche par libellé rapide ;
- la recherche par parent `TypeProduit` ;
- la recherche par identifiant.

### 5.3) Comptage

Le port expose `count()` comme opération technique de comptage du stockage.

## 6) Règles d'architecture à respecter

- aucune dépendance DTO ;
- aucune logique UC ;
- aucune exception de SERVICE UC ;
- aucun Raw Type dans les signatures ;
- la pagination est contractuelle via `ResultatPage<SousTypeProduit>`.

## 7) Fichiers à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie du PORT `SousTypeProduitGatewayIService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java` — 45211 octets, 1334 lignes LF, SHA-256 `a8a2d24649693a6aa03f86fe1f4cd434d9ef0c3ab88f9cead074a0e9443ed9c8`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java` — 56277 octets, 1835 lignes LF, SHA-256 `d57c5f10377e9d15667526218de17269520a7a5451a1cbed51f53579df85c298`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java` — 373805 octets, 9418 lignes LF, SHA-256 `ac59581fffd921b7d283049074f963b89b174f184b29fbf84ec4f9c1e5a73633`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java` — 319576 octets, 8537 lignes LF, SHA-256 `c2c17bb7dbab60ed20663844baeaa131b85b75aca5655a8f2d7542665ff54b3b`

Ce PORT ne doit jamais être recodé de mémoire. Pour toute analyse ou génération, l'IA doit relire dans cet ordre : `CONTRAT_IA.md`, `CoucheServicesGateway.md`, le présent contrat local, le PORT Java, l'ADAPTER JPA, puis les tests Mock et intégration.

### 99.2) Rôle exact à conserver

`SousTypeProduitGatewayIService` porte le contrat technique Gateway de `SousTypeProduit`, objet métier enfant de `TypeProduit`.

Le PORT porte les messages techniques Gateway utilisés par les ADAPTERS et les tests. Ces constantes ne sont pas décoratives : elles verrouillent le texte des exceptions et doivent être conservées dans leur logique, leur nommage et leur ordre.

### 99.3) Constantes contractuelles exactes du PORT

```java
	String ANOMALIE_APPLICATIVE = "Anomalie applicative ";
	String OBJET_METIER_PARAM_NULL
		= "- l'objet métier passé en paramètre est null.";
	String OBJET_METIER_A
		= "- l'objet métier passé en paramètre a un ";
	String LIBELLE_BLANK = "libellé blank (null ou que des espaces).";
	String PAS_DANS_STOCKAGE = "pas déjà dans le stockage : ";
	String MESSAGE_CREER_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_CREER_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ LIBELLE_BLANK;
	String MESSAGE_CREER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à créer a un "
			+ LIBELLE_BLANK;
	String MESSAGE_CREER_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet que vous voulez créer n'existe "
			+ PAS_DANS_STOCKAGE;
	String MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL =
			ANOMALIE_APPLICATIVE
			+ "- le paramètre pObject ne doit pas être null.";
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
			ANOMALIE_APPLICATIVE
			+ "- le libellé de pObject passé en paramètre "
			+ "ne doit pas être blank.";
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
	= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet à rechercher a un "
		+ LIBELLE_BLANK;
	String MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT
	= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet que vous voulez rechercher n'existe "
		+ PAS_DANS_STOCKAGE;
	String MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le libellé passé en paramètre est un "
			+ LIBELLE_BLANK;
	String MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ "- le contenu passé en paramètre est null.";
	String MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
	= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet passé en paramètre a un "
		+ LIBELLE_BLANK;
	String MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet n'existait "
			+ PAS_DANS_STOCKAGE;
	String ID_PARAM_NULL = "- l'identifiant passé en paramètre est null.";
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
		= ANOMALIE_APPLICATIVE
		+ "- l'objet que vous voulez modifier n'est pas persistant "
			+ "(ID null) : ";
	String MESSAGE_UPDATE_KO_PARENT_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ "parent null.";
	String MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK
		= ANOMALIE_APPLICATIVE
			+ "- le parent de l'objet à modifier a un "
			+ LIBELLE_BLANK;
	String MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- le parent de l'objet que vous voulez modifier n'existe "
			+ PAS_DANS_STOCKAGE;
	String MESSAGE_DELETE_KO_PARAM_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_PARAM_NULL;
	String ID_NULL = "ID null.";
	String MESSAGE_DELETE_KO_ID_NULL
		= ANOMALIE_APPLICATIVE
			+ OBJET_METIER_A
			+ ID_NULL;
	String MESSAGE_DELETE_KO_NON_PERSISTENT
		= ANOMALIE_APPLICATIVE
		+ "- l'objet que vous voulez détruire n'existait "
			+ "pas dans le stockage : ";
	String ERREUR_TECHNIQUE_STOCKAGE
		= "Erreur Technique lors du stockage : ";
	String ERREUR_TECHNIQUE_KO_STOCKAGE
		= "Erreur Technique - Le stockage a retourné null.";
	String PREFIX_MESSAGE_CREER = "MESSAGE_CREER";
	String PREFIX_MESSAGE_FINDBYOBJETMETIER = "MESSAGE_FINDBYOBJETMETIER";
	String PREFIX_MESSAGE_FINDALLBYPARENT = "MESSAGE_FINDALLBYPARENT";
	String PREFIX_MESSAGE_UPDATE = "MESSAGE_UPDATE";
	String SUFFIXE_KO_PARENT_NULL
		= "_KO_PARENT_NULL";
	String SUFFIXE_KO_LIBELLE_PARENT_BLANK
		= "_KO_LIBELLE_PARENT_BLANK";
	String SUFFIXE_KO_PARENT_NON_PERSISTENT
		= "_KO_PARENT_NON_PERSISTENT";
```

Règle stricte : ne pas remplacer ces constantes par des messages inline, ne pas changer leur préfixe `MESSAGE_`, ne pas supprimer les constantes de fragments, ne pas déplacer les messages vers la couche UC ou Controller.

### 99.4) Signatures et ordre exacts des méthodes du PORT

```java
SousTypeProduit creer(SousTypeProduit pObject) throws Exception
List<SousTypeProduit> rechercherTous() throws Exception
ResultatPage<SousTypeProduit> rechercherTousParPage(RequetePage pRequetePage) throws Exception
SousTypeProduit findByObjetMetier(SousTypeProduit pObject) throws Exception
List<SousTypeProduit> findByLibelle(String pLibelle) throws Exception
List<SousTypeProduit> findByLibelleRapide(String pContenu) throws Exception
List<SousTypeProduit> findAllByParent(TypeProduit pParent) throws Exception
SousTypeProduit findById(Long pId) throws Exception
SousTypeProduit update(SousTypeProduit pObject) throws Exception
void delete(SousTypeProduit pObject) throws Exception
long count() throws Exception
```

L'ordre des blocs est contractuel : `creer`, `rechercherTous`, `rechercherTousParPage`, `findByObjetMetier`, `findByLibelle`, `findByLibelleRapide`, éventuel `findAllByParent`, `findById`, `update`, `delete`, `count`.

### 99.5) Tests de référence à préserver

#### Test Mock
- ligne 572 : `testCreerNull`
- ligne 619 : `testCreerLibelleBlank`
- ligne 668 : `testCreerParentNull`
- ligne 716 : `testCreerParentLibelleBlank`
- ligne 766 : `testCreerParentIdNull`
- ligne 819 : `testCreerParentAbsent`
- ligne 877 : `testCreerParentDAOExceptionMessageNonNull`
- ligne 946 : `testCreerParentDAOExceptionMessageNull`
- ligne 1011 : `testCreerDAOSaveRetourneNull`
- ligne 1073 : `testCreerDAOSaveExceptionMessageNonNull`
- ligne 1149 : `testCreerDAOSaveExceptionMessageNull`
- ligne 1229 : `testCreerDoublon`
- ligne 1318 : `testCreerParentCaracteresSpeciaux`
- ligne 1393 : `testCreerNominal`
- ligne 1468 : `testRechercherTousDAORetourneNull`
- ligne 1520 : `testRechercherTousDAORetourneVide`
- ligne 1574 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 1640 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1713 : `testRechercherTousTriDedoublonnage`
- ligne 1824 : `testRechercherTousNominal`
- ligne 1933 : `testRechercherTousParPageNull`
- ligne 2072 : `testRechercherTousParPageDAORetourneNull`
- ligne 2142 : `testRechercherTousParPageContenuNull`
- ligne 2221 : `testRechercherTousParPageDAOExceptionMessageNonNull`
- ligne 2319 : `testRechercherTousParPageDAOExceptionMessageNull`
- ligne 2421 : `testRechercherTousParPagePageSizeZero`
- ligne 2563 : `testRechercherTousParPageAvecTri`
- ligne 2722 : `testRechercherTousParPageContenuAvecNulls`
- ligne 2860 : `testRechercherTousParPagePageVide`
- ligne 2965 : `testRechercherTousParPageNominalRequeteNeutre`
- ligne 3123 : `testRechercherTousParPageNominal`
- ligne 3275 : `testFindByObjetMetierNull`
- ligne 3320 : `testFindByObjetMetierLibelleBlank`
- ligne 3373 : `testFindByObjetMetierParentNull`
- ligne 3425 : `testFindByObjetMetierParentLibelleBlank`
- ligne 3479 : `testFindByObjetMetierParentIdNull`
- ligne 3537 : `testFindByObjetMetierParentAbsent`
- ligne 3600 : `testFindByObjetMetierParentDAOExceptionMessageNonNull`
- ligne 3675 : `testFindByObjetMetierParentDAOExceptionMessageNull`
- ligne 3752 : `testFindByObjetMetierDAORetourneNull`
- ligne 3821 : `testFindByObjetMetierDAOExceptionMessageNonNull`
- ligne 3906 : `testFindByObjetMetierDAOExceptionMessageNull`
- ligne 3990 : `testFindByObjetMetierParentSansObjetMetier`
- ligne 4063 : `testFindByObjetMetierNonTrouve`
- ligne 4144 : `testFindByObjetMetierNominal`
- ligne 4240 : `testFindByLibelleNull`
- ligne 4279 : `testFindByLibelleBlank`
- ligne 4325 : `testFindByLibelleDAORetourneNull`
- ligne 4401 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 4488 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 4576 : `testFindByLibelleNonTrouve`
- ligne 4668 : `testFindByLibelleNominal`
- ligne 4811 : `testFindByLibelleRapideNull`
- ligne 4853 : `testFindByLibelleRapideBlank`
- ligne 4958 : `testFindByLibelleRapideDAORetourneNull`
- ligne 5029 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 5122 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 5228 : `testFindByLibelleRapideContenuSpeciauxNominal`
- ligne 5332 : `testFindByLibelleRapideNonTrouve`
- ligne 5419 : `testFindByLibelleRapideNominal`
- ligne 5562 : `testFindAllByParentNull`
- ligne 5601 : `testFindAllByParentParentLibelleBlank`
- ligne 5649 : `testFindAllByParentParentIdNull`
- ligne 5702 : `testFindAllByParentParentAbsent`
- ligne 5767 : `testFindAllByParentParentDAOExceptionMessageNonNull`
- ligne 5837 : `testFindAllByParentParentDAOExceptionMessageNull`
- ligne 5908 : `testFindAllByParentDAORetourneNull`
- ligne 5971 : `testFindAllByParentDAOExceptionMessageNonNull`
- ligne 6046 : `testFindAllByParentDAOExceptionMessageNull`
- ligne 6122 : `testFindAllByParentNonTrouve`
- ligne 6192 : `testFindAllByParentAvecDoublons`
- ligne 6277 : `testFindAllByParentNominal`
- ligne 6405 : `testFindByIdNull`
- ligne 6449 : `testFindByIdDAORetourneNull`
- ligne 6506 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 6578 : `testFindByIdDAOExceptionMessageNull`
- ligne 6651 : `testFindByIdNonTrouve`
- ligne 6719 : `testFindByIdNominal`
- ligne 6799 : `testUpdateNull`
- ligne 6831 : `testUpdateLibelleNull`
- ligne 6867 : `testUpdateLibelleBlank`
- ligne 6904 : `testUpdateIdNull`
- ligne 6941 : `testUpdateParentNull`
- ligne 6977 : `testUpdateParentLibelleBlank`
- ligne 7014 : `testUpdateParentIdNull`
- ligne 7054 : `testUpdateParentAbsent`
- ligne 7103 : `testUpdateParentDAOExceptionMessageNonNull`
- ligne 7159 : `testUpdateParentDAOExceptionMessageNull`
- ligne 7214 : `testUpdateDAOFindByIdRetourneNull`
- ligne 7265 : `testUpdateAbsent`
- ligne 7319 : `testUpdateDAOFindByIdExceptionMessageNonNull`
- ligne 7377 : `testUpdateDAOFindByIdExceptionMessageNull`
- ligne 7433 : `testUpdateDAOSaveRetourneNull`
- ligne 7486 : `testUpdateDAOSaveExceptionMessageNonNull`
- ligne 7546 : `testUpdateDAOSaveExceptionMessageNull`
- ligne 7605 : `testUpdateSansModification`
- ligne 7663 : `testUpdateParentLibelleCaseSensitive`
- ligne 7722 : `testUpdateModificationCasse`
- ligne 7787 : `testUpdateParentModifie`
- ligne 7858 : `testUpdateNominal`
- ligne 7927 : `testDeleteNull`
- ligne 7961 : `testDeleteIdNull`
- ligne 8001 : `testDeleteDAOFindByIdRetourneNull`
- ligne 8063 : `testDeleteAbsent`
- ligne 8125 : `testDeleteDAOFindByIdExceptionMessageNonNull`
- ligne 8202 : `testDeleteDAOFindByIdExceptionMessageNull`
- ligne 8281 : `testDeleteEntityManagerRemoveExceptionMessageNonNull`
- ligne 8359 : `testDeleteEntityManagerRemoveExceptionMessageNull`
- ligne 8431 : `testDeleteEntityManagerFlushExceptionMessageNonNull`
- ligne 8499 : `testDeleteEntityManagerFlushExceptionMessageNull`
- ligne 8565 : `testDeleteVerificationPostSuppressionEchoue`
- ligne 8630 : `testDeleteVerificationPostSuppressionDAOExceptionMessageNonNull`
- ligne 8701 : `testDeleteVerificationPostSuppressionDAOExceptionMessageNull`
- ligne 8770 : `testDeleteNominal`
- ligne 8839 : `testCountStockageVide`
- ligne 8898 : `testCountDAOExceptionMessageNonNull`
- ligne 8972 : `testCountDAOExceptionMessageNull`
- ligne 9052 : `testCountNominal`
- ligne 9114 : `testSanityLocaleToUpperCase`
- ligne 9149 : `testSanitySafeMessage`
- ligne 9182 : `testSanityIsBlankButNotNull`
- ligne 9212 : `testSanityConstruireMessageNonPersistent`
- ligne 9256 : `testSanityMockitoDefaultNullOnOptionalIsHandled`

#### Test intégration
- ligne 652 : `testCreerNull`
- ligne 709 : `testCreerLibelleNull`
- ligne 775 : `testCreerLibelleBlank`
- ligne 843 : `testCreerParentNull`
- ligne 909 : `testCreerParentLibelleNull`
- ligne 977 : `testCreerParentLibelleBlank`
- ligne 1045 : `testCreerParentIdNull`
- ligne 1117 : `testCreerParentAbsent`
- ligne 1206 : `testCreerDoublon`
- ligne 1338 : `testCreerNominal`
- ligne 1519 : `testCreerPlusieurs`
- ligne 1761 : `testRechercherTousStockageVide`
- ligne 1834 : `testRechercherTousNominal`
- ligne 1981 : `testRechercherTousParPageNull`
- ligne 2076 : `testRechercherTousParPageAvecTri`
- ligne 2227 : `testRechercherTousParPageStockageVide`
- ligne 2318 : `testRechercherTousParPageTailleSuperieureAuTotal`
- ligne 2442 : `testRechercherTousParPagePageHorsBorne`
- ligne 2536 : `testRechercherTousParPageTailleZero`
- ligne 2683 : `testRechercherTousParPageNominal`
- ligne 2830 : `testFindByObjetMetierNull`
- ligne 2886 : `testFindByObjetMetierLibelleNull`
- ligne 2950 : `testFindByObjetMetierLibelleBlank`
- ligne 3014 : `testFindByObjetMetierParentNull`
- ligne 3076 : `testFindByObjetMetierParentLibelleNull`
- ligne 3139 : `testFindByObjetMetierParentLibelleBlank`
- ligne 3202 : `testFindByObjetMetierParentIdNull`
- ligne 3269 : `testFindByObjetMetierParentAbsent`
- ligne 3350 : `testFindByObjetMetierNonTrouve`
- ligne 3439 : `testFindByObjetMetierNominal`
- ligne 3549 : `testFindByObjetMetierIdIgnoreCasseIgnoree`
- ligne 3732 : `testFindByLibelleNull`
- ligne 3787 : `testFindByLibelleBlank`
- ligne 3844 : `testFindByLibelleNonTrouve`
- ligne 3930 : `testFindByLibelleCaseInsensitive`
- ligne 4071 : `testFindByLibelleNominal`
- ligne 4202 : `testFindByLibelleRapideNull`
- ligne 4259 : `testFindByLibelleRapideBlank`
- ligne 4352 : `testFindByLibelleRapideNonTrouve`
- ligne 4437 : `testFindByLibelleRapideCaseInsensitive`
- ligne 4595 : `testFindByLibelleRapideDedoublonnage`
- ligne 4726 : `testFindByLibelleRapideNominal`
- ligne 4880 : `testFindAllByParentNull`
- ligne 4935 : `testFindAllByParentParentLibelleNull`
- ligne 4995 : `testFindAllByParentParentLibelleBlank`
- ligne 5056 : `testFindAllByParentParentIdNull`
- ligne 5122 : `testFindAllByParentParentAbsent`
- ligne 5199 : `testFindAllByParentParentSansEnfant`
- ligne 5301 : `testFindAllByParentNominal`
- ligne 5501 : `testFindByIdNull`
- ligne 5558 : `testFindByIdNonTrouve`
- ligne 5636 : `testFindByIdNominal`
- ligne 5774 : `testFindByIdIdCree`
- ligne 5958 : `testUpdateNull`
- ligne 6015 : `testUpdateLibelleNull`
- ligne 6083 : `testUpdateLibelleBlank`
- ligne 6152 : `testUpdateIdNull`
- ligne 6225 : `testUpdateParentNull`
- ligne 6288 : `testUpdateParentLibelleNull`
- ligne 6354 : `testUpdateParentLibelleBlank`
- ligne 6421 : `testUpdateParentIdNull`
- ligne 6492 : `testUpdateParentAbsent`
- ligne 6574 : `testUpdateAbsent`
- ligne 6664 : `testUpdateSansModification`
- ligne 6839 : `testUpdateNominal`
- ligne 7023 : `testUpdateParentModifie`
- ligne 7240 : `testUpdateDoublonMetier`
- ligne 7443 : `testDeleteNull`
- ligne 7500 : `testDeleteIdNull`
- ligne 7567 : `testDeleteAbsent`
- ligne 7661 : `testDeleteNominal`
- ligne 7856 : `testDeleteDoubleSuppression`
- ligne 8089 : `testCountStockageVide`
- ligne 8164 : `testCountNominal`
- ligne 8246 : `testCountApresCreationPuisSuppression`

Règle d'autonomie : pour recoder le PORT, l'IA doit vérifier que chaque constante et chaque signature expliquent au moins un bloc de tests. Si un message est utilisé par un test d'exception, il doit rester dans le PORT et non dans l'ADAPTER.
