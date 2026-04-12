/* ********************************************************************* */
/* ******************** TEST MOCKMVC CONTROLLER WEB ******************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import levy.daniel.application.controllers.metier.produittype.SousTypeProduitIController;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitWebControllerMockMvcTest.java :
 * </p>
 * <ul>
 * <li>Tests MockMvc ciblés du CONTROLLER ADAPTER WEB
 * {@link SousTypeProduitWebController}.</li>
 * <li>Vérifie le comportement HTTP réel exposé par Spring MVC :
 * existence des mappings,
 * binding JSON,
 * gestion d'un body absent,
 * sérialisation JSON de la réponse
 * et accessibilité du message exposé par l'endpoint dédié.</li>
 * <li>Le SERVICE UC {@link SousTypeProduitICuService} est mocké :
 * cette classe ne prouve donc ni la logique métier interne,
 * ni la persistance,
 * ni les scénarios d'intégration avec preuve BD.</li>
 * <li>Cette classe complète les tests Mockito du controller
 * en ajoutant une vérification concrète de la couche HTTP.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 10 avril 2026
 */
/*
 * Limite le contexte Spring de test à la couche web utile ici.
 * On teste le controller web réel et son comportement HTTP,
 * sans démarrer toute l'application.
 */
@WebMvcTest(controllers = SousTypeProduitWebController.class)

/*
 * Indique à Spring quelle configuration minimale utiliser
 * pour démarrer proprement ce test MockMvc.
 * Cela évite d'aller chercher une configuration plus large
 * qui ne serait pas utile ici.
 */
@ContextConfiguration(
		classes = SousTypeProduitWebControllerMockMvcTest.MockMvcBootConfiguration.class)

/*
 * Active le groupe de profils test-web-jpa.
 * Ici, cela garde une cohérence avec le mode WEB de test,
 * même si le service métier est mocké dans cette classe.
 */
@ActiveProfiles({ "test-web-jpa" })

/*
 * Demande à Spring de reconstruire un contexte propre après chaque méthode
 * afin d'éviter qu'un test ne pollue le suivant
 * par l'état mémorisé du controller ou des mocks Spring.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SousTypeProduitWebControllerMockMvcTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests MockMvc du controller web. */
	public static final String TAG = "controller-web-mockmvc";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** TypeProduit parent modifié : "tourisme". */
	public static final String TOURISME = "tourisme";

	/** SousTypeProduit parent : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** SousTypeProduit modifié : "guides". */
	public static final String GUIDES = "guides";

	/** "/soustypeproduit/message" */
	public static final String SOUS_TYPE_PRODUIT_MESSAGE
		= "/soustypeproduit/message";

	// *************************** BEANS ***********************************/

	/** MockMvc Spring MVC. */
	@Autowired
	private MockMvc mockMvc;

	/** SERVICE UC mocké sous le nom réellement injecté par le controller. */
	@MockitoBean(name = "SousTypeProduitCuService")
	private SousTypeProduitICuService service;

	// ********************** CONFIGURATION DE TEST ***********************/

	/**
	 * <div>
	 * <p>Configuration Spring Boot minimale dédiée au test MockMvc.</p>
	 * <p>
	 * Elle sert uniquement à fournir à Spring Boot
	 * une {@link SpringBootConfiguration} explicite,
	 * afin d'éviter la recherche automatique infructueuse
	 * d'une configuration applicative dans l'arborescence des packages.
	 * </p>
	 * </div>
	 */
	/*
	 * Déclare une classe de configuration Spring 
	 * dédiée à ce test d'intégration.
	 * Elle sert de point d'entrée au contexte Spring Boot de test.
	 * @SpringBootConfiguration est l’alternative Spring Boot 
	 * à @Configuration (annotation Spring générique 
	 * pour déclarer une classe de configuration), 
	 * prévue pour marquer une configuration 
	 * d’application Boot et pouvoir être trouvée automatiquement, 
	 * notamment dans les tests
	 */
	@SpringBootConfiguration
	
	/*
	 * Demande à Spring Boot d'appliquer son auto-configuration standard
	 * compatible avec les dépendances présentes et le profil de test actif.
	 */
	@EnableAutoConfiguration
	
	/*
	 * Importe juste la ou les classes nécessaires 
	 * pourla configuration SPRING du présent test.
	 */
	@Import(SousTypeProduitWebController.class)
	public static class MockMvcBootConfiguration {
		/* configuration minimale de bootstrapping du test. */
	}

	// *************************** CONSTRUCTEUR ****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitWebControllerMockMvcTest() {
		super();
	}

	
	
	// *************************** METHODES *******************************/

	/**
	 * <div>
	 * <p>POST /soustypeproduit/creer sans body : le binding HTTP transmet {@code null}.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /soustypeproduit/creer sans body : 200 + message local MESSAGE_CREER_VUE_NULL + aucune interaction service")
	public void testCreerSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/soustypeproduit/creer"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitIController.MESSAGE_CREER_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>POST /soustypeproduit/parent sans body : le binding HTTP transmet {@code null}.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /soustypeproduit/parent sans body : 200 + message local MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL + aucune interaction service")
	public void testFindAllByParentSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/soustypeproduit/parent"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>GET /soustypeproduit/rechercherTous : smoke test du mapping GET et du rendu JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("GET /soustypeproduit/rechercherTous : 200 + tableau JSON + message service")
	public void testRechercherTousHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitDTO.OutputDTO dto
			= new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);

		when(this.service.rechercherTous()).thenReturn(List.of(dto));
		when(this.service.getMessage())
				.thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(get("/soustypeproduit/rechercherTous"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].idSousTypeProduit").value(1))
				.andExpect(jsonPath("$[0].typeProduit").value(BAZAR))
				.andExpect(jsonPath("$[0].sousTypeProduit").value(OUTILLAGE));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_OK));

		verify(this.service, times(1)).rechercherTous();
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>PUT /soustypeproduit : smoke test du mapping PUT racine et du binding JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("PUT /soustypeproduit : 200 + binding JSON InputDTO + JSON OutputDTO + message service")
	public void testUpdateHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitDTO.OutputDTO output
			= new SousTypeProduitDTO.OutputDTO(7L, TOURISME, GUIDES, null);

		when(this.service.update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit())
				&& GUIDES.equals(dto.getSousTypeProduit()))))
				.thenReturn(output);
		when(this.service.getMessage())
				.thenReturn(SousTypeProduitICuService.MESSAGE_MODIF_OK + GUIDES);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				put("/soustypeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "tourisme",
								  "sousTypeProduit": "guides"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.idSousTypeProduit").value(7))
				.andExpect(jsonPath("$.typeProduit").value(TOURISME))
				.andExpect(jsonPath("$.sousTypeProduit").value(GUIDES));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitICuService.MESSAGE_MODIF_OK + GUIDES));

		verify(this.service, times(1)).update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit())
				&& GUIDES.equals(dto.getSousTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>DELETE /soustypeproduit : smoke test du mapping DELETE racine et du binding JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("DELETE /soustypeproduit : 200 + binding JSON InputDTO + message service")
	public void testDeleteHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		doNothing().when(this.service)
				.delete(argThat(dto ->
						dto != null
						&& BAZAR.equals(dto.getTypeProduit())
						&& OUTILLAGE.equals(dto.getSousTypeProduit())));
		when(this.service.getMessage())
				.thenReturn(SousTypeProduitICuService.MESSAGE_DELETE_OK + OUTILLAGE);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				delete("/soustypeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar",
								  "sousTypeProduit": "outillage"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitICuService.MESSAGE_DELETE_OK + OUTILLAGE));

		verify(this.service, times(1)).delete(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())
				&& OUTILLAGE.equals(dto.getSousTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>POST /soustypeproduit/parent : smoke test du mapping POST et du binding JSON parent.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /soustypeproduit/parent : 200 + binding JSON TypeProduitDTO + tableau JSON + message service")
	public void testFindAllByParentHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitDTO.OutputDTO dto
			= new SousTypeProduitDTO.OutputDTO(9L, BAZAR, OUTILLAGE, null);

		when(this.service.findAllByParent(argThat(parent ->
				parent != null
				&& BAZAR.equals(parent.getTypeProduit()))))
				.thenReturn(List.of(dto));
		when(this.service.getMessage())
				.thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				post("/soustypeproduit/parent")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].idSousTypeProduit").value(9))
				.andExpect(jsonPath("$[0].typeProduit").value(BAZAR))
				.andExpect(jsonPath("$[0].sousTypeProduit").value(OUTILLAGE));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(SOUS_TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						SousTypeProduitICuService.MESSAGE_RECHERCHE_OK));

		verify(this.service, times(1)).findAllByParent(argThat(parent ->
				parent != null
				&& BAZAR.equals(parent.getTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



} // FIN DE LA CLASSE SousTypeProduitWebControllerMockMvcTest.------------