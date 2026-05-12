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
	 * <p>garantit que creer(null) :</p>
	 * <ul>
	 * <li>retourne {@code null} ;</li>
	 * <li>positionne le message utilisateur {@link
	 * ProduitICuService#MESSAGE_CREER_NULL} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier 
	 * ni avec le Gateway parent.</li>
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
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(libellé blank) :</p>
	 * <ul>
	 * <li>jette une {@link ExceptionParametreBlank} ;</li>
	 * <li>positionne le message utilisateur {@link
	 * ProduitICuService#MESSAGE_CREER_NOM_BLANK} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_BLANK)
	@Test
	public void testCreerBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO dont le libellé de l'objet métier est blank.
		 *
		 * Ce cas doit être bloqué par le SERVICE METIER UC
		 * avant toute délégation aux Gateways.
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, ESPACES);
		
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
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionParametreBlank ;
		 * - émet le message MESSAGE_CREER_NOM_BLANK
		 *   contractuel du PORT UC.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class)
				.hasMessage(ProduitICuService.MESSAGE_CREER_NOM_BLANK);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NOM_BLANK);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(libellé parent blank) :</p>
	 * <ul>
	 * <li>contrôle localement le libellé du parent ;</li>
	 * <li>jette une {@link IllegalStateException} ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT} ;</li>
	 * <li>n'interagit ni avec le Gateway objet métier
	 * ni avec le Gateway parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_BLANK)
	@Test
	public void testCreerParentBlank() throws Exception {

		/* ARRANGE :
		 * prépare un DTO dont le libellé parent est blank.
		 *
		 * Le SERVICE METIER UC doit refuser ce parent avant
		 * toute délégation aux Gateways.
		 */
		final InputDTO dto = input(BAZAR, ESPACES, MARTEAU);

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
		/* Garantit que service.creer(dto) :
		 * - jette une IllegalStateException ;
		 * - émet le message MESSAGE_PAS_PARENT.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit qu'aucun Gateway mocké n'a été appelé. */
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
	 * + message technique ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
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
		 * simule une panne technique de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

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
						ProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que la création et la recherche du parent
		 * ne sont jamais tentées après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(sousTypeProduitGateway);

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONTROLE_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerControleTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide pour atteindre réellement
		 * le contrôle d'unicité.
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
		 * simule une panne technique sans message de gateway.findByLibelle(...)
		 * pendant le contrôle de doublon réalisé par isDoublon(...).
		 */
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création et la recherche du parent
		 * ne sont jamais tentées après l'échec du contrôle de doublon.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(doublon fonctionnel) :</p>
	 * <ul>
	 * <li>contrôle l'unicité via {@code isDoublon(...)} ;</li>
	 * <li>{@code isDoublon(...)} interroge le Gateway objet métier
	 * via {@code gateway.findByLibelle(...)} ;</li>
	 * <li>retient seulement le doublon portant le même parent
	 * et le même libellé ;</li>
	 * <li>jette une {@link ExceptionDoublon} ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_DOUBLON} + libellé ;</li>
	 * <li>ne cherche jamais le parent ;</li>
	 * <li>ne délègue jamais la création au Gateway objet métier.</li>
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
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		
		final SousTypeProduit parent = parentPersistant();
		final Produit existant = produit(MARTEAU, parent, 100L);

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
		 * simule un doublon fonctionnel détecté par isDoublon(...)
		 * via l'appel gateway.findByLibelle(...).
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(existant));

		/* ACT - ASSERT */
		/* Garantit que service.creer(dto) :
		 * - jette une ExceptionDoublon ;
		 * - émet le message MESSAGE_DOUBLON + libellé.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class)
				.hasMessage(ProduitICuService.MESSAGE_DOUBLON
						+ MARTEAU);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DOUBLON
						+ MARTEAU);

		/* Garantit que le contrôle d'unicité a été exécuté,
		 * que la création n'a jamais été déléguée au Gateway objet métier,
		 * et que le parent n'a pas été recherché après le doublon.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(recherche parent KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>atteint la recherche du parent persistant ;</li>
	 * <li>propage l'exception technique levée par
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER}
	 * + message technique ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide sans doublon afin d'atteindre
		 * réellement la recherche du parent.
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
		 * - findByLibelle(...) sur le Gateway objet métier retourne une liste vide
		 *   pour simuler l'absence de doublon ;
		 * - findByLibelle(...) sur le Gateway parent lève une panne technique.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
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
						ProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec de recherche du parent.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(recherche parent KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>atteint la recherche du parent persistant ;</li>
	 * <li>propage l'exception technique sans message levée par
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>ne tente jamais {@code gateway.creer(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_PARENT_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerParentTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide sans doublon afin d'atteindre
		 * réellement la recherche du parent.
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
		 * - findByLibelle(...) sur le Gateway objet métier retourne une liste vide
		 *   pour simuler l'absence de doublon ;
		 * - findByLibelle(...) sur le Gateway parent lève une panne technique
		 *   sans message.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
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
						ProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que la création n'est jamais tentée
		 * après l'échec de recherche du parent.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT} ;</li>
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
		 * - le Gateway objet métier ne détecte aucun doublon ;
		 * - le Gateway parent ne retrouve aucun parent persistant.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		/* ACT - ASSERT */
		/* Garantit que l'absence de parent est refusée
		 * avec le message utilisateur MESSAGE_PAS_PARENT.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la création n'est jamais tentée
		 * lorsque le parent est absent.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT} ;</li>
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
		 * - le Gateway objet métier ne détecte aucun doublon ;
		 * - le Gateway parent retourne un objet sans identifiant.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentNonPersistant));

		/* ACT - ASSERT */
		/* Garantit qu'un parent non persistant est refusé
		 * avec le même message utilisateur qu'un parent absent.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		/* Garantit que la création n'est jamais tentée
		 * lorsque le parent n'est pas persistant.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		
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
		final ArgumentCaptor<Produit> captor 
				= ArgumentCaptor.forClass(Produit.class);

		/*
		 * Configuration du Mock :
		 * - aucun doublon objet métier n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) échoue avec un message technique.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
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
						ProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY);

		/* Garantit que le scénario a atteint la création Gateway
		 * avec un objet métier rattaché au parent persistant.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		assertThat(captor.getValue().getProduit()).isEqualTo(MARTEAU);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

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
	 * {@link ProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CREATION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerCreationTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		
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
		final ArgumentCaptor<Produit> captor 
				= ArgumentCaptor.forClass(Produit.class);

		/*
		 * Configuration du Mock :
		 * - aucun doublon objet métier n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) échoue sans message technique.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.creer(any(Produit.class)))
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
						ProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avec un objet métier rattaché au parent persistant.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		assertThat(captor.getValue().getProduit()).isEqualTo(MARTEAU);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(gateway.creer(...) retourne null) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché au parent ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>jette une {@link IllegalStateException} si le Gateway
	 * ne retourne aucun objet créé ;</li>
	 * <li>émet le message
	 * {@link ProduitICuService#MESSAGE_CREATION_TECHNIQUE_KO_CREER}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_GATEWAY_RETOURNE_NULL)
	@Test
	public void testCreerGatewayRetourneNull() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la délégation gateway.creer(...).
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		
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
		
		final ArgumentCaptor<Produit> captor 
				= ArgumentCaptor.forClass(Produit.class);

		/*
		 * Configuration du Mock :
		 * - aucun doublon objet métier n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) retourne null.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.creer(any(Produit.class)))
				.thenReturn(null);

		/* ACT - ASSERT */
		/* Garantit que le SERVICE METIER UC sécurise le succès apparent
		 * et refuse une réponse technique null.
		 */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		/* Garantit que le Gateway a bien été sollicité jusqu'à la création,
		 * avec un objet métier rattaché au parent persistant,
		 * puis que l'anomalie null est traitée côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		assertThat(captor.getValue().getProduit()).isEqualTo(MARTEAU);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO avec message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché au parent ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur rationalisé avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
	 * + message technique.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_AVEC_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoAvecMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la création Gateway puis
		 * la conversion finale en OutputDTO.
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		
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
		final ArgumentCaptor<Produit> captor 
				= ArgumentCaptor.forClass(Produit.class);

		/*
		 * Configuration du Mock :
		 * - aucun doublon objet métier n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) retourne un objet métier créé ;
		 * - la conversion en OutputDTO de cet objet déclenche
		 *   une panne technique avec message.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.creer(any(Produit.class)))
				.thenReturn(cree);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC expose
		 * un message utilisateur rationalisé pour l'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
						+ MESSAGE_GATEWAY_BIS);

		/* Garantit que le scénario a atteint la création Gateway
		 * avec un objet métier rattaché au parent persistant,
		 * puis la conversion finale côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());
		verify(cree, times(1)).getSousTypeProduit();

		assertThat(captor.getValue().getProduit()).isEqualTo(MARTEAU);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(conversion OutputDTO KO sans message) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon ;</li>
	 * <li>récupère le parent persistant ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché au parent ;</li>
	 * <li>atteint l'appel {@code gateway.creer(...)} ;</li>
	 * <li>atteint la conversion finale de l'objet métier créé en
	 * {@link OutputDTO} ;</li>
	 * <li>propage l'exception sans message levée pendant cette conversion ;</li>
	 * <li>positionne un message utilisateur sûr avec
	 * {@link ProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}
	 * + {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName(DISPLAY_NAME_CREER_CONVERSION_TECHNIQUE_KO_SANS_MESSAGE)
	@Test
	public void testCreerConversionTechniqueKoSansMessage() throws Exception {

		/* ARRANGE :
		 * prépare un DTO valide, non doublon, avec parent persistant,
		 * pour atteindre réellement la création Gateway puis
		 * la conversion finale en OutputDTO.
		 */
		final InputDTO dto = input(BAZAR, OUTILLAGE, MARTEAU);
		final SousTypeProduit parent = parentPersistant();
		
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
		final ArgumentCaptor<Produit> captor 
				= ArgumentCaptor.forClass(Produit.class);

		/*
		 * Configuration du Mock :
		 * - aucun doublon objet métier n'est détecté ;
		 * - le parent persistant est retrouvé ;
		 * - gateway.creer(...) retourne un objet métier créé ;
		 * - la conversion en OutputDTO de cet objet déclenche
		 *   une panne technique sans message.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parent));
		when(gateway.creer(any(Produit.class)))
				.thenReturn(cree);

		/* ACT - ASSERT */
		/* Garantit que l'exception technique d'origine est propagée. */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		/* Garantit que le SERVICE METIER UC ne produit jamais
		 * un message utilisateur null en cas d'échec de conversion.
		 */
		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
						+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		/* Garantit que le scénario a atteint la création Gateway
		 * avec un objet métier rattaché au parent persistant,
		 * puis la conversion finale côté UC.
		 */
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());
		verify(cree, times(1)).getSousTypeProduit();

		assertThat(captor.getValue().getProduit()).isEqualTo(MARTEAU);
		assertThat(captor.getValue().getSousTypeProduit()).isSameAs(parent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>contrôle d'abord l'absence de doublon via
	 * {@code gateway.findByLibelle(...)} ;</li>
	 * <li>récupère le parent persistant via
	 * {@code sousTypeProduitGateway.findByLibelle(...)} ;</li>
	 * <li>convertit l'InputDTO en objet métier rattaché
	 * au parent persistant ;</li>
	 * <li>délègue la création à {@code gateway.creer(...)} ;</li>
	 * <li>convertit l'objet métier créé en {@link OutputDTO} ;</li>
	 * <li>retourne un {@link OutputDTO} portant l'identifiant généré,
	 * le bon type, le bon parent et le bon libellé ;</li>
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
		final InputDTO dto = new ProduitDTO.InputDTO(
				BAZAR, OUTILLAGE, MARTEAU);
		
		final TypeProduit typePersistant = new TypeProduit(BAZAR);
		typePersistant.setIdTypeProduit(1L);
		
		final SousTypeProduit parentPersistant
				= new SousTypeProduit(OUTILLAGE, typePersistant);
		parentPersistant.setIdSousTypeProduit(10L);
		
		final Produit cree = new Produit(MARTEAU, parentPersistant);
		cree.setIdProduit(100L);
		
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
		 * - findByLibelle(...) sur le Gateway objet métier retourne
		 *   une liste vide pour simuler l'absence de doublon fonctionnel ;
		 * - findByLibelle(...) sur le Gateway parent retourne le parent
		 *   persistant ;
		 * - creer(...) retourne l'objet métier réellement créé
		 *   avec l'identifiant généré par le stockage.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());
		when(sousTypeProduitGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.singletonList(parentPersistant));
		when(gateway.creer(any(Produit.class))).thenReturn(cree);

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
		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeProduitGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		/* Garantit que l'objet métier envoyé au Gateway objet métier :
		 * - n'est pas null ;
		 * - ne porte pas encore d'identifiant ;
		 * - porte le libellé métier issu de l'InputDTO ;
		 * - porte le parent persistant retrouvé via le Gateway parent.
		 */
		final Produit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdProduit()).isNull();
		assertThat(envoye.getProduit()).isEqualTo(MARTEAU);
		assertThat(envoye.getSousTypeProduit()).isSameAs(parentPersistant);
		assertThat(envoye.getSousTypeProduit().getIdSousTypeProduit())
				.isEqualTo(10L);
		assertThat(envoye.getSousTypeProduit().getSousTypeProduit())
				.isEqualTo(OUTILLAGE);
		assertThat(envoye.getSousTypeProduit().getTypeProduit()).isNotNull();
		assertThat(envoye.getSousTypeProduit().getTypeProduit().getIdTypeProduit())
				.isEqualTo(1L);
		assertThat(envoye.getSousTypeProduit().getTypeProduit().getTypeProduit())
				.isEqualTo(BAZAR);

		/* Garantit que la réponse retournée au controller appelant :
		 * - n'est pas null ;
		 * - porte l'identifiant généré ;
		 * - porte le bon type ;
		 * - porte le bon parent ;
		 * - porte le bon libellé métier ;
		 * - expose le message utilisateur de succès.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(100L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_CREER_OK);
		
	} // __________________________________________________________________
	


	// ======================== rechercherTous ============================


	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway retourne null) :</p>
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

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTous()).thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO avec message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOAvecMessage() throws Exception {

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
		when(gateway.rechercherTous()).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(gateway KO sans message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousGatewayKOSansMessage() throws Exception {

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
		when(gateway.rechercherTous()).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO avec message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOAvecMessage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(conversion OutputDTO KO sans message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousConversionOutputDTOKOSansMessage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide + MESSAGE_RECHERCHE_VIDE » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousVideApresFiltrage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, null));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.rechercherTous();


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « filtre, trie, dédoublonne + MESSAGE_RECHERCHE_OK »
	 * ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_NOMINAL)
	@Test
	public void testRechercherTousNominal() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitScie, null, produitMarteau, produitScie));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.rechercherTous();


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________


	// ============================ rechercherTousString =================================


	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « propage rechercherTous() » ;</li>
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

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTous()).thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « propage rechercherTous() » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOAvecMessage() throws Exception {

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
		when(gateway.rechercherTous()).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(gateway KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « propage rechercherTous() » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousStringGatewayKOSansMessage() throws Exception {

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
		when(gateway.rechercherTous()).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion KO avec message) :</p>
	 * <ul>
	 * <li>exécute le scénario « propage rechercherTous() » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOAvecMessage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(conversion KO sans message) :</p>
	 * <ul>
	 * <li>exécute le scénario « propage rechercherTous() » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_CONVERSION_STRING_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousStringConversionStringKOSansMessage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousString(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide + MESSAGE_RECHERCHE_VIDE » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousStringVideApresFiltrage() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(null, null));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<String> retour = service.rechercherTousString();


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
	 * <p>garantit que rechercherTousString(libellés blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide + MESSAGE_RECHERCHE_VIDE » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_VIDE_APRES_LIBELLES_BLANK)
	@Test
	public void testRechercherTousStringVideApresLibellesBlank() throws Exception {

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
		final Produit produitBlank = produit(ESPACES, parent, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitBlank));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<String> retour = service.rechercherTousString();


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
	 * <p>garantit que rechercherTousString(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « libellés triés + MESSAGE_RECHERCHE_OK » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_STRING)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_STRING_NOMINAL)
	@Test
	public void testRechercherTousStringNominal() throws Exception {

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
		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(produitScie, produitMarteau));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<String> retour = service.rechercherTousString();


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).containsExactly(MARTEAU, SCIE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ rechercherTousParPage =================================


	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_PAGEABLE_NULL » ;</li>
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


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(null))
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOAvecMessage() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway KO sans message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageGatewayKOSansMessage() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException();

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(gateway retourne null) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_RECHERCHE_PAGINEE_KO » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_GATEWAY_RETOUR_NULL)
	@Test
	public void testRechercherTousParPageGatewayRetourNull() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(conversion OutputDTO KO avec message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOAvecMessage() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);
		final Produit produitKo = produitConversionKo(panneTechnique);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitKo), 0, 4, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenReturn(page);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + MESSAGE_GATEWAY);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(conversion OutputDTO KO sans message) :</p>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testRechercherTousParPageConversionOutputDTOKOSansMessage() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final IllegalStateException panneTechnique = new IllegalStateException();
		final Produit produitKo = produitConversionKo(panneTechnique);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitKo), 0, 4, 1L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenReturn(page);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ ProduitICuService.TIRET_ESPACE + ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(vide après filtrage) :</p>
	 * <ul>
	 * <li>exécute le scénario « page vide + MESSAGE_RECHERCHE_PAGINEE_OK » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_VIDE_APRES_FILTRAGE)
	@Test
	public void testRechercherTousParPageVideApresFiltrage() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(null, null), 0, 4, 2L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenReturn(page);


		/* ACT :
		 * exécute l'appel testé.
		 */
		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(requete);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getContent()).isNotNull().isEmpty();
		assertThat(retour.getTotalElements()).isEqualTo(2L);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTousParPage(nominal) :</p>
	 * <ul>
	 * <li>exécute le scénario « page cohérente + MESSAGE_RECHERCHE_PAGINEE_OK » ;</li>
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
	@Tag(TAG_RECHERCHER_TOUS_PAR_PAGE)
	@DisplayName(DISPLAY_NAME_RECHERCHER_TOUS_PAR_PAGE_NOMINAL)
	@Test
	public void testRechercherTousParPageNominal() throws Exception {

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
		final RequetePage requete = new RequetePage(0, 4);
		final SousTypeProduit parent = parentPersistant();
		final Produit produitScie = produit(SCIE, parent, 2L);
		final Produit produitMarteau = produit(MARTEAU, parent, 1L);
		final ResultatPage<Produit> page = new ResultatPage<Produit>(
				Arrays.asList(produitScie, null, produitMarteau, produitScie),
				0, 4, 10L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.rechercherTousParPage(requete))
				.thenReturn(page);


		/* ACT :
		 * exécute l'appel testé.
		 */
		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(requete);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(10L);
		assertThat(retour.getContent()).hasSize(2);
		assertThat(retour.getContent()).extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	} // __________________________________________________________________


	// ============================ findByLibelle =================================


	/**
	 * <div>
	 * <p>garantit que findByLibelle(null) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_PARAM_BLANK » ;</li>
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
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelle(null);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « null + MESSAGE_PARAM_BLANK » ;</li>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_BLANK)
	@Test
	public void testFindByLibelleBlank() throws Exception {

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
		final List<OutputDTO> retour = service.findByLibelle(ESPACES);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway retourne null) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_RETOUR_NULL)
	@Test
	public void testFindByLibelleGatewayRetourNull() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU)).thenReturn(null);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO avec message) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOAvecMessage() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(gateway KO sans message) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_GATEWAY_KOSANS_MESSAGE)
	@Test
	public void testFindByLibelleGatewayKOSansMessage() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO avec message) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOAVEC_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOAvecMessage() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(conversion OutputDTO KO sans message) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_CONVERSION_OUTPUT_DTOKOSANS_MESSAGE)
	@Test
	public void testFindByLibelleConversionOutputDTOKOSansMessage() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitKo));


		/* ACT - ASSERT :
		 * exécute l'appel testé et vérifie l'exception attendue.
		 */
		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isNull();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findByLibelle(introuvable) :</p>
	 * <ul>
	 * <li>exécute le scénario « liste vide + MESSAGE_RECHERCHE_VIDE » ;</li>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_INTROUVABLE)
	@Test
	public void testFindByLibelleIntrouvable() throws Exception {

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
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Collections.emptyList());


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelle(MARTEAU);


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
	 * <p>garantit que findByLibelle(nominal) :</p>
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
	@Tag(TAG_FIND_BY_LIBELLE)
	@DisplayName(DISPLAY_NAME_FIND_BY_LIBELLE_NOMINAL)
	@Test
	public void testFindByLibelleNominal() throws Exception {

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
		final SousTypeProduit parentA = parentPersistant();
		final SousTypeProduit parentB = parentPersistant(QUINCAILLERIE, ATELIER, 2L, 20L);
		final Produit produitA = produit(MARTEAU, parentA, 100L);
		final Produit produitB = produit(MARTEAU, parentB, 200L);

		/* Configuration du Mock :
		 * prépare les réponses ou exceptions Gateway nécessaires
		 * au scénario testé.
		 */
		when(gateway.findByLibelle(MARTEAU))
				.thenReturn(Arrays.asList(produitA, null, produitB));


		/* ACT :
		 * exécute l'appel testé.
		 */
		final List<OutputDTO> retour = service.findByLibelle(MARTEAU);


		/* ASSERT :
		 * vérifie le résultat, le message utilisateur observable
		 * et les interactions Gateway attendues ou interdites.
		 */
		assertThat(retour).isNotNull().hasSize(2);
		assertThat(retour).extracting(OutputDTO::getSousTypeProduit)
				.containsExactlyInAnyOrder(OUTILLAGE, ATELIER);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

	} // __________________________________________________________________


	// ============================ findByLibelleRapide =================================


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
	 * <li>exécute le scénario « RECHERCHE_SOUSTYPEPRODUIT_NULL » ;</li>
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
				.hasMessage(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que findAllByParent(parent blank) :</p>
	 * <ul>
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
	 * <li>exécute le scénario « MESSAGE_PAS_PARENT » ;</li>
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
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);
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
				.isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

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
