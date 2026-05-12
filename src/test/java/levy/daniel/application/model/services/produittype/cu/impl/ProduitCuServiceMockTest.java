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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * <p>Tests unitaires JUnit 5 / Mockito du SERVICE METIER UC
 * {@link ProduitCuService} pour l'objet métier {@link Produit}.</p>
 * <p>Le SERVICE METIER UC est le point d'entrée dans la logique métier
 * dialoguant directement avec le controller appelant.</p>
 * <p>Ces tests vérifient le respect du PORT {@link ProduitICuService},
 * les validations locales des DTO, les messages utilisateur exposés par
 * {@link ProduitCuService#getMessage()}, les conversions
 * {@link ProduitDTO.InputDTO} -> objet métier -> {@link ProduitDTO.OutputDTO},
 * les délégations attendues vers {@link ProduitGatewayIService} et
 * {@link SousTypeProduitGatewayIService}, ainsi que l'absence de délégation
 * Gateway lorsque le SERVICE METIER UC bloque localement l'opération.</p>
 * <p>{@link Produit} est un objet métier enfant : son parent est
 * {@link SousTypeProduit}, lui-même rattaché à un {@link TypeProduit}.</p>
 * <p>La classe reprend le formalisme stabilisé dans
 * {@code TypeProduitCuServiceMockTest} et
 * {@code SousTypeProduitCuServiceMockTest}, sans réinvention inutile.</p>
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

	/** "creer(null) : MESSAGE_CREER_NULL + aucune interaction Gateway". */
	public static final String DISPLAY_NAME_CREER_NULL
			= "creer(null) : MESSAGE_CREER_NULL + aucune interaction Gateway";

	/** "creer(blank) : ExceptionParametreBlank + MESSAGE_CREER_NOM_BLANK". */
	public static final String DISPLAY_NAME_CREER_BLANK
			= "creer(blank) : ExceptionParametreBlank + MESSAGE_CREER_NOM_BLANK";

	/** "creer(parent blank) : MESSAGE_PAS_PARENT + aucune interaction Gateway". */
	public static final String DISPLAY_NAME_CREER_PARENT_BLANK
			= "creer(parent blank) : MESSAGE_PAS_PARENT + aucune interaction Gateway";

	/** "creer(contrôle technique KO avec message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_AVEC_MESSAGE
			= "creer(contrôle technique KO avec message) : exception propagée + message rationalisé";

	/** "creer(contrôle technique KO sans message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_SANS_MESSAGE
			= "creer(contrôle technique KO sans message) : exception propagée + message rationalisé";

	/** "creer(doublon) : ExceptionDoublon + aucune création Gateway". */
	public static final String DISPLAY_NAME_CREER_DOUBLON
			= "creer(doublon) : ExceptionDoublon + aucune création Gateway";

	/** "creer(parent technique KO avec message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_AVEC_MESSAGE
			= "creer(parent technique KO avec message) : exception propagée + message rationalisé";

	/** "creer(parent technique KO sans message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_SANS_MESSAGE
			= "creer(parent technique KO sans message) : exception propagée + message rationalisé";

	/** "creer(parent absent) : MESSAGE_PAS_PARENT + aucune création Gateway". */
	public static final String DISPLAY_NAME_CREER_PARENT_ABSENT
			= "creer(parent absent) : MESSAGE_PAS_PARENT + aucune création Gateway";

	/** "creer(parent non persistant) : MESSAGE_PAS_PARENT + aucune création Gateway". */
	public static final String DISPLAY_NAME_CREER_PARENT_NON_PERSISTANT
			= "creer(parent non persistant) : MESSAGE_PAS_PARENT + aucune création Gateway";

	/** "creer(création technique KO avec message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_AVEC_MESSAGE
			= "creer(création technique KO avec message) : exception propagée + message rationalisé";

	/** "creer(création technique KO sans message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_SANS_MESSAGE
			= "creer(création technique KO sans message) : exception propagée + message rationalisé";

	/** "creer(gateway retourne null) : MESSAGE_CREATION_TECHNIQUE_KO_CREER". */
	public static final String DISPLAY_NAME_CREER_GATEWAY_RETOURNE_NULL
			= "creer(gateway retourne null) : MESSAGE_CREATION_TECHNIQUE_KO_CREER";

	/** "creer(conversion OutputDTO KO avec message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_AVEC_MESSAGE
			= "creer(conversion OutputDTO KO avec message) : exception propagée + message rationalisé";

	/** "creer(conversion OutputDTO KO sans message) : exception propagée + message rationalisé". */
	public static final String DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_SANS_MESSAGE
			= "creer(conversion OutputDTO KO sans message) : exception propagée + message rationalisé";

	/** "creer(nominal) : OutputDTO cohérent + MESSAGE_CREER_OK". */
	public static final String DISPLAY_NAME_CREER_NOMINAL
			= "creer(nominal) : OutputDTO cohérent + MESSAGE_CREER_OK";

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

	/** "findAllByParent(null) : RECHERCHE_SOUSTYPEPRODUIT_NULL". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL
			= "findAllByParent(null) : RECHERCHE_SOUSTYPEPRODUIT_NULL";

	/** "findAllByParent(parent blank) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK
			= "findAllByParent(parent blank) : MESSAGE_PAS_PARENT";

	/** "findAllByParent(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOAVEC_MESSAGE
			= "findAllByParent(recherche parent KO avec message) : exception propagée";

	/** "findAllByParent(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOSANS_MESSAGE
			= "findAllByParent(recherche parent KO sans message) : exception propagée";

	/** "findAllByParent(parent absent) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT
			= "findAllByParent(parent absent) : MESSAGE_PAS_PARENT";

	/** "findAllByParent(parent non persistant) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT
			= "findAllByParent(parent non persistant) : MESSAGE_PAS_PARENT";

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

	/** "findByDTO(parent blank) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_FIND_BY_DTOPARENT_BLANK
			= "findByDTO(parent blank) : MESSAGE_PAS_PARENT";

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

	/** "update(parent blank) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_BLANK
			= "update(parent blank) : MESSAGE_PAS_PARENT";

	/** "update(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE
			= "update(recherche parent KO avec message) : exception propagée";

	/** "update(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE
			= "update(recherche parent KO sans message) : exception propagée";

	/** "update(parent absent) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_ABSENT
			= "update(parent absent) : MESSAGE_PAS_PARENT";

	/** "update(parent non persistant) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_UPDATE_PARENT_NON_PERSISTANT
			= "update(parent non persistant) : MESSAGE_PAS_PARENT";

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

	/** "delete(parent blank) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_DELETE_PARENT_BLANK
			= "delete(parent blank) : MESSAGE_PAS_PARENT";

	/** "delete(recherche parent KO avec message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE
			= "delete(recherche parent KO avec message) : exception propagée";

	/** "delete(recherche parent KO sans message) : exception propagée". */
	public static final String DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE
			= "delete(recherche parent KO sans message) : exception propagée";

	/** "delete(parent absent) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_DELETE_PARENT_ABSENT
			= "delete(parent absent) : MESSAGE_PAS_PARENT";

	/** "delete(parent non persistant) : MESSAGE_PAS_PARENT". */
	public static final String DISPLAY_NAME_DELETE_PARENT_NON_PERSISTANT
			= "delete(parent non persistant) : MESSAGE_PAS_PARENT";

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
	 * <p>creer(null) : MESSAGE_CREER_NULL + aucune interaction Gateway.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_NULL)
	@Test
	public void testCreerNull() throws Exception {

		/* ARRANGE :
		 * prépare le SERVICE UC avec ses Gateways mockés.
		 */
		final Scenario scenario = scenario();

		/* ACT */
		final OutputDTO retour = scenario.service.creer(null);

		/* ASSERT */
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(blank) : ExceptionParametreBlank + MESSAGE_CREER_NOM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_BLANK)
	@Test
	public void testCreerBlank() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(ProduitICuService.MESSAGE_CREER_NOM_BLANK);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(parent blank) : MESSAGE_PAS_PARENT + aucune interaction Gateway.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_BLANK)
	@Test
	public void testCreerParentBlank() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(contrôle technique KO avec message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
								+ MESSAGE_GATEWAY);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(contrôle technique KO sans message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
								+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(doublon) : ExceptionDoublon + aucune création Gateway.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_DOUBLON)
	@Test
	public void testCreerDoublon() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(existant));

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(ProduitICuService.MESSAGE_DOUBLON + MARTEAU);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DOUBLON + MARTEAU);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(parent technique KO avec message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
								+ MESSAGE_GATEWAY);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(scenario.gateway, never()).creer(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(parent technique KO sans message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
								+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(scenario.gateway, never()).creer(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(parent absent) : MESSAGE_PAS_PARENT + aucune création Gateway.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_ABSENT)
	@Test
	public void testCreerParentAbsent() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(scenario.gateway, times(1)).findByLibelle(MARTEAU);
		verify(scenario.sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(scenario.gateway, never()).creer(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(parent non persistant) : MESSAGE_PAS_PARENT + aucune création Gateway.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_NON_PERSISTANT)
	@Test
	public void testCreerParentNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parentNonPersistant = parentNonPersistant();

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant));

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(scenario.gateway, never()).creer(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(création technique KO avec message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
								+ MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(création technique KO sans message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
								+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(gateway retourne null) : MESSAGE_CREATION_TECHNIQUE_KO_CREER.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_RETOURNE_NULL)
	@Test
	public void testCreerGatewayRetourneNull() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenReturn(null);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(conversion OutputDTO KO avec message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit cree = produitConversionKo(panneTechnique);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenReturn(cree);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
								+ MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(conversion OutputDTO KO sans message) : exception propagée + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit cree = produitConversionKo(panneTechnique);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenReturn(cree);

		assertThatThrownBy(() -> scenario.service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
								+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>creer(nominal) : OutputDTO cohérent + MESSAGE_CREER_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_NOMINAL)
	@Test
	public void testCreerNominal() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit cree = produit(MARTEAU, parent, 100L);
		final ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);

		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.creer(any(Produit.class)))
				.thenReturn(cree);

		final OutputDTO retour = scenario.service.creer(dto);

		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

		verify(scenario.gateway, times(1)).creer(captor.capture());
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________


	// ============================ rechercherTous =================================


	/**
	 * <div>
	 * <p>rechercherTous(gateway retourne null) : MESSAGE_STOCKAGE_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.rechercherTous()).thenReturn(null);

		assertThatThrownBy(() -> scenario.service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(scenario.gateway, times(1)).rechercherTous();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(gateway KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.rechercherTous()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verify(scenario.gateway, times(1)).rechercherTous();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(gateway KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.rechercherTous()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verify(scenario.gateway, times(1)).rechercherTous();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, null));

		final List<OutputDTO> retour = scenario.service.rechercherTous();

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTous(nominal) : filtre, trie, dédoublonne + MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL)
	@Test
	public void testRechercherTousNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);

		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau, produitScie));

		final List<OutputDTO> retour = scenario.service.rechercherTous();

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________


	// ============================ rechercherTousString =================================


	/**
	 * <div>
	 * <p>rechercherTousString(gateway retourne null) : propage rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousStringGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.rechercherTous()).thenReturn(null);

		assertThatThrownBy(() -> scenario.service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(gateway KO avec message) : propage rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.rechercherTous()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(gateway KO sans message) : propage rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.rechercherTous()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(conversion KO avec message) : propage rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(conversion KO sans message) : propage rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(vide après filtrage) : liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousStringVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, null));

		final List<String> retour = scenario.service.rechercherTousString();

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(libellés blank) : liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK)
	@Test
	public void testRechercherTousStringVideApresLibellesBlank() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final Produit produitBlank = produit(ESPACES, parent, 1L);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitBlank));

		final List<String> retour = scenario.service.rechercherTousString();

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousString(nominal) : libellés triés + MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL)
	@Test
	public void testRechercherTousStringNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		when(scenario.gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitScie, produitMarteau));

		final List<String> retour = scenario.service.rechercherTousString();

		assertThat(retour).containsExactly(MARTEAU, SCIE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ rechercherTousParPage =================================


	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : MESSAGE_PAGEABLE_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NULL)
	@Test
	public void testRechercherTousParPageNull() throws Exception {

		final Scenario scenario = scenario();

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAGEABLE_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(gateway KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(gateway KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(gateway retourne null) : MESSAGE_RECHERCHE_PAGINEE_KO.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousParPageGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenReturn(null);

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(requete))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitKo), 0, 4, 1L);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenReturn(page);

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitKo), 0, 4, 1L);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenReturn(page);

		assertThatThrownBy(() -> scenario.service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(vide après filtrage) : page vide + MESSAGE_RECHERCHE_PAGINEE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousParPageVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(null, null), 0, 4, 2L);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenReturn(page);

		final ResultatPage<OutputDTO> retour = scenario.service.rechercherTousParPage(requete);

		assertThat(retour).isNotNull();
		assertThat(retour.getContent()).isNotNull().isEmpty();
		assertThat(retour.getTotalElements()).isEqualTo(2L);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>rechercherTousParPage(nominal) : page cohérente + MESSAGE_RECHERCHE_PAGINEE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
	@Test
	public void testRechercherTousParPageNominal() throws Exception {

		final Scenario scenario = scenario();
		final RequetePage requete = new RequetePage(0, 4);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitScie, null, produitMarteau, produitScie),
				0, 4, 10L);
		when(scenario.gateway.rechercherTousParPage(requete))
				.thenReturn(page);

		final ResultatPage<OutputDTO> retour = scenario.service.rechercherTousParPage(requete);

		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(10L);
		assertThat(retour.getContent()).hasSize(2);
		assertThat(retour.getContent()).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________


	// ============================ findByLibelle =================================


	/**
	 * <div>
	 * <p>findByLibelle(null) : null + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NULL)
	@Test
	public void testFindByLibelleNull() throws Exception {

		final Scenario scenario = scenario();

		final List<OutputDTO> retour = scenario.service.findByLibelle(null);

		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(blank) : null + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_BLANK)
	@Test
	public void testFindByLibelleBlank() throws Exception {

		final Scenario scenario = scenario();

		final List<OutputDTO> retour = scenario.service.findByLibelle(ESPACES);

		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(gateway retourne null) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByLibelleGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.findByLibelle(MARTEAU)).thenReturn(null);

		assertThatThrownBy(() -> scenario.service.findByLibelle(MARTEAU))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(gateway KO avec message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(gateway KO sans message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(conversion OutputDTO KO avec message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(conversion OutputDTO KO sans message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_INTROUVABLE)
	@Test
	public void testFindByLibelleIntrouvable() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());

		final List<OutputDTO> retour = scenario.service.findByLibelle(MARTEAU);

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelle(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL)
	@Test
	public void testFindByLibelleNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parentA = parentPersistant();
		final SousTypeProduit parentB = parentPersistant(QUINCAILLERIE, ATELIER, 2L, 20L);
		final Produit produitA = produit(MARTEAU, parentA, 100L);
		final Produit produitB = produit(MARTEAU, parentB, 200L);
		when(scenario.gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitA, null, produitB));

		final List<OutputDTO> retour = scenario.service.findByLibelle(MARTEAU);

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(OUTILLAGE, ATELIER);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findByLibelleRapide =================================


	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : MESSAGE_PARAM_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NULL)
	@Test
	public void testFindByLibelleRapideNull() throws Exception {

		final Scenario scenario = scenario();
		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PARAM_NULL);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue à rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_BLANK)
	@Test
	public void testFindByLibelleRapideBlank() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final Produit produit = produit(MARTEAU, parent, 1L);
		when(scenario.gateway.rechercherTous()).thenReturn(Arrays.asList(produit));

		final List<OutputDTO> retour = scenario.service.findByLibelleRapide(ESPACES);

		assertThat(retour).isNotNull().hasSize(1);
		assertThat(retour.get(0).getProduit()).isEqualTo(MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(gateway KO avec message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(gateway KO sans message) : exception propagée par l'ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);

		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(gateway retourne null) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByLibelleRapideGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE)).thenReturn(null);

		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(CONTENU_RAPIDE))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByLibelleRapideConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findByLibelleRapide(CONTENU_RAPIDE))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(vide après filtrage) : liste vide.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindByLibelleRapideVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(null, null));

		final List<OutputDTO> retour = scenario.service.findByLibelleRapide(CONTENU_RAPIDE);

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByLibelleRapide(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_LIBELLE_RAPIDE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_RAPIDE_NOMINAL)
	@Test
	public void testFindByLibelleRapideNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		when(scenario.gateway.findByLibelleRapide(CONTENU_RAPIDE))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		final List<OutputDTO> retour = scenario.service.findByLibelleRapide(CONTENU_RAPIDE);

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findAllByParent =================================


	/**
	 * <div>
	 * <p>findAllByParent(null) : RECHERCHE_SOUSTYPEPRODUIT_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_NULL)
	@Test
	public void testFindAllByParentNull() throws Exception {

		final Scenario scenario = scenario();
		assertThatThrownBy(() -> scenario.service.findAllByParent(null))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_BLANK)
	@Test
	public void testFindAllByParentParentBlank() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, ESPACES);
		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(recherche parent KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(recherche parent KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindAllByParentParentGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_ABSENT)
	@Test
	public void testFindAllByParentParentAbsent() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(parent non persistant) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_PARENT_NON_PERSISTANT)
	@Test
	public void testFindAllByParentParentNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(recherche enfants KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(recherche enfants KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_ENFANTS_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindAllByParentEnfantsGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(gateway retourne null) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindAllByParentGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent)).thenReturn(null);

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindAllByParentConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));

		assertThatThrownBy(() -> scenario.service.findAllByParent(parentDto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(vide après filtrage) : liste vide selon ADAPTER réel.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_VIDE_APRES_FILTRAGE)
	@Test
	public void testFindAllByParentVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(null, null));

		final List<OutputDTO> retour = scenario.service.findAllByParent(parentDto);

		assertThat(retour).isNotNull().isEmpty();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findAllByParent(nominal) : liste cohérente + MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_ALL_BY_PARENT)
	@DisplayName(DISPLAY_NAME_FIND_ALL_BY_PARENT_NOMINAL)
	@Test
	public void testFindAllByParentNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parent = parentPersistant();
		final SousTypeProduitDTO.InputDTO parentDto = parentDto(BAZAR, OUTILLAGE);
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		final List<OutputDTO> retour = scenario.service.findAllByParent(parentDto);

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findByDTO =================================


	/**
	 * <div>
	 * <p>findByDTO(null) : MESSAGE_RECHERCHE_OBJ_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTONULL)
	@Test
	public void testFindByDTONull() throws Exception {

		final Scenario scenario = scenario();
		final OutputDTO retour = scenario.service.findByDTO(null);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(parent blank) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_BLANK)
	@Test
	public void testFindByDTOParentBlank() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(recherche parent KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(recherche parent KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_PARENT_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheParentSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(parent absent) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_ABSENT)
	@Test
	public void testFindByDTOParentAbsent() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(parent non persistant) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOPARENT_NON_PERSISTANT)
	@Test
	public void testFindByDTOParentNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(recherche enfants KO avec message) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_AVEC_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(recherche enfants KO sans message) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOERREUR_TECHNIQUE_RECHERCHE_ENFANTS_SANS_MESSAGE)
	@Test
	public void testFindByDTOErreurTechniqueRechercheEnfantsSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(gateway retourne null) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOGATEWAY_RETOUR_NULL)
	@Test
	public void testFindByDTOGatewayRetourNull() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent)).thenReturn(null);
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(vide) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOVIDE)
	@Test
	public void testFindByDTOVide() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Collections.emptyList());
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(vide après filtrage) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOVIDE_APRES_FILTRAGE)
	@Test
	public void testFindByDTOVideApresFiltrage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(null, null));
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(introuvable dans liste) : null + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOINTROUVABLE_DANS_LISTE)
	@Test
	public void testFindByDTOIntrouvableDansListe() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie));
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(conversion OutputDTO KO avec message) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = mock(Produit.class);
		when(produitKo.getProduit()).thenReturn(MARTEAU);
		when(produitKo.getSousTypeProduit()).thenThrow(panneTechnique);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(conversion OutputDTO KO sans message) : KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTOCONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByDTOConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = mock(Produit.class);
		when(produitKo.getProduit()).thenReturn(MARTEAU);
		when(produitKo.getSousTypeProduit()).thenThrow(panneTechnique);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitKo));
		assertThatThrownBy(() -> scenario.service.findByDTO(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findByDTO(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_DTO)
	@DisplayName(DISPLAY_NAME_FIND_BY_DTONOMINAL)
	@Test
	public void testFindByDTONominal() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau));
		final OutputDTO retour = scenario.service.findByDTO(dto);
		assertProduitDTO(retour, 1L, BAZAR, OUTILLAGE, MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	} // __________________________________________________________________


	// ============================ findById =================================


	/**
	 * <div>
	 * <p>findById(null) : MESSAGE_PARAM_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NULL)
	@Test
	public void testFindByIdNull() throws Exception {

		final Scenario scenario = scenario();
		final OutputDTO retour = scenario.service.findById(null);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(introuvable) : MESSAGE_OBJ_INTROUVABLE + id.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_INTROUVABLE)
	@Test
	public void testFindByIdIntrouvable() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.findById(999L)).thenReturn(null);
		final OutputDTO retour = scenario.service.findById(999L);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + 999L);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(gateway KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_AVEC_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.findById(100L)).thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(gateway KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_ERREUR_TECHNIQUE_SANS_MESSAGE)
	@Test
	public void testFindByIdErreurTechniqueSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.findById(100L)).thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findById(100L)).thenReturn(produitKo);
		assertThatThrownBy(() -> scenario.service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByIdConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		when(scenario.gateway.findById(100L)).thenReturn(produitKo);
		assertThatThrownBy(() -> scenario.service.findById(100L))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage()).isNull();

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>findById(nominal) : OutputDTO exact + MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FIND_BY_ID)
	@DisplayName(DISPLAY_NAME_FIND_BY_ID_NOMINAL)
	@Test
	public void testFindByIdNominal() throws Exception {

		final Scenario scenario = scenario();
		final Produit produit = produit(MARTEAU, parentPersistant(), 100L);
		when(scenario.gateway.findById(100L)).thenReturn(produit);
		final OutputDTO retour = scenario.service.findById(100L);
		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________


	// ============================ update =================================


	/**
	 * <div>
	 * <p>update(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NULL)
	@Test
	public void testUpdateNull() throws Exception {

		final Scenario scenario = scenario();
		assertThatThrownBy(() -> scenario.service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_LIBELLE_NULL)
	@Test
	public void testUpdateLibelleNull() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, null);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_BLANK)
	@Test
	public void testUpdateBlank() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(parent blank) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_BLANK)
	@Test
	public void testUpdateParentBlank() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(recherche parent KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(recherche parent KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheParentTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(parent absent) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_ABSENT)
	@Test
	public void testUpdateParentAbsent() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(parent non persistant) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_PARENT_NON_PERSISTANT)
	@Test
	public void testUpdateParentNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(recherche enfants KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(recherche enfants KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(stockage null pendant ré-identification) : null + MESSAGE_OBJ_INTROUVABLE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testUpdateStockageNullPendantReidentification() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent)).thenReturn(null);
		final OutputDTO retour = scenario.service.update(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(introuvable) : null + MESSAGE_OBJ_INTROUVABLE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_INTROUVABLE)
	@Test
	public void testUpdateIntrouvable() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(SCIE, parent, 2L)));
		final OutputDTO retour = scenario.service.update(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);
		verify(scenario.gateway, never()).update(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(non persistant) : ExceptionNonPersistant.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NON_PERSISTANT)
	@Test
	public void testUpdateNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit sansId = produit(MARTEAU, parent, null);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(sansId));
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		verify(scenario.gateway, never()).update(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(modification technique KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.update(any(Produit.class)))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(modification technique KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testUpdateModificationTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.update(any(Produit.class)))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(modification retourne null) : null + MESSAGE_MODIF_KO.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NULL)
	@Test
	public void testUpdateModificationRetourNull() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		when(scenario.gateway.update(any(Produit.class))).thenReturn(null);
		final OutputDTO retour = scenario.service.update(dto);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(modification retourne non persistant) : ExceptionNonPersistant.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_MODIFICATION_RETOUR_NON_PERSISTANT)
	@Test
	public void testUpdateModificationRetourNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		when(scenario.gateway.update(any(Produit.class)))
				.thenReturn(produit(MARTEAU, parent, null));
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(conversion OutputDTO KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit modifie = mock(Produit.class);
		when(modifie.getIdProduit()).thenReturn(100L);
		when(modifie.getSousTypeProduit()).thenThrow(panneTechnique);
		when(scenario.gateway.update(any(Produit.class)))
				.thenReturn(modifie);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(conversion OutputDTO KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testUpdateConversionOutputDTOKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit modifie = mock(Produit.class);
		when(modifie.getIdProduit()).thenReturn(100L);
		when(modifie.getSousTypeProduit()).thenThrow(panneTechnique);
		when(scenario.gateway.update(any(Produit.class)))
				.thenReturn(modifie);
		assertThatThrownBy(() -> scenario.service.update(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>update(nominal) : OutputDTO cohérent + ID conservé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_UPDATE)
	@DisplayName(DISPLAY_NAME_UPDATE_NOMINAL)
	@Test
	public void testUpdateNominal() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(existant));
		final Produit modifie = produit(MARTEAU, parent, 100L);
		final ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
		when(scenario.gateway.update(any(Produit.class))).thenReturn(modifie);
		final OutputDTO retour = scenario.service.update(dto);
		assertProduitDTO(retour, 100L, BAZAR, OUTILLAGE, MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + MARTEAU);
		verify(scenario.gateway, times(1)).update(captor.capture());
		assertThat(captor.getValue().getIdProduit()).isEqualTo(100L);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________


	// ============================ delete =================================


	/**
	 * <div>
	 * <p>delete(null) : ExceptionParametreNull + MESSAGE_PARAM_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NULL)
	@Test
	public void testDeleteNull() {

		final Scenario scenario = scenario();
		assertThatThrownBy(() -> scenario.service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(libellé null) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_LIBELLE_NULL)
	@Test
	public void testDeleteLibelleNull() {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, null);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(blank) : ExceptionParametreBlank + MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_BLANK)
	@Test
	public void testDeleteBlank() {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(parent blank) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_BLANK)
	@Test
	public void testDeleteParentBlank() {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(recherche parent KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(recherche parent KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheParentTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(scenario.gateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(parent absent) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_ABSENT)
	@Test
	public void testDeleteParentAbsent() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(scenario.gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(parent non persistant) : MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_PARENT_NON_PERSISTANT)
	@Test
	public void testDeleteParentNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant()));
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);
		verify(scenario.gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(scenario.gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(recherche enfants KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(recherche enfants KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_RECHERCHE_ENFANTS_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testDeleteRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(stockage null pendant ré-identification) : MESSAGE_STOCKAGE_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_STOCKAGE_NULL_PENDANT_REIDENTIFICATION)
	@Test
	public void testDeleteStockageNullPendantReidentification() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent)).thenReturn(null);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(scenario.gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(introuvable) : MESSAGE_OBJ_INTROUVABLE + aucune suppression.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_INTROUVABLE)
	@Test
	public void testDeleteIntrouvable() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(SCIE, parent, 2L)));
		scenario.service.delete(dto);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);
		verify(scenario.gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(non persistant) : ExceptionNonPersistant.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NON_PERSISTANT)
	@Test
	public void testDeleteNonPersistant() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(produit(MARTEAU, parent, null)));
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class)
				.hasMessage(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);
		verify(scenario.gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(destruction KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KOAVEC_MESSAGE)
	@Test
	public void testDeleteDestructionKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit cible = produit(MARTEAU, parent, 100L);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(cible));
		doThrow(panneTechnique).when(scenario.gateway).delete(cible);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verify(scenario.gateway, times(1)).delete(cible);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(destruction KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_DESTRUCTION_KOSANS_MESSAGE)
	@Test
	public void testDeleteDestructionKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		final Produit cible = produit(MARTEAU, parent, 100L);
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(scenario.gateway.findAllByParent(parent))
				.thenReturn(Arrays.asList(cible));
		doThrow(panneTechnique).when(scenario.gateway).delete(cible);
		assertThatThrownBy(() -> scenario.service.delete(dto))
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_KO + MARTEAU
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verify(scenario.gateway, times(1)).delete(cible);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>delete(nominal) : suppression sur le couple parent/libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_DELETE)
	@DisplayName(DISPLAY_NAME_DELETE_NOMINAL)
	@Test
	public void testDeleteNominal() throws Exception {

		final Scenario scenario = scenario();
		final SousTypeProduit parentA = parentPersistant(BAZAR, OUTILLAGE, 1L, 10L);
		final SousTypeProduit parentB = parentPersistant(QUINCAILLERIE, OUTILLAGE, 2L, 20L);
		final Produit homonymeAutreParent = produit(MARTEAU, parentA, 100L);
		final Produit cible = produit(MARTEAU, parentB, 200L);
		final InputDTO dto = input(QUINCAILLERIE, OUTILLAGE, MARTEAU);
		when(scenario.sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentA, parentB));
		when(scenario.gateway.findAllByParent(parentB))
				.thenReturn(Arrays.asList(cible));
		scenario.service.delete(dto);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_OK + MARTEAU);
		verify(scenario.gateway, never()).findAllByParent(parentA);
		verify(scenario.gateway, never()).delete(homonymeAutreParent);
		verify(scenario.gateway, times(1)).delete(cible);

	} // __________________________________________________________________


	// ============================ count =================================


	/**
	 * <div>
	 * <p>count(gateway KO avec message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testCountGatewayKOAvecMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		when(scenario.gateway.count()).thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.count())
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>count(gateway KO sans message) : exception propagée.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testCountGatewayKOSansMessage() throws Exception {

		final Scenario scenario = scenario();
		final IllegalStateException panneTechnique = new IllegalStateException();
		when(scenario.gateway.count()).thenThrow(panneTechnique);
		assertThatThrownBy(() -> scenario.service.count())
				.isSameAs(panneTechnique);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>count(retour négatif) : IllegalStateException.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_RETOUR_NEGATIF)
	@Test
	public void testCountRetourNegatif() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(-1L);
		assertThatThrownBy(() -> scenario.service.count())
				.isInstanceOf(IllegalStateException.class);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE
						+ "comptage négatif incohérent : -1");

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>count(0) : MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_ZERO)
	@Test
	public void testCountZero() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(0L);
		final long retour = scenario.service.count();
		assertThat(retour).isZero();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>count(nominal) : MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_COUNT)
	@DisplayName(DISPLAY_NAME_COUNT_NOMINAL)
	@Test
	public void testCountNominal() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(42L);
		final long retour = scenario.service.count();
		assertThat(retour).isEqualTo(42L);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ getMessage =================================


	/**
	 * <div>
	 * <p>getMessage(initial) : null.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_INITIAL_NULL)
	@Test
	public void testGetMessageInitialNull() {

		final Scenario scenario = scenario();
		assertThat(scenario.service.getMessage()).isNull();
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>getMessage(après erreur locale) : MESSAGE_CREER_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_ERREUR_LOCALE)
	@Test
	public void testGetMessageApresErreurLocale() throws Exception {

		final Scenario scenario = scenario();
		final OutputDTO retour = scenario.service.creer(null);
		assertThat(retour).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);
		verifyNoInteractions(scenario.gateway);
		verifyNoInteractions(scenario.sousTypeGateway);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>getMessage(après count zéro) : MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_ZERO)
	@Test
	public void testGetMessageApresCountZero() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(0L);
		final long retour = scenario.service.count();
		assertThat(retour).isZero();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>getMessage(après count nominal) : MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_APRES_COUNT_NOMINAL)
	@Test
	public void testGetMessageApresCountNominal() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(1L);
		final long retour = scenario.service.count();
		assertThat(retour).isEqualTo(1L);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________

	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) : dernier message observable.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_GET_MESSAGE)
	@DisplayName(DISPLAY_NAME_GET_MESSAGE_DERNIER_MESSAGE_GAGNE)
	@Test
	public void testGetMessageDernierMessageGagne() throws Exception {

		final Scenario scenario = scenario();
		when(scenario.gateway.count()).thenReturn(1L);
		final long retourCount = scenario.service.count();
		assertThat(retourCount).isEqualTo(1L);
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		final OutputDTO retourCreer = scenario.service.creer(null);
		assertThat(retourCreer).isNull();
		assertThat(scenario.service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

	} // __________________________________________________________________


	// ***************************** HELPERS *******************************/


	/**
	 * <div>
	 * <p>Prépare un scénario Mockito standard pour Produit.</p>
	 * </div>
	 *
	 * @return Scenario
	 */
	private static Scenario scenario() {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		return new Scenario(gateway, sousTypeGateway, service);
	}


	/**
	 * <div>
	 * <p>DTO Produit d'entrée.</p>
	 * </div>
	 *
	 * @param typeProduit type parent
	 * @param sousTypeProduit sous-type parent
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
	}


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
	}


	/**
	 * <div>
	 * <p>Parent persistant nominal.</p>
	 * </div>
	 *
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit parentPersistant() {

		return parentPersistant(BAZAR, OUTILLAGE, 1L, 10L);
	}


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
	}


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
	}


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
	}


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
	}


	/**
	 * <div>
	 * <p>Vérifie le contenu principal d'un OutputDTO Produit.</p>
	 * </div>
	 *
	 * @param dto DTO contrôlé
	 * @param id id attendu
	 * @param typeProduit type attendu
	 * @param sousTypeProduit sous-type attendu
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
	}


	/**
	 * <div>
	 * <p>Scénario Mockito standard.</p>
	 * </div>
	 */
	private static final class Scenario {

		/** Gateway Produit mocké. */
		private final ProduitGatewayIService gateway;

		/** Gateway parent SousTypeProduit mocké. */
		private final SousTypeProduitGatewayIService sousTypeGateway;

		/** SERVICE METIER UC testé. */
		private final ProduitCuService service;

		/**
		 * <div>
		 * <p>Constructeur complet.</p>
		 * </div>
		 *
		 * @param pGateway Gateway Produit
		 * @param pSousTypeGateway Gateway parent
		 * @param pService SERVICE METIER UC
		 */
		private Scenario(
				final ProduitGatewayIService pGateway,
				final SousTypeProduitGatewayIService pSousTypeGateway,
				final ProduitCuService pService) {

			super();
			this.gateway = pGateway;
			this.sousTypeGateway = pSousTypeGateway;
			this.service = pService;
		}
	}

}
