/* ********************************************************************* */
/* ********************* TEST MOCKITO METIER UC ************************ */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOSousTypeProduit;
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
	 * "IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String ILLEGAL_STATE_EXCEPTION_MESSAGE 
		= "IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";
	
	/**
	 * "null + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String NULL_MESSAGE_RECHERCHE_VIDE
			= "null + MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String EXCEPTION_PARAM_BLANK_MESSAGE 
		= "ExceptionParametreBlank + MESSAGE_PARAM_BLANK";


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
	 * "findById"
	 */
	public static final String TAG_FIND_BY_ID = "findById";
	
	/**
	 * "update"
	 */
	public static final String TAG_UPDATE = "update";
	
	/**
	 * "delete"
	 */
	public static final String TAG_DELETE = "delete";
	
	/**
	 * "count"
	 */
	public static final String TAG_COUNT = "count";
	
	/**
	 * "getMessage"
	 */
	public static final String TAG_GET_MESSAGE = "getMessage";

	/**
	 * "creer(null) : MESSAGE_CREER_NULL_KO + aucune interaction Gateway"
	 */
	public static final String DISPLAY_NAME_CREER_NULL
			= "creer(null) : MESSAGE_CREER_NULL_KO "
					+ "+ aucune interaction Gateway";
	
	/**
	 * "creer(blank) : ExceptionParametreBlank + MESSAGE_CREER_LIBELLE_BLANK_KO"
	 */
	public static final String DISPLAY_NAME_CREER_BLANK
			= "creer(blank) : ExceptionParametreBlank "
					+ "+ MESSAGE_CREER_LIBELLE_BLANK_KO";
	
	/**
	 * "creer(libellé parent blank) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_BLANK
			= "creer(libellé parent blank) : IllegalStateException "
					+ "+ MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO";
	
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
	 * "creer(parent absent) : IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_ABSENT
			= "creer(parent absent) : IllegalStateException "
					+ "+ MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";
	
	/**
	 * "creer(parent non persistant) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_CREER_PARENT_NON_PERSISTANT
			= "creer(parent non persistant) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "creer(doublon) : ExceptionDoublon + aucune création Gateway"
	 */
	public static final String DISPLAY_NAME_CREER_DOUBLON
			= "creer(doublon) : ExceptionDoublon "
					+ "+ aucune création Gateway";
	
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
	 * MESSAGE_CREER_GATEWAY_KO"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_RETOUR_NULL
			= "creer(gateway.creer retourne null) : "
					+ "MESSAGE_CREER_GATEWAY_KO";
	
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
	 * "creer(conversion OutputDTO retourne null) :
	 * MESSAGE_CREER_CONVERSION_KO"
	 */
	public static final String DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_RETOUR_NULL
			= "creer(conversion OutputDTO retourne null) : "
					+ "MESSAGE_CREER_CONVERSION_KO";
	
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
	 * IllegalStateException + RECHERCHE_PARENT_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL
			= "findAllByParent(null) : "
					+ "IllegalStateException + RECHERCHE_PARENT_NULL";
	
	/**
	 * "findAllByParent(libellé parent blank) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK
			= "findAllByParent(libellé parent blank) : "
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
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT
			= "findAllByParent(parent absent) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "findAllByParent(parent non persistant) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
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
	 * "findByDTO(libellé parent blank) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_PARENT_BLANK
			= "findByDTO(libellé parent blank) : "
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
	
	/**
	 * "findById(null) :
	 * null + MESSAGE_PARAM_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_NULL
			= "findById(null) : "
					+ "null + MESSAGE_PARAM_NULL";
	
	/**
	 * "findById(introuvable) :
	 * null + MESSAGE_OBJ_INTROUVABLE + id"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_INTROUVABLE
			= "findById(introuvable) : "
					+ "null + MESSAGE_OBJ_INTROUVABLE + id";
	
	/**
	 * "findById(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_AVEC_MESSAGE
			= "findById(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findById(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_SANS_MESSAGE
			= "findById(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findById(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findById(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "findById(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findById(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "findById(nominal) :
	 * OutputDTO + MESSAGE_SUCCES_RECHERCHE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_NOMINAL
			= "findById(nominal) : "
					+ "OutputDTO + MESSAGE_SUCCES_RECHERCHE";
	
	/**
	 * "update(null) :
	 * ExceptionParametreNull + MESSAGE_PARAM_NULL"
	 */
	public static final String DISPLAY_NAME_UPDATE_NULL
			= "update(null) : "
					+ "ExceptionParametreNull + MESSAGE_PARAM_NULL";
	
	/**
	 * "update(libellé null) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_UPDATE_LIBELLE_NULL
			= "update(libellé null) : "
					+ EXCEPTION_PARAM_BLANK_MESSAGE;
	
	/**
	 * "update(blank) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_UPDATE_BLANK
			= "update(blank) : "
					+ EXCEPTION_PARAM_BLANK_MESSAGE;
	
	/**
	 * "update(libellé parent blank) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_UPDATE_PARENT_BLANK
			= "update(libellé parent blank) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "update(parent gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_UPDATE_PARENT_GATEWAY_KO_AVEC_MESSAGE
			= "update(parent gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "update(parent gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_PARENT_GATEWAY_KO_SANS_MESSAGE
			= "update(parent gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "update(parent absent) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_UPDATE_PARENT_ABSENT
			= "update(parent absent) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "update(parent non persistant) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_UPDATE_PARENT_NON_PERSISTANT
			= "update(parent non persistant) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "update(enfants gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_UPDATE_ENFANTS_GATEWAY_KO_AVEC_MESSAGE
			= "update(enfants gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "update(enfants gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_ENFANTS_GATEWAY_KO_SANS_MESSAGE
			= "update(enfants gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "update(gateway enfants retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_UPDATE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION
			= "update(gateway enfants retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "update(introuvable) :
	 * null + MESSAGE_OBJ_INTROUVABLE"
	 */
	public static final String DISPLAY_NAME_UPDATE_INTROUVABLE
			= "update(introuvable) : "
					+ "null + MESSAGE_OBJ_INTROUVABLE";
	
	/**
	 * "update(non persistant) :
	 * ExceptionNonPersistant + MESSAGE_OBJ_NON_PERSISTE"
	 */
	public static final String DISPLAY_NAME_UPDATE_NON_PERSISTANT
			= "update(non persistant) : "
					+ "ExceptionNonPersistant + MESSAGE_OBJ_NON_PERSISTE";
	
	/**
	 * "update(modification KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_KO_AVEC_MESSAGE
			= "update(modification KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "update(modification KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_KO_SANS_MESSAGE
			= "update(modification KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "update(gateway.update retourne null) :
	 * null + MESSAGE_MODIF_KO"
	 */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NULL
			= "update(gateway.update retourne null) : "
					+ "null + MESSAGE_MODIF_KO";
	
	/**
	 * "update(gateway.update retourne non persistant) :
	 * IllegalStateException + MESSAGE_OBJ_NON_PERSISTE"
	 */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT
			= "update(gateway.update retourne non persistant) : "
					+ "IllegalStateException + MESSAGE_OBJ_NON_PERSISTE";
	
	/**
	 * "update(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "update(conversion OutputDTO KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "update(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "update(conversion OutputDTO KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "update(nominal) :
	 * OutputDTO + MESSAGE_MODIF_OK"
	 */
	public static final String DISPLAY_NAME_UPDATE_NOMINAL
			= "update(nominal) : "
					+ "OutputDTO + MESSAGE_MODIF_OK";
	
	/**
	 * "delete(null) :
	 * ExceptionParametreNull + MESSAGE_PARAM_NULL"
	 */
	public static final String DISPLAY_NAME_DELETE_NULL
			= "delete(null) : "
					+ "ExceptionParametreNull + MESSAGE_PARAM_NULL";
	
	/**
	 * "delete(libellé null) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_DELETE_LIBELLE_NULL
			= "delete(libellé null) : "
					+ EXCEPTION_PARAM_BLANK_MESSAGE;
	
	/**
	 * "delete(blank) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_DELETE_BLANK
			= "delete(blank) : "
					+ EXCEPTION_PARAM_BLANK_MESSAGE;
	
	/**
	 * "delete(libellé parent blank) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_DELETE_PARENT_BLANK
			= "delete(libellé parent blank) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "delete(parent gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_DELETE_PARENT_GATEWAY_KO_AVEC_MESSAGE
			= "delete(parent gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "delete(parent gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_DELETE_PARENT_GATEWAY_KO_SANS_MESSAGE
			= "delete(parent gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "delete(parent absent) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_DELETE_PARENT_ABSENT
			= "delete(parent absent) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "delete(parent non persistant) :
	 * IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO"
	 */
	public static final String DISPLAY_NAME_DELETE_PARENT_NON_PERSISTANT
			= "delete(parent non persistant) : "
					+ ILLEGAL_STATE_EXCEPTION_MESSAGE;
	
	/**
	 * "delete(enfants gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_DELETE_ENFANTS_GATEWAY_KO_AVEC_MESSAGE
			= "delete(enfants gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "delete(enfants gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_DELETE_ENFANTS_GATEWAY_KO_SANS_MESSAGE
			= "delete(enfants gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "delete(gateway enfants retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_DELETE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION
			= "delete(gateway enfants retourne null) : "
					+ EXCEPTION_STOCKAGE_VIDE_MESSAGE;
	
	/**
	 * "delete(introuvable) :
	 * MESSAGE_OBJ_INTROUVABLE + libellé"
	 */
	public static final String DISPLAY_NAME_DELETE_INTROUVABLE
			= "delete(introuvable) : "
					+ "MESSAGE_OBJ_INTROUVABLE + libellé";
	
	/**
	 * "delete(non persistant) :
	 * ExceptionNonPersistant + MESSAGE_OBJ_NON_PERSISTE"
	 */
	public static final String DISPLAY_NAME_DELETE_NON_PERSISTANT
			= "delete(non persistant) : "
					+ "ExceptionNonPersistant + MESSAGE_OBJ_NON_PERSISTE";
	
	/**
	 * "delete(destruction KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_DELETE_DESTRUCTION_KO_AVEC_MESSAGE
			= "delete(destruction KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "delete(destruction KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_DELETE_DESTRUCTION_KO_SANS_MESSAGE
			= "delete(destruction KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "delete(nominal) :
	 * MESSAGE_DELETE_OK"
	 */
	public static final String DISPLAY_NAME_DELETE_NOMINAL
			= "delete(nominal) : "
					+ "MESSAGE_DELETE_OK";
	
	/**
	 * "count(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KO_AVEC_MESSAGE
			= "count(gateway KO avec message) : "
					+ EXCEPTION_PROPAGEE_MESSAGE;
	
	/**
	 * "count(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KO_SANS_MESSAGE
			= "count(gateway KO sans message) : "
					+ FALLBACK;
	
	/**
	 * "count(retour négatif) :
	 * IllegalStateException + message technique explicite"
	 */
	public static final String DISPLAY_NAME_COUNT_RETOUR_NEGATIF
			= "count(retour négatif) : "
					+ "IllegalStateException + message technique explicite";
	
	/**
	 * "count(0) :
	 * 0 + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_COUNT_ZERO
			= "count(0) : "
					+ "0 + MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "count(nominal) :
	 * comptage exact + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_COUNT_NOMINAL
			= "count(nominal) : "
					+ "comptage exact + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "getMessage(initial) :
	 * null + aucune interaction Gateway"
	 */
	public static final String DISPLAY_NAME_GET_MESSAGE_INITIAL_NULL
			= "getMessage(initial) : "
					+ "null + aucune interaction Gateway";
	
	/**
	 * "getMessage(après erreur locale) :
	 * MESSAGE_CREER_NULL_KO"
	 */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_ERREUR_LOCALE
			= "getMessage(après erreur locale) : "
					+ "MESSAGE_CREER_NULL_KO";
	
	/**
	 * "getMessage(après count 0) :
	 * MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_ZERO
			= "getMessage(après count 0) : "
					+ "MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "getMessage(après count nominal) :
	 * MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_NOMINAL
			= "getMessage(après count nominal) : "
					+ "MESSAGE_RECHERCHE_OK";
	
	/**
	 * "getMessage(dernier message gagne) :
	 * le message le plus récent écrase le précédent"
	 */
	public static final String DISPLAY_NAME_GET_MESSAGE_DERNIER_MESSAGE_GAGNE
			= "getMessage(dernier message gagne) : "
					+ "le message le plus récent écrase le précédent";
	

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
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL_KO} ;</li>
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
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
	 * </ul>
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
		 * - émet le message MESSAGE_CREER_LIBELLE_BLANK_KO
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé parent blank) :</p>
	 * <ul>
	 * <li>contrôle localement le libellé du parent ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO} ;</li>
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
		 * - émet le message MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent KO avec message) :</p>
	 * <ul>
	 * <li>atteint la récupération du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway parent ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO}
	 * + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
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
		 * prépare un DTO valide pour atteindre réellement
		 * la recherche du parent persistant.
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
		 * simule une panne technique de typeProduitGateway.findByLibelle(...)
		 * pendant la récupération du parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec parent.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO
						+ MESSAGE_GATEWAY);

		/* Garantit que seule la recherche parent a été atteinte. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent KO sans message) :</p>
	 * <ul>
	 * <li>atteint la récupération du parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception sans message levée par le Gateway parent ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
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
		 * prépare un DTO valide pour atteindre réellement
		 * la recherche du parent persistant.
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
		 * simule une panne technique sans message de
		 * typeProduitGateway.findByLibelle(...).
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec parent.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que seule la recherche parent a été atteinte. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent absent) :</p>
	 * <ul>
	 * <li>interroge le Gateway parent ;</li>
	 * <li>détecte que le parent n'existe pas dans le stockage ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
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
		 * prépare un DTO valide dont le parent est introuvable.
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
		 * simule un parent non trouvé dans le stockage.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) refuse un parent absent. */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* Garantit que le scénario s'arrête après la recherche parent. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent non persistant) :</p>
	 * <ul>
	 * <li>interroge le Gateway parent ;</li>
	 * <li>détecte que le parent retourné ne possède pas d'identifiant ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
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
		 * prépare un DTO valide dont le parent retourné par le Gateway
		 * ne porte pas d'identifiant persistant.
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
		 * simule un parent trouvé mais non persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) refuse un parent non persistant. */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* Garantit que le scénario s'arrête après la recherche parent. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(doublon) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant ;</li>
	 * <li>contrôle l'unicité via {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} interroge le Gateway SousTypeProduit via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>jette une {@link ExceptionDoublon} si un même libellé existe
	 * déjà sous le même parent ;</li>
	 * <li>émet le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
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
		 * existe déjà dans le stockage selon les Gateways mockés.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit doublon 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		doublon.setIdSousTypeProduit(1L);
		
		final List<SousTypeProduit> existants = new ArrayList<SousTypeProduit>();
		existants.add(doublon);
		
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
		 * - le parent est persistant ;
		 * - le Gateway enfant retourne un objet portant le même couple
		 *   [parent, libellé].
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(existants);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionDoublon ;
		 * - émet le message MESSAGE_CREER_DOUBLON_KO + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
						+ OUTILLAGE);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_CREER_DOUBLON_KO
						+ OUTILLAGE);

		/* Garantit que le parent et le contrôle d'unicité ont été exécutés,
		 * et que la création n'a jamais été déléguée au Gateway enfant.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(contrôle de doublon KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant ;</li>
	 * <li>atteint le bloc {@code try/catch} qui appelle
	 * {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} appelle
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
	 * + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_DOUBLON_AVEC_MESSAGE)
	@Test
	public void testCreerControleDoublonKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
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
		 * - le parent est persistant ;
		 * - le contrôle de doublon échoue techniquement.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec
		 * du contrôle d'unicité.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ MESSAGE_GATEWAY);

		/* Garantit que la création n'a jamais été appelée. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(contrôle de doublon KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant ;</li>
	 * <li>atteint le bloc {@code try/catch} qui appelle
	 * {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} appelle
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception sans message levée par
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_DOUBLON_SANS_MESSAGE)
	@Test
	public void testCreerControleDoublonKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
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
		 * - le parent est persistant ;
		 * - le contrôle de doublon échoue techniquement sans message.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création n'a jamais été appelée. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer KO avec message) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_AVEC_MESSAGE)
	@Test
	public void testCreerGatewayCreerKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
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
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - la création technique échoue avec message.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
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
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer KO sans message) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception sans message levée par
	 * {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_SANS_MESSAGE)
	@Test
	public void testCreerGatewayCreerKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
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
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - la création technique échoue sans message.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(gateway.creer(any(SousTypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer retourne null) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>positionne le message
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_GATEWAY_KO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_CREER_RETOUR_NULL)
	@Test
	public void testCreerGatewayCreerKORetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
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
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - gateway.creer(...) retourne null.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le succès apparent
		 * et refuse une réponse technique null.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		/* Garantit que le scénario a atteint la création Gateway,
		 * puis que l'anomalie null est traitée côté UC.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE)
	@Test
	public void testCreerConversionOutputDTOKOAvecMessage() throws Exception {

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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
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
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_SANS_MESSAGE)
	@Test
	public void testCreerConversionOutputDTOKOSansMessage() throws Exception {

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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
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
						SousTypeProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO retourne null) :</p>
	 * <ul>
	 * <li>récupère le parent persistant ;</li>
	 * <li>contrôle l'absence de doublon ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>force ponctuellement la méthode static de conversion à retourner
	 * {@code null} avec un {@link MockedStatic}, parce que le convertisseur
	 * réel ne retourne {@code null} que si son paramètre est {@code null},
	 * alors que le SERVICE METIER UC intercepte déjà un retour {@code null}
	 * du Gateway avant la conversion ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>positionne le message utilisateur
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_CONVERSION_KO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_RETOUR_NULL)
	@Test
	public void testCreerConversionOutputDTORetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et non doublon.
		 *
		 * Le Gateway retourne ensuite un objet métier créé.
		 * Le cas défensif à tester est uniquement le retour null
		 * de la conversion finale.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit cree 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		cree.setIdSousTypeProduit(1L);
		
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
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du MockedStatic :
		 * - le convertisseur réel est une classe utilitaire static ;
		 * - avec un objet métier non null, il ne retourne normalement pas null ;
		 * - gateway.creer(...) ne peut pas retourner null ici,
		 *   car le SERVICE METIER UC s'arrêterait avant la conversion ;
		 * - le MockedStatic est donc limité à ce test pour atteindre
		 *   la branche défensive "dto == null" du SERVICE METIER UC.
		 */
		try (MockedStatic<ConvertisseurMetierToOutputDTOSousTypeProduit> mockedStatic
				= mockStatic(ConvertisseurMetierToOutputDTOSousTypeProduit.class)) {
			
			/*
			 * Configuration du MockedStatic :
			 * la conversion finale en OutputDTO retourne null.
			 */
			mockedStatic.when(
					() -> ConvertisseurMetierToOutputDTOSousTypeProduit
							.convert(cree))
					.thenReturn(null);

			/* ACT - ASSERT */
			/* Garantit que le SERVICE METIER UC refuse
			 * une conversion finale null.
			 */
			assertThatThrownBy(() -> service.creer(dto))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage(
							SousTypeProduitICuService
									.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le message utilisateur correspond
			 * au cas contractuel "conversion retourne null".
			 */
			assertThat(service.getMessage())
					.isEqualTo(
							SousTypeProduitICuService
									.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le scénario a atteint la création Gateway
			 * avant le contrôle du retour null de conversion.
			 */
			verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
			verify(gateway, times(1)).findByLibelle(OUTILLAGE);
			verify(gateway, times(1)).creer(any(SousTypeProduit.class));
			
			/* Garantit que le MockedStatic a été strictement limité
			 * à la conversion finale attendue par le SERVICE METIER UC.
			 */
			mockedStatic.verify(
					() -> ConvertisseurMetierToOutputDTOSousTypeProduit
							.convert(cree),
					times(1));
		}

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>récupère le parent persistant via
	 * {@code typeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>contrôle l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
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
		 * - findByLibelle(...) sur le Gateway parent retourne le parent
		 *   persistant ;
		 * - findByLibelle(...) sur le Gateway enfant retourne une liste vide
		 *   pour simuler l'absence de doublon fonctionnel ;
		 * - creer(...) retourne l'objet métier réellement créé
		 *   avec l'identifiant généré par le stockage.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findByLibelle(OUTILLAGE))
				.thenReturn(new ArrayList<SousTypeProduit>());
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		/* ACT :
		 * exécute la création via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que les Gateways ont bien été sollicités
		 * dans le scénario attendu :
		 * recherche du parent, contrôle d'unicité, puis création.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
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
		 * au lieu d'une liste non null attendue par le SERVICE METIER UC.
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
						SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
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
						SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
		 * aucun objet métier non null après filtrage.
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
		 * ne contenant aucun objet métier non null.
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
		 * - deux objets métier non null ;
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
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
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
		 * au lieu d'une liste non null attendue par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousString() :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO);

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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
		 * aucun objet métier non null après filtrage.
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
		 * ne contenant aucun objet métier non null.
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
		 * dont les seuls libellés non null sont blank.
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
		 * - deux objets métier non null ;
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
		 * - contient uniquement les libellés non blank ;
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
		 * non null attendu par le SERVICE METIER UC.
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
		 * aucun objet métier non null après filtrage.
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
		 * ne contenant aucun objet métier non null.
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
		 * - deux objets métier non null ;
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
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
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
	 * au lieu d'une liste non null attendue ;</li>
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
		 * au lieu d'une liste non null attendue par le SERVICE METIER UC.
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
	 * contenant un objet métier non null ;</li>
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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
	 * contenant un objet métier non null ;</li>
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
		 * un objet métier non null retenu par filtrerEtTrier(...).
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
	 * ne contenant aucun objet métier non null ;</li>
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
		 * - porte le message utilisateur de recherche en échec.
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
		 * - deux objets métier non null portant le libellé recherché ;
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
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
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
						SousTypeProduitICuService.MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE);

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
	 * au lieu d'une liste non null attendue ;</li>
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
		 * au lieu d'une liste non null attendue par le SERVICE METIER UC.
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
		 * contenant un objet métier non null retenu par filtrerEtTrier(...).
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
		 * contenant un objet métier non null retenu par filtrerEtTrier(...).
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
		 * aucun objet métier non null après filtrage.
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
		 * ne contenant aucun objet métier non null.
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
		 * - deux objets métier non null ;
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
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
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
	 * {@link SousTypeProduitICuService#RECHERCHE_PARENT_NULL} ;</li>
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
		 * - émet le message RECHERCHE_PARENT_NULL contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.findAllByParent(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.RECHERCHE_PARENT_NULL);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.RECHERCHE_PARENT_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findAllByParent(libellé parent blank) :</p>
	 * <ul>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
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
		 * - émet le message MESSAGE_CREER_PARENT_NON_PERSISTANT_KO ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

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
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
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
		 * avec le message utilisateur MESSAGE_CREER_PARENT_NON_PERSISTANT_KO.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

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
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
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
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

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
	 * au lieu d'une liste non null attendue ;</li>
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
		 * - le Gateway enfant retourne null au lieu d'une liste non null attendue.
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
		 * - l'objet métier possède un libellé non blank ;
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
		 * - l'objet métier possède un libellé non blank ;
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
		 * non null après filtrage.
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
		 *   ne contenant aucun objet métier non null.
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
		 * - deux objets métier non null du parent ;
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
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
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
	 * <li>teste le cas où le DTO transmis est null ;</li>
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
		/* Garantit que findByDTO(null) :
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
	 * <p>garantit que findByDTO(libellé parent blank) :</p>
	 * <ul>
	 * <li>teste un DTO dont le parent a un libellé blank ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
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
		 * prépare un DTO non null dont le parent a un libellé blank.
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
		/* Garantit que le parent au libellé blank est refusé avant
		 * toute recherche Gateway.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent gateway KO avec message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via
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
		 * prépare un DTO valide pour déclencher la recherche
		 * du parent persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent gateway KO sans message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via
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
		 * prépare un DTO valide pour déclencher la recherche
		 * du parent persistant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent absent) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent est absent du stockage ;</li>
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
		 * le Gateway parent ne retrouve aucun parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(parent non persistant) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent retrouvé ne porte pas
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
		 * dépourvu d'identifiant persistant.
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
		 * le Gateway parent retourne un objet sans identifiant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(enfants gateway KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
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
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(enfants gateway KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
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
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway retourne null) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>teste le cas où le Gateway SousTypeProduit retourne {@code null} ;</li>
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

		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(vide) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>teste le cas où le Gateway SousTypeProduit retourne une liste vide ;</li>
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

		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(vide après filtrage) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>teste une liste contenant uniquement des éléments {@code null} ;</li>
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
		 * et une réponse Gateway enfant contenant uniquement un null.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(introuvable dans la liste) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>parcourt les enfants non null du parent ;</li>
	 * <li>ne trouve aucun enfant portant le libellé demandé ;</li>
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
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre 
			= new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(2L);
		
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'enfant correspondant au couple [parent, libellé] ;</li>
	 * <li>appelle la conversion finale en {@link OutputDTO}
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
				BAZAR, OUTILLAGE);
		
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
		 * - l'objet métier possède le libellé recherché ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getSousTypeProduit()).thenReturn(OUTILLAGE);
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'enfant correspondant au couple [parent, libellé] ;</li>
	 * <li>appelle la conversion finale en {@link OutputDTO}
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
				BAZAR, OUTILLAGE);
		
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
		 * - l'objet métier possède le libellé recherché ;
		 * - la conversion en OutputDTO lit le parent de l'objet métier ;
		 * - cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getSousTypeProduit()).thenReturn(OUTILLAGE);
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

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(nominal) :</p>
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
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre 
			= new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(2L);
		
		final SousTypeProduit cible 
			= new SousTypeProduit(OUTILLAGE, parentPersistant);
		cible.setIdSousTypeProduit(3L);
		
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
		 * - gateway.findAllByParent(...) retourne un autre objet métier,
		 *   un null et la cible attendue.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
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
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(3L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService
						.MESSAGE_FINDBYDTO_OK);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		
	} // __________________________________________________________________

	
			
	// =========================== findById ===============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(null) :</p>
	 * <ul>
	 * <li>teste le cas où l'identifiant transmis est null ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NULL)
	@Test
	public void testFindByIdNull() throws Exception {

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
		 * exécute la recherche par identifiant avec un ID null.
		 */
		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que findById(null) :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_NULL ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(introuvable) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>teste le cas où le Gateway retourne {@code null} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + identifiant recherché ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_INTROUVABLE)
	@Test
	public void testFindByIdIntrouvable() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide absent du stockage
		 * selon le Gateway mocké.
		 */
		final Long id = 12L;
		
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
		 * pour l'identifiant recherché.
		 */
		when(gateway.findById(id)).thenReturn(null);

		/* ACT :
		 * exécute la recherche par identifiant via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - retourne null ;
		 * - porte le message utilisateur d'introuvabilité.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		/* Garantit que la recherche par identifiant a bien été déléguée. */
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(gateway KO avec message) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide pour atteindre réellement
		 * la délégation gateway.findById(...).
		 */
		final Long id = 13L;
		
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
		 * de l'appel gateway.findById(...).
		 */
		when(gateway.findById(id)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(gateway KO sans message) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide pour atteindre réellement
		 * la délégation gateway.findById(...).
		 */
		final Long id = 14L;
		
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
		 * de l'appel gateway.findById(...).
		 */
		when(gateway.findById(id)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOSousTypeProduit.convert(...)} ;</li>
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
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final Long id = 31L;
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

		when(gateway.findById(id)).thenReturn(sousTypeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
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
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOSousTypeProduit.convert(...)} ;</li>
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
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final Long id = 32L;
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

		when(gateway.findById(id)).thenReturn(sousTypeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(sousTypeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
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
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(OK) :</p>
	 * <ul>
	 * <li>cherche l'objet métier par identifiant via le Gateway
	 * SousTypeProduit ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>convertit l'objet métier trouvé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant,
	 * le parent et le libellé attendus ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NOMINAL)
	@Test
	public void testFindByIdNominal() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide et un objet métier trouvé
		 * par le Gateway.
		 */
		final Long id = 3L;

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit sousTypeProduit =
				new SousTypeProduit(OUTILLAGE, parent);
		sousTypeProduit.setIdSousTypeProduit(id);
		
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
		 * le Gateway SousTypeProduit retourne l'objet métier attendu.
		 */
		when(gateway.findById(id)).thenReturn(sousTypeProduit);

		/* ACT :
		 * exécute la recherche par identifiant via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier attendu ;
		 * - porte le parent attendu ;
		 * - porte le libellé métier attendu ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(id);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_FINDBYDTO_OK);

		/* Garantit que la recherche par identifiant a bien été déléguée. */
		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	
			
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(null) :</p>
	 * <ul>
	 * <li>refuse un DTO de modification {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreNull} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NULL)
	@Test
	public void testUpdateNull() throws Exception {

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
		/* Garantit que service.update(null) :
		 * - jette ExceptionParametreNull ;
		 * - émet MESSAGE_PARAM_NULL ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(libellé null) :</p>
	 * <ul>
	 * <li>lit le libellé de l'objet métier porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_LIBELLE_NULL)
	@Test
	public void testUpdateLibelleNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé de l'objet métier est null.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, null);
		
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
		/* Garantit que le libellé null est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(libellé blank) :</p>
	 * <ul>
	 * <li>lit le libellé de l'objet métier porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé blank ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_BLANK)
	@Test
	public void testUpdateBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé de l'objet métier est blank.
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
		/* Garantit que le libellé blank est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(libellé parent blank) :</p>
	 * <ul>
	 * <li>lit le parent porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce parent dont le libellé est blank ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_BLANK)
	@Test
	public void testUpdateParentBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le parent a un libellé blank.
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
		/* Garantit que le parent au libellé blank est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(parent gateway KO avec message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>propage l'exception technique levée par ce Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
	 * <p>garantit que update(parent gateway KO sans message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>propage l'exception technique sans message levée par ce Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
	 * <p>garantit que update(parent absent) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent est absent du stockage ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_ABSENT)
	@Test
	public void testUpdateParentAbsent() throws Exception {

		/* ARRANGE :
		 * prépare un DTO dont le parent est absent du stockage.
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
		 * le Gateway TypeProduit ne retrouve aucun parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que l'absence du parent bloque la modification. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(parent non persistant) :</p>
	 * <ul>
	 * <li>cherche le parent via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent retrouvé ne porte pas d'identifiant ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_NON_PERSISTANT)
	@Test
	public void testUpdateParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent retrouvé sans identifiant.
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
		 * le Gateway TypeProduit retourne un parent sans identifiant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ACT - ASSERT */
		/* Garantit qu'un parent non persistant bloque la modification. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(enfants gateway KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_ENFANTS_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique du Gateway SousTypeProduit.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message pendant la recherche
		 * des enfants du parent.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(enfants gateway KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
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
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_ENFANTS_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique sans message du Gateway SousTypeProduit.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message pendant la recherche
		 * des enfants du parent.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(gateway enfants retourne null) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>détecte que le Gateway SousTypeProduit retourne {@code null} ;</li>
	 * <li>lève une {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testUpdateStockageNullPendantReidentification()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent persistant.
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le retour null de recherche enfant est refusé. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(introuvable) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>parcourt les enfants non null du parent ;</li>
	 * <li>ne trouve aucun enfant portant le libellé demandé ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé demandé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_INTROUVABLE)
	@Test
	public void testUpdateIntrouvable() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une liste ne contenant pas le libellé demandé.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre =
				new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(2L);
		
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
				.thenReturn(Arrays.asList(autre));

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'objet à modifier absent :
		 * - retourne null ;
		 * - positionne MESSAGE_OBJ_INTROUVABLE + libellé ;
		 * - ne délègue jamais la modification.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(non persistant) :</p>
	 * <ul>
	 * <li>retrouve un enfant portant le libellé demandé ;</li>
	 * <li>teste le cas où cet enfant ne porte pas d'identifiant ;</li>
	 * <li>lève une {@link ExceptionNonPersistant} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé demandé ;</li>
	 * <li>ne délègue jamais la modification au Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NON_PERSISTANT)
	@Test
	public void testUpdateNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet retrouvé sans identifiant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		
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
				.thenReturn(Arrays.asList(existant));

		/* ACT - ASSERT */
		/* Garantit qu'un objet retrouvé sans identifiant est refusé. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(modification KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un objet persistant retrouvé et une panne de modification.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(modification KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_SANS_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un objet persistant retrouvé et une panne sans message.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
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
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(gateway.update retourne null) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>teste le cas où le Gateway retourne {@code null} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé demandé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NULL)
	@Test
	public void testUpdateModificationRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant
		 * et un objet persistant retrouvé.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
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
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(null);

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'un retour null de modification :
		 * - retourne null ;
		 * - positionne MESSAGE_MODIF_KO + libellé.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_KO
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(gateway.update retourne non persistant) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>teste le cas où l'objet retourné ne porte pas d'identifiant ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé demandé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT)
	@Test
	public void testUpdateModificationRetourNonPersistant()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un objet persistant retrouvé et un retour modifié sans identifiant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
		final SousTypeProduit modifie =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		
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
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		/* ACT - ASSERT */
		/* Garantit qu'un retour modifié non persistant est refusé. */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>le Gateway retourne un objet modifié persistant ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet modifié mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
		final SousTypeProduit modifie = mock(SousTypeProduit.class);
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
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		/*
		 * Configuration du Mock :
		 * le retour de modification est persistant,
		 * puis la conversion lit son parent et échoue avec message.
		 */
		when(modifie.getIdSousTypeProduit()).thenReturn(3L);
		when(modifie.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à modifier ;</li>
	 * <li>le Gateway retourne un objet modifié persistant ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un objet modifié mocké dont l'accès au parent
		 * provoque une panne sans message pendant la conversion.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
		final SousTypeProduit modifie = mock(SousTypeProduit.class);
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
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		/*
		 * Configuration du Mock :
		 * le retour de modification est persistant,
		 * puis la conversion lit son parent et échoue sans message.
		 */
		when(modifie.getIdSousTypeProduit()).thenReturn(3L);
		when(modifie.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(OK) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'objet à modifier dans les enfants du parent ;</li>
	 * <li>réinjecte l'identifiant persistant retrouvé dans l'objet envoyé
	 * à {@code gateway.update(...)} ;</li>
	 * <li>délègue la modification au Gateway SousTypeProduit ;</li>
	 * <li>convertit l'objet métier modifié en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant,
	 * le parent et le libellé attendus ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_OK}
	 * + libellé demandé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NOMINAL)
	@Test
	public void testUpdateNominal() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * l'objet persistant retrouvé et l'objet métier modifié.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre =
				new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(2L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(3L);
		
		final SousTypeProduit modifie =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		modifie.setIdSousTypeProduit(3L);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(autre, null, existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que les Gateways ont bien été sollicités
		 * dans l'ordre fonctionnel attendu :
		 * recherche du parent, recherche des enfants, puis modification.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé au Gateway :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant retrouvé ;
		 * - porte le libellé métier issu de l'InputDTO ;
		 * - porte le parent persistant retrouvé via le Gateway TypeProduit.
		 */
		final SousTypeProduit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdSousTypeProduit()).isEqualTo(3L);
		assertThat(envoye.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(envoye.getTypeProduit()).isNotNull();
		assertThat(envoye.getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(envoye.getTypeProduit().getIdTypeProduit()).isEqualTo(1L);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier attendu ;
		 * - porte le parent attendu ;
		 * - porte le libellé métier attendu ;
		 * - expose le message utilisateur de succès de modification.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(3L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_OK
						+ OUTILLAGE);

	} // __________________________________________________________________

	
		
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(null) :</p>
	 * <ul>
	 * <li>refuse un DTO de suppression {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreNull} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NULL)
	@Test
	public void testDeleteNull() throws Exception {

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
		/* Garantit que service.delete(null) :
		 * - jette ExceptionParametreNull ;
		 * - émet MESSAGE_PARAM_NULL ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(libellé null) :</p>
	 * <ul>
	 * <li>lit le libellé de l'objet métier porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_LIBELLE_NULL)
	@Test
	public void testDeleteLibelleNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé de l'objet métier est null.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, null);
		
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
		/* Garantit que le libellé null est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(libellé blank) :</p>
	 * <ul>
	 * <li>lit le libellé de l'objet métier porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé blank ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_BLANK)
	@Test
	public void testDeleteBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé de l'objet métier est blank.
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
		/* Garantit que le libellé blank est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(libellé parent blank) :</p>
	 * <ul>
	 * <li>lit le parent porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce parent dont le libellé est blank ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_BLANK)
	@Test
	public void testDeleteParentBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le parent a un libellé blank.
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
		/* Garantit que le parent au libellé blank est refusé avant toute délégation. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(parent gateway KO avec message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>propage l'exception technique levée par ce Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
	 * <p>garantit que delete(parent gateway KO sans message) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>propage l'exception technique sans message levée par ce Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche du parent.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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
	 * <p>garantit que delete(parent absent) :</p>
	 * <ul>
	 * <li>cherche le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent est absent du stockage ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_ABSENT)
	@Test
	public void testDeleteParentAbsent() throws Exception {

		/* ARRANGE :
		 * prépare un DTO dont le parent est absent du stockage.
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
		 * le Gateway TypeProduit ne retrouve aucun parent persistant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que l'absence du parent bloque la suppression. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(parent non persistant) :</p>
	 * <ul>
	 * <li>cherche le parent via le Gateway TypeProduit ;</li>
	 * <li>teste le cas où le parent retrouvé ne porte pas d'identifiant ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_NON_PERSISTANT)
	@Test
	public void testDeleteParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent retrouvé sans identifiant.
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
		 * le Gateway TypeProduit retourne un parent sans identifiant.
		 */
		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ACT - ASSERT */
		/* Garantit qu'un parent non persistant bloque la suppression. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(enfants gateway KO avec message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway SousTypeProduit ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_ENFANTS_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoAvecMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique du Gateway SousTypeProduit.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message pendant la recherche
		 * des enfants du parent.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(enfants gateway KO sans message) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
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
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_ENFANTS_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoSansMessage()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une panne technique sans message du Gateway SousTypeProduit.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message pendant la recherche
		 * des enfants du parent.
		 */
		when(gateway.findAllByParent(parentPersistant))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(gateway enfants retourne null) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>appelle {@code gateway.findAllByParent(parentPersistant)} ;</li>
	 * <li>détecte que le Gateway SousTypeProduit retourne {@code null} ;</li>
	 * <li>lève une {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testDeleteStockageNullPendantReidentification()
			throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un parent persistant.
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

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentPersistant);
		when(gateway.findAllByParent(parentPersistant)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le retour null de recherche enfant est refusé. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(introuvable) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>parcourt les enfants non null du parent ;</li>
	 * <li>ne trouve aucun enfant portant le libellé demandé ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé demandé ;</li>
	 * <li>ne délègue jamais la suppression au Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_INTROUVABLE)
	@Test
	public void testDeleteIntrouvable() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * et une liste ne contenant pas le libellé demandé.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre =
				new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(2L);
		
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
				.thenReturn(Arrays.asList(autre));

		/* ACT :
		 * exécute la suppression via le SERVICE METIER UC.
		 */
		service.delete(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'objet à supprimer absent :
		 * - positionne MESSAGE_OBJ_INTROUVABLE + libellé ;
		 * - ne délègue jamais la suppression.
		 */
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(non persistant) :</p>
	 * <ul>
	 * <li>retrouve un enfant portant le libellé demandé ;</li>
	 * <li>teste le cas où cet enfant ne porte pas d'identifiant ;</li>
	 * <li>lève une {@link ExceptionNonPersistant} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé demandé ;</li>
	 * <li>ne délègue jamais la suppression au Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NON_PERSISTANT)
	@Test
	public void testDeleteNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet retrouvé sans identifiant.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		
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
				.thenReturn(Arrays.asList(existant));

		/* ACT - ASSERT */
		/* Garantit qu'un objet retrouvé sans identifiant est refusé. */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
						+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(destruction KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à supprimer ;</li>
	 * <li>atteint l'appel {@code gateway.delete(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteDestructionKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un objet persistant retrouvé et une panne de suppression.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(20L);
		
		final Exception panneTechnique = new Exception(MESSAGE_GATEWAY);
		
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
				.thenReturn(Arrays.asList(existant));
		doThrow(panneTechnique).when(gateway).delete(existant);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(destruction KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet à supprimer ;</li>
	 * <li>atteint l'appel {@code gateway.delete(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret
	 * + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KO_SANS_MESSAGE)
	@Test
	public void testDeleteDestructionKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un objet persistant retrouvé et une panne sans message.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit existant =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(21L);
		
		final Exception panneTechnique = new Exception();
		
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
				.thenReturn(Arrays.asList(existant));
		doThrow(panneTechnique).when(gateway).delete(existant);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
						+ OUTILLAGE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(OK) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant via le Gateway TypeProduit ;</li>
	 * <li>retrouve l'objet à supprimer dans les enfants du parent ;</li>
	 * <li>délègue la suppression au Gateway SousTypeProduit ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_OK}
	 * + libellé demandé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NOMINAL)
	@Test
	public void testDeleteNominal() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, le parent persistant,
		 * un autre objet métier, un null et l'objet métier à supprimer.
		 */
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(
				BAZAR, OUTILLAGE);
		
		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit autre =
				new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(30L);
		
		final SousTypeProduit cible =
				new SousTypeProduit(OUTILLAGE, parentPersistant);
		cible.setIdSousTypeProduit(31L);
		
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
				.thenReturn(Arrays.asList(autre, null, cible));

		/* ACT :
		 * exécute la suppression via le SERVICE METIER UC.
		 */
		service.delete(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que les Gateways ont bien été sollicités
		 * dans l'ordre fonctionnel attendu :
		 * recherche du parent, recherche des enfants, puis suppression.
		 */
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).delete(cible);

		/* Garantit que la réponse observable par le controller appelant
		 * porte le message utilisateur de succès de suppression.
		 */
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_DELETE_OK
						+ OUTILLAGE);

	} // __________________________________________________________________

	
			
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>propage l'exception technique levée pendant le comptage ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'expose aucun comptage au controller appelant ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testCountGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique avec message pendant
		 * le comptage délégué au Gateway.
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
		 * gateway.count() jette une exception avec message.
		 * Le SERVICE METIER UC doit propager l'exception d'origine
		 * et produire KO_TECHNIQUE_RECHERCHE + détail technique.
		 */
		when(gateway.count()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		/* Garantit que le message observable côté controller appelant
		 * est rationalisé et contient le détail technique disponible.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur le comptage Gateway. */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>propage l'exception technique sans message levée
	 * pendant le comptage ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'expose aucun comptage au controller appelant ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testCountGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une panne technique sans message pendant
		 * le comptage délégué au Gateway.
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
		 * gateway.count() jette une exception sans message.
		 * Le SERVICE METIER UC doit propager l'exception d'origine
		 * et remplacer le message technique absent par MSG_ERREUR_NON_SPECIFIEE.
		 */
		when(gateway.count()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur le comptage Gateway. */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(retour négatif) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>détecte qu'un comptage strictement négatif est incohérent ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne un message technique explicite avec
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + valeur incohérente ;</li>
	 * <li>n'expose jamais cette valeur incohérente
	 * au controller appelant ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_RETOUR_NEGATIF)
	@Test
	public void testCountRetourNegatif() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway incohérent.
		 *
		 * Un comptage observable côté UC peut valoir 0,
		 * mais ne doit jamais être strictement négatif.
		 */
		final long comptageIncoherent = -1L;
		final String messageTechnique
				= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ "comptage négatif incohérent : "
				+ comptageIncoherent;
		
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
		 * gateway.count() retourne une valeur strictement négative.
		 * Le SERVICE METIER UC doit refuser ce résultat incohérent
		 * avant tout retour au controller appelant.
		 */
		when(gateway.count()).thenReturn(comptageIncoherent);

		/* ACT - ASSERT */
		/* Garantit que service.count() :
		 * - jette IllegalStateException ;
		 * - porte le message technique explicite ;
		 * - n'expose pas le comptage incohérent.
		 */
		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(messageTechnique);

		/* Garantit que le message observable côté controller appelant
		 * explique précisément le comptage incohérent refusé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(messageTechnique);

		/* Garantit que le comptage Gateway a bien été délégué une fois. */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(0) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>accepte le comptage {@code 0} comme résultat cohérent ;</li>
	 * <li>retourne exactement {@code 0} ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne positionne ce message qu'après récupération effective
	 * du comptage Gateway ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_ZERO)
	@Test
	public void testCountZero() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway cohérent indiquant
		 * qu'aucun objet métier n'est présent dans le stockage.
		 */
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
		 * gateway.count() retourne 0.
		 * Le SERVICE METIER UC doit retourner 0 et positionner
		 * MESSAGE_RECHERCHE_VIDE seulement après ce retour Gateway.
		 */
		when(gateway.count()).thenReturn(0L);

		/* ACT :
		 * exécute le comptage via le SERVICE METIER UC.
		 */
		final long retour = service.count();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - vaut exactement 0 ;
		 * - confirme l'absence d'objet métier accessible ;
		 * - expose le message utilisateur de recherche vide.
		 */
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que le message de résultat vide a été produit
		 * après récupération effective du comptage Gateway.
		 */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>accepte un comptage strictement positif comme résultat cohérent ;</li>
	 * <li>retourne exactement le comptage fourni par le Gateway ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>ne positionne ce message qu'après récupération effective
	 * du comptage Gateway ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_NOMINAL)
	@Test
	public void testCountNominal() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway cohérent indiquant
		 * que plusieurs objets métier sont présents dans le stockage.
		 */
		final long comptageAttendu = 42L;
		
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
		 * gateway.count() retourne un comptage strictement positif.
		 * Le SERVICE METIER UC doit retourner ce comptage exact
		 * et positionner MESSAGE_RECHERCHE_OK seulement après ce retour Gateway.
		 */
		when(gateway.count()).thenReturn(comptageAttendu);

		/* ACT :
		 * exécute le comptage via le SERVICE METIER UC.
		 */
		final long retour = service.count();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - vaut exactement le comptage fourni par le Gateway ;
		 * - expose le message utilisateur de succès de recherche.
		 */
		assertThat(retour).isEqualTo(comptageAttendu);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que le message de succès a été produit
		 * après récupération effective du comptage Gateway.
		 */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(initial) :</p>
	 * <ul>
	 * <li>reste appelable avant toute opération métier ;</li>
	 * <li>retourne {@code null} tant qu'aucun message n'a été positionné ;</li>
	 * <li>lit uniquement l'état local du SERVICE METIER UC ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_INITIAL_NULL)
	@Test
	public void testGetMessageInitialNull() throws Exception {

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
		 * lit le message courant sans opération UC préalable.
		 */
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'état initial observable côté controller appelant
		 * est bien null avant toute opération ayant positionné un message.
		 */
		assertThat(message).isNull();

		/* Garantit que getMessage() lit seulement l'état local
		 * du SERVICE METIER UC et ne sollicite jamais les Gateways.
		 */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après erreur locale) :</p>
	 * <ul>
	 * <li>lit le message positionné par une opération UC précédente ;</li>
	 * <li>retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NULL_KO}
	 * après {@code creer(null)} ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>n'interagit ni avec le Gateway SousTypeProduit
	 * ni avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_ERREUR_LOCALE)
	@Test
	public void testGetMessageApresErreurLocale() throws Exception {

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
		 * provoque une erreur utilisateur bénigne via le SERVICE METIER UC.
		 */
		service.creer(null);

		/* Lit le message courant après cette opération locale. */
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que getMessage() retourne le message déjà positionné
		 * par creer(null), sans recalcul ni transformation.
		 */
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* Garantit que creer(null) puis getMessage() restent locaux
		 * et ne sollicitent jamais les Gateways.
		 */
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après count 0) :</p>
	 * <ul>
	 * <li>lit le message positionné par un comptage précédent ;</li>
	 * <li>retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}
	 * après un {@code count()} égal à {@code 0} ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>ne déclenche aucune interaction Gateway supplémentaire ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_ZERO)
	@Test
	public void testGetMessageApresCountZero() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway cohérent indiquant
		 * qu'aucun objet métier n'est présent dans le stockage.
		 */
		final long comptageRetourne = 0L;
		
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
		 * gateway.count() retourne 0.
		 * Le SERVICE METIER UC doit positionner MESSAGE_RECHERCHE_VIDE
		 * seulement après ce retour Gateway.
		 */
		when(gateway.count()).thenReturn(comptageRetourne);

		/* ACT :
		 * exécute le comptage via le SERVICE METIER UC.
		 */
		final long retour = service.count();

		/* Lit le message courant après ce comptage. */
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que le comptage a bien positionné le message
		 * de recherche vide avant consultation par getMessage().
		 */
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que getMessage() n'a pas provoqué
		 * d'interaction Gateway supplémentaire.
		 */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après count nominal) :</p>
	 * <ul>
	 * <li>lit le message positionné par un comptage précédent ;</li>
	 * <li>retourne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}
	 * après un {@code count()} strictement positif ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>ne déclenche aucune interaction Gateway supplémentaire ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_NOMINAL)
	@Test
	public void testGetMessageApresCountNominal() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway cohérent indiquant
		 * que plusieurs objets métier sont présents dans le stockage.
		 */
		final long comptageAttendu = 42L;
		
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
		 * gateway.count() retourne un comptage strictement positif.
		 * Le SERVICE METIER UC doit positionner MESSAGE_RECHERCHE_OK
		 * seulement après ce retour Gateway.
		 */
		when(gateway.count()).thenReturn(comptageAttendu);

		/* ACT :
		 * exécute le comptage via le SERVICE METIER UC.
		 */
		final long retour = service.count();

		/* Lit le message courant après ce comptage. */
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que le comptage nominal a bien positionné
		 * le message de recherche OK avant consultation par getMessage().
		 */
		assertThat(retour).isEqualTo(comptageAttendu);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que getMessage() n'a pas provoqué
		 * d'interaction Gateway supplémentaire.
		 */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(dernier message gagne) :</p>
	 * <ul>
	 * <li>retourne d'abord le message produit par une erreur locale ;</li>
	 * <li>retourne ensuite le message produit par une opération plus récente ;</li>
	 * <li>prouve que l'opération UC la plus récente écrase
	 * le message observable précédent ;</li>
	 * <li>ne déclenche aucune interaction Gateway supplémentaire
	 * lors des consultations successives de {@code getMessage()} ;</li>
	 * <li>n'interagit jamais avec le Gateway TypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_DERNIER_MESSAGE_GAGNE)
	@Test
	public void testGetMessageDernierMessageGagne() throws Exception {

		/* ARRANGE :
		 * prépare un comptage Gateway cohérent indiquant
		 * qu'un objet métier est présent dans le stockage.
		 */
		final long comptageAttendu = 1L;
		
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
		 * gateway.count() retourne un comptage strictement positif.
		 * Cette seconde opération doit écraser le message précédemment
		 * positionné par creer(null).
		 */
		when(gateway.count()).thenReturn(comptageAttendu);

		/* ACT :
		 * provoque d'abord une erreur utilisateur bénigne.
		 */
		service.creer(null);

		/* Lit le message courant après cette première opération. */
		final String messageErreur = service.getMessage();

		/* ACT :
		 * exécute ensuite un comptage nominal plus récent.
		 */
		final long retour = service.count();

		/* Lit le message courant après cette seconde opération. */
		final String messageFinal = service.getMessage();

		/* ASSERT */
		/* Garantit que la première consultation retourne bien
		 * le message local produit par creer(null).
		 */
		assertThat(messageErreur)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* Garantit que la seconde opération retourne son comptage
		 * et remplace le message observable par MESSAGE_RECHERCHE_OK.
		 */
		assertThat(retour).isEqualTo(comptageAttendu);
		assertThat(messageFinal)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que seule l'opération count() a sollicité le Gateway ;
		 * les consultations getMessage() lisent uniquement l'état local.
		 */
		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
} // FIN DE LA CLASSE SousTypeProduitCuServiceMockTest.--------------------
