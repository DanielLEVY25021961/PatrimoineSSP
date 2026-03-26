#!/usr/bin/env bash
mvn test "-Dtest=**/model/metier/produittype/*Test.java,**/model/utilitaires/metier/produittype/*Test.java,**/persistence/metier/produittype/entities/entitiesJPA/*Test.java,**/model/services/produittype/gateway/impl/*Test.java"
