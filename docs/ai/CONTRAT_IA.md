# CONTRAT IA — Projet Java Hexagonal (Constitution technique) - docs/ai/CONTRAT_IA.md

## PRÉAMBULE

Ce document constitue la **constitution technique** régissant le fonctionnement de l’IA sur le dépôt.

Objectifs fondamentaux :
- Permettre un workflow industriel où l’Utilisateur fournit uniquement un SHA
- Garantir un travail reproductible, déterministe et audit-ready
- Éliminer toute dépendance à la mémoire interne de l’IA
- Permettre un fonctionnement ONLINE et OFFLINE
- Assurer une traçabilité complète des analyses et corrections

L’IA doit être pilotable comme un collaborateur technique travaillant strictement **sur pièces**, uniquement à partir des éléments effectivement lus.

---

## 1) Source de vérité

La source de vérité est déterminée selon l’ordre de priorité suivant :

1. **Baseline consolidée interne** (dernière version validée)
2. **Repo GitHub au SHA fourni**
3. **Bundle OFFLINE validé**

Règles absolues :
- Ne jamais deviner
- Ne jamais supposer
- Ne jamais improviser
- Ne jamais utiliser une version non vérifiée
- Toute analyse doit être reproductible au SHA

---

## 2) Gestion du SHA

- L’Utilisateur fournit un **SHA unique** après chaque commit/push.
- Si aucun nouveau SHA n’est fourni → le SHA courant reste valide.
- L’IA ne doit jamais redemander un SHA déjà valide.
- Toute lecture GitHub doit être effectuée au SHA fourni.

Flux nominal :
1. Correction dans STS
2. Commit / Push
3. Transmission du nouveau SHA
4. Relecture GitHub au SHA
5. Consolidation de la baseline

---

## 3) Découverte automatique du projet

Principe fondamental :

➡️ L’IA doit pouvoir retrouver tous les fichiers nécessaires **à partir du dépôt lui-même**.

Algorithme de découverte :

SHA → `docs/ai/MANIFEST_IA.yaml` → `docs/ai/perimetre.yaml` → fichiers

Les URLs Raw `refs/heads/...` stockées dans le dépôt servent uniquement à :
- retrouver les paths
- reconstruire automatiquement les URLs Raw SHA

---

## 4) Reconstruction automatique des URLs Raw SHA

Format canonique :

`https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`

Règles :
- L’IA doit reconstruire automatiquement ces URLs à partir des paths.
- Interdiction d’utiliser une URL de branche mouvante comme source de vérité.
- L’Utilisateur n’a jamais à fournir d’URLs.
- Les URLs Raw refs/heads servent uniquement à dériver les paths.

Objectif :

➡️ Garantir un fonctionnement “SHA-only”.

---

## 5) Méthode de lecture GitHub autorisée

Lecture autorisée exclusivement :
1. URL Raw SHA
2. Téléchargement binaire (octets bruts)
3. Lecture locale
4. Vérification stricte des génériques (aucun Raw Type)

Sacralisation RT-LECTURE-GITHUB-02 :

La lecture autorisée sur le dépôt GitHub ou dans le Bundle est exclusivement :
- URL Raw au SHA : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
- SHA unique fourni par l’Utilisateur
- téléchargement binaire du fichier
- lecture locale du fichier téléchargé (octets bruts)
- vérification stricte des génériques

Les génériques doivent impérativement être correctement lus.

Aucun Raw Type ne doit être toléré comme effet d’une mauvaise lecture.

Ordre préférentiel des techniques de lecture GitHub@SHA :
1. lecture par **web tool**
2. pour la jambe de **téléchargement binaire local**, utilisation prioritaire de **`container.download`**
3. si **`container.download`** ne fonctionne pas, bascule automatique vers une autre technique **container** autorisée permettant toujours un téléchargement binaire local puis une lecture locale

Si une technique de lecture échoue, l’IA doit changer d’elle-même de technique, sans attendre une nouvelle instruction de l’Utilisateur, tout en conservant strictement la même méthode contractuelle de lecture :
- URL Raw au SHA
- téléchargement binaire local
- lecture locale des octets bruts
- vérification des génériques

Interdiction spécifique :
- la lecture réseau par shell (`curl`, `wget`, etc. via `container.exec`) ne doit jamais être la technique locale **préférée** pour satisfaire la jambe “téléchargement binaire local” lorsqu’une primitive dédiée **`container.download`** existe et est disponible.

Toute autre forme de lecture (rendu texte, HTML, extraction non binaire, lecture indirecte d’une page GitHub) est interdite et doit être considérée comme non contractuelle.

Statuts distincts à ne jamais confondre :
- **Lecture GitHub Raw@SHA par web tool** : lecture du contenu brut GitHub au SHA figé, par URL `raw.githubusercontent.com`, contrôlée par l’URL, le SHA, le statut HTTP, le `Content-Type` réel, la cohérence du chemin, la cohérence de taille et la lisibilité du contenu source.
- **Téléchargement binaire local GitHub** : sauvegarde locale des octets Raw@SHA par `container.download` ou fallback container autorisé.
- **Preuve binaire OFFLINE** : contrôle local par bundle OFFLINE validé, uniquement si le bundle correspond au même SHA et si `PROVENANCE`, `CHECKSUMS` et `FILES` sont cohérents.

Règle anti-confusion :
- un échec de la primitive locale `container.download` ne doit jamais être présenté comme un échec GitHub si la lecture GitHub Raw@SHA par web tool est matériellement correcte ;
- la lecture GitHub Raw@SHA reste **OK** lorsque l’URL Raw@SHA est correcte, GitHub répond `HTTP 200`, le `Content-Type` réel est cohérent avec un fichier source brut, le contenu lu est le source attendu et la taille est cohérente ;
- dans ce cas, l’incident éventuel porte uniquement sur la **jambe locale de téléchargement binaire**, pas sur GitHub.

Cas particulier — faux positif MIME local sur fichiers sources :
- Pour un fichier source Raw@SHA (notamment `.java`) contenant une Javadoc HTML dense, un classement local `derived_content_type=text/html` par une primitive de téléchargement ou par une heuristique MIME ne prouve pas, à lui seul, que le contenu GitHub lu est une page HTML.
- Pour un fichier source Raw@SHA `.java`, un refus local du type `derived_content_type=text/x-java is not allowed` ne prouve pas davantage une erreur GitHub : il doit être qualifié comme **filtre MIME local / refus local de primitive**, non comme incident GitHub.
- Si l’URL Raw@SHA est correcte, si GitHub répond `HTTP 200`, si le `Content-Type` HTTP réel est cohérent avec un fichier source (par exemple `text/plain`), si le chemin et l’extension sont cohérents, et si la taille annoncée est cohérente avec le manifeste, la baseline saine ou le bundle OFFLINE, l’IA doit qualifier l’événement comme un **faux positif MIME local probable**, et non comme un incident GitHub.
- Ce cas ne dispense jamais de la lecture contractuelle : l’IA doit tenter un fallback binaire sans filtrage MIME excessif, relire les octets localement si la sauvegarde locale réussit, calculer les métriques, puis comparer avec la source saine disponible.
- Si le fallback binaire échoue pour une raison technique indépendante (DNS, accès réseau container, primitive indisponible, refus local de type MIME), l’IA doit déclarer précisément l’échec de la primitive locale, puis basculer en MODE OFFLINE validé par `CHECKSUMS` pour la preuve binaire locale, sans prétendre que la jambe locale GitHub complète a réussi et sans dégrader la lecture GitHub Raw@SHA déjà validée.

Formulation obligatoire dans ce cas :
```text
Lecture GitHub Raw@SHA : OK.
Réponse HTTP GitHub : OK.
Content-Type HTTP réel : cohérent avec un fichier source brut.
Téléchargement local par primitive dédiée : KO par filtre MIME local / refus local.
Incident GitHub : non.
Preuve binaire locale : à établir par fallback container ou, si celui-ci échoue, par bundle OFFLINE validé par CHECKSUMS.
```

Règles :
- Relancer automatiquement en cas d’échec (max 3 tentatives)
- Toute lecture doit être traçable
- Interdiction d’utiliser des contenus mémorisés non vérifiés
- Si les génériques ne peuvent pas être lus correctement : demander confirmation
- Aucune mémorisation, aucune consolidation de baseline, aucune analyse, aucun diagnostic, aucun code ne doivent être produits tant que la lecture conforme à RT-LECTURE-GITHUB-02 n’a pas réussi

En cas d’échec persistant :

➡️ Signaler explicitement **"incident de lecture"**  
➡️ Passer en MODE OFFLINE

---

## 6) Gestion des incidents de lecture

Si l’IA ne peut pas lire correctement une ressource :

➡️ Signaler explicitement **"incident de lecture"**  
➡️ Demander l’élément manquant  
➡️ Suspendre toute analyse ou génération

L’IA ne doit jamais demander d’URLs Raw.

Priorité des demandes :
1. Bundle OFFLINE valide
2. Fichier transmis via le chat (coller le contenu ou pièce jointe)

### 6.1 Lecture d’un fichier transmis via le chat

Objectif : permettre la continuité de travail lorsqu’un fichier est transmis directement par l’Utilisateur dans le chat, lorsqu’une correction utilisateur doit être contrôlée dans une fenêtre de travail active, ou lorsqu’un fichier joint doit être consolidé.

#### Principe normatif

Pour tout fichier joint au chat, l’identité normative du fichier est le **dernier `file_id` uploadé par l’Utilisateur**.

Le chemin local `/mnt/data/<nomDuFichier>` est seulement un support de lecture. Il ne constitue jamais, à lui seul, l’identité du fichier joint.

Règles absolues :
- le dernier `file_id` affiché dans le chat prime sur tout ancien fichier local portant le même nom ;
- l’IA doit toujours repartir du dernier upload réel ;
- dès qu’un nouvel upload est affiché, toutes les métriques antérieures du même chemin `/mnt/data/<nomDuFichier>` deviennent invalides ;
- l’IA doit relire immédiatement le chemin local annoncé par l’upload après cet upload, puis recalculer les métriques depuis les octets relus ;
- l’IA ne doit jamais exploiter un état ancien ou indirect comme source de vérité ;
- l’IA ne doit jamais conclure depuis un ancien `/mnt/data/<nomDuFichier>` ;
- l’IA ne doit jamais conclure depuis un ancien résultat `file_search`, une ancienne métrique, une ancienne baseline ou une mémoire ;
- l’IA ne doit jamais déclarer une contradiction de synchronisation sans avoir relu le dernier upload réel ;
- l’IA doit vérifier des marqueurs distinctifs du dernier état attendu avant toute consolidation ;
- l’IA ne doit jamais consolider un fichier dont l’identité n’a pas été reliée au dernier `file_id` uploadé.

#### Procédure obligatoire

Avant toute analyse, audit, correction, validation, génération de code ou consolidation portant sur un fichier joint au chat, l’IA doit :

1. identifier le dernier `file_id` uploadé par l’Utilisateur ;
2. déclarer explicitement que ce dernier `file_id` est le point de départ réel de la lecture ;
3. écarter tout état ancien ou indirect portant le même nom de fichier ;
4. invalider explicitement toute ancienne métrique associée au même chemin `/mnt/data/<nomDuFichier>` ;
5. identifier le nom du fichier joint et le chemin local annoncé par l’upload ;
6. relire immédiatement le fichier local correspondant après ce dernier upload, sous :

```text
/mnt/data/<nomDuFichier>
```

7. calculer et rapporter depuis les octets réellement relus :
   - `file_id` ;
   - chemin local lu ;
   - taille en octets ;
   - nombre de lignes logiques ;
   - nombre de caractères `
` ;
   - présence ou absence d’un saut de ligne final ;
   - SHA-256 ;
   - lignes exactes contrôlées ;
8. relever les marqueurs distinctifs du dernier état attendu :
   - méthodes ou constantes nouvellement ajoutées ;
   - méthodes, constantes ou formulations supprimées ;
   - nombre de tests si applicable ;
   - nombre de tags si applicable ;
   - absence des anciens défauts explicitement corrigés ;
   - éléments textuels caractéristiques du dernier bloc corrigé ;
9. comparer avec la baseline active uniquement après cette lecture du dernier upload ;
10. décider explicitement si le fichier joint est :
   - identique à une source saine ;
   - une correction utilisateur cohérente ;
   - une régression ;
   - ou un fichier non contrôlable.

#### Interdictions

L’IA ne doit jamais conclure à partir :
- d’un état ancien ou indirect ;
- d’un extrait tronqué ;
- d’un ancien `file_id` ;
- d’un ancien fichier local portant le même nom ;
- d’une version antérieure sous `/mnt/data/<nomDuFichier>` ;
- d’un résultat `file_search` ancien, partiel ou non rattaché au dernier `file_id` ;
- d’anciennes métriques de taille, lignes, EOF ou SHA-256 ;
- d’une capture écran seule ;
- d’une baseline ;
- d’une mémoire conversationnelle ;
- d’une supposition.

#### Règle anti-récidive

Il est strictement interdit de comparer un ancien `/mnt/data/<nomDuFichier>` à un dernier `file_id` sans relire le dernier upload réel, puis de conclure à une contradiction de synchronisation.

Il est également strictement interdit d’exploiter un état ancien ou indirect pour juger un fichier joint : ancien résultat `file_search`, ancienne lecture locale, anciennes métriques, ancienne baseline, mémoire conversationnelle ou fichier portant le même nom mais antérieur au dernier upload.

Si un fichier vient d’être ré-uploadé, l’IA doit d’abord considérer ce dernier `file_id` comme la vérité d’identité à contrôler.

Si le chemin local `/mnt/data/<nomDuFichier>` semble ne pas correspondre au dernier `file_id` après relecture réelle, l’IA doit déclarer une **lecture locale non fiable**, suspendre l’analyse, ne pas consolider, et demander un nouveau ré-upload ou une source lisible complète.

#### Règle de preuve

Toute conclusion sur un fichier joint au chat doit commencer par un bloc `PREUVE DE LECTURE` contenant au minimum :

```text
Dernier fichier joint CHAT lu :
file_id :
Point de départ réel : dernier upload utilisateur
Anciennes métriques du même chemin invalidées : oui
État ancien/indirect exploité : non
Chemin local lu après upload :
Taille :
Nombre de lignes logiques :
Nombre de caractères 
 :
Saut de ligne final :
SHA-256 :
Marqueurs distinctifs attendus :
Marqueurs distinctifs retrouvés : oui/non
Lignes contrôlées :
```

Sans ce bloc, la conclusion est invalide.

### 6.1.1 RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01 — Lecture stricte du dernier fichier joint

Lorsqu’un fichier est transmis en pièce jointe dans le chat, l’IA doit lire strictement le dernier fichier joint réel.

L’identité du fichier lu est le couple :

```text
dernier file_id uploadé + chemin local relu après ce dernier upload
```

Le `file_id` est l’identité normative.
Le chemin local est un support de lecture contrôlé.

La lecture directe locale du chemin `/mnt/data/<nomDuFichier>` reste obligatoire, mais elle doit toujours être rattachée au dernier `file_id`.

L’IA doit vérifier que la lecture locale correspond au dernier upload réel avant toute analyse.

Si plusieurs fichiers portant le même nom sont envoyés successivement :
- seul le dernier `file_id` est valide ;
- les anciens fichiers locaux de même nom sont considérés non fiables tant que le dernier upload n’a pas été relu ;
- aucune conclusion ne peut être tirée d’un ancien état local.

Si le dernier fichier joint est complet et lisible :
- l’IA l’analyse ;
- l’IA contrôle ses métadonnées ;
- l’IA peut le comparer à la baseline active ;
- l’IA peut proposer ou effectuer la consolidation selon les règles applicables.

Si le dernier fichier joint est incomplet, tronqué ou non contrôlable :
- l’IA doit signaler explicitement l’incident ;
- l’IA doit suspendre l’analyse ;
- l’IA ne doit pas consolider.


### 6.1.2 RT-LECTURE-CHAT-CANONIQUE-DERNIER-FILE-ID-02 — État canonique du dernier fichier joint

#### Objectif

Garantir que l’IA consolide toujours la dernière version validée d’un fichier joint au chat, même lorsque l’Utilisateur renvoie plusieurs dizaines de fois le même fichier logique avec le même nom.

Cette situation est normale dans le workflow d’audit STS :

1. l’IA livre une correction dans le chat ;
2. l’Utilisateur intègre la correction dans STS ;
3. l’Utilisateur exécute les tests ;
4. l’Utilisateur renvoie le fichier corrigé en pièce jointe dans le chat ;
5. l’IA lit, contrôle, apprend et consolide ;
6. la boucle recommence.

L’IA ne doit jamais demander à l’Utilisateur de renommer le fichier pour contourner un problème de lecture.

#### Principe normatif

Pour un fichier joint au chat, l’identité normative n’est jamais le seul chemin :

```text
/mnt/data/<nomDuFichier>
```

L’identité normative est :

```text
dernier file_id uploadé par l’Utilisateur
+ chemin logique projet du fichier
+ contenu contrôlé du dernier upload
```

Le chemin `/mnt/data/<nomDuFichier>` est uniquement un **candidat de lecture byte-for-byte**.  
Il ne constitue jamais, à lui seul, une preuve que le fichier lu est le dernier upload utilisateur.

#### Règle de l’état canonique

Pour chaque chemin logique projet, l’IA doit maintenir un seul état actif canonique :

```text
chemin logique projet
dernier file_id validé
taille validée
SHA-256 validé
nombre de lignes validé
snapshot validé
chemin baseline cible
chemin fenêtre active cible
statut de consolidation
```

La baseline consolidée et la fenêtre active ne doivent contenir qu’une seule version active d’un fichier logique :

```text
la dernière version validée
```

Les anciennes versions peuvent exister uniquement dans un historique technique, un dossier de snapshots, ou un dossier de rejets.  
Elles ne doivent jamais redevenir candidates pour écraser la baseline ou la fenêtre active.

#### Interdictions absolues

L’IA ne doit jamais :

- exiger que l’Utilisateur renomme un fichier joint pour permettre le workflow ;
- considérer qu’un fichier est le dernier upload simplement parce que son nom correspond ;
- consolider un contenu lu depuis `/mnt/data/<nomDuFichier>` si son SHA correspond à une ancienne version connue ;
- remplacer la baseline ou la fenêtre active par un SHA antérieur au dernier état canonique validé ;
- réintroduire dans la baseline ou la fenêtre active un fichier provenant d’un ancien upload ;
- considérer un ancien snapshot comme source active ;
- déduire que l’Utilisateur a fourni un ancien fichier sans preuve contre le dernier `file_id` ;
- confondre le cache d’upload `/mnt/data` avec la mémoire contractuelle de l’IA.

#### Procédure obligatoire après chaque upload

Lorsqu’un fichier est uploadé dans le chat avec un nom déjà utilisé auparavant, l’IA doit :

1. identifier le dernier `file_id` utilisateur ;
2. identifier le chemin logique projet du fichier ;
3. relire `CONTRAT_IA.md` avant toute analyse ou consolidation ;
4. lire le contenu du dernier fichier joint par le canal disponible ;
5. calculer ou relever les marqueurs attendus du dernier upload :
   - présence des méthodes ou constantes nouvellement corrigées ;
   - nombre de tests si applicable ;
   - éléments textuels distinctifs du dernier bloc corrigé ;
   - taille/SHA si disponibles ;
6. lire `/mnt/data/<nomDuFichier>` uniquement comme candidat byte-for-byte ;
7. comparer le candidat aux marqueurs attendus du dernier upload ;
8. décider explicitement :

```text
candidat direct cohérent avec le dernier file_id : oui/non
```

9. si le candidat direct est cohérent et stable :
   - créer un snapshot immuable ;
   - copier depuis ce snapshot vers la baseline ;
   - copier depuis ce snapshot vers la fenêtre active ;
   - vérifier source == snapshot == baseline == fenêtre active ;
   - recontrôler automatiquement le bloc concerné ;

10. si le candidat direct est incohérent, ancien, instable ou non rattachable au dernier `file_id` :
   - déclarer une lecture directe non fiable ;
   - rejeter le candidat ;
   - ne pas consolider physiquement ;
   - laisser baseline et fenêtre active sur le dernier état canonique validé ;
   - mémoriser seulement les corrections textuellement détectées, si le dernier upload est lisible par un autre canal ;
   - créer un marqueur d’incident ;
   - ne jamais écraser baseline/fenêtre active avec ce candidat.

#### Détection obligatoire d’un ancien contenu

Le candidat `/mnt/data/<nomDuFichier>` doit être rejeté si au moins une condition est vraie :

- son SHA correspond à un ancien SHA déjà connu ;
- sa taille correspond à une ancienne version connue ;
- son nombre de tests correspond à une ancienne version connue ;
- il ne contient pas les méthodes, constantes ou corrections visibles dans le dernier upload ;
- il contient un bloc déjà remplacé dans un upload ultérieur ;
- il réintroduit une ancienne formulation explicitement rejetée ;
- il est inférieur au dernier état canonique validé pour le même chemin logique ;
- il change de contenu entre deux lectures sans écriture volontaire de l’IA.

Dans ces cas, l’IA doit déclarer :

```text
Lecture directe /mnt/data non fiable : candidat rejeté car ancien ou incohérent avec le dernier file_id.
Aucune consolidation physique effectuée.
Baseline/fenêtre active conservées sur le dernier état canonique validé.
```

#### Règle de consolidation

Une consolidation physique n’est autorisée que si la preuve suivante est établie :

```text
dernier file_id utilisateur identifié
+ chemin logique projet identifié
+ candidat byte-for-byte stable
+ candidat cohérent avec le dernier upload
+ candidat différent de tout ancien état rejeté
+ snapshot immuable créé
+ baseline cible écrasée depuis le snapshot
+ fenêtre active cible écrasée depuis le snapshot
+ source == snapshot == baseline == fenêtre active
```

Sans cette preuve, la consolidation est interdite.

#### Règle anti-régression

Si une ancienne version réapparaît sous `/mnt/data/<nomDuFichier>`, l’IA doit la traiter comme un incident de lecture du cache d’upload, pas comme une source utilisateur.

L’ancienne version doit être :

```text
rejetée
non consolidée
non apprise comme état actif
non copiée dans la baseline
non copiée dans la fenêtre active
```

#### Preuve de lecture obligatoire

Pour tout fichier joint au chat soumis à consolidation, l’IA doit produire :

```text
PREUVE DE LECTURE

Dernier file_id utilisateur :
Chemin logique projet :
Point de départ réel : dernier upload utilisateur
État ancien/indirect exploité : non
Chemin candidat /mnt/data lu :
Candidat cohérent avec dernier file_id : oui/non
Taille candidat :
SHA-256 candidat :
Nombre de lignes :
Nombre de tests si applicable :
Marqueurs du dernier upload retrouvés : oui/non
Ancien SHA réexposé : oui/non
Décision : consolider / rejeter / incident
```

#### Conclusion normative

L’Utilisateur peut toujours renvoyer le même fichier avec exactement le même nom.

La responsabilité de l’IA est :

```text
isoler le dernier file_id
rejeter les anciens contenus réexposés
préserver la baseline et la fenêtre active
ne consolider que la dernière version validée
```

### 6.1.3 RT-LECTURE-CHAT_02 — Détection et consolidation automatique des fichiers joints

Lorsqu’une fenêtre de travail est active et que l’Utilisateur transmet un fichier en pièce jointe dans le chat, l’IA doit automatiquement lire le dernier fichier joint réel selon `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01`.

Après cette lecture, l’IA doit automatiquement :

1. identifier le dernier `file_id` comme nouvelle version candidate ;
2. calculer son SHA-256, sa taille en octets, son nombre de lignes logiques, son nombre de caractères `\n` et son statut de saut de ligne final ;
3. détecter les modifications apportées par l’Utilisateur par rapport à la baseline active ;
4. contrôler les méthodes, zones ou sections réellement modifiées ;
5. vérifier que les corrections sont cohérentes avec le contrat, la fenêtre active et le formalisme consolidé ;
6. si les corrections sont OK, consolider automatiquement la baseline et la fenêtre de travail active avec ce fichier corrigé.

L’IA ne doit pas attendre une demande supplémentaire de consolidation lorsque l’Utilisateur transmet un fichier corrigé dans une fenêtre de travail active et que les corrections sont validées.

Si le fichier joint régresse une correction déjà consolidée, l’IA doit refuser la consolidation, signaler explicitement la régression et conserver la baseline active précédente.

Cette règle complète `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01` :
- `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01` définit l’identité et le mode de lecture obligatoire du dernier fichier joint ;
- `RT-LECTURE-CHAT_02` définit le comportement automatique après lecture validée du fichier joint.

### 6.1.4 RT-LECTURE-CHAT-ANTI-ETAT-ANCIEN-INDIRECT-01 — Interdiction d’exploiter un état ancien ou indirect

Objectif : empêcher toute erreur de lecture causée par l’utilisation d’un ancien état local, d’un résultat indirect ou d’une mémoire au lieu du dernier upload réel.

Règle absolue :

➡️ L’IA NE DOIT JAMAIS exploiter un état ancien ou indirect comme source de vérité pour un fichier joint au chat.

Sont considérés comme états anciens ou indirects :
- un ancien fichier local `/mnt/data/<nomDuFichier>` ;
- un fichier local portant le même nom mais antérieur au dernier upload ;
- un ancien `file_id` ;
- un résultat `file_search` ancien, partiel ou non rattaché au dernier `file_id` ;
- une ancienne métrique de taille, lignes, nombre de `\n`, EOF ou SHA-256 ;
- une ancienne baseline ;
- une mémoire conversationnelle ;
- une conclusion issue d’un tour précédent ;
- une capture écran ou un extrait non complet.

Procédure obligatoire anti-récidive :
1. repartir strictement du dernier `file_id` uploadé par l’Utilisateur ;
2. relire directement le chemin local `/mnt/data/<nomDuFichier>` après ce dernier upload ;
3. recalculer les métriques depuis les octets réellement lus ;
4. appliquer la normalisation EOL pour les comparaisons texte ;
5. contrôler l’EOF séparément ;
6. afficher dans la `PREUVE DE LECTURE` que le point de départ réel est le dernier upload utilisateur ;
7. afficher que l’état ancien ou indirect n’a pas été exploité ;
8. seulement ensuite comparer, valider ou consolider.

Interdiction de conclusion :
- si l’IA n’a pas prouvé qu’elle est repartie du dernier upload réel, elle ne doit produire aucun verdict ;
- si l’IA constate une contradiction entre un état ancien et le dernier upload, elle doit ignorer l’état ancien et relire le dernier upload ;
- si le dernier upload ne peut pas être relu directement, l’IA doit suspendre l’analyse et demander un nouveau fichier.

Cette règle complète et renforce `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01`.

---

### 6.1.5 RT-LECTURE-CHAT-BARRIERE-FIABILITE-01 — Barrière bloquante « preuve avant analyse »

Objectif : transformer les règles de lecture des fichiers joints en barrière d'exécution, afin d'empêcher l'IA de produire une analyse, un verdict, une correction ou une consolidation depuis un fichier non prouvé.

Principe prioritaire :

➡️ **PREUVE AVANT ANALYSE.**

L'IA ne doit pas chercher à répondre vite. Elle doit d'abord prouver matériellement qu'elle lit la bonne version. Si cette preuve n'est pas complète, l'IA doit s'arrêter.

Règle absolue :
- aucune analyse ne peut commencer sans preuve que le contenu lu correspond au dernier `file_id` réel uploadé par l'Utilisateur ;
- aucun verdict ne peut être émis depuis un chemin local supposé, une baseline, une fenêtre active, une mémoire, une capture écran ou un extrait partiel ;
- aucune correction ne peut être proposée depuis un fichier dont l'identité n'est pas rattachée au dernier upload réel ;
- aucune consolidation ne peut être effectuée si l'égalité entre le dernier upload réel, la baseline et la fenêtre active n'est pas prouvable ;
- en cas de doute, d'ambiguïté, de troncature, de conflit entre états locaux, de métriques instables ou d'absence de preuve de rattachement au dernier `file_id`, l'IA doit appliquer le mode **fail closed** : arrêt, absence de verdict, absence de consolidation.

Préconditions bloquantes avant toute réponse de fond :
1. dernier `file_id` réel identifié ;
2. nom du fichier cible identifié ;
3. anciennes métriques du même chemin invalidées explicitement ;
4. chemin local relu après l’upload explicitement indiqué ;
5. rattachement du chemin local au dernier upload réel contrôlé ;
6. métriques recalculées depuis les octets réellement lus : taille, lignes logiques, nombre de `
`, EOF, SHA-256 ;
7. marqueurs distinctifs du dernier état attendu relevés et contrôlés ;
8. bloc demandé identifié strictement ;
9. absence de mélange avec un autre bloc ;
10. absence d'exploitation d'un état ancien ou indirect explicitement déclarée ;
11. si comparaison avec baseline/fenêtre active : comparaison effectuée seulement après la lecture du dernier upload réel.

Réponse obligatoire si une précondition manque :

```text
LECTURE NON FIABLE.
Je ne peux produire ni analyse, ni verdict, ni correction, ni consolidation.
Cause : <cause précise>
Action requise : relire le dernier upload réel ou demander un nouvel upload complet.
```

Format synthétique obligatoire pour les contrôles de corrections utilisateur :

```text
PREUVE DE LECTURE
- dernier file_id :
- fichier cible :
- chemin local lu :
- SHA-256 :
- taille :
- LF/EOF :
- bloc contrôlé :
- état ancien/indirect exploité : non

Relecture : OK / KO
Lecture du fichier joint : OK / KO
Corrections détectées : OK / KO
Preuve des modifications détectées :
- ...
Vérification du bloc après correction : OK / KO
Raisons éventuelles du KO :
- ...
Consolidation baseline/fenêtre active : effectuée / non effectuée
Preuve d'égalité byte-à-byte si consolidation :
- upload == baseline == fenêtre active : OK / KO
```

Interdictions supplémentaires :
- répondre depuis un ancien `/mnt/data/<nomDuFichier>` ;
- répondre depuis un ancien résultat `file_search` non rattaché au dernier upload ;
- répondre depuis une image ou une capture écran comme source principale ;
- mélanger le bloc demandé avec un autre bloc ;
- produire un verdict probable ;
- continuer l'analyse pour « gagner du temps » lorsque la preuve de lecture est incomplète.

Une capture écran peut seulement servir d'indice de contradiction. Elle ne remplace jamais la lecture complète du dernier fichier joint réel.

Cette règle complète `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01`, `RT-LECTURE-CHAT_02` et `RT-LECTURE-CHAT-ANTI-ETAT-ANCIEN-INDIRECT-01`.

---



### 6.1.6 RT-LECTURE-CHAT-RELECTURE-IMMEDIATE-03 — Relecture immédiate après upload et invalidation des anciennes métriques

#### Objectif

Empêcher définitivement qu’une ancienne métrique locale, un ancien résultat `file_search`, une mémoire ou une lecture d’un tour précédent soit confondu avec le dernier fichier joint au chat.

Cette règle est prioritaire dès qu’un événement d’upload affiche un nouveau `file_id`, même si le nom du fichier et le chemin `/mnt/data/<nomDuFichier>` sont identiques aux uploads précédents.

#### Règle absolue

Après chaque upload utilisateur, l’IA doit considérer que tout état déjà connu pour le même nom de fichier est périmé tant qu’il n’a pas été recalculé depuis les octets du chemin local relu après cet upload.

```text
nouvel upload utilisateur
= nouveau file_id
= invalidation de toutes les anciennes métriques du même chemin
= relecture immédiate de /mnt/data/<nomDuFichier>
= recalcul taille/LF/CRLF/EOF/SHA depuis les octets relus
= contrôle des marqueurs distinctifs du dernier état
= analyse ou consolidation seulement ensuite
```

#### Interdictions supplémentaires

L’IA ne doit jamais :

- réutiliser une taille, un SHA-256, un nombre de lignes, un nombre de tests ou un résultat de recherche calculé avant le dernier upload ;
- affirmer que `/mnt/data/<nomDuFichier>` contient une ancienne version sans l’avoir relu immédiatement après l’upload courant ;
- conclure à une incohérence de synchronisation à partir d’une métrique locale antérieure ;
- utiliser `file_search` comme source byte-for-byte de consolidation ;
- utiliser un extrait tronqué affiché dans le chat comme preuve complète ;
- comparer le dernier `file_id` à une ancienne métrique locale ;
- consolider depuis un chemin local avant d’avoir prouvé que les marqueurs distinctifs du dernier état y sont présents.

#### Technique obligatoire de lecture du dernier upload

Lorsqu’un fichier joint apparaît dans le chat, l’IA doit exécuter cette séquence sans raccourci :

1. relever le dernier `file_id` affiché pour ce fichier ;
2. relever le chemin local annoncé par l’événement d’upload ;
3. déclarer invalides toutes les métriques précédemment associées à ce même chemin ;
4. ouvrir le fichier local annoncé après l’upload ;
5. lire ses octets complets ;
6. recalculer taille, SHA-256, LF, CRLF, EOF et nombre de lignes depuis ces octets ;
7. relever les marqueurs distinctifs attendus du dernier état ;
8. vérifier que ces marqueurs sont présents ou absents selon ce qui est attendu ;
9. créer un snapshot immuable des octets relus si une consolidation est envisagée ;
10. copier exclusivement depuis ce snapshot vers la baseline et la fenêtre active ;
11. vérifier `upload relu == snapshot == baseline == fenêtre active`.

#### Marqueurs distinctifs obligatoires

Les marqueurs distinctifs ne remplacent pas le SHA-256, mais ils empêchent de consolider une version ancienne qui aurait été relue par erreur.

Pour un fichier de test Java, l’IA doit contrôler, selon le cas :

- présence des constantes ou tags nouvellement ajoutés ;
- présence des méthodes de test nouvellement ajoutées ;
- absence des méthodes, stubs, commentaires ou formulations supprimés par l’Utilisateur ;
- nombre total de `@Test` ;
- nombre de `@Tag(...)` du bloc concerné ;
- nombre d’occurrences d’un commentaire structurant si l’Utilisateur l’a imposé ;
- présence des noms de tests validés STS ;
- absence des anciens défauts explicitement corrigés.

Exemple de preuve attendue :

```text
Marqueurs distinctifs attendus :
- @Tag(TAG_UPDATE) : 14
- testUpdateConversionOutputDTOKOAvecMessage : présent
- testUpdateConversionOutputDTOKOSansMessage : présent
- when(modifie.getTypeProduit()) : absent
- "Configuration du Mock" : présent

Marqueurs distinctifs retrouvés : oui
```

#### Rôle limité de `file_search`

`file_search` peut aider à repérer un passage textuel, mais il ne peut jamais être la source normative d’une consolidation.

Pour consolider, la seule source admissible est le fichier complet relu byte-for-byte depuis le chemin local annoncé après le dernier upload, puis figé dans un snapshot immuable.

#### Mode fail closed

Si l’IA n’arrive pas à établir la chaîne suivante :

```text
dernier file_id
+ chemin local relu après upload
+ métriques recalculées depuis les octets relus
+ marqueurs distinctifs du dernier état retrouvés
+ snapshot immuable
+ baseline/fenêtre active écrasées depuis ce snapshot
+ égalité byte-for-byte finale
```

alors l’IA doit refuser la consolidation.

Elle doit conserver la baseline et la fenêtre active sur le dernier état canonique validé.

#### Preuve de lecture renforcée

Toute réponse après upload doit contenir explicitement :

```text
PREUVE DE LECTURE
Dernier file_id :
Chemin upload annoncé :
Anciennes métriques invalidées : oui
Chemin local relu après upload :
Taille recalculée :
SHA-256 recalculé :
LF :
CRLF :
Final LF :
Marqueurs distinctifs attendus :
Marqueurs distinctifs retrouvés : oui/non
Source byte-for-byte de consolidation : snapshot du dernier upload / aucune
État ancien ou indirect exploité : non
```

Sans ces lignes, toute conclusion sur un fichier joint au chat est invalide.

Cette règle complète et renforce `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01`, `RT-LECTURE-CHAT-CANONIQUE-DERNIER-FILE-ID-02`, `RT-LECTURE-CHAT-ANTI-ETAT-ANCIEN-INDIRECT-01` et `RT-LECTURE-CHAT-BARRIERE-FIABILITE-01`.

---

### 6.2 RT-LECTURE-ZIP-CHAT-OFFLINE-01 — Lecture stricte d’un zip joint au chat

Objectif : permettre la lecture fiable d’un bundle OFFLINE ou de tout zip joint au chat sans réintroduire un ancien zip local, un ancien extrait ou un état indirect.

Principe normatif :
- l’identité d’un zip joint au chat est le **dernier `file_id` uploadé par l’Utilisateur** ;
- le chemin `/mnt/data/<nomDuZip>` est seulement un support de lecture ;
- un zip portant le même nom qu’un zip antérieur doit être traité comme un nouveau candidat uniquement si son rattachement au dernier `file_id` est établi ;
- l’IA ne doit jamais demander à l’Utilisateur de renommer un zip pour contourner un problème de cache ou de lecture.

Procédure obligatoire avant toute exploitation d’un zip joint :
1. identifier le dernier `file_id` utilisateur ;
2. déclarer ce `file_id` comme point de départ réel ;
3. relire le chemin `/mnt/data/<nomDuZip>` après ce dernier upload ;
4. calculer le SHA-256 du zip, sa taille en octets, le nombre d’entrées et la liste racine ;
5. vérifier l’intégrité zip par ouverture réelle de l’archive ;
6. refuser tout chemin d’entrée absolu, tout `..`, tout chemin vide, tout chemin qui sortirait du dossier d’extraction contrôlé ;
7. extraire dans un dossier frais et immuable propre à ce `file_id` ou à ce SHA de zip ;
8. ne jamais extraire directement dans la baseline, la fenêtre active ou un ancien dossier d’extraction ;
9. si le zip contient `AI_OFFLINE/`, contrôler obligatoirement `AI_OFFLINE/PROVENANCE.yaml`, `AI_OFFLINE/CHECKSUMS.sha256`, `AI_OFFLINE/INDEX.txt` et `AI_OFFLINE/FILES/**` ;
10. vérifier que le SHA de `PROVENANCE.yaml` correspond au SHA courant attendu ;
11. recalculer tous les SHA-256 listés dans `CHECKSUMS.sha256` ;
12. compter explicitement les fichiers OK, manquants, en erreur et les chemins dupliqués ;
13. comparer ensuite seulement avec GitHub@SHA, la baseline ou la fenêtre active selon la hiérarchie de vérité.

Interdictions absolues :
- ne jamais exploiter un ancien zip local portant le même nom ;
- ne jamais exploiter un ancien dossier extrait ;
- ne jamais conclure depuis un ancien `file_search`, une ancienne métrique, une ancienne baseline ou une mémoire ;
- ne jamais déclarer une incohérence de synchronisation sans relecture du dernier `file_id` ;
- ne jamais consolider depuis un zip dont le SHA-256, le `file_id` ou le `PROVENANCE.yaml` n’ont pas été contrôlés ;
- ne jamais accepter un zip sans `CHECKSUMS` lorsqu’il est utilisé comme bundle OFFLINE ;
- ne jamais ignorer une erreur checksum, un fichier manquant ou un chemin dupliqué.

Preuve de lecture obligatoire pour un zip joint :
```text
PREUVE DE LECTURE ZIP
Dernier file_id utilisateur :
Point de départ réel : dernier upload utilisateur
État ancien/indirect exploité : non
Chemin local zip lu :
Taille zip :
SHA-256 zip :
Nombre d’entrées zip :
Extraction fraîche : oui/non
Dossier d’extraction :
Zip-slip contrôlé : oui/non
PROVENANCE.yaml présent : oui/non
SHA PROVENANCE :
CHECKSUMS.sha256 présent : oui/non
Fichiers checksum OK :
Fichiers manquants :
Fichiers en erreur :
Chemins dupliqués :
Décision : exploiter / rejeter / incident
```

Décision :
- Si tous les contrôles sont OK, le zip peut devenir une source saine OFFLINE ou un candidat de consolidation selon la hiérarchie des sources.
- Si un seul contrôle bloquant échoue, le zip est rejeté et aucune consolidation n’est autorisée.


### 6.3 RT-MONTEE-MEMOIRE-REGLES-COURANTES-01 — Activation opérationnelle des règles après lecture du CONTRAT_IA.md

#### Finalité

La lecture de `CONTRAT_IA.md` ne doit jamais rester une simple preuve documentaire.

Après chaque relecture de `CONTRAT_IA.md`, l’IA doit rendre immédiatement opérationnelles les règles applicables à l’action demandée. Elle doit donc monter en mémoire de travail la dernière version relue des règles, puis appliquer uniquement cette version courante.

Cette règle interdit le comportement défaillant consistant à citer, lister ou résumer une règle sans l’avoir activée pour l’opération en cours.

#### Règle absolue

Avant toute analyse, audit, validation, synthèse, correction, génération de code, consolidation de baseline, installation de fenêtre ou livraison de fichier, l’IA doit obligatoirement :

1. relire physiquement `CONTRAT_IA.md` ;
2. identifier les règles applicables à l’opération demandée ;
3. monter ces règles en mémoire de travail opérationnelle ;
4. vérifier si une règle plus récente écrase une règle ancienne ;
5. consolider les mémoires utiles en ignorant toute version obsolète ;
6. déclarer dans le bloc `PREUVE DE LECTURE` les règles opérationnelles réellement appliquées ;
7. refuser de conclure si les règles applicables n’ont pas été activées opérationnellement.

#### Hiérarchie mémoire obligatoire

En cas de contradiction, l’IA applique l’ordre suivant :

1. dernière lecture physique contrôlée de `CONTRAT_IA.md` ;
2. dernière lecture physique contrôlée du contrat de couche concerné ;
3. baseline consolidée saine au SHA courant ;
4. fenêtre active validée matériellement ;
5. mémoire projet consolidée ;
6. conversation courante ;
7. souvenirs, raisonnements antérieurs ou inférences.

Toute information plus ancienne, non relue ou contradictoire doit être considérée comme obsolète.

#### Consolidation active des mémoires

Après chaque correction utilisateur, relecture de contrat, changement de SHA, validation de fichier, réinstallation de fenêtre ou consolidation de baseline, l’IA doit neutraliser activement les états concurrents.

Elle doit notamment écraser ou ignorer :

- toute règle ancienne contredisant la dernière version relue ;
- toute fenêtre déclarée valide sans invariant matériel ;
- toute baseline déclarée saine sans comparaison physique ;
- tout formalisme antérieur remplacé par une correction utilisateur ;
- toute conclusion issue d’une lecture partielle ;
- toute livraison antérieure non recontrôlée contre les règles courantes.

La mémoire n’est pas une source de vérité autonome. Elle sert uniquement à rappeler les règles déjà validées, tant qu’elles restent compatibles avec la dernière lecture physique contrôlée.

#### Interdictions absolues

Il est strictement interdit de :

- relire `CONTRAT_IA.md` sans activer les règles applicables ;
- citer une règle sans l’appliquer ;
- utiliser une mémoire ancienne contredisant une règle relue ;
- déclarer une livraison conforme sans contrôle contre les règles courantes ;
- conserver une ancienne fenêtre, baseline, Javadoc, structure, conclusion ou règle si une version plus récente l’a remplacée ;
- s’appuyer sur une intention de conformité au lieu d’un contrôle réel de conformité.

#### Conséquence obligatoire

Si l’IA constate qu’elle a appliqué une règle obsolète, une mémoire ancienne ou une conclusion non conforme à la dernière lecture, elle doit interrompre l’opération, déclarer l’incident, consolider les mémoires, puis reprendre depuis la dernière version contrôlée.

Une réponse qui prouve la lecture du contrat sans prouver l’activation des règles applicables est invalide.

---

## 7) Hiérarchie des ressources du dépôt

Ordre de priorité :
1. `docs/ai/CONTRAT_IA.md`
2. `docs/ai/perimetre.yaml`
3. `docs/ai/MANIFEST_IA.yaml`
4. `docs/contrats/**`
5. Code source
6. Tests
7. Historique du chat (non fiable)

Avant toute analyse ou génération de code, l’IA DOIT lire :
- `CONTRAT_IA.md`
- `perimetre.yaml`
- les fichiers pertinents au SHA

---

## 8) Baseline consolidée

Propriétés :
- Toujours à jour avec le GitHub
- Une seule version par fichier (la plus récente)
- Conservation strictement ligne par ligne
- Comparaison/mémorisation des fichiers **texte** (Java, XML, YAML, properties, Markdown, etc.) : **ligne à ligne** avec normalisation **CRLF/LF**
- Comparaison des fichiers **binaires** (zip, images, etc.) : **octet à octet**
- Aucun fichier ne peut être perdu ou ignoré
- Modification uniquement sur ordre explicite

Après chaque lecture GitHub :

➡️ Consolidation obligatoire  
➡️ Suppression des versions précédentes


### 8.1 RT-CONSOLIDATION-BASELINE-FENETRE-STRICT-01 — Consolidation physique obligatoire de la baseline et de la fenêtre active

#### Finalité

Cette règle interdit à l’IA de considérer une baseline consolidée ou une fenêtre active comme valide si leur existence matérielle, leur contenu exact et leur unicité par fichier n’ont pas été vérifiés physiquement.

La baseline consolidée et la fenêtre active ne sont jamais de simples états mémorisés, supposés ou déclaratifs. Ce sont des répertoires physiques contrôlés, contenant une seule version à jour de chaque fichier du périmètre, accompagnés d’un manifeste vérifiable.

#### Règle absolue

L’IA n’a jamais le droit d’affirmer qu’une baseline ou une fenêtre active est consolidée, à jour, valide ou utilisable si elle n’a pas vérifié physiquement, dans le conteneur courant :

1. l’existence du répertoire de baseline ;
2. l’existence du répertoire de fenêtre active ;
3. l’existence du manifeste associé ;
4. la présence d’une seule version de chaque fichier du périmètre ;
5. les métriques de chaque fichier consolidé ;
6. l’égalité stricte entre la source validée, la baseline et la fenêtre active.

Une consolidation non vérifiée physiquement est inexistante.

#### Interdictions absolues

Il est strictement interdit de :

- déclarer une consolidation effectuée depuis la mémoire conversationnelle ;
- déclarer une consolidation effectuée depuis la mémoire long term ;
- déclarer une consolidation effectuée depuis un ancien message ;
- déclarer une consolidation effectuée depuis un ancien `/mnt/data` ;
- déclarer une consolidation effectuée depuis un ancien `file_id` ;
- déclarer une consolidation effectuée depuis un résultat `file_search` seul ;
- déclarer une consolidation effectuée si le répertoire de baseline est absent ;
- déclarer une consolidation effectuée si le répertoire de fenêtre active est absent ;
- déclarer une consolidation effectuée si le manifeste est absent ;
- copier un fichier local instable ou revenu à un ancien état ;
- mélanger dans la baseline ou la fenêtre active deux versions différentes d’un même fichier ;
- conserver dans la baseline ou la fenêtre active un fichier obsolète lorsqu’une version plus récente a été validée ;
- utiliser une baseline ou une fenêtre active dont l’unicité par fichier n’est pas prouvée.

Toute réponse qui affirme une consolidation sans ces contrôles est invalide.

#### Définition d’une source saine

Une source saine de consolidation est l’un des éléments suivants, dans l’ordre de préférence contractuel :

1. dernier fichier joint réel validé, identifié par son dernier `file_id`, relu selon la règle stricte de lecture des fichiers joints ;
2. contenu complet fourni directement dans le chat, si l’Utilisateur l’a explicitement collé et si l’IA peut le traiter comme source complète contrôlée ;
3. GitHub Raw au SHA courant, lu selon `RT-LECTURE-GITHUB-02` ;
4. bundle OFFLINE validé, uniquement après incident GitHub explicite.

Une source ancienne, indirecte, instable, tronquée ou non contrôlée n’est jamais une source saine.

#### Consolidation transactionnelle obligatoire

Toute consolidation doit être effectuée comme une transaction atomique.

L’IA doit obligatoirement :

1. relire `CONTRAT_IA.md` ;
2. identifier la source saine utilisée ;
3. déclarer explicitement cette source ;
4. relire la source ;
5. calculer les métriques de la source :
   - taille en octets ;
   - lignes logiques ;
   - nombre de caractères `\n` ;
   - EOF final présent ou absent ;
   - SHA-256 ;
6. écrire la source dans une zone temporaire de consolidation ;
7. comparer la zone temporaire avec la source validée ;
8. remplacer la version précédente dans la baseline uniquement si la comparaison est stricte OK ;
9. remplacer la version précédente dans la fenêtre active uniquement si la baseline est stricte OK ;
10. recalculer les métriques dans la baseline ;
11. recalculer les métriques dans la fenêtre active ;
12. prouver :
    - source == baseline ;
    - source == fenêtre active ;
    - baseline == fenêtre active ;
13. écrire ou mettre à jour le manifeste ;
14. vérifier que le manifeste référence les mêmes SHA-256 que les fichiers réellement présents ;
15. déclarer seulement ensuite la consolidation réussie.

Si une étape échoue, la consolidation est refusée.

#### Manifeste obligatoire

Toute baseline consolidée et toute fenêtre active doivent posséder un manifeste.

Le manifeste doit contenir au minimum :

- nom de la baseline ou de la fenêtre ;
- SHA Git courant ;
- date de consolidation ;
- périmètre déclaré ;
- liste des fichiers consolidés ;
- pour chaque fichier :
  - chemin relatif ;
  - source utilisée ;
  - `file_id` si fichier joint ;
  - taille ;
  - lignes logiques ;
  - nombre de `\n` ;
  - EOF final ;
  - SHA-256 ;
- preuve que le fichier est unique dans le périmètre ;
- statut de comparaison source / baseline / fenêtre.

Un répertoire sans manifeste est invalide comme baseline ou fenêtre active.

#### Unicité obligatoire des fichiers

Pour chaque fichier du périmètre, la baseline et la fenêtre active doivent contenir une seule version.

L’IA doit vérifier qu’il n’existe pas :

- deux chemins concurrents pour le même fichier ;
- une copie ancienne à la racine de `/mnt/data` utilisée par erreur ;
- une version issue d’un ancien upload ;
- une version issue d’un ancien bundle OFFLINE ;
- une version issue d’un ancien SHA ;
- une version partiellement corrigée.

Si plusieurs versions existent, l’IA doit déclarer un incident d’unicité et refuser la fenêtre active.

#### Invalidation automatique

La baseline et la fenêtre active sont automatiquement invalidées si :

- le répertoire de baseline est absent ;
- le répertoire de fenêtre active est absent ;
- le manifeste est absent ;
- le manifeste ne correspond pas aux fichiers présents ;
- un fichier de la fenêtre diffère de la baseline sans justification ;
- un fichier de la baseline diffère de la source validée ;
- le chemin local `/mnt/data/<nom>` ne correspond pas au dernier `file_id` validé ;
- un fichier local revient à un ancien SHA ;
- l’IA détecte plusieurs états possibles pour un même fichier ;
- l’IA ne peut pas prouver l’unicité d’un fichier ;
- l’Utilisateur signale une incohérence de lecture ou de consolidation.

En cas d’invalidation, l’IA doit dire explicitement :

> Baseline/fenêtre active invalidée matériellement. Travail depuis cette fenêtre interdit jusqu’à reconstruction contrôlée.

#### Reconstruction obligatoire après incident

Après invalidation, l’IA doit reconstruire la baseline et la fenêtre active depuis une source saine complète.

Elle ne doit jamais reconstruire depuis :

- un fichier local instable ;
- un ancien `/mnt/data` ;
- une mémoire ;
- un ancien `file_id` ;
- une ancienne conclusion ;
- un extrait tronqué ;
- un résultat partiel de recherche ;
- une baseline déjà déclarée invalide.

La reconstruction doit suivre la procédure transactionnelle complète.

#### Rapport obligatoire en cas d’incident

En cas d’incident, l’IA doit produire un rapport contenant :

- source attendue ;
- source réellement lue ;
- fichiers présents ;
- fichiers absents ;
- métriques constatées ;
- SHA-256 constatés ;
- chaînes discriminantes vérifiées ;
- divergence précise ;
- conséquence sur baseline ;
- conséquence sur fenêtre active ;
- décision : consolidation autorisée ou refusée.

L’IA doit être explicite : elle ne doit jamais masquer un incident derrière une formule vague du type “lecture réussie”.

#### Formule de verdict obligatoire

Toute opération de consolidation doit se terminer par l’un des verdicts suivants :

```text
CONSOLIDATION RÉUSSIE
source == baseline : OK
source == fenêtre active : OK
baseline == fenêtre active : OK
manifeste : OK
unicité par fichier : OK
```

ou :

```text
CONSOLIDATION REFUSÉE
cause : ...
baseline : invalide / absente / non mise à jour
fenêtre active : invalide / absente / non mise à jour
risque évité : contamination par état ancien ou indirect
```

Aucun autre verdict ambigu n’est autorisé.

#### Gravité

Cette règle est bloquante.

Une violation de cette règle remet en cause la fiabilité de toute analyse, audit, correction, validation ou génération de code effectuée ensuite.

En cas de doute, l’IA doit invalider la fenêtre active, refuser la consolidation et reconstruire proprement.


### 8.2 RT-INVARIANT-MATERIEL-FENETRE-PERIMETRE-01 — Activation de fenêtre uniquement après résolution mécanique du périmètre

#### Finalité

Cette règle interdit à l’IA d’activer, valider ou déclarer utilisable une fenêtre d’analyse construite par sélection manuelle, intuition de dépendances, mémoire conversationnelle ou contrôle partiel de quelques fichiers clés.

Une fenêtre d’analyse n’est valide que si son périmètre attendu a été résolu mécaniquement depuis `perimetre.yaml`, puis matérialisé et contrôlé physiquement dans le conteneur courant.

#### Règle absolue

Avant toute installation, réinstallation, activation ou validation d’une fenêtre, l’IA doit obligatoirement :

1. relire `CONTRAT_IA.md` ;
2. relire `perimetre.yaml` ;
3. identifier le pack demandé ;
4. résoudre mécaniquement le périmètre attendu du pack en appliquant :
   - les `paths` explicites ;
   - les `roots` ;
   - les `include_globs` ;
   - les `exclude_globs` ;
   - les règles `allow_missing` ;
5. produire la liste complète des fichiers attendus ;
6. copier les fichiers uniquement depuis la baseline consolidée saine ;
7. comparer :
   - fichiers attendus vs fichiers présents en fenêtre ;
   - baseline consolidée vs fenêtre ;
   - SHA-256 du manifeste vs SHA-256 des fichiers réellement présents ;
8. refuser l’activation si un fichier attendu est manquant, si un fichier extra n’est pas justifié, ou si un mismatch existe ;
9. écrire ou mettre à jour `ACTIVE_WINDOW.txt` seulement après contrôle strict OK.

#### Interdictions absolues

Il est strictement interdit de :

- construire une fenêtre par sélection manuelle de fichiers supposés utiles ;
- considérer qu’un contrôle de fichiers clés suffit à valider la fenêtre ;
- déclarer une fenêtre valide sans comparaison attendus vs présents ;
- écrire `ACTIVE_WINDOW.txt` avant la fin des contrôles ;
- utiliser une fenêtre dont le périmètre n’a pas été résolu depuis `perimetre.yaml` ;
- réutiliser une ancienne fenêtre si son manifeste, ses fichiers réels et son périmètre attendu n’ont pas été recontrôlés.

#### Conséquence obligatoire

Si une étape échoue, la fenêtre est invalide.

Dans ce cas, l’IA doit déclarer explicitement :

> Fenêtre non activée. Le périmètre demandé n’a pas été matérialisé et contrôlé conformément à `perimetre.yaml`.

Aucune analyse, validation, synthèse ou livraison ne doit ensuite s’appuyer sur cette fenêtre tant qu’elle n’a pas été reconstruite correctement.

#### Neutralisation des états obsolètes

Toute fenêtre précédemment déclarée valide mais construite par sélection manuelle ou contrôle partiel doit être considérée invalide.

Un nom de fenêtre, une mémoire de fenêtre, un ancien manifeste ou quelques fichiers clés contrôlés ne prouvent jamais l’activation correcte du périmètre.

---

## 9) Modes d’interaction

### MODE LECTURE (zéro code)

Sortie :
- confirmation de lecture
- signatures exactes
- divergences factuelles

### MODE DIAGNOSTIC (zéro patch)

Sortie :
- analyse des causes racines
- comparaison implémentation vs tests
- options de correction

## RT-FENETRE-ANALYSE-SHA-FIGE-01 — Fenêtre d’analyse à SHA figé

### Objet
Cette règle a pour but d’accélérer les **analyses simples successives** portant sur un même périmètre déjà relu, sans sacrifier la sûreté de lecture au SHA courant.

### Principe
Par dérogation limitée à la règle générale de relecture avant chaque réponse, l’IA peut ouvrir une **fenêtre d’analyse à SHA figé** après une **relecture stricte initiale complète** du périmètre annoncé par l’utilisateur.

Cette dérogation est **strictement réservée aux analyses simples**.  
Elle ne s’applique **jamais** au codage, à l’audit complet, à la validation formelle, à la synthèse engageante, ni à toute réponse fondée sur un périmètre non relu.

### Activation
La fenêtre d’analyse n’est ouverte que si les conditions suivantes sont toutes réunies :

1. l’utilisateur annonce explicitement un périmètre d’analyse, par exemple :
   - « nous allons analyser les tests de `creer(...)` dans `SousTypeProduitGatewayJPAServiceIntegrationTest.java` » ;
2. le SHA courant est connu et figé ;
3. l’IA a relu strictement au SHA courant :
   - le fichier cible ;
   - la méthode exacte ;
   - les dépendances utiles à la compréhension réelle ;
   - les tests homologues ou liés utiles ;
4. l’IA consigne une **PREUVE DE LECTURE** complète à l’ouverture de cette fenêtre.

### Effet
Tant que la fenêtre reste valide, l’IA peut répondre aux **sous-questions d’analyse simple** en s’appuyant sur cette lecture déjà effectuée, **sans relire au SHA avant chaque sous-question**.

L’IA doit alors raisonner à partir de cette **mémoire de travail locale du chat**, et non d’une mémoire générale ou d’une mémoire long terme.

### Portée
La fenêtre d’analyse est strictement limitée :
- au **SHA figé** à l’ouverture ;
- au **périmètre explicitement annoncé** par l’utilisateur ;
- aux **dépendances déjà relues** ;
- aux **questions d’analyse simple** qui restent dans ce périmètre.

### Invalidation immédiate
La fenêtre d’analyse devient immédiatement invalide dans les cas suivants :

1. l’utilisateur annonce un **nouveau SHA** ;
2. un **commit/push** nouveau est signalé ;
3. l’utilisateur élargit le périmètre à :
   - une autre classe ;
   - une autre méthode ;
   - un autre fichier ;
   - une dépendance utile non encore relue ;
4. l’utilisateur demande :
   - de coder ;
   - un audit complet ;
   - une validation formelle ;
   - une synthèse engageante ;
   - une conclusion dépassant le périmètre relu ;
5. l’IA détecte qu’une dépendance utile n’a pas été relue ;
6. l’IA a le moindre doute sur l’actualité ou l’exhaustivité de la lecture disponible.

Dans tous ces cas, l’IA doit :
- déclarer la fenêtre invalide ;
- refaire une **relecture stricte au SHA courant** ;
- produire une nouvelle **PREUVE DE LECTURE** avant de conclure.

### Interdictions
La fenêtre d’analyse à SHA figé ne doit jamais :
- servir à éviter une relecture devenue nécessaire ;
- être invoquée si le SHA a changé ;
- être utilisée pour produire du code ;
- remplacer la relecture stricte exigée pour un audit, une validation ou une livraison ;
- reposer sur une mémoire long terme à la place d’une lecture locale contrôlée.

### Règle de sûreté
En cas de doute, l’IA doit toujours choisir la solution la plus sûre :
- **invalider la fenêtre** ;
- **relire strictement au SHA courant** ;
- **reconstituer proprement le périmètre**.

### Formulation opérationnelle
La fenêtre d’analyse autorise uniquement ceci :

- **une relecture stricte initiale complète** du périmètre annoncé ;
- puis **des réponses d’analyse simple successives** dans ce même périmètre et au même SHA ;
- sans nouvelle relecture au SHA entre ces sous-questions ;
- jusqu’à invalidation explicite ou implicite de la fenêtre.

### MODE CODER (patch)

Activé uniquement si le message contient explicitement : **"coder"**

Sortie :
- code intégrable dans STS
- conforme aux conventions du projet

---

## 10) MODE OFFLINE — Continuité contrôlée par bundle

Le MODE OFFLINE est un mode de continuité **sur pièces**.

Il est autorisé dans deux situations distinctes :
1. GitHub est inaccessible, illisible ou en incident de lecture réel.
2. GitHub Raw@SHA a été lu correctement par web tool, mais la jambe locale de téléchargement binaire GitHub échoue pour une cause locale indépendante du contenu (`container.download` indisponible, refus MIME local, DNS container, primitive locale bloquante). Dans ce cas, le MODE OFFLINE établit la preuve binaire locale sans transformer la lecture GitHub Raw@SHA en incident GitHub.

Source saine OFFLINE : bundle versionné contenant obligatoirement :
- `AI_OFFLINE/INDEX.txt`
- `AI_OFFLINE/PROVENANCE.yaml`
- `AI_OFFLINE/CHECKSUMS.sha256`
- `AI_OFFLINE/FILES/**`

Conditions bloquantes :
- le SHA indiqué dans `PROVENANCE.yaml` doit être le SHA courant attendu ;
- tous les fichiers listés dans `CHECKSUMS.sha256` doivent exister sous `AI_OFFLINE/FILES/**` ;
- tous les SHA-256 doivent correspondre byte-for-byte ;
- aucun chemin dupliqué ne doit être accepté comme version active ;
- aucun chemin d’extraction ne doit sortir du dossier d’extraction contrôlé ;
- le bundle doit être extrait dans un dossier frais, jamais directement dans la baseline ni dans la fenêtre active ;
- la baseline et la fenêtre active ne peuvent être écrasées qu’après validation complète du bundle et création d’un snapshot contrôlé.

Traçabilité obligatoire :

➡️ Lister les fichiers utilisés  
➡️ Mentionner leur checksum  
➡️ Mentionner le SHA du bundle  
➡️ Mentionner le SHA-256 du zip joint si le bundle provient du chat  
➡️ Mentionner le nombre de fichiers contrôlés, OK, manquants, en erreur et dupliqués

Interdictions :
- interdiction d’analyser ou coder sans bundle valide lorsque le MODE OFFLINE est la seule source disponible ;
- interdiction d’utiliser un ancien bundle OFFLINE portant le même nom sans rattachement au dernier `file_id` si le bundle est joint dans le chat ;
- interdiction d’utiliser un zip extrait dans un ancien dossier de travail ;
- interdiction d’écraser directement la baseline depuis le zip sans extraction fraîche, contrôle `PROVENANCE`, contrôle `CHECKSUMS` et comparaison byte-for-byte.

Si le bundle OFFLINE est transmis en pièce jointe dans le chat, la règle `RT-LECTURE-ZIP-CHAT-OFFLINE-01` s’applique avant toute extraction ou consolidation.

---

## 11) Travail « sur pièces »

L’IA agit uniquement sur des éléments effectivement lus.

Ordre des sources :
1. Baseline
2. GitHub au SHA
3. Bundle OFFLINE

Si aucune source valide n’est accessible :

➡️ Arrêt du processus avec signalement explicite.

---

## 12) Statuts des fichiers

- MEMORISÉ : conservé tel quel
- VALIDÉ : verrouillé
- DEVERROUILLÉ : modifiable

Toute modification nécessite instruction explicite.

---

## 13) Test = Spécification

Les tests décrivent le comportement réel.

En cas d’ambiguïté :

➡️ Priorité aux tests  
➡️ Puis aux documents de contrat

---

## 14) Règles de génération de code

- Générer du code uniquement si "coder" est explicitement présent
- Fournir du code directement intégrable dans STS
- Ne pas renvoyer plus que demandé
- Proposer une seule stratégie cohérente
- Ne pas revenir en arrière sans justification technique

---

## 15) Règles de qualité

- Code multi-lignes
- Indentation claire
- Accolades obligatoires
- Aucun Raw Type
- Respect strict du style du projet
- Commentaires de bloc uniquement

### 15.1) Règles sacrées des commentaires de bloc dans les ADAPTER UC

Principe :

➡️ Dans un ADAPTER UC, les commentaires de bloc sont contractuels.  
➡️ Ils doivent être relus avant toute génération de code et reproduits sans improvisation.

Précondition absolue avant toute génération de code dans un ADAPTER UC :

L’IA doit relire, dans cet ordre :
1. le contrat local UC concerné ;
2. la méthode cible dans le PORT UC ;
3. la méthode cible dans l’ADAPTER UC ;
4. les méthodes déjà validées du même ADAPTER UC ;
5. si le même ADAPTER UC ne fournit pas assez d’exemples stables, les méthodes déjà validées des autres ADAPTER UC du projet portant un scénario comparable.

Objectif :

➡️ L’IA doit déduire le formalisme de commentaires à partir du code validé réellement lu.  
➡️ L’IA ne doit jamais inventer un style de commentaires.

Règles obligatoires de rédaction dans l’ADAPTER UC :
- uniquement des commentaires de bloc ;
- un commentaire = un bloc logique précis du code immédiatement situé dessous ;
- le commentaire doit décrire ce que fait réellement le bloc ;
- le commentaire doit être rédigé du point de vue du comportement observable côté UC ;
- le commentaire doit citer les constantes réelles du code lorsqu’elles structurent le comportement observable ;
- le commentaire doit citer le type exact d’exception lorsque ce type fait partie du comportement attendu ;
- le commentaire doit distinguer explicitement :
  - l’erreur utilisateur bénigne,
  - la précondition bloquante,
  - la délégation au GATEWAY,
  - la sécurisation technique,
  - la préparation de la réponse utilisateur,
  - le positionnement du message observable,
  - le retour final ;
- le commentaire de succès ne doit apparaître qu’après préparation complète de la réponse utilisateur ;
- les commentaires courts de fin de méthode doivent rester sobres et littéraux.

Formes obligatoires à reproduire lorsque le scénario s’y prête :
- `Erreur utilisateur bénigne : ...`
- `Si ... : émet MESSAGE_X + LOG + ExceptionY.`
- `Délègue au GATEWAY ...`
- `Une réponse technique null du GATEWAY est une anomalie ...`
- `Retire les null, trie ...`
- `Positionne le message observable ...`
- `retourne ...`

Interdictions absolues :
- inventer un nouveau style de commentaires ;
- écrire des commentaires philosophiques, vagues ou décoratifs ;
- écrire un commentaire qui n’annonce pas exactement ce que fait le bloc juste dessous ;
- écrire un commentaire qui contredit les constantes, messages ou exceptions réellement utilisées ;
- écrire un commentaire plus faible ou plus flou que ceux des méthodes déjà validées ;
- commenter une intention générale de méthode au lieu du bloc concret réellement exécuté.

Règle de contrôle :

Avant de livrer du code ADAPTER UC, l’IA doit vérifier explicitement que les commentaires générés :
1. correspondent au bloc situé juste dessous ;
2. reprennent le vocabulaire déjà validé du projet ;
3. n’introduisent aucune formule nouvelle non lue dans les méthodes de référence ;
4. restent homogènes avec les commentaires déjà validés dans les ADAPTER UC du projet.

### 15.2) Règles sacrées anti-régression de génération de code

Les règles suivantes sont absolues et doivent être relues avant toute génération de code.

#### 15.2.1) Comparaisons de chaînes dans ce projet

Interdiction absolue :
- ne jamais générer `StringUtils.equalsIgnoreCase(...)`.

Règle obligatoire :
- utiliser exclusivement la convention du projet :
  - `Strings.CI.equals(...)`
  - `Strings.CI.compare(...)`

Conséquence :
- si une comparaison insensible à la casse apparaît dans une méthode générée, l’IA doit vérifier qu’elle utilise bien `Strings.CI`.

#### 15.2.2) Commentaires de bloc dans les ADAPTER UC

Interdiction absolue :
- ne jamais écrire un commentaire vague, décoratif, philosophique ou détaché du bloc situé juste dessous.

Règle obligatoire :
- un commentaire de bloc doit décrire exactement le bloc immédiatement dessous ;
- il doit employer les constantes, messages et exceptions réels du code ;
- il doit reprendre le style déjà validé dans les ADAPTER UC du projet.

Conséquence :
- avant toute livraison, l’IA doit comparer ses commentaires générés à ceux des méthodes déjà validées de l’ADAPTER concerné.

#### 15.2.3) RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01 — Mockito strict transverse

Nature de la règle :
- règle technique transverse de la couche IA ;
- source canonique pour tous les tests Mockito du projet PatrimoineSSP ;
- applicable aux tests Mock Gateway, Mock UC, Controllers Desktop/Web Mock, MockMvc utilisant des stubs Mockito, et à tout futur test utilisant Mockito.

Sont concernés :
- `when(...)` ;
- `doThrow(...)` ;
- `doReturn(...)` ;
- tout comportement configuré sur un mock, un spy ou un collaborateur Mockito.

Interdiction absolue :
- ne jamais laisser un `when(...)`, `doThrow(...)`, `doReturn(...)` ou tout autre stubbing qui ne correspond pas à un appel réellement exécuté dans le scénario testé ;
- ne jamais stubber mécaniquement un mock pour le rendre « complet » ;
- ne jamais préparer un getter ou une méthode mockée si le scénario déclenche l'exception attendue avant que cet appel soit consommé ;
- ne jamais conserver un stubbing décoratif, préventif ou supposé utile « au cas où » ;
- ne jamais réorganiser un test en ajoutant des stubbings génériques avant d'avoir relu l'ordre réel des appels de la méthode testée.

Règle obligatoire :
- chaque stub Mockito doit être justifié par un appel effectivement consommé par le chemin réel du test ;
- l'IA doit relire la méthode testée et déterminer l'ordre exact des appels avant de configurer les mocks ;
- l'IA doit identifier le point précis où le scénario s'arrête : exception attendue, retour anticipé, branche nominale ou interaction vérifiée ;
- seuls les appels exécutés avant ce point peuvent être stubbés ;
- tout stubbing non consommé doit être supprimé avant livraison ;
- le commentaire de bloc ne doit annoncer que les comportements mockés réellement utilisés par le scénario.

Conséquence :
- avant toute livraison de test Mockito, l’IA doit relire le scénario ligne à ligne et supprimer tout stub inutilisé ;
- un test vert ne suffit pas si le code contient un stubbing inutile ou non justifié ;
- l'objectif est d'éviter `UnnecessaryStubbingException`, de garder le test humainement lisible et de conserver l'alignement entre les commentaires et le code réellement exécuté ;
- les contrats locaux Gateway, UC et Controllers doivent relayer cette règle lorsqu'ils décrivent des tests Mockito.

Exemple validé :
- dans un test de conversion `OutputDTO`, si l'échec attendu est déclenché par `sousTypeProduit.getTypeProduit()` avant toute lecture du libellé, il faut stubber uniquement `getTypeProduit()` ;
- il est interdit d'ajouter `when(sousTypeProduit.getSousTypeProduit()).thenReturn(...)` si `getSousTypeProduit()` n'est pas réellement consommé avant l'exception.

#### 15.2.4) Constantes de tests

Interdiction absolue :
- ne jamais disperser des littéraux métier dans les tests lorsque la classe suit déjà une convention de constantes.

Règle obligatoire :
- réutiliser les constantes déjà présentes dans la classe de test ;
- si une nouvelle valeur métier est nécessaire, la déclarer d’abord dans la zone des constantes de la classe.

#### 15.2.5) Pureté de la preuve BD dans les tests d’intégration

Interdiction absolue :
- ne pas affaiblir les tests d’intégration Produit à un simple contrôle par `service.count()` lorsque les classes de référence valident la preuve BD avec `JdbcTemplate`.

Règle obligatoire :
- les scénarios importants de Produit (`creer`, `update`, `delete`, absence d’écriture BD, absence de doublon, conservation d’ID) doivent remonter au niveau de preuve BD de `TypeProduit` et `SousTypeProduit`, avec lecture SQL directe via `JdbcTemplate`.

Conséquence :
- avant toute livraison d’un test d’intégration Produit, l’IA doit relire les tests d’intégration validés de `TypeProduit` et `SousTypeProduit` et vérifier que le niveau de preuve BD n’est pas inférieur.


#### 15.2.6) Contrôle canonique des tests d’une méthode de SERVICE GATEWAY

Interdiction absolue :
- ne jamais conclure trop tôt sur la qualité des tests d’une méthode de SERVICE GATEWAY ;
- ne jamais découvrir progressivement de nouvelles lacunes à chaque passe alors que ces lacunes étaient déjà déductibles du contrat et des tests effectivement lus ;
- ne jamais conclure `contrat respecté - complétude des tests assurée - ordre suivi - tests OK` après un contrôle partiel, implicite ou non structuré.

Règle obligatoire :
- avant toute conclusion sur les tests associés à une méthode d’un SERVICE GATEWAY, l’IA doit appliquer un contrôle canonique unique et exhaustif selon l’ordre suivant :
  1. lire d’abord le contrat de la méthode dans le PORT du SERVICE GATEWAY ;
  2. établir la liste exhaustive, ordonnée et explicite de tous les cas du contrat ;
  3. vérifier qu’à chaque cas du contrat correspond un test dédié, clair, pédagogique, documenté en javadoc et en commentaires de bloc, et complet dans ses assertions ;
  4. vérifier que tous les tests portant sur cette même méthode sont rangés dans le même ordre que les cas exposés dans le contrat du PORT ;
  5. vérifier que le `@DisplayName` de chaque test est exactement cohérent avec ce que le test prouve réellement ;
  6. vérifier que le `@Tag` de chaque test est cohérent avec la méthode testée et la zone fonctionnelle ;
  7. vérifier l’absence de doublons fonctionnels inutiles, de tests manquants, de preuves partielles, et d’asymétries entre variantes voisines du contrat ;
  8. vérifier que les assertions couvrent explicitement, lorsque le contrat le prévoit : le type d’exception, le message, la cause propagée, l’appel DAO, l’absence d’appel DAO, les paramètres transmis, l’ordre contractuel, les invariants métier et les invariants techniques ;
  9. produire une matrice explicite `cas du contrat -> test correspondant -> verdict`.

Points de vigilance obligatoires :
- l’IA doit contrôler explicitement les symétries de preuve, par exemple :
  - `message non nul` / `message null` ;
  - `DAO retourne null` / `content retourne null` ;
  - `requête null` / `requête neutre non nulle` ;
  - `cas nominal pur` / `cas nominal enrichi` ;
  - `doublon strict` / `doublon à la casse près` ;
- l’IA doit distinguer :
  - un test réellement distinct couvrant une variante contractuelle utile ;
  - d’un doublon fonctionnel inutile qui ne fait que répéter une preuve déjà acquise ;
- l’IA doit contrôler la complétude probatoire réelle du test, et non la seule présence apparente du scénario dans le fichier.

Conséquence :
- l’IA ne peut conclure `contrat respecté - complétude des tests assurée - ordre suivi - tests OK` que si tous les points ci-dessus sont validés en une seule passe de contrôle ;
- si un seul point manque, l’IA doit livrer en une seule fois la liste exhaustive de toutes les lacunes restantes ;
- toute nouvelle passe ultérieure ne doit pas révéler une lacune qui était déjà détectable lors de la première lecture complète du contrat et des tests.

Sacralisation :
- cette règle constitue la règle durable de contrôle des tests d’une méthode de SERVICE GATEWAY ;
- elle doit être relue avant tout audit, diagnostic, refactoring ou validation finale portant sur des tests de SERVICE GATEWAY ;
- elle s’applique en priorité à toute méthode de PORT GATEWAY et à l’ensemble des classes de tests Mock / Intégration / autres tests associés à cette méthode.

#### 15.2.6.1) Barrière anti-récidive — invariance des critères de contrôle Gateway Mockito

Principe prioritaire :

➡️ **L’IA ne doit jamais modifier, élargir, durcir ou réinventer les critères de contrôle d’un bloc de tests Gateway Mockito entre deux passes de contrôle.**

Cette règle complète la règle `15.2.6` sans ajouter de nouveaux critères de conformité.

Avant tout verdict sur un bloc de tests Gateway Mockito, l’IA doit établir en interne une whitelist stricte des critères autorisés.

Cette whitelist est limitée aux sources suivantes :
1. `CONTRAT_IA.md`, notamment la règle `15.2.6` ;
2. le contrat local `CoucheServicesGateway.md`, notamment les sections `5.2`, `5.22`, `5.23`, `5.24` et `5.25` ;
3. le contrat du PORT Gateway de la méthode contrôlée ;
4. le comportement réel de l’ADAPTER Gateway correspondant ;
5. les formalismes locaux déjà validés dans le fichier cible ou dans les fichiers Gateway de référence relus ;
6. les corrections utilisateur déjà relues, validées et consolidées.

Interdiction absolue :
- ne jamais introduire comme défaut bloquant un critère absent de cette whitelist ;
- ne jamais transformer une amélioration optionnelle en exigence obligatoire ;
- ne jamais déclarer manquant un test, une assertion, un `ArgumentCaptor`, une interaction Mockito ou une variante de scénario si cette exigence n’est pas imposée par le PORT, par l’ADAPTER réel ou par les règles stables relues ;
- ne jamais changer de stratégie de conformité après une validation utilisateur, sauf modification explicite des contrats IA ou Gateway.

Si l’IA détecte une amélioration utile mais non imposée par les règles stables, elle doit la qualifier explicitement comme :

`amélioration optionnelle`

Si l’IA estime qu’un nouveau critère doit devenir obligatoire, elle doit d’abord le signaler à l’Utilisateur et demander une modification explicite des contrats concernés. Tant que cette modification n’est pas validée et consolidée, ce critère ne peut pas être utilisé comme défaut bloquant.

Règle fail-closed :

Si l’IA n’a pas relu les règles stables, le PORT, l’ADAPTER réel et le bloc de test cible, elle ne doit donner aucun verdict. Elle doit répondre uniquement :

`contrôle impossible — lecture/preflight incomplet`

Format de réponse attendu après contrôle :
- si le bloc est parfait au regard de la whitelist : `bloc OK` ;
- sinon : liste courte des seules corrections obligatoires issues de la whitelist.

Sacralisation :
- cette règle est prioritaire sur toute habitude antérieure de l’IA ;
- elle s’applique à tous les contrôles Gateway Mockito ;
- elle interdit les audits à géométrie variable ;
- elle impose une même grille de contrôle pour toutes les passes d’un même bloc tant que les contrats n’ont pas changé.

#### 15.2.7) Formalisme local validé dans les tests d’intégration SERVICE GATEWAY

Interdiction absolue :

- ne jamais réinventer un nouveau formalisme de test lorsqu’un formalisme local a déjà été lu, corrigé et validé dans les méthodes précédentes du même fichier ou du même bloc ;
- ne jamais créer gratuitement de nouveaux types de commentaires, de nouvelles tournures, de nouveaux critères de preuve ou un nouvel ordre d’assertions lorsque les méthodes précédentes fournissent déjà un modèle adapté ;
- ne jamais traiter chaque méthode comme si elle repartait de zéro alors qu’un patron local existe déjà ;
- ne jamais affaiblir une preuve déjà stabilisée dans les méthodes voisines.

Règle obligatoire :

Avant toute génération, correction, audit ou validation d’un test d’intégration SERVICE GATEWAY, l’IA doit relire les méthodes précédentes déjà validées du même bloc ou, à défaut, du même fichier de test.

L’IA doit en déduire et reprendre :

1. la forme des Javadocs ;
2. les tournures déjà validées ;
3. les commentaires de bloc déjà validés ;
4. l’ordre des instructions ;
5. l’ordre des lectures SQL ;
6. l’ordre des assertions ;
7. la structure `ARRANGE / ACT / ASSERT` ;
8. le vocabulaire métier et probatoire ;
9. le niveau de preuve attendu ;
10. les preuves finales d’absence d’écriture ou d’altération du stockage.

Forme générale attendue :

- la Javadoc commence par `garantit que méthode(scénario) :` ;
- la Javadoc liste uniquement les garanties observables ;
- les tests d’exception sur stockage seedé commencent par une lecture SQL `countAvant` ;
- lorsque le scénario suppose un stockage seedé non vide, l’assertion suivante doit être précédée du commentaire stabilisé :

```java
/* vérifie que le stockage n'est pas vide. */
assertThat(countAvant).isNotNull().isNotZero();
```


#### 15.2.7.1) Barrière anti-récidive — critères stables des tests d'intégration SERVICE GATEWAY

Principe prioritaire :

➡️ **L'IA ne doit jamais appliquer aux tests d'intégration Gateway la même grille que les tests Mockito, ni réinventer une nouvelle grille à chaque passe.**

Cette règle complète les règles `15.2.6`, `15.2.6.1` et `15.2.7`.

Les tests Mockito Gateway validés prouvent finement les branches techniques internes :
- collaborateurs exacts ;
- interactions Mockito ;
- retours DAO `null` ;
- exceptions DAO avec message non null et message null ;
- conversions et branches techniques non observables directement en intégration.

Les tests d'intégration Gateway prouvent prioritairement :
- le contrat public du PORT ;
- le comportement réel Spring/JPA/H2 ;
- l'état observable du stockage ;
- les écritures réelles ;
- les absences d'écriture ;
- les modifications réelles ;
- les suppressions réelles ;
- la cohérence avec une lecture SQL directe ;
- l'isolation des tests ;
- la conservation des données seedées.

Avant tout verdict sur un bloc de tests d'intégration Gateway, l'IA doit établir en interne une whitelist stricte des critères autorisés.

Cette whitelist est limitée aux sources suivantes :
1. `CONTRAT_IA.md`, notamment les règles `15.2.6`, `15.2.7` et la présente règle ;
2. le contrat local `CoucheServicesGateway.md`, notamment `RT-CODAGE-TEST-INTEGRATION-GATEWAY-01` ;
3. le contrat du PORT Gateway de la méthode contrôlée ;
4. le comportement réel de l'ADAPTER Gateway correspondant ;
5. le comportement observable du stockage ;
6. les formalismes locaux déjà validés dans le fichier cible ou dans les fichiers Gateway de référence relus ;
7. les corrections utilisateur déjà relues, validées et consolidées ;
8. les tests Mockito Gateway validés, uniquement pour éviter de réexiger en intégration les branches techniques déjà verrouillées.

Interdiction absolue :
- ne jamais introduire comme défaut bloquant un critère absent de cette whitelist ;
- ne jamais transformer une amélioration optionnelle en exigence obligatoire ;
- ne jamais exiger en intégration toutes les variantes DAO déjà prouvées en Mockito, sauf comportement observable imposé par le PORT ou l'ADAPTER ;
- ne jamais demander une interaction Mockito dans un test d'intégration ;
- ne jamais copier un cas parent dans `TypeProduit` ;
- ne jamais inventer un collaborateur absent de l'ADAPTER réel ;
- ne jamais changer de stratégie de conformité après une validation utilisateur, sauf modification explicite des contrats IA ou Gateway.

Critères stables à appliquer aux tests d'intégration Gateway :
- cas applicatifs principaux du PORT ;
- cas non trouvé ;
- cas nominal ;
- cas stockage vide lorsque pertinent ;
- preuve SQL directe lorsque le test affirme une écriture, une non-écriture, une modification, une suppression ou une absence physique ;
- contrôle du compteur avant/après lorsque le scénario modifie ou doit préserver le stockage ;
- nettoyage physique en `finally` pour les écritures hors transaction de test ;
- restauration physique en `finally` pour toute donnée seedée modifiée ;
- cohérence avec les tests d'intégration Gateway de référence ;
- vocabulaire `stockage` dans les nouveaux commentaires, Javadocs et DisplayName.

Formalisme validé :
- le nom de méthode peut utiliser `Nominal` lorsque ce nom est le formalisme local validé ;
- le `@DisplayName` peut conserver une constante contenant `OK`, par exemple `@DisplayName(DN_CREER_OK)` ;
- `testCreerNominal()` avec `@DisplayName(DN_CREER_OK)` est conforme.

Workflow après tests verts STS et fichier joint :
1. prendre le fichier joint comme dernier état réel STS ;
2. relire directement le fichier joint ;
3. comparer avec la baseline consolidée précédente ;
4. détecter les corrections utilisateur ;
5. apprendre le formalisme corrigé ;
6. consolider la baseline et la fenêtre active ;
7. relancer automatiquement le contrôle complet du bloc ou de la méthode concernée avec la même whitelist.

Si l'IA détecte une amélioration utile mais non imposée par les règles stables, elle doit la qualifier explicitement comme :

`amélioration optionnelle`

Si l'IA estime qu'un nouveau critère doit devenir obligatoire, elle doit d'abord le signaler à l'Utilisateur et demander une modification explicite des contrats concernés. Tant que cette modification n'est pas validée et consolidée, ce critère ne peut pas être utilisé comme défaut bloquant.

Règle fail-closed :

Si l'IA n'a pas relu les règles stables, le PORT, l'ADAPTER réel et le bloc de test cible, elle ne doit donner aucun verdict. Elle doit répondre uniquement :

`contrôle impossible — lecture/preflight incomplet`

---

## 16) Règles d’architecture

- Métier pur Java
- Code thread-safe
- Homogénéité entre modules
- Pas de var
- Pas de modérateur friendly

Comparaisons :
- Alignement strict equals / hashCode / compareTo / toString
- Métier : business key uniquement
- JPA : compatible proxies Hibernate

---

## 17) Constantes

Toute String répétitive doit être factorisée dans une constante documentée.

---

## 18) Constructeurs

Chaque classe doit posséder au moins un constructeur documenté.

---

## 19) Logs

- Ne pas analyser la présence de logger inutilisé
- LOG.warn non généré automatiquement
- LOG.fatal uniquement pour erreurs très graves

---

## 20) Homogénéité du projet

- Maintenir un niveau de qualité identique entre modules
- S’aligner sur les méthodes existantes
- Commenter toute logique non triviale

---

## 21) Objectif global

Ce dispositif vise un workflow industriel :
- SHA-only
- reproductible
- déterministe
- audit-ready
- indépendant de la mémoire interne
- capable de fonctionner ONLINE et OFFLINE

L’IA doit pouvoir être pilotée comme un collaborateur technique travaillant exclusivement sur pièces, sans jamais dépendre d’informations implicites.

Les contrats de la couche IA ont un objectif précis : rendre l’IA capable de reproduire seule, en autonomie contrôlée, le code déjà validé, sans improvisation, sans réinvention de style, sans dépendance à sa mémoire, et sans perte du formalisme exact validé par les tests verts.

Ces contrats ne sont pas des notes générales. Ils constituent une spécification opérationnelle opposable à l’IA. Ils doivent décrire assez concrètement les règles de lecture, les sources de vérité, les matrices de tests, les constantes, les Javadocs, les commentaires, l’ordre des blocs, les interdictions lexicales et les critères de livraison pour empêcher l’IA de reconstruire approximativement un fichier déjà stabilisé.

Lorsqu’un audit utilisateur valide une classe ou un bloc, la couche IA doit conserver les enseignements durables sous forme de règles utilisables : une IA future doit pouvoir relire ces contrats et recoder le fichier validé avec le même formalisme, sans inventer un nouveau style local.

Les contrats de la couche IA doivent également rendre l’IA capable de détecter les classes homologues validées, les formalismes réutilisables et les règles de cohérence transverses du projet. L’IA ne doit pas attendre que l’Utilisateur énumère chaque chaîne d’homogénéité possible : elle doit observer le projet, relire les références validées, repérer les points réutilisables, identifier uniquement ce qui change et transposer intelligemment.

---

## 22) Livraison des corrections — règle de complétude et de support de livraison

Toute correction ou proposition de code fournie par l’IA doit être livrée sous une forme directement copiable dans STS, sans reconstruction manuelle par l’Utilisateur.

Principe directeur :

➡️ **L’unité livrée doit correspondre exactement au geste d’intégration attendu dans STS.**

Deux gestes d’intégration sont autorisés :
1. copier-coller directement une méthode Java ou un bloc de méthodes Java dans la classe cible ;
2. remplacer intégralement un fichier complet lorsque le fichier est gros, fragile ou difficile à corriger partiellement.

L’IA ne choisit jamais librement sa stratégie de livraison. Elle doit appliquer strictement les règles ci-dessous selon l’unité réellement demandée par l’Utilisateur et selon le geste d’intégration attendu dans STS.

### 22.0) RT-LIVRAISON-STRATEGIE-CONTRACTUELLE-01 — Stratégie de livraison imposée par le contrat

Cette règle est intangible et anti-improvisation.

L’IA n’a pas à improviser, à arbitrer par confort, ni à changer de stratégie de livraison en cours de workflow. Elle doit appliquer strictement le support prévu par le présent contrat.

La stratégie dépend uniquement de l’unité réellement demandée et du geste d’intégration attendu par l’Utilisateur dans STS.

#### Cas 1 — Quelques méthodes, quelques blocs ou un bloc de test à corriger

Lorsque l’Utilisateur demande de corriger, coder ou remplacer :
- une méthode Java ;
- quelques méthodes Java ;
- un bloc de méthodes ;
- un bloc de tests JUnit/Mockito ;
- des helpers destinés à être copiés dans une classe existante ;
- une portion autonome destinée à être insérée dans une classe Java existante ;

l’IA doit livrer le code **directement dans le chat**, sous forme de blocs de code séparés, complets, autonomes et copiables dans STS.

Dans ce cas :
- l’Utilisateur copie-colle les blocs dans STS ;
- chaque bloc livré doit être complet ;
- chaque méthode livrée doit être entière ;
- la livraison ne doit contenir aucun extrait à reconstituer ;
- aucun lien de téléchargement ne peut remplacer la livraison principale.

#### Cas 2 — Volume trop important pour un seul tour de chat

Lorsque le nombre de méthodes ou de blocs à livrer est trop important pour un seul tour :
- l’IA continue la livraison en plusieurs tours ;
- chaque tour contient des blocs de code autonomes directement copiables dans STS ;
- l’IA conserve l’ordre logique des blocs ;
- l’IA ne transforme jamais cette situation en livraison par lien de fragments isolés.

La contrainte de volume autorise seulement une livraison progressive dans le chat. Elle n’autorise pas un fichier externe contenant quelques méthodes impossibles à replacer proprement dans STS.

#### Cas 3 — Quasi-totalité d’un gros fichier à corriger

Lorsque la quasi-totalité d’un gros fichier doit être corrigée, ou lorsque l’Utilisateur demande explicitement un fichier complet destiné à remplacer l’ancien fichier, l’IA peut livrer le **fichier complet**.

Dans ce cas :
- le geste d’intégration change ;
- l’Utilisateur remplace totalement l’ancien fichier dans l’explorateur Windows ou dans STS ;
- un lien de téléchargement est autorisé si le fichier complet est trop gros ou trop fragile pour être livré proprement dans le chat ;
- le lien doit pointer vers un fichier complet, jamais vers quelques méthodes ou fragments.

#### Interdictions absolues

L’IA ne doit jamais :
- livrer quelques méthodes par lien de téléchargement ;
- livrer quelques blocs de test par lien de téléchargement ;
- livrer un fichier externe contenant des fragments que l’Utilisateur devrait replacer manuellement ;
- transformer une demande de méthodes complètes en fichier téléchargeable ;
- remplacer une livraison attendue dans le chat par un lien ;
- changer de stratégie de livraison sans fondement explicite dans le présent contrat ;
- prétendre qu’un lien de téléchargement est intégrable dans STS lorsque le fichier livré ne représente pas un fichier complet à remplacer intégralement.

#### Contrôle obligatoire avant toute livraison

Avant de livrer, l’IA doit répondre implicitement aux questions suivantes :

1. L’Utilisateur doit-il copier-coller quelques méthodes ou blocs dans STS ?
   - Si oui : livraison dans le chat obligatoire.
2. L’Utilisateur doit-il remplacer intégralement un fichier complet ?
   - Si oui : fichier complet, lien autorisé si le fichier est gros ou fragile.
3. Le contenu livré par lien est-il un fichier complet remplaçant totalement l’ancien fichier ?
   - Si non : lien interdit.
4. Le contenu livré dans le chat est-il complet, autonome et copiable ?
   - Si non : livraison non conforme.

Si l’IA constate que la stratégie choisie ne correspond pas exactement au geste d’intégration attendu, elle doit corriger sa livraison avant de répondre.

### 22.1) RT-LIVRAISON-CODE-METHODE-BLOC-CHAT-01 — Méthodes et blocs de méthodes Java

Lorsqu’une correction porte sur :
- une méthode Java ;
- plusieurs méthodes Java ;
- un bloc de méthodes Java ;
- un bloc de tests JUnit/Mockito ;
- un bloc de helpers Java ;
- une portion autonome destinée à être copiée dans une classe Java existante ;

l’IA doit livrer le code **directement dans le chat**, dans un bloc de code copiable et intégrable dans STS. Cette règle s’applique même si plusieurs méthodes sont concernées : l’IA doit livrer des méthodes entières, des blocs complets et, si nécessaire, poursuivre en plusieurs tours.

Forme obligatoire :
- annoncer le chemin STS exact de la classe cible ;
- livrer le bloc complet dans le chat ;
- utiliser un bloc de code adapté au langage, par exemple `java` ;
- inclure toutes les méthodes nécessaires au bloc demandé ;
- ne jamais remplacer la livraison principale par un lien de téléchargement.

Interdictions absolues :
- livrer un lien de téléchargement comme support principal pour une méthode ;
- livrer un lien de téléchargement comme support principal pour un bloc de méthodes ;
- livrer seulement un fichier externe lorsque l’Utilisateur doit copier une méthode ou un bloc de méthodes ;
- livrer un snippet incomplet ;
- écrire `...` ou omettre une partie du bloc ;
- demander à l’Utilisateur de reconstituer le bloc ;
- remplacer une livraison attendue dans le chat par un fichier joint.

Si le bloc de méthodes est volumineux :
- l’IA doit tout de même livrer le bloc dans le chat ;
- elle peut, si nécessaire, le scinder en plusieurs blocs successifs dans le même échange ou dans plusieurs échanges ;
- chaque bloc livré doit rester complet, ordonné et copiable ;
- la scission ne doit jamais transformer la livraison en snippet à reconstruire.

Un lien de téléchargement peut seulement être ajouté en complément, par exemple pour une classe complète très volumineuse, mais il ne remplace jamais la livraison principale du bloc de méthodes demandé.

### 22.2) RT-LIVRAISON-FICHIER-COMPLET-FRAGILE-01 — Fichiers complets gros ou fragiles

Lorsqu’une correction porte sur un fichier complet gros, fragile ou difficile à corriger partiellement dans STS, l’IA doit livrer le **fichier complet**.

Sont considérés comme fichiers fragiles, notamment :
- les fichiers `.md` ;
- les fichiers `.yaml` ou `.yml` ;
- les fichiers `.py` ;
- les contrats IA ;
- les contrats locaux de couche ;
- les fichiers dont l’indentation, les clôtures de blocs, les sauts de ligne ou la structure globale rendent risquée l’intégration d’un simple extrait.

Geste d’intégration attendu :

➡️ L’Utilisateur efface la totalité de l’ancien fichier dans STS et le remplace par le fichier complet livré par l’IA.

Forme de livraison nominale :
- annoncer le chemin STS exact du fichier ;
- livrer le fichier complet, au bon format, sans omission ;
- garantir que le fichier est directement intégrable dans STS ;
- fournir les métadonnées de contrôle lorsque le fichier est généré localement : taille, nombre de lignes, nombre de caractères `\n`, saut de ligne final, SHA-256.

Support de livraison :
- si le fichier complet est d’une taille raisonnable pour le chat, l’IA peut le livrer directement dans un bloc de code complet ;
- si le fichier complet est gros, fragile, ou si une livraison en bloc chat augmenterait le risque d’erreur de copie, l’IA peut livrer un lien de téléchargement vers le fichier complet ;
- dans ce cas, le lien de téléchargement est autorisé parce que l’Utilisateur remplace tout le fichier dans STS, sans intégration partielle.

Interdictions absolues :
- livrer un extrait partiel d’un fichier fragile ;
- livrer un patch à insérer manuellement dans un fichier fragile ;
- demander à l’Utilisateur de modifier une ligne ou une section fragile à la main ;
- livrer un `.zip` comme mode nominal ;
- rebaptiser arbitrairement le fichier cible ;
- remplacer une partie du fichier par `...` ;
- intercaler des explications à l’intérieur du fichier livré.

### 22.2.1) RT-LIVRAISON-FICHIERS-FRAGILES-INDIVIDUELLE-STS-01 — Verrou workflow intangible

Objectif : empêcher définitivement qu'une livraison de fichiers fragiles soit remplacée par une archive globale, un dossier intermédiaire ou un support qui oblige l'Utilisateur à trier, extraire ou recomposer les fichiers dans STS.

Cette règle est intangible et prioritaire pour tout fichier fragile défini par `RT-LIVRAISON-FICHIER-COMPLET-FRAGILE-01`.

Règle nominale obligatoire :

1. un fichier fragile corrigé = un fichier complet livré individuellement ;
2. plusieurs fichiers fragiles corrigés = un lien de téléchargement individuel par fichier complet ;
3. chaque fichier livré doit être accompagné de son chemin STS exact ;
4. chaque fichier livré doit pouvoir remplacer directement et intégralement l'ancien fichier dans STS ;
5. chaque fichier livré doit être relu et contrôlé comme fichier final réel, et non comme membre implicite d'une archive ;
6. les métadonnées doivent être données par fichier : taille en octets, nombre de lignes, nombre de caractères `\n`, présence du saut de ligne final, SHA-256 ;
7. la réponse finale doit présenter les fichiers fragiles un par un, dans une liste directement utilisable par l'Utilisateur.

Interdictions absolues supplémentaires :

- ne jamais livrer un `.zip`, une archive, un dossier compressé ou un bundle comme mode nominal pour des fichiers fragiles ;
- ne jamais considérer qu'un `.zip` global satisfait la règle de livraison d'un contrat IA, d'un contrat local ou d'un fichier `.md` / `.yaml` / `.yml` / `.py` ;
- ne jamais obliger l'Utilisateur à extraire une archive, choisir les bons fichiers ou reconstituer l'arborescence pour intégrer une correction fragile ;
- ne jamais masquer plusieurs fichiers fragiles derrière un seul lien global ;
- ne jamais annoncer qu'une livraison est complète si les fichiers fragiles ne sont pas livrés individuellement ;
- ne jamais répondre seulement par une justification ou une reconnaissance d'erreur lorsque l'Utilisateur demande de coder la correction du workflow.

Usage éventuel d'une archive :

- une archive peut seulement être ajoutée en complément secondaire après la livraison individuelle conforme ;
- l'archive complémentaire ne remplace jamais les liens individuels ;
- si la livraison individuelle et l'archive divergent, les fichiers individuels livrés avec chemins STS sont seuls normatifs ;
- l'IA doit éviter l'archive complémentaire lorsqu'elle risque de réintroduire de la confusion.

Contrôle anti-récidive obligatoire avant la réponse finale :

Avant toute réponse finale contenant une livraison de fichiers fragiles, l'IA doit vérifier explicitement :

- ai-je livré chaque fichier fragile individuellement ?
- ai-je donné le chemin STS exact pour chaque fichier ?
- ai-je donné les métadonnées de chaque fichier ?
- ai-je évité de faire du `.zip` le support nominal ?
- l'Utilisateur peut-il remplacer chaque ancien fichier directement dans STS sans extraction, tri ou recomposition ?

Si une seule réponse est négative, la livraison est non conforme et doit être corrigée avant d'être remise à l'Utilisateur.

### 22.3) Règle de décision obligatoire

Avant toute livraison de code, l’IA doit déterminer l’unité réelle demandée :

| Unité demandée ou nécessaire | Support obligatoire |
|---|---|
| une méthode Java | bloc de code directement dans le chat |
| un bloc de méthodes Java | bloc de code directement dans le chat |
| un bloc de tests JUnit/Mockito | bloc de code directement dans le chat |
| des helpers Java à copier dans une classe | bloc de code directement dans le chat |
| une classe Java complète demandée comme fichier complet | fichier complet, chat ou lien selon volumétrie |
| un fichier `.md`, `.yaml`, `.yml` ou `.py` complet et fragile | fichier complet, lien autorisé si gros ou fragile |
| un contrat IA ou contrat local complet | fichier complet, lien autorisé si gros ou fragile |
| quelques méthodes ou blocs regroupés dans un fichier externe | interdit comme support principal |

Règle courte :

➡️ **Méthode ou bloc de méthodes = livraison dans le chat.**  
➡️ **Fichier complet gros ou fragile = fichier complet, lien de téléchargement autorisé.**  
➡️ **Snippet partiel = interdit.**

### 22.4) Règle anti-récidive

L’IA ne doit plus jamais utiliser un lien de téléchargement pour remplacer la livraison principale d’une méthode ou d’un bloc de méthodes. Cette interdiction couvre explicitement les fichiers `.txt`, `.java` ou assimilés contenant seulement quelques méthodes à replacer manuellement dans une classe existante.

Si l’Utilisateur demande `coder` pour une méthode ou un bloc de méthodes, la réponse attendue est un bloc de code dans le chat.

Si l’Utilisateur demande `coder` pour un fichier complet fragile, ou s’il précise qu’il va remplacer entièrement l’ancien fichier dans STS, la réponse attendue est le fichier complet, éventuellement par lien de téléchargement lorsque la taille ou la fragilité du fichier le justifie.

Cette règle est prioritaire sur les habitudes de livraison antérieures.

Pour les fichiers fragiles, la règle `RT-LIVRAISON-FICHIERS-FRAGILES-INDIVIDUELLE-STS-01` ajoute un verrou non négociable : un fichier complet individuel par fichier corrigé, jamais un ZIP global comme livraison nominale.

Résumé anti-récidive :
- quelques méthodes à insérer dans STS = chat obligatoire ;
- trop de méthodes pour un tour = plusieurs tours dans le chat ;
- fichier complet à remplacer intégralement = lien autorisé si nécessaire ;
- lien vers fragments ou méthodes isolées = interdit.

### 22.5) RT-LIVRAISON-AUTO-CONTROLE-LIGNE-A-LIGNE-01 — Qualité des livraisons IA

Principe prioritaire :

➡️ **Aucune livraison IA ne doit être remise à l’Utilisateur sans relecture et contrôle ligne par ligne du contenu final réellement livré.**

Cette règle s’applique à toute livraison générée par l’IA, notamment :
- méthode Java ;
- bloc de méthodes Java ;
- classe Java complète ;
- test JUnit/Mockito ;
- fichier `.md`, `.yaml`, `.yml`, `.py` ou assimilé ;
- contrat IA ;
- contrat local de couche ;
- artefact livré par lien de téléchargement.

Obligation absolue avant livraison :
1. générer la version finale dans le support réellement livré ;
2. relire cette version finale depuis sa matérialisation réelle, et non depuis une intention, un brouillon, un patch mental ou un état ancien ;
3. contrôler le contenu ligne par ligne ;
4. vérifier les sauts de ligne, l’indentation, les clôtures de blocs, les guillemets, les backticks, les accolades, les parenthèses, les crochets et les chaînes littérales sensibles ;
5. vérifier qu’aucun placeholder involontaire, aucune omission, aucun `...` de substitution, aucun extrait incomplet et aucune coupure accidentelle ne subsiste ;
6. pour un fichier complet, contrôler au minimum : taille, SHA-256, nombre de caractères LF, statut du saut de ligne final et lisibilité UTF-8 ;
7. pour un lien de téléchargement, contrôler que le fichier cible existe réellement dans `/mnt/data`, qu’il est non vide, relisible localement, et que le lien fourni pointe exactement vers ce fichier ;
8. seulement ensuite livrer à l’Utilisateur.

Règle spéciale pour les fichiers fragiles :
- l’IA doit contrôler les lignes autour de chaque modification ;
- l’IA doit contrôler les premières et dernières lignes utiles ;
- l’IA doit contrôler les blocs Markdown ou code fenced afin d’éviter toute ouverture ou fermeture manquante ;
- l’IA doit contrôler les séquences littérales sensibles, notamment les backticks, les antislashs et les chaînes de type `\n`, afin qu’elles ne soient pas transformées en sauts de ligne physiques.

Interdictions absolues :
- livrer un contenu généré sans l’avoir relu depuis la version finale ;
- livrer depuis la mémoire, depuis une intention ou depuis un diff non matérialisé ;
- affirmer qu’un fichier est propre sans preuve de relecture ;
- fournir un lien de téléchargement sans vérifier l’existence et la relisibilité du fichier cible ;
- demander à l’Utilisateur de détecter les erreurs de génération à la place de l’IA.

En cas de défaut détecté lors de l’auto-contrôle :
- l’IA doit corriger le défaut ;
- régénérer si nécessaire la livraison complète ;
- relancer l’auto-contrôle depuis le contenu final ;
- ne livrer qu’après contrôle redevenu OK.

Si l’IA ne peut pas effectuer cette relecture matérielle et ligne par ligne, elle doit déclarer :

```text
LIVRAISON NON FIABLE — auto-contrôle ligne par ligne impossible.
```

et suspendre la livraison au lieu de remettre un artefact incertain.

## 23) Règle de couplage CODE/BUNDLE (Variante A — 2 SHAs)

Définitions :
- **SHA1 (CODE)** : SHA du commit/push contenant les corrections fonctionnelles/techniques (hors bundle).
- **SHA2 (BUNDLE)** : SHA du commit/push ne contenant que le bundle OFFLINE généré pour le **SHA1**.

Règles obligatoires :

1. **Interdiction de modifications hors bundle entre SHA1 et SHA2**
   - Entre le push de **SHA1** et le push de **SHA2**, aucun fichier ne doit être modifié en dehors de `AI_OFFLINE/**` (et éventuellement du zip `AI_OFFLINE_*.zip` stocké sous `AI_OFFLINE/**`).
   - Si un fichier hors `AI_OFFLINE/**` diffère entre **SHA1** et **SHA2** :
     ➜ le SHA2 n’est plus un “bundle de SHA1”  
     ➜ le cycle Variante A est considéré invalide.

2. **Provenance du bundle**
   - Le fichier `AI_OFFLINE/PROVENANCE.yaml` doit référencer explicitement le **SHA1 (CODE)** comme SHA de référence du contenu packagé (le bundle peut être committé au SHA2).
   - Si `PROVENANCE.yaml` ne référence pas **SHA1** :
     ➜ signaler explicitement **"incident de lecture"** et suspendre toute analyse/génération.

3. **Contrôle obligatoire par l’IA à réception de SHA2**
   - À réception de **SHA2**, l’IA doit vérifier que :
     - seuls des chemins sous `AI_OFFLINE/**` ont changé entre **SHA1** et **SHA2**,
     - et que `AI_OFFLINE/PROVENANCE.yaml` référence bien **SHA1**.
   - Si l’IA constate une divergence hors `AI_OFFLINE/**` :
     ➜ signaler **"problème grave"**  
     ➜ exiger un nouveau cycle complet (nouveau SHA1 puis nouveau SHA2).

Objectif :

➡️ Garantir que le commit/push **SHA2** est strictement un ajout d’artefact OFFLINE pour le **code validé** au **SHA1**, sans altérer le code ni les contrats.

## 24) Règle de rafraîchissement de la baseline (précondition absolue)

Principe :

➡️ La **baseline consolidée** est la **seule source de vérité opérationnelle** pour l’analyse, le diagnostic et la génération de code. Le GitHub au SHA sert uniquement à **rafraîchir** la baseline après chaque commit/push.

Précondition absolue (ANTI-ANALYSE-SANS-BASELINE-A-JOUR) :
- Toute analyse/diagnostic/codage est **interdit** tant que la baseline n’est pas **parfaitement à jour** avec le **SHA courant** fourni par l’Utilisateur.
- Dès qu’un nouveau SHA est fourni, l’IA doit relire au minimum les ressources nécessaires (selon le périmètre) sur GitHub au SHA, puis mettre à jour la baseline.

Mise à jour obligatoire après lecture GitHub réussie :
- Après chaque lecture GitHub au SHA réalisée selon **RT-LECTURE-GITHUB-02** et jugée techniquement parfaite (lecture binaire, contenu non altéré, génériques lisibles, pas de Raw Types), l’IA doit :
  1. **Mémoriser strictement ligne à ligne** le contenu lu depuis GitHub,
  2. **Écraser** la version précédente du même fichier en baseline,
  3. **Consolider** la baseline (une seule version par chemin : la plus récente),
  4. Considérer la baseline comme unique matériau autorisé pour l’analyse.

Critères minimaux de “lecture parfaite” (pour autoriser la mise à jour baseline) :
- Lecture effectuée via URL Raw **au SHA** : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
- Téléchargement binaire (octets bruts) + lecture locale
- Contenu cohérent (non vide, non HTML d’erreur, non tronqué)
- Génériques Java correctement lus lorsque applicable (aucun “Raw Type” dû à une lecture incorrecte)

En cas de lecture imparfaite :
- Si l’IA ne peut pas garantir la lecture correcte (ex : doute sur génériques, contenu altéré, lecture incomplète) :
  ➜ signaler explicitement **"incident de lecture"**  
  ➜ suspendre toute analyse/génération  
  ➜ relancer automatiquement la lecture jusqu’à succès (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02  
  ➜ à échec persistant : basculer en MODE OFFLINE (bundle) selon le présent contrat

Objectif :

➡️ Garantir que toute analyse/correction est **reproductible**, **audit-ready** et strictement basée sur une baseline **consolidée** et **alignée** sur le SHA fourni.

### Règle d’exploitation de la baseline consolidée (ANTI-RELECTURE-GITHUB-BUNDLE)

Principe :

➡️ Une fois la baseline **rafraîchie** et **consolidée** au **SHA courant** (lecture parfaite + consolidation réalisées), l’IA est autorisée à **analyser / diagnostiquer / coder** uniquement à partir des fichiers de la **baseline**, sans relecture GitHub ni bundle, tant que le SHA courant reste inchangé.

Règle absolue (ANTI-RELECTURE-APRES-BASELINE-A-JOUR) :
- Si la baseline est jugée **parfaitement à jour** au SHA courant (lecture parfaite + consolidation réalisées), l’IA **ne doit pas** relire GitHub/bundle “par prudence” pour travailler : la baseline devient le **matériau unique** de travail opérationnel.
- GitHub au SHA et le bundle OFFLINE ne servent ensuite **qu’à** :
  1. rafraîchir la baseline lors d’un **nouveau SHA**,
  2. résoudre un **incident de lecture**,
  3. satisfaire une **demande explicite** de relecture par l’Utilisateur.

Traçabilité :
- Le bloc **PREUVE DE LECTURE** peut être produit à partir des **preuves déjà établies** lors du rafraîchissement (URL Raw SHA utilisée lors de la lecture, taille, checksum), sans nécessiter une nouvelle lecture GitHub/bundle tant que le SHA courant n’a pas changé.

Révocation immédiate :
- Si l’IA constate un doute sur la baseline (fichier requis manquant, incohérence, génériques illisibles/douteux, divergence constatée, ou **incident de lecture**) :
  ➜ signaler explicitement **"incident de lecture"**  
  ➜ suspendre toute analyse/génération  
  ➜ relancer la lecture (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02  
  ➜ à échec persistant : basculer en MODE OFFLINE

### Bloc PREUVE DE LECTURE (OBLIGATOIRE)

Toute réponse contenant une **analyse**, un **diagnostic** ou du **code** DOIT commencer par un bloc **PREUVE DE LECTURE** listant au minimum :
- **SHA courant**
- **Source de lecture** de `docs/ai/CONTRAT_IA.md` : `GITHUB_RAW_SHA` / `BUNDLE_OFFLINE` / `CHAT`
- **URL Raw SHA** utilisée (si source = `GITHUB_RAW_SHA`, sinon `N/A`)
- **Chemin local** utilisé (si source = `BUNDLE_OFFLINE` ou `CHAT`, sinon `N/A`)
- **Taille** (en octets) du fichier lu
- **Nombre de lignes** du fichier lu
- **Checksum** du contenu lu (SHA-256)

Interdiction : aucune analyse/diagnostic/génération ne doit être produite tant que ce bloc n’a pas été affiché et que la baseline n’a pas été rafraîchie/consolidée au SHA courant.

## 25) Détection des modifications au nouveau SHA (comparaison GitHub ↔ baseline)

Principe :

➡️ À chaque **nouveau SHA unique** fourni par l’Utilisateur, l’IA doit être capable de **détecter et constater** les modifications par rapport à la baseline consolidée, uniquement à partir de lectures GitHub au SHA (RT-LECTURE-GITHUB-02) et/ou du bundle OFFLINE.

Algorithme obligatoire (à réception d’un nouveau SHA) :

1. **Pré-lecture d’ancrage (obligatoire)**
   - Lire au SHA : `docs/ai/MANIFEST_IA.yaml`, `docs/ai/perimetre.yaml`, `docs/ai/CONTRAT_IA.md`.

2. **Découverte du périmètre**
   - Extraire la liste canonique des `paths` depuis `docs/ai/perimetre.yaml`.
   - Reconstruire toutes les URLs Raw SHA : `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`.

3. **Détermination des fichiers requis (performance)**
   - Par défaut, l’IA rafraîchit **uniquement** les fichiers **nécessaires** à la demande courante :
     - pivots (`docs/ai/CONTRAT_IA.md`, `docs/ai/MANIFEST_IA.yaml`, `docs/ai/perimetre.yaml`),
     - fichiers explicitement concernés (PORT/ADAPTER/tests/document de contrat),
     - fichiers strictement indispensables à la compréhension/compilation de la zone (si besoin).
   - Si l’Utilisateur demande explicitement un **rafraîchissement complet**, alors l’IA traite **tous** les `paths` du périmètre.

4. **Lecture + contrôle technique (RT-LECTURE-GITHUB-02)**
   - Pour chaque fichier requis :
     - télécharger en binaire (octets bruts) via Raw SHA,
     - lire localement,
     - vérifier la cohérence (non vide, non HTML d’erreur, non tronqué),
     - vérifier les génériques lorsque applicable (aucun “Raw Type” dû à une lecture incorrecte).

5. **Comparaison stricte avec la baseline**
   - Comparer strictement le contenu lu au SHA avec la version correspondante en baseline consolidée :
     - **Fichiers texte** : comparaison **ligne à ligne** avec normalisation **CRLF/LF**.
     - **Fichiers binaires** : comparaison **octet à octet**.
   - Classer chaque fichier dans l’un des statuts :
     - **INCHANGÉ** : identique à la baseline,
     - **MODIFIÉ** : différence constatée,
     - **INACCESSIBLE** : non lisible au SHA (fichier manquant / erreur persistante).

6. **Signalements obligatoires**
   - Si un fichier attendu devient **INACCESSIBLE** :
     ➜ signaler explicitement **"problème grave"** (périmètre non satisfaisable au SHA),  
     ➜ suspendre analyse/génération.
   - Si la lecture d’un fichier est douteuse (ex : génériques illisibles) :
     ➜ signaler explicitement **"incident de lecture"**,  
     ➜ relancer automatiquement jusqu’à succès (max 3 tentatives),  
     ➜ à échec persistant : basculer en MODE OFFLINE.

7. **Mise à jour de la baseline (obligatoire, après lectures parfaites)**
   - Après lecture parfaite au SHA :
     - mémoriser strictement ligne à ligne dans la baseline,
     - écraser l’ancienne version,
     - consolider la baseline (une seule version par chemin : la plus récente).

Interdiction :
- Il est **interdit** de démarrer une analyse/diagnostic/génération tant que :
  - la comparaison GitHub ↔ baseline n’a pas été effectuée au SHA courant,
  - et la baseline n’a pas été rafraîchie/consolidée lorsque des modifications existent.

Objectif :

➡️ Garantir que l’IA peut, à partir du **SHA seul**, (1) découvrir, (2) lire, (3) comparer, (4) prouver les modifications, et (5) travailler uniquement sur une baseline à jour.

## 26) Méthode d’analyse d’alignement PORT–ADAPTER (procédure + analyse en 7 points)

Objectif : analyser l’alignement **PORT ↔ ADAPTER** **méthode par méthode**, de façon exhaustive et contrôlée, avec une boucle de correction reproductible (SHA-only), sans jamais “sauter” un défaut sérieux.

### 26.1 Principe de progression (gating strict)

- L’IA analyse **une seule méthode à la fois**.
- **Interdiction** de passer à la méthode suivante tant que l’analyse profonde détecte un problème sérieux sur la méthode courante (contrat incorrect, implémentation déficiente, tests incomplets/non alignés, etc.).
- Le **passage à la méthode suivante** est **décidé uniquement par l’Utilisateur**.
- **RAPPEL** : ne pas analyser les **LOG** ni leurs imports.

### 26.2 Analyse profonde en 7 points (checklist obligatoire, méthode par méthode)

Pour chaque méthode analysée (ex : « analyser l’alignement du PORT GATEWAY `ProduitGatewayIService` avec l’ADAPTER GATEWAY `ProduitGatewayJPAService` »), l’IA applique **strictement** les points suivants, **dans l’ordre**, et **ne passe pas au point suivant** tant qu’un problème sérieux subsiste :

1. **Qualité des contrats dans le PORT**
   - Signature, exceptions, préconditions, cohérence des types, cohérence des messages/constantes de contrat.

2. **Qualité du code de l’ADAPTER**
   - Implémentation conforme au contrat du PORT, robustesse, gestion des cas limites, cohérence avec l’architecture.

3. **Javadoc du PORT**
   - Vérifier que chaque javadoc de méthode contient bien les 3 rubriques :
     - **INTENTION TECHNIQUE**
     - **CONTRAT TECHNIQUE**
     - **GARANTIES TECHNIQUES et METIER**
   - Contrôler l’homogénéité du formalisme HTML (`div`, `ul`, `li`, exceptions jetées, etc.).
   - **RAPPEL** : respecter systématiquement la javadoc existante et son formalisme HTML (sauf si elle est fausse).

4. **Javadoc de l’ADAPTER**
   - Contrôler la javadoc (hors `{@inheritDoc}`) des méthodes **privées** de l’ADAPTER : intention/contrat/garanties lorsque pertinent + respect du style HTML du projet.

5. **Commentaires (de bloc) dans l’ADAPTER**
   - Vérifier la présence, la cohérence et l’homogénéité des commentaires de bloc existants ; ne pas les dégrader ; les reproduire dans le code généré.

6. **Détection exhaustive des défauts**
   - Rechercher **absolument tout** ce qui ne va pas en vue d’une correction finale.
   - Examiner les remarques **point par point pour la méthode en cours**, **pas tout d’un coup**.

7. **Alignement et complétude des tests JUnit**
   - Vérifier l’alignement et la complétude des tests relatifs à la méthode :
     - **Test Mock** + **Test d’intégration**
   - Vérifier l’homogénéité du code (PORT/ADAPTER/tests) avec le code similaire existant (parent/enfants).

Précondition absolue :
**lecture stricte** (ligne à ligne, CRLF/LF normalisés) de `docs/ai/CONTRAT_IA.md` et des fichiers nécessaires **avant toute analyse** ou **tout code** (cf. §24, RT-LECTURE-GITHUB-02).

### 26.3 Boucle opérationnelle (7 points)

1. **Détection d’un défaut d’alignement** :
   l’IA produit une première correction **autonome** et **directement intégrable dans STS** (javadoc + méthode complète / classe complète selon le besoin), en précisant **où** l’intégrer (classe + méthode / fichier concerné).

2. L’Utilisateur **applique** la correction dans STS et **rejoue les tests unitaires**.

3. Si tests **verts**, l’Utilisateur **commit/push** et fournit un **nouveau SHA unique**.

3bis. Si tests **KO**, l’Utilisateur demande une **réécriture** de la correction ou du test incriminé.

4. L’IA relit sur GitHub au **SHA** (exclusivement via **RT-LECTURE-GITHUB-02**) et, en l’absence d’incident de lecture, **mémorise** le(s) fichier(s) corrigé(s) en baseline, puis **consolide** la baseline (une seule version par chemin).

5. À partir de là, l’IA **vérifie** la bonne application de la correction et **relance l’analyse** de **la même méthode** directement **depuis la baseline** (sans relecture GitHub/bundle).

5bis. Si l’alignement est parfait : l’IA répond **uniquement** :
`alignement parfait`

5ter. Sinon : l’IA propose une **nouvelle correction autonome** directement intégrable STS → retour au point **2**.

6. Quand **plus aucun défaut** ne subsiste sur la méthode courante : l’IA répond **uniquement** :
`alignement parfait`
→ l’Utilisateur peut décider de passer à la méthode suivante.

### 26.4 Compatibilité avec les modes (LECTURE / DIAGNOSTIC / CODER)

- Cette procédure s’applique aux analyses d’alignement en **MODE DIAGNOSTIC**.
- Toute production de code reste soumise à la règle :
  **MODE CODER uniquement si “coder” est explicitement demandé**.
- En cas de baseline non à jour au SHA courant, ou d’**incident de lecture**, la procédure est **suspendue** jusqu’à restauration d’une baseline saine (cf. §24 et §6).

---

## 27) Gouvernance du `docs/ai/CONTRAT_IA.md` (stabilité et changements batchés)

Objectif : éviter la dérive et les micro-changements continus du CONTRAT, tout en permettant des évolutions contrôlées, auditables et efficaces.

### 27.1 Principe de stabilité

- Par défaut, `docs/ai/CONTRAT_IA.md` est considéré **VALIDÉ** (verrouillé).
- L’IA **ne propose pas** de modifications du CONTRAT “à chaque échange”.
- Les suggestions d’amélioration peuvent être listées en **backlog** (dans le chat), mais **sans** modifier le CONTRAT tant que l’Utilisateur n’a pas explicitement demandé une mise à jour.

### 27.2 Déclenchement explicite d’une mise à jour (gating)

Une mise à jour du CONTRAT n’est autorisée que si l’Utilisateur la demande explicitement (exemples : “MAJ CONTRAT”, “mettre à jour le CONTRAT”, ou message contenant **coder** + demande de consolidation du CONTRAT).

Dans ce cas :
1. L’IA prépare une **proposition consolidée unique** (toutes les modifications nécessaires) au format **fichier complet** `docs/ai/CONTRAT_IA.md` (ANTI-SNIPPETS).
2. L’Utilisateur applique la mise à jour, commit/push, fournit un **nouveau SHA**.
3. L’IA relit au SHA via **RT-LECTURE-GITHUB-02**, puis **consolide** la baseline.
4. Le CONTRAT repasse en statut **VALIDÉ** (verrouillé) jusqu’à la prochaine demande explicite.

### 27.3 Baseline potentiellement “en avance” sur le bundle OFFLINE

- La baseline peut être **en avance** sur le bundle OFFLINE si l’Utilisateur a committé des changements (contrat, code) sans avoir encore produit/committé un nouveau bundle.
- Dans ce cas, la source de vérité reste l’ordre défini en §1 : baseline (si prouvée à jour) > GitHub@SHA > bundle OFFLINE.
- Le bundle OFFLINE est un **artefact** de continuité : il peut être produit ultérieurement (cf. Variante A) sans bloquer la progression, tant que la baseline est prouvée saine au SHA courant.

### 27.4 Exigences de performance (Java)

- Pour les fichiers **texte** (Java et assimilés), la comparaison opérationnelle est **ligne à ligne** avec normalisation **CRLF/LF**.
- L’IA doit privilégier le **rafraîchissement minimal par objectif** (cf. §25) afin d’éviter de relire un périmètre inutilement large quand la tâche porte sur un sous-ensemble (PORT/ADAPTER/tests).

## 28) Règles prioritaires invariantes (sacralisation) — RT-PRIORITAIRE-* / RT-MEMORISATION-* / REGLE-COMMENTAIRE-JAVADOC-07

Cette section sacralise explicitement des règles déjà appliquées dans le workflow, afin d’éliminer toute ambiguïté.

---

### 28.1 RT-PRIORITAIRE-00 (VALIDÉ) — Relecture des règles avant tout code

Avant toute génération de code (MODE CODER), l’IA DOIT relire strictement :
- les règles de travail du présent CONTRAT,
- les règles de qualité et d’architecture,

et NE DOIT JAMAIS deviner / supposer / improviser.

---

### 28.2 RT-PRIORITAIRE-01 (VALIDÉ) — Relecture des fichiers baseline avant tout code

Avant toute génération de code (MODE CODER), l’IA DOIT relire strictement ligne à ligne tous les fichiers de travail nécessaires dans la baseline afin d’en déduire sans hypothèse :
- types de retour des méthodes,
- imports,
- dépendances entre classes,
- contraintes et invariants (y compris listes défensives immutables),
- modificateurs d’accès (public/private/protected),
- signatures exactes et surcharges.

---

### 28.3 RT-PRIORITAIRE-02 (VALIDÉ) — Relecture strictement ligne à ligne

L’IA DOIT relire strictement ligne à ligne les fichiers concernés de la baseline (liste fournie le cas échéant ; sinon toute la baseline) avant toute action nécessitant ces fichiers.

---

### 28.4 RT-PRIORITAIRE-03 (VALIDÉ) — Déduction + preuve de relecture par signatures

Après application de RT-PRIORITAIRE-02, l’IA DOIT déduire (avant tout code) :
- constructeurs,
- méthodes (signatures exactes),
- types de retour,
- modificateurs,
- dépendances,
- contraintes.

Exigence de traçabilité :
- En MODE CODER, l’IA DOIT confirmer la relecture en listant explicitement les signatures exactes (constructeurs/méthodes), types et modificateurs pertinents pour la tâche demandée.
- L’IA ne doit pas oublier les fichiers explicitement cités par l’Utilisateur (ex : `TypeProduitGatewayJPAService.java`).

---

### 28.5 RT-PRIORITAIRE-100 (VALIDÉ) — Intangibilité de la baseline (hors ordre explicite)

Tout fichier déposé/mémorisé dans la baseline DOIT être conservé intégralement, ligne à ligne, sans modification.

Interdiction totale de modifier un fichier baseline tant que l’Utilisateur n’a pas ordonné explicitement de travailler dessus.

---

### 28.6 RT-PRIORITAIRE-101 (VALIDÉ) — Vérification baseline avant toute affirmation factuelle

Avant de répondre, analyser ou coder, l’IA DOIT vérifier dans la baseline les informations factuelles qu’elle avance (imports, signatures, constantes, etc.).

Interdiction d’écrire des hypothèses conditionnelles du type “si l’import X existe…”.

### 28.7 RT-PRIORITAIRE-102 (VALIDÉ) — Baseline unique source de vérité + consolidation

La baseline est l’unique source de vérité.

Toute lecture GitHub au SHA DOIT conduire à une consolidation de la baseline en écrasant toute version précédente (une seule version par path, la plus récente).

Aucun fichier baseline ne doit être oublié / égaré / perdu.

Une consolidation n’existe que si elle est physiquement prouvée : mémoire, ancien `/mnt/data`, ancien `file_id`, ancien résultat `file_search`, ancien bundle, ancienne conclusion ou déclaration précédente ne prouvent jamais une consolidation. Une baseline ou fenêtre sans répertoire, sans manifeste, sans métriques et sans comparaison stricte `source == baseline == fenêtre active` est invalide.

Modification uniquement sur ordre explicite de l’Utilisateur.

---

### 28.8 RT-LECTURE-GITHUB-02 — Clarification “génériques illisibles”

Si l’IA ne peut pas lire correctement les génériques (doute, troncature, rendu altéré, Raw Types possibles) :
- elle DOIT signaler un “incident de lecture”,
- elle DOIT demander confirmation (ou l’élément manquant) avant toute conclusion,
- elle DOIT relancer automatiquement la lecture (max 3 tentatives) conformément à RT-LECTURE-GITHUB-02,
- puis basculer en MODE OFFLINE si échec persistant.

---

### 28.9 RT-MEMORISATION-UNICITE-01 (VALIDÉ) — Unicité de mémorisation

Lors de la mémorisation d’un élément (fichier, règle, chat, etc.), l’IA DOIT effacer les versions précédentes.

Invariant : ne conserver qu’une seule version, toujours la plus récente.

---

### 28.10 RT-MEMORISATION-COURANTE-02 (VALIDÉ) — “mémoriser. baseline” (analyse sans codage)

Commande : “mémoriser. baseline”

- Lire et analyser le fichier transmis (javadoc/code/commentaires),
- Stocker ligne à ligne sans modification dans la baseline en écrasant toute version précédente,
- Ne générer aucun code, aucune suggestion,
- Produire uniquement une synthèse structurée de mémorisation.

---

### 28.11 RT-MEMORISATION-SIMPLE-03 (VALIDÉ) — “mémoriser. baseline. Pas analyser. Pas coder”

Commande : “mémoriser. baseline. Pas analyser. Pas coder”

- Stocker ligne à ligne sans modification dans la baseline en écrasant toute version précédente,
- Aucun traitement immédiat, aucune analyse, aucun code, aucune suggestion,
- Produire uniquement une synthèse structurée de mémorisation.

---

### 28.12 REGLE-COMMENTAIRE-JAVADOC-07 (VALIDÉ) — Javadoc et commentaires (HTML + bloc)

- Les commentaires de l’Utilisateur doivent être reproduits en respectant le formalisme HTML `<div> ... </div>` sans lignes vides, sauf s’ils sont faux.
- Les commentaires ajoutés par l’IA doivent être exclusivement des commentaires de bloc (jamais de commentaires de ligne).
- La javadoc existante doit être conservée (sauf si fausse) et la javadoc ajoutée par l’IA doit respecter le style HTML du projet.

---

### 28.13 RT-PRIORITAIRE-CANAL-LECTURE-01 (VALIDÉ) — Sélection du canal de lecture GitHub@SHA avant “incident de lecture”

Objectif : éviter tout faux “incident de lecture” dû à l’échec d’un canal technique unique alors qu’un autre canal de téléchargement binaire est fonctionnel.

Règle prioritaire invariante :
- Lors d’une lecture GitHub@SHA (RT-LECTURE-GITHUB-02), l’IA DOIT tenter en priorité la lecture par **web tool**.
- Pour la jambe de **téléchargement binaire local**, l’IA DOIT utiliser en priorité la primitive dédiée **`container.download`**.
- Si la lecture par **web tool** échoue, ou si **`container.download`** échoue, l’IA DOIT basculer d’elle-même sur une autre technique **container** autorisée, sans attendre une nouvelle instruction de l’Utilisateur.
- Toutes les techniques doivent strictement respecter la même méthode contractuelle :
  - URL Raw@SHA
  - téléchargement binaire local (octets bruts)
  - lecture locale du fichier téléchargé
  - vérification stricte des génériques (aucun Raw Type)

Règles de tentative :
- L’IA DOIT effectuer au maximum **3 tentatives au total**.
- L’IA DOIT garantir **au moins une tentative par web tool**.
- L’IA DOIT garantir **au moins une tentative par `container.download`** avant de conclure à un “incident de lecture”.
- Si nécessaire, l’IA DOIT garantir **au moins une tentative par autre technique container autorisée** avant de conclure à un “incident de lecture”.

Règle de déclaration d’incident :
- L’IA NE DOIT déclarer **"incident de lecture"** que si :
  - la technique **web tool** a été tentée,
  - la technique **`container.download`** a été tentée,
  - une autre technique **container** a été tentée si nécessaire,
  - les tentatives ont échoué,
  - et que, pour chacune des tentatives, tout contenu manifestement invalide (HTML d’erreur, contenu vide, tronqué, incohérent) est traité comme un échec de tentative.

En cas de succès (par **web tool**, par **`container.download`** ou par une autre technique **container** autorisée) :
- L’IA poursuit la procédure RT-LECTURE-GITHUB-02 :
  - lecture locale du fichier téléchargé,
  - vérification de cohérence,
  - vérification stricte des génériques (aucun Raw Type).
- Si les génériques sont illisibles/douteux :
  - l’IA DOIT demander confirmation avant toute conclusion.

Fallback :
- Le MODE OFFLINE (bundle : PROVENANCE + CHECKSUMS + FILES) est autorisé après un **incident de lecture GitHub réel** établi selon la présente règle :
  - web tool tenté,
  - `container.download` tenté,
  - autre technique container tentée si nécessaire,
  - ≤ 3 tentatives.
- Le MODE OFFLINE est également autorisé comme **preuve binaire locale de secours** lorsque la lecture GitHub Raw@SHA par web tool est OK mais que le téléchargement binaire local échoue pour une cause locale indépendante du contenu (`derived_content_type=text/html`, `derived_content_type=text/x-java is not allowed`, DNS container, primitive indisponible).
- Dans ce second cas, l’IA doit formuler :
```text
Lecture GitHub Raw@SHA : OK.
Incident GitHub : non.
Téléchargement binaire local GitHub : KO technique local.
Preuve binaire locale : bundle OFFLINE validé par CHECKSUMS.
```

---
### 28.14 RT-DOWNLOAD-BINAIRE-LOCAL-01 (VALIDÉ) — Primitive locale préférée pour le téléchargement Raw@SHA

Objectif : éliminer les faux incidents de lecture dus à l’usage prioritaire d’une technique réseau shell fragile alors qu’une primitive locale de téléchargement binaire dédiée est disponible.

Règle obligatoire :
- Pour satisfaire la jambe **“téléchargement binaire local”** de RT-LECTURE-GITHUB-02, l’IA DOIT utiliser en priorité **`container.download`**.
- L’usage d’une commande réseau shell (`curl`, `wget`, etc. via `container.exec`) ne peut être qu’un **fallback** et ne doit jamais être la technique locale préférée.
- Toute technique de fallback doit continuer à respecter strictement :
  - l’URL Raw@SHA,
  - le téléchargement binaire local,
  - la lecture locale des octets bruts,
  - la vérification stricte des génériques.

Conséquences :
- L’IA NE DOIT PAS conclure à un **"incident de lecture"** tant que **`container.download`** n’a pas été tenté.
- Si **`container.download`** réussit, la lecture GitHub@SHA est réputée nominalement valide au titre de la jambe locale de téléchargement binaire.
- Si **`container.download`** échoue, l’IA DOIT relancer automatiquement selon la politique de tentatives puis basculer sur une autre technique **container** autorisée avant toute conclusion d’échec définitif.

Interdiction absolue :
- conclure à une indisponibilité GitHub, à un défaut de SHA, ou à une illisibilité d’un fichier alors que seule une technique réseau shell locale a échoué sans tentative préalable de **`container.download`**.

---



### 28.14.1 RT-LECTURE-GITHUB-FAUX-POSITIF-MIME-01 (VALIDÉ) — Faux positif MIME `text/html` / `text/x-java` sur fichier source Java Raw@SHA

Objectif : empêcher qu’un fichier source Java légitime soit déclaré à tort comme une page HTML, comme un fichier interdit, ou comme un incident GitHub lorsque l’échec provient uniquement d’une heuristique MIME locale ou d’un filtre local de primitive.

Constat technique autorisé :
- un fichier `.java` peut contenir légalement une Javadoc avec de nombreuses balises HTML (`<div>`, `<p>`, `<ul>`, `<li>`, `<style>`, etc.) ;
- une primitive locale ou un outil de détection MIME peut alors classer à tort le fichier comme `text/html` ;
- une primitive locale peut également refuser de sauvegarder un fichier source `.java` avec un message du type `derived_content_type=text/x-java is not allowed` ;
- ces classements ou refus locaux ne prouvent pas que GitHub a renvoyé une page HTML, un mauvais contenu ou un fichier illisible.

Règle de qualification obligatoire :
- Si l’URL est une URL `raw.githubusercontent.com` reconstruite au SHA figé ;
- si GitHub répond `HTTP 200` ;
- si le `Content-Type` HTTP réel est `text/plain` ou autrement cohérent avec un fichier source brut ;
- si le chemin attendu se termine par une extension source cohérente, notamment `.java` ;
- si la taille HTTP annoncée est cohérente avec le manifeste, le bundle OFFLINE ou la baseline saine ;
- alors un `derived_content_type=text/html` local ou un `derived_content_type=text/x-java is not allowed` local DOIT être qualifié comme **faux positif / refus MIME local probable**, et NON comme preuve d’un incident GitHub.

Formulation obligatoire :
```text
GitHub Raw@SHA : OK.
Réponse HTTP : OK.
Content-Type HTTP réel : cohérent avec un fichier source brut.
Échec local : refus ou alerte de la primitive de téléchargement par faux positif / filtre MIME local.
Incident GitHub : non.
```

Interdictions :
- Interdiction de conclure à un incident GitHub sur le seul fondement de `derived_content_type=text/html`.
- Interdiction de conclure à un incident GitHub sur le seul fondement de `derived_content_type=text/x-java is not allowed`.
- Interdiction de présenter la lecture GitHub Raw@SHA comme incomplète lorsque le Raw@SHA a été lu correctement par web tool et que seul le téléchargement local est bloqué.
- Interdiction de modifier le fichier Java, de supprimer sa Javadoc HTML ou de dégrader le formalisme documentaire du projet pour satisfaire une heuristique MIME.
- Interdiction d’utiliser une lecture HTML, un rendu GitHub ou une page web comme substitut à la lecture Raw@SHA.

Procédure obligatoire après faux positif / refus MIME local :
1. conserver l’URL Raw@SHA stricte ;
2. déclarer explicitement le faux positif ou refus MIME local probable ;
3. distinguer le statut **Lecture GitHub Raw@SHA** du statut **Téléchargement binaire local GitHub** ;
4. tenter une autre technique container autorisée permettant un téléchargement binaire sans filtrage MIME excessif ;
5. relire localement les octets bruts réellement sauvegardés si une sauvegarde locale réussit ;
6. calculer et rapporter au minimum : taille, SHA-256, nombre de `\n`, statut EOF, premières lignes et dernières lignes utiles ;
7. comparer avec le manifeste, la baseline saine ou le bundle OFFLINE validé ;
8. vérifier les génériques et les signatures utiles ;
9. seulement ensuite conclure.

Si le fallback binaire échoue pour une cause indépendante du contenu (DNS, réseau container, primitive indisponible, refus technique local), l’IA doit déclarer :
```text
Lecture GitHub Raw@SHA : OK.
Incident GitHub : non.
Téléchargement local par primitive dédiée : KO par faux positif / refus MIME local.
Fallback binaire container : KO technique indépendant.
Bascule OFFLINE validée par CHECKSUMS : requise pour la preuve binaire locale.
```

Conséquence de consolidation :
- La consolidation peut reposer sur le bundle OFFLINE uniquement si `PROVENANCE`, `CHECKSUMS` et les fichiers extraits sont cohérents et contrôlés.
- Dans ce cas, l’IA doit dire que la consolidation repose sur OFFLINE contrôlé pour la preuve binaire locale, et non prétendre que la jambe locale GitHub complète a réussi.
- L’IA ne doit pas dégrader la lecture GitHub Raw@SHA déjà validée.

---

### 28.14.2 RT-LECTURE-BUNDLE-OFFLINE-STRICT-01 (VALIDÉ) — Contrôle strict d’un bundle OFFLINE

Objectif : rendre opérationnelle et bloquante la lecture d’un bundle OFFLINE, notamment lorsqu’il est utilisé comme preuve binaire locale après lecture GitHub Raw@SHA OK mais téléchargement local GitHub KO.

Structure obligatoire :
```text
AI_OFFLINE/INDEX.txt
AI_OFFLINE/PROVENANCE.yaml
AI_OFFLINE/CHECKSUMS.sha256
AI_OFFLINE/FILES/**
```

Contrôles obligatoires :
1. lire `PROVENANCE.yaml` ;
2. vérifier `repo_owner`, `repo_name` et `sha` ;
3. vérifier que `sha` correspond au SHA courant attendu ;
4. lire `CHECKSUMS.sha256` ;
5. recalculer le SHA-256 de chaque fichier sous `AI_OFFLINE/FILES/**` listé ;
6. refuser tout fichier manquant ;
7. refuser tout checksum incohérent ;
8. refuser tout chemin dupliqué comme version active ;
9. comparer le nombre de fichiers listés, extraits et contrôlés ;
10. produire une preuve de lecture OFFLINE avant toute consolidation.

Formulation obligatoire :
```text
Bundle OFFLINE lu : oui.
SHA PROVENANCE :
SHA attendu :
CHECKSUMS contrôlés : oui.
Fichiers OK :
Fichiers manquants :
Fichiers en erreur :
Chemins dupliqués :
Décision : exploiter / rejeter / incident.
```

Interdictions :
- ne jamais exploiter un bundle OFFLINE d’un ancien SHA comme s’il correspondait au SHA courant ;
- ne jamais exploiter un ancien dossier `AI_OFFLINE/` déjà extrait ;
- ne jamais consolider si un checksum manque ou diverge ;
- ne jamais utiliser `AI_OFFLINE/FILES/**` sans rattacher le bundle au SHA courant et, si le zip vient du chat, au dernier `file_id`.

---

### 28.14.3 RT-LECTURE-ZIP-CHAT-OFFLINE-01 (VALIDÉ) — Zip joint au chat contenant un bundle OFFLINE

Objectif : empêcher les erreurs de lecture des zip joints au chat, notamment lorsque plusieurs bundles portent le même nom logique ou que `/mnt/data/` réexpose un ancien fichier.

Règle absolue :
- le dernier `file_id` utilisateur est l’identité normative du zip joint ;
- `/mnt/data/<nomDuZip>` est un support de lecture, pas une identité ;
- l’extraction doit être fraîche, contrôlée et isolée ;
- aucun ancien zip local ni ancien dossier extrait ne peut servir de source active.

Procédure obligatoire :
1. appliquer `RT-LECTURE-CHAT-FICHIER-JOINT-STRICT-01` au zip lui-même ;
2. calculer taille, SHA-256, nombre d’entrées et racines du zip ;
3. contrôler les chemins d’archive contre toute sortie de dossier d’extraction ;
4. extraire dans un dossier neuf ;
5. si le zip contient `AI_OFFLINE/`, appliquer `RT-LECTURE-BUNDLE-OFFLINE-STRICT-01` ;
6. comparer ensuite seulement avec GitHub@SHA, baseline ou fenêtre active.

Interdictions :
- ne jamais demander à l’Utilisateur de renommer le zip ;
- ne jamais conclure depuis un zip local non rattaché au dernier `file_id` ;
- ne jamais utiliser une ancienne extraction ;
- ne jamais consolider directement depuis l’archive sans contrôle checksum complet.

---

### 28.15 RT-LECTURE-LITTERALE-01 (VALIDÉ) — Lecture littérale ancrée sur la méthode exacte de référence

Objectif : éliminer définitivement les erreurs de lecture par reconnaissance de motif, généralisation de style ou relecture “avec une idée en tête”.

Règle prioritaire invariante :
- Avant toute analyse, affirmation, javadoc, test ou génération de code, l’IA DOIT verrouiller la méthode exacte de référence déjà validée dans le même fichier, la même couche, le même objet métier et la même famille de méthode.
- L’IA DOIT effectuer une lecture littérale ancrée sur les lignes exactes de cette méthode de référence.
- Interdiction formelle de relire “avec une idée en tête”, par reconnaissance de motif, par généralisation de style, ou à partir d’une conclusion antérieure non revérifiée.
- Aucune règle de style ne peut être déduite globalement d’une classe, d’une couche, d’un souvenir de lecture ou d’un lot précédent.

Vérification point par point obligatoire :
- présence ou absence de balises HTML de javadoc (`<div>`, `<p>`, `<ul>`, `<li>`),
- vocabulaire exact,
- structure des puces,
- annotations exactes,
- placement du trait final `} // __________________________________________________________________`,
- nombre exact de lignes vides entre méthodes,
- tout autre élément de formalisme visible dans les lignes relues.

Règle de conclusion :
- L’IA NE DOIT conclure qu’à partir des éléments matériellement visibles dans la méthode exacte relue.
- Si la méthode exacte de référence n’a pas été relue littéralement ligne à ligne, l’IA DOIT s’arrêter, le signaler, et ne pas livrer.

---

### 28.16 RT-COMPARAISON-TEXTE-CRLF-01 (VALIDÉ) — Normalisation EOL obligatoire avant toute décision de différence réelle

Objectif : éliminer définitivement les faux positifs de comparaison sur les fichiers texte entre STS/chat, GitHub@SHA et bundle OFFLINE.

Règle prioritaire invariante :
- Pour tout fichier texte (`.md`, `.yaml`, `.py`, `.java`, `.xml`, `.properties`, `.sh`, etc.), l’IA DOIT distinguer strictement :
  - l’identité binaire (octet à octet),
  - l’identité textuelle canonique après normalisation EOL,
  - l’écart EOF (présence ou absence d’un saut de ligne final).
- Pour la comparaison de contenu texte, l’IA DOIT construire une vue canonique en remplaçant :
  - `CRLF` par `LF`,
  - puis `CR` isolé par `LF`.
- La comparaison métier DOIT ensuite être réalisée ligne à ligne sur cette vue canonique.
- L’écart EOF DOIT être contrôlé et signalé séparément ; il ne doit jamais être confondu avec une différence de ligne.

Définition prioritaire de « le même fichier » :
- Pour un fichier texte, « le même fichier » ne signifie pas nécessairement « mêmes octets ».
- Pour un fichier texte, « le même fichier » signifie : **même contenu texte canonique après normalisation EOL**, sauf si le fichier est binaire ou si le présent CONTRAT / le format du fichier lui donne explicitement une sémantique spéciale.

Règle de décision :
- Si l’identité binaire diffère uniquement à cause de `LF` / `CRLF`, mais que le contenu texte canonique est identique, l’IA DOIT qualifier le cas de :
  - **FAUSSE DIFFÉRENCE BINAIRE — EOL ONLY**
- Dans ce cas :
  - le fichier DOIT être considéré comme **INCHANGÉ** sur le plan métier / contenu ;
  - l’IA NE DOIT PAS annoncer un fichier **MODIFIÉ** ;
  - cet écart NE DOIT PAS bloquer la comparaison, la consolidation, le diagnostic ni la génération ;
  - l’IA DOIT signaler séparément l’écart binaire si cela est utile à l’audit.

Cas explicitement visés :
- `docs/ai/CONTRAT_IA.md`
- `docs/ai/perimetre.yaml`
- plus généralement tout fichier texte comparé entre STS/chat, GitHub@SHA et bundle OFFLINE.

Conséquence projet :
- GitHub en `LF` et bundle OFFLINE en `CRLF` ne doivent jamais produire un faux statut **MODIFIÉ** tant que le contenu texte normalisé est identique.

### 28.17 RT-LECTURE-DEPENDANCES-METHODE-01 (VALIDÉ) — Relecture obligatoire du contrat, de la classe cible, des dépendances utiles et des tests concernés

Objectif : éliminer définitivement les analyses Java incomplètes fondées sur une lecture partielle de la seule classe cible ou sur un souvenir de dépendances non revérifiées.

Règle prioritaire invariante :
- Avant toute analyse, audit, synthèse, validation, comparaison ou génération de code concernant une méthode Java, l’IA NE DOIT JAMAIS raisonner de mémoire.
- L’IA DOIT relire strictement au SHA courant :
  1. le contrat / PORT / interface applicable à la méthode ;
  2. la classe cible ;
  3. la méthode exacte concernée ;
  4. toutes les dépendances utiles à la compréhension réelle de cette méthode ;
  5. les tests concernés.
- L’IA DOIT travailler méthode par méthode, une seule méthode à la fois.

Dépendances utiles à relire selon le cas :
- PORT, interface ou contrat porté ou consommé ;
- super-classe et interfaces implémentées ;
- classes métier manipulées ;
- DTO, entities, mappers, repositories, gateways, services ;
- utilitaires, constantes, exceptions, annotations ;
- classes parentes, enfants, agrégées ou associées ;
- classes appelées directement ou indirectement dès lors qu’elles influencent le comportement observé ;
- tests Mock, tests d’intégration et toute classe de référence utile à la méthode.

Ordre canonique de lecture :
- d’abord le contrat ;
- ensuite la classe cible ;
- ensuite les dépendances utiles à la méthode exacte ;
- enfin les tests concernés.

Règle de conclusion :
- L’IA NE DOIT PAS conclure sur une méthode si une dépendance utile n’a pas été relue.
- Si une dépendance utile n’a pas encore été relue, l’IA DOIT déclarer explicitement que la lecture est incomplète, puis relire cette dépendance avant toute conclusion.
- L’IA NE DOIT PAS extrapoler à partir d’une autre classe “semblable”, d’une conclusion antérieure, d’un souvenir de lecture ou d’une analogie rapide.

Conséquence :
- Toute conclusion sur une méthode Java doit pouvoir être retracée à la séquence explicite : contrat -> classe cible -> méthode exacte -> dépendances utiles -> tests concernés.

---

### 28.18 RT-LECTURE-CHAT-ANTI-ETAT-ANCIEN-INDIRECT-01 (VALIDÉ) — Interdiction absolue d’exploiter un état ancien ou indirect

Objectif : empêcher les erreurs récurrentes de lecture de fichiers joints au chat provoquées par l’utilisation d’un ancien état local, d’un résultat indirect ou d’une mémoire au lieu du dernier upload réel.

Règle prioritaire invariante :
- L’IA NE DOIT JAMAIS exploiter un état ancien ou indirect comme source de vérité pour un fichier joint au chat.
- L’IA DOIT TOUJOURS repartir strictement du dernier `file_id` uploadé par l’Utilisateur.
- L’IA DOIT TOUJOURS apporter la preuve qu’elle est repartie de la dernière version uploadée.

États interdits comme source de vérité :
- ancien `/mnt/data/<nomDuFichier>` ;
- ancien `file_id` ;
- ancien résultat `file_search` ;
- ancienne métrique de taille, lignes, `\n`, EOF ou SHA-256 ;
- ancienne baseline ;
- mémoire long term ;
- mémoire top-mind ;
- fenêtre active ;
- conclusion précédente ;
- capture écran ou extrait partiel.

Procédure obligatoire :
1. identifier le dernier `file_id` ;
2. relire directement `/mnt/data/<nomDuFichier>` après ce dernier upload ;
3. recalculer taille, lignes, nombre de `\n`, EOF et SHA-256 ;
4. comparer les fichiers texte après normalisation EOL ;
5. contrôler l’EOF séparément ;
6. déclarer dans la `PREUVE DE LECTURE` : `Point de départ réel : dernier upload utilisateur` ;
7. déclarer dans la `PREUVE DE LECTURE` : `État ancien/indirect exploité : non`.

Conséquence :
- sans preuve de départ depuis le dernier upload réel, aucune analyse, aucun verdict, aucune mémorisation et aucune consolidation ne sont valides.

---

### 28.19 RT-LECTURE-CHAT-BARRIERE-FIABILITE-01 (VALIDÉ) — Barrière bloquante « preuve avant analyse »

Objectif : rendre les règles de lecture effectivement opposables en interdisant à l'IA de continuer lorsqu'elle ne peut pas prouver qu'elle lit la bonne version.

Règle prioritaire invariante :
- L'IA DOIT appliquer le principe **PREUVE AVANT ANALYSE**.
- L'IA NE DOIT produire aucune analyse, aucun verdict, aucune correction, aucune mémorisation et aucune consolidation tant que la preuve matérielle du dernier upload réel n'est pas complète.
- En cas de doute, ambiguïté, troncature, divergence entre états locaux, métriques instables, capture écran contradictoire ou rattachement non prouvé au dernier `file_id`, l'IA DOIT appliquer le mode **fail closed** : s'arrêter, déclarer `LECTURE NON FIABLE`, expliquer la cause, et ne rien consolider.

Barrières bloquantes minimales :
1. dernier `file_id` réel identifié ;
2. chemin local lu rattaché à ce dernier upload ;
3. métriques recalculées depuis les octets lus : taille, lignes, `\n`, EOF, SHA-256 ;
4. bloc demandé identifié sans mélange avec un autre bloc ;
5. état ancien ou indirect explicitement non exploité ;
6. conclusion limitée au fichier et au bloc réellement relus.

Réponse obligatoire en cas d'échec d'une barrière :

```text
LECTURE NON FIABLE.
Je ne peux produire ni analyse, ni verdict, ni correction, ni consolidation.
Cause : <cause précise>
```

Conséquences :
- Il est moins grave de suspendre temporairement une analyse que de conclure depuis un mauvais fichier.
- Une réponse de fond produite sans preuve de lecture complète est invalide.
- Une consolidation produite sans preuve `dernier upload == baseline == fenêtre active` est invalide.

---

## 29) Gouvernance canonique par couches (sacralisation progressive)

Objectif : permettre à l’IA de découvrir la structure complète de l’application, les contrats applicables et les formalismes associés, de façon **couche par couche**, avec un bootstrap reproductible, audit-ready et indépendant de la mémoire interne.

### 29.1 Nomenclature canonique des couches

Les couches canoniques du projet sont désormais :

1. `couche_ia`
2. `couche_configuration_tests`
3. `couche_metier`
4. `couche_dto`
5. `couche_persistance`
6. `couche_services`
7. `couche_controllers`
8. `couche_vues`

Sous-couches logiques admises :
- `couche_services.gateway`
- `couche_services.uc`
- `couche_persistance.jpa`

### 29.2 Couche bootstrap obligatoire : `couche_ia`

La première couche à lire avant toute action de l’IA est `couche_ia`.

Cette couche est **sacralisée** comme couche de bootstrap et doit être lue avant toute analyse, tout diagnostic, tout code, toute comparaison et toute consolidation.

La couche `couche_ia` comprend au minimum les fichiers suivants :

#### IA / gouvernance
- `docs/ai/CONTRAT_IA.md`
- `docs/ai/MANIFEST_IA.yaml`
- `docs/ai/perimetre.yaml`

#### Outils IA
- `tools/ai/make_offline_bundle.py`
- `tools/ai/pack_reading_list.py`
- `tools/ai/paths.txt`
- `tools/ai/perimetre_resolver.py`
- `tools/ai/resolve_raw_sha.py`

#### Contrats locaux à relire avant action de l’IA
- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`
- `docs/contrats/metier/TypeProduit.md`
- `docs/contrats/metier/SousTypeProduit.md`
- `docs/contrats/metier/Produit.md`
- `docs/contrats/dto/TypeProduitDTO.md`
- `docs/contrats/dto/SousTypeProduitDTO.md`
- `docs/contrats/dto/ProduitDTO.md`
- `docs/contrats/persistance/CouchePersistance.md`
- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`

### 29.3 Obligation de prélecture de `couche_ia`

Avant toute action, l’IA DOIT relire strictement `couche_ia` dans le dépôt au SHA courant ou dans la baseline consolidée si elle est à jour.

Règles :
- aucun nouveau chat ne doit s’appuyer sur la mémoire seule ;
- la découverte des règles, contrats, packs, couches et formalismes doit provenir du dépôt lui-même ;
- `couche_ia` doit être résolue depuis `docs/ai/perimetre.yaml` et bootstrapée via `tools/ai/paths.txt`.

### 29.4 Anticipation obligatoire des couches futures

L’IA ne doit pas raisonner seulement par liste ponctuelle de fichiers déjà rencontrés.

L’IA doit raisonner par **couches canoniques**.

Chaque couche canonique doit pouvoir être décrite par :
- un nom canonique ;
- un statut (`validated_locked`, `validated_unlocked`, `declared`, `reserved`) ;
- un ou plusieurs packs de périmètre ;
- des fichiers pivots ;
- des contrats locaux éventuels ;
- des tests Mock et d’intégration associés ;
- des règles de lecture applicables.

Une couche peut être pré-déclarée même si elle n’est pas encore totalement remplie.

Objectif :
- éviter toute réécriture du modèle IA lors de l’apparition de nouvelles classes ;
- permettre des audits globaux par couche ;
- garantir qu’une IA puisse découvrir la structure complète du projet même dans un nouveau chat.

### 29.5 Audit global par couche

L’Utilisateur doit pouvoir demander à l’IA un audit global d’une couche canonique.

Pour qu’un audit global soit possible, chaque couche doit permettre à l’IA de retrouver, comme un tout cohérent :
- les classes principales ;
- les utilitaires liés ;
- les tests JUnit Mock ;
- les tests JUnit d’intégration ;
- les contrats locaux applicables ;
- les formalismes spécifiques.

### 29.6 Sacralisation opérationnelle

La gouvernance par couches doit être matérialisée dans :
- `docs/ai/MANIFEST_IA.yaml`
- `docs/ai/perimetre.yaml`
- `tools/ai/paths.txt`
- `tools/ai/perimetre_resolver.py`

Règles :
- `couche_ia` doit exister comme pack explicite ;
- `tools/ai/paths.txt` doit contenir le bootstrap minimal complet de `couche_ia` ;
- `docs/ai/perimetre.yaml` doit déclarer les couches canoniques et leurs packs associés ;
- `tools/ai/perimetre_resolver.py` doit pouvoir exposer cette gouvernance par couches comme API canonique.

### 29.7 Sacralisation de `couche_configuration_tests`

La couche canonique `couche_configuration_tests` a vocation à regrouper l’ensemble des fichiers racine, build, CI, profils Spring, configuration applicative, configuration de logs et ressources de tests permettant à l’IA de comprendre comment construire, configurer, tester et valider les couches déjà vertes du projet.

Sous-couches logiques admises :
- `couche_configuration_tests.racine_build_ci`
- `couche_configuration_tests.configuration`

#### `couche_configuration_tests.racine_build_ci`

Cette sous-couche comprend au minimum :
- `.gitattributes`
- `.gitignore`
- `pom.xml`
- `README.md`
- `scripts/test_couches_validees.sh`
- `.github/workflows/maven.yml`

#### `couche_configuration_tests.configuration`

Cette sous-couche comprend au minimum :
- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`
- `src/main/resources/application-jpa.properties`
- `src/main/resources/application-desktop.properties`
- `src/main/resources/application-web.properties`
- `src/main/resources/application-xml.properties`
- `src/main/resources/Log4j2.xml`
- `src/test/resources/application-test.properties`
- `src/test/resources/Log4j2-test.xml`
- `src/test/resources/data-test.sql`
- `src/test/resources/truncate-test.sql`

Règles :
- le script shell de lancement des tests validés ne doit pas être figé sur un nombre de couches ;
- son nom canonique devient `scripts/test_couches_validees.sh` ;
- ce script doit rester évolutif : il commence par jouer les tests des couches déjà validées, en particulier les services UC, puis il devra intégrer les tests des Controllers et des Vues lorsqu’ils seront créés et validés ;
- l’objectif est de disposer d’un build Maven jouant tous les tests déjà validés et passant verts ;
- la sous-couche `configuration` doit permettre à l’IA de relire les profils Spring réellement utilisés pour les audits de configuration/tests ;
- la sous-couche `configuration` doit couvrir les fichiers `src/main/resources` et `src/test/resources` nécessaires pour comprendre le comportement effectif de l’application et des tests.

La couche `couche_configuration_tests` doit donc permettre à l’IA de retrouver comme un tout cohérent :
- les règles racine du dépôt ;
- le build Maven ;
- le point d’entrée CI ;
- le script des couches validées ;
- les profils Spring applicatifs ;
- la configuration de logs applicative et de tests ;
- les ressources de test déjà sacralisées.

### 29.8 Sacralisation de `couche_metier`

La couche canonique `couche_metier` a vocation à regrouper l’ensemble des objets métier, interfaces métier, utilitaires métier et tests associés permettant à l’IA de comprendre le noyau fonctionnel pur Java du projet.

Sous-couches logiques admises :
- `couche_metier.objets`
- `couche_metier.utilitaires`
- `couche_metier.tests`

Cette couche comprend au minimum :

#### Objets métier et interfaces liées
- `src/main/java/levy/daniel/application/model/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/model/metier/IExportateurJTable.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/CloneContext.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/Produit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/ProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/SousTypeProduitI.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduit.java`
- `src/main/java/levy/daniel/application/model/metier/produittype/TypeProduitI.java`

#### Utilitaires métier
- `src/main/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtils.java`

#### Tests métier et tests d’utilitaires métier
- `src/test/java/levy/daniel/application/model/metier/produittype/CloneContextTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/MetierGlobalConformiteTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/ProduitTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/SousTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitSousTypeProduitIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/metier/produittype/TypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/utilitaires/metier/produittype/NormalizerUtilsTest.java`

#### Contrats locaux métier pivots
- `docs/contrats/metier/CoucheMetier.md`
- `docs/contrats/metier/TypeProduit.md`
- `docs/contrats/metier/SousTypeProduit.md`
- `docs/contrats/metier/Produit.md`
- `docs/contrats/metier/CloneContext.md`
- `docs/contrats/metier/NormalizerUtils.md`
- `docs/contrats/metier/ConvertisseursMetierOutputDTO.md`

Règles :
- `couche_metier` doit être auditée comme un tout cohérent, et non fichier par fichier isolé ;
- les objets métier racines `TypeProduit`, `SousTypeProduit` et `Produit` constituent les pivots documentaires de la couche ;
- les utilitaires métier liés, notamment `NormalizerUtils`, appartiennent à `couche_metier` ;
- les tests métier et les tests utilitaires métier appartiennent également à `couche_metier` ;
- les contrats locaux métier doivent permettre à l’IA de retrouver :
  - le rôle métier de l’objet,
  - ses invariants,
  - ses relations avec les autres objets métier,
  - les comportements observables,
  - les règles d’égalité, de comparaison, de mutabilité et de clonage lorsque pertinent,
  - les règles de thread-safety, snapshots et verrouillages multi-objets,
  - les règles exactes CSV/JTable,
  - les tests qui verrouillent ces comportements ;
- le contrat `CoucheMetier.md` est obligatoire pour toute action globale sur la couche métier ;
- `CloneContext.md` est obligatoire pour toute action touchant au clonage profond ;
- `NormalizerUtils.md` est obligatoire pour toute action touchant à la normalisation ;
- `ConvertisseursMetierOutputDTO.md` est obligatoire lorsque la préparation de réponse DTO dépend d'objets métier.

La couche `couche_metier` doit permettre à l’IA de retrouver comme un tout cohérent :
- les objets métier principaux ;
- les interfaces métier liées ;
- les utilitaires métier liés ;
- les tests unitaires métier ;
- les tests d’intégration métier ;
- les contrats locaux métier applicables ;
- les contrats auxiliaires nécessaires au clonage, à la normalisation et aux convertisseurs liés au métier.

### 29.9 Sacralisation de `couche_dto`

La couche canonique `couche_dto` a vocation à regrouper l’ensemble des DTO, des convertisseurs DTO et des tests associés permettant à l’IA de comprendre le contrat d’échange entre couches applicatives, en particulier entre CONTROLLER et SERVICE UC.

Sous-couches logiques admises :
- `couche_dto.objets`
- `couche_dto.convertisseurs`
- `couche_dto.tests`

Cette couche comprend au minimum :

#### DTO et convertisseurs DTO
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOSousTypeProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOTypeProduit.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/ProduitDTO.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/SousTypeProduitDTO.java`
- `src/main/java/levy/daniel/application/model/dto/produittype/TypeProduitDTO.java`

#### Tests DTO et convertisseurs DTO
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOSousTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ConvertisseurMetierToOutputDTOTypeProduitTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/ProduitDTOTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/SousTypeProduitDTOTest.java`
- `src/test/java/levy/daniel/application/model/dto/produittype/TypeProduitDTOTest.java`

#### Contrats locaux DTO pivots
- `docs/contrats/dto/TypeProduitDTO.md`
- `docs/contrats/dto/SousTypeProduitDTO.md`
- `docs/contrats/dto/ProduitDTO.md`

Règles :
- les DTO ne relèvent pas de `couche_persistance` ;
- les DTO servent de contrat d’échange entre couches applicatives, notamment CONTROLLER ↔ SERVICE UC ;
- les convertisseurs DTO appartiennent à `couche_dto` ;
- les tests DTO et convertisseurs DTO appartiennent à `couche_dto` ;
- `couche_dto` doit être auditée comme un tout cohérent, et non fichier par fichier isolé ;
- les pivots documentaires initiaux de la couche sont `TypeProduitDTO`, `SousTypeProduitDTO` et `ProduitDTO`.

Les contrats locaux DTO doivent permettre à l’IA de retrouver :
- le rôle du DTO ;
- la structure `InputDTO` / `OutputDTO` lorsque applicable ;
- les invariants de structure ;
- les contraintes de nullité ;
- les règles d’égalité / hashCode ;
- les règles sur les collections embarquées ;
- les interactions avec les convertisseurs ;
- les tests qui verrouillent ces comportements.

La couche `couche_dto` doit permettre à l’IA de retrouver comme un tout cohérent :
- les DTO principaux ;
- les convertisseurs DTO liés ;
- les tests unitaires DTO ;
- les tests unitaires des convertisseurs DTO ;
- les contrats locaux DTO applicables.

### 29.10 Sacralisation de `couche_persistance`

La couche canonique `couche_persistance` a vocation à regrouper l’ensemble des composants de persistance du domaine `produittype`, en particulier les interfaces transverses de persistance, les convertisseurs JPA ↔ métier, les entités JPA, les DAO JPA et les tests associés.

Sous-couche logique admise :
- `couche_persistance.jpa`

Cette couche comprend au minimum :

#### Interfaces transverses de persistance
- `src/main/java/levy/daniel/application/persistence/metier/IExportateurCsv.java`
- `src/main/java/levy/daniel/application/persistence/metier/IExportateurJTable.java`

#### Entités JPA / convertisseurs
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetier.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPA.java`

#### DAO JPA
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/ProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/SousTypeProduitDaoJPA.java`
- `src/main/java/levy/daniel/application/persistence/metier/produittype/dao/daosJPA/TypeProduitDaoJPA.java`

#### Tests persistance
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurJPAToMetierTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ConvertisseurMetierToJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/ProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/SousTypeProduitJPATest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPARattachementDetachementTest.java`
- `src/test/java/levy/daniel/application/persistence/metier/produittype/entities/entitiesJPA/TypeProduitJPATest.java`

#### Contrat local persistance pivot
- `docs/contrats/persistance/CouchePersistance.md`

Règles :
- `couche_persistance` porte les composants de persistance et jamais la logique UC ;
- la duplication éventuelle d’interfaces transverses entre `couche_metier` et `couche_persistance` est admise lorsqu’elle sert l’autonomie stricte des couches ;
- les convertisseurs JPA ↔ métier appartiennent à `couche_persistance` ;
- les DAO JPA appartiennent à `couche_persistance` ;
- les tests de persistance appartiennent à `couche_persistance` ;
- la couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La couche `couche_persistance` doit permettre à l’IA de retrouver comme un tout cohérent :
- les interfaces transverses de persistance ;
- les convertisseurs JPA ↔ métier ;
- les entités JPA ;
- les DAO JPA ;
- les tests de persistance ;
- le contrat local de couche applicable.

### 29.11 Sacralisation de `couche_services.gateway`

La sous-couche logique `couche_services.gateway` a vocation à regrouper l’ensemble des services techniques d’accès au stockage du domaine `produittype`, leurs exceptions Gateway, leur socle de pagination, leurs tests associés et leurs contrats locaux.

Cette sous-couche comprend au minimum :

#### PORTS Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/ProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/SousTypeProduitGatewayIService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/TypeProduitGatewayIService.java`

#### ADAPTERS Gateway JPA
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAService.java`

#### Exceptions Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliLibelleBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNonPersistent.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParamNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionAppliParentNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGateway.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsgateway/ExceptionTechniqueGatewayNonPersistent.java`

#### Pagination transverse Gateway
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/DirectionTri.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/RequetePage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/ResultatPage.java`
- `src/main/java/levy/daniel/application/model/services/produittype/pagination/TriSpec.java`

#### Tests Gateway et pagination
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/ProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/SousTypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/gateway/impl/TypeProduitGatewayJPAServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/DirectionTriTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/RequetePageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/ResultatPageTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/pagination/TriSpecTest.java`

#### Contrats locaux Gateway pivots
- `docs/contrats/gateway/CoucheServicesGateway.md`
- `docs/contrats/gateway/ProduitGatewayIService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayIService.md`
- `docs/contrats/gateway/TypeProduitGatewayIService.md`
- `docs/contrats/gateway/ProduitGatewayJPAService.md`
- `docs/contrats/gateway/SousTypeProduitGatewayJPAService.md`
- `docs/contrats/gateway/TypeProduitGatewayJPAService.md`

Règles :
- `couche_services.gateway` manipule les objets métier et jamais les DTO ;
- `couche_services.gateway` ne produit aucun message utilisateur et ne porte aucune logique UC ;
- les exceptions `exceptionsgateway` appartiennent à `couche_services.gateway` ;
- les exceptions `exceptionsservices` n’appartiennent pas à `couche_services.gateway` et relèvent du périmètre UC ;
- la pagination liée aux recherches paginées des Gateways appartient à `couche_services.gateway` ;
- les tests Mock et d’intégration Gateway appartiennent à la même sous-couche logique ;
- les contrats locaux JPA Gateway doivent exister pour `Produit`, `SousTypeProduit` et `TypeProduit` ;
- la sous-couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La sous-couche `couche_services.gateway` doit permettre à l’IA de retrouver comme un tout cohérent :
- les PORTS Gateway ;
- les ADAPTERS Gateway JPA ;
- les exceptions Gateway ;
- le socle de pagination transverse ;
- les tests Mock Gateway ;
- les tests d’intégration Gateway ;
- les contrats locaux Gateway applicables.

### 29.12 Sacralisation de `couche_services.uc`

La sous-couche logique `couche_services.uc` a vocation à regrouper l’ensemble des services métier de cas d’usage du domaine `produittype`, leurs exceptions de SERVICE UC, leurs tests associés et leurs contrats locaux.

Cette sous-couche comprend au minimum :

#### PORTS UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/cu/ProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/SousTypeProduitICuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/TypeProduitICuService.java`

#### ADAPTERS UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuService.java`
- `src/main/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuService.java`

#### Exceptions services UC métier
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionDoublon.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionNonPersistant.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreBlank.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionParametreNull.java`
- `src/main/java/levy/daniel/application/model/services/produittype/exceptionsservices/ExceptionStockageVide.java`

#### Tests services UC métier
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/ProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/SousTypeProduitCuServiceMockTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceIntegrationTest.java`
- `src/test/java/levy/daniel/application/model/services/produittype/cu/impl/TypeProduitCuServiceMockTest.java`

#### Contrats locaux UC pivots
- `docs/contrats/cu/CoucheServicesUC.md`
- `docs/contrats/cu/ProduitICuService.md`
- `docs/contrats/cu/SousTypeProduitICuService.md`
- `docs/contrats/cu/TypeProduitICuService.md`

Règles :
- `couche_services.uc` manipule les DTO applicatifs lorsque le contrat de service l’exige ;
- `couche_services.uc` est le point d’entrée dans la logique métier dialoguant directement avec le controller appelant et délègue les opérations techniques de stockage à `couche_services.gateway` ;
- `couche_services.uc` est responsable du message observable côté appelant via `getMessage()` ;
- les exceptions `exceptionsservices` appartiennent à `couche_services.uc` ;
- les exceptions `exceptionsgateway` n’appartiennent pas à `couche_services.uc` et relèvent du périmètre Gateway ;
- les tests Mock et d’intégration UC appartiennent à la même sous-couche logique ;
- la sous-couche doit être auditée comme un tout cohérent, et non fichier par fichier isolé.

La sous-couche `couche_services.uc` doit permettre à l’IA de retrouver comme un tout cohérent :
- les PORTS UC ;
- les ADAPTERS UC ;
- les exceptions de SERVICE UC ;
- les tests Mock UC ;
- les tests d’intégration UC ;
- les contrats locaux UC applicables.


### 29.13 Sacralisation de `couche_controllers`

La couche canonique `couche_controllers` regroupe les controllers Desktop/Web, les tests Controllers Mock, les tests MockMvc et les tests d'intégration Controllers.

#### Controllers et tests associés

Pour toute analyse, correction ou génération de test Controller utilisant Mockito, l'IA doit appliquer les règles transverses de `CONTRAT_IA.md`, notamment :
- `RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01` ;
- les règles de commentaires alignés avec le code immédiatement suivant ;
- les règles de non-réinvention à partir des tests déjà validés.

#### Contrat local Controllers pivot

Les règles locales Controllers doivent être stabilisées dans :
- `docs/contrats/controllers/CoucheControllers.md`.

Tant qu'une fenêtre active Controllers n'est pas installée, ce contrat local sert de relais minimal obligatoire pour les règles transverses déjà sacralisées et ne dispense jamais de relire les classes Controllers et les tests concernés avant toute conclusion.

## 30) RT-REFERENCE-GATEWAY-IMPL-VALIDEE-01 — Package Gateway impl validé comme référence autonome

### 30.1 Objet

Le package suivant est validé comme référence officielle pour les tests Gateway `produittype` :

`src/test/java/levy/daniel/application/model/services/produittype/gateway/impl`

Cette validation est acquise au SHA :

`dc478be7a24b69d41072fbcf210126f922526090`

Le package validé peut servir de référence pour écrire, réécrire, contrôler ou harmoniser les autres tests du projet.

### 30.2 Répartition validée

La référence validée contient exactement 539 tests :

| Fichier | Rôle | Nombre de tests |
|---|---:|---:|
| `ProduitGatewayJPAServiceIntegrationTest.java` | Produit intégration | 74 |
| `ProduitGatewayJPAServiceMockTest.java` | Produit Mockito | 114 |
| `SousTypeProduitGatewayJPAServiceIntegrationTest.java` | SousTypeProduit intégration | 75 |
| `SousTypeProduitGatewayJPAServiceMockTest.java` | SousTypeProduit Mockito | 123 |
| `TypeProduitGatewayJPAServiceIntegrationTest.java` | TypeProduit intégration | 51 |
| `TypeProduitGatewayJPAServiceMockTest.java` | TypeProduit Mockito | 102 |

La baseline consolidée, la fenêtre active et le bundle OFFLINE validé doivent rester cohérents avec cette référence tant qu’un nouveau SHA ou une nouvelle consolidation utilisateur ne l’invalide pas.

### 30.3 Rôle de référence

Lorsqu’un test Gateway doit être généré ou contrôlé, l’IA doit utiliser ce package validé comme référence de formalisme pour :

- l’ordre des blocs correspondant aux méthodes du PORT ;
- l’ordre des cas à l’intérieur de chaque bloc ;
- la distinction entre tests contractuels et tests didactiques non contractuels ;
- la Javadoc HTML des méthodes de test ;
- les commentaires didactiques internes `ARRANGE / ACT / ASSERT` ou équivalents ;
- les preuves Mockito d’interaction avec les collaborateurs ;
- les preuves d’intégration par comportement observable du stockage ;
- la structure des constantes, tags et display names ;
- les helpers de test réellement utiles ;
- la cohérence transversale entre `TypeProduit`, `SousTypeProduit` et `Produit`.

### 30.4 Acceptations lexicales stabilisées

Les points suivants sont explicitement acceptés et ne doivent plus être signalés comme défauts lorsqu’ils respectent leur condition d’explication :

- `PERSISTANT` et `PERSISTENT` sont tous deux tolérés : `persistant` est français, `persistent` est anglais ;
- `wipe` est toléré si l’expression explique clairement la conservation des données seedées, par exemple `conserve (ne wipe pas) les données seedées` ;
- `béton` est toléré si l’intitulé ou le commentaire explique ce que fait le test, par exemple `TESTS BETON (sanity / invariants)`.

L’IA ne doit pas perdre de temps sur ces détails lexicaux lorsqu’ils sont expliqués. Ils ne constituent pas des résidus actionnables, sauf si l’Utilisateur inverse explicitement cette décision ou si le terme est employé seul sans explication utile.

### 30.5 Workflow obligatoire après fichiers joints de tests Gateway

Lorsqu’un ou plusieurs fichiers de tests Gateway sont transmis dans le chat, l’IA doit automatiquement :

1. repartir du dernier `file_id` réel pour chaque fichier joint ;
2. relire `/mnt/data/<nom>` après l’upload comme support de lecture, sans le considérer comme identité normative ;
3. vérifier taille, SHA-256, nombre de LF, absence de CRLF si attendu, LF final et nombre de `@Test` ;
4. détecter les modifications par rapport à la baseline consolidée ;
5. apprendre les corrections utilisateur si les tests sont déclarés verts ;
6. consolider baseline et fenêtre active en écrasant les versions obsolètes ;
7. produire un rapport de consolidation ;
8. rejouer automatiquement la dernière commande ayant engendré des corrections.

Commande de rejeu actuellement obligatoire pour ce périmètre :

`contrôle strict de l'ensemble des tests situés dans le package src/test/java/levy/daniel/application/model/services/produittype/gateway/impl`

### 30.6 Génération autonome complète de tests Gateway

Pour réécrire entièrement une classe de test Gateway, l’IA doit appliquer la séquence stricte suivante :

1. relire le présent contrat IA ;
2. relire le contrat local `docs/contrats/gateway/CoucheServicesGateway.md` ;
3. relire le PORT concerné ;
4. relire l’ADAPTER Gateway réel ;
5. relire les dépendances utiles : objets métier, interfaces, entities JPA, DAO, pagination, exceptions, constantes, helpers et scripts SQL ;
6. relire les classes de test de référence du package validé ;
7. établir la matrice `méthode du PORT -> cas contractuels -> tests attendus -> preuve attendue` ;
8. choisir le type de preuve adapté : Mockito pour les branches techniques/interactions, intégration pour le comportement observable et le stockage ;
9. générer la classe complète sans snippet, sans ellipse, sans trou de méthode ;
10. vérifier le total de tests, l’ordre des blocs, les Javadocs, les commentaires internes, les imports, les constantes, les helpers et le LF final ;
11. livrer selon les règles de livraison applicables.

Cette règle est bloquante : si une dépendance utile n’est pas relue, l’IA doit déclarer la lecture incomplète et la compléter avant tout code ou verdict.

## 31) RT-TESTS-UC-PRODUIT-01 — Professionnalisation des tests SERVICES METIER UC

### 31.1 Objet

Les tests des SERVICES METIER UC `produittype` doivent être professionnalisés en s'inspirant du package Gateway validé, sans copier mécaniquement le comportement Gateway.

Le SERVICE METIER UC est le point d'entrée dans la logique métier dialoguant directement avec le controller appelant.
Cette formulation est la formulation contractuelle à utiliser.
La formule `orchestration applicative observable` est interdite car elle est vague et inutile.

### 31.2 Référence de formalisme

Le package Gateway validé :

`src/test/java/levy/daniel/application/model/services/produittype/gateway/impl`

sert de référence de formalisme pour les tests UC :

- Javadoc de tête ;
- constantes ;
- tags et display names ;
- blocs par méthode du PORT ;
- Javadoc HTML par méthode de test ;
- commentaires didactiques internes ;
- ordre des cas ;
- distinction entre tests contractuels et tests didactiques non contractuels ;
- vocabulaire stable ;
- espacement inter-tests de 3 lignes.

Cette référence ne remplace jamais le contrat du PORT UC.
Le comportement attendu d'un test UC se déduit toujours du contrat UC et de l'ADAPTER UC réel.

### 31.3 Ordre obligatoire de lecture pour un test UC

Avant toute analyse, correction, validation ou génération de code portant sur un test UC, l'IA doit relire strictement :

1. `docs/contrats/cu/CoucheServicesUC.md` ;
2. le contrat local du PORT UC ciblé ;
3. le PORT Java UC ciblé ;
4. l'ADAPTER UC réel ;
5. les DTO et convertisseurs DTO utiles ;
6. les exceptions `exceptionsservices` utiles ;
7. le PORT Gateway appelé ;
8. l'ADAPTER Gateway si l'intégration UC le nécessite ;
9. les objets métier utiles ;
10. les tests Gateway validés, uniquement comme référence de formalisme ;
11. le test UC ciblé.

Si une dépendance utile n'a pas été relue, l'IA doit déclarer la lecture incomplète et la relire avant toute conclusion.

### 31.4 Contrôle méthode par méthode

Les tests UC doivent être analysés méthode du PORT UC par méthode du PORT UC.

Pour chaque méthode, l'IA doit établir la matrice :

`cas contractuel UC -> test attendu -> preuve attendue -> verdict`

Le test est la spécification.
L'IA ne doit pas inventer de nouveaux critères à chaque passe.
Les critères stables sont ceux du contrat UC, de l'ADAPTER UC réel, du présent contrat IA et de `CoucheServicesUC.md`.

### 31.5 Tests Mock UC

Un test Mock UC prouve le comportement du SERVICE METIER UC côté controller appelant.

Il doit vérifier selon le cas :

- les DTO d'entrée et de sortie ;
- les validations d'entrée ;
- l'exception `exceptionsservices` exacte ;
- le message final observable via `getMessage()` ;
- la délégation exacte au Gateway ;
- l'absence d'interaction Gateway quand le contrat l'impose ;
- les conversions DTO / objet métier / DTO ;
- les scénarios Gateway `null`, vide, exception et nominal.

Un test Mock UC ne prouve pas directement le stockage et ne manipule pas le DAO.

Règle Mockito stricte : un test Mock UC ne doit configurer que les comportements réellement consommés par le scénario. Chaque `when(...)`, `doThrow(...)`, `doReturn(...)` ou stubbing équivalent doit correspondre à un appel exécuté avant le point d'arrêt du scénario. Si l'exception attendue est déclenchée avant la lecture d'un getter ou d'une méthode mockée, ce getter ou cette méthode ne doit pas être stubbé.

### 31.6 Tests d'intégration UC

Un test d'intégration UC prouve le comportement réel du SERVICE METIER UC avec ses collaborateurs réels utiles au scénario.

Il doit vérifier selon le cas :

- le message final observable via `getMessage()` ;
- les DTO retournés ;
- les exceptions `exceptionsservices` ;
- l'effet réel dans le stockage lorsque la méthode écrit ou supprime ;
- l'absence d'effet de bord pour les lectures ;
- la cohérence avec les règles parent/enfant et les contrats Gateway déjà validés.

En intégration UC, l'IA ne doit pas refaire inutilement toutes les clauses techniques déjà prouvées dans les tests Gateway Mock.

### 31.7 Fenêtre d'audit UC

La fenêtre `Fenêtre audit_uc_produit_complet` doit permettre de travailler sur les tests UC sans relire GitHub à chaque passe, tant que le SHA courant et le périmètre ne changent pas.

Elle est installée depuis la baseline consolidée et inclut au minimum :

- les contrats UC ;
- les PORTS UC ;
- les ADAPTERS UC ;
- les exceptions `exceptionsservices` ;
- les DTO et convertisseurs DTO ;
- les objets métier utiles ;
- les PORTS Gateway et, si nécessaire, les ADAPTERS Gateway ;
- les tests UC ;
- les tests Gateway validés comme référence de formalisme ;
- les ressources de test utiles.

Une fois activée, cette fenêtre devient la source de travail locale pour l'audit UC jusqu'à nouveau SHA, nouveau périmètre ou demande explicite de relecture GitHub.

## 32) RT-FORMALISME-TYPEPRODUIT-CU-MOCK-REFERENCE-02 — Référence autonome TypeProduitCuServiceMockTest

### 32.1 Objet

`TypeProduitCuServiceMockTest.java` corrigé au dernier SHA courant fourni par l'utilisateur est une référence complète de formalisme Mockito UC pour le SERVICE METIER UC `TypeProduitCuService`.

Cette règle complète les règles générales UC : elle ne remplace jamais le contrat du PORT UC, mais elle fixe le formalisme exact que l'IA doit savoir reproduire sans réinvention lorsqu'elle travaille sur cette classe ou sur une classe UC Mockito comparable.

### 32.2 Matrice de recodage obligatoire

Pour recoder seule `TypeProduitCuServiceMockTest.java`, l'IA doit reproduire la matrice complète suivante, dans l'ordre exact des blocs du PORT UC :

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

Total attendu : `101` tests.

L'IA ne doit ni retirer un cas, ni ajouter un nouveau cas, ni renommer un test déjà validé sans demande explicite de l'utilisateur.

### 32.3 Javadoc et bannière de tête

La classe doit conserver la bannière :

```java
/* ********************************************************************* */
/* ********************* TEST MOCKITO METIER UC ************************ */
/* ********************************************************************* */
```

La Javadoc de tête doit conserver explicitement les points suivants :

- tests unitaires JUnit 5 / Mockito du SERVICE METIER UC `TypeProduitCuService` ;
- objet métier `TypeProduit` ;
- SERVICE METIER UC comme point d'entrée dans la logique métier dialoguant directement avec le controller appelant ;
- respect du PORT `TypeProduitICuService` ;
- Gateway mocké ;
- validations locales des paramètres et des DTO ;
- messages utilisateur exposés par `getMessage()` ;
- conversions `InputDTO` -> objet métier -> `OutputDTO` ;
- délégations attendues vers `TypeProduitGatewayIService` ;
- absence de délégation Gateway lorsque le SERVICE METIER UC bloque localement l'opération ;
- propagation des exceptions techniques et rationalisation des messages observables ;
- cas d'erreur, cas alternatifs et cas nominaux ;
- reprise stricte des blocs déjà validés, sans réinvention inutile.

### 32.4 Tags, display names et annotations

La classe doit utiliser exclusivement les tags dédiés par bloc :

- `TAG_CREER` ;
- `TAG_RECHERCHER_TOUS` ;
- `TAG_RECHERCHER_TOUS_STRING` ;
- `TAG_RECHERCHER_TOUS_PAR_PAGE` ;
- `TAG_FIND_BY_LIBELLE` ;
- `TAG_FIND_BY_LIBELLE_RAPIDE` ;
- `TAG_FIND_BY_DTO` ;
- `TAG_FIND_BY_ID` ;
- `TAG_UPDATE` ;
- `TAG_DELETE` ;
- `TAG_COUNT` ;
- `TAG_GET_MESSAGE`.

La constante générique `TAG = "cu-mock"` ne doit pas être réintroduite.

Tous les `@DisplayName` doivent pointer vers une constante `DISPLAY_NAME_*`. Les `@DisplayName("...")` inline sont interdits dans cette classe.

L'ordre d'annotations corrigé et validé est :

```java
@Tag(...)
@DisplayName(...)
@Test
```

L'IA ne doit pas revenir à un ordre hétérogène d'annotations et ne doit pas déplacer ces annotations sans demande explicite.

### 32.5 Commentaires internes obligatoires

Les commentaires internes doivent reprendre les libellés validés :

- `ARRANGE` ;
- `Configuration du Mock` ;
- `ACT` ;
- `ACT - ASSERT` ;
- `ASSERT`.

Un commentaire doit documenter immédiatement la ou les lignes qui suivent. Si le commentaire annonce une donnée de scénario, la ligne suivante doit préparer cette donnée. Si le commentaire annonce le mock Gateway et le service UC, les lignes suivantes doivent créer le mock Gateway et le service UC.

Exemple correct :

```java
/* ARRANGE :
 * prépare un comptage Gateway cohérent indiquant
 * que plusieurs objets métier sont présents dans le stockage.
 */
final long comptageAttendu = 42L;
```

Exemple à reproduire tel quel lorsque le scénario nécessite un Gateway mocké :

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

L'IA ne doit pas réinventer ce bloc, le reformuler décorativement, ni l'éloigner des lignes qu'il documente.

### 32.6 Ordre interne d'un test

L'ordre interne validé n'est pas « mock Gateway d'abord ».

L'ordre correct est l'ordre humainement cohérent avec les commentaires :

1. préparer les données propres au scénario quand le commentaire les annonce ;
2. créer le Gateway mocké et le service UC avec le bloc standard validé ;
3. configurer le Mock ;
4. exécuter l'action ;
5. vérifier les assertions et les interactions.

Exemples validés :

- préparer `panneTechnique` avant le mock Gateway lorsque le commentaire annonce une panne technique ;
- préparer `comptageIncoherent` et `messageTechnique` avant le mock Gateway lorsque le commentaire annonce un comptage incohérent ;
- préparer `comptageAttendu` immédiatement après le commentaire qui annonce un comptage Gateway cohérent ;
- préparer DTO, libellés, objets métier, `RequetePage` ou `ResultatPage` immédiatement après le commentaire qui les annonce.

### 32.6.1 RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01 — Application locale UC d'une règle transverse

Cette section applique aux tests Mock UC la règle Mockito transverse définie en `15.2.3`.
Elle ne limite pas la règle aux UC : la même exigence s'applique aussi aux tests Mock Gateway, Controllers Mock, MockMvc utilisant Mockito, et à tout futur test Mockito du projet.

Dans les tests Mockito UC, chaque stubbing doit être consommé par le scénario réellement exécuté.

Sont concernés :

- `when(...)` ;
- `doThrow(...)` ;
- `doReturn(...)` ;
- tout comportement configuré sur un mock.

Règle obligatoire :

1. relire la méthode testée ;
2. déterminer l'ordre réel des appels ;
3. identifier le point exact où le scénario s'arrête ;
4. ne stubber que les appels exécutés avant ce point ;
5. supprimer tout stubbing non consommé.

Interdictions :

- ne jamais stubber un mock « pour le rendre complet » ;
- ne jamais ajouter un getter mocké par réflexe ;
- ne jamais conserver un stubbing préventif, décoratif ou non vérifié par le scénario ;
- ne jamais ignorer un risque de `UnnecessaryStubbingException`.

Exemple validé dans `SousTypeProduitCuServiceMockTest.java` :

```java
when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);
```

est suffisant lorsque l'échec de conversion `OutputDTO` est déclenché par l'accès au parent.

Dans ce scénario, il est interdit d'ajouter :

```java
when(sousTypeProduit.getSousTypeProduit()).thenReturn(OUTILLAGE);
```

si `getSousTypeProduit()` n'est pas consommé avant l'exception.

### 32.7 Non-réinvention

Avant toute génération ou correction dans `TypeProduitCuServiceMockTest.java`, l'IA doit relire :

1. le présent contrat IA ;
2. `docs/contrats/cu/CoucheServicesUC.md` ;
3. le PORT UC ciblé ;
4. l'ADAPTER UC réel ;
5. les méthodes déjà validées de `TypeProduitCuServiceMockTest.java`.

L'IA doit reprendre le formalisme validé dans la classe au lieu de créer un nouveau style local.

L'IA ne doit pas normaliser, déplacer, renommer, supprimer ou simplifier un élément déjà validé sans demande explicite de l'utilisateur.

## 33) RT-FORMALISME-SOUSTYPEPRODUIT-CU-MOCK-REFERENCE-01 — Référence autonome SousTypeProduitCuServiceMockTest

### 33.1 Objet

`SousTypeProduitCuServiceMockTest.java`, audité et stabilisé au SHA `a6118d58808e9805b99d899644226a6398eed9ec`, devient une référence complète de formalisme Mockito UC pour le SERVICE METIER UC `SousTypeProduitCuService`.

Cette règle complète les règles générales UC et la référence `TypeProduitCuServiceMockTest.java`. Elle ne remplace jamais le contrat du PORT UC `SousTypeProduitICuService`, mais elle fixe le formalisme exact que l'IA doit savoir reproduire sans réinvention lorsqu'elle travaille sur cette classe ou sur une classe UC Mockito comparable portant sur un objet métier intermédiaire.

`SousTypeProduit` doit être traité comme l'objet métier de la classe testée. Il possède un parent `TypeProduit`. L'IA ne doit pas écrire `sous-type` pour désigner l'objet métier dans les commentaires ou Javadocs de cette classe.

### 33.2 Matrice de recodage obligatoire

Pour recoder seule `SousTypeProduitCuServiceMockTest.java`, l'IA doit reproduire la matrice complète suivante, dans l'ordre exact des blocs du PORT UC :

| Bloc | Nombre de tests |
|---|---:|
| `creer` | 16 |
| `rechercherTous` | 7 |
| `rechercherTousString` | 8 |
| `rechercherTousParPage` | 8 |
| `findByLibelle` | 9 |
| `findByLibelleRapide` | 9 |
| `findAllByParent` | 13 |
| `findByDTO` | 15 |
| `findById` | 7 |
| `update` | 20 |
| `delete` | 16 |
| `count` | 5 |
| `getMessage` | 5 |

Total attendu : `138` tests.

L'IA ne doit ni retirer un cas, ni ajouter un nouveau cas, ni renommer un test déjà validé sans demande explicite de l'utilisateur.

### 33.3 Ordre exact des tests validés

L'ordre validé des tests est contractuel.

#### Bloc `creer`

1. `testCreerNull` ;
2. `testCreerBlank` ;
3. `testCreerParentBlank` ;
4. `testCreerControleTechniqueKoAvecMessage` ;
5. `testCreerControleTechniqueKoSansMessage` ;
6. `testCreerDoublon` ;
7. `testCreerParentTechniqueKoAvecMessage` ;
8. `testCreerParentTechniqueKoSansMessage` ;
9. `testCreerParentAbsent` ;
10. `testCreerParentNonPersistant` ;
11. `testCreerCreationTechniqueKoAvecMessage` ;
12. `testCreerCreationTechniqueKoSansMessage` ;
13. `testCreerGatewayRetourneNull` ;
14. `testCreerConversionTechniqueKoAvecMessage` ;
15. `testCreerConversionTechniqueKoSansMessage` ;
16. `testCreerNominal`.

#### Bloc `rechercherTous`

1. `testRechercherTousGatewayRetourNull` ;
2. `testRechercherTousGatewayKOAvecMessage` ;
3. `testRechercherTousGatewayKOSansMessage` ;
4. `testRechercherTousConversionOutputDTOKOAvecMessage` ;
5. `testRechercherTousConversionOutputDTOKOSansMessage` ;
6. `testRechercherTousVideApresFiltrage` ;
7. `testRechercherTousNominal`.

#### Bloc `rechercherTousString`

1. `testRechercherTousStringGatewayRetourNull` ;
2. `testRechercherTousStringGatewayKOAvecMessage` ;
3. `testRechercherTousStringGatewayKOSansMessage` ;
4. `testRechercherTousStringConversionStringKOAvecMessage` ;
5. `testRechercherTousStringConversionStringKOSansMessage` ;
6. `testRechercherTousStringVideApresFiltrage` ;
7. `testRechercherTousStringVideApresLibellesBlank` ;
8. `testRechercherTousStringNominal`.

#### Bloc `rechercherTousParPage`

1. `testRechercherTousParPageNull` ;
2. `testRechercherTousParPageGatewayKOAvecMessage` ;
3. `testRechercherTousParPageGatewayKOSansMessage` ;
4. `testRechercherTousParPageGatewayRetourNull` ;
5. `testRechercherTousParPageConversionOutputDTOKOAvecMessage` ;
6. `testRechercherTousParPageConversionOutputDTOKOSansMessage` ;
7. `testRechercherTousParPageVideApresFiltrage` ;
8. `testRechercherTousParPageNominal`.

#### Bloc `findByLibelle`

1. `testFindByLibelleNull` ;
2. `testFindByLibelleBlank` ;
3. `testFindByLibelleGatewayRetourNull` ;
4. `testFindByLibelleGatewayKOAvecMessage` ;
5. `testFindByLibelleGatewayKOSansMessage` ;
6. `testFindByLibelleConversionOutputDTOKOAvecMessage` ;
7. `testFindByLibelleConversionOutputDTOKOSansMessage` ;
8. `testFindByLibelleIntrouvable` ;
9. `testFindByLibelleNominal`.

#### Bloc `findByLibelleRapide`

1. `testFindByLibelleRapideNull` ;
2. `testFindByLibelleRapideBlank` ;
3. `testFindByLibelleRapideGatewayKOAvecMessage` ;
4. `testFindByLibelleRapideGatewayKOSansMessage` ;
5. `testFindByLibelleRapideGatewayRetourNull` ;
6. `testFindByLibelleRapideConversionOutputDTOKOAvecMessage` ;
7. `testFindByLibelleRapideConversionOutputDTOKOSansMessage` ;
8. `testFindByLibelleRapideVideApresFiltrage` ;
9. `testFindByLibelleRapideNominal`.

#### Bloc `findAllByParent`

1. `testFindAllByParentNull` ;
2. `testFindAllByParentParentBlank` ;
3. `testFindAllByParentParentGatewayKOAvecMessage` ;
4. `testFindAllByParentParentGatewayKOSansMessage` ;
5. `testFindAllByParentParentAbsent` ;
6. `testFindAllByParentParentNonPersistant` ;
7. `testFindAllByParentEnfantsGatewayKOAvecMessage` ;
8. `testFindAllByParentEnfantsGatewayKOSansMessage` ;
9. `testFindAllByParentGatewayRetourNull` ;
10. `testFindAllByParentConversionOutputDTOKOAvecMessage` ;
11. `testFindAllByParentConversionOutputDTOKOSansMessage` ;
12. `testFindAllByParentVideApresFiltrage` ;
13. `testFindAllByParentNominal`.

#### Bloc `findByDTO`

1. `testFindByDTONull` ;
2. `testFindByDTOParentBlank` ;
3. `testFindByDTOErreurTechniqueRechercheParentAvecMessage` ;
4. `testFindByDTOErreurTechniqueRechercheParentSansMessage` ;
5. `testFindByDTOParentAbsent` ;
6. `testFindByDTOParentNonPersistant` ;
7. `testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage` ;
8. `testFindByDTOErreurTechniqueRechercheEnfantsSansMessage` ;
9. `testFindByDTOGatewayRetourNull` ;
10. `testFindByDTOVide` ;
11. `testFindByDTOVideApresFiltrage` ;
12. `testFindByDTOIntrouvableDansListe` ;
13. `testFindByDTOConversionOutputDTOKOAvecMessage` ;
14. `testFindByDTOConversionOutputDTOKOSansMessage` ;
15. `testFindByDTONominal`.

#### Bloc `findById`

1. `testFindByIdNull` ;
2. `testFindByIdIntrouvable` ;
3. `testFindByIdErreurTechniqueAvecMessage` ;
4. `testFindByIdErreurTechniqueSansMessage` ;
5. `testFindByIdConversionOutputDTOKOAvecMessage` ;
6. `testFindByIdConversionOutputDTOKOSansMessage` ;
7. `testFindByIdNominal`.

#### Bloc `update`

1. `testUpdateNull` ;
2. `testUpdateLibelleNull` ;
3. `testUpdateBlank` ;
4. `testUpdateParentBlank` ;
5. `testUpdateRechercheParentTechniqueKoAvecMessage` ;
6. `testUpdateRechercheParentTechniqueKoSansMessage` ;
7. `testUpdateParentAbsent` ;
8. `testUpdateParentNonPersistant` ;
9. `testUpdateRechercheEnfantsTechniqueKoAvecMessage` ;
10. `testUpdateRechercheEnfantsTechniqueKoSansMessage` ;
11. `testUpdateStockageNullPendantReidentification` ;
12. `testUpdateIntrouvable` ;
13. `testUpdateNonPersistant` ;
14. `testUpdateModificationTechniqueKoAvecMessage` ;
15. `testUpdateModificationTechniqueKoSansMessage` ;
16. `testUpdateModificationRetourNull` ;
17. `testUpdateModificationRetourNonPersistant` ;
18. `testUpdateConversionOutputDTOKOAvecMessage` ;
19. `testUpdateConversionOutputDTOKOSansMessage` ;
20. `testUpdateNominal`.

#### Bloc `delete`

1. `testDeleteNull` ;
2. `testDeleteLibelleNull` ;
3. `testDeleteBlank` ;
4. `testDeleteParentBlank` ;
5. `testDeleteRechercheParentTechniqueKoAvecMessage` ;
6. `testDeleteRechercheParentTechniqueKoSansMessage` ;
7. `testDeleteParentAbsent` ;
8. `testDeleteParentNonPersistant` ;
9. `testDeleteRechercheEnfantsTechniqueKoAvecMessage` ;
10. `testDeleteRechercheEnfantsTechniqueKoSansMessage` ;
11. `testDeleteStockageNullPendantReidentification` ;
12. `testDeleteIntrouvable` ;
13. `testDeleteNonPersistant` ;
14. `testDeleteDestructionKOAvecMessage` ;
15. `testDeleteDestructionKOSansMessage` ;
16. `testDeleteNominal`.

#### Bloc `count`

1. `testCountGatewayKOAvecMessage` ;
2. `testCountGatewayKOSansMessage` ;
3. `testCountRetourNegatif` ;
4. `testCountZero` ;
5. `testCountNominal`.

#### Bloc `getMessage`

1. `testGetMessageInitialNull` ;
2. `testGetMessageApresErreurLocale` ;
3. `testGetMessageApresCountZero` ;
4. `testGetMessageApresCountNominal` ;
5. `testGetMessageDernierMessageGagne`.

### 33.4 Tags, display names et annotations

La classe doit utiliser exclusivement les tags dédiés par bloc :

- `TAG_CREER` ;
- `TAG_RECHERCHER_TOUS` ;
- `TAG_RECHERCHER_TOUS_STRING` ;
- `TAG_RECHERCHER_TOUS_PAR_PAGE` ;
- `TAG_FIND_BY_LIBELLE` ;
- `TAG_FIND_BY_LIBELLE_RAPIDE` ;
- `TAG_FIND_ALL_BY_PARENT` ;
- `TAG_FIND_BY_DTO` ;
- `TAG_FIND_BY_ID` ;
- `TAG_UPDATE` ;
- `TAG_DELETE` ;
- `TAG_COUNT` ;
- `TAG_GET_MESSAGE`.

La constante générique `TAG = "cu-mock"` ne doit pas être réintroduite.

Tous les `@DisplayName` doivent pointer vers une constante `DISPLAY_NAME_*`. Les `@DisplayName("...")` inline sont interdits dans cette classe.

L'ordre d'annotations corrigé et validé est :

```java
@Tag(...)
@DisplayName(...)
@Test
```

L'IA ne doit pas revenir à un ordre hétérogène d'annotations et ne doit pas déplacer ces annotations sans demande explicite.

### 33.5 Javadoc de tête et vocabulaire obligatoire

La Javadoc de tête doit conserver explicitement les points suivants :

- tests unitaires JUnit 5 / Mockito du SERVICE METIER UC `SousTypeProduitCuService` ;
- objet métier `SousTypeProduit` ;
- parent `TypeProduit` ;
- SERVICE METIER UC comme point d'entrée dans la logique métier dialoguant directement avec le controller appelant ;
- respect du PORT `SousTypeProduitICuService` ;
- Gateway `SousTypeProduitGatewayIService` mocké ;
- Gateway parent `TypeProduitGatewayIService` mocké ;
- validations locales des paramètres et des DTO ;
- messages utilisateur exposés par `getMessage()` ;
- conversions `InputDTO` -> objet métier -> `OutputDTO` ;
- recherche préalable du parent lorsque le scénario l'exige ;
- recherche des objets métier enfants lorsque le scénario l'exige ;
- délégations attendues vers les Gateways ;
- absence de délégation Gateway lorsque le SERVICE METIER UC bloque localement l'opération ;
- propagation des exceptions techniques et rationalisation des messages observables ;
- cas d'erreur, cas alternatifs et cas nominaux ;
- reprise stricte des blocs déjà validés, sans réinvention inutile.

Vocabulaire validé à employer :

- `objet métier` pour `SousTypeProduit` ;
- `parent` pour `TypeProduit` ;
- `Gateway` ou `Gateway mocké` ;
- `stockage` ;
- `SERVICE METIER UC`.

Vocabulaire interdit dans les Javadocs et commentaires de cette classe, sauf citation explicite d'une ancienne erreur à corriger :

- `sous-type` ;
- `branche locale` ;
- `non exploitable` ;
- `objet exploitable` ;
- `réponse exploitable` ;
- `scénario sécurisé` ;
- toute formule vague qui ne dit pas concrètement ce que le test vérifie.

### 33.6 Commentaires internes obligatoires

Les commentaires internes doivent rester simples, concrets et proches des lignes qu'ils documentent.

Libellés validés à conserver selon le scénario :

- `ARRANGE` ;
- `Configuration du Mock` ;
- `ACT` ;
- `ACT - ASSERT` ;
- `ASSERT`.

Le bloc standard de création des deux Gateways mockés et du service UC doit rester cohérent avec la classe validée :

```java
/* ARRANGE :
 * Mocke les services Gateway et les passe
 * à un service UC instancié dans le test.
 */
final SousTypeProduitGatewayIService gateway 
	= mock(SousTypeProduitGatewayIService.class);
final TypeProduitGatewayIService typeProduitGateway 
	= mock(TypeProduitGatewayIService.class);
final SousTypeProduitCuService service 
	= new SousTypeProduitCuService(gateway, typeProduitGateway);
```

L'IA ne doit pas remplacer ce commentaire par une formulation décorative, abstraite ou éloignée des lignes qu'elle documente.

### 33.7 Spécificité de l'objet métier intermédiaire

`SousTypeProduitCuServiceMockTest.java` ne doit pas être généré comme une simple copie de `TypeProduitCuServiceMockTest.java`.

La classe teste un objet métier intermédiaire :

- l'objet métier testé est `SousTypeProduit` ;
- le parent est `TypeProduit` ;
- certains scénarios interrogent d'abord le Gateway parent ;
- certains scénarios interrogent ensuite le Gateway de l'objet métier ;
- les scénarios nominaux doivent prouver la bonne conversion du parent et de l'objet métier ;
- les scénarios d'échec doivent distinguer les erreurs de recherche parent, les erreurs de recherche enfants, les retours `null`, les retours vides, les retours non persistants, les introuvables et les conversions finales KO.

L'IA doit donc relire la méthode réelle testée avant de configurer les mocks. Elle ne doit pas stubber un Gateway parent ou enfant par réflexe si l'appel n'est pas consommé dans le scénario réellement exécuté.

### 33.8 Stubbing Mockito strictement consommé

La règle `RT-MOCKITO-STUBBING-STRICTEMENT-CONSOMME-01` est bloquante dans cette classe.

Pour chaque test, l'IA doit déterminer :

1. l'ordre réel des validations locales ;
2. l'ordre réel des recherches Gateway ;
3. le point exact où le scénario s'arrête ;
4. les appels Gateway réellement consommés ;
5. les conversions DTO / objet métier / DTO réellement exécutées.

Tout stubbing non consommé est interdit, même s'il semble rendre le scénario plus complet.

### 33.9 Contrôle automatique minimal avant livraison

Avant toute livraison complète ou partielle portant sur `SousTypeProduitCuServiceMockTest.java`, l'IA doit contrôler au minimum :

- `138` méthodes de test `public void test*` ;
- `138` annotations `@Test` ;
- `138` annotations `@Tag(...)` ;
- `138` annotations `@DisplayName(...)` ;
- `13` constantes `TAG_*` dédiées aux blocs ;
- absence de constante générique `TAG = "cu-mock"` ;
- absence de `@Tag(TAG)` ;
- absence de `@DisplayName("...")` inline ;
- absence de doublon de méthode de test ;
- présence d'un EOF LF ;
- absence de CRLF ;
- absence des formulations interdites listées en `33.5`.

Si l'un de ces contrôles échoue, l'IA ne doit pas livrer le fichier comme stabilisé.

### 33.10 Ordre obligatoire de lecture avant correction

Avant toute analyse, correction, validation ou génération de code dans `SousTypeProduitCuServiceMockTest.java`, l'IA doit relire :

1. le présent contrat IA ;
2. `docs/contrats/cu/CoucheServicesUC.md` ;
3. `docs/contrats/cu/SousTypeProduitICuService.md` ;
4. le PORT Java `SousTypeProduitICuService.java` ;
5. l'ADAPTER UC réel `SousTypeProduitCuService.java` ;
6. les DTO et convertisseurs DTO utiles ;
7. les objets métier `SousTypeProduit` et `TypeProduit` ;
8. les PORTS Gateway `SousTypeProduitGatewayIService` et `TypeProduitGatewayIService` ;
9. `TypeProduitCuServiceMockTest.java` uniquement comme référence de formalisme comparable ;
10. `SousTypeProduitCuServiceMockTest.java` comme classe cible.

Si une dépendance utile n'a pas été relue, l'IA doit déclarer la lecture incomplète et la compléter avant tout verdict ou code.

## 34) RT-NON-REINVENTION-REFERENCES-VALIDEES-01 — Réutilisation obligatoire des points validés

### 34.1 Objectif

Cette règle sacralise un principe général valable pour tout le projet Java, pour les couches actuelles comme pour les couches futures.

L’IA ne doit jamais repartir de zéro lorsqu’une référence validée existe.

Une référence validée peut être une classe, une méthode, une Javadoc, un commentaire de bloc, une structure de test, une convention de nommage, un ordre de méthodes, une matrice de cas, un helper, une constante, un formalisme d’assertions ou tout autre point déjà stabilisé par les tests verts et par la validation utilisateur.

L’objectif n’est pas de figer une liste fermée de chaînes d’homogénéité. L’objectif est d’obliger l’IA à détecter elle-même les références homologues validées et les formalismes réutilisables, puis à les transposer intelligemment partout où cela est possible.

### 34.2 Principe général

Avant toute analyse, audit, validation, correction ou génération de code portant sur une classe ou une méthode Java, l’IA doit appliquer la séquence suivante :

1. détecter les classes homologues validées ;
2. relire les références amont, voisines ou parentes utiles ;
3. repérer les formalismes réutilisables ;
4. identifier uniquement ce qui change réellement ;
5. transposer intelligemment ;
6. conserver l’homogénéité du projet.

Cette séquence est obligatoire même si l’Utilisateur n’a pas explicitement listé les classes de référence. Une IA compétente doit observer la structure du projet et détecter les analogies stables : objets métier, DTO, entities JPA, Gateways, services UC, Controllers futurs, tests Mock, tests d’intégration, contrats, Javadocs, commentaires et conventions transverses.

### 34.3 Références homologues validées

Une classe ou méthode est homologue lorsqu’elle joue un rôle comparable dans une chaîne métier, technique ou documentaire du projet.

L’homologie peut provenir notamment :

- d’une hiérarchie métier ou fonctionnelle ;
- d’un objet métier parent, enfant ou intermédiaire ;
- d’un DTO équivalent ;
- d’une entity JPA équivalente ;
- d’un repository, DAO, Gateway ou service équivalent ;
- d’un PORT ou ADAPTER comparable ;
- d’un test Mock comparable ;
- d’un test d’intégration comparable ;
- d’un contrat local de couche comparable ;
- d’une Javadoc ou d’un commentaire validé dans une classe voisine ;
- d’un ordre de blocs ou de méthodes déjà stabilisé.

L’IA ne doit pas exiger que ces chaînes soient toutes nommées dans le contrat. Si une cohérence apparaît dans le projet, l’IA doit la détecter et l’exploiter.

### 34.4 Copie mécanique prioritaire puis adaptation minimale

Quand une référence homologue validée existe et que le scénario est le même, transposer intelligemment signifie d'abord copier mécaniquement la structure validée.

L'IA doit recopier la Javadoc, les commentaires, l'ordre des cas, les messages, les assertions, les vérifications et le formalisme autant que possible. Elle ne doit adapter que ce qui change réellement : noms métier, DTO, Gateway, types de retour, getters, constantes réellement différentes ou dépendances objectivement différentes.

L’IA doit relire le contrat, la classe cible, la méthode exacte et les dépendances utiles, puis adapter uniquement ce qui change réellement selon le cas :

- objet métier ;
- parent ou enfant métier ;
- grand-parent éventuel ;
- DTO ;
- entity JPA ;
- repository, DAO, Gateway ou service ;
- PORT concerné ;
- ADAPTER réel ;
- messages contractuels ;
- exceptions ;
- comportement réel observable ;
- cas supplémentaires propres à un objet métier intermédiaire ou à une couche particulière.

Tout le reste doit être conservé autant que possible : structure, ordre, Javadocs, commentaires, noms, constantes, tags, display names, assertions, vérifications Mockito, stratégie de preuve, vocabulaire métier et niveau de détail.

Toute divergence par rapport à une référence homologue validée doit être justifiée par une différence réelle lue dans le PORT, l'ADAPTER, la méthode cible ou une dépendance utile. Une préférence stylistique de l'IA, une reformulation décorative ou une volonté de "faire mieux" ne constitue jamais une justification.

### 34.5 Interdictions absolues

L’IA ne doit jamais :

- traiter une classe comme une page blanche lorsqu’une référence validée existe ;
- inventer un nouveau formalisme alors qu’un formalisme réutilisable a été validé ;
- remplacer une Javadoc validée par une Javadoc générique ;
- remplacer un commentaire didactique validé par un commentaire abstrait ou décoratif ;
- créer un gabarit artificiel lorsque la référence fournit déjà une méthode concrète transposable ;
- créer un helper, un `Scenario`, un mini-framework ou une abstraction si les références validées instancient explicitement les objets dans les tests ;
- simplifier un code ou un test validé au motif qu’une autre solution serait équivalente ;
- déplacer, renommer ou supprimer un élément validé sans demande explicite ;
- perdre l’ordre des blocs, des méthodes ou des cas validés ;
- oublier de comparer la méthode générée avec la méthode homologue relue ;
- attendre que l’Utilisateur énumère toutes les chaînes d’homogénéité évidentes du projet.

### 34.6 Contrôle obligatoire avant livraison

Avant toute livraison de code ou de contrat fondée sur une référence homologue, l’IA doit contrôler explicitement :

1. la référence validée relue ;
2. la méthode ou section homologue relue ;
3. les différences réelles entre référence et cible ;
4. les éléments repris à l’identique ;
5. les éléments adaptés ;
6. les éléments volontairement non repris, avec justification contractuelle ;
7. l’absence de réinvention stylistique ;
8. l’homogénéité finale avec les références validées.

Si la livraison porte sur une classe complète, ce contrôle doit être effectué sur la classe finale réellement livrée, et non sur une intention de génération.

### 34.7 Règle d’arrêt en cas de doute

Si l’IA a un doute, si elle ne sait pas quelle référence est prioritaire, si deux formalismes validés semblent concurrents, si une adaptation n’est pas évidente, ou si le contrat ne permet pas de trancher, elle ne doit jamais improviser.

Elle doit répondre explicitement :

```text
RÈGLE MANQUANTE OU AMBIGUË.
Je ne peux pas trancher sans risquer d'improviser.
Point à arbitrer : ...
Références relues : ...
Proposition éventuelle : ...
```

L’Utilisateur arbitrera alors la règle manquante ou ambiguë, puis cette règle sera injectée dans la couche IA si elle doit devenir durable.

### 34.8 Portée transverse

Cette règle s’applique à toutes les couches du projet : métier, DTO, persistance, Gateway, UC, Controllers futurs, vues futures, contrats, tests, Javadocs, commentaires, helpers et constantes.

Elle complète les règles locales de référence déjà présentes dans le contrat. Ces règles locales restent utiles lorsqu’elles décrivent une classe validée précise, une matrice de tests exacte ou un formalisme stabilisé. Mais elles ne dispensent jamais l’IA de détecter d’autres références homologues validées dans le reste du projet.


### 34.9 Application obligatoire à `ProduitCuServiceMockTest.java` — Javadoc de tête homogène avec les références UC

La correction utilisateur relative à `ProduitCuServiceMockTest.java` est prioritaire et durable.

Avant toute nouvelle livraison, correction ou recodage de `ProduitCuServiceMockTest.java`, l’IA doit comparer explicitement la Javadoc de tête finale avec les Javadocs de tête relues de :

1. `TypeProduitCuServiceMockTest.java` ;
2. `SousTypeProduitCuServiceMockTest.java`.

La Javadoc de tête de `ProduitCuServiceMockTest.java` ne doit pas être une synthèse condensée ni une Javadoc générique. Elle doit transposer intelligemment la structure validée de `SousTypeProduitCuServiceMockTest.java`, avec adaptation stricte au rôle métier de `Produit`.

Elle doit notamment conserver :

- plusieurs `<div>` distincts lorsque la référence validée les utilise ;
- un paragraphe d’identification de la classe et de l’objet métier ;
- un paragraphe sur le respect du PORT UC ;
- une liste `<ul><li>` des contrôles ;
- un paragraphe indiquant que les Gateways sont mockés et que les tests ne valident pas les adaptateurs de stockage ;
- un paragraphe spécifique au parent métier `SousTypeProduit`, lui-même rattaché à `TypeProduit` ;
- un paragraphe de formalisme attendu ;
- le vocabulaire concret `objet métier`, `parent`, `SERVICE METIER UC`, `Gateway mocké` et `stockage` ;
- le niveau de détail validé dans les références homologues.

La phrase déclarative selon laquelle une classe « reprend le formalisme stabilisé » ne suffit jamais. L’homogénéité doit être prouvée par le contenu réel de la Javadoc livrée.

L’IA ne doit pas livrer `ProduitCuServiceMockTest.java` comme conforme si la Javadoc de tête finale n’a pas été comparée point par point avec la Javadoc homologue relue.

### 34.10 Copie obligatoire des méthodes homologues validées

Lorsqu'une méthode de `Produit`, `SousTypeProduit` ou `TypeProduit` possède une méthode homologue déjà validée, l'IA doit traiter cette méthode validée comme référence de départ.

La règle par défaut est : **recopier ce qui peut être recopié**.

L'IA doit reprendre mécaniquement :

- la structure de la méthode ;
- la Javadoc ;
- les commentaires internes ;
- les scénarios de tests ;
- l'ordre des tests ;
- les constantes `DISPLAY_NAME` lorsque le formalisme impose leur correction ;
- les messages attendus ;
- les assertions ;
- les vérifications Mockito ;
- les cas d'erreur, alternatifs et nominaux.

L'IA ne peut modifier que :

- le nom de l'objet métier ;
- le nom du parent ;
- les DTO ;
- les Gateways ;
- les getters réellement différents ;
- les types de retour réellement différents ;
- les constantes réellement différentes ;
- les cas supplémentaires imposés par le PORT ou l'ADAPTER relu.

L'IA ne doit jamais créer deux traitements différents pour une même méthode homologue sans preuve de différence métier ou technique relue. Si `SousTypeProduit.findByLibelle(...)` fournit le comportement validé et que `Produit.findByLibelle(...)` doit être harmonisé, l'IA doit copier le comportement, pas inventer une variante.

---

## 35) RT-AUTONOMIE-METIER-RECODAGE-01 — Autonomie réelle sur la couche métier validée

### 35.1 Objectif

Lorsqu'une couche est déclarée validée et que l'utilisateur demande que l'IA puisse la recoder en autonomie, les contrats IA de cette couche ne doivent pas seulement décrire l'intention générale. Ils doivent fournir une fiche d'autonomie suffisante pour recoder quasiment à l'identique le formalisme général, les Javadocs, les commentaires de bloc, les constantes, les méthodes, les algorithmes réellement appliqués et les tests de verrouillage.

Pour la couche métier, cette règle est obligatoire et prioritaire.

### 35.2 Règle de non-improvisation renforcée

Pour une classe métier validée, l'IA n'a pas le droit de remplacer un détail validé par une solution seulement équivalente si ce détail est observable dans le code ou les tests.

Détails à considérer comme contractuels :

- nom des constantes publiques et privées ;
- valeur littérale des constantes ;
- ordre des attributs ;
- ordre exact des méthodes ;
- visibilité des méthodes ;
- variantes de clonage ;
- méthodes internes d'identité ;
- méthodes de traitement des mauvaises instances ;
- stratégie de verrouillage ;
- snapshots sous verrou ;
- recalcul de validité ;
- chaînes CSV et JTable ;
- retours `null`, chaîne vide, `invalide` ou texte `null` ;
- commentaires techniques nécessaires à la compréhension des verrous, snapshots, relations bidirectionnelles et clonages profonds.

### 35.3 Fiche d'autonomie obligatoire

Chaque contrat local d'une classe métier validée doit contenir ou compléter les informations suivantes :

1. inventaire structurel complet : constantes, attributs, méthodes publiques, méthodes protégées, méthodes privées ;
2. ordre d'apparition des méthodes à conserver ;
3. constantes littérales exactes, y compris les chaînes historiques ;
4. règles algorithmiques méthode par méthode pour les méthodes sensibles ;
5. règles de formalisme Javadoc/commentaires à conserver ;
6. inventaire des tests validés qui verrouillent la classe ;
7. interdictions concrètes empêchant l'IA de simplifier le code validé.

Si une fiche locale est incomplète, l'IA doit relire le code validé correspondant et compléter le contrat IA avant de prétendre pouvoir recoder la classe en autonomie.

### 35.4 Hiérarchie lors du recodage autonome

Même après enrichissement des contrats, la hiérarchie reste :

1. baseline consolidée ;
2. GitHub Raw au SHA courant ;
3. bundle OFFLINE validé ;
4. contrats IA enrichis.

Les contrats IA rendent l'IA autonome sur la compréhension et la reproduction du formalisme. Ils ne l'autorisent jamais à ignorer le SHA courant ni à coder depuis un souvenir.

### 35.5 Critère de réussite

La couche IA est considérée comme autonome pour une classe métier uniquement si une IA peut produire un code qui :

- conserve le comportement validé par les tests ;
- conserve le style général de la classe ;
- conserve l'ordre fonctionnel et les méthodes internes ;
- conserve les Javadocs/commentaires critiques ;
- ne remplace pas les algorithmes thread-safe validés par des raccourcis ;
- ne casse aucune relation bidirectionnelle ;
- ne modifie pas les chaînes CSV/JTable historiques.
---

## 36) RT-COUCHE-IA-01 et RT-WORKFLOW-AUDIT-TEST-01 — Gouvernance des règles détectées et workflow d'audit JUnit

### 36.1 RT-COUCHE-IA-01 (VALIDÉ) — Signalement, validation et intégration des règles détectées

Lorsqu'une règle nouvelle ou une précision de workflow doit s'imposer dans le projet, l'IA NE DOIT PAS la sacraliser silencieusement.

Workflow obligatoire :

1. l'IA signale explicitement à l'Utilisateur le besoin de sacraliser la règle détectée ;
2. l'IA explique pourquoi cette règle est nécessaire ;
3. l'IA propose le ou les fichiers de couche IA à modifier ;
4. l'Utilisateur valide ou corrige la règle ;
5. si l'Utilisateur demande explicitement `coder`, l'IA livre les fichiers fragiles de couche IA complets et individuellement, avec chemin STS exact et lien de téléchargement opérationnel ;
6. l'Utilisateur intègre dans STS et soumet à l'IA les fichiers corrigés ou un nouveau SHA ;
7. l'IA relit les fichiers corrigés avec la technique de lecture sacralisée ;
8. si tout est OK, l'IA mémorise la règle, consolide la mémoire long terme, le contexte actif/top-mind, la baseline et la fenêtre active si elle existe ;
9. toute règle antérieure ou concurrente portant sur le même sujet est immédiatement neutralisée.

Interdictions :

- ne jamais détecter une règle importante sans la signaler à l'Utilisateur ;
- ne jamais intégrer une règle dans la couche IA sans validation utilisateur ;
- ne jamais livrer un fichier fragile de couche IA sous forme de ZIP global nominal ;
- ne jamais remplacer la livraison individuelle complète par un extrait ou un patch à reconstituer.

### 36.2 RT-REGLE-CANDIDATE-CODER-01 (VALIDÉ) — Conservation des règles validées en attente du prochain `coder`

Quand l'Utilisateur demande de mémoriser une règle validée, une correction de workflow ou une règle détectée à intégrer plus tard dans la couche IA, l'IA doit la conserver comme règle candidate à intégrer lors de la prochaine demande explicite `coder`.

Tant que `coder` n'est pas demandé :

- l'IA ne modifie pas les fichiers fragiles ;
- l'IA ne livre pas de fichier corrigé ;
- l'IA garde la règle en mémoire utile ;
- l'IA l'applique immédiatement dans le dialogue si elle concerne le comportement courant ;
- l'IA la proposera pour intégration dans la couche IA au prochain codage.

Au prochain `coder`, l'IA doit relire les règles candidates mémorisées et intégrer toutes celles qui sont compatibles avec la demande, sans en oublier.

### 36.3 RT-WORKFLOW-AUDIT-TEST-01 (VALIDÉ) — Workflow complet d'échanges lors de l'audit d'un bloc de test JUnit

L'audit d'un bloc de test JUnit est un workflow itératif, pas une génération isolée de méthodes.

#### 36.3.1 Préambule obligatoire

Avant toute opération, l'IA relit obligatoirement `CONTRAT_IA.md`.

Cette étape est contractuelle et non négociable.

#### 36.3.2 Demande initiale d'audit d'un test JUnit

Lorsque l'Utilisateur demande l'audit d'un test JUnit ou d'une classe de test JUnit, l'IA ne code pas sauf si le message contient explicitement `coder`.

#### 36.3.3 Détermination du périmètre utile

Si ce n'est pas déjà fait, l'IA détermine le périmètre utile de l'audit :

- contrat de couche ;
- contrat local / PORT ;
- classe de service, adapter ou composant testé ;
- classe de test cible ;
- dépendances utiles ;
- DTO, convertisseurs, exceptions ou helpers utiles ;
- références validées ;
- méthodes déjà validées dans la même classe de test.

#### 36.3.4 Contrôle de la baseline

L'IA contrôle que tous les fichiers du périmètre sont présents, lisibles, corrects et à jour dans la baseline consolidée.

Si tout est OK, l'IA installe ou active une fenêtre de travail.

Une fois la fenêtre active, l'IA peut travailler directement depuis cette fenêtre sans relire GitHub au SHA à chaque bloc, sauf :

- nouveau SHA ;
- changement de périmètre ;
- demande explicite de l'Utilisateur ;
- incident de lecture ;
- fenêtre suspecte, périmée ou incomplète.

#### 36.3.5 Demande d'audit d'un bloc précis

Quand l'Utilisateur demande l'audit complet d'un bloc, l'IA relit :

- `CONTRAT_IA.md` ;
- la fenêtre active ou la baseline consolidée ;
- le périmètre utile du bloc ;
- le PORT ou contrat concerné ;
- la méthode réelle testée ;
- la classe de test cible ;
- les méthodes déjà validées dans cette classe ;
- les références validées dans le périmètre.

L'IA doit regarder ce qui a déjà été fait dans la classe pour les blocs corrigés ou validés. Elle ne doit pas inventer un nouveau formalisme.

#### 36.3.6 Rapport d'audit sans code

Si l'Utilisateur n'a pas écrit `coder`, l'IA rend un rapport d'audit sans code.

Le rapport doit contenir :

- preuve de lecture ;
- bloc contrôlé ;
- liste des tests présents ;
- ordre actuel des tests ;
- comportements couverts ;
- manques détectés ;
- tests à ajouter, déplacer ou renommer ;
- problèmes de Javadoc ou commentaires ;
- proposition d'ordre corrigé ;
- verdict : conforme, incomplet ou non validable.

#### 36.3.7 Correction de l'analyse par l'Utilisateur

Si l'Utilisateur corrige l'analyse de l'IA, l'IA doit prendre cette correction comme prioritaire, reconstruire son analyse, mémoriser la règle de formalisme durable et la conserver comme règle candidate à intégrer dans la couche IA au prochain `coder`.

#### 36.3.8 Livraison seulement après `coder`

Si l'Utilisateur écrit `coder`, l'IA livre selon le format attendu par la classe cible.

Pour un bloc de test JUnit, la livraison doit être structurée en blocs autonomes séparés :

1. bloc autonome des constantes TAG à ajouter ;
2. bloc autonome des constantes DISPLAY_NAME à ajouter ;
3. bloc autonome des constantes DN uniquement si elles sont réellement nécessaires ;
4. bloc autonome complet des tests.

Règles obligatoires :

- ne jamais livrer seulement des méthodes isolées si le workflow établi exige un bloc autonome ;
- ne jamais noyer les constantes TAG dans les DISPLAY_NAME ;
- ne jamais noyer les constantes DN dans les DISPLAY_NAME ;
- réutiliser les constantes existantes dès qu'elles suffisent ;
- ne jamais créer de constantes DN inutiles ou décoratives ;
- respecter le formalisme déjà validé dans la classe.

Le bloc complet des tests doit contenir :

- le commentaire de bloc ;
- les Javadocs utiles ;
- les commentaires ARRANGE / ACT / ASSERT conformes à la classe ;
- les méthodes de test ordonnées ;
- les noms de tests cohérents ;
- les `@Tag` conformes ;
- les `@DisplayName` conformes.

#### 36.3.9 Intégration STS et retour utilisateur

L'Utilisateur intègre la livraison dans STS et exécute les tests.

L'Utilisateur peut ensuite répondre avec tests verts, tests KO ou corrections, le plus souvent sous forme de fichier joint au chat afin d'éviter un commit/push et un changement de SHA à chaque tour.

#### 36.3.10 Relecture du dernier fichier joint réel

Lorsque l'Utilisateur soumet le fichier corrigé, l'IA doit relire le dernier fichier joint réel avec la technique sacralisée :

- repartir du dernier `file_id` réel ;
- relire le fichier local correspondant dans `/mnt/data` ;
- calculer taille, lignes, SHA-256, EOF LF et CRLF ;
- invalider tout ancien état local ;
- ne jamais conclure depuis une ancienne copie.

#### 36.3.11 Consolidation et recontrôle automatique

Si les corrections ont bien été apportées et si les tests sont verts, l'IA consolide immédiatement :

- baseline consolidée ;
- fenêtre active si elle existe ;
- mémoire utile du formalisme.

Cette consolidation peut être en avance par rapport au dépôt GitHub.

Après consolidation, l'IA doit automatiquement recontrôler la totalité du bloc jusqu'à pouvoir le déclarer conforme aux critères de qualité des tests.

Le recontrôle porte notamment sur :

- complétude du bloc ;
- ordre des tests ;
- conformité au PORT / contrat ;
- conformité à la méthode réelle testée ;
- messages attendus ;
- interactions Mockito ;
- preuves d'intégration si test d'intégration ;
- Javadocs ;
- commentaires ;
- constantes TAG / DISPLAY_NAME / DN ;
- formalisme validé de la classe.

#### 36.3.12 Passage au bloc suivant

Si tout est OK, l'Utilisateur passe au bloc suivant et le cycle reprend à la demande d'audit du bloc précis.

### 36.4 RT-WORKFLOW-BLOC-TEST-AUTONOME-01 (VALIDÉ) — Format de livraison des blocs de tests Java

Pour toute livraison d'un bloc de tests Java demandé en audit/correction, un bloc autonome doit inclure, lorsque c'est le formalisme établi de la classe ou de la livraison attendue :

1. les constantes TAG nécessaires au bloc ;
2. les constantes DISPLAY_NAME nécessaires au bloc ;
3. les constantes DN / données nominales uniquement si elles sont réellement nécessaires ;
4. la Javadoc et les commentaires de bloc couvrant l'ensemble du bloc demandé ;
5. le bloc complet directement copiable dans STS.

L'IA ne doit jamais livrer seulement les méthodes isolées si le workflow établi exige un bloc autonome avec constantes et commentaires de bloc.

Les constantes doivent être livrées par familles séparées : TAG, DISPLAY_NAME, puis DN si nécessaire. Une constante DN ne doit jamais être créée par réflexe si une constante existante exprime déjà clairement le scénario.

### 36.5 RT-JAVADOC-TEST-SIMPLE-CONCRETE-01 (VALIDÉ) — Style des Javadocs de tests

Dans les Javadocs et commentaires de tests, l'IA doit dire simplement ce que le test vérifie.

Interdictions, sauf si l'Utilisateur demande explicitement de conserver une citation ancienne pour audit :

- « vise la branche locale » ;
- « recherche exacte » ;
- « libellé exploitable » ;
- « exploitable » pour qualifier un paramètre, un libellé, une réponse ou un résultat ;
- « non exploitable » ;
- « objet exploitable » ;
- « réponse exploitable » ;
- « résultat partiel incohérent » ;
- « scénario sécurisé » ;
- toute formulation vague, abstraite ou prétentieuse qui ne décrit pas directement le fait testé.

L'IA doit remplacer ces formulations par une description concrète du fait testé : paramètre `null`, chaîne blank, Gateway retourne `null`, liste vide, exception propagée, message positionné, conversion KO, objet absent du stockage, parent absent, parent non persistant.

Formulations attendues :

- DTO transmis null ;
- parent avec libellé blank ;
- parent absent du stockage ;
- parent sans identifiant persistant ;
- Gateway retourne null ;
- liste vide ;
- liste contenant uniquement des éléments null ;
- conversion OutputDTO KO avec message ;
- conversion OutputDTO KO sans message.

### 36.6 RT-UC-CONVERSION-FINALE-TRY-CATCH-01 (VALIDÉ) — Tests Mock UC des conversions finales protégées

Pour un bloc de test Mock UC, dès que la méthode de service contient une phase de conversion finale protégée par `try/catch` et produisant un message utilisateur rationalisé, les tests du bloc doivent envisager explicitement :

- échec de conversion avec message ;
- échec de conversion sans message.

La règle s'applique sauf justification contractuelle explicite d'impossibilité ou de non-pertinence.

Le formalisme doit s'inspirer des cas validés de `TypeProduitCuServiceMockTest.java`, sans ajouter de stubbing décoratif ou non consommé.

## 37) RT-PRODUIT-CU-MOCK-METHODE-PAR-METHODE-01 — Règles issues de l'audit méthode par méthode de `ProduitCuServiceMockTest`

### 37.1 Objet

Cette section sacralise les règles détectées pendant la reprise méthode par méthode de `ProduitCuServiceMockTest.java`.

Elle complète les règles générales UC, les références `TypeProduitCuServiceMockTest.java` et `SousTypeProduitCuServiceMockTest.java`, ainsi que la règle de non-réinvention des références validées.

Elle est prioritaire pour toute correction, génération, validation ou consolidation portant sur une méthode de `ProduitCuServiceMockTest.java` ou sur une classe homologue de tests Mock UC.

Objectif : empêcher l'IA de produire une classe fonctionnellement plausible mais formellement non conforme, de générer des Javadocs gabarits, de renommer des formulations validées, de modifier des constantes hors périmètre ou de corriger davantage que ce qui a été demandé.

### 37.2 Travail strictement méthode par méthode

Pour `ProduitCuServiceMockTest.java`, l'IA doit travailler méthode par méthode lorsque l'Utilisateur le demande.

Règles obligatoires :

1. attendre l'instruction explicite de l'Utilisateur sur la méthode ou le bloc cible ;
2. relire `CONTRAT_IA.md` et monter les règles applicables en mémoire de travail ;
3. relire la méthode cible dans `ProduitCuServiceMockTest.java` ;
4. relire la méthode homologue validée dans `SousTypeProduitCuServiceMockTest.java` ;
5. relire `TypeProduitCuServiceMockTest.java` si la méthode ou le formalisme nécessite une seconde référence ;
6. relire le PORT UC et l'ADAPTER UC réel si le comportement testé ou l'ordre des appels n'est pas directement déductible de la méthode homologue ;
7. livrer uniquement la méthode demandée ou le bloc explicitement demandé ;
8. ne jamais régénérer la classe complète sans demande explicite de fichier complet.

Si une dépendance utile ou une méthode homologue n'a pas été relue, l'IA doit déclarer la lecture incomplète et s'arrêter avant toute livraison.

### 37.3 Faire strictement ce qui est demandé, pas plus

Lorsque l'Utilisateur demande de coder ou corriger une méthode précise, l'IA doit respecter strictement le périmètre demandé.

Interdictions absolues :

- ne jamais modifier une constante ;
- ne jamais modifier un identifiant Java ;
- ne jamais modifier un `DISPLAY_NAME` ;
- ne jamais modifier un tag ;
- ne jamais modifier une signature ;
- ne jamais modifier un helper ;
- ne jamais modifier une section hors méthode ;
- ne jamais modifier une formulation validée extérieure à la méthode cible ;
- ne jamais corriger un autre test non demandé ;
- ne jamais transformer une correction de méthode en refonte de fichier.

Exceptions admises uniquement :

1. demande explicite de l'Utilisateur ;
2. nécessité technique bloquante démontrée avant livraison et acceptée dans le périmètre de correction.

Règle courte : l'IA doit faire exactement ce qui est demandé, correctement, complètement et contrôlé, mais pas plus.

### 37.4 Ne jamais confondre adaptation et renommage

Lorsqu'une Javadoc, un commentaire, un libellé de scénario ou une formulation métier provient d'une référence validée, l'IA doit la reprendre strictement.

L'adaptation autorisée se limite aux éléments qui changent objectivement :

- classe cible ;
- PORT UC ;
- constantes contractuelles ;
- objet métier ;
- parent ;
- Gateway concerné ;
- types Java ;
- appels réels ;
- messages contractuels réellement différents.

Tout le reste doit être conservé.

Exemple normatif : si la référence validée écrit `contrôle de doublon KO avec message`, l'IA ne doit pas remplacer cette formulation par `contrôle technique KO avec message`, sauf demande explicite de l'Utilisateur.

Une formulation plus vague, même si elle semble cohérente avec un `DISPLAY_NAME` existant, ne doit pas remplacer une formulation validée plus claire.

### 37.5 Vocabulaire générique obligatoire dans les Javadocs transposables

Dans les Javadocs de tests UC Mockito transposables entre `TypeProduit`, `SousTypeProduit` et `Produit`, l'IA doit privilégier les rôles métier génériques clairs lorsque le contexte de la classe rend les classes concrètes évidentes.

Pour `ProduitCuServiceMockTest.java` :

- écrire `objet métier` pour `Produit` ;
- écrire `parent` pour `SousTypeProduit` ;
- écrire `Gateway objet métier` pour `ProduitGatewayIService` ;
- écrire `Gateway parent` pour `SousTypeProduitGatewayIService`.

L'IA ne doit pas alourdir inutilement chaque Javadoc en répétant `Produit` et `SousTypeProduit` lorsque le contexte de la classe suffit.

Cette règle ne supprime pas les liens Javadoc utiles dans la Javadoc de tête ou dans les zones où une ambiguïté réelle existe. Elle impose seulement de privilégier les rôles métier dans les Javadocs de méthodes lorsque la classe cible est évidente.

### 37.6 Javadocs de méthodes : garanties exactes, jamais gabarits

Une Javadoc de méthode de test doit énumérer les garanties exactes et observables du test.

Forme attendue :

```java
/**
 * <div>
 * <p>garantit que methode(scénario) :</p>
 * <ul>
 * <li>garantie observable 1 ;</li>
 * <li>garantie observable 2 ;</li>
 * <li>garantie observable 3.</li>
 * </ul>
 * </div>
 *
 * @throws Exception
 */
```

La Javadoc doit citer, selon le scénario :

- le retour attendu ;
- l'exception exacte jetée ;
- le message utilisateur exact du PORT UC ;
- l'appel Gateway attendu ;
- l'absence d'appel Gateway ;
- la propagation exacte d'une exception ;
- la rationalisation exacte du message observable ;
- l'absence de création, modification, suppression ou recherche lorsque le scénario l'impose.

Interdictions absolues dans les Javadocs de méthodes :

- `exécute le scénario` ;
- `contrôle le retour, l'exception ou l'état observable attendu` ;
- `lorsque le scénario en produit un` ;
- `vérifie les interactions attendues ou interdites` sans dire lesquelles ;
- toute phrase gabarit valable pour n'importe quel test ;
- toute formule qui mélange exception et message dans un slogan au lieu d'énumérer les garanties.

La présence de `<div>`, `<ul>` et `<li>` ne suffit pas. Le contenu des puces doit être spécifique au scénario testé.

### 37.7 Commentaires internes : spécifiques au scénario et alignés avec le code

Les commentaires internes doivent décrire exactement le bloc de code immédiatement situé dessous.

Pour un scénario de validation locale, l'`ARRANGE` doit expliciter :

- la donnée invalide préparée ;
- le point de blocage attendu dans le SERVICE METIER UC ;
- l'absence de délégation aux Gateways lorsque c'est le comportement testé.

Exemple de forme attendue :

```java
/* ARRANGE :
 * prépare un DTO dont le libellé de l'objet métier est blank.
 *
 * Ce cas doit être bloqué par le SERVICE METIER UC
 * avant toute délégation aux Gateways.
 */
```

Le bloc standard de création des Gateways et du service doit rester explicite dans les méthodes corrigées méthode par méthode lorsque la référence homologue instancie explicitement les collaborateurs :

```java
/* ARRANGE :
 * Mocke les services Gateway et les passe
 * à un service UC instancié dans le test.
 */
```

Les commentaires `ACT`, `ACT - ASSERT` et `ASSERT` doivent citer l'appel exact testé et les garanties exactes du bloc.

Interdictions :

- commentaires décoratifs ;
- commentaires génériques non liés au code immédiatement dessous ;
- commentaire annonçant un scénario que le code ne prépare pas ;
- commentaire annonçant un Gateway ou une interaction qui n'est pas réellement utilisée ;
- remplacement d'un commentaire validé par une formulation abstraite.

### 37.8 Instanciation explicite dans les tests corrigés méthode par méthode

Lorsqu'une méthode homologue validée instancie explicitement ses Gateways mockés et le SERVICE METIER UC, la méthode corrigée dans `ProduitCuServiceMockTest.java` doit faire de même.

L'IA ne doit pas masquer la structure du test derrière un helper local lorsque le formalisme validé montre explicitement :

- le Gateway objet métier mocké ;
- le Gateway parent mocké ;
- le SERVICE METIER UC instancié dans le test.

Interdictions :

- créer ou conserver un helper `Scenario` si la référence validée ne l'utilise pas ;
- cacher l'instanciation des collaborateurs derrière un mini-framework local ;
- remplacer la preuve visible par une abstraction qui rend le test moins lisible.

Une exception n'est possible que si l'Utilisateur demande explicitement un refactoring de helpers ou si la classe validée de référence utilise déjà ce helper comme formalisme stabilisé.

### 37.9 Cas de validation locale avant Gateway : preuve obligatoire

Pour tout scénario où le SERVICE METIER UC bloque localement avant délégation Gateway, le test doit prouver explicitement :

1. la donnée invalide préparée ;
2. le retour ou l'exception exacte ;
3. le message utilisateur exact du PORT UC ;
4. le message observable via `service.getMessage()` lorsque le service le positionne ;
5. l'absence d'interaction avec le Gateway objet métier ;
6. l'absence d'interaction avec le Gateway parent.

Exemples concernés :

- `creer(null)` ;
- `creer(libellé blank)` ;
- `creer(libellé parent blank)` ;
- paramètres `null` ;
- paramètres `blank` ;
- DTO invalides ;
- parent localement invalide.

La preuve Mockito doit utiliser `verifyNoInteractions(...)` sur les Gateways concernés lorsque le scénario impose l'absence totale de délégation.

### 37.10 Méthode homologue de référence : reprise stricte et comparaison obligatoire

Avant de livrer une méthode corrigée de `ProduitCuServiceMockTest.java`, l'IA doit comparer la méthode cible avec la méthode homologue validée.

La comparaison doit porter au minimum sur :

- le titre de la Javadoc ;
- les puces de garanties ;
- les termes métier ;
- les commentaires `ARRANGE` ;
- les commentaires `Configuration du Mock` ;
- les commentaires `ACT`, `ACT - ASSERT` et `ASSERT` ;
- l'ordre des données préparées ;
- l'ordre des stubbings ;
- les assertions ;
- les interactions Mockito ;
- le trait final `} // __________________________________________________________________` ;
- les lignes vides entre tests lorsque le bloc livré inclut plusieurs tests.

L'IA doit identifier uniquement ce qui change objectivement entre la référence et `ProduitCuServiceMockTest.java`, puis conserver tout le reste.

Si la comparaison n'a pas été faite, la livraison est invalide.

### 37.11 Effet normatif des corrections utilisateur et des tests verts

Lorsqu'une correction utilisateur est relue et que l'Utilisateur indique `test vert ok`, la méthode corrigée devient une référence locale pour les scénarios analogues.

L'IA doit mémoriser :

- la Javadoc validée ;
- les commentaires internes validés ;
- l'ordre du test ;
- les assertions ;
- les interactions Mockito ;
- le vocabulaire choisi par l'Utilisateur ;
- les différences avec les anciennes livraisons rejetées.

Les anciennes versions de la même méthode doivent être considérées obsolètes, notamment si elles contenaient :

- un helper `Scenario` non validé ;
- une Javadoc gabarit ;
- des commentaires génériques ;
- une formulation renommée sans demande ;
- une modification hors périmètre.

### 37.12 Règles locales validées par les premières méthodes corrigées

Les corrections validées de `testCreerNull()`, `testCreerBlank()` et `testCreerParentBlank()` fixent les règles locales suivantes pour les scénarios analogues :

- `testCreerNull()` : Javadoc avec garanties exactes, retour `null`, message `MESSAGE_CREER_NULL`, aucune interaction Gateway ;
- `testCreerBlank()` : exception `ExceptionParametreBlank`, message `MESSAGE_CREER_NOM_BLANK`, blocage local avant délégation ;
- `testCreerParentBlank()` : contrôle local du libellé parent, exception `IllegalStateException`, message `MESSAGE_PAS_PARENT`, aucune interaction Gateway.

Ces méthodes servent de référence immédiate pour :

- validations locales ;
- paramètres invalides ;
- libellés blank ;
- parents localement invalides ;
- absence de délégation Gateway ;
- distinction entre message d'exception et message observable `getMessage()`.

### 37.13 Application à `testCreerControleTechniqueKoAvecMessage()`

Pour `testCreerControleTechniqueKoAvecMessage()`, la méthode homologue validée impose de conserver la formulation de Javadoc :

```text
contrôle de doublon KO avec message
```

Cette formulation décrit mieux le scénario qu'une formulation vague du type `contrôle technique KO avec message`.

La correction de la méthode doit donc porter sur la Javadoc et les commentaires internes de la méthode, en conservant la formulation validée lorsque le scénario est identique.

En revanche, si l'Utilisateur n'a pas demandé de modifier la constante `DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_AVEC_MESSAGE`, l'IA n'a pas le droit de la modifier.

La Javadoc d'une méthode et la constante `DISPLAY_NAME_*` sont deux zones différentes. Une correction demandée sur la méthode ne donne pas automatiquement le droit de modifier la constante déclarée en haut de classe.

### 37.14 Respect strict du périmètre de livraison

Avant toute livraison, l'IA doit déterminer le périmètre exact demandé :

- méthode unique ;
- bloc de méthodes ;
- constantes ;
- helpers ;
- fichier complet.

L'IA doit ensuite livrer uniquement ce périmètre.

Exemples :

- si l'Utilisateur demande `coder testCreerParentBlank()`, livrer uniquement la méthode complète `testCreerParentBlank()` ;
- si l'Utilisateur demande `coder testCreerControleTechniqueKoAvecMessage()`, ne pas modifier la constante `DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_AVEC_MESSAGE` ;
- si l'Utilisateur demande une règle à intégrer dans `CONTRAT_IA.md`, livrer le fichier complet fragile `CONTRAT_IA.md`, pas un patch ;
- si l'Utilisateur demande une méthode Java, livrer la méthode dans le chat, pas un lien de téléchargement.

### 37.15 Auto-contrôle obligatoire avant livraison méthode par méthode

Avant de livrer une méthode Java corrigée, l'IA doit contrôler explicitement :

1. la méthode cible est complète ;
2. la signature et les annotations sont inchangées sauf demande explicite ;
3. aucune constante hors méthode n'a été modifiée ;
4. aucun `DISPLAY_NAME` n'a été modifié ;
5. aucun tag n'a été modifié ;
6. aucun helper n'a été modifié ;
7. aucune formulation validée n'a été renommée ;
8. la Javadoc énumère les garanties exactes ;
9. aucun gabarit interdit n'est présent ;
10. les commentaires décrivent le code immédiatement dessous ;
11. les stubbings Mockito sont consommés par le scénario ;
12. les interactions attendues et interdites sont prouvées ;
13. l'espacement inter-tests de 3 lignes est respecté si plusieurs tests sont livrés ;
14. le bloc final est directement copiable dans STS.

Si un seul point n'est pas contrôlé, l'IA doit suspendre la livraison.

### 37.16 Réponse obligatoire en cas de tentation de surcorrection

Si l'IA estime qu'une amélioration hors périmètre serait utile, elle ne doit pas l'appliquer.

Elle doit seulement signaler, séparément du code livré :

```text
Amélioration hors périmètre détectée : <description>.
Non appliquée car non demandée.
```

L'IA ne doit jamais transformer cette amélioration en correction livrée.

### 37.17 Synthèse courte opposable

Pour `ProduitCuServiceMockTest.java`, les règles opérationnelles sont :

```text
Référence validée d'abord.
Méthode homologue relue ligne à ligne.
Pas de gabarit.
Pas de renommage.
Pas de modification hors périmètre.
Pas de constante modifiée sans demande.
Rôles génériques objet métier / parent.
Garanties exactes en Javadoc.
Commentaires spécifiques au scénario.
Stubbings strictement consommés.
Preuve Mockito explicite.
Auto-contrôle avant livraison.
```

Une livraison qui viole l'un de ces points doit être rejetée avant d'être remise à l'Utilisateur.

## 38) RT-HOMOGENEITE-FINDBYLIBELLE-PRODUIT-01 — Correction issue du bloc `findByLibelle` Produit

### 38.1 Origine de la règle

Cette règle sacralise les corrections utilisateur apportées après la livraison incorrecte du bloc `findByLibelle` de `ProduitCuServiceMockTest.java`, du PORT `ProduitICuService.java` et de l'ADAPTER `ProduitCuService.java`.

L'erreur de l'IA a été de reformuler, d'inventer une Javadoc et de proposer un comportement différent de la référence `SousTypeProduit`, alors que le besoin réel était l'homogénéité.

### 38.2 Principe opposable

Pour une méthode homologue déjà stabilisée, l'IA ne doit pas "améliorer", reformuler ou redessiner le contrat.

Elle doit :

1. relire le PORT homologue ;
2. relire l'ADAPTER homologue ;
3. relire les tests homologues ;
4. copier le comportement validé ;
5. remplacer uniquement les noms, DTO, Gateways, getters et types réellement différents ;
6. conserver les messages contractuels homologues lorsque le comportement attendu est le même.

### 38.3 Règle spécifique `findByLibelle`

Pour `findByLibelle(...)` dans la chaîne `TypeProduit` / `SousTypeProduit` / `Produit`, la référence `SousTypeProduit` impose le comportement à recopier lorsqu'aucune différence métier relue ne justifie autre chose.

Pour `Produit.findByLibelle(...)`, le comportement harmonisé attendu est :

- paramètre `null` ou blank : retourner une liste vide non `null` et positionner `MESSAGE_PARAM_BLANK` ;
- Gateway retourne `null` : lever `ExceptionStockageVide` et positionner `MESSAGE_STOCKAGE_NULL` ;
- Gateway KO avec message : propager l'exception et positionner `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message technique` ;
- Gateway KO sans message : propager l'exception et positionner `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
- conversion OutputDTO KO avec message : propager l'exception et positionner `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + message technique` ;
- conversion OutputDTO KO sans message : propager l'exception et positionner `KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE` ;
- objet absent du stockage : retourner une liste vide non `null` et positionner `MESSAGE_OBJ_INTROUVABLE + libellé` ;
- nominal : retourner les `OutputDTO` triés et dédoublonnés, puis positionner `MESSAGE_SUCCES_RECHERCHE`.

L'IA ne doit pas livrer `MESSAGE_RECHERCHE_VIDE` ni `MESSAGE_RECHERCHE_OK` pour `Produit.findByLibelle(...)` si la référence homologue validée attend `MESSAGE_OBJ_INTROUVABLE` et `MESSAGE_SUCCES_RECHERCHE`.

L'IA ne doit pas livrer `return null` pour le cas blank si la référence homologue validée retourne une nouvelle liste vide.

### 38.4 Effet sur le périmètre de codage

Quand l'Utilisateur demande d'harmoniser un comportement de méthode homologue, l'IA doit identifier tout le périmètre réellement nécessaire :

- PORT ;
- ADAPTER ;
- tests ;
- constantes `DISPLAY_NAME` si les messages attendus changent ;
- commentaires et Javadocs du bloc concerné.

L'IA ne doit pas limiter la correction aux tests si le PORT ou l'ADAPTER exprime encore l'ancien contrat.

Inversement, l'IA ne doit pas modifier d'autres blocs non concernés.

### 38.5 Vocabulaire banni dans les nouvelles livraisons

Dans les nouvelles livraisons PatrimoineSSP, l'IA ne doit plus utiliser les formulations suivantes pour présenter, commenter ou justifier le code :

- « recherche exacte » ;
- « libellé exploitable » ;
- « exploitable » pour qualifier un libellé, une réponse ou un résultat ;
- « résultat partiel incohérent » ;
- toute phrase décorative qui présente une évidence comme une garantie technique.

L'IA doit écrire le fait contrôlé directement : `libellé`, `paramètre blank`, `liste vide`, `Gateway retourne null`, `message positionné`, `exception propagée`, `objet absent du stockage`, `succès`.

### 38.6 Auto-contrôle obligatoire avant livraison d'une méthode homologue

Avant toute livraison portant sur une méthode homologue, l'IA doit produire ou effectuer l'auto-contrôle suivant :

```text
Référence homologue relue : oui/non.
PORT cible relu : oui/non.
ADAPTER cible relu : oui/non.
Tests cibles relus : oui/non.
Comportement copié depuis la référence : oui/non.
Divergences justifiées par une différence réelle relue : oui/non.
Vocabulaire banni absent de la livraison : oui/non.
Messages attendus alignés sur la référence : oui/non.
Constantes DISPLAY_NAME cohérentes avec le comportement attendu : oui/non.
```

Si une réponse est `non`, l'IA ne doit pas livrer le code.

## 39) RT-FORMALISME-UC-INTEGRATION-CREER-01 — Règles issues de `TypeProduitCuServiceIntegrationTest.creer(...)`

### 39.1 Origine de la règle

Cette règle sacralise les corrections utilisateur validées pendant la reprise du bloc `creer(...)` de `TypeProduitCuServiceIntegrationTest.java` et l'harmonisation des PORTS / ADAPTERS UC `creer(...)`.

L'erreur à empêcher est double :

- changer de formalisme alors qu'une référence validée existe déjà ;
- livrer un bloc de test incomplet pour STS en mélangeant constantes, tags, display names et méthodes.

### 39.2 Référence de formalisme obligatoire

Pour un bloc de tests comparable déjà validé, l'IA doit relire et reprendre le formalisme du test de référence du même niveau.

Pour les tests d'intégration UC, les tests Gateway d'intégration validés peuvent servir de référence de formalisme lorsque leur structure est transposable :

- constantes de tags dédiées par bloc ;
- constantes de display name ou `DN_...` selon le formalisme local ;
- annotations dans l'ordre validé ;
- commentaires internes simples `ARRANGE`, `ACT`, `ACT - ASSERT`, `ASSERT` ;
- preuve par lecture directe du stockage lorsque le scénario le justifie ;
- noms de tests stables et proches du scénario ;
- ordre des cas : erreurs locales, cas alternatifs, scénario nominal.

L'IA ne doit pas introduire un nouveau format comme des séparateurs décoratifs lourds, de nouveaux noms de tests, de nouvelles formulations ou des `@DisplayName("...")` en dur si la référence utilise des constantes.

### 39.3 Livraison STS obligatoire en blocs séparés

Lorsqu'un bloc de tests est codé ou corrigé, l'IA doit livrer séparément les éléments selon leur emplacement réel dans STS.

Ordre de livraison obligatoire :

1. bloc dédié pour la constante de tag du bloc de tests, par exemple `TAG_CREER` ;
2. bloc dédié pour les constantes de display name du bloc, par exemple `DN_CREER_NULL`, `DN_CREER_BLANK`, `DN_CREER_DOUBLON`, `DN_CREER_OK` ;
3. bloc dédié pour les méthodes de test, à placer dans le corps de la classe.

La raison est pratique : la constante `TAG_...` ne se place pas au même endroit que les constantes `DN_...`, et les méthodes de test se placent dans une autre zone de la classe.

Interdictions :

- ne pas noyer le tag dans le bloc des `DN_...` ;
- ne pas mélanger les constantes et les méthodes dans une seule livraison ;
- ne pas mettre de `@DisplayName` inline si la classe ou la référence utilise des constantes ;
- ne pas créer une constante inutile lorsque le formalisme local ne l'exige pas ;
- ne pas renommer un test validé sans demande explicite.

### 39.4 Commentaires de tests : simplicité factuelle obligatoire

Les commentaires de tests doivent rester simples, factuels et utiles.

Ils doivent décrire directement :

- la préparation du scénario ;
- l'appel testé ;
- l'exception attendue ou le retour attendu ;
- le message contractuel attendu ;
- la preuve de stockage réalisée.

L'IA ne doit pas ajouter de formulation vague, interprétative ou ronflante.

Formulation acceptée :

```java
/* ACT - ASSERT :
 * Garantit que this.service.creer(libellé blank)
 * - jette une ExceptionParametreBlank
 * - avec un message MESSAGE_CREER_LIBELLE_BLANK_KO.
 */
```

Formulation à éviter dans un commentaire d'exécution si elle n'apporte rien au code contrôlé :

```text
erreur utilisateur bénigne
échec contractuel
scénario exploitable
résultat exploitable
```

Un commentaire doit nommer l'appel ou le fait contrôlé lorsque cela clarifie le test, par exemple `service.creer(null)`, `service.creer(input)` ou `MESSAGE_CREER_NULL_KO`.

### 39.5 Javadocs des constantes de test

La Javadoc d'une constante de test sert d'abord au développeur dans STS : au survol ou à l'autocomplétion, il doit voir immédiatement la valeur littérale de la constante.

Pour une constante simple, utiliser une Javadoc qui affiche la valeur :

```java
/**
 * "IT-TP-ALPHA".
 */
public static final String IT_ALPHA = "IT-TP-ALPHA";
```

Ne pas ajouter un préfixe vague ou décoratif si ce préfixe n'apporte aucune information concrète.

Exemple à éviter :

```java
/**
 * TypeProduit IT : "IT-TP-PAGE-03".
 */
public static final String IT_PAGE_03 = "IT-TP-PAGE-03";
```

`TypeProduit IT :` n'éclaire pas le développeur : la classe, le nom de constante et la valeur littérale suffisent déjà.

### 39.6 Valeurs seedées et valeurs créées par le test

Dans les tests d'intégration de création, l'IA doit distinguer strictement :

- les valeurs déjà présentes dans le stockage seedé ;
- les valeurs créées par le test.

Pour un scénario `créer une première fois, puis recréer le même libellé pour provoquer un doublon`, le libellé utilisé pour la première création doit être absent du stockage initial.

Exemple validé :

- `OUTIL = "Outil"` peut servir au scénario nominal si cette valeur est absente du stockage seedé ;
- `NON_SEEDE = "Eléctronique"` sert au scénario doublon construit par le test ;
- `Vêtement` ne doit pas être utilisé pour installer un doublon par première création si `Vêtement` est déjà seedé.

Si le scénario attendu est un doublon immédiat sur une valeur seedée, alors le test doit attendre l'exception dès le premier `service.creer(...)`.

### 39.7 Configuration Spring autonome des tests d'intégration UC

Tous les tests d'intégration DAO, Gateway, Services UC et Controllers doivent être autonomes et rejouables individuellement ou en suite complète.

Pour un test d'intégration UC avec stockage JPA/H2, le modèle validé est :

- `@DataJpaTest` ;
- `@ContextConfiguration` avec une `ConfigTest` locale ;
- `@SpringBootConfiguration(proxyBeanMethods = false)` ;
- `@AutoConfigurationPackage(basePackageClasses = ...)` ;
- `@Import` explicite du SERVICE UC testé et des Gateways nécessaires ;
- pas de `@SpringBootTest` si un slice suffit ;
- pas de `@ComponentScan` large ;
- pas de `@EnableJpaRepositories` manuel ;
- pas de `@EntityScan` manuel ;
- pas de `spring.main.allow-bean-definition-overriding=true`.

Lorsque le SERVICE UC mémorise un état local comme le message retourné par `getMessage()`, `@Sql` ne suffit pas : `@Sql` réinitialise le stockage, mais pas l'état du bean Spring.

Dans ce cas, conserver et commenter :

```java
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
```

La justification doit rester précise : recréer un SERVICE UC neuf entre les tests pour que l'état initial de `getMessage()` soit réellement contrôlable.

### 39.8 Preuves JDBC après suppression JPA sous `@DataJpaTest`

Sous `@DataJpaTest`, chaque méthode s'exécute dans une transaction de test.

Après une suppression JPA, une lecture directe par `JdbcTemplate` peut ne pas voir immédiatement la suppression si le contexte de persistance n'a pas été synchronisé.

Avant une preuve JDBC portant sur une suppression, le test doit injecter `EntityManager` et appeler :

```java
this.entityManager.flush();
```

juste après `service.delete(...)` et avant les lectures SQL directes.

Le `flush()` ne change pas le scénario métier : il rend observable dans le stockage la suppression déjà demandée au SERVICE UC.

### 39.9 Homogénéité des PORTS et ADAPTERS UC `creer(...)`

Pour les PORTS et ADAPTERS UC comparables `TypeProduit`, `SousTypeProduit` et `Produit`, l'IA doit recopier à l'identique tout ce qui peut l'être.

Les seules différences autorisées sont celles imposées par l'objet métier :

- types DTO, objet métier et Gateway ;
- libellé textuel de l'objet métier ;
- présence du parent pour `SousTypeProduit` et `Produit` ;
- recherche du parent ;
- parent absent ;
- parent non persistant.

Toute autre divergence doit être justifiée par une contrainte métier ou contractuelle relue.

### 39.10 Constantes `MESSAGE_CREER_xxx` et `PREFIX_MESSAGE_CREER_xxx`

Les constantes `MESSAGE_CREER_xxx` et `PREFIX_MESSAGE_CREER_xxx` doivent être déduites mécaniquement des branches observables de la méthode `creer(...)`.

- `MESSAGE_CREER_xxx` correspond à un message complet pour une issue déterministe déjà entièrement connue par le SERVICE UC.
- `PREFIX_MESSAGE_CREER_xxx` correspond à un contexte UC fixe utilisé lorsqu'une dépendance peut jeter une exception, puis complété par le message sécurisé de cette exception.

L'IA ne doit pas inventer, renommer ou déplacer ces constantes sans lien direct avec une branche relue du code.

## 40) RT-RECOPIE-REFERENCE-VALIDEE-01 — Référence validée d'abord, recopie maximale, adaptation minimale

### 40.1 Origine de la règle

Cette règle sacralise une correction de méthode de travail : l'IA ne doit pas recréer un bloc, un commentaire ou un formalisme lorsqu'une référence validée existe déjà et peut être recopiée.

L'erreur à empêcher est la suivante :

- relire une référence validée ;
- constater qu'elle contient déjà la structure attendue ;
- puis malgré tout réécrire des commentaires, des annotations, un ordre de tests ou des assertions différents, moins homogènes et moins fiables.

Cette erreur coûte du temps, dégrade la cohérence du projet et augmente le risque d'introduire des divergences inutiles.

### 40.2 Règle générale obligatoire

Avant de coder, corriger ou auditer un bloc comparable, l'IA doit d'abord rechercher puis relire la référence validée existante du même niveau.

Si une méthode, un bloc de tests, une Javadoc, un commentaire, une structure `ARRANGE / ACT / ASSERT`, un ordre de cas, une constante, une annotation ou une assertion peut être recopié depuis cette référence, l'IA doit le recopier.

L'IA ne doit adapter que ce que l'objet métier cible impose réellement.

Le réflexe obligatoire est :

```text
référence validée d'abord ;
recopie maximale ;
adaptation minimale.
```

### 40.3 Ce qui doit être recopié

Lorsque le métier cible le permet, l'IA doit recopier depuis la référence validée :

- l'ordre des cas de test ;
- les noms de tests ;
- les annotations `@Tag`, `@DisplayName`, `@Test` ;
- les constantes `TAG_...`, `DN_...` ou `DISPLAY_NAME_...` ;
- les Javadocs de méthodes de test ;
- les commentaires internes `ARRANGE`, `ACT`, `ACT - ASSERT`, `ASSERT` ;
- les assertions ;
- les preuves de stockage ;
- la structure générale du bloc ;
- les choix de vocabulaire déjà validés.

### 40.4 Adaptations autorisées

Les seules adaptations autorisées sont celles imposées par l'objet métier cible ou par le contrat relu :

- types DTO ;
- types métier ;
- Gateway, DAO, service ou repository ciblé ;
- constantes du PORT cible ;
- libellés métier ;
- présence ou absence d'un parent ;
- méthode de recherche du parent ;
- table ou stockage contrôlé ;
- preuve spécifique indispensable au comportement cible.

Toute autre divergence doit être considérée comme suspecte et doit être justifiée explicitement par une contrainte relue.

### 40.5 Interdictions

L'IA ne doit pas :

- recréer des commentaires lorsque ceux de la référence validée peuvent être repris ;
- réordonner les cas sans raison métier ;
- renommer des tests validés sans nécessité ;
- inventer de nouvelles constantes si la référence fournit déjà le motif ;
- remplacer des constantes `DN_...` ou `DISPLAY_NAME_...` par des `@DisplayName("...")` inline ;
- changer la structure `ARRANGE / ACT / ASSERT` lorsqu'elle est transposable ;
- livrer un bloc homogène sur le fond mais divergent dans la forme alors qu'une recopie était possible.

### 40.6 Application aux tests UC d'intégration `creer(...)`

Pour `SousTypeProduitCuServiceIntegrationTest.creer(...)`, la référence première est `TypeProduitCuServiceIntegrationTest.creer(...)`.

Pour une méthode comme `testCreerNull()`, le cas s'arrête avant toute logique de parent. La méthode de référence `TypeProduitCuServiceIntegrationTest.testCreerNull()` peut donc être reprise presque intégralement.

Les seules adaptations attendues sont mécaniques :

- `TypeProduitICuService` vers `SousTypeProduitICuService` ;
- `TypeProduitDTO.OutputDTO` vers `SousTypeProduitDTO.OutputDTO` ou `OutputDTO` importé ;
- `SELECT_COUNT_FROM_TYPES_PRODUIT` vers `SELECT_COUNT_FROM_SOUS_TYPES_PRODUIT` ;
- preuve de stockage sur `TYPES_PRODUIT` vers preuve de stockage sur `SOUS_TYPES_PRODUIT`.

L'IA doit donc commencer par vérifier si la référence peut être recopiée, puis recopier, puis adapter seulement ces éléments.

### 40.7 Critère de livraison

Avant de livrer un bloc comparable, l'IA doit contrôler :

```text
Référence validée relue : oui/non.
Éléments recopiables identifiés : oui/non.
Éléments effectivement recopiés : oui/non.
Adaptations limitées aux différences métier : oui/non.
Divergences restantes justifiées par le contrat ou le code cible : oui/non.
```

Si une réponse est `non`, l'IA ne doit pas livrer le code.

