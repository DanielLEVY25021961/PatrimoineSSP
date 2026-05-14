/* ********************************************************************* */
/* ********************* TEST MOCKITO METIER UC ************************ */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOTypeProduit;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitCuServiceMockTest.java :</p>
 *
 * <p>
 * Tests unitaires JUnit 5 / Mockito du SERVICE METIER UC
 * {@link TypeProduitCuService} pour l'objet métier {@link TypeProduit}.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * Cette classe vérifie que {@link TypeProduitCuService}, point d'entrée
 * dans la logique métier dialoguant directement avec le controller appelant,
 * respecte le contrat du PORT {@link TypeProduitICuService}.
 * </p>
 *
 * <p>Elle contrôle notamment :</p>
 * <ul>
 * <li>les validations locales des paramètres et des DTO ;</li>
 * <li>les messages utilisateur exposés par {@code getMessage()} ;</li>
 * <li>les conversions entre {@link TypeProduitDTO.InputDTO},
 * {@link TypeProduit} et {@link TypeProduitDTO.OutputDTO} ;</li>
 * <li>les délégations attendues vers {@link TypeProduitGatewayIService} ;</li>
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
 * Le {@link TypeProduitGatewayIService} est mocké : ces tests ne valident
 * pas l'adaptateur de stockage, mais le comportement métier observable
 * du SERVICE METIER UC et le contrat de délégation entre le SERVICE METIER UC
 * et le PORT Gateway.
 * </p>
 * </div>
 *
 * <div>
 * <p>Le formalisme attendu dans cette classe est le suivant :</p>
 * <ul>
 * <li>organisation par bloc de méthode du PORT UC ;</li>
 * <li>ordre lisible : erreurs, cas alternatifs, puis nominal ;</li>
 * <li>commentaires {@code ARRANGE}, {@code Configuration du Mock},
 * {@code ACT} et {@code ASSERT} alignés avec le code immédiatement suivant ;</li>
 * <li>reprise stricte des blocs déjà validés dans la classe,
 * sans réinvention inutile ;</li>
 * <li>vérifications Mockito explicites sur les interactions attendues
 * ou interdites avec le Gateway.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 21 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class TypeProduitCuServiceMockTest {

	// *************************** CONSTANTES ******************************/

	/** "bazar" */
	public static final String BAZAR = "bazar";

	/** "tourisme" */
	public static final String TOURISME = "tourisme";

	/** "outillage" */
	public static final String OUTILLAGE = "outillage";

	/** "vêtement" */
	public static final String VETEMENT = "vêtement";

	/** "   " */
	public static final String ESPACES = "   ";
	
	/**
	 * "lecture technique KO"
	 */
	public static final String LECTURE_TECHNIQUE_KO = "lecture technique KO";
	
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
	public static final String TAG_RECHERCHER_TOUS_STRING = "rechercherTousString";

	/**
	 * "rechercherTousParPage"
	 */
	public static final String TAG_RECHERCHER_TOUS_PAR_PAGE = "rechercherTousParPage";

	/**
	 * "findByLibelle"
	 */
	public static final String TAG_FIND_BY_LIBELLE = "findByLibelle";

	/**
	 * "findByLibelleRapide"
	 */
	public static final String TAG_FIND_BY_LIBELLE_RAPIDE = "findByLibelleRapide";

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

	/** "message gateway" */
	public static final String MESSAGE_GATEWAY = "message gateway";

	/** "message gateway (bis)" */
	public static final String MESSAGE_GATEWAY_BIS = "message gateway (bis)";
	
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
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "creer(contrôle doublon KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_CONTROLE_DOUBLON_SANS_MESSAGE
			= "creer(contrôle doublon KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "creer(gateway.creer KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_AVEC_MESSAGE
			= "creer(gateway.creer KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "creer(gateway.creer KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_GATEWAY_CREER_SANS_MESSAGE
			= "creer(gateway.creer KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "creer(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_SANS_MESSAGE
			= "creer(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
					+ "ExceptionStockageVide + MESSAGE_STOCKAGE_NULL";
	
	/**
	 * "rechercherTous(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_AVEC_MESSAGE
			= "rechercherTous(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTous(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTous(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTous(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE
			= "rechercherTous(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTous(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_SANS_MESSAGE
			= "rechercherTous(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTous(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE
			= "rechercherTous(vide après filtrage) : "
					+ "liste vide + MESSAGE_RECHERCHE_VIDE"; // NOPMD by danyl on 09/05/2026 20:55
	
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
					+ "ExceptionStockageVide + MESSAGE_STOCKAGE_NULL";
	
	/**
	 * "rechercherTousString(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_AVEC_MESSAGE
			= "rechercherTousString(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTousString(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTousString(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTousString(conversion String KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_AVEC_MESSAGE
			= "rechercherTousString(conversion String KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTousString(conversion String KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_SANS_MESSAGE
			= "rechercherTousString(conversion String KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTousString(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE
			= "rechercherTousString(vide après filtrage) : "
					+ "liste vide + MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "rechercherTousString(vide après libellés blank) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK
			= "rechercherTousString(vide après libellés blank) : "
					+ "liste vide + MESSAGE_RECHERCHE_VIDE";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTousParPage(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_SANS_MESSAGE
			= "rechercherTousParPage(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTousParPage(gateway retourne null) :
	 * MESSAGE_RECHERCHE_PAGINEE_KO"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL
			= "rechercherTousParPage(gateway retourne null) : "
					+ "MESSAGE_RECHERCHE_PAGINEE_KO";
	
	/**
	 * "rechercherTousParPage(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "rechercherTousParPage(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "rechercherTousParPage(vide après filtrage) :
	 * page vide + MESSAGE_RECHERCHE_PAGINEE_OK"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE
			= "rechercherTousParPage(vide après filtrage) : "
					+ "page vide + MESSAGE_RECHERCHE_PAGINEE_OK";
	
	/**
	 * "rechercherTousParPage(nominal) :
	 * pagination reprise + OutputDTO triés dédoublonnés"
	 */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL
			= "rechercherTousParPage(nominal) : "
					+ "pagination reprise + OutputDTO triés dédoublonnés";

	/**
	 * "findByLibelle(null) :
	 * null + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NULL
			= "findByLibelle(null) : "
					+ "null + MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByLibelle(blank) :
	 * null + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_BLANK
			= "findByLibelle(blank) : "
					+ "null + MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByLibelle(gateway retourne null) :
	 * null + MESSAGE_OBJ_INTROUVABLE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL
			= "findByLibelle(gateway retourne null) : "
					+ "null + MESSAGE_OBJ_INTROUVABLE"; // NOPMD by danyl on 09/05/2026 21:17
	
	/**
	 * "findByLibelle(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_AVEC_MESSAGE
			= "findByLibelle(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "findByLibelle(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_SANS_MESSAGE
			= "findByLibelle(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "findByLibelle(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByLibelle(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findByLibelle(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByLibelle(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findByLibelle(nominal) :
	 * OutputDTO + MESSAGE_SUCCES_RECHERCHE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL
			= "findByLibelle(nominal) : "
					+ "OutputDTO + MESSAGE_SUCCES_RECHERCHE";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "findByLibelleRapide(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_SANS_MESSAGE
			= "findByLibelleRapide(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "findByLibelleRapide(gateway retourne null) :
	 * ExceptionStockageVide + MESSAGE_STOCKAGE_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL
			= "findByLibelleRapide(gateway retourne null) : "
					+ "ExceptionStockageVide + MESSAGE_STOCKAGE_NULL";
	
	/**
	 * "findByLibelleRapide(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findByLibelleRapide(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findByLibelleRapide(vide après filtrage) :
	 * liste vide + MESSAGE_RECHERCHE_VIDE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE
			= "findByLibelleRapide(vide après filtrage) : "
					+ "liste vide + MESSAGE_RECHERCHE_VIDE";
	
	/**
	 * "findByLibelleRapide(nominal) :
	 * OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL
			= "findByLibelleRapide(nominal) : "
					+ "OutputDTO triés dédoublonnés + MESSAGE_RECHERCHE_OK";
	
	/**
	 * "findByDTO(null) :
	 * null + MESSAGE_RECHERCHE_OBJ_NULL"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_NULL
			= "findByDTO(null) : "
					+ "null + MESSAGE_RECHERCHE_OBJ_NULL";
	
	/**
	 * "findByDTO(libellé null) :
	 * délègue à findByLibelle(null) + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_LIBELLE_NULL
			= "findByDTO(libellé null) : "
					+ "délègue à findByLibelle(null) "
					+ "+ MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByDTO(blank) :
	 * délègue à findByLibelle(blank) + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_BLANK
			= "findByDTO(blank) : "
					+ "délègue à findByLibelle(blank) "
					+ "+ MESSAGE_PARAM_BLANK";
	
	/**
	 * "findByDTO(gateway retourne null) :
	 * null + MESSAGE_OBJ_INTROUVABLE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_GATEWAY_RETOUR_NULL
			= "findByDTO(gateway retourne null) : "
					+ "null + MESSAGE_OBJ_INTROUVABLE";
	
	/**
	 * "findByDTO(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_AVEC_MESSAGE
			= "findByDTO(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "findByDTO(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_SANS_MESSAGE
			= "findByDTO(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "findByDTO(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findByDTO(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findByDTO(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findByDTO(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
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
	 * "findById(gateway retourne null) :
	 * null + MESSAGE_OBJ_INTROUVABLE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_GATEWAY_RETOUR_NULL
			= "findById(gateway retourne null) : "
					+ "null + MESSAGE_OBJ_INTROUVABLE";
	
	/**
	 * "findById(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_AVEC_MESSAGE
			= "findById(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "findById(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_SANS_MESSAGE
			= "findById(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "findById(conversion OutputDTO KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE
			= "findById(conversion OutputDTO KO avec message) : "
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "findById(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "findById(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
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
					+ "ExceptionParametreBlank + MESSAGE_PARAM_BLANK"; // NOPMD by danyl on 09/05/2026 22:43
	
	/**
	 * "update(blank) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_UPDATE_BLANK
			= "update(blank) : "
					+ "ExceptionParametreBlank + MESSAGE_PARAM_BLANK";
	
	/**
	 * "update(recherche KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_KO_AVEC_MESSAGE
			= "update(recherche KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "update(recherche KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_KO_SANS_MESSAGE
			= "update(recherche KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "update(modification KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_KO_SANS_MESSAGE
			= "update(modification KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
					+ "exception propagée + message rationalisé"; // NOPMD by danyl on 09/05/2026 20:34
	
	/**
	 * "update(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE
			= "update(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE"; // NOPMD by danyl on 09/05/2026 20:34
	
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
					+ "ExceptionParametreBlank + MESSAGE_PARAM_BLANK";
	
	/**
	 * "delete(blank) :
	 * ExceptionParametreBlank + MESSAGE_PARAM_BLANK"
	 */
	public static final String DISPLAY_NAME_DELETE_BLANK
			= "delete(blank) : "
					+ "ExceptionParametreBlank + MESSAGE_PARAM_BLANK";
	
	/**
	 * "delete(recherche KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_KO_AVEC_MESSAGE
			= "delete(recherche KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "delete(recherche KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_KO_SANS_MESSAGE
			= "delete(recherche KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "delete(introuvable) :
	 * MESSAGE_OBJ_INTROUVABLE + aucune destruction"
	 */
	public static final String DISPLAY_NAME_DELETE_INTROUVABLE
			= "delete(introuvable) : "
					+ "MESSAGE_OBJ_INTROUVABLE + aucune destruction";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "delete(destruction KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_DELETE_DESTRUCTION_KO_SANS_MESSAGE
			= "delete(destruction KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
	/**
	 * "delete(nominal) :
	 * MESSAGE_DELETE_OK + destruction déléguée"
	 */
	public static final String DISPLAY_NAME_DELETE_NOMINAL
			= "delete(nominal) : "
					+ "MESSAGE_DELETE_OK + destruction déléguée";
	
	/**
	 * "count(gateway KO avec message) :
	 * exception propagée + message rationalisé"
	 */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KO_AVEC_MESSAGE
			= "count(gateway KO avec message) : "
					+ "exception propagée + message rationalisé";
	
	/**
	 * "count(gateway KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KO_SANS_MESSAGE
			= "count(gateway KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
	public TypeProduitCuServiceMockTest() {
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
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL_KO} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la création avec un DTO null.
		 */
		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne le message utilisateur contractuel ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>émet le message
	 * {@link TypeProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
	 * </ul>
	 * <p>
	 * Ce test vise la branche locale :
	 * {@code StringUtils.isBlank(libelle)} dans
	 * {@code TypeProduitCuService.creer(...)}.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_BLANK)
	@Test
	public void testCreerBlank() {

		/* ARRANGE :
		 * prépare un DTO dont le libellé est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation au Gateway.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionParametreBlank ;
		 * - émet le message MESSAGE_CREER_LIBELLE_BLANK_KO
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* Garantit que le Gateway mocké n'a pas été appelé. */
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(doublon fonctionnel) :</p>
	 * <ul>
	 * <li>contrôle l'unicité via {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} interroge le Gateway via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>jette une {@link ExceptionDoublon} si le Gateway retourne
	 * un objet métier existant ;</li>
	 * <li>émet le message
	 * {@link TypeProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
	 * <li>ne délègue jamais la création au Gateway.</li>
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
		 * prépare un DTO valide dont le libellé existe déjà
		 * dans le stockage selon le Gateway mocké.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(VETEMENT);
			
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		/*
		 * Configuration du Mock :
		 * simule un doublon fonctionnel détecté par isDoublon(...)
		 * via l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(VETEMENT))
				.thenReturn(new TypeProduit(VETEMENT));

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionDoublon ;
		 * - émet le message MESSAGE_CREER_DOUBLON_KO + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_DOUBLON_KO + VETEMENT);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_DOUBLON_KO + VETEMENT);

		/* Garantit que le contrôle d'unicité a été exécuté
		 * et que la création n'a jamais été déléguée au Gateway.
		 */
		verify(gateway, times(1)).findByLibelle(VETEMENT);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
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
		final InputDTO dto = new TypeProduitDTO.InputDTO(BAZAR);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		/*
		 * Configuration du Mock :
		 * simule une panne technique de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé 
		 * PREFIX_MESSAGE_CREER_DOUBLON_KO + LECTURE_TECHNIQUE_KO.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
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
		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);
				
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(TOURISME)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(TOURISME);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
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
		 * prépare un DTO valide et non doublon pour atteindre
		 * réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(OUTILLAGE) retourne null
		 * pour laisser passer le contrôle de doublon.
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.creer(...).
		 */
		when(gateway.creer(any(TypeProduit.class))).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé 
		 * PREFIX_MESSAGE_CREER_GATEWAY_KO + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ MESSAGE_GATEWAY);

		/* Garantit l'ordre fonctionnel du scénario :
		 * contrôle d'unicité puis tentative de création.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * {@code gateway.creer(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		 * prépare un DTO valide et non doublon pour atteindre
		 * réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(OUTILLAGE) retourne null
		 * pour laisser passer le contrôle de doublon.
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.creer(...).
		 */
		when(gateway.creer(any(TypeProduit.class))).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit l'ordre fonctionnel du scénario :
		 * contrôle d'unicité puis tentative de création.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) retourne null) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>jette une {@link IllegalStateException} lorsque le Gateway
	 * retourne {@code null} ;</li>
	 * <li>positionne le message
	 * {@link TypeProduitICuService#MESSAGE_CREER_GATEWAY_KO} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un DTO valide et non doublon pour atteindre
		 * réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
				
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		/*
		 * Configuration du Mock :
		 * simule un Gateway qui accepte le contrôle d'unicité
		 * mais ne retourne aucun objet créé.
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le succès apparent
		 * et refuse une réponse technique null.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		/* Garantit que le Gateway a bien été sollicité
		 * jusqu'à la création, puis que l'anomalie null est traitée côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
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
		 * aux enfants provoque une panne pendant la conversion en OutputDTO.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final TypeProduit cree = mock(TypeProduit.class);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(cree.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		 * aux enfants provoque une panne sans message pendant la conversion.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final TypeProduit cree = mock(TypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(cree.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO retourne null) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
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
	 * {@link TypeProduitICuService#MESSAGE_CREER_CONVERSION_KO}.</li>
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
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final TypeProduit cree = new TypeProduit(OUTILLAGE);
		cree.setIdTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);
		
		/*
		 * Configuration du MockedStatic :
		 * - le convertisseur réel est une classe utilitaire static ;
		 * - avec un objet métier non null, il ne retourne normalement pas null ;
		 * - gateway.creer(...) ne peut pas retourner null ici,
		 *   car le SERVICE METIER UC s'arrêterait avant la conversion ;
		 * - le MockedStatic est donc limité à ce test pour atteindre
		 *   la branche défensive "dto == null" du SERVICE METIER UC.
		 */
		try (MockedStatic<ConvertisseurMetierToOutputDTOTypeProduit> mockedStatic
				= mockStatic(ConvertisseurMetierToOutputDTOTypeProduit.class)) {
			
			/*
			 * Configuration du MockedStatic :
			 * la conversion finale en OutputDTO retourne null.
			 */
			mockedStatic.when(
					() -> ConvertisseurMetierToOutputDTOTypeProduit
							.convert(cree))
					.thenReturn(null);

			/* ACT - ASSERT */
			/* Garantit que le SERVICE METIER UC refuse
			 * une conversion finale null.
			 */
			assertThatThrownBy(() -> service.creer(dto))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage(TypeProduitICuService.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le message utilisateur correspond
			 * au cas contractuel "conversion retourne null".
			 */
			assertThat(service.getMessage())
					.isEqualTo(TypeProduitICuService.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le scénario a atteint la création Gateway
			 * avant le contrôle du retour null de conversion.
			 */
			verify(gateway, times(1)).findByLibelle(OUTILLAGE);
			verify(gateway, times(1)).creer(any(TypeProduit.class));
			
			/* Garantit que le MockedStatic a été strictement limité
			 * à la conversion finale attendue par le SERVICE METIER UC.
			 */
			mockedStatic.verify(
					() -> ConvertisseurMetierToOutputDTOTypeProduit
							.convert(cree),
					times(1));
		}
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier sans identifiant initial ;</li>
	 * <li>délègue la création à {@code gateway.creer(...)} ;</li>
	 * <li>convertit l'objet métier créé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant généré
	 * et le bon libellé ;</li>
	 * <li>positionne le message
	 * {@link TypeProduitICuService#MESSAGE_CREER_OK}.</li>
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
		 * prépare un DTO valide, un retour métier créé par le Gateway,
		 * et un captor pour contrôler précisément l'objet métier
		 * envoyé à gateway.creer(...).
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
		
		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/*
		 * Configuration du Mock :
		 * - findByLibelle(...) retourne null pour simuler l'absence
		 *   de doublon fonctionnel ;
		 * - creer(...) retourne l'objet métier réellement créé
		 *   avec l'identifiant généré par le stockage.
		 */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final TypeProduit cree = new TypeProduit(OUTILLAGE);
		cree.setIdTypeProduit(1L);

		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);

		/* ACT :
		 * exécute la création via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que le Gateway a bien été sollicité
		 * dans l'ordre fonctionnel attendu :
		 * contrôle d'unicité puis création.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		/* Garantit que l'objet métier envoyé au Gateway :
		 * - n'est pas null ;
		 * - ne porte pas encore d'identifiant ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		final TypeProduit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdTypeProduit()).isNull();
		assertThat(envoye.getTypeProduit()).isEqualTo(OUTILLAGE);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant généré ;
		 * - porte le bon libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);
		
	} // __________________________________________________________________

	
	
	// ======================== rechercherTous ============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
	
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
	
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
		 * MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);
	
		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);
	
		final IllegalStateException panneTechnique = new IllegalStateException();
	
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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
	
		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_STOCKAGE_NULL}.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui retourne null au lieu d'une liste
		 * exploitable par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTous() :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_NULL_KO);

		/* Garantit que le Gateway a bien été sollicité une seule fois. */
		verify(gateway, times(1)).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_CONVERSION_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_CONVERSION_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_CONVERSION_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_SANS_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_CONVERSION_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
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
		final List<TypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.rechercherTous()).thenReturn(records);

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE METIER UC.
		 */
		final List<TypeProduitDTO.OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - n'est jamais null ;
		 * - est vide après filtrage ;
		 * - porte le message utilisateur de recherche vide.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée. */
		verify(gateway, times(1)).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
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
		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE METIER UC.
		 */
		final List<TypeProduitDTO.OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée. */
		verify(gateway, times(1)).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_STOCKAGE_NULL}.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui retourne null au lieu d'une liste
		 * exploitable par le SERVICE METIER UC.
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousString() :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que le Gateway a bien été sollicité une seule fois. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

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
		 * MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + MESSAGE_GATEWAY_BIS.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion String KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint l'extraction des libellés via
	 * {@code TypeProduit.getTypeProduit()} ;</li>
	 * <li>propage l'exception levée pendant cette extraction ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au libellé
		 * provoque une panne pendant l'extraction String.
		 */
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la préparation de la réponse String lit le libellé métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec d'extraction.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion String KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint l'extraction des libellés via
	 * {@code TypeProduit.getTypeProduit()} ;</li>
	 * <li>propage l'exception sans message levée pendant cette extraction ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au libellé
		 * provoque une panne pendant l'extraction String.
		 */
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la préparation de la réponse String lit le libellé métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getTypeProduit()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec d'extraction.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
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
		final List<TypeProduit> records = new ArrayList<>();
		records.add(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après libellés blank) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>extrait les libellés via {@code TypeProduit.getTypeProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
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
		 * prépare une réponse Gateway contenant un objet métier
		 * dont le libellé est blank.
		 */
		final TypeProduit typeProduitBlank = new TypeProduit(ESPACES);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, typeProduitBlank));

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
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousString(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>extrait les libellés via {@code TypeProduit.getTypeProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>dédoublonne les libellés en conservant l'ordre ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
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
		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);
		
		final TypeProduit tpBlank = new TypeProduit(ESPACES);
		tpBlank.setIdTypeProduit(3L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						tpTourisme, null, tpBlank, tpBazar, tpTourisme));

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
		assertThat(retour).containsExactly(BAZAR, TOURISME);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée. */
		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________

	
	
	// ===================== rechercherTousParPage ========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(null) :</p>
	 * <ul>
	 * <li>refuse une requête de pagination {@code null} ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PAGEABLE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousParPage(null) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PAGEABLE_NULL contractuel ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
		 * MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche paginée Gateway. */
		verify(gateway, times(1)).rechercherTousParPage(requete);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche paginée Gateway. */
		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousParPageGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 2);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.hasMessage(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		/* Garantit que le Gateway a bien été sollicité une seule fois. */
		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès aux enfants provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 2);
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		final ResultatPage<TypeProduit> resultatGateway
				= new ResultatPage<TypeProduit>(
						Arrays.asList(typeProduit),
						0,
						2,
						1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès aux enfants provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 2);
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		final ResultatPage<TypeProduit> resultatGateway
				= new ResultatPage<TypeProduit>(
						Arrays.asList(typeProduit),
						0,
						2,
						1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousParPageVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final RequetePage requete = new RequetePage(0, 4);
		
		final List<TypeProduit> records = new ArrayList<>();
		records.add(null);
		
		final ResultatPage<TypeProduit> resultatGateway
				= new ResultatPage<TypeProduit>(
						records,
						0,
						4,
						1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée. */
		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}.</li>
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

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		final ResultatPage<TypeProduit> resultatGateway
				= new ResultatPage<TypeProduit>(
						Arrays.asList(tpTourisme, null, tpBazar, tpTourisme),
						0,
						4,
						10L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
		 * - est triée par libellé métier ;
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
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée. */
		verify(gateway, times(1)).rechercherTousParPage(requete);

	} // __________________________________________________________________
	
	
			
	// ========================= findByLibelle ============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(null) :</p>
	 * <ul>
	 * <li>vise la branche locale
	 * {@code StringUtils.isBlank(pLibelle)} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche exacte avec un libellé null.
		 */
		final OutputDTO retour = service.findByLibelle(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(blank) :</p>
	 * <ul>
	 * <li>vise la branche locale
	 * {@code StringUtils.isBlank(pLibelle)} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * avant toute délégation au Gateway.
		 */
		final String libelle = ESPACES;
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche exacte avec un libellé blank.
		 */
		final OutputDTO retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>détecte que le Gateway ne trouve aucun objet métier ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé recherché ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un libellé valide absent du stockage selon le Gateway mocké.
		 */
		final String libelleAbsent = "IT_FIND_BY_LIBELLE_ABSENT_01";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui ne trouve aucun objet métier
		 * pour le libellé recherché.
		 */
		when(gateway.findByLibelle(libelleAbsent)).thenReturn(null);

		/* ACT :
		 * exécute la recherche exacte via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByLibelle(libelleAbsent);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - retourne null ;
		 * - porte le message utilisateur d'introuvabilité.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE 
						+ libelleAbsent);

		/* Garantit que la recherche exacte a bien été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelleAbsent);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		final String libelle = "IT_FIND_BY_LIBELLE_TECH_KO_01";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

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
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		final String libelle = "IT_FIND_BY_LIBELLE_TECH_KO_02";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = "IT_FIND_BY_LIBELLE_CONV_KO_01";
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = "IT_FIND_BY_LIBELLE_CONV_KO_02";
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>convertit l'objet métier trouvé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant
	 * et le bon libellé ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
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
		 * prépare un libellé valide et un objet métier trouvé
		 * par le Gateway.
		 */
		final String libelle = "IT_FIND_BY_LIBELLE_OK_01";
		final TypeProduit typeProduit = new TypeProduit(libelle);
		typeProduit.setIdTypeProduit(7L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/* ACT :
		 * exécute la recherche exacte via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier ;
		 * - porte le libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(7L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* Garantit que la recherche exacte a bien été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________	

	
		
	// ====================== findByLibelleRapide =========================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(null) :</p>
	 * <ul>
	 * <li>vise la branche locale {@code pContenu == null} ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.findByLibelleRapide(null) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PARAM_NULL contractuel ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(blank) :</p>
	 * <ul>
	 * <li>vise la branche locale
	 * {@code StringUtils.isBlank(pContenu)} ;</li>
	 * <li>délègue exactement au scénario complet
	 * {@code rechercherTous()} ;</li>
	 * <li>n'appelle jamais
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>retourne la liste DTO issue de la recherche exhaustive ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
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
		
		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la branche blank délègue à rechercherTous(),
		 * donc elle doit appeler gateway.rechercherTous().
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ACT :
		 * exécute la recherche rapide avec un contenu blank.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - provient de la recherche exhaustive ;
		 * - est triée par libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que le blank délègue à rechercherTous()
		 * et n'appelle jamais la recherche rapide Gateway.
		 */
		verify(gateway, times(1)).rechercherTous();
		verify(gateway, never()).findByLibelleRapide(anyString());

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un contenu valide pour atteindre réellement
		 * la délégation gateway.findByLibelleRapide(...).
		 */
		final String contenu = "QA_FIND_RAPIDE_TECH_01";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

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
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche rapide Gateway. */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un contenu valide pour atteindre réellement
		 * la délégation gateway.findByLibelleRapide(...).
		 */
		final String contenu = "QA_FIND_RAPIDE_TECH_02";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche rapide Gateway. */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.findByLibelleRapide(...)} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un contenu valide pour atteindre réellement
		 * la délégation gateway.findByLibelleRapide(...).
		 */
		final String contenu = "QA_FIND_RAPIDE_NULL";
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui retourne null au lieu d'une liste
		 * exploitable par le SERVICE METIER UC.
		 */
		when(gateway.findByLibelleRapide(contenu)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.findByLibelleRapide(contenu) :
		 * - lève ExceptionStockageVide ;
		 * - émet le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que la recherche rapide a bien été déléguée. */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String contenu = "QA_FIND_RAPIDE_CONV_KO_01";
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne une liste non null
		 * contenant un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche rapide Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String contenu = "QA_FIND_RAPIDE_CONV_KO_02";
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelleRapide(...) retourne une liste non null
		 * contenant un objet métier exploitable par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(typeProduit));

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche rapide Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		
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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindByLibelleRapideVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse Gateway non null mais ne contenant
		 * aucun objet métier exploitable après filtrage.
		 */
		final String contenu = "QA_FIND_RAPIDE_VIDE";
		final List<TypeProduit> records = new ArrayList<TypeProduit>();
		records.add(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche rapide a bien été déléguée
		 * et que rechercherTous() n'a pas été appelée.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>ne délègue jamais à {@code rechercherTous()}.</li>
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
		final String contenu = "QA_FIND_RAPIDE_OK";

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ACT :
		 * exécute la recherche rapide via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier exploitables ;
		 * - est triée par libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche rapide a bien été déléguée
		 * et que rechercherTous() n'a pas été appelée.
		 */
		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

	} // __________________________________________________________________	

	
			
	// ========================== findByDTO ===============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(null) :</p>
	 * <ul>
	 * <li>vise la branche locale {@code pInputDTO == null} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche par DTO avec un DTO null.
		 */
		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_RECHERCHE_OBJ_NULL ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(libellé null) :</p>
	 * <ul>
	 * <li>ne vise pas la branche locale {@code pInputDTO == null} ;</li>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>aboutit à la branche déléguée
	 * {@code StringUtils.isBlank(pLibelle)} de {@code findByLibelle(...)} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_LIBELLE_NULL)
	@Test
	public void testFindByDTOLibelleNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé métier est null.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche par DTO.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la délégation à findByLibelle(null) :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(blank) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>aboutit à la branche déléguée
	 * {@code StringUtils.isBlank(pLibelle)} de {@code findByLibelle(...)} ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_BLANK)
	@Test
	public void testFindByDTOBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO non null dont le libellé métier est blank.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche par DTO.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la délégation à findByLibelle(blank) :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway retourne null) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>détecte que le Gateway ne trouve aucun objet métier ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé recherché ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
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
		 * prépare un DTO valide absent du stockage selon le Gateway mocké.
		 */
		final String libelleAbsent = "IT_FIND_BY_DTO_ABSENT_01";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelleAbsent);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui ne trouve aucun objet métier
		 * pour le libellé extrait du DTO.
		 */
		when(gateway.findByLibelle(libelleAbsent)).thenReturn(null);

		/* ACT :
		 * exécute la recherche par DTO.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse utilisateur :
		 * - retourne null ;
		 * - porte le message utilisateur d'introuvabilité.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE 
						+ libelleAbsent);

		/* Garantit que la recherche exacte a bien été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelleAbsent);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway KO avec message) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByDTOGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = "IT_FIND_BY_DTO_TECH_KO_01";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway KO sans message) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByDTOGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = "IT_FIND_BY_DTO_TECH_KO_02";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet métier mocké dont l'accès
		 * aux enfants provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = "IT_FIND_BY_DTO_CONV_KO_01";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet métier mocké dont l'accès
		 * aux enfants provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = "IT_FIND_BY_DTO_CONV_KO_02";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findByDTO(OK) :</p>
	 * <ul>
	 * <li>délègue à {@code findByLibelle(pInputDTO.getTypeProduit())} ;</li>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>convertit l'objet métier trouvé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant
	 * et le bon libellé ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
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
		 * prépare un DTO valide et un objet métier trouvé
		 * par le Gateway.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit typeProduit = new TypeProduit(libelle);
		typeProduit.setIdTypeProduit(9L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.findByLibelle(libelle)).thenReturn(typeProduit);

		/* ACT :
		 * exécute la recherche par DTO via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier ;
		 * - porte le libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(9L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* Garantit que la recherche exacte a bien été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelle);
		
	} // __________________________________________________________________	

	
			
	// =========================== findById ===============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(null) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT :
		 * exécute la recherche par identifiant avec un ID null.
		 */
		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_NULL ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>détecte que le Gateway ne trouve aucun objet métier ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + identifiant recherché ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByIdGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide absent du stockage
		 * selon le Gateway mocké.
		 */
		final Long id = 12L;
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		/* Garantit que la recherche par identifiant a bien été déléguée. */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_AVEC_MESSAGE)
	@Test
	public void testFindByIdGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide pour atteindre réellement
		 * la délégation gateway.findById(...).
		 */
		final Long id = 21L;
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_SANS_MESSAGE)
	@Test
	public void testFindByIdGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un identifiant valide pour atteindre réellement
		 * la délégation gateway.findById(...).
		 */
		final Long id = 22L;
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final Long id = 31L;
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findById(id)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOTypeProduit.convert(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final Long id = 32L;
		final TypeProduit typeProduit = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findById(id)).thenReturn(typeProduit);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */
		when(typeProduit.getSousTypeProduits()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que findById(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findById(...)} ;</li>
	 * <li>le Gateway retourne un objet métier non {@code null} ;</li>
	 * <li>convertit l'objet métier trouvé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant
	 * et le bon libellé ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
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
		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(id);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		when(gateway.findById(id)).thenReturn(typeProduit);

		/* ACT :
		 * exécute la recherche par identifiant via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant métier ;
		 * - porte le libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(id);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* Garantit que la recherche par identifiant a bien été déléguée. */
		verify(gateway, times(1)).findById(id);
		
	} // __________________________________________________________________	

	
			
	// ============================ update ================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(null) :</p>
	 * <ul>
	 * <li>refuse un DTO de modification {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreNull} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.update(null) :
		 * - jette ExceptionParametreNull ;
		 * - émet MESSAGE_PARAM_NULL ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_NULL);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_NULL.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		/* Garantit que le refus du DTO null est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(libellé null) :</p>
	 * <ul>
	 * <li>lit le libellé porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * prépare un DTO non null dont le libellé métier est null.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation au Gateway.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.update(dto) :
		 * - jette ExceptionParametreBlank ;
		 * - émet MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_BLANK.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le refus du libellé null est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(blank) :</p>
	 * <ul>
	 * <li>lit le libellé porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé blank ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * prépare un DTO dont le libellé métier est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation au Gateway.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.update(dto) :
		 * - jette ExceptionParametreBlank ;
		 * - émet MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_BLANK.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le refus du libellé blank est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(recherche KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée pendant la recherche ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
		 *
		 * Cette recherche préalable est nécessaire car l'InputDTO
		 * ne porte pas l'identifiant persistant de l'objet à modifier.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		/*
		 * Configuration du Mock :
		 * simule une panne technique au moment où le service
		 * recherche l'objet existant pour récupérer son ID persistant.
		 * La modification ne doit donc jamais être tentée.
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + LECTURE_TECHNIQUE_KO.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * et bloque toute délégation de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(recherche KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée pendant la recherche ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
		 *
		 * Cette recherche préalable est nécessaire car l'InputDTO
		 * ne porte pas l'identifiant persistant de l'objet à modifier.
		 */
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment où le service
		 * recherche l'objet existant pour récupérer son ID persistant.
		 * Le test vérifie le fallback MSG_ERREUR_NON_SPECIFIEE.
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * et bloque toute délégation de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(introuvable) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>détecte qu'aucun objet métier persistant ne correspond
	 * au libellé exact ;</li>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
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
		 * prépare un DTO valide absent du stockage selon le Gateway mocké.
		 */
		final String libelle = VETEMENT;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne null.
		 * Le service doit donc considérer l'objet comme introuvable
		 * et ne jamais appeler gateway.update(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(null);

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'objet absent du stockage :
		 * - retourne null côté controller appelant ;
		 * - positionne MESSAGE_OBJ_INTROUVABLE + libellé ;
		 * - ne déclenche aucune modification Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

		/* Garantit que seule la recherche exacte préalable a été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(non persistant) :</p>
	 * <ul>
	 * <li>retrouve un objet métier par libellé exact ;</li>
	 * <li>détecte que cet objet ne porte pas d'identifiant persistant ;</li>
	 * <li>lève une {@link ExceptionNonPersistant} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
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
		 * prépare un DTO valide et un objet métier retrouvé
		 * mais non persistant.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne un objet métier sans ID.
		 * Le service doit refuser la modification, car il ne peut pas
		 * réinjecter un identifiant persistant dans l'objet reconstruit.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		/* ACT - ASSERT */
		/* Garantit que service.update(dto) :
		 * - jette ExceptionNonPersistant ;
		 * - émet MESSAGE_OBJ_NON_PERSISTE + libellé ;
		 * - ne sollicite jamais gateway.update(...).
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit que le message observable côté controller appelant
		 * explique précisément l'objet non persistant refusé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit que la modification n'est jamais déléguée
		 * après détection de l'objet existant non persistant.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(modification KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>propage l'exception technique levée pendant la modification ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateModificationKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant modification.
		 */
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(41L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 41L doit être réinjecté dans l'objet reconstruit
		 * depuis l'InputDTO avant l'appel à gateway.update(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) jette une exception avec message.
		 * Le test vérifie que le service propage cette exception
		 * et construit MESSAGE_MODIF_KO + libellé + message technique.
		 */
		when(gateway.update(any(TypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * MESSAGE_MODIF_KO + libellé + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(41L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(modification KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>propage l'exception technique sans message levée pendant la modification ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_SANS_MESSAGE)
	@Test
	public void testUpdateModificationKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant modification.
		 */
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(42L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 42L doit être réinjecté dans l'objet reconstruit
		 * depuis l'InputDTO avant l'appel à gateway.update(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) jette une exception sans message.
		 * Le test vérifie le fallback MSG_ERREUR_NON_SPECIFIEE
		 * dans le message utilisateur de modification KO.
		 */
		when(gateway.update(any(TypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(42L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(gateway.update retourne null) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>retourne {@code null} si le Gateway ne retourne aucun objet modifié ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé.</li>
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
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant modification.
		 */
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(10L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 10L doit être réinjecté dans l'objet envoyé
		 * à gateway.update(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) retourne null.
		 * Le test vérifie que le service retourne null et positionne
		 * MESSAGE_MODIF_KO + libellé.
		 */
		when(gateway.update(any(TypeProduit.class))).thenReturn(null);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit qu'une réponse technique null du Gateway :
		 * - retourne null côté controller appelant ;
		 * - positionne MESSAGE_MODIF_KO + libellé ;
		 * - ne tente aucune conversion finale en OutputDTO.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_KO + libelle);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(10L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(gateway.update retourne non persistant) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>atteint l'appel {@code gateway.update(...)} ;</li>
	 * <li>détecte que l'objet modifié retourné ne porte plus d'identifiant ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT)
	@Test
	public void testUpdateModificationRetourNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, un objet métier persistant retrouvé
		 * avant modification, et un objet modifié redevenu non persistant.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(77L);

		final TypeProduit modifie = new TypeProduit(libelle);
		modifie.setIdTypeProduit(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 77L doit être réinjecté dans l'objet envoyé
		 * à gateway.update(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) retourne un objet sans ID.
		 * Le test vérifie que le service refuse ce retour technique,
		 * car un objet modifié doit rester persistant.
		 */
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT - ASSERT */
		/* Garantit que service.update(dto) :
		 * - jette IllegalStateException ;
		 * - émet MESSAGE_OBJ_NON_PERSISTE + libellé ;
		 * - refuse une réponse Gateway redevenue non persistante.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit que le message observable côté controller appelant
		 * explique précisément le retour modifié non persistant.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de modification.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(77L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>modifie l'objet via {@code gateway.update(...)} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, un objet métier persistant retrouvé,
		 * et un objet modifié mocké dont l'accès aux enfants
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(51L);
		
		final TypeProduit modifie = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 51L doit être réinjecté dans l'objet reconstruit
		 * depuis l'InputDTO avant la modification.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) retourne un objet mocké non null.
		 * Ce retour permet d'atteindre le contrôle de persistance
		 * puis la conversion finale en OutputDTO.
		 */
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		/*
		 * Configuration du Mock :
		 * getIdTypeProduit() retourne 51L pour passer le contrôle
		 * "objet modifié toujours persistant" avant la conversion.
		 */
		when(modifie.getIdTypeProduit()).thenReturn(51L);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet modifié.
		 * Cet accès déclenche précisément la panne de conversion attendue.
		 */
		when(modifie.getSousTypeProduits()).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion :
		 * MESSAGE_MODIF_KO + libellé + TIRET_ESPACE + MESSAGE_GATEWAY_BIS.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis modification Gateway,
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(51L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>modifie l'objet via {@code gateway.update(...)} ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, un objet métier persistant retrouvé,
		 * et un objet modifié mocké dont l'accès aux enfants
		 * provoque une panne sans message pendant la conversion en OutputDTO.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(52L);
		
		final TypeProduit modifie = mock(TypeProduit.class);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 52L doit être réinjecté dans l'objet reconstruit
		 * depuis l'InputDTO avant la modification.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) retourne un objet mocké non null.
		 * Ce retour permet d'atteindre le contrôle de persistance
		 * puis la conversion finale en OutputDTO.
		 */
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		/*
		 * Configuration du Mock :
		 * getIdTypeProduit() retourne 52L pour passer le contrôle
		 * "objet modifié toujours persistant" avant la conversion.
		 */
		when(modifie.getIdTypeProduit()).thenReturn(52L);
		
		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit les enfants de l'objet modifié.
		 * Cet accès déclenche précisément une panne sans message,
		 * ce qui force le fallback MSG_ERREUR_NON_SPECIFIEE.
		 */
		when(modifie.getSousTypeProduits()).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion
		 * sans message technique.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis modification Gateway,
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(52L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(OK) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>réinjecte son identifiant dans l'objet reconstruit depuis le DTO ;</li>
	 * <li>délègue la modification à {@code gateway.update(...)} ;</li>
	 * <li>convertit l'objet métier modifié en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant persistant
	 * et le bon libellé ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_OK}
	 * + libellé.</li>
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
		 * prépare un DTO valide, un objet persistant retrouvé,
		 * un objet métier modifié retourné par le Gateway,
		 * et un captor pour contrôler l'objet envoyé à gateway.update(...).
		 */
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(5L);

		final TypeProduit modifie = new TypeProduit(libelle);
		modifie.setIdTypeProduit(5L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Son ID 5L doit être réinjecté dans l'objet reconstruit
		 * depuis l'InputDTO avant la modification.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.update(...) retourne l'objet métier modifié persistant.
		 * Le test vérifie ensuite que cet objet est converti en OutputDTO
		 * et que le message MESSAGE_MODIF_OK + libellé est positionné.
		 */
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<TypeProduit> captor
				= ArgumentCaptor.forClass(TypeProduit.class);

		/* ACT :
		 * exécute la modification via le SERVICE METIER UC.
		 */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant de l'objet modifié ;
		 * - porte le libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(5L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + libelle);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis modification Gateway.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		/* Garantit que l'objet métier envoyé à gateway.update(...) :
		 * - n'est pas null ;
		 * - porte l'identifiant persistant récupéré par la recherche ;
		 * - porte le libellé métier issu de l'InputDTO.
		 */
		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(5L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________

	
						
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(null) :</p>
	 * <ul>
	 * <li>refuse un DTO de destruction {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreNull} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * Mocke un service Gateway et le passe
		 * à un service UC instancié dans le test.
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.delete(null) :
		 * - jette ExceptionParametreNull ;
		 * - émet MESSAGE_PARAM_NULL ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_NULL);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_NULL.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		/* Garantit que le refus du DTO null est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(libellé null) :</p>
	 * <ul>
	 * <li>lit le libellé porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé {@code null} ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * prépare un DTO non null dont le libellé métier est null.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation au Gateway.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.delete(dto) :
		 * - jette ExceptionParametreBlank ;
		 * - émet MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_BLANK.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le refus du libellé null est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(blank) :</p>
	 * <ul>
	 * <li>lit le libellé porté par un DTO non {@code null} ;</li>
	 * <li>refuse ce libellé blank ;</li>
	 * <li>lève une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
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
		 * prépare un DTO dont le libellé métier est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation au Gateway.
		 */
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/* ACT - ASSERT */
		/* Garantit que service.delete(dto) :
		 * - jette ExceptionParametreBlank ;
		 * - émet MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le message observable côté controller appelant
		 * est exactement le message contractuel MESSAGE_PARAM_BLANK.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* Garantit que le refus du libellé blank est traité localement
		 * par le SERVICE METIER UC avant toute délégation.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(recherche KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée pendant la recherche ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.delete(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
		 *
		 * Cette recherche préalable est nécessaire car l'InputDTO
		 * ne porte pas l'identifiant persistant de l'objet à détruire.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		/*
		 * Configuration du Mock :
		 * simule une panne technique au moment où le service
		 * recherche l'objet existant à détruire par libellé exact.
		 * La destruction ne doit donc jamais être tentée.
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + TIRET_ESPACE + LECTURE_TECHNIQUE_KO.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * et bloque toute délégation de destruction.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(recherche KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée pendant la recherche ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.delete(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
		 *
		 * Cette recherche préalable est nécessaire car l'InputDTO
		 * ne porte pas l'identifiant persistant de l'objet à détruire.
		 */
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment où le service
		 * recherche l'objet existant à détruire par libellé exact.
		 * Le test vérifie le fallback MSG_ERREUR_NON_SPECIFIEE.
		 */
		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * et bloque toute délégation de destruction.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(introuvable) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>détecte qu'aucun objet métier persistant ne correspond
	 * au libellé exact ;</li>
	 * <li>ne jette aucune exception ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé ;</li>
	 * <li>ne tente jamais {@code gateway.delete(...)}.</li>
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
		 * prépare un DTO valide absent du stockage selon le Gateway mocké.
		 */
		final String libelle = VETEMENT;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne null.
		 * Le service doit donc considérer l'objet comme introuvable
		 * et ne jamais appeler gateway.delete(...).
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(null);

		/* ACT :
		 * exécute la destruction via le SERVICE METIER UC.
		 */
		service.delete(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'objet absent du stockage :
		 * - ne déclenche aucune exception ;
		 * - positionne MESSAGE_OBJ_INTROUVABLE + libellé ;
		 * - ne déclenche aucune destruction Gateway.
		 */
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

		/* Garantit que seule la recherche exacte préalable a été déléguée. */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(non persistant) :</p>
	 * <ul>
	 * <li>retrouve un objet métier par libellé exact ;</li>
	 * <li>détecte que cet objet ne porte pas d'identifiant persistant ;</li>
	 * <li>lève une {@link ExceptionNonPersistant} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé ;</li>
	 * <li>ne tente jamais {@code gateway.delete(...)}.</li>
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
		 * prépare un DTO valide et un objet métier retrouvé
		 * mais non persistant.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(null);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne un objet métier sans ID.
		 * Le service doit refuser la destruction, car le contrat impose
		 * de détruire uniquement un objet déjà persistant.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		/* ACT - ASSERT */
		/* Garantit que service.delete(dto) :
		 * - jette ExceptionNonPersistant ;
		 * - émet MESSAGE_OBJ_NON_PERSISTE + libellé ;
		 * - ne sollicite jamais gateway.delete(...).
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit que le message observable côté controller appelant
		 * explique précisément l'objet non persistant refusé.
		 */
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		/* Garantit que la destruction n'est jamais déléguée
		 * après détection de l'objet existant non persistant.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(destruction KO avec message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>vérifie que cet objet porte un identifiant persistant ;</li>
	 * <li>atteint l'appel {@code gateway.delete(...)} ;</li>
	 * <li>propage l'exception technique levée pendant la destruction ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_DELETE_KO}
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
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant destruction.
		 */
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(41L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final Exception panneTechnique = new Exception(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Cet objet doit être transmis tel quel à gateway.delete(...),
		 * car la destruction porte sur l'objet réellement retrouvé.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.delete(existant) jette une exception avec message.
		 * Le test vérifie que le service propage cette exception
		 * et construit MESSAGE_DELETE_KO + libellé + message technique.
		 */
		doThrow(panneTechnique).when(gateway).delete(existant);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé
		 * MESSAGE_DELETE_KO + libellé + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de destruction.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(destruction KO sans message) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>vérifie que cet objet porte un identifiant persistant ;</li>
	 * <li>atteint l'appel {@code gateway.delete(...)} ;</li>
	 * <li>propage l'exception technique sans message levée pendant la destruction ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret
	 * + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant destruction.
		 */
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(42L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		final Exception panneTechnique = new Exception();

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Cet objet doit être transmis tel quel à gateway.delete(...),
		 * car la destruction porte sur l'objet réellement retrouvé.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		
		/*
		 * Configuration du Mock :
		 * gateway.delete(existant) jette une exception sans message.
		 * Le test vérifie le fallback MSG_ERREUR_NON_SPECIFIEE
		 * dans le message utilisateur de destruction KO.
		 */
		doThrow(panneTechnique).when(gateway).delete(existant);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null lorsque l'exception technique
		 * ne porte aucun message.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis tentative de destruction.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que delete(OK) :</p>
	 * <ul>
	 * <li>ré-identifie l'objet persistant par libellé exact ;</li>
	 * <li>vérifie que cet objet porte un identifiant persistant ;</li>
	 * <li>délègue la destruction à {@code gateway.delete(...)} ;</li>
	 * <li>ne retourne aucune valeur ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_OK}
	 * + libellé seulement après destruction effective.</li>
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
		 * prépare un DTO valide et un objet métier persistant retrouvé
		 * avant délégation nominale de la destruction au Gateway.
		 */
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(15L);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * la recherche préalable retourne l'objet persistant existant.
		 * Le service doit ensuite déléguer sa destruction à gateway.delete(...)
		 * puis positionner MESSAGE_DELETE_OK + libellé.
		 */
		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		/* ACT :
		 * exécute la destruction via le SERVICE METIER UC.
		 */
		service.delete(dto);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que le message observable côté controller appelant :
		 * - confirme la destruction nominale ;
		 * - n'est positionné qu'après retour sans exception
		 *   de gateway.delete(...).
		 */
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + libelle);

		/* Garantit l'ordre fonctionnel du scénario :
		 * recherche exacte préalable puis destruction Gateway.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________

	
	
	// ============================ count =================================
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>propage l'exception technique levée pendant le comptage ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + message technique ;</li>
	 * <li>n'expose aucun comptage au controller appelant.</li>
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
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

		/*
		 * Configuration du Mock :
		 * gateway.count() jette une exception avec message.
		 * Le SERVICE METIER UC doit propager l'exception d'origine
		 * et produire MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO + détail technique.
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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		/* Garantit que la panne intervient bien sur le comptage Gateway. */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>propage l'exception technique sans message levée
	 * pendant le comptage ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'expose aucun comptage au controller appelant.</li>
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
		final IllegalStateException panneTechnique 
			= new IllegalStateException();
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
						TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur le comptage Gateway. */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(retour négatif) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>détecte qu'un comptage strictement négatif est incohérent ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne un message technique explicite avec
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO}
	 * + tiret + valeur incohérente ;</li>
	 * <li>n'expose jamais cette valeur incohérente
	 * au controller appelant.</li>
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
				= TypeProduitICuService.MESSAGE_RECHERCHER_TOUS_TECHNIQUE_KO
				+ TypeProduitICuService.TIRET_ESPACE
				+ "comptage négatif incohérent : "
				+ comptageIncoherent;
		
		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(0) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>accepte le comptage {@code 0} comme résultat cohérent ;</li>
	 * <li>retourne exactement {@code 0} ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>ne positionne ce message qu'après récupération effective
	 * du comptage Gateway.</li>
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
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que le message de résultat vide a été produit
		 * après récupération effective du comptage Gateway.
		 */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que count(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.count()} ;</li>
	 * <li>accepte un comptage strictement positif comme résultat cohérent ;</li>
	 * <li>retourne exactement le comptage fourni par le Gateway ;</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>ne positionne ce message qu'après récupération effective
	 * du comptage Gateway.</li>
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
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
		 * - reprend exactement le comptage Gateway ;
		 * - confirme une recherche positive ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isEqualTo(comptageAttendu);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que le message de succès a été produit
		 * après récupération effective du comptage Gateway.
		 */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________	

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(initial) :</p>
	 * <ul>
	 * <li>reste appelable avant toute opération métier ;</li>
	 * <li>retourne {@code null} tant qu'aucun message n'a été positionné ;</li>
	 * <li>lit uniquement l'état local du SERVICE METIER UC ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_INITIAL_NULL)
	@Test
	public void testGetMessageInitialNull() throws Exception {

		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
		 * du SERVICE METIER UC et ne sollicite jamais le Gateway.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après erreur locale) :</p>
	 * <ul>
	 * <li>lit le message positionné par une opération UC précédente ;</li>
	 * <li>retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL_KO}
	 * après {@code creer(null)} ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>n'interagit jamais avec le Gateway.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_ERREUR_LOCALE)
	@Test
	public void testGetMessageApresErreurLocale() throws Exception {

		/* 
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* Garantit que creer(null) puis getMessage() restent locaux
		 * et ne sollicitent jamais le Gateway.
		 */
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après count 0) :</p>
	 * <ul>
	 * <li>lit le message positionné par un comptage précédent ;</li>
	 * <li>retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}
	 * après un {@code count()} égal à {@code 0} ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>ne déclenche aucune interaction Gateway supplémentaire.</li>
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
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que getMessage() n'a pas provoqué
		 * d'interaction Gateway supplémentaire.
		 */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que getMessage(après count nominal) :</p>
	 * <ul>
	 * <li>lit le message positionné par un comptage précédent ;</li>
	 * <li>retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}
	 * après un {@code count()} strictement positif ;</li>
	 * <li>ne recalcule pas le message ;</li>
	 * <li>ne déclenche aucune interaction Gateway supplémentaire.</li>
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
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que getMessage() n'a pas provoqué
		 * d'interaction Gateway supplémentaire.
		 */
		verify(gateway, times(1)).count();

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
	 * lors des consultations successives de {@code getMessage()}.</li>
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
		 * Mocke un service Gateway et le passe 
		 * à un service UC instancié dans le test. 
		 */
		final TypeProduitGatewayIService gateway 
			= mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service 
			= new TypeProduitCuService(gateway);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL_KO);

		/* Garantit que la seconde opération retourne son comptage
		 * et remplace le message observable par MESSAGE_RECHERCHE_OK.
		 */
		assertThat(retour).isEqualTo(comptageAttendu);
		assertThat(messageFinal)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que seule l'opération count() a sollicité le Gateway ;
		 * les consultations getMessage() lisent uniquement l'état local.
		 */
		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
}
