<!--
Contrat local généré par l'IA depuis la baseline consolidée au SHA ae857f1e26fd092f8ed9371c0e8b1a66c47c6518.
Ce fichier est un fichier fragile : il doit être livré complet et remplacé intégralement dans STS.
-->
# Contrat local de couche — Couche persistance

## 1) Objectif d'autonomie

La couche `couche_persistance` doit être décrite de façon assez prescriptive pour que l'IA puisse recoder quasiment à l'identique les fichiers validés, après application obligatoire du workflow `CONTRAT_IA.md` : relecture du contrat central, de la baseline consolidée, du présent contrat de couche, du contrat local, du fichier cible, des dépendances utiles et des tests concernés.

La couche IA ne doit pas être une simple carte générale. Elle doit empêcher l'IA d'improviser sur :

- les annotations JPA ;
- les interfaces transverses ;
- les constantes ;
- l'ordre des méthodes ;
- les helpers privés ;
- les relations bidirectionnelles ;
- les convertisseurs cycle-safe ;
- les DAO Spring Data ;
- les Javadocs et commentaires de bloc caractéristiques ;
- les tests de persistance déjà validés.

## 2) Frontière architecturale

La couche persistance manipule les entities JPA, les DAO JPA, les convertisseurs JPA ↔ métier et les interfaces transverses propres à la persistance.

Elle ne porte pas :

- logique UC ;
- logique Gateway ;
- logique Controller ;
- logique View ;
- DTO comme contrat applicatif ;
- message utilisateur applicatif.

La duplication de `IExportateurCsv` et `IExportateurJTable` côté persistance est volontaire. Elle sert l'autonomie stricte de la couche persistance et ne doit pas être supprimée au motif qu'un équivalent existe côté métier.

## 3) Périmètre validé à relire

### 3.1 Interfaces transverses persistance

```text
src/main/java/levy/daniel/application/persistence/metier/IExportateurCsv.java
src/main/java/levy/daniel/application/persistence/metier/IExportateurJTable.java
```

### 3.2 Entities JPA et convertisseurs

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java
```

### 3.3 DAO JPA

```text
src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPA.java
src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java
```

### 3.4 Tests directs de persistance

```text
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPARattachementDetachementTest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetierTest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/DaoJPATestConfig.java
src/test/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPATest.java
src/test/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPATest.java
```

## 4) Règles transverses de formalisme

Les fichiers validés de persistance ont un formalisme historique à conserver :

- séparateurs de tête `/* ********************************************************************* */` pour les DAO ;
- grands blocs `// ************************ATTRIBUTS************************************/` et `// *************************METHODES**********************************/` pour les entities et convertisseurs ;
- Javadocs HTML avec `<style>`, `<div>`, `<p>`, `<ul>`, `<li>` ;
- commentaires de fin de constructeur, méthode, classe ou interface ;
- commentaires de bloc expliquant les caches, les relations bidirectionnelles, les snapshots et les conversions ;
- ordre logique validé : attributs, logger, constructeurs, méthodes fondamentales, clonage, relations, CSV/JTable, getters/setters, helpers.

L'IA ne doit pas remplacer ce formalisme par un style moderne plus court.

## 5) Interfaces transverses à recoder quasiment à l'identique

### 5.1 `IExportateurCsv`

- Package : `levy.daniel.application.persistence.metier`.
- Interface publique, sans modificateur explicite sur les méthodes.
- Deux méthodes dans cet ordre strict :

```java
String getEnTeteCsv();
String toStringCsv();
```

Règles :

- la Javadoc explique l'export CSV ;
- `getEnTeteCsv()` fournit l'en-tête et doit rester transient côté implémentations JPA ;
- `toStringCsv()` fournit une ligne CSV et assume la représentation Java de `null` ;
- conserver le commentaire final d'interface.

### 5.2 `IExportateurJTable`

- Package : `levy.daniel.application.persistence.metier`.
- Interface publique.
- Deux méthodes dans cet ordre strict :

```java
String getEnTeteColonne(int pI);
Object getValeurColonne(int pI);
```

Règles :

- les colonnes sont `0-based` ;
- l'ordre suit le CSV ;
- les méthodes doivent rester transient côté implémentations JPA ;
- hors index connu, les implémentations retournent `"invalide"` ;
- conserver le commentaire final d'interface.

## 6) Règles entities JPA

Les trois entities JPA suivent les règles communes suivantes :

- `@Entity(name = "...JPA")` ;
- `@Access(AccessType.FIELD)` ;
- `@Table(...)` explicite avec nom de table, schéma, contrainte ou index selon la classe ;
- ID avec `@Id`, `@GeneratedValue(strategy=GenerationType.IDENTITY)` et `@Column(name="...")` ;
- libellé non nullable, insérable et modifiable ;
- parent `@ManyToOne(fetch = FetchType.LAZY, optional = false, targetEntity = ...JPA.class)` ;
- enfant `@OneToMany(targetEntity = ...JPA.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "...")` ;
- relations exposées via interfaces métier (`TypeProduitI`, `SousTypeProduitI`, `ProduitI`) malgré l'annotation `targetEntity` ;
- `getEnTeteCsv()`, `getEnTeteColonne(...)` et `getValeurColonne(...)` annotées `@Transient` ;
- `valide` est également `@Transient` quand il existe ;
- `equals(...)` doit privilégier l'ID quand les deux IDs sont présents, puis retomber sur l'égalité métier ;
- `hashCode()` doit être cohérent avec l'ID persistant et les tests de stabilité ;
- `compareTo(...)` doit être cohérent avec le parent puis le libellé normalisé ;
- `normalize(...)` applique `StringUtils.trimToEmpty(...).toLowerCase(Locale.ROOT)` ;
- les setters de libellé normalisent ;
- les setters de parent/enfants doivent maintenir les relations bidirectionnelles sans doublons.

## 7) Règles convertisseurs JPA ↔ métier

Les convertisseurs sont des classes utilitaires `final` :

- constructeur privé d'arité nulle ;
- aucune dépendance DTO ;
- conversion `null` ou libellé blank vers `null` ;
- `ConversionContext` interne fondé sur `IdentityHashMap` ;
- `SHARED_CACHE` statique thread-safe via `Collections.synchronizedMap(new IdentityHashMap<>())` ;
- `get(...)` consulte d'abord `SHARED_CACHE`, puis le cache local ;
- `put(...)` alimente les deux caches ;
- conversion parent/enfants par setters canoniques, jamais par écrasement brutal des collections ;
- sécurité LAZY : si une collection source est `null` ou non chargée, ne pas vider la collection cible ;
- `requireMetier(...)` et `requireJPA(...)` jettent `IllegalStateException` avec contexte si l'objet n'a pas l'implémentation attendue ;
- les helpers d'affichage privés sont conservés parce qu'ils verrouillent le diagnostic didactique historique.

## 8) Règles DAO JPA

Les DAO JPA sont des interfaces Spring Data :

- package `levy.daniel.application.persistence.metier.produittype.dao.daosJPA` ;
- bannière de tête `REPOSITORY DAO JPA` ;
- `@Repository("...DaoJPA")` ;
- extension `JpaRepository<...JPA, Long>` ;
- méthodes Spring Data exactement nommées, sans implémentation ;
- `findAll()` et `findAll(Pageable)` explicitement redéclarées avec `@Override` ;
- Javadocs explicatives conservées pour les méthodes utilisées par les Gateways ;
- aucun message applicatif, aucune logique UC, aucune conversion.

## 9) Tests DAO directs : correction du verrou de preuve

Contrairement à l'ancien état du contrat, la couche persistance porte bien des tests DAO directs validés. Ces tests sont sacralisés :

```text
TypeProduitDaoJPA: 4 tests
SousTypeProduitDaoJPA: 6 tests
ProduitDaoJPA: 6 tests
```

`DaoJPATestConfig.java` est également sacralisé. Il évite les scans concurrents en exécution groupée STS et ne doit pas être supprimé.

## 10) Inventaire global des tests persistance

```text
TypeProduitJPA: 25 tests
TypeProduitJPARattachementDetachement: 3 tests
SousTypeProduitJPA: 26 tests
ProduitJPA: 23 tests
ConvertisseurMetierToJPA: 16 tests
ConvertisseurJPAToMetier: 18 tests
TypeProduitDaoJPA: 4 tests
SousTypeProduitDaoJPA: 6 tests
ProduitDaoJPA: 6 tests
```

L'IA doit relire les tests concernés avant de modifier ou recoder le composant correspondant. Un test validé est une spécification.

## 11) Anti-improvisation persistance

Si un détail exact n'est pas recopié dans le contrat local, l'IA doit le reprendre depuis le code validé relu dans la baseline consolidée. Elle ne doit jamais remplacer :

- une annotation JPA par une annotation équivalente supposée ;
- une méthode privée par une simplification ;
- un setter canonique par une écriture directe d'attribut ;
- une relation bidirectionnelle par une collection non synchronisée ;
- un cache `IdentityHashMap` par `HashMap` ;
- un helper d'affichage par une sortie plus courte ;
- une Javadoc historique par une phrase générique.

## 12) Objectif de validation

La couche IA persistance est considérée suffisamment autonome si, après relecture obligatoire de la baseline consolidée, tous les éléments suivants sont couverts : interfaces, annotations, constantes, attributs, méthodes dans l'ordre, helpers privés, tests, relations, convertisseurs, DAO et configuration de test.
