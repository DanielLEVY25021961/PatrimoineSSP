# docs/ai/CONTRAT_IA.md

# Contrat IA — Projet Java Hexagonal (référence unique versionnée)

## 1) Source de vérité
- La **seule source de vérité** est le **repo Git** au **SHA unique** fourni.
- Toute discussion, analyse ou correction doit être **reproductible** en relisant les fichiers au SHA.

## 2) Méthode de lecture autorisée (RT-LECTURE-GITHUB-02)
La lecture autorisée est **uniquement** :
1. URL Raw au format :
   `https://raw.githubusercontent.com/{owner}/{repo}/{SHA}/{path}`
2. **Download binaire** (octets bruts)
3. Lecture locale des octets
4. **Vérification des génériques** (jamais de Raw Types)

Règles :
- Si un fichier ne peut pas être lu correctement (ex : génériques illisibles) :
  - signaler **"incident de lecture"**
  - demander une **nouvelle URL raw SHA** ou une **recopie dans le chat**
- En cas d’incident de lecture : relancer automatiquement jusqu’à succès (max 3 tentatives).

## 3) Modes d’interaction (toujours explicites)
### MODE A — LECTURE (zéro code)
Entrées attendues :
- SHA
- liste de fichiers (paths)
- objectif

Sortie :
- confirmation de lecture
- signatures exactes / contrats trouvés
- différences factuelles / divergences

### MODE B — DIAGNOSTIC (zéro patch)
Sortie :
- comparaison “test suppose X / impl fait Y”
- causes racines
- options de correction (test vs impl), impact

### MODE C — CODER (patch)
Uniquement si le message contient : **"coder"**.
Sortie :
- code intégrable (fichiers complets ou méthodes complètes selon demande)
- style conforme (javadoc, commentaires, etc.) si Java

## 4) SHA unique obligatoire
- Toute requête doit fournir **un SHA unique courant**.
- Interdiction d’utiliser `refs/heads/...` comme source de vérité.
- Les URLs `refs/heads/...` ne servent qu’à retrouver le `path`, puis conversion en raw SHA.

## 5) Statuts des fichiers
- `MEMORISE` : conservé tel quel ; modification uniquement sur demande explicite.
- `VALIDE` : verrouillé ; modification uniquement sur demande explicite de **déverrouillage**.
- `DEVERROUILLE` : modifiable selon la demande.

## 6) “Test = Spec”
- Les tests d’intégration et unitaires décrivent le **contrat réel** (comportement).
- Toute ambiguïté doit être tranchée dans :
  - le test (preuve)
  - le document de contrat (spécification lisible)

## 7) Checklist minimale (à chaque demande)
- [ ] SHA fourni
- [ ] liste des paths fournie
- [ ] mode demandé : LECTURE / DIAG / CODER
- [ ] si CODER : indiquer “fichier complet” ou “méthodes uniquement”
