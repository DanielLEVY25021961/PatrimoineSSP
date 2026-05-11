# docs/contrats/gateway/TypeProduitGatewayJPAService.md

# Contrat comportemental — TypeProduitGatewayJPAService (Gateway JPA)

## 1) Port concerné

- `TypeProduitGatewayIService`

## 2) Objectif

Décrire le pivot comportemental local du gateway JPA `TypeProduitGatewayJPAService` pour la sacralisation de `couche_services.gateway`.

Le présent contrat local doit être relu conjointement avec :
- le PORT `TypeProduitGatewayIService` ;
- l'implémentation `TypeProduitGatewayJPAService` ;
- les tests Mock ;
- les tests d'intégration.

## 3) Contrats importants

### 3.1) creer(TypeProduit)

- Paramètre `null` -> Exception applicative (contrat du port)
- Libellé blank -> Exception applicative (contrat du port)
- Stockage KO / DAO `null` / exception technique -> `ExceptionTechniqueGateway`

### 3.2) rechercherTous()

- Retourne toujours une liste non `null` (éventuellement vide)
- Tri + dédoublonnage selon la stratégie du gateway

### 3.3) rechercherTousParPage(RequetePage)

- Si requête `null` -> pagination par défaut
- Le résultat doit être cohérent :
  - `content` non `null`
  - `pageNumber` cohérent
  - `pageSize` cohérent
  - `totalElements` cohérent

**Cas limite à documenter / verrouiller par test :**
- `pageSize == 0`
  - soit page vide avec `totalElements` conservé,
  - soit comportement délégué de Spring Data si le gateway le conserve explicitement.

### 3.4) findByLibelle(String)

- Le libellé exact est une opération technique du gateway
- Le comportement sur `blank` relève de l'exception applicative du port
- Le comportement sur stockage KO relève de `ExceptionTechniqueGateway`

### 3.5) update(TypeProduit)

#### Contrat : libellé existant (collision)

**Comportement réel recherché et documenté :**
- pas d'exception ;
- pas de modification ;
- retour de l'objet persistant inchangé.

Exemple de scénario de référence :

```java
final TypeProduit t1 = service.findByLibelle("vêtement");
final TypeProduit t2 = service.findByLibelle("bazar");

final Long id2 = t2.getIdTypeProduit();

/* Tentative de collision. */
final TypeProduit aModifier = new TypeProduit(id2, "vêtement");

/* Comportement réel : pas d'exception et pas de modification. */
final TypeProduit retour = service.update(aModifier);
```

### 3.6) delete(TypeProduit)

- L'absence en stockage est constatée techniquement par recherche préalable
- Le gateway ne produit pas de message utilisateur
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

### 3.7) count()

- Retourne un compteur technique `>= 0`
- Le stockage KO reste un cas de `ExceptionTechniqueGateway`

## 4) Fichiers de preuve à relire conjointement

- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
## 99) RT-AUTONOMIE-GATEWAY-RECODAGE-01 — Verrou d'autonomie de l'ADAPTER `TypeProduitGatewayJPAService`

### 99.1) Fichiers relus et vérité de codage

- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java` — 33209 octets, 1158 lignes LF, SHA-256 `bca2ae80b8abc42dee1668fba19eb62bf5ca57a381c005aaa31877a2c272db6f`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java` — 35734 octets, 1026 lignes LF, SHA-256 `5d210a35d477fd3f3bffd1f51a2020fc8c539d78b8571139dbaaa05b9ddc57f3`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java` — 262556 octets, 7057 lignes LF, SHA-256 `4ea611289c3c6d35416d18d0b3cef5f68fce0ae37086660adf1d8dd8abf405b6`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java` — 147931 octets, 4230 lignes LF, SHA-256 `b1028b48d4ad98325aad9e13c385b8ec8878fa64f41da4a85124828a27ac806c`

L'ADAPTER JPA est le point technique qui dialogue avec les DAO et convertisseurs. Il ne porte aucune logique UC, aucun DTO et aucun message utilisateur. Il transforme les erreurs techniques en exceptions Gateway et respecte strictement les messages exposés par le PORT.

### 99.2) Attributs, constantes et collaborateurs à conserver

#### Champs détectés
- `private final TypeProduitDaoJPA typeProduitDaoJPA;`
- `private static final Logger LOG = LogManager.getLogger(TypeProduitGatewayJPAService.class);`

#### Constantes explicites
```java
	private static final Logger LOG
		= LogManager.getLogger(TypeProduitGatewayJPAService.class);
```

#### Constructeur(s)

Constructeur unique d'injection Spring à conserver strictement :

```java
	public TypeProduitGatewayJPAService(
			@Qualifier("TypeProduitDaoJPA")
				final TypeProduitDaoJPA pTypeProduitDaoJPA) {
		super();
		this.typeProduitDaoJPA = pTypeProduitDaoJPA;
	}
```

Javadoc constructeur à conserver dans son intention et son formalisme :
- `CONSTRUCTEUR appelé automatiquement par SPRING` ;
- injection d'un `TypeProduitDaoJPA` ;
- avertissement explicite : ne surtout pas créer de constructeur d'arité nulle, faute de quoi Spring ne pourra plus injecter.

Règle stricte : conserver l'injection par constructeur validée avec `@Qualifier("TypeProduitDaoJPA")`, ne jamais la remplacer par une injection de champ, un constructeur vide, un constructeur d'arité nulle ou une autre forme d'injection, ne pas créer de dépendance DTO, UC ou Controller, ne pas supprimer `LOG` même s'il est peu utilisé.

### 99.3) Ordre exact des méthodes de l'ADAPTER

- ligne 141 : `public TypeProduit creer(final TypeProduit pObject)`
- ligne 216 : `public List<TypeProduit> rechercherTous()`
- ligne 276 : `public ResultatPage<TypeProduit> rechercherTousParPage(final RequetePage pRequetePage)`
- ligne 357 : `public TypeProduit findByObjetMetier(final TypeProduit pObject)`
- ligne 396 : `public TypeProduit findByLibelle(final String pLibelle)`
- ligne 456 : `public List<TypeProduit> findByLibelleRapide(final String pContenu)`
- ligne 532 : `public TypeProduit findById(final Long pId)`
- ligne 597 : `public TypeProduit update(final TypeProduit pObject)`
- ligne 744 : `public void delete(final TypeProduit pObject)`
- ligne 819 : `public long count()`
- ligne 869 : `private List<TypeProduit> filtrerTrierDedoublonner(final List<TypeProduitJPA> pListe)`
- ligne 948 : `private static boolean appliquerModifications(final TypeProduitJPA pPersistant, final TypeProduit pObject)`
- ligne 1015 : `private Pageable convertirRequetePageEnPageable(final RequetePage pRequetePage)`
- ligne 1095 : `private static boolean isBlank(final String pString)`
- ligne 1117 : `private static String safeMessage(final Object p)`
- ligne 1145 : `private static boolean safeEquals(final Object p1, final Object p2)`

Cet ordre doit être conservé au recodage : méthodes du PORT d'abord, puis méthodes techniques internes dans l'ordre validé par la classe.

### 99.4) Algorithmes et comportements à préserver


- `creer` : refuse paramètre `null`, libellé blank, sauvegarde DAO `null`, exceptions DAO ; convertit métier→JPA puis JPA→métier.
- `rechercherTous` : DAO `findAll()`, refuse retour DAO `null`, filtre les éléments `null`, convertit, trie case-insensitive, dédoublonne.
- `rechercherTousParPage` : requête `null` -> `RequetePage` par défaut ; conversion vers `Pageable` ; refuse page JPA `null` et contenu `null` ; convertit le contenu et reconstruit `ResultatPage`.
- `findByObjetMetier` : refuse objet `null` et libellé blank ; délègue au libellé exact.
- `findByLibelle` : refuse libellé blank ; DAO `findByTypeProduitIgnoreCase`; `Optional.empty()` -> `null`; retour DAO `null` -> `ExceptionTechniqueGateway`.
- `findByLibelleRapide` : refuse contenu `null`; contenu blank -> `rechercherTous`; DAO containing ignore case ; liste `null` -> exception technique ; tri/dédoublonnage.
- `findById` : ID `null` -> `ExceptionAppliParamNull`; DAO `findById`; Optional `null` -> exception technique ; absent -> `null`.
- `update` : refuse paramètre `null`, libellé blank, ID `null`; absent -> `null`; collision de libellé existant sur autre ID -> retour de l'objet persistant inchangé ; sans modification -> retour sans sauvegarde ; modification -> `save` puis conversion.
- `delete` : refuse paramètre `null` et ID `null`; absent -> ne fait rien ; nominal -> `delete`.
- `count` : délègue à DAO `count`, exception DAO -> `ExceptionTechniqueGateway`.
- `convertirRequetePageEnPageable` : copie les tris valides seulement, ignore propriété blank, direction `DESC` sinon `ASC`, compose les `Sort`.


### 99.5) Tests Mock associés, par bloc de responsabilité

#### Bloc `Creer`
- ligne 328 : `testCreerNull`
- ligne 361 : `testCreerLibelleBlank`
- ligne 398 : `testCreerDAOSaveRetourneNull`
- ligne 456 : `testCreerDAOSaveExceptionMessageNonNull`
- ligne 528 : `testCreerDAOSaveExceptionMessageNull`
- ligne 604 : `testCreerDoublon`
- ligne 689 : `testCreerNominal`

#### Bloc `RechercherTousParPage`
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

#### Bloc `RechercherTous`
- ligne 761 : `testRechercherTousDAORetourneNull`
- ligne 806 : `testRechercherTousDAORetourneVide`
- ligne 856 : `testRechercherTousDAOExceptionMessageNonNull`
- ligne 924 : `testRechercherTousDAOExceptionMessageNull`
- ligne 1003 : `testRechercherTousTriDedoublonnage`
- ligne 1078 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
- ligne 2278 : `testFindByObjetMetierNull`
- ligne 2311 : `testFindByObjetMetierLibelleBlank`
- ligne 2353 : `testFindByObjetMetierDAOExceptionMessageNonNull`
- ligne 2425 : `testFindByObjetMetierDAOExceptionMessageNull`
- ligne 2504 : `testFindByObjetMetierNonTrouve`
- ligne 2565 : `testFindByObjetMetierNominal`

#### Bloc `FindByLibelleRapide`
- ligne 2959 : `testFindByLibelleRapideNull`
- ligne 2994 : `testFindByLibelleRapideBlank`
- ligne 3063 : `testFindByLibelleRapideDAORetourneNull`
- ligne 3117 : `testFindByLibelleRapideDAOExceptionMessageNonNull`
- ligne 3190 : `testFindByLibelleRapideDAOExceptionMessageNull`
- ligne 3269 : `testFindByLibelleRapideNonTrouve`
- ligne 3335 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 2628 : `testFindByLibelleNull`
- ligne 2661 : `testFindByLibelleBlank`
- ligne 2696 : `testFindByLibelleDAOExceptionMessageNonNull`
- ligne 2768 : `testFindByLibelleDAOExceptionMessageNull`
- ligne 2847 : `testFindByLibelleNonTrouve`
- ligne 2902 : `testFindByLibelleNominal`

#### Bloc `FindById`
- ligne 3429 : `testFindByIdNull`
- ligne 3468 : `testFindByIdDAORetourneNull`
- ligne 3517 : `testFindByIdDAOExceptionMessageNonNull`
- ligne 3581 : `testFindByIdDAOExceptionMessageNull`
- ligne 3646 : `testFindByIdNonTrouve`
- ligne 3703 : `testFindByIdNominal`

#### Bloc `Update`
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

#### Bloc `Delete`
- ligne 5061 : `testDeleteNull`
- ligne 5093 : `testDeleteIdNull`
- ligne 5129 : `testDeleteDAOFindByIdRetourneNull`
- ligne 5188 : `testDeleteAbsent`
- ligne 5248 : `testDeleteDAOFindByIdExceptionMessageNonNull`
- ligne 5326 : `testDeleteDAOFindByIdExceptionMessageNull`
- ligne 5411 : `testDeleteDAODeleteExceptionMessageNonNull`
- ligne 5494 : `testDeleteDAODeleteExceptionMessageNull`
- ligne 5582 : `testDeleteNominal`

#### Bloc `Count`
- ligne 5640 : `testCountStockageVide`
- ligne 5692 : `testCountDAOExceptionMessageNonNull`
- ligne 5760 : `testCountDAOExceptionMessageNull`
- ligne 5834 : `testCountNominal`

#### Bloc `ConvertirRequetePageEnPageable`
- ligne 5891 : `testConvertirRequetePageEnPageableNull`
- ligne 5936 : `testConvertirRequetePageEnPageableTrisNull`
- ligne 5985 : `testConvertirRequetePageEnPageableTrisVides`
- ligne 6034 : `testConvertirRequetePageEnPageableTrisValides`
- ligne 6097 : `testConvertirRequetePageEnPageableTrisInvalides`

#### Bloc `FiltrerTrierDedoublonner`
- ligne 6166 : `testFiltrerTrierDedoublonnerNull`
- ligne 6207 : `testFiltrerTrierDedoublonnerVide`
- ligne 6248 : `testFiltrerTrierDedoublonnerAvecNulls`
- ligne 6307 : `testFiltrerTrierDedoublonnerCaseInsensitive`
- ligne 6371 : `testFiltrerTrierDedoublonnerTri`

#### Bloc `AppliquerModifications`
- ligne 6430 : `testAppliquerModificationsAvecNull`
- ligne 6483 : `testAppliquerModificationsSansModification`
- ligne 6529 : `testAppliquerModificationsAvecModification`

#### Bloc `SafeEquals`
- ligne 6575 : `testSafeEqualsAvecNull`
- ligne 6631 : `testSafeEqualsAvecObjetsEgaux`
- ligne 6668 : `testSafeEqualsAvecObjetsDifferents`

#### Bloc `IsBlank`
- ligne 6705 : `testIsBlankAvecNull`
- ligne 6742 : `testIsBlankAvecChaineVide`
- ligne 6779 : `testIsBlankAvecChaineBlanche`
- ligne 6816 : `testIsBlankAvecChaineValide`

#### Bloc `SafeMessage`
- ligne 6853 : `testSafeMessageAvecNull`
- ligne 6890 : `testSafeMessageAvecObjetValide`
- ligne 6927 : `testSafeMessageAvecToStringNull`

### 99.6) Tests d'intégration associés, par bloc de responsabilité

#### Bloc `Creer`
- ligne 544 : `testCreerNull`
- ligne 601 : `testCreerBlank`
- ligne 671 : `testCreerLibelleExistant`
- ligne 771 : `testCreerNominal`
- ligne 882 : `testCreerPlusieurs`

#### Bloc `RechercherTousParPage`
- ligne 1146 : `testRechercherTousParPageNull`
- ligne 1224 : `testRechercherTousParPageAvecTri`
- ligne 1301 : `testRechercherTousParPageVide`
- ligne 1352 : `testRechercherTousParPageTailleSuperieure`
- ligne 1412 : `testRechercherTousParPageHorsBornes`
- ligne 1465 : `testRechercherTousParPageTailleZero`
- ligne 1538 : `testRechercherTousParPageNominal`

#### Bloc `RechercherTous`
- ligne 1010 : `testRechercherTousBaseVide`
- ligne 1058 : `testRechercherTousNominal`

#### Bloc `FindByObjetMetier`
- ligne 1627 : `testFindByObjetMetierNull`
- ligne 1656 : `testFindByObjetMetierLibelleNull`
- ligne 1692 : `testFindByObjetMetierBlank`
- ligne 1730 : `testFindByObjetMetierNonTrouve`
- ligne 1795 : `testFindByObjetMetierNominal`
- ligne 1853 : `testFindByObjetMetierIdIgnoreCasseIgnoree`

#### Bloc `FindByLibelleRapide`
- ligne 2182 : `testFindByLibelleRapideNull`
- ligne 2213 : `testFindByLibelleRapideBlank`
- ligne 2269 : `testFindByLibelleRapideNonTrouve`
- ligne 2315 : `testFindByLibelleRapideCaseInsensitive`
- ligne 2387 : `testFindByLibelleRapideDedoublonnage`
- ligne 2429 : `testFindByLibelleRapideNominal`

#### Bloc `FindByLibelle`
- ligne 1958 : `testFindByLibelleNull`
- ligne 1987 : `testFindByLibelleBlank`
- ligne 2018 : `testFindByLibelleNonTrouve`
- ligne 2072 : `testFindByLibelleCaseInsensitive`
- ligne 2128 : `testFindByLibelleNominal`

#### Bloc `FindById`
- ligne 2502 : `testFindByIdNull`
- ligne 2533 : `testFindByIdNonTrouve`
- ligne 2582 : `testFindByIdNominal`
- ligne 2639 : `testFindByIdIdCree`

#### Bloc `Update`
- ligne 2719 : `testUpdateNull`
- ligne 2776 : `testUpdateLibelleNull`
- ligne 2841 : `testUpdateLibelleBlank`
- ligne 2907 : `testUpdateIdNull`
- ligne 2975 : `testUpdateEntityInexistante`
- ligne 3050 : `testUpdateLibelleExistant`
- ligne 3172 : `testUpdateSansModification`
- ligne 3278 : `testUpdateNominal`

#### Bloc `Delete`
- ligne 3407 : `testDeleteNull`
- ligne 3462 : `testDeleteIdNull`
- ligne 3522 : `testDeleteIdInexistant`
- ligne 3592 : `testDeleteNominal`
- ligne 3708 : `testDeleteDoubleSuppression`

#### Bloc `Count`
- ligne 3849 : `testCountBaseVide`
- ligne 3911 : `testCountNominal`
- ligne 3976 : `testCountApresCreationPuisSuppression`

### 99.7) Règle anti-improvisation locale

L'IA ne doit jamais remplacer un helper privé par une version jugée équivalente sans reconfronter les tests ci-dessus. Les helpers `filtrerTrierDedoublonner`, `convertirRequetePageEnPageable`, `safeMessage`, `safeEquals`, `isBlank`, les résolutions de messages parent et `appliquerModifications` sont contractuels lorsqu'ils existent dans la classe validée.
