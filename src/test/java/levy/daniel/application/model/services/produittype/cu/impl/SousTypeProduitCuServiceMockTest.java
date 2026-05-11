/* ********************************************************************* */
/* ********************* TEST MOCKITO METIER UC ************************ */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE SousTypeProduitCuServiceMockTest.java :</p>
 *
 * <p>
 * Tests unitaires JUnit 5 / Mockito du SERVICE METIER UC
 * {@link SousTypeProduitCuService} pour l'objet métier
 * {@link SousTypeProduit}.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * Cette classe vérifie que {@link SousTypeProduitCuService}, point d'entrée
 * dans la logique métier dialoguant directement avec le controller appelant,
 * respecte le contrat du PORT {@link SousTypeProduitICuService}.
 * </p>
 *
 * <p>Elle contrôle notamment :</p>
 * <ul>
 * <li>les validations locales des paramètres et des DTO ;</li>
 * <li>les messages utilisateur exposés par {@code getMessage()} ;</li>
 * <li>les conversions entre {@link SousTypeProduitDTO.InputDTO},
 * {@link SousTypeProduit} et {@link SousTypeProduitDTO.OutputDTO} ;</li>
 * <li>les scénarios où le parent {@link TypeProduit} doit être identifié
 * avant de traiter l'objet métier {@link SousTypeProduit} ;</li>
 * <li>les délégations attendues vers
 * {@link SousTypeProduitGatewayIService} et {@link TypeProduitGatewayIService} ;</li>
 * <li>l'absence de délégation Gateway lorsque le SERVICE METIER UC
 * bloque localement l'opération ;</li>
 * <li>la propagation des exceptions techniques et la rationalisation
 * des messages observables ;</li>
 * <li>les cas d'erreur, les cas alternatifs et les cas nominaux
 * de chaque méthode du PORT UC.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p>
 * Les {@link SousTypeProduitGatewayIService} et
 * {@link TypeProduitGatewayIService} sont mockés : ces tests ne valident
 * pas les adaptateurs de stockage, mais le comportement métier observable
 * du SERVICE METIER UC et le contrat de délégation entre le SERVICE METIER UC
 * et les PORTS Gateway.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * La présence du parent {@link TypeProduit} fait partie du scénario métier
 * propre à {@link SousTypeProduitCuService} : le test doit donc vérifier
 * à la fois la validation du DTO, la recherche ou la vérification du parent,
 * puis le traitement de l'objet métier {@link SousTypeProduit}.
 * </p>
 * </div>
 *
 * <div>
 * <p>Le formalisme attendu dans cette classe est le suivant :</p>
 * <ul>
 * <li>organisation par bloc de méthode du PORT UC ;</li>
 * <li>ordre lisible : erreurs, cas alternatifs, puis nominal ;</li>
 * <li>commentaires {@code ARRANGE}, {@code Configuration du Mock},
 * {@code ACT}, {@code ACT - ASSERT} et {@code ASSERT}
 * alignés avec le code immédiatement suivant ;</li>
 * <li>reprise stricte des blocs déjà validés dans la classe ou dans
 * la référence {@link TypeProduitCuServiceMockTest},
 * sans réinvention inutile ;</li>
 * <li>vérifications Mockito explicites sur les interactions attendues
 * ou interdites avec les Gateways.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class SousTypeProduitCuServiceMockTest {

	// *************************** CONSTANTES ******************************/

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** TypeProduit parent : "tourisme". */
	public static final String TOURISME = "tourisme";

	/** SousTypeProduit : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** SousTypeProduit : "vêtement". */
	public static final String VETEMENT = "vêtement";

	/** Contenu recherche rapide : "tou". */
	public static final String TOU = "tou";

	/** SousTypeProduit recherche rapide : "tourisme-a". */
	public static final String TOURISME_A = "tourisme-a";

	/** SousTypeProduit recherche rapide : "tourisme-b". */
	public static final String TOURISME_B = "tourisme-b";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** Message mock gateway : "message gateway". */
	public static final String MESSAGE_GATEWAY = "message gateway";

	/** Message mock gateway (bis) : "message gateway (bis)". */
	public static final String MESSAGE_GATEWAY_BIS = "message gateway (bis)";
	
	/**
	 * "IT_STP_GAMMA"
	 */
	public static final String IT_STP_GAMMA = "IT_STP_GAMMA";
	
	/**
	 * "IT_STP_DELTA"
	 */
	public static final String IT_STP_DELTA = "IT_STP_DELTA";
	
	/**
	 * "exception propagée + message rationalisé"
	 */
	public static final String EXCEPTION_PROPAGEE_MESSAGE 
		= "exception propagée + message rationalisé";
	
	/**
	 * "fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String FALLBACK 
		= "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String EXCEPTION_STOCKAGE_VIDE_MESSAGE 
		= "ExceptionStockageVide + MESSAGE_STOCKAGE_NULL";
	
	/**
	 * "liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String LISTE_VIDE_MESSAGE 
		= "liste vide + MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String ILLEGAL_STATE_EXCEPTION_MESSAGE 
		= "IllegalStateException + MESSAGE_PAS_PARENT";
	
	/**
	 * "null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String NULL_MESSAGE_RECHERCHE_VIDE
			= "null + MESSAGE_RECHERCHE_VIDE";

	/**
	 * "IT_FIND_BY_DTO_PARENT"
	 */
	public static final String DN_FIND_BY_DTO_PARENT
			= "IT_FIND_BY_DTO_PARENT";

	/**
	 * "IT_FIND_BY_DTO_ENFANT"
	 */
	public static final String DN_FIND_BY_DTO_ENFANT
			= "IT_FIND_BY_DTO_ENFANT";

	/**
	 * "IT_FIND_BY_DTO_AUTRE"
	 */
	public static final String DN_FIND_BY_DTO_AUTRE
			= "IT_FIND_BY_DTO_AUTRE";

	/**
	 * 1L
	 */
	public static final Long DN_FIND_BY_DTO_ID_PARENT = 1L;

	/**
	 * 2L
	 */
	public static final Long DN_FIND_BY_DTO_ID_AUTRE = 2L;

	/**
	 * 3L
	 */
	public static final Long DN_FIND_BY_DTO_ID_CIBLE = 3L;

	/** Tag JUnit : tests Mockito de la couche CU. */
	public static final String TAG = "cu-mock";

	/**
	 * "creer"
	 */
	public static final String TAG_CREER = "creer";
	
	/**
	 * "rechercherTous"
	 */
	public static final String TAG_RECHERCHER_TOUS = "rechercherTous";
	
	/**
	 * "rechercherTousString"
	 */
	public static final String TAG_RECHERCHER_TOUS_STRING 
		= "rechercherTousString";
	
	/**
	 * "rechercherTousParPage"
	 */
	public static final String TAG_RECHERCHER_TOUS_PAR_PAGE
		= "rechercherTousParPage";
	
	/**
	 * "findByLibelle"
	 */
	public static final String TAG_FIND_BY_LIBELLE = "findByLibelle";
	
	/**
	 * "findByLibelleRapide"
	 */
	public static final String TAG_FIND_BY_LIBELLE_RAPIDE 
		= "findByLibelleRapide";
	
	/**
	 * "findAllByParent"
	 */
	public static final String TAG_FIND_ALL_BY_PARENT = "findAllByParent";

	/**
	 * "findByDTO"
	 */
	public static final String TAG_FIND_BY_DTO = "findByDTO";

	/**
	 * "creer(null) : MESSAGE_CREER_NULL + aucune interaction Gateway"
	 */
	public static final String DISPLAY_NAME_CREER_NULL
			= "creer(null) : MESSAGE_CREER_NULL "
					+ "+ aucune interaction Gateway";
	
	/**
	 * "creer(blank) : ExceptionParametreBlank + MESSAGE_CREER_NOM_BLANK"
	 */
	public static final String DISPLAY_NAME_CREER_BLANK
			= "creer(blank) : ExceptionParametreBlank "
					+ "+ MESSAGE_CREER_NOM_BLANK";
	
	/**
	 * "creer(parent blank) : IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_BLANK
			= "creer(parent blank) : IllegalStateException "
					+ "+ MESSAGE_PAS_PARENT";
	
	/**
	 * "creer(contrôle doublon KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_CREER_CONTROLE_DOUBLON_AVEC_MESSAGE
			= "creer(contrôle doublon KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "creer(contrôle doublon KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_CONTROLE_DOUBLON_SANS_MESSAGE
			= "creer(contrôle doublon KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "creer(doublon) : ExceptionDoublon + aucune création Gateway"
	 */
	public static final String DISPLAY_NAME_CREER_DOUBLON
			= "creer(doublon) : ExceptionDoublon "
					+ "+ aucune création Gateway";
	
	/**
	 * "creer(parent KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_TECHNIQUE_AVEC_MESSAGE
			= "creer(parent KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "creer(parent KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_TECHNIQUE_SANS_MESSAGE
			= "creer(parent KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "creer(parent absent) : IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_ABSENT
			= "creer(parent absent) : IllegalStateException "
					+ "+ MESSAGE_PAS_PARENT";
	
	/**
	 * "creer(parent non persistant) :
	 * IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_NON_PERSISTANT
			= "creer(parent non persistant) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "creer(gateway.creer KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_AVEC_MESSAGE
			= "creer(gateway.creer KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "creer(gateway.creer KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_SANS_MESSAGE
			= "creer(gateway.creer KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "creer(gateway.creer retourne null) :
	 * MESSAGE_CREATION_TECHNIQUE_KO_CREER"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_RETOUR_NULL
			= "creer(gateway.creer retourne null) : "
					+ "MESSAGE_CREATION_TECHNIQUE_KO_CREER";
	
	/**
	 * "creer(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE
			= "creer(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "creer(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_SANS_MESSAGE
			= "creer(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "creer(nominal) : OutputDTO + MESSAGE_CREER_OK"
	 */
	public static final String DISPLAY_NAME_CREER_NOMINAL
			= "creer(nominal) : OutputDTO + MESSAGE_CREER_OK";
	
	/**
	 * "rechercherTous(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_RETOUR_NULL
			= "rechercherTous(gateway retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "rechercherTous(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_AVEC_MESSAGE
			= "rechercherTous(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTous(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTous(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTous(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE
			= "rechercherTous(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTous(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_SANS_MESSAGE
			= "rechercherTous(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTous(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE
			= "rechercherTous(vide après filtrage) : "
					+ LISTE_VIDE_MESSAGE;
	
	/**
	 * "rechercherTous(nominal) :
	 * OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL
			= "rechercherTous(nominal) : "
					+ "OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "rechercherTousString(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_RETOUR_NULL
			= "rechercherTousString(gateway retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "rechercherTousString(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_AVEC_MESSAGE
			= "rechercherTousString(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTousString(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTousString(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTousString(conversion String KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_AVEC_MESSAGE
			= "rechercherTousString(conversion String KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTousString(conversion String KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_SANS_MESSAGE
			= "rechercherTousString(conversion String KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTousString(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE
			= "rechercherTousString(vide après filtrage) : "
					+ LISTE_VIDE_MESSAGE;
	
	/**
	 * "rechercherTousString(vide après libellés blank) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK
			= "rechercherTousString(vide après libellés blank) : "
					+ LISTE_VIDE_MESSAGE;
	
	/**
	 * "rechercherTousString(nominal) :
	 * libellés triés dédoublonnés + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL
			= "rechercherTousString(nominal) : "
					+ "libellés triés dédoublonnés + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "rechercherTousParPage(null) :
	 * IllegalStateException + MESSAGE_PAGEABLE_NULL"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL
			= "rechercherTousParPage(null) : "
					+ "IllegalStateException + MESSAGE_PAGEABLE_NULL";
	
	/**
	 * "rechercherTousParPage(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_AVEC_MESSAGE
			= "rechercherTousParPage(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTousParPage(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTousParPage(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTousParPage(gateway retourne null) :
	 * IllegalStateException + MESSAGE_RECHERCHE_PAGINEE_KO"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL
			= "rechercherTousParPage(gateway retourne null) : "
					+ "IllegalStateException + MESSAGE_RECHERCHE_PAGINEE_KO";
	
	/**
	 * "rechercherTousParPage(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "rechercherTousParPage(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "rechercherTousParPage(vide après filtrage) :
	 * page vide + MESSAGE_RECHERCHE_PAGINEE_OK"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE
			= "rechercherTousParPage(vide après filtrage) : "
					+ "page vide + MESSAGE_RECHERCHE_PAGINEE_OK";
	
	/**
	 * "rechercherTousParPage(nominal) :
	 * page OutputDTO triée dédoublonnée + MESSAGE_RECHERCHE_PAGINEE_OK"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL
			= "rechercherTousParPage(nominal) : "
					+ "page OutputDTO triée dédoublonnée "
					+ "+ MESSAGE_RECHERCHE_PAGINEE_OK";
	
	/**
	 * "findByLibelle(null) :
	 * liste vide + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NULL
			= "findByLibelle(null) : "
					+ "liste vide + MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByLibelle(blank) :
	 * liste vide + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_BLANK
			= "findByLibelle(blank) : "
					+ "liste vide + MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByLibelle(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL
			= "findByLibelle(gateway retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "findByLibelle(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_AVEC_MESSAGE
			= "findByLibelle(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByLibelle(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_SANS_MESSAGE
			= "findByLibelle(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByLibelle(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByLibelle(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByLibelle(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByLibelle(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByLibelle(introuvable) :
	 * liste vide + MESSAGE_OBJ_INTROUVABLE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_INTROUVABLE
			= "findByLibelle(introuvable) : "
					+ "liste vide + MESSAGE_OBJ_INTROUVABLE";
	
	/**
	 * "findByLibelle(nominal) :
	 * OutputDTO triés dédoublonnés + MESSAGE_SUCCES_RECHERCHE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL
			= "findByLibelle(nominal) : "
					+ "OutputDTO triés dédoublonnés "
					+ "+ MESSAGE_SUCCES_RECHERCHE";
	
	/**
	 * "findByLibelleRapide(null) :
	 * IllegalStateException + MESSAGE_PARAM_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL
			= "findByLibelleRapide(null) : "
					+ "IllegalStateException + MESSAGE_PARAM_NULL";
	
	/**
	 * "findByLibelleRapide(blank) :
	 * délègue à rechercherTous() + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK
			= "findByLibelleRapide(blank) : "
					+ "délègue à rechercherTous() + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "findByLibelleRapide(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_AVEC_MESSAGE
			= "findByLibelleRapide(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByLibelleRapide(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_SANS_MESSAGE
			= "findByLibelleRapide(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByLibelleRapide(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL
			= "findByLibelleRapide(gateway retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "findByLibelleRapide(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByLibelleRapide(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByLibelleRapide(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE
			= "findByLibelleRapide(vide après filtrage) : "
					+ LISTE_VIDE_MESSAGE;
	
	/**
	 * "findByLibelleRapide(nominal) :
	 * OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL
			= "findByLibelleRapide(nominal) : "
					+ "OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "findAllByParent(null) :
	 * IllegalStateException + RECHERCHE_TYPEPRODUIT_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL
			= "findAllByParent(null) : "
					+ "IllegalStateException + RECHERCHE_TYPEPRODUIT_NULL";
	
	/**
	 * "findAllByParent(parent blank) :
	 * IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK
			= "findAllByParent(parent blank) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "findAllByParent(parent gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KO_AVEC_MESSAGE
			= "findAllByParent(parent gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findAllByParent(parent gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KO_SANS_MESSAGE
			= "findAllByParent(parent gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findAllByParent(parent absent) :
	 * IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT
			= "findAllByParent(parent absent) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "findAllByParent(parent non persistant) :
	 * IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT
			= "findAllByParent(parent non persistant) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "findAllByParent(enfants gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KO_AVEC_MESSAGE
			= "findAllByParent(enfants gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findAllByParent(enfants gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KO_SANS_MESSAGE
			= "findAllByParent(enfants gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findAllByParent(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_GATEWAY_RETOUR_NULL
			= "findAllByParent(gateway retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "findAllByParent(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findAllByParent(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findAllByParent(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findAllByParent(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findAllByParent(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_VIDE_APRES_FILTRAGE
			= "findAllByParent(vide après filtrage) : "
					+ LISTE_VIDE_MESSAGE;
	
	/**
	 * "findAllByParent(nominal) :
	 * OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NOMINAL
			= "findAllByParent(nominal) : "
					+ "OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "findByDTO(null) :
	 * null + MESSAGE_RECHERCHE_OBJ_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_NULL
			= "findByDTO(null) : "
					+ "null + MESSAGE_RECHERCHE_OBJ_NULL";
	
	/**
	 * "findByDTO(parent blank) :
	 * IllegalStateException + MESSAGE_PAS_PARENT"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_BLANK
			= "findByDTO(parent blank) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "findByDTO(parent gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_GATEWAY_KO_AVEC_MESSAGE
			= "findByDTO(parent gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByDTO(parent gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_GATEWAY_KO_SANS_MESSAGE
			= "findByDTO(parent gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByDTO(parent absent) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_ABSENT
			= "findByDTO(parent absent) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(parent non persistant) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_NON_PERSISTANT
			= "findByDTO(parent non persistant) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(enfants gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_ENFANTS_GATEWAY_KO_AVEC_MESSAGE
			= "findByDTO(enfants gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByDTO(enfants gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_ENFANTS_GATEWAY_KO_SANS_MESSAGE
			= "findByDTO(enfants gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByDTO(gateway retourne null) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_GATEWAY_RETOUR_NULL
			= "findByDTO(gateway retourne null) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(vide) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_VIDE
			= "findByDTO(vide) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(vide après filtrage) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_VIDE_APRES_FILTRAGE
			= "findByDTO(vide après filtrage) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(introuvable dans la liste) :
	 * null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_INTROUVABLE_DANS_LISTE
			= "findByDTO(introuvable dans la liste) : "
					+ NULL_MESSAGE_RECHERCHE_VIDE;
	
	/**
	 * "findByDTO(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByDTO(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findByDTO(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByDTO(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findByDTO(nominal) :
	 * OutputDTO + MESSAGE_SUCCES_RECHERCHE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_NOMINAL
			= "findByDTO(nominal) : "
					+ "OutputDTO + MESSAGE_SUCCES_RECHERCHE";
	

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitCuServiceMockTest() {
		super();
	}

	
	
	// ***************************** TESTS *******************************/

	
	
	// ============================ creer =================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(null) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne le message utilisateur
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_NULL)
	@Test
	public void testCreerNull() throws Exception {

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

		/* ACT :
		 * exécute la création avec un DTO null.
		 */
		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne le message utilisateur contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NOM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * <p>
	 * Ce test vise la branche locale :
	 * {@code StringUtils.isBlank(libelle)} dans
	 * {@code SousTypeProduitCuService.creer(...)}.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_BLANK)
	@Test
	public void testCreerBlank() {

		/* ARRANGE :
		 * prépare un DTO dont le libellé de l'objet métier est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation aux Gateways.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionParametreBlank ;
		 * - émet le message MESSAGE_CREER_NOM_BLANK
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent blank) :</p>
	 * <ul>
	 * <li>contrôle localement le libellé du parent ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_BLANK)
	@Test
	public void testCreerParentBlank() {

		/* ARRANGE :
		 * prépare un DTO dont le libellé parent est blank.
		 *
		 * Le SERVICE METIER UC doit refuser ce parent avant
		 * toute délégation aux Gateways.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				ESPACES, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une IllegalStateException ;
		 * - émet le message MESSAGE_PAS_PARENT.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(contrôle de doublon KO avec message) :</p>
	 * <ul>
	 * <li>atteint le bloc {@code try/catch} qui appelle
	 * {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} appelle
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
	 * + message technique ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_DOUBLON_AVEC_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * simule une panne technique de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que la création et la recherche du parent
		 * ne sont jamais tentées après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(contrôle de doublon KO sans message) :</p>
	 * <ul>
	 * <li>atteint le bloc {@code try/catch} qui appelle
	 * {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} appelle
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_DOUBLON_SANS_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création et la recherche du parent
		 * ne sont jamais tentées après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(doublon fonctionnel) :</p>
	 * <ul>
	 * <li>contrôle l'unicité via {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} interroge le Gateway SousTypeProduit
	 * via {@code gateway.findByLibelle(...)} ;</li>
	 * <li>retient seulement le doublon portant le même parent
	 * et le même libellé ;</li>
	 * <li>jette une {@link ExceptionDoublon} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_DOUBLON} + libellé ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne délègue jamais la création au Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_DOUBLON)
	@Test
	public void testCreerDoublon() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide dont le couple [parent, libellé]
		 * existe déjà dans le stockage selon le Gateway mocké.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentExistant = new TypeProduit(BAZAR);
		parentExistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant 
			= new SousTypeProduit(OUTILLAGE, parentExistant);
		existant.setIdSousTypeProduit(1L);
			
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		/*
		 * Configuration du Mock :
		 * simule un doublon fonctionnel détecté par isDoublon(...)
		 * via l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(existant));

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionDoublon ;
		 * - émet le message MESSAGE_DOUBLON + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_DOUBLON
						+ OUTILLAGE);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_DOUBLON
						+ OUTILLAGE);

		/* Garantit que le contrôle d'unicité a été exécuté,
		 * que la création n'a jamais été déléguée au Gateway enfant,
		 * et que le parent n'a pas été recherché après le doublon.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>atteint la recherche du parent persistant ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER}
	 * + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_AVEC_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide sans doublon afin d'atteindre
		 * réellement la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * - findByLibelle(...) sur le Gateway enfant retourne une liste vide
		 *   pour simuler l'absence de doublon ;
		 * - findByLibelle(...) sur le Gateway parent lève une panne technique.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec de recherche du parent.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>atteint la recherche du parent persistant ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_SANS_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide sans doublon afin d'atteindre
		 * réellement la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * - findByLibelle(...) sur le Gateway enfant retourne une liste vide
		 *   pour simuler l'absence de doublon ;
		 * - findByLibelle(...) sur le Gateway parent lève une panne technique
		 *   sans message.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec de recherche du parent.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent absent) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>cherche le parent persistant ;</li>
	 * <li>jette une {@link IllegalStateException} si le parent
	 * est absent du stockage ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_ABSENT)
	@Test
	public void testCreerParentAbsent() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide dont le parent n'existe pas
		 * dans le stockage selon le Gateway parent mocké.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le Gateway enfant ne détecte aucun doublon ;
		 * - le Gateway parent ne retrouve aucun parent persistant.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que l'absence de parent est refusée
		 * avec le message utilisateur MESSAGE_PAS_PARENT.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la création n'est jamais tentée
		 * lorsque le parent est absent.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent non persistant) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>cherche le parent ;</li>
	 * <li>jette une {@link IllegalStateException} si le parent retrouvé
	 * ne porte pas d'identifiant persistant ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_NON_PERSISTANT)
	@Test
	public void testCreerParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent retrouvé
		 * mais dépourvu d'identifiant persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le Gateway enfant ne détecte aucun doublon ;
		 * - le Gateway parent retourne un objet sans identifiant.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ACT - ASSERT */
		/* Garantit qu'un parent non persistant est refusé
		 * avec le même message utilisateur qu'un parent absent.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la création n'est jamais tentée
		 * lorsque le parent n'est pas persistant.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché au parent ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception levée par {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_AVEC_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * - aucun doublon enfant n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) échoue avec un message technique.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de création.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché au parent ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception sans message levée par
	 * {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_SANS_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * - aucun doublon enfant n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) échoue sans message technique.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de création.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) retourne null) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>jette une {@link IllegalStateException} si le Gateway
	 * ne retourne aucun objet créé ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREATION_TECHNIQUE_KO_CREER}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_RETOUR_NULL)
	@Test
	public void testCreerGatewayRetourneNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui accepte le contrôle d'unicité
		 * et le parent, mais ne retourne aucun objet créé.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le succès apparent
		 * et refuse une réponse technique null.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		/* Garantit que le Gateway a bien été sollicité jusqu'à la création,
		 * puis que l'anomalie null est traitée côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
		 *
		 * Le Gateway retourne ensuite un objet métier mocké dont l'accès
		 * au parent provoque une panne pendant la conversion en OutputDTO.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final SousTypeProduit cree = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(cree.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_SANS_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
		 *
		 * Le Gateway retourne ensuite un objet métier mocké dont l'accès
		 * au parent provoque une panne sans message pendant la conversion.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final SousTypeProduit cree = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(cree.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>récupère le parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché
	 * au parent persistant ;</li>
	 * <li>délègue la création à {@code gateway.creer(...)} ;</li>
	 * <li>convertit l'objet métier créé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant généré,
	 * le bon libellé et le bon parent ;</li>
	 * <li>positionne le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_NOMINAL)
	@Test
	public void testCreerNominal() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant, le retour métier
		 * créé par le Gateway, et un captor pour contrôler précisément
		 * l'objet métier envoyé à gateway.creer(...).
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit cree 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		cree.setIdSousTypeProduit(1L);
		
		final ArgumentCaptor<SousTypeProduit> captor
				= ArgumentCaptor.forClass(SousTypeProduit.class);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);
		
		/*
		 * Configuration du Mock :
		 * - findByLibelle(...) sur le Gateway enfant retourne une liste vide
		 *   pour simuler l'absence de doublon fonctionnel ;
		 * - findByLibelle(...) sur le Gateway parent retourne le parent
		 *   persistant ;
		 * - creer(...) retourne l'objet métier réellement créé
		 *   avec l'identifiant généré par le stockage.
		 */
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		/* ACT :
		 * exécute la création via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que les Gateways ont bien été sollicités
		 * dans l'ordre fonctionnel attendu :
		 * contrôle d'unicité, recherche du parent, puis création.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(captor.capture());

		/* Garantit que l'objet métier envoyé au Gateway enfant :
		 * - n'est pas null ;
		 * - ne porte pas encore d'identifiant ;
		 * - porte le libellé métier issu de l'InputDTO ;
		 * - porte le parent persistant retrouvé via le Gateway parent.
		 */
		final SousTypeProduit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdSousTypeProduit()).isNull();
		assertThat(envoye.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(envoye.getTypeProduit()).isNotNull();
		assertThat(envoye.getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(envoye.getTypeProduit().getIdTypeProduit()).isEqualTo(1L);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant généré ;
		 * - porte le bon libellé métier ;
		 * - porte le bon parent ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(1L);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_OK);

	} // __________________________________________________________________

	
	
	// ======================== rechercherTous ============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousGatewayRetourNull() throws Exception {

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

		/*
		 * Configuration du Mock :
		 * simule un Gateway SousTypeProduit qui retourne null
		 * au lieu d'une liste exploitable par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTous() :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway SousTypeProduit
		 * a été sollicité pour la recherche exhaustive.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique avec message.
		 */
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.rechercherTous().
		 */
		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique sans message.
		 */
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.rechercherTous().
		 */
		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_SANS_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne sans message pendant la conversion en OutputDTO.
		 */
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final List<SousTypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null
		 * ne contenant aucun objet métier exploitable.
		 */
		when(gateway.rechercherTous()).thenReturn(records);

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide après filtrage ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL)
	@Test
	public void testRechercherTousNominal() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway contenant :
		 * - deux objets métier exploitables ;
		 * - un élément null à filtrer ;
		 * - un doublon à dédoublonner côté DTO.
		 */
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		
		final SousTypeProduit stpVetement 
			= new SousTypeProduit(VETEMENT, parent);
		stpVetement.setIdSousTypeProduit(2L);
		
		final SousTypeProduit stpOutillage 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillage.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpOutillageDoublon 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillageDoublon.setIdSousTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne des objets métier dans un ordre
		 * non trié, avec un null et un doublon côté DTO.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						stpVetement, null, stpOutillage, stpOutillageDoublon));

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, VETEMENT);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	// ===================== rechercherTousString =========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousStringGatewayRetourNull() throws Exception {

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

		/*
		 * Configuration du Mock :
		 * simule un Gateway SousTypeProduit qui retourne null
		 * au lieu d'une liste exploitable par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousString() :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway SousTypeProduit
		 * a été sollicité pour la recherche exhaustive.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique avec message.
		 */
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.rechercherTous().
		 */
		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique sans message.
		 */
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.rechercherTous().
		 */
		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion String KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint l'extraction des libellés via
	 * {@code SousTypeProduit.getSousTypeProduit()} ;</li>
	 * <li>propage l'exception levée pendant cette extraction ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au libellé
		 * provoque une panne pendant l'extraction String.
		 */
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la préparation de la réponse String lit le libellé métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getSousTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec d'extraction.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion String KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint l'extraction des libellés via
	 * {@code SousTypeProduit.getSousTypeProduit()} ;</li>
	 * <li>propage l'exception sans message levée pendant cette extraction ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au libellé
		 * provoque une panne pendant l'extraction String.
		 */
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la préparation de la réponse String lit le libellé métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getSousTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec d'extraction.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousStringVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final List<SousTypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null
		 * ne contenant aucun objet métier exploitable.
		 */
		when(gateway.rechercherTous()).thenReturn(records);

		/* ACT :
		 * exécute la recherche exhaustive String via le SERVICE METIER UC.
		 */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide après filtrage ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après libellés blank) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>extrait les libellés via
	 * {@code SousTypeProduit.getSousTypeProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK)
	@Test
	public void testRechercherTousStringVideApresLibellesBlank() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway contenant un null
		 * et un objet métier dont le libellé est blank.
		 */
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		
		final SousTypeProduit sousTypeProduitBlank 
			= new SousTypeProduit(ESPACES, parent);
		sousTypeProduitBlank.setIdSousTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null
		 * dont les libellés restants ne sont pas exploitables.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, sousTypeProduitBlank));

		/* ACT :
		 * exécute la recherche exhaustive String via le SERVICE METIER UC.
		 */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - ne contient aucun libellé blank ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>extrait les libellés via
	 * {@code SousTypeProduit.getSousTypeProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>dédoublonne les libellés en conservant l'ordre ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL)
	@Test
	public void testRechercherTousStringNominal() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway contenant :
		 * - deux objets métier exploitables ;
		 * - un élément null à filtrer ;
		 * - un libellé blank à ignorer ;
		 * - un doublon à dédoublonner côté String.
		 */
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		
		final SousTypeProduit stpVetement 
			= new SousTypeProduit(VETEMENT, parent);
		stpVetement.setIdSousTypeProduit(2L);
		
		final SousTypeProduit stpOutillage 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillage.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpBlank 
			= new SousTypeProduit(ESPACES, parent);
		stpBlank.setIdSousTypeProduit(3L);
		
		final SousTypeProduit stpOutillageDoublon 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillageDoublon.setIdSousTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne des objets métier dans un ordre
		 * non trié, avec un null, un libellé blank et un doublon String.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						stpVetement, null, stpBlank, stpOutillage, 
						stpOutillageDoublon));

		/* ACT :
		 * exécute la recherche exhaustive String via le SERVICE METIER UC.
		 */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les libellés exploitables ;
		 * - est triée selon l'ordre métier des objets ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly(OUTILLAGE, VETEMENT);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	
	
	// ===================== rechercherTousParPage ========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(null) :</p>
	 * <ul>
	 * <li>refuse une requête de pagination {@code null} ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAGEABLE_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL)
	@Test
	public void testRechercherTousParPageNull() throws Exception {

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

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousParPage(null) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PAGEABLE_NULL contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.rechercherTousParPage(...).
		 */
		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche paginée
		 * du Gateway SousTypeProduit, sans solliciter le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.rechercherTousParPage(...).
		 */
		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche paginée
		 * du Gateway SousTypeProduit, sans solliciter le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>détecte que le Gateway retourne un résultat paginé {@code null} ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousParPageGatewayRetourNull()
			throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui retourne null au lieu d'un ResultatPage
		 * exploitable par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTousParPage(requete)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le contrat observable
		 * et refuse une réponse paginée technique null.
		 */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		/* Garantit que le Gateway SousTypeProduit a bien été sollicité
		 * une seule fois, sans interaction avec le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>récupère le contenu via {@code resultatPagine.getContent()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 2);
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final ResultatPage<SousTypeProduit> resultatGateway
				= new ResultatPage<SousTypeProduit>(
						Arrays.asList(sousTypeProduit),
						0,
						2,
						1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>récupère le contenu via {@code resultatPagine.getContent()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 2);
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final ResultatPage<SousTypeProduit> resultatGateway
				= new ResultatPage<SousTypeProduit>(
						Arrays.asList(sousTypeProduit),
						0,
						2,
						1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>récupère le contenu via {@code resultatPagine.getContent()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>reconstruit un {@link ResultatPage} DTO non {@code null}
	 * avec un contenu vide ;</li>
	 * <li>reprend la pagination observable ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousParPageVideApresFiltrage()
			throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final RequetePage requete = new RequetePage(0, 4);
		
		final List<SousTypeProduit> records = new ArrayList<>();
		records.add(null);
		
		final ResultatPage<SousTypeProduit> resultatGateway
				= new ResultatPage<SousTypeProduit>(
						records,
						0,
						4,
						1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTousParPage(...) retourne une page non null
		 * ne contenant aucun objet métier exploitable.
		 */
		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/* ACT :
		 * exécute la recherche paginée via le SERVICE METIER UC.
		 */
		final ResultatPage<OutputDTO> retour 
				= service.rechercherTousParPage(requete);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse paginée utilisateur :
		 * - n'est jamais null ;
		 * - reprend le numéro de page ;
		 * - reprend la taille de page ;
		 * - sécurise le total d'éléments ;
		 * - contient une page vide après filtrage ;
		 * - porte le message utilisateur de succès paginé.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(1L);

		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent()).isEmpty();

		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>récupère le contenu via {@code resultatPagine.getContent()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>reprend le numéro de page, la taille de page
	 * et le total d'éléments ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
	@Test
	public void testRechercherTousParPageNominal() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway contenant :
		 * - deux objets métier exploitables ;
		 * - un élément null à filtrer ;
		 * - un doublon à dédoublonner côté DTO.
		 */
		final RequetePage requete = new RequetePage(0, 4);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		
		final SousTypeProduit stpVetement 
			= new SousTypeProduit(VETEMENT, parent);
		stpVetement.setIdSousTypeProduit(2L);
		
		final SousTypeProduit stpOutillage 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillage.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpOutillageDoublon 
			= new SousTypeProduit(OUTILLAGE, parent);
		stpOutillageDoublon.setIdSousTypeProduit(1L);
		
		final ResultatPage<SousTypeProduit> resultatGateway
				= new ResultatPage<SousTypeProduit>(
						Arrays.asList(
								stpVetement,
								null,
								stpOutillage,
								stpOutillageDoublon),
						0,
						4,
						10L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTousParPage(...) retourne des objets métier
		 * dans un ordre non trié, avec un null et un doublon côté DTO.
		 */
		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/* ACT :
		 * exécute la recherche paginée via le SERVICE METIER UC.
		 */
		final ResultatPage<OutputDTO> retour
				= service.rechercherTousParPage(requete);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse paginée retournée au controller appelant :
		 * - n'est pas null ;
		 * - reprend le numéro de page ;
		 * - reprend la taille de page ;
		 * - reprend le total d'éléments ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès paginé.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(10L);

		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent()).hasSize(2);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, VETEMENT);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	
	
	
	
	// ========================= findByLibelle ============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(null) :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NULL)
	@Test
	public void testFindByLibelleNull() throws Exception {

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

		/* ACT :
		 * exécute la recherche exacte avec un libellé null.
		 */
		final List<OutputDTO> retour = service.findByLibelle(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne une liste vide non null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(blank) :</p>
	 * <ul>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_BLANK)
	@Test
	public void testFindByLibelleBlank() throws Exception {

		/* ARRANGE :
		 * prépare un libellé blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation aux Gateways.
		 */
		final String libelle = ESPACES;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ACT :
		 * exécute la recherche exacte avec un libellé blank.
		 */
		final List<OutputDTO> retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne une liste vide non null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>détecte que le Gateway retourne {@code null}
	 * au lieu d'une liste exploitable ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByLibelleGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = OUTILLAGE;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway SousTypeProduit qui retourne null
		 * au lieu d'une liste exploitable par le SERVICE METIER UC.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.findByLibelle(libelle) :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway SousTypeProduit
		 * a été sollicité pour la recherche exacte.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = OUTILLAGE;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = OUTILLAGE;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne une liste non {@code null}
	 * contenant un objet métier exploitable ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = OUTILLAGE;
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne une liste non {@code null}
	 * contenant un objet métier exploitable ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = OUTILLAGE;
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(introuvable) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne une liste non {@code null}
	 * ne contenant aucun objet métier exploitable ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé recherché ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_INTROUVABLE)
	@Test
	public void testFindByLibelleIntrouvable() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide absent du stockage
		 * selon le Gateway mocké.
		 */
		final String libelle = OUTILLAGE;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui ne trouve aucun objet métier
		 * pour le libellé recherché.
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(new ArrayList<SousTypeProduit>());

		/* ACT :
		 * exécute la recherche exacte via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide ;
		 * - porte le message utilisateur d'introuvabilité.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
						+ libelle);

		/* Garantit que la recherche exacte a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>retourne une liste cohérente portant les parents
	 * et le libellé recherché ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL)
	@Test
	public void testFindByLibelleNominal() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway contenant :
		 * - deux objets métier exploitables portant le libellé recherché ;
		 * - un élément null à filtrer ;
		 * - un doublon à dédoublonner côté DTO.
		 */
		final String libelle = OUTILLAGE;
		
		final TypeProduit parentBazar = new TypeProduit(BAZAR);
		parentBazar.setIdTypeProduit(1L);
		
		final TypeProduit parentTourisme = new TypeProduit(TOURISME);
		parentTourisme.setIdTypeProduit(2L);
		
		final SousTypeProduit stpBazar 
			= new SousTypeProduit(libelle, parentBazar);
		stpBazar.setIdSousTypeProduit(10L);
		
		final SousTypeProduit stpTourisme 
			= new SousTypeProduit(libelle, parentTourisme);
		stpTourisme.setIdSousTypeProduit(20L);
		
		final SousTypeProduit stpBazarDoublon 
			= new SousTypeProduit(libelle, parentBazar);
		stpBazarDoublon.setIdSousTypeProduit(10L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne des objets métier dans un ordre
		 * non trié, avec un null et un doublon côté DTO.
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(
						stpTourisme, null, stpBazar, stpBazarDoublon));

		/* ACT :
		 * exécute la recherche exacte via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(10L, 20L);

		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* Garantit que la recherche exacte a bien été déléguée
		 * et que le Gateway TypeProduit reste inutilisé.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________		

	
	
	// ====================== findByLibelleRapide =========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(null) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL)
	@Test
	public void testFindByLibelleRapideNull() throws Exception {

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

		/* ACT - ASSERT */
		/* Garantit que service.findByLibelleRapide(null) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PARAM_NULL contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(blank) :</p>
	 * <ul>
	 * <li>délègue exactement au scénario complet
	 * {@code rechercherTous()} ;</li>
	 * <li>n'appelle jamais
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>retourne la liste DTO issue de la recherche exhaustive ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK)
	@Test
	public void testFindByLibelleRapideBlank() throws Exception {

		/* ARRANGE :
		 * prépare une recherche rapide blank.
		 *
		 * Ce cas doit être délégué par le SERVICE METIER UC
		 * à rechercherTous().
		 */
		final String contenu = ESPACES;
		
		final TypeProduit parentBazar = new TypeProduit(BAZAR);
		parentBazar.setIdTypeProduit(1L);
		
		final TypeProduit parentTourisme = new TypeProduit(TOURISME);
		parentTourisme.setIdTypeProduit(2L);
		
		final SousTypeProduit stpGamma 
			= new SousTypeProduit(IT_STP_GAMMA, parentBazar);
		stpGamma.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpDelta 
			= new SousTypeProduit(IT_STP_DELTA, parentTourisme);
		stpDelta.setIdSousTypeProduit(2L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * le cas blank délègue à rechercherTous(),
		 * donc il doit appeler gateway.rechercherTous().
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						stpDelta, null, stpGamma, stpDelta));

		/* ACT :
		 * exécute la recherche rapide avec un contenu blank.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - provient de la recherche exhaustive ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(IT_STP_GAMMA, IT_STP_DELTA);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que le contenu blank délègue à rechercherTous()
		 * et n'appelle jamais la recherche rapide Gateway.
		 */
		verify(gateway, times(1)).rechercherTous();
		verify(gateway, never()).findByLibelleRapide(any(String.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un contenu de recherche rapide valide.
		 */
		final String contenu = TOU;
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.findByLibelleRapide(...).
		 */
		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche rapide
		 * du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un contenu de recherche rapide valide.
		 */
		final String contenu = TOU;
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.findByLibelleRapide(...).
		 */
		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche rapide
		 * du Gateway SousTypeProduit.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>détecte que le Gateway retourne {@code null}
	 * au lieu d'une liste exploitable ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByLibelleRapideGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un contenu de recherche rapide valide.
		 */
		final String contenu = TOU;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway SousTypeProduit qui retourne null
		 * au lieu d'une liste exploitable par le SERVICE METIER UC.
		 */
		when(gateway.findByLibelleRapide(contenu)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.findByLibelleRapide(contenu) :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway SousTypeProduit
		 * a été sollicité pour la recherche rapide.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>le Gateway retourne une liste métier non {@code null} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String contenu = TOU;
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne une liste non null
		 * contenant un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche rapide Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>le Gateway retourne une liste métier non {@code null} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String contenu = TOU;
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne une liste non null
		 * contenant un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche rapide Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindByLibelleRapideVideApresFiltrage()
			throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final String contenu = TOU;
		
		final List<SousTypeProduit> records = new ArrayList<SousTypeProduit>();
		records.add(null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne une liste non null
		 * ne contenant aucun objet métier exploitable.
		 */
		when(gateway.findByLibelleRapide(contenu)).thenReturn(records);

		/* ACT :
		 * exécute la recherche rapide via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide après filtrage ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche rapide a bien été déléguée
		 * et que rechercherTous() n'a pas été appelée.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL)
	@Test
	public void testFindByLibelleRapideNominal() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway contenant :
		 * - deux objets métier exploitables ;
		 * - un élément null à filtrer ;
		 * - un doublon à dédoublonner côté DTO.
		 */
		final String contenu = TOU;
		
		final TypeProduit parentBazar = new TypeProduit(BAZAR);
		parentBazar.setIdTypeProduit(1L);
		
		final TypeProduit parentTourisme = new TypeProduit(TOURISME);
		parentTourisme.setIdTypeProduit(2L);
		
		final SousTypeProduit stpA 
			= new SousTypeProduit(TOURISME_A, parentBazar);
		stpA.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpB 
			= new SousTypeProduit(TOURISME_B, parentTourisme);
		stpB.setIdSousTypeProduit(2L);
		
		final SousTypeProduit stpADoublon 
			= new SousTypeProduit(TOURISME_A, parentBazar);
		stpADoublon.setIdSousTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne des objets métier
		 * dans un ordre non trié, avec un null et un doublon côté DTO.
		 */
		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(stpB, null, stpA, stpADoublon));

		/* ACT :
		 * exécute la recherche rapide via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(TOURISME_A, TOURISME_B);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche rapide a bien été déléguée
		 * et que rechercherTous() n'a pas été appelée.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	// ======================= findAllByParent(...) =======================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(null) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#RECHERCHE_TYPEPRODUIT_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL)
	@Test
	public void testFindAllByParentNull() throws Exception {

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

		/* ACT - ASSERT */
		/* Garantit que service.findAllByParent(null) :
		 * - lève IllegalStateException ;
		 * - émet le message RECHERCHE_TYPEPRODUIT_NULL contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.findAllByParent(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent blank) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK)
	@Test
	public void testFindAllByParentParentBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent dont le libellé est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation aux Gateways.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(ESPACES);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.findAllByParent(parentDto) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PAS_PARENT ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint la recherche du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway TypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide pour atteindre réellement
		 * la recherche du parent persistant.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message pendant la recherche
		 * du parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que l'échec de recherche parent bloque
		 * toute recherche des objets métier enfants.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint la recherche du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * le Gateway TypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide pour atteindre réellement
		 * la recherche du parent persistant.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message pendant la recherche
		 * du parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que l'échec de recherche parent bloque
		 * toute recherche des objets métier enfants.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent absent) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>lève une {@link IllegalStateException} si le parent
	 * est absent du stockage ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT)
	@Test
	public void testFindAllByParentParentAbsent() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide absent du stockage
		 * selon le Gateway parent mocké.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * le Gateway parent ne retrouve aucun parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que l'absence de parent est refusée
		 * avec le message utilisateur MESSAGE_PAS_PARENT.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la recherche des objets métier enfants
		 * n'est jamais tentée lorsque le parent est absent.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent non persistant) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>lève une {@link IllegalStateException} si le parent retrouvé
	 * ne porte pas d'identifiant persistant ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT)
	@Test
	public void testFindAllByParentParentNonPersistant()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide et un parent retrouvé
		 * mais dépourvu d'identifiant persistant.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * le Gateway parent retourne un objet sans identifiant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ACT - ASSERT */
		/* Garantit qu'un parent non persistant est refusé
		 * avec le même message utilisateur qu'un parent absent.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la recherche des objets métier enfants
		 * n'est jamais tentée lorsque le parent n'est pas persistant.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(enfants gateway KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway
	 * SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>transmet bien le parent persistant au Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide et le parent persistant
		 * retrouvé par le Gateway parent.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - la recherche des objets métier enfants échoue avec message.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la recherche enfant est appelée avec
		 * le parent persistant retrouvé via le Gateway TypeProduit.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(enfants gateway KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * le Gateway SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>transmet bien le parent persistant au Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide et le parent persistant
		 * retrouvé par le Gateway parent.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - la recherche des objets métier enfants échoue sans message.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la recherche enfant est appelée avec
		 * le parent persistant retrouvé via le Gateway TypeProduit.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(gateway retourne null) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>détecte que le Gateway SousTypeProduit retourne {@code null}
	 * au lieu d'une liste exploitable ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindAllByParentGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide et le parent persistant
		 * retrouvé par le Gateway parent.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - le Gateway enfant retourne null au lieu d'une liste exploitable.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.findAllByParent(parentDto) :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que la recherche enfant est appelée avec
		 * le parent persistant retrouvé via le Gateway TypeProduit.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>le Gateway SousTypeProduit retourne une liste métier non
	 * {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide, le parent persistant,
		 * et un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * - l'objet métier possède un libellé exploitable ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche enfant Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>le Gateway SousTypeProduit retourne une liste métier non
	 * {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code convertirEtDedoublonner(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide, le parent persistant,
		 * et un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * - l'objet métier possède un libellé exploitable ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche enfant Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(vide après filtrage) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindAllByParentVideApresFiltrage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide, le parent persistant,
		 * et une réponse Gateway enfant ne contenant aucun objet métier
		 * exploitable après filtrage.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final List<SousTypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - le Gateway enfant retourne une liste non null
		 *   ne contenant aucun objet métier exploitable.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(records);

		/* ACT :
		 * exécute la recherche des objets métier enfants via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide après filtrage ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche enfant a bien été déléguée
		 * avec le parent persistant retrouvé.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(OK) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>transmet ce parent persistant au Gateway SousTypeProduit ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_NOMINAL)
	@Test
	public void testFindAllByParentNominal() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide, le parent persistant,
		 * une réponse Gateway contenant :
		 * - deux objets métier exploitables ;
		 * - un élément null à filtrer ;
		 * - un doublon à dédoublonner côté DTO ;
		 * et un captor pour contrôler précisément le parent transmis
		 * au Gateway SousTypeProduit.
		 */
		final TypeProduitDTO.InputDTO parentDto 
			= new TypeProduitDTO.InputDTO(BAZAR);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit stpOutillage 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		stpOutillage.setIdSousTypeProduit(1L);
		
		final SousTypeProduit stpVetement 
			= new SousTypeProduit(VETEMENT, parentPersistant);
		stpVetement.setIdSousTypeProduit(2L);
		
		final SousTypeProduit stpOutillageDoublon 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		stpOutillageDoublon.setIdSousTypeProduit(1L);
		
		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - gateway.findAllByParent(...) retourne des objets métier
		 *   dans un ordre non trié, avec un null et un doublon côté DTO.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(
						stpVetement,
						null,
						stpOutillage,
						stpOutillageDoublon));

		/* ACT :
		 * exécute la recherche par parent via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que le SERVICE METIER UC a recherché le parent
		 * puis a transmis au Gateway enfant le parent persistant retrouvé.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(captor.capture());

		final TypeProduit parentTransmis = captor.getValue();

		assertThat(parentTransmis).isNotNull();
		assertThat(parentTransmis.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(parentTransmis.getIdTypeProduit()).isEqualTo(1L);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par parent puis libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, VETEMENT);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________	

	
	
	// ========================== findByDTO ===============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(null) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_NULL)
	@Test
	public void testFindByDTONull() throws Exception {

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

		/* ACT :
		 * exécute la recherche par DTO avec un DTO null.
		 */
		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_OBJ_NULL ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent blank) :</p>
	 * <ul>
	 * <li>détecte que le parent porté par le DTO a un libellé blank ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_PARENT_BLANK)
	@Test
	public void testFindByDTOParentBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le parent à un libellé blank.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				ESPACES, DN_FIND_BY_DTO_ENFANT);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que le parent blank est refusé avant
		 * toute recherche Gateway.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint la recherche du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway TypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_PARENT_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * la recherche du parent persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message pendant la recherche
		 * du parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que l'échec de recherche parent bloque
		 * toute recherche des objets métier enfants.
		 */
		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint la recherche du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * le Gateway TypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_PARENT_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * la recherche du parent persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message pendant la recherche
		 * du parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que l'échec de recherche parent bloque
		 * toute recherche des objets métier enfants.
		 */
		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent absent) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>détecte que le parent est absent du stockage ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_PARENT_ABSENT)
	@Test
	public void testFindByDTOParentAbsent() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide absent du stockage
		 * selon le Gateway parent mocké.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * le Gateway parent ne retrouve aucun parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(null);

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'absence de parent :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE ;
		 * - bloque la recherche enfant.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent non persistant) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>détecte que le parent retrouvé ne porte pas
	 * d'identifiant persistant ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_PARENT_NON_PERSISTANT)
	@Test
	public void testFindByDTOParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO parent valide et un parent retrouvé
		 * mais dépourvu d'identifiant persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentNonPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * le Gateway parent retourne un objet sans identifiant.
		 */
		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentNonPersistant);

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'un parent non persistant :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE ;
		 * - bloque la recherche enfant.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(enfants gateway KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_ENFANTS_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique du Gateway enfant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message pendant la recherche
		 * des objets métier enfants.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(enfants gateway KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * le Gateway SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_ENFANTS_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique sans message du Gateway enfant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message pendant la recherche
		 * des objets métier enfants.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway retourne null) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>détecte que le Gateway SousTypeProduit retourne {@code null} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByDTOGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(null);

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'une réponse enfant null du Gateway :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(vide) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>détecte que le Gateway SousTypeProduit retourne une liste vide ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_VIDE)
	@Test
	public void testFindByDTOVide() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une réponse Gateway enfant vide.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(new ArrayList<>());

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'une liste enfant vide :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(vide après filtrage) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>ne trouve aucun objet métier exploitable ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindByDTOVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une réponse Gateway enfant ne contenant aucun objet métier
		 * exploitable après filtrage.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final List<SousTypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(records);

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'une réponse ne contenant que des nulls :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(introuvable dans la liste) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>atteint l'appel
	 * {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>parcourt les enfants exploitables du parent ;</li>
	 * <li>ne trouve aucun enfant correspondant au libellé demandé ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_INTROUVABLE_DANS_LISTE)
	@Test
	public void testFindByDTOIntrouvableDansListe() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une réponse Gateway enfant ne contenant pas le libellé demandé.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final SousTypeProduit autre 
			= new SousTypeProduit(DN_FIND_BY_DTO_AUTRE, parentPersistant);
		autre.setIdSousTypeProduit(DN_FIND_BY_DTO_ID_AUTRE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(autre));

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'un enfant introuvable dans la liste :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_VIDE.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'enfant correspondant au couple [parent, libellé] ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOSousTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * - l'objet métier possède le libellé recherché ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getSousTypeProduit())
				.thenReturn(DN_FIND_BY_DTO_ENFANT);
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'enfant correspondant au couple [parent, libellé] ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOSousTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final SousTypeProduit sousTypeProduit = mock(SousTypeProduit.class);
		
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(sousTypeProduit));

		/*
		 * Configuration du Mock :
		 * - l'objet métier possède le libellé recherché ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getSousTypeProduit())
				.thenReturn(DN_FIND_BY_DTO_ENFANT);
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(OK) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>transmet ce parent persistant au Gateway SousTypeProduit ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retrouve l'enfant correspondant au couple [parent, libellé] ;</li>
	 * <li>convertit l'objet métier trouvé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant,
	 * le parent et le libellé attendus ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_NOMINAL)
	@Test
	public void testFindByDTONominal() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * une réponse Gateway contenant un autre objet métier,
		 * un null et l'objet métier recherché.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				DN_FIND_BY_DTO_PARENT, DN_FIND_BY_DTO_ENFANT);
		
		final TypeProduit parentPersistant 
			= new TypeProduit(DN_FIND_BY_DTO_PARENT);
		parentPersistant.setIdTypeProduit(DN_FIND_BY_DTO_ID_PARENT);
		
		final SousTypeProduit autre 
			= new SousTypeProduit(DN_FIND_BY_DTO_AUTRE, parentPersistant);
		autre.setIdSousTypeProduit(DN_FIND_BY_DTO_ID_AUTRE);
		
		final SousTypeProduit cible 
			= new SousTypeProduit(DN_FIND_BY_DTO_ENFANT, parentPersistant);
		cible.setIdSousTypeProduit(DN_FIND_BY_DTO_ID_CIBLE);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final SousTypeProduitGatewayIService gateway 
			= mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway 
			= mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service 
			= new SousTypeProduitCuService(gateway, typeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent persistant est retrouvé ;
		 * - gateway.findAllByParent(...) retourne des objets métier
		 *   dans un ordre non trié, avec un null et la cible attendue.
		 */
		when(typeProduitGateway.findByLibelle(DN_FIND_BY_DTO_PARENT))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(autre, null, cible));

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier attendu ;
		 * - porte le parent attendu ;
		 * - porte le libellé métier recherché ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit())
				.isEqualTo(DN_FIND_BY_DTO_ID_CIBLE);
		assertThat(retour.getTypeProduit())
				.isEqualTo(DN_FIND_BY_DTO_PARENT);
		assertThat(retour.getSousTypeProduit())
				.isEqualTo(DN_FIND_BY_DTO_ENFANT);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(typeProduitGateway, times(1))
				.findByLibelle(DN_FIND_BY_DTO_PARENT);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________

	
		
	// =========================== findById ===============================
	
	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'appelle pas le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : null + message MESSAGE_PARAM_NULL")
	public void testFindByIdNull() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + id")
	public void testFindByIdIntrouvable() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 12L;

		when(gateway.findById(id)).thenReturn(null);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(KO technique avec message) : panne technique remontée
	 * par le Gateway.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(KO technique avec message) : RuntimeException + message rationalisé")
	public void testFindByIdErreurTechniqueAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 13L;

		when(gateway.findById(id))
				.thenThrow(new RuntimeException(MESSAGE_GATEWAY));

		assertThatThrownBy(() -> service.findById(id))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(MESSAGE_GATEWAY);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(KO technique sans message) : panne technique remontée
	 * par le Gateway.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(KO technique sans message) : RuntimeException + message rationalisé")
	public void testFindByIdErreurTechniqueSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 14L;

		when(gateway.findById(id)).thenThrow(new RuntimeException());

		assertThatThrownBy(() -> service.findById(id))
				.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(ok) : succès nominal de recherche par identifiant.</p>
	 * <ul>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(ok) : retourne OutputDTO cohérent + message exact de succès")
	public void testFindByIdOk() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 3L;

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit stp = new SousTypeProduit(OUTILLAGE, parent);
		stp.setIdSousTypeProduit(id);

		when(gateway.findById(id)).thenReturn(stp);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(id);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	
	
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testUpdateNull() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testUpdateBlank() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testUpdateParentBlank() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche parent technique KO avec message) :
	 * panne technique pendant la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche parent KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testUpdateRechercheParentTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche parent technique KO sans message) :
	 * panne technique sans message pendant
	 * la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche parent KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateRechercheParentTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testUpdateParentAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent non persistant) : le parent retrouvé
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent non persistant) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testUpdateParentNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		parentNonPersistant.setIdTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche enfants technique KO avec message) :
	 * panne technique pendant la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche enfants KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testUpdateRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche enfants technique KO sans message) :
	 * panne technique sans message pendant
	 * la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche enfants KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(stockage null pendant ré-identification) :
	 * le Gateway retourne {@code null}
	 * pour les enfants du parent persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testUpdateStockageNullPendantReidentification() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(null);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testUpdateIntrouvable() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(22L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null));

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(non persistant) : l'objet ré-identifié
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(modification technique KO avec message) :
	 * le Gateway échoue pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + détail technique</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * et du parent persistant dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(modification KO technique avec message) : exception relancée + message MESSAGE_MODIF_KO + détail")
	public void testUpdateModificationTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(41L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(41L);
		assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(modification technique KO sans message) :
	 * le Gateway échoue sans message
	 * pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(modification KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateModificationTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(42L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(42L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(gateway null) : le Gateway retourne {@code null}
	 * après délégation de modification.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO} + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(gateway null) : null + message MESSAGE_MODIF_KO + libellé")
	public void testUpdateGatewayNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(43L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(null);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_KO + OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(43L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(retour non persistant) :
	 * le Gateway retourne un objet modifié
	 * dont l'identifiant est redevenu {@code null}.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(retour non persistant) : IllegalStateException + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateRetourNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(44L);

		final SousTypeProduit modifie = new SousTypeProduit(OUTILLAGE, parentPersistant);
		modifie.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(44L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * et du parent persistant dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(ok) : OutputDTO cohérent + message MESSAGE_MODIF_OK + libellé + ID réinjecté")
	public void testUpdateOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(45L);

		final SousTypeProduit modifie = new SousTypeProduit(OUTILLAGE, parentPersistant);
		modifie.setIdSousTypeProduit(45L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(45L);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_OK + OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(45L);
		assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________	

	
	
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testDeleteNull() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testDeleteBlank() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testDeleteParentBlank() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche parent technique KO avec message) :
	 * panne technique pendant la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche parent KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheParentTechniqueKoAvecMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche parent technique KO sans message) :
	 * panne technique sans message pendant la recherche
	 * du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche parent KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheParentTechniqueKoSansMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testDeleteParentAbsent() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent non persistant) : le parent retrouvé
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent non persistant) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testDeleteParentNonPersistant() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		parentNonPersistant.setIdTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche enfants technique KO avec message) :
	 * panne technique pendant la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche enfants KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheEnfantsTechniqueKoAvecMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche enfants technique KO sans message) :
	 * panne technique sans message pendant la recherche
	 * des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche enfants KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheEnfantsTechniqueKoSansMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(stockage null pendant ré-identification) :
	 * le Gateway retourne {@code null}
	 * pour les enfants du parent persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testDeleteStockageNullPendantReidentification()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(null);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>ne lève pas d'exception</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(introuvable) : aucune exception + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testDeleteIntrouvable() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(22L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(non persistant) : l'objet ré-identifié
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testDeleteNonPersistant() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(KO technique de suppression avec message) :
	 * le Gateway échoue pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret + message technique</li>
	 * <li>délègue bien la destruction
	 * sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique avec message) : exception relancée + message MESSAGE_DELETE_KO + détail technique")
	public void testDeleteTechniqueKoAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(20L);

		final Exception ex = new Exception(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		doThrow(ex).when(gateway).delete(existant);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(KO technique de suppression sans message) :
	 * le Gateway échoue sans message pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue bien la destruction
	 * sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteTechniqueKoSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(21L);

		final Exception ex = new Exception();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		doThrow(ex).when(gateway).delete(existant);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>délègue la destruction
	 * sur l'objet persistant retrouvé</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_OK}
	 * + libellé</li>
	 * <li>vérifie bien la ré-identification
	 * sur le couple [parent, libellé]</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : suppression déléguée + message MESSAGE_DELETE_OK + couple [parent, libellé]")
	public void testDeleteOk() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(30L);

		final SousTypeProduit cible = new SousTypeProduit(OUTILLAGE, parentPersistant);
		cible.setIdSousTypeProduit(31L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null, cible));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_OK
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________	

	
	
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>count(KO technique avec message) :
	 * le Gateway échoue pendant le comptage
	 * avec un message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE + détail")
	public void testCountTechniqueKoAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.count()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(KO technique sans message) :
	 * le Gateway échoue pendant le comptage
	 * sans message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCountTechniqueKoSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.count()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(retour négatif) :
	 * le Gateway retourne une valeur incohérente
	 * pour un comptage observable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne un message technique explicite</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(retour négatif) : IllegalStateException + message technique explicite")
	public void testCountRetourNegatifIncoherent() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(-1L);

		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ "comptage négatif incohérent : -1");

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(0) : aucun résultat en stockage.</p>
	 * <ul>
	 * <li>retourne {@code 0}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(0) : retourne 0 + message MESSAGE_RECHERCHE_VIDE")
	public void testCountZero() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(0L);

		final long retour = service.count();

		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(positif) : succès nominal du comptage.</p>
	 * <ul>
	 * <li>retourne le comptage exact</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(positif) : retourne le comptage exact + message MESSAGE_RECHERCHE_OK")
	public void testCountPositif() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(42L);

		final long retour = service.count();

		assertThat(retour).isEqualTo(42L);
		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du service Mock.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>n'interagit jamais avec le Gateway SousTypeProduit</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(initial) : retourne null + aucune interaction gateway")
	public void testGetMessageInitialNull() {

		// ===================== ARRANGE =====================
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ======================= ACT =======================
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message).isNull();
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>getMessage(après erreur locale) :
	 * retourne le message courant
	 * positionné par une erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>après {@code creer(null)},
	 * retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway SousTypeProduit</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après erreur locale) : retourne MESSAGE_CREER_NULL")
	public void testGetMessageApresErreurLocale() throws Exception {

		// ===================== ARRANGE =====================
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ======================= ACT =======================
		service.creer(null);
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>getMessage(après succès vide) :
	 * retourne le message courant
	 * positionné par un comptage à zéro.</p>
	 * <ul>
	 * <li>après {@code count() == 0},
	 * retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>délègue une seule fois au Gateway SousTypeProduit</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès vide) : retourne MESSAGE_RECHERCHE_VIDE")
	public void testGetMessageApresCountZero() throws Exception {

		// ===================== ARRANGE =====================
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(0L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>getMessage(après succès positif) :
	 * retourne le message courant
	 * positionné par un comptage positif.</p>
	 * <ul>
	 * <li>après {@code count() > 0},
	 * retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>délègue une seule fois au Gateway SousTypeProduit</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès positif) : retourne MESSAGE_RECHERCHE_OK")
	public void testGetMessageApresCountPositif() throws Exception {

		// ===================== ARRANGE =====================
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(42L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isEqualTo(42L);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) :
	 * une opération plus récente
	 * écrase bien le message précédent.</p>
	 * <ul>
	 * <li>après une erreur locale,
	 * le message vaut d'abord
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>après un {@code count()} positif,
	 * le message courant devient
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>délègue une seule fois au Gateway SousTypeProduit</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(dernier message gagne) : le message le plus récent écrase le précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		// ===================== ARRANGE =====================
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(1L);

		// ======================= ACT =======================
		service.creer(null);
		final String messageErreur = service.getMessage();

		final long retour = service.count();
		final String messageFinal = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(messageErreur)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);
		assertThat(retour).isEqualTo(1L);
		assertThat(messageFinal)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	

}
