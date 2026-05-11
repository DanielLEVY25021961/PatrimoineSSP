# Contrat local — SousTypeProduitGatewayJPAService

## 1) Adapter concerné

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`

## 2) Port concerné

- `SousTypeProduitGatewayIService`

## 3) Objectif

Décrire le pivot comportemental local du gateway JPA `SousTypeProduitGatewayJPAService` pour la sacralisation de `couche_services.gateway`.

Le présent contrat local doit être relu conjointement avec :
- le PORT `SousTypeProduitGatewayIService` ;
- l'implémentation `SousTypeProduitGatewayJPAService` ;
- les tests Mock ;
- les tests d'intégration.

## 4) Contrats importants

### 4.1) creer(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Libellé du parent blank -> `ExceptionAppliLibelleBlank`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Sauvegarde DAO `null` -> `ExceptionTechniqueGateway`
- En nominal : retourne l'objet métier persisté

### 4.2) rechercherTous()

- Retourne toujours une liste non `null`
- Une absence de contenu retourne une liste vide
- Le résultat observable est filtré, trié, dédoublonné et converti en objets métier

### 4.3) rechercherTousParPage(RequetePage)

- Si `pRequetePage == null` -> utilise une `RequetePage` par défaut
- `pageJPA == null` -> `ExceptionTechniqueGateway`
- `pageJPA.getContent() == null` -> `ExceptionTechniqueGateway`
- Le `ResultatPage<SousTypeProduit>` retourné reste cohérent :
  - `content` non `null`
  - `pageNumber` cohérent
  - `pageSize` cohérent
  - `totalElements` cohérent

### 4.4) findByObjetMetier(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- En nominal : retourne l'objet métier correspondant ou `null` si introuvable

### 4.5) findByLibelle(String)

- Libellé blank -> `ExceptionAppliLibelleBlank`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.6) findByLibelleRapide(String)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Contenu blank -> délègue à `rechercherTous()`
- Aucun résultat -> liste vide
- Résultat nominal -> liste non `null`

### 4.7) findAllByParent(TypeProduit)

- Parent `null` -> `ExceptionAppliParentNull`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Aucun enfant trouvé -> liste vide
- Résultat nominal -> liste non `null`

### 4.8) findById(Long)

- Paramètre `null` -> `ExceptionAppliParamNull`
- DAO vide -> retourne `null`
- DAO `null` -> `ExceptionTechniqueGateway`
- Résultat nominal -> retourne l'objet métier persisté

### 4.9) update(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- Libellé blank -> `ExceptionAppliLibelleBlank`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- Parent non persistant -> `ExceptionTechniqueGatewayNonPersistent`
- Objet absent en stockage -> retourne `null`
- Si aucune modification n'est détectée -> retourne l'objet persistant converti sans sauvegarde inutile
- En nominal : sauvegarde l'entité modifiée puis retourne l'objet métier mis à jour

### 4.10) delete(SousTypeProduit)

- Paramètre `null` -> `ExceptionAppliParamNull`
- ID `null` -> `ExceptionAppliParamNonPersistent`
- DAO `null` -> `ExceptionTechniqueGateway`
- Objet absent en stockage -> ne fait rien
- En nominal : supprime techniquement l'objet ciblé

### 4.11) count()

- Retourne un compteur technique `>= 0`
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

## 5) Règles de frontière

- le gateway ne manipule aucun DTO ;
- le gateway ne produit aucun message utilisateur ;
- les contrôles portent uniquement sur la sécurité technique du contrat Gateway ;
- les exceptions de `exceptionsservices` n'appartiennent pas à ce périmètre.

## 6) Fichiers de preuve à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie de l'ADAPTER `SousTypeProduitGatewayJPAService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java` — 56277 octets, 1835 lignes LF, SHA-256 `d57c5f10377e9d15667526218de17269520a7a5451a1cbed51f53579df85c298`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java` — 45211 octets, 1334 lignes LF, SHA-256 `a8a2d24649693a6aa03f86fe1f4cd434d9ef0c3ab88f9cead074a0e9443ed9c8`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java` — 373805 octets, 9418 lignes LF, SHA-256 `ac59581fffd921b7d283049074f963b89b174f184b29fbf84ec4f9c1e5a73633`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java` — 319576 octets, 8537 lignes LF, SHA-256 `c2c17bb7dbab60ed20663844baeaa131b85b75aca5655a8f2d7542665ff54b3b`

L'ADAPTER JPA est le point technique qui dialogue avec les DAO et convertisseurs. Il ne porte aucune logique UC, aucun DTO et aucun message utilisateur. Il transforme les erreurs techniques en exceptions Gateway et respecte strictement les messages exposés par le PORT.

### 99.2) Attributs, constantes et collaborateurs à conserver

#### Champs détectés
- `private final SousTypeProduitDaoJPA sousTypeProduitDaoJPA;`
- `private final TypeProduitDaoJPA typeProduitDaoJPA;`
- `@PersistenceContext private EntityManager entityManager;`
- `@SuppressWarnings("unused") private static final Logger LOG = LogManager.getLogger(SousTypeProduitGatewayJPAService.class);`

#### Constantes explicites
```java
	private static final Logger LOG
		= LogManager.getLogger(SousTypeProduitGatewayJPAService.class);
```

#### Constructeur(s)

Constructeur unique d'injection Spring à conserver strictement :

```java
	public SousTypeProduitGatewayJPAService(
			@Qualifier("SousTypeProduitDaoJPA") 
			final SousTypeProduitDaoJPA pSousTypeProduitDaoJPA,
			@Qualifier("TypeProduitDaoJPA") 
			final TypeProduitDaoJPA pTypeProduitDaoJPA) {
		super();
		this.sousTypeProduitDaoJPA = pSousTypeProduitDaoJPA;
		this.typeProduitDaoJPA = pTypeProduitDaoJPA;
	}
```

Javadoc constructeur à conserver dans son intention et son formalisme :
- `CONSTRUCTEUR appelé automatiquement par SPRING` ;
- injection d'un `SousTypeProduitDaoJPA` et d'un `TypeProduitDaoJPA` ;
- avertissement explicite : ne surtout pas créer de constructeur d'arité nulle, faute de quoi Spring ne pourra plus injecter.

Règle stricte : conserver l'injection par constructeur validée avec `@Qualifier("SousTypeProduitDaoJPA")` puis `@Qualifier("TypeProduitDaoJPA")`, conserver l'ordre des paramètres DAO validé, ne jamais la remplacer par une injection de champ, un constructeur vide, un constructeur d'arité nulle ou une autre forme d'injection, ne pas créer de dépendance DTO, UC ou Controller, ne pas supprimer `LOG` même s'il est peu utilisé.

### 99.3) Ordre exact des méthodes de l'ADAPTER

- ligne 205 : `public SousTypeProduit creer(final SousTypeProduit pObject)`
- ligne 301 : `public List<SousTypeProduit> rechercherTous()`
- ligne 365 : `public ResultatPage<SousTypeProduit> rechercherTousParPage(final RequetePage pRequetePage)`
- ligne 454 : `public SousTypeProduit findByObjetMetier(final SousTypeProduit pObject)`
- ligne 562 : `public List<SousTypeProduit> findByLibelle(final String pLibelle)`
- ligne 640 : `public List<SousTypeProduit> findByLibelleRapide(final String pContenu)`
- ligne 721 : `public List<SousTypeProduit> findAllByParent(final TypeProduit pParent)`
- ligne 792 : `public SousTypeProduit findById(final Long pId)`
- ligne 861 : `public SousTypeProduit update(final SousTypeProduit pObject)`
- ligne 1037 : `public void delete(final SousTypeProduit pObject)`
- ligne 1135 : `public long count()`
- ligne 1166 : `public void setEntityManager(final EntityManager pEntityManager)`
- ligne 1231 : `private TypeProduitJPA verifierPersistanceParent(final TypeProduitI pParent, final String pMethode)`
- ligne 1332 : `private List<SousTypeProduit> filtrerTrierDedoublonner(final List<SousTypeProduitJPA> pEntities)`
- ligne 1405 : `private static Comparator<SousTypeProduitJPA> construireComparateurSousTypeProduit()`
- ligne 1487 : `private static boolean appliquerModifications(final SousTypeProduitJPA pPersistant, final SousTypeProduit pObject, final TypeProduitJPA pParentPersistant)`
- ligne 1549 : `private static Pageable convertirRequetePageEnPageable(final RequetePage pRequetePage)`
- ligne 1606 : `private static String resoudreMessageParentNull(final String pMethode)`
- ligne 1643 : `private static String resoudreMessageLibelleParentBlank(final String pMethode)`
- ligne 1686 : `private static String resoudreMessageParentNonPersistent(final String pMethode)`
- ligne 1724 : `private static String construireMessageParentNonPersistent(final String pMethode, final String pLibelleParent)`
- ligne 1779 : `private static String safeMessage(final Object p)`
- ligne 1803 : `private static boolean safeEquals(final Object p1, final Object p2)`
- ligne 1831 : `private static boolean isBlank(final String pString)`

Cet ordre doit être conservé au recodage : méthodes du PORT d'abord, puis méthodes techniques internes dans l'ordre validé par la classe.

### 99.4) Algorithmes et comportements à préserver


- `creer` : refuse objet `null`, libellé blank, parent `null`, libellé parent blank, parent ID `null`, parent absent ; vérifie le parent via DAO `TypeProduitDaoJPA`; convertit métier→JPA avec parent persistant ; sauvegarde ; convertit retour.
- `rechercherTous` : DAO `findAll()`, retour `null` interdit, éléments `null` ignorés, conversion, tri par parent puis libellé, dédoublonnage.
- `rechercherTousParPage` : requête `null` -> `RequetePage`; conversion `Pageable`; page JPA `null` ou contenu `null` -> exception technique ; conversion du contenu.
- `findByObjetMetier` : validations objet/libellé/parent ; vérifie parent ; cherche tous les enfants du parent puis compare le libellé en ignorant la casse ; non trouvé -> `null`.
- `findByLibelle` : refuse blank ; DAO `findBySousTypeProduitIgnoreCase`; liste `null` -> exception ; tri/dédoublonnage.
- `findByLibelleRapide` : `null` interdit ; blank -> `rechercherTous`; DAO `findBySousTypeProduitContainingIgnoreCase`; liste `null` interdite.
- `findAllByParent` : parent `null`, libellé parent blank, ID parent `null`, parent absent ou exception DAO sont distingués ; DAO `findAllByTypeProduit` ; liste `null` interdite.
- `findById` : ID `null` interdit ; Optional `null` interdit ; absent -> `null` ; présent -> conversion.
- `update` : validations objet/libellé/ID/parent ; absent -> `null`; sans modification -> retour sans sauvegarde ; modification parent/libellé -> `save`.
- `delete` : validations ; absent -> ne fait rien ; nominal utilise `EntityManager.remove` puis `flush`, puis vérification post-suppression via DAO ; toute anomalie technique devient `ExceptionTechniqueGateway`.
- `setEntityManager` reste présent pour les tests et ne doit pas être supprimé.
- Helpers parent : les méthodes `resoudreMessageParentNull`, `resoudreMessageLibelleParentBlank`, `resoudreMessageParentNonPersistent` et `construireMessageParentNonPersistent` centralisent les messages du PORT selon la méthode appelante.


### 99.5) Tests Mock associés, par bloc de responsabilité

#### Bloc `Creer`
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

#### Bloc `RechercherTousParPage`
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

#### Bloc `RechercherTous`
- ligne 1468 : `testRechercherTousDAORetourneNull`
- ligne 1520 : `testRechercherTousDAORetourneVide`
- ligne 1574 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 1640 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1713 : `testRechercherTousTriDedoublonnage`
- ligne 1824 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
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

#### Bloc `FindByLibelleRapide`
- ligne 4811 : `testFindByLibelleRapideNull`
- ligne 4853 : `testFindByLibelleRapideBlank`
- ligne 4958 : `testFindByLibelleRapideDAORetourneNull`
- ligne 5029 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 5122 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 5228 : `testFindByLibelleRapideContenuSpeciauxNominal`
- ligne 5332 : `testFindByLibelleRapideNonTrouve`
- ligne 5419 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 4240 : `testFindByLibelleNull`
- ligne 4279 : `testFindByLibelleBlank`
- ligne 4325 : `testFindByLibelleDAORetourneNull`
- ligne 4401 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 4488 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 4576 : `testFindByLibelleNonTrouve`
- ligne 4668 : `testFindByLibelleNominal`

#### Bloc `FindAllByParent`
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

#### Bloc `FindById`
- ligne 6405 : `testFindByIdNull`
- ligne 6449 : `testFindByIdDAORetourneNull`
- ligne 6506 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 6578 : `testFindByIdDAOExceptionMessageNull`
- ligne 6651 : `testFindByIdNonTrouve`
- ligne 6719 : `testFindByIdNominal`

#### Bloc `Update`
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

#### Bloc `Delete`
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

#### Bloc `Count`
- ligne 8839 : `testCountStockageVide`
- ligne 8898 : `testCountDAOExceptionMessageNonNull`
- ligne 8972 : `testCountDAOExceptionMessageNull`
- ligne 9052 : `testCountNominal`

#### Bloc `Sanity`
- ligne 9114 : `testSanityLocaleToUpperCase`
- ligne 9149 : `testSanitySafeMessage`
- ligne 9182 : `testSanityIsBlankButNotNull`
- ligne 9212 : `testSanityConstruireMessageNonPersistent`
- ligne 9256 : `testSanityMockitoDefaultNullOnOptionalIsHandled`

### 99.6) Tests d'intégration associés, par bloc de responsabilité

#### Bloc `Creer`
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

#### Bloc `RechercherTousParPage`
- ligne 1981 : `testRechercherTousParPageNull`
- ligne 2076 : `testRechercherTousParPageAvecTri`
- ligne 2227 : `testRechercherTousParPageStockageVide`
- ligne 2318 : `testRechercherTousParPageTailleSuperieureAuTotal`
- ligne 2442 : `testRechercherTousParPagePageHorsBorne`
- ligne 2536 : `testRechercherTousParPageTailleZero`
- ligne 2683 : `testRechercherTousParPageNominal`

#### Bloc `RechercherTous`
- ligne 1761 : `testRechercherTousStockageVide`
- ligne 1834 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
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

#### Bloc `FindByLibelleRapide`
- ligne 4202 : `testFindByLibelleRapideNull`
- ligne 4259 : `testFindByLibelleRapideBlank`
- ligne 4352 : `testFindByLibelleRapideNonTrouve`
- ligne 4437 : `testFindByLibelleRapideCaseInsensitive`
- ligne 4595 : `testFindByLibelleRapideDedoublonnage`
- ligne 4726 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 3732 : `testFindByLibelleNull`
- ligne 3787 : `testFindByLibelleBlank`
- ligne 3844 : `testFindByLibelleNonTrouve`
- ligne 3930 : `testFindByLibelleCaseInsensitive`
- ligne 4071 : `testFindByLibelleNominal`

#### Bloc `FindAllByParent`
- ligne 4880 : `testFindAllByParentNull`
- ligne 4935 : `testFindAllByParentParentLibelleNull`
- ligne 4995 : `testFindAllByParentParentLibelleBlank`
- ligne 5056 : `testFindAllByParentParentIdNull`
- ligne 5122 : `testFindAllByParentParentAbsent`
- ligne 5199 : `testFindAllByParentParentSansEnfant`
- ligne 5301 : `testFindAllByParentNominal`

#### Bloc `FindById`
- ligne 5501 : `testFindByIdNull`
- ligne 5558 : `testFindByIdNonTrouve`
- ligne 5636 : `testFindByIdNominal`
- ligne 5774 : `testFindByIdIdCree`

#### Bloc `Update`
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

#### Bloc `Delete`
- ligne 7443 : `testDeleteNull`
- ligne 7500 : `testDeleteIdNull`
- ligne 7567 : `testDeleteAbsent`
- ligne 7661 : `testDeleteNominal`
- ligne 7856 : `testDeleteDoubleSuppression`

#### Bloc `Count`
- ligne 8089 : `testCountStockageVide`
- ligne 8164 : `testCountNominal`
- ligne 8246 : `testCountApresCreationPuisSuppression`

### 99.7) Règle anti-improvisation locale

L'IA ne doit jamais remplacer un helper privé par une version jugée équivalente sans reconfronter les tests ci-dessus. Les helpers `filtrerTrierDedoublonner`, `convertirRequetePageEnPageable`, `safeMessage`, `safeEquals`, `isBlank`, les résolutions de messages parent et `appliquerModifications` sont contractuels lorsqu'ils existent dans la classe validée.
