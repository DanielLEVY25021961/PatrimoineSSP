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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOProduit;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitCuServiceMockTest.java :</p>
 *
 * <p>
 * Tests unitaires JUnit 5 / Mockito du SERVICE METIER UC
 * {@link ProduitCuService} pour l'objet métier {@link Produit}.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * Cette classe vérifie que {@link ProduitCuService}, point d'entrée
 * dans la logique métier dialoguant directement avec le controller appelant,
 * respecte le contrat du PORT {@link ProduitICuService}.
 * </p>
 *
 * <p>Elle contrôle notamment :</p>
 * <ul>
 * <li>les validations locales des paramètres et des DTO ;</li>
 * <li>les messages utilisateur exposés par {@code getMessage()} ;</li>
 * <li>les conversions entre {@link ProduitDTO.InputDTO},
 * {@link Produit} et {@link ProduitDTO.OutputDTO} ;</li>
 * <li>les scénarios où le parent {@link SousTypeProduit} doit être identifié
 * avant de traiter l'objet métier {@link Produit} ;</li>
 * <li>les scénarios où le parent {@link SousTypeProduit}, lui-même rattaché
 * à un {@link TypeProduit}, détermine l'identité métier observable
 * de l'objet métier {@link Produit} ;</li>
 * <li>les délégations attendues vers
 * {@link ProduitGatewayIService} et {@link SousTypeProduitGatewayIService} ;</li>
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
 * Les {@link ProduitGatewayIService} et
 * {@link SousTypeProduitGatewayIService} sont mockés : ces tests ne valident
 * pas les adaptateurs de stockage, mais le comportement métier observable
 * du SERVICE METIER UC et le contrat de délégation entre le SERVICE METIER UC
 * et les PORTS Gateway.
 * </p>
 * </div>
 *
 * <div>
 * <p>
 * La présence du parent {@link SousTypeProduit}, lui-même rattaché à un
 * {@link TypeProduit}, fait partie du scénario métier propre à
 * {@link ProduitCuService} : le test doit donc vérifier à la fois
 * la validation du DTO, la recherche ou la vérification du parent,
 * puis le traitement de l'objet métier {@link Produit}.
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
 * <li>reprise stricte des blocs déjà validés dans
 * {@link TypeProduitCuServiceMockTest} et
 * {@link SousTypeProduitCuServiceMockTest}, sans réinvention inutile ;</li>
 * <li>vérifications Mockito explicites sur les interactions attendues
 * ou interdites avec les Gateways.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 12 mai 2026
 */
@ExtendWith(MockitoExtension.class)
public class ProduitCuServiceMockTest {

	/** "bazar". */
	public static final String BAZAR = "bazar";

	/** "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** "marteau". */
	public static final String MARTEAU = "marteau";

	/** "quincaillerie". */
	public static final String QUINCAILLERIE = "quincaillerie";

	/** "atelier". */
	public static final String ATELIER = "atelier";

	/** "scie". */
	public static final String SCIE = "scie";

	/** "mar". */
	public static final String CONTENU_RAPIDE = "mar";

	/** "zzz". */
	public static final String CONTENU_ABSENT = "zzz";

	/** "   ". */
	public static final String ESPACES = "   ";

	/** "message gateway". */
	public static final String MESSAGE_GATEWAY = "message gateway";

	/** "message gateway (bis)". */
	public static final String MESSAGE_GATEWAY_BIS = "message gateway (bis)";
	
	/**
	 * "exception propagée + message rationalisé"
	 */
	public static final String EXCEPTION_PROPAGEE_MESSAGE 
		= "exception propagée + message rationalisé";
	
	/**
	 * "fallback MSG_ERREUR_NON_SPECIFIEE"
	 */
	public static final String FALLBACK_MESSAGE 
		= "fallback MSG_ERREUR_NON_SPECIFIEE";

	/** "creer". */
	public static final String TAG_CREER = "creer";

	/** "rechercherTous". */
	public static final String TAG_RECHERCHER_TOUS = "rechercherTous";

	/** "rechercherTousString". */
	public static final String TAG_RECHERCHER_TOUS_STRING = "rechercherTousString";

	/** "rechercherTousParPage". */
	public static final String TAG_RECHERCHER_TOUS_PAR_PAGE = "rechercherTousParPage";

	/** "findByLibelle". */
	public static final String TAG_FIND_BY_LIBELLE = "findByLibelle";

	/** "findByLibelleRapide". */
	public static final String TAG_FIND_BY_LIBELLE_RAPIDE = "findByLibelleRapide";

	/** "findAllByParent". */
	public static final String TAG_FIND_ALL_BY_PARENT = "findAllByParent";

	/** "findByDTO". */
	public static final String TAG_FIND_BY_DTO = "findByDTO";

	/** "findById". */
	public static final String TAG_FIND_BY_ID = "findById";

	/** "update". */
	public static final String TAG_UPDATE = "update";

	/** "delete". */
	public static final String TAG_DELETE = "delete";

	/** "count". */
	public static final String TAG_COUNT = "count";

	/** "getMessage". */
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
					+ FALLBACK_MESSAGE;

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
					+ "IllegalStateException + MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

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
					+ FALLBACK_MESSAGE;

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
					+ FALLBACK_MESSAGE;

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
					+ FALLBACK_MESSAGE;

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

	/** "rechercherTous(gateway retourne null) : MESSAGE_STOCKAGE_NULL". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_RETOUR_NULL
			= "rechercherTous(gateway retourne null) : MESSAGE_STOCKAGE_NULL";

	/** "rechercherTous(gateway KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOAVEC_MESSAGE
			= "rechercherTous(gateway KO avec message) : exception propagée";

	/** "rechercherTous(gateway KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOSANS_MESSAGE
			= "rechercherTous(gateway KO sans message) : exception propagée";

	/** "rechercherTous(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "rechercherTous(conversion OutputDTO KO avec message) : exception propagée";

	/** "rechercherTous(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "rechercherTous(conversion OutputDTO KO sans message) : exception propagée";

	/** "rechercherTous(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE
			= "rechercherTous(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE";

	/** "rechercherTous(nominal) : filtre, trie, dédoublonne + MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL
			= "rechercherTous(nominal) : filtre, trie, dédoublonne + MESSAGE_RECHERCHE_OK";

	/** "rechercherTousString(gateway retourne null) : propage rechercherTous()". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_RETOUR_NULL
			= "rechercherTousString(gateway retourne null) : propage rechercherTous()";

	/** "rechercherTousString(gateway KO avec message) : propage rechercherTous()". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOAVEC_MESSAGE
			= "rechercherTousString(gateway KO avec message) : propage rechercherTous()";

	/** "rechercherTousString(gateway KO sans message) : propage rechercherTous()". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOSANS_MESSAGE
			= "rechercherTousString(gateway KO sans message) : propage rechercherTous()";

	/** "rechercherTousString(conversion KO avec message) : propage rechercherTous()". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOAVEC_MESSAGE
			= "rechercherTousString(conversion KO avec message) : propage rechercherTous()";

	/** "rechercherTousString(conversion KO sans message) : propage rechercherTous()". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOSANS_MESSAGE
			= "rechercherTousString(conversion KO sans message) : propage rechercherTous()";

	/** "rechercherTousString(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE
			= "rechercherTousString(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE";

	/** "rechercherTousString(libellés blank) : liste vide + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK
			= "rechercherTousString(libellés blank) : liste vide + MESSAGE_RECHERCHE_VIDE";

	/** "rechercherTousString(nominal) : libellés triés + MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL
			= "rechercherTousString(nominal) : libellés triés + MESSAGE_RECHERCHE_OK";

	/** "rechercherTousParPage(null) : MESSAGE_PAGEABLE_NULL". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL
			= "rechercherTousParPage(null) : MESSAGE_PAGEABLE_NULL";

	/** "rechercherTousParPage(gateway KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOAVEC_MESSAGE
			= "rechercherTousParPage(gateway KO avec message) : exception propagée";

	/** "rechercherTousParPage(gateway KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOSANS_MESSAGE
			= "rechercherTousParPage(gateway KO sans message) : exception propagée";

	/** "rechercherTousParPage(gateway retourne null) : MESSAGE_RECHERCHE_PAGINEE_KO". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL
			= "rechercherTousParPage(gateway retourne null) : MESSAGE_RECHERCHE_PAGINEE_KO";

	/** "rechercherTousParPage(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO avec message) : exception propagée";

	/** "rechercherTousParPage(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "rechercherTousParPage(conversion OutputDTO KO sans message) : exception propagée";

	/** "rechercherTousParPage(vide après filtrage) : page vide + MESSAGE_RECHERCHE_PAGINEE_OK". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE
			= "rechercherTousParPage(vide après filtrage) : page vide + MESSAGE_RECHERCHE_PAGINEE_OK";

	/** "rechercherTousParPage(nominal) : page cohérente + MESSAGE_RECHERCHE_PAGINEE_OK". */
	public static final String DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL
			= "rechercherTousParPage(nominal) : page cohérente + MESSAGE_RECHERCHE_PAGINEE_OK";

	/** "findByLibelle(null) : null + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NULL
			= "findByLibelle(null) : null + MESSAGE_PARAM_BLANK";

	/** "findByLibelle(blank) : null + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_BLANK
			= "findByLibelle(blank) : null + MESSAGE_PARAM_BLANK";

	/** "findByLibelle(gateway retourne null) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL
			= "findByLibelle(gateway retourne null) : KO_TECHNIQUE_RECHERCHE";

	/** "findByLibelle(gateway KO avec message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOAVEC_MESSAGE
			= "findByLibelle(gateway KO avec message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelle(gateway KO sans message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOSANS_MESSAGE
			= "findByLibelle(gateway KO sans message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelle(conversion OutputDTO KO avec message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "findByLibelle(conversion OutputDTO KO avec message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelle(conversion OutputDTO KO sans message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "findByLibelle(conversion OutputDTO KO sans message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelle(introuvable) : liste vide + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_INTROUVABLE
			= "findByLibelle(introuvable) : liste vide + MESSAGE_RECHERCHE_VIDE";

	/** "findByLibelle(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL
			= "findByLibelle(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK";

	/** "findByLibelleRapide(null) : MESSAGE_PARAM_NULL". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL
			= "findByLibelleRapide(null) : MESSAGE_PARAM_NULL";

	/** "findByLibelleRapide(blank) : délègue à rechercherTous()". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK
			= "findByLibelleRapide(blank) : délègue à rechercherTous()";

	/** "findByLibelleRapide(gateway KO avec message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOAVEC_MESSAGE
			= "findByLibelleRapide(gateway KO avec message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelleRapide(gateway KO sans message) : exception propagée par l'ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOSANS_MESSAGE
			= "findByLibelleRapide(gateway KO sans message) : exception propagée par l'ADAPTER réel";

	/** "findByLibelleRapide(gateway retourne null) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL
			= "findByLibelleRapide(gateway retourne null) : KO_TECHNIQUE_RECHERCHE";

	/** "findByLibelleRapide(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO avec message) : exception propagée";

	/** "findByLibelleRapide(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "findByLibelleRapide(conversion OutputDTO KO sans message) : exception propagée";

	/** "findByLibelleRapide(vide après filtrage) : liste vide". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE
			= "findByLibelleRapide(vide après filtrage) : liste vide";

	/** "findByLibelleRapide(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL
			= "findByLibelleRapide(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK";

	/** "findAllByParent(null) : RECHERCHE_PARENT_NULL". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL
			= "findAllByParent(null) : RECHERCHE_PARENT_NULL";

	/** "findAllByParent(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK
			= "findAllByParent(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "findAllByParent(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOAVEC_MESSAGE
			= "findAllByParent(recherche parent KO avec message) : exception propagée";

	/** "findAllByParent(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOSANS_MESSAGE
			= "findAllByParent(recherche parent KO sans message) : exception propagée";

	/** "findAllByParent(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT
			= "findAllByParent(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "findAllByParent(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT
			= "findAllByParent(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "findAllByParent(recherche enfants KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOAVEC_MESSAGE
			= "findAllByParent(recherche enfants KO avec message) : exception propagée";

	/** "findAllByParent(recherche enfants KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOSANS_MESSAGE
			= "findAllByParent(recherche enfants KO sans message) : exception propagée";

	/** "findAllByParent(gateway retourne null) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_GATEWAY_RETOUR_NULL
			= "findAllByParent(gateway retourne null) : KO_TECHNIQUE_RECHERCHE";

	/** "findAllByParent(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "findAllByParent(conversion OutputDTO KO avec message) : exception propagée";

	/** "findAllByParent(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "findAllByParent(conversion OutputDTO KO sans message) : exception propagée";

	/** "findAllByParent(vide après filtrage) : liste vide selon ADAPTER réel". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_VIDE_APRES_FILTRAGE
			= "findAllByParent(vide après filtrage) : liste vide selon ADAPTER réel";

	/** "findAllByParent(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NOMINAL
			= "findAllByParent(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK";

	/** "findByDTO(null) : MESSAGE_RECHERCHE_OBJ_NULL". */
	public static final String DISPLAY_NAME_FIND_BY_DTONULL
			= "findByDTO(null) : MESSAGE_RECHERCHE_OBJ_NULL";

	/** "findByDTO(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_FIND_BY_DTOPARENT_BLANK
			= "findByDTO(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "findByDTO(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_AVEC_MESSAGE
			= "findByDTO(recherche parent KO avec message) : exception propagée";

	/** "findByDTO(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_SANS_MESSAGE
			= "findByDTO(recherche parent KO sans message) : exception propagée";

	/** "findByDTO(parent absent) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOPARENT_ABSENT
			= "findByDTO(parent absent) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(parent non persistant) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOPARENT_NON_PERSISTANT
			= "findByDTO(parent non persistant) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(recherche enfants KO avec message) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_AVEC_MESSAGE
			= "findByDTO(recherche enfants KO avec message) : KO_TECHNIQUE_RECHERCHE";

	/** "findByDTO(recherche enfants KO sans message) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_SANS_MESSAGE
			= "findByDTO(recherche enfants KO sans message) : KO_TECHNIQUE_RECHERCHE";

	/** "findByDTO(gateway retourne null) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOGATEWAY_RETOUR_NULL
			= "findByDTO(gateway retourne null) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(vide) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOVIDE
			= "findByDTO(vide) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(vide après filtrage) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOVIDE_APRES_FILTRAGE
			= "findByDTO(vide après filtrage) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(introuvable dans liste) : null + MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOINTROUVABLE_DANS_LISTE
			= "findByDTO(introuvable dans liste) : null + MESSAGE_RECHERCHE_VIDE";

	/** "findByDTO(conversion OutputDTO KO avec message) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "findByDTO(conversion OutputDTO KO avec message) : KO_TECHNIQUE_RECHERCHE";

	/** "findByDTO(conversion OutputDTO KO sans message) : KO_TECHNIQUE_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "findByDTO(conversion OutputDTO KO sans message) : KO_TECHNIQUE_RECHERCHE";

	/** "findByDTO(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_DTONOMINAL
			= "findByDTO(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE";

	/** "findById(null) : MESSAGE_PARAM_NULL". */
	public static final String DISPLAY_NAME_FIND_BY_ID_NULL
			= "findById(null) : MESSAGE_PARAM_NULL";

	/** "findById(introuvable) : MESSAGE_OBJ_INTROUVABLE + id". */
	public static final String DISPLAY_NAME_FIND_BY_ID_INTROUVABLE
			= "findById(introuvable) : MESSAGE_OBJ_INTROUVABLE + id";

	/** "findById(gateway KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_AVEC_MESSAGE
			= "findById(gateway KO avec message) : exception propagée";

	/** "findById(gateway KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_SANS_MESSAGE
			= "findById(gateway KO sans message) : exception propagée";

	/** "findById(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "findById(conversion OutputDTO KO avec message) : exception propagée";

	/** "findById(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "findById(conversion OutputDTO KO sans message) : exception propagée";

	/** "findById(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE". */
	public static final String DISPLAY_NAME_FIND_BY_ID_NOMINAL
			= "findById(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE";

	/** "update(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL". */
	public static final String DISPLAY_NAME_UPDATE_NULL
			= "update(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL";

	/** "update(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_UPDATE_LIBELLE_NULL
			= "update(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK";

	/** "update(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_UPDATE_BLANK
			= "update(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK";

	/** "update(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_BLANK
			= "update(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "update(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE
			= "update(recherche parent KO avec message) : exception propagée";

	/** "update(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE
			= "update(recherche parent KO sans message) : exception propagée";

	/** "update(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_ABSENT
			= "update(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "update(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_NON_PERSISTANT
			= "update(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "update(recherche enfants KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE
			= "update(recherche enfants KO avec message) : exception propagée";

	/** "update(recherche enfants KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE
			= "update(recherche enfants KO sans message) : exception propagée";

	/** "update(stockage null pendant ré-identification) : null + MESSAGE_OBJ_INTROUVABLE". */
	public static final String DISPLAY_NAME_UPDATE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION
			= "update(stockage null pendant ré-identification) : null + MESSAGE_OBJ_INTROUVABLE";

	/** "update(introuvable) : null + MESSAGE_OBJ_INTROUVABLE". */
	public static final String DISPLAY_NAME_UPDATE_INTROUVABLE
			= "update(introuvable) : null + MESSAGE_OBJ_INTROUVABLE";

	/** "update(non persistant) : ExceptionNonPersistant". */
	public static final String DISPLAY_NAME_UPDATE_NON_PERSISTANT
			= "update(non persistant) : ExceptionNonPersistant";

	/** "update(modification technique KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_AVEC_MESSAGE
			= "update(modification technique KO avec message) : exception propagée";

	/** "update(modification technique KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_SANS_MESSAGE
			= "update(modification technique KO sans message) : exception propagée";

	/** "update(modification retourne null) : null + MESSAGE_MODIF_KO". */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NULL
			= "update(modification retourne null) : null + MESSAGE_MODIF_KO";

	/** "update(modification retourne non persistant) : ExceptionNonPersistant". */
	public static final String DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT
			= "update(modification retourne non persistant) : ExceptionNonPersistant";

	/** "update(conversion OutputDTO KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE
			= "update(conversion OutputDTO KO avec message) : exception propagée";

	/** "update(conversion OutputDTO KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE
			= "update(conversion OutputDTO KO sans message) : exception propagée";

	/** "update(nominal) : OutputDTO cohérent + ID conservé". */
	public static final String DISPLAY_NAME_UPDATE_NOMINAL
			= "update(nominal) : OutputDTO cohérent + ID conservé";

	/** "delete(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL". */
	public static final String DISPLAY_NAME_DELETE_NULL
			= "delete(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL";

	/** "delete(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_DELETE_LIBELLE_NULL
			= "delete(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK";

	/** "delete(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK". */
	public static final String DISPLAY_NAME_DELETE_BLANK
			= "delete(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK";

	/** "delete(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_DELETE_PARENT_BLANK
			= "delete(parent blank) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "delete(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE
			= "delete(recherche parent KO avec message) : exception propagée";

	/** "delete(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE
			= "delete(recherche parent KO sans message) : exception propagée";

	/** "delete(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_DELETE_PARENT_ABSENT
			= "delete(parent absent) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "delete(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO". */
	public static final String DISPLAY_NAME_DELETE_PARENT_NON_PERSISTANT
			= "delete(parent non persistant) : MESSAGE_CREER_PARENT_NON_PERSISTANT_KO";

	/** "delete(recherche enfants KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE
			= "delete(recherche enfants KO avec message) : exception propagée";

	/** "delete(recherche enfants KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE
			= "delete(recherche enfants KO sans message) : exception propagée";

	/** "delete(stockage null pendant ré-identification) : MESSAGE_STOCKAGE_NULL". */
	public static final String DISPLAY_NAME_DELETE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION
			= "delete(stockage null pendant ré-identification) : MESSAGE_STOCKAGE_NULL";

	/** "delete(introuvable) : MESSAGE_OBJ_INTROUVABLE + aucune suppression". */
	public static final String DISPLAY_NAME_DELETE_INTROUVABLE
			= "delete(introuvable) : MESSAGE_OBJ_INTROUVABLE + aucune suppression";

	/** "delete(non persistant) : ExceptionNonPersistant". */
	public static final String DISPLAY_NAME_DELETE_NON_PERSISTANT
			= "delete(non persistant) : ExceptionNonPersistant";

	/** "delete(destruction KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_DESTRUCTION_KOAVEC_MESSAGE
			= "delete(destruction KO avec message) : exception propagée";

	/** "delete(destruction KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_DESTRUCTION_KOSANS_MESSAGE
			= "delete(destruction KO sans message) : exception propagée";

	/** "delete(nominal) : suppression sur le couple parent/libellé". */
	public static final String DISPLAY_NAME_DELETE_NOMINAL
			= "delete(nominal) : suppression sur le couple parent/libellé";

	/** "count(gateway KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KOAVEC_MESSAGE
			= "count(gateway KO avec message) : exception propagée";

	/** "count(gateway KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_COUNT_GATEWAY_KOSANS_MESSAGE
			= "count(gateway KO sans message) : exception propagée";

	/** "count(retour négatif) : IllegalStateException". */
	public static final String DISPLAY_NAME_COUNT_RETOUR_NEGATIF
			= "count(retour négatif) : IllegalStateException";

	/** "count(0) : MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_COUNT_ZERO
			= "count(0) : MESSAGE_RECHERCHE_VIDE";

	/** "count(nominal) : MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_COUNT_NOMINAL
			= "count(nominal) : MESSAGE_RECHERCHE_OK";

	/** "getMessage(initial) : null". */
	public static final String DISPLAY_NAME_GET_MESSAGE_INITIAL_NULL
			= "getMessage(initial) : null";

	/** "getMessage(après erreur locale) : MESSAGE_CREER_NULL". */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_ERREUR_LOCALE
			= "getMessage(après erreur locale) : MESSAGE_CREER_NULL";

	/** "getMessage(après count zéro) : MESSAGE_RECHERCHE_VIDE". */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_ZERO
			= "getMessage(après count zéro) : MESSAGE_RECHERCHE_VIDE";

	/** "getMessage(après count nominal) : MESSAGE_RECHERCHE_OK". */
	public static final String DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_NOMINAL
			= "getMessage(après count nominal) : MESSAGE_RECHERCHE_OK";

	/** "getMessage(dernier message gagne) : dernier message observable". */
	public static final String DISPLAY_NAME_GET_MESSAGE_DERNIER_MESSAGE_GAGNE
			= "getMessage(dernier message gagne) : dernier message observable";

	// ************************* CONSTRUCTEURS *****************************/


	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitCuServiceMockTest() {
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
	 * {@link ProduitICuService#MESSAGE_CREER_NULL_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway Produit
	 * ni avec le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_CREER_LIBELLE_BLANK_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway Produit
	 * ni avec le Gateway parent SousTypeProduit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionParametreBlank ;
		 * - émet le message MESSAGE_CREER_LIBELLE_BLANK_KO
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(ProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_LIBELLE_BLANK_KO);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(libellé parent blank) :</p>
	 * <ul>
	 * <li>contrôle localement le libellé du parent direct ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO} ;</li>
	 * <li>n'interagit ni avec le Gateway Produit
	 * ni avec le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_BLANK)
	@Test
	public void testCreerParentBlank() {

		/* ARRANGE :
		 * prépare un DTO dont le libellé du parent direct est blank.
		 *
		 * Le SERVICE METIER UC doit refuser ce parent avant
		 * toute délégation aux Gateways.
		 */
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une IllegalStateException ;
		 * - émet le message MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						ProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_PARENT_LIBELLE_BLANK_KO);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent KO avec message) :</p>
	 * <ul>
	 * <li>atteint la récupération du parent persistant via
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway parent ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO}
	 * + message technique ;</li>
	 * <li>ne sollicite jamais le Gateway Produit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * simule une panne technique de
		 * sousTypeProduitGateway.findByLibelle(...)
		 * pendant la récupération du parent persistant.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
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
						ProduitICuService
								.PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO
						+ MESSAGE_GATEWAY);

		/* Garantit que seule la recherche parent a été atteinte. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(parent KO sans message) :</p>
	 * <ul>
	 * <li>atteint la récupération du parent persistant via
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception sans message levée par le Gateway parent ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne sollicite jamais le Gateway Produit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message de
		 * sousTypeProduitGateway.findByLibelle(...).
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
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
						ProduitICuService
								.PREFIX_MESSAGE_CREER_RECHERCHE_PARENT_KO
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que seule la recherche parent a été atteinte. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway Produit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un parent non trouvé dans le stockage.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) refuse un parent absent. */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* Garantit que le scénario s'arrête après la recherche parent. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
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
	 * {@link ProduitICuService#MESSAGE_CREER_PARENT_NON_PERSISTANT_KO} ;</li>
	 * <li>ne sollicite jamais le Gateway Produit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentNonPersistant = parentNonPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un parent trouvé mais non persistant.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant));

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) refuse un parent non persistant. */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService
								.MESSAGE_CREER_PARENT_NON_PERSISTANT_KO);

		/* Garantit que le scénario s'arrête après la recherche parent. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(doublon) :</p>
	 * <ul>
	 * <li>récupère d'abord le parent persistant ;</li>
	 * <li>contrôle l'unicité via {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} interroge le Gateway Produit via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>jette une {@link ExceptionDoublon} si un même libellé existe
	 * déjà sous le même parent ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_CREER_DOUBLON_KO} + libellé ;</li>
	 * <li>ne délègue jamais la création au Gateway Produit.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		final Produit doublon = produit(MARTEAU, parentPersistant, 100L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - le Gateway Produit retourne un objet portant le même couple
		 *   [parent, libellé].
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(doublon));

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionDoublon ;
		 * - émet le message MESSAGE_CREER_DOUBLON_KO + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(
						ProduitICuService.MESSAGE_CREER_DOUBLON_KO
						+ MARTEAU);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_CREER_DOUBLON_KO
						+ MARTEAU);

		/* Garantit que le parent et le contrôle d'unicité ont été exécutés,
		 * et que la création n'a jamais été déléguée au Gateway Produit.
		 */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - le contrôle de doublon échoue techniquement.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

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
						ProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ MESSAGE_GATEWAY);

		/* Garantit que la création n'a jamais été appelée. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_DOUBLON_KO}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - le contrôle de doublon échoue techniquement sans message.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

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
						ProduitICuService.PREFIX_MESSAGE_CREER_DOUBLON_KO
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création n'a jamais été appelée. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - la création technique échoue avec message.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class)))
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
						ProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_GATEWAY_KO}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - la création technique échoue sans message.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class)))
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
						ProduitICuService.PREFIX_MESSAGE_CREER_GATEWAY_KO
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway. */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(any(Produit.class));

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
	 * {@link ProduitICuService#MESSAGE_CREER_GATEWAY_KO}.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * - le parent est persistant ;
		 * - aucun doublon n'est trouvé ;
		 * - gateway.creer(...) retourne null.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class))).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le succès apparent
		 * et refuse une réponse technique null.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(
						ProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_CREER_GATEWAY_KO);

		/* Garantit que le scénario a atteint la création Gateway,
		 * puis que l'anomalie null est traitée côté UC.
		 */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		final Produit cree = produitConversionKo(panneTechnique);

		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class))).thenReturn(cree);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREER_CONVERSION_KO}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit cree = produitConversionKo(panneTechnique);

		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class))).thenReturn(cree);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CREER_CONVERSION_KO
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(any(Produit.class));

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
	 * {@link ProduitICuService#MESSAGE_CREER_CONVERSION_KO}.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		final Produit cree = produit(MARTEAU, parentPersistant, 100L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class))).thenReturn(cree);
		
		/*
		 * Configuration du MockedStatic :
		 * - le convertisseur réel est une classe utilitaire static ;
		 * - avec un objet métier non null, il ne retourne normalement pas null ;
		 * - gateway.creer(...) ne peut pas retourner null ici,
		 *   car le SERVICE METIER UC s'arrêterait avant la conversion ;
		 * - le MockedStatic est donc limité à ce test pour atteindre
		 *   la branche défensive "dto == null" du SERVICE METIER UC.
		 */
		try (MockedStatic<ConvertisseurMetierToOutputDTOProduit> mockedStatic
				= mockStatic(ConvertisseurMetierToOutputDTOProduit.class)) {
			
			/*
			 * Configuration du MockedStatic :
			 * la conversion finale en OutputDTO retourne null.
			 */
			mockedStatic.when(
					() -> ConvertisseurMetierToOutputDTOProduit
							.convert(cree))
					.thenReturn(null);

			/* ACT - ASSERT */
			/* Garantit que le SERVICE METIER UC refuse
			 * une conversion finale null.
			 */
			assertThatThrownBy(() -> service.creer(dto))
					.isInstanceOf(IllegalStateException.class)
					.hasMessage(
							ProduitICuService
									.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le message utilisateur correspond
			 * au cas contractuel "conversion retourne null".
			 */
			assertThat(service.getMessage())
					.isEqualTo(
							ProduitICuService
									.MESSAGE_CREER_CONVERSION_KO);

			/* Garantit que le scénario a atteint la création Gateway
			 * avant le contrôle du retour null de conversion.
			 */
			verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
			verify(gateway, times(1)).findByLibelle(MARTEAU);
			verify(gateway, times(1)).creer(any(Produit.class));
			
			/* Garantit que le MockedStatic a été strictement limité
			 * à la conversion finale attendue par le SERVICE METIER UC.
			 */
			mockedStatic.verify(
					() -> ConvertisseurMetierToOutputDTOProduit
							.convert(cree),
					times(1));
		}

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>récupère le parent persistant via
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>contrôle l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché
	 * au parent persistant ;</li>
	 * <li>délègue la création à {@code gateway.creer(...)} ;</li>
	 * <li>convertit l'objet métier créé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant généré,
	 * le bon libellé et le bon parent ;</li>
	 * <li>positionne le message
	 * {@link ProduitICuService#MESSAGE_CREER_OK}.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parentPersistant = parentPersistant();
		final Produit cree = produit(MARTEAU, parentPersistant, 100L);
		
		final ArgumentCaptor<Produit> captor
				= ArgumentCaptor.forClass(Produit.class);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		
		/*
		 * Configuration du Mock :
		 * - findByLibelle(...) sur le Gateway parent retourne le parent
		 *   persistant ;
		 * - findByLibelle(...) sur le Gateway Produit retourne une liste vide
		 *   pour simuler l'absence de doublon fonctionnel ;
		 * - creer(...) retourne l'objet métier réellement créé
		 *   avec l'identifiant généré par le stockage.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(gateway.creer(any(Produit.class))).thenReturn(cree);

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
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, times(1)).creer(captor.capture());

		/* Garantit que l'objet métier envoyé au Gateway Produit :
		 * - n'est pas null ;
		 * - ne porte pas encore d'identifiant ;
		 * - porte le libellé métier issu de l'InputDTO ;
		 * - porte le parent persistant retrouvé via le Gateway parent.
		 */
		final Produit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdProduit()).isNull();
		assertThat(envoye.getProduit()).isEqualTo(MARTEAU);
		assertThat(envoye.getSousTypeProduit()).isNotNull();
		assertThat(envoye.getSousTypeProduit().getSousTypeProduit())
				.isEqualTo(OUTILLAGE);
		assertThat(envoye.getSousTypeProduit().getIdSousTypeProduit())
				.isEqualTo(10L);
		assertThat(envoye.getSousTypeProduit().getTypeProduit()).isNotNull();
		assertThat(envoye.getSousTypeProduit().getTypeProduit().getTypeProduit())
				.isEqualTo(BAZAR);
		assertThat(envoye.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
				.isEqualTo(1L);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant généré ;
		 * - porte le bon libellé métier ;
		 * - porte le bon parent ;
		 * - expose le message utilisateur de succès.
		 */
		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

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
	 * {@link ProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway objet métier qui retourne null
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
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway objet métier
		 * a été sollicité pour la recherche exhaustive.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway objet métier ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOAVEC_MESSAGE)
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway objet métier.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * objet métier ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOSANS_MESSAGE)
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway objet métier.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne sans message pendant la conversion en OutputDTO.
		 */
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>garantit que rechercherTous(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final List<Produit> records = Arrays.asList(null, null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final SousTypeProduit parent = parentPersistant();
		
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		final Produit produitMarteauDoublon = produit(MARTEAU, parent, 1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne des objets métier dans un ordre
		 * non trié, avec un null et un doublon côté DTO.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						produitScie, null, produitMarteau, produitMarteauDoublon));

		/* ACT :
		 * exécute la recherche exhaustive via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
		 * - est triée par libellé métier ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour)
				.extracting(OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	


	// ================== rechercherTousString ============================


	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway retourne null) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>détecte que le Gateway retourne {@code null} ;</li>
	 * <li>lève {@link ExceptionStockageVide} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway objet métier qui retourne null
		 * au lieu d'une liste non null attendue par rechercherTous().
		 */
		when(gateway.rechercherTous()).thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousString() :
		 * - propage ExceptionStockageVide ;
		 * - conserve le message MESSAGE_STOCKAGE_NULL contractuel.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway objet métier
		 * a été sollicité pour la recherche exhaustive.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>propage l'exception technique levée par le Gateway objet métier ;</li>
	 * <li>conserve le message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOAVEC_MESSAGE)
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique avec message au moment
		 * de l'appel gateway.rechercherTous() déclenché par rechercherTous().
		 */
		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC conserve
		 * le message utilisateur rationalisé préparé par rechercherTous().
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway objet métier.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * objet métier ;</li>
	 * <li>conserve un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOSANS_MESSAGE)
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule une panne technique sans message au moment
		 * de l'appel gateway.rechercherTous() déclenché par rechercherTous().
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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche
		 * exhaustive du Gateway objet métier.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * réalisée par {@code rechercherTous()} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>conserve le message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont la conversion en OutputDTO
		 * échoue avant l'extraction finale des libellés String.
		 */
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC conserve
		 * le message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>filtre les {@code null} et trie les objets métier ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * réalisée par {@code rechercherTous()} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>conserve un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont la conversion en OutputDTO
		 * échoue avant l'extraction finale des libellés String.
		 */
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après filtrage) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>retourne une liste de libellés non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final List<Produit> records = Arrays.asList(null, null);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après libellés blank) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>extrait les libellés via {@code OutputDTO.getProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>retourne une liste non {@code null} et vide ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final SousTypeProduit parent = parentPersistant();
		final Produit produitBlank = produit(ESPACES, parent, 1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne une liste non null
		 * dont les seuls libellés non null sont blank.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, produitBlank));

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
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.rechercherTous()} via
	 * {@code rechercherTous()} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>extrait les libellés via {@code OutputDTO.getProduit()} ;</li>
	 * <li>ignore les libellés blank ;</li>
	 * <li>retourne les libellés exploitables dans l'ordre préparé
	 * par {@code rechercherTous()} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		 * - un doublon à dédoublonner côté DTO.
		 */
		final SousTypeProduit parent = parentPersistant();
		
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		final Produit produitBlank = produit(ESPACES, parent, 3L);
		final Produit produitMarteauDoublon = produit(MARTEAU, parent, 1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.rechercherTous() retourne des objets métier dans un ordre
		 * non trié, avec un null, un libellé blank et un doublon côté DTO.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(
						produitScie, null, produitBlank, produitMarteau, 
						produitMarteauDoublon));

		/* ACT :
		 * exécute la recherche exhaustive String via le SERVICE METIER UC.
		 */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les libellés non blank ;
		 * - conserve l'ordre préparé par rechercherTous() ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly(MARTEAU, SCIE);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* Garantit que la recherche exhaustive a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	


	// ================== rechercherTousParPage ===========================


	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(null) :</p>
	 * <ul>
	 * <li>refuse une requête de pagination {@code null} ;</li>
	 * <li>lève une {@link IllegalStateException} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAGEABLE_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT */
		/* Garantit que service.rechercherTousParPage(null) :
		 * - lève IllegalStateException ;
		 * - émet le message MESSAGE_PAGEABLE_NULL contractuel ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway objet métier ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 4);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche paginée
		 * du Gateway objet métier, sans solliciter le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel
	 * {@code gateway.rechercherTousParPage(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * objet métier ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une requête de pagination valide pour atteindre
		 * réellement la délégation gateway.rechercherTousParPage(...).
		 */
		final RequetePage requete = new RequetePage(0, 4);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche paginée
		 * du Gateway objet métier, sans solliciter le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final RequetePage requete = new RequetePage(0, 4);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
				.hasMessage(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		/* Garantit que le Gateway objet métier a bien été sollicité
		 * une seule fois, sans interaction avec le Gateway parent.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		final ResultatPage<Produit> resultatGateway
				= new ResultatPage<Produit>(
						Arrays.asList(produitKo),
						0,
						4,
						1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique avec message.
		 */

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare une réponse paginée Gateway non null contenant
		 * un objet métier mocké dont l'accès au parent provoque
		 * une panne pendant la conversion en OutputDTO.
		 */
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		final ResultatPage<Produit> resultatGateway
				= new ResultatPage<Produit>(
						Arrays.asList(produitKo),
						0,
						4,
						1L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/*
		 * Configuration du Mock :
		 * la conversion en OutputDTO lit le parent de l'objet métier ;
		 * cet accès déclenche ici une panne technique sans message.
		 */

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche paginée Gateway
		 * avant l'échec de préparation côté UC.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		 * aucun objet métier non null après filtrage.
		 */
		final RequetePage requete = new RequetePage(0, 4);
		
		final List<Produit> records = Arrays.asList(null, null);
		
		final ResultatPage<Produit> resultatGateway
				= new ResultatPage<Produit>(
						records,
						0,
						4,
						2L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
		assertThat(retour.getTotalElements()).isEqualTo(2L);

		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent()).isEmpty();

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		
		final SousTypeProduit parent = parentPersistant();
		
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		final Produit produitMarteauDoublon = produit(MARTEAU, parent, 1L);
		
		final ResultatPage<Produit> resultatGateway
				= new ResultatPage<Produit>(
						Arrays.asList(
								produitScie,
								null,
								produitMarteau,
								produitMarteauDoublon),
						0,
						4,
						10L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
				.extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* Garantit que la recherche paginée a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	


	// ====================== findByLibelle ===============================


	
	/**
	 * <div>
	 * <p>garantit que findByLibelle(null) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT :
		 * exécute la recherche exacte avec un libellé null.
		 */
		final List<OutputDTO> retour = service.findByLibelle(null);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(blank) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT :
		 * exécute la recherche exacte avec un libellé blank.
		 */
		final List<OutputDTO> retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que l'erreur utilisateur bénigne :
		 * - retourne null ;
		 * - positionne MESSAGE_PARAM_BLANK ;
		 * - ne sollicite aucun Gateway.
		 */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_STOCKAGE_NULL} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final String libelle = MARTEAU;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway objet métier qui retourne null
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
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		/* Garantit que seul le Gateway objet métier
		 * a été sollicité pour la recherche exacte.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique levée par le Gateway objet métier ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = MARTEAU;
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * du Gateway objet métier.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>propage l'exception technique sans message levée par le Gateway
	 * objet métier ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un libellé valide pour atteindre réellement
		 * la délégation gateway.findByLibelle(...).
		 */
		final String libelle = MARTEAU;
		final IllegalStateException panneTechnique = new IllegalStateException();
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

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
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la panne intervient bien sur la recherche exacte
		 * du Gateway objet métier.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne une liste non {@code null}
	 * contenant un objet métier non null ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + message technique ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = MARTEAU;
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>le Gateway retourne une liste non {@code null}
	 * contenant un objet métier non null ;</li>
	 * <li>atteint la conversion finale en {@link OutputDTO}
	 * via {@code ConvertisseurMetierToOutputDTOProduit.convertList(...)} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un objet métier mocké dont l'accès au parent
		 * provoque une panne pendant la conversion en OutputDTO.
		 */
		final String libelle = MARTEAU;
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne une liste non null contenant
		 * un objet métier non null retenu par filtrerEtTrier(...).
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la recherche Gateway
		 * avant l'échec de conversion côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final String libelle = MARTEAU;
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * simule un Gateway qui ne trouve aucun objet métier
		 * pour le libellé recherché.
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Collections.emptyList());

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
		assertThat(message).isEqualTo(
				ProduitCuService.MESSAGE_OBJ_INTROUVABLE
				+ libelle);
		
		/* Garantit que la recherche exacte a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(OK) :</p>
	 * <ul>
	 * <li>atteint l'appel {@code gateway.findByLibelle(...)} ;</li>
	 * <li>filtre les éléments {@code null} ;</li>
	 * <li>trie les objets métier par libellé produit ;</li>
	 * <li>convertit les objets métier en {@link OutputDTO} ;</li>
	 * <li>dédoublonne la réponse DTO ;</li>
	 * <li>retourne une liste cohérente portant les parents
	 * et le libellé recherché ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK} ;</li>
	 * <li>n'interagit jamais avec le Gateway parent.</li>
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
		final String libelle = MARTEAU;
		
		final SousTypeProduit parentOutillage = parentPersistant();
		final SousTypeProduit parentAtelier 
				= parentPersistant(QUINCAILLERIE, ATELIER, 2L, 20L);
		
		final Produit produitOutillage = produit(libelle, parentOutillage, 100L);
		final Produit produitAtelier = produit(libelle, parentAtelier, 200L);
		final Produit produitOutillageDoublon = produit(libelle, parentOutillage, 100L);
		
		/* 
		 * Mocke les services Gateway et les passe 
		 * à un service UC instancié dans le test. 
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/*
		 * Configuration du Mock :
		 * gateway.findByLibelle(...) retourne des objets métier dans un ordre
		 * exploitable, avec un null et un doublon côté DTO.
		 */
		when(gateway.findByLibelle(libelle))
				.thenReturn(Arrays.asList(
						produitOutillage,
						null,
						produitAtelier,
						produitOutillageDoublon));

		/* ACT :
		 * exécute la recherche exacte via le SERVICE METIER UC.
		 */
		final List<OutputDTO> retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		/* ASSERT */
		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - contient uniquement les objets métier non null convertis en OutputDTO ;
		 * - est dédoublonnée ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getProduit)
				.containsExactly(libelle, libelle);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, ATELIER);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, QUINCAILLERIE);

		assertThat(retour)
				.extracting(OutputDTO::getIdProduit)
				.containsExactly(100L, 200L);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_FINDBYLIBELLE_SUCCES_RECHERCHE);

		/* Garantit que la recherche exacte a bien été déléguée
		 * et que le Gateway parent reste inutilisé.
		 */
		verify(gateway, times(1)).findByLibelle(libelle);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________
	


	// ==================== findByLibelleRapide ===========================


	
	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_PARAM_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « délègue à rechercherTous() » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final Produit produit = produit(MARTEAU, parent, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTous()).thenReturn(Arrays.asList(produit));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().hasSize(1);
		assertThat(retour.get(0).getProduit()).isEqualTo(MARTEAU);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée par l'ADAPTER réel » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée par l'ADAPTER réel » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(gateway retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE)).thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(CONTENU_RAPIDE))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(null, null));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(CONTENU_RAPIDE);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelleRapide(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste cohérente + MESSAGE_RECHERCHE_OK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelleRapide(CONTENU_RAPIDE);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findAllByParent =================================


	/**
	 * <div>
	 * <p>garantit que findAllByParent(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « RECHERCHE_PARENT_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(null))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.RECHERCHE_PARENT_NULL);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.RECHERCHE_PARENT_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, ESPACES);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent absent) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT)
	@Test
	public void testFindAllByParentParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(recherche enfants KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(recherche enfants KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(gateway retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent)).thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide selon ADAPTER réel » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindAllByParentVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(null, null));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findAllByParent(parentDto);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste cohérente + MESSAGE_RECHERCHE_OK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findAllByParent(parentDto);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findByDTO =================================


	/**
	 * <div>
	 * <p>garantit que findByDTO(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_OBJ_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTONULL)
	@Test
	public void testFindByDTONull() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(null);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(parent blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_BLANK)
	@Test
	public void testFindByDTOParentBlank() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(parent absent) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_ABSENT)
	@Test
	public void testFindByDTOParentAbsent() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(parent non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_NON_PERSISTANT)
	@Test
	public void testFindByDTOParentNonPersistant() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(recherche enfants KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(recherche enfants KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(gateway retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOGATEWAY_RETOUR_NULL)
	@Test
	public void testFindByDTOGatewayRetourNull() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent)).thenReturn(null);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(vide) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOVIDE)
	@Test
	public void testFindByDTOVide() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Collections.emptyList());

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOVIDE_APRES_FILTRAGE)
	@Test
	public void testFindByDTOVideApresFiltrage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(null, null));

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(introuvable dans liste) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOINTROUVABLE_DANS_LISTE)
	@Test
	public void testFindByDTOIntrouvableDansListe() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie));

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = mock(Produit.class);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(produitKo.getProduit()).thenReturn(MARTEAU);
		when(produitKo.getSousTypeProduit()).thenThrow(panneTechnique);
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « KO_TECHNIQUE_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = mock(Produit.class);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(produitKo.getProduit()).thenReturn(MARTEAU);
		when(produitKo.getSousTypeProduit()).thenThrow(panneTechnique);
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByDTO(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « OutputDTO exact + MESSAGE_SUCCES_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTONOMINAL)
	@Test
	public void testFindByDTONominal() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findByDTO(dto);
		assertProduitDTO(retour, 1L, BAZAR, OUTILLAGE, MARTEAU);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	} // __________________________________________________________________


	// ============================ findById =================================


	/**
	 * <div>
	 * <p>garantit que findById(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_PARAM_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findById(null);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(introuvable) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_OBJ_INTROUVABLE + id » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(999L)).thenReturn(null);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findById(999L);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + 999L);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(gateway KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_AVEC_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(100L)).thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(gateway KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_SANS_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(100L)).thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(100L)).thenReturn(produitKo);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(100L)).thenReturn(produitKo);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findById(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « OutputDTO exact + MESSAGE_SUCCES_RECHERCHE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final Produit produit = produit(MARTEAU, parentPersistant(), 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findById(100L)).thenReturn(produit);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.findById(100L);
		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________


	// ============================ update =================================


	/**
	 * <div>
	 * <p>garantit que update(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreNull + MESSAGE_PARAM_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(libellé null) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreBlank + MESSAGE_PARAM_BLANK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, null);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreBlank + MESSAGE_PARAM_BLANK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(parent blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(parent absent) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(parent non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(recherche enfants KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(recherche enfants KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(stockage null pendant ré-identification) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_OBJ_INTROUVABLE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testUpdateStockageNullPendantReidentification() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent)).thenReturn(null);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.update(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(introuvable) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_OBJ_INTROUVABLE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(SCIE, parent, 2L)));

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.update(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);
		verify(gateway, never()).update(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionNonPersistant » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit sansId = produit(MARTEAU, parent, null);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(sansId));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		verify(gateway, never()).update(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(modification technique KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(gateway.update(any(Produit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(modification technique KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(gateway.update(any(Produit.class)))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(modification retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_MODIF_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(Produit.class))).thenReturn(null);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.update(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(modification retourne non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionNonPersistant » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(Produit.class)))
				.thenReturn(produit(MARTEAU, parent, null));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit modifie = mock(Produit.class);
		when(modifie.getIdProduit()).thenReturn(100L);
		when(modifie.getSousTypeProduit()).thenThrow(panneTechnique);
		when(gateway.update(any(Produit.class)))
				.thenReturn(modifie);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit modifie = mock(Produit.class);
		when(modifie.getIdProduit()).thenReturn(100L);
		when(modifie.getSousTypeProduit()).thenThrow(panneTechnique);
		when(gateway.update(any(Produit.class)))
				.thenReturn(modifie);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que update(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « OutputDTO cohérent + ID conservé » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final Produit modifie = produit(MARTEAU, parent, 100L);
		final ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
		when(gateway.update(any(Produit.class))).thenReturn(modifie);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.update(dto);
		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + MARTEAU);
		verify(gateway, times(1)).update(captor.capture());
		assertThat(captor.getValue().getIdProduit()).isEqualTo(100L);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________


	// ============================ delete =================================


	/**
	 * <div>
	 * <p>garantit que delete(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreNull + MESSAGE_PARAM_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(libellé null) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreBlank + MESSAGE_PARAM_BLANK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, null);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionParametreBlank + MESSAGE_PARAM_BLANK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(parent blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(parent absent) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(parent non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_PARENT_NON_PERSISTANT_KO » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(recherche enfants KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(recherche enfants KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(stockage null pendant ré-identification) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_STOCKAGE_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testDeleteStockageNullPendantReidentification() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent)).thenReturn(null);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(introuvable) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_OBJ_INTROUVABLE + aucune suppression » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(SCIE, parent, 2L)));

		/* ACT :
		 * exécute l'appel testé.
		 */
		service.delete(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(non persistant) :</p>
	 * <ul>
	 * <li>exécute le scénario « ExceptionNonPersistant » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(MARTEAU, parent, null)));

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(destruction KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KOAVEC_MESSAGE)
	@Test
	public void testDeleteDestructionKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit cible = produit(MARTEAU, parent, 100L);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(cible));
		doThrow(panneTechnique).when(gateway).delete(cible);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(destruction KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KOSANS_MESSAGE)
	@Test
	public void testDeleteDestructionKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit cible = produit(MARTEAU, parent, 100L);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(cible));
		doThrow(panneTechnique).when(gateway).delete(cible);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que delete(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « suppression sur le couple parent/libellé » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final SousTypeProduit parentA = parentPersistant(BAZAR, OUTILLAGE, 1L, 10L);
		final SousTypeProduit parentB = parentPersistant(QUINCAILLERIE, OUTILLAGE, 2L, 20L);
		final Produit homonymeAutreParent = produit(MARTEAU, parentA, 100L);
		final Produit cible = produit(MARTEAU, parentB, 200L);
		final InputDTO dto = input(QUINCAILLERIE, OUTILLAGE, MARTEAU);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentA, parentB));
		when(gateway.findAllByParent(parentB))
				.thenReturn(Arrays.asList(cible));

		/* ACT :
		 * exécute l'appel testé.
		 */
		service.delete(dto);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_OK + MARTEAU);
		verify(gateway, never()).findAllByParent(parentA);
		verify(gateway, never()).delete(homonymeAutreParent);
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________


	// ============================ count =================================


	/**
	 * <div>
	 * <p>garantit que count(gateway KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testCountGatewayKOAvecMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que count(gateway KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « exception propagée » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testCountGatewayKOSansMessage() throws Exception {

		/* ARRANGE :
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenThrow(panneTechnique);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que count(retour négatif) :</p>
	 * <ul>
	 * <li>exécute le scénario « IllegalStateException » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(-1L);

		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ "comptage négatif incohérent : -1");

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que count(0) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(0L);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final long retour = service.count();

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que count(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_OK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(42L);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final long retour = service.count();

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isEqualTo(42L);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	
	// ============================ getMessage =================================


	/**
	 * <div>
	 * <p>garantit que getMessage(initial) :</p>
	 * <ul>
	 * <li>exécute le scénario « null » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */

		/* ACT - ASSERT :
		 * vérifie l'état initial du message sans solliciter les Gateways.
		 */
		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que getMessage(après erreur locale) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_CREER_NULL » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final OutputDTO retour = service.creer(null);

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que getMessage(après count zéro) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_VIDE » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(0L);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final long retour = service.count();

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que getMessage(après count nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_OK » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(1L);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final long retour = service.count();

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isEqualTo(1L);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que getMessage(dernier message gagne) :</p>
	 * <ul>
	 * <li>exécute le scénario « dernier message observable » ;</li>
	 * <li>contrôle le retour, l'exception ou l'état observable attendu par le PORT
	 * UC ;</li>
	 * <li>contrôle le message utilisateur exposé par {@link
	 * ProduitCuService#getMessage()} lorsque le scénario en produit un ;</li>
	 * <li>vérifie les interactions attendues ou interdites avec le Gateway Produit
	 * et le Gateway parent SousTypeProduit.</li>
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
		 * Mocke les services Gateway et les passe
		 * à un service UC instancié dans le test.
		 */
		final ProduitGatewayIService gateway 
			= mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeProduitGateway 
			= mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service 
			= new ProduitCuService(gateway, sousTypeProduitGateway);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.count()).thenReturn(1L);

		/* ACT :
		 * exécute l'appel testé.
		 */
		final long retourCount = service.count();

		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retourCount).isEqualTo(1L);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		final OutputDTO retourCreer = service.creer(null);
		assertThat(retourCreer).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL_KO);

	} // __________________________________________________________________


	
	// ***************************** HELPERS *******************************/



	/**
	 * <div>
	 * <p>DTO Produit d'entrée.</p>
	 * </div>
	 *
	 * @param typeProduit type parent
	 * @param sousTypeProduit parent métier
	 * @param produit produit
	 * @return InputDTO
	 */
	private static InputDTO input(
			final String typeProduit,
			final String sousTypeProduit,
			final String produit) {

		return new ProduitDTO.InputDTO(
				typeProduit,
				sousTypeProduit,
				produit);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>DTO SousTypeProduit parent d'entrée.</p>
	 * </div>
	 *
	 * @param typeProduit type parent
	 * @param sousTypeProduit objet métier parent
	 * @return SousTypeProduitDTO.InputDTO
	 */
	private static SousTypeProduitDTO.InputDTO parentDto(
			final String typeProduit,
			final String sousTypeProduit) {

		return new SousTypeProduitDTO.InputDTO(
				typeProduit,
				sousTypeProduit);
		
	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>Parent persistant nominal.</p>
	 * </div>
	 *
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit parentPersistant() {

		return parentPersistant(BAZAR, OUTILLAGE, 1L, 10L);
		
	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>Parent persistant paramétré.</p>
	 * </div>
	 *
	 * @param typeLibelle libellé TypeProduit
	 * @param sousTypeLibelle libellé SousTypeProduit
	 * @param typeId id TypeProduit
	 * @param sousTypeId id SousTypeProduit
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit parentPersistant(
			final String typeLibelle,
			final String sousTypeLibelle,
			final Long typeId,
			final Long sousTypeId) {

		final TypeProduit typeProduit = new TypeProduit(typeLibelle);
		typeProduit.setIdTypeProduit(typeId);

		final SousTypeProduit parent =
				new SousTypeProduit(sousTypeLibelle, typeProduit);
		parent.setIdSousTypeProduit(sousTypeId);

		return parent;
		
	} // __________________________________________________________________


	/**
	 * <div>
	 * <p>Parent non persistant.</p>
	 * </div>
	 *
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit parentNonPersistant() {

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		return new SousTypeProduit(OUTILLAGE, typeProduit);
		
	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>Produit métier paramétré.</p>
	 * </div>
	 *
	 * @param libelle libellé Produit
	 * @param parent parent SousTypeProduit
	 * @param id id Produit
	 * @return Produit
	 */
	private static Produit produit(
			final String libelle,
			final SousTypeProduit parent,
			final Long id) {

		final Produit produit = new Produit(libelle, parent);
		produit.setIdProduit(id);

		return produit;
		
	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>Produit mocké qui échoue pendant la conversion OutputDTO.</p>
	 * </div>
	 *
	 * @param panneTechnique panne à jeter
	 * @return Produit
	 */
	private static Produit produitConversionKo(
			final RuntimeException panneTechnique) {

		final Produit produit = mock(Produit.class);
		when(produit.getSousTypeProduit()).thenThrow(panneTechnique);

		return produit;
		
	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>Vérifie le contenu principal d'un OutputDTO Produit.</p>
	 * </div>
	 *
	 * @param dto DTO contrôlé
	 * @param id id attendu
	 * @param typeProduit type attendu
	 * @param sousTypeProduit parent métier attendu
	 * @param produit produit attendu
	 */
	private static void assertProduitDTO(
			final OutputDTO dto,
			final Long id,
			final String typeProduit,
			final String sousTypeProduit,
			final String produit) {

		assertThat(dto).isNotNull();
		assertThat(dto.getIdProduit()).isEqualTo(id);
		assertThat(dto.getTypeProduit()).isEqualTo(typeProduit);
		assertThat(dto.getSousTypeProduit()).isEqualTo(sousTypeProduit);
		assertThat(dto.getProduit()).isEqualTo(produit);
		
	} // __________________________________________________________________


	
} // FIN DE LA CLASSE ProduitCuServiceMockTest.----------------------------
