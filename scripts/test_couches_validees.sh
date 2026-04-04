#!/usr/bin/env bash
set -euo pipefail

# =====================================================================
# Script canonique des couches validées
# =====================================================================
#
# Objectif :
# - lancer les tests Maven des couches déjà validées et vertes ;
# - fournir un point d’entrée stable et évolutif ;
# - commencer par inclure les services UC ;
# - intégrer plus tard les tests des Controllers puis des Vues
#   lorsqu’ils seront créés et validés.
#
# Règle :
# - ce script ne doit jamais être nommé selon un nombre de couches ;
# - il doit refléter l’ensemble courant des couches validées.
# =====================================================================

TESTS_PATTERNS=(
  "**/model/dto/produittype/*Test.java"
  "**/model/metier/produittype/*Test.java"
  "**/model/utilitaires/metier/produittype/*Test.java"
  "**/persistence/metier/produittype/entities/entitiesJPA/*Test.java"
  "**/model/services/produittype/pagination/*Test.java"
  "**/model/services/produittype/gateway/impl/*Test.java"
  "**/model/services/produittype/cu/impl/*Test.java"
  # A ajouter plus tard lorsque les couches seront créées et validées :
  # "**/controllers/**/*Test.java"
  # "**/views/**/*Test.java"
)

TESTS_CSV="$(IFS=,; echo "${TESTS_PATTERNS[*]}")"

echo "=================================================="
echo "Lancement Maven des couches validées"
echo "=================================================="
echo "Patterns : ${TESTS_CSV}"
echo

mvn test "-Dtest=${TESTS_CSV}"