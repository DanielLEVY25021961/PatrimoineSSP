package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
 * <p>Tests JUnit Mockito complets (avec tests "béton") pour
 * {@link TypeProduitCuService}.</p>
 * <p>Vérifie l'implémentation des contrats du PORT
 * {@link TypeProduitICuService} et la délégation vers
 * {@link TypeProduitGatewayIService}.</p>
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
	
	/** "cu-mock" */
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

	/** "message gateway" */
	public static final String MESSAGE_GATEWAY = "message gateway";

	/** "message gateway (bis)" */
	public static final String MESSAGE_GATEWAY_BIS = "message gateway (bis)";
	
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
					+ "exception propagée + message rationalisé";
	
	/**
	 * "creer(conversion OutputDTO KO sans message) :
	 * fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String DISPLAY_NAME_CREER_CONVERSION_OUTPUT_DTO_SANS_MESSAGE
			= "creer(conversion OutputDTO KO sans message) : "
					+ "fallback MSG_ERREUR_NON_SPECIFIEE";
	
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
					+ "ExceptionParametreBlank + MESSAGE_PARAM_BLANK";
	
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
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL} ;</li>
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
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>émet le message
	 * {@link TypeProduitICuService#MESSAGE_CREER_NOM_BLANK} ;</li>
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
		 * - émet le message MESSAGE_CREER_NOM_BLANK
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

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
	 * {@link TypeProduitICuService#MESSAGE_DOUBLON} + libellé ;</li>
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
		 * - émet le message MESSAGE_DOUBLON + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(TypeProduitICuService.MESSAGE_DOUBLON + VETEMENT);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_DOUBLON + VETEMENT);

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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
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
		 * PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER + LECTURE_TECHNIQUE_KO.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
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
						TypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
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
		 * PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
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
						TypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
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
	 * {@link TypeProduitICuService#MESSAGE_CREATION_TECHNIQUE_KO_CREER} ;</li>
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
				.hasMessage(TypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
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
						TypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
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
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
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
						TypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_RETOUR_NULL)
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
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que le Gateway a bien été sollicité une seule fois. */
		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_AVEC_MESSAGE)
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
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche Gateway. */
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_AVEC_MESSAGE)
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
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY_BIS.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_AVEC_MESSAGE)
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
		 * KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + MESSAGE_GATEWAY.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE)
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
	@Test
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NULL)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_BLANK)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE)
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
	@Test
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL)
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
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_NULL)
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
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_LIBELLE_NULL)
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
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_BLANK)
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
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTO_NOMINAL)
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
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NULL)
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
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_RETOUR_NULL)
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente pas de conversion finale en {@link OutputDTO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_GATEWAY_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
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
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
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
	@Test
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NOMINAL)
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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NULL)
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

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_LIBELLE_NULL)
	public void testUpdateLibelleNull() throws Exception {

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

		/* ACT - ASSERT */
		/* Garantit que service.update(dto) :
		 * - jette ExceptionParametreBlank ;
		 * - émet MESSAGE_PARAM_BLANK ;
		 * - ne sollicite jamais le Gateway.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_BLANK)
	public void testUpdateBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO dont le libellé métier est blank.
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

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que update(recherche KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée pendant la recherche ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_KO_AVEC_MESSAGE)
	public void testUpdateRechercheKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
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

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_KO_SANS_MESSAGE)
	public void testUpdateRechercheKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre la recherche exacte.
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

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_INTROUVABLE)
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
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NON_PERSISTANT)
	public void testUpdateNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier retrouvé mais non persistant.
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
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_AVEC_MESSAGE)
	public void testUpdateModificationKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier persistant retrouvé avant modification.
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
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_KO_SANS_MESSAGE)
	public void testUpdateModificationKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier persistant retrouvé avant modification.
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
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NULL)
	public void testUpdateModificationRetourNull() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier persistant retrouvé avant modification.
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
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_KO + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT)
	public void testUpdateModificationRetourNonPersistant() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier persistant retrouvé avant modification
		 * et un objet modifié redevenu non persistant.
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
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_AVEC_MESSAGE)
	public void testUpdateConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet modifié mocké dont l'accès aux enfants
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
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTO_KO_SANS_MESSAGE)
	public void testUpdateConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet modifié mocké dont l'accès aux enfants
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
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
						+ libelle
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

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
	@Test
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NOMINAL)
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
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(5L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(5L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________

	
			
	// ============================ delete ================================
	
	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testDeleteNull() {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testDeleteBlank() {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(recherche KO technique avec message) :
	 * panne technique pendant la ré-identification
	 * par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur
	 * avec {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(recherche KO technique sans message) :
	 * panne technique sans message pendant
	 * la ré-identification par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au libellé exact.</p>
	 * <ul>
	 * <li>ne jette aucune exception</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(introuvable) : aucune exception + message MESSAGE_OBJ_INTROUVABLE + libellé + gateway.delete jamais appelé")
	public void testDeleteIntrouvable() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = VETEMENT;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		when(gateway.findByLibelle(libelle)).thenReturn(null);

		// ===================== ACT =====================
		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(non persistant) : l'objet ré-identifié existe
	 * mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE} + libellé</li>
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

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);

		existant.setIdTypeProduit(null);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(KO technique de suppression avec message) :
	 * le Gateway échoue pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_KO} + libellé
	 * + tiret + message technique</li>
	 * <li>délègue bien la destruction sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique avec message) : exception relancée + message MESSAGE_DELETE_KO + détail technique")
	public void testDeleteTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(20L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		final Exception ex = new Exception(MESSAGE_GATEWAY);
		doThrow(ex).when(gateway).delete(tp);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(KO technique de suppression sans message) :
	 * le Gateway échoue sans message pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue bien la destruction sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(21L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		final Exception ex = new Exception();
		doThrow(ex).when(gateway).delete(tp);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>délègue la destruction sur l'objet persistant retrouvé</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : gateway.delete appelé + message MESSAGE_DELETE_OK + libellé")
	public void testDeleteOk() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(15L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		// ===================== ACT =====================
		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

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
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE + détail")
	public void testCountTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.count()).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(KO technique sans message) :
	 * le Gateway échoue pendant le comptage
	 * sans message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCountTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.count()).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(retour négatif) :
	 * le Gateway retourne une valeur incohérente
	 * pour un comptage observable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne un message technique explicite</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(retour négatif) : IllegalStateException + message technique explicite")
	public void testCountRetourNegatifIncoherent() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(-1L);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ "comptage négatif incohérent : -1");

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(0) : aucun résultat en stockage.</p>
	 * <ul>
	 * <li>retourne {@code 0}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(0) : retourne 0 + message MESSAGE_RECHERCHE_VIDE")
	public void testCountZero() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(0L);

		// ===================== ACT =====================
		final long retour = service.count();

		// ===================== ASSERT =====================
		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(positif) : succès nominal du comptage.</p>
	 * <ul>
	 * <li>retourne le comptage exact</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(positif) : retourne le comptage exact + message MESSAGE_RECHERCHE_OK")
	public void testCountPositif() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(42L);

		// ===================== ACT =====================
		final long retour = service.count();

		// ===================== ASSERT =====================
		assertThat(retour).isEqualTo(42L);
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________	

	
	
	// ========================== getMessage ==============================
	
	
	
	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du service Mock.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(initial) : retourne null + aucune interaction gateway")
	public void testGetMessageInitialNull() {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ======================= ACT =======================
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message).isNull();

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après erreur locale) :
	 * retourne le message courant
	 * positionné par une erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>après {@code creer(null)},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ======================= ACT =======================
		service.creer(null);
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès vide) :
	 * retourne le message courant
	 * positionné par un comptage à zéro.</p>
	 * <ul>
	 * <li>après {@code count() == 0},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(0L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès positif) :
	 * retourne le message courant
	 * positionné par un comptage positif.</p>
	 * <ul>
	 * <li>après {@code count() > 0},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(42L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isEqualTo(42L);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) :
	 * une opération plus récente
	 * écrase bien le message précédent.</p>
	 * <ul>
	 * <li>après une erreur locale,
	 * le message vaut d'abord
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>après un {@code count()} positif,
	 * le message courant devient
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(1L);

		// ======================= ACT =======================
		service.creer(null);
		final String messageErreur = service.getMessage();

		final long retour = service.count();
		final String messageFinal = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(messageErreur)
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		assertThat(retour).isEqualTo(1L);
		assertThat(messageFinal)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
}
