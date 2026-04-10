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
import org.springframework.context.annotation.Import;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import levy.daniel.application.controllers.metier.produittype.ProduitIController;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitWebControllerMockMvcTest.java :
 * </p>
 * <p>
 * Exemple de tests MockMvc ciblés du CONTROLLER ADAPTER WEB
 * {@link ProduitWebController}.
 * </p>
 * <p>
 * Cette classe ne remplace ni les tests Mockito du controller,
 * ni les tests d'intégration avec preuve BD.
 * Elle ajoute un petit verrouillage HTTP réel sur quelques endpoints critiques :
 * mapping Spring MVC, binding JSON,
 * corps absent sur {@code @RequestBody(required = false)}
 * et restitution HTTP de la réponse.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 10 avril 2026
 */
@WebMvcTest(controllers = ProduitWebController.class)
@ContextConfiguration(
		classes = ProduitWebControllerMockMvcTest.MockMvcBootConfiguration.class)
@ActiveProfiles({ "test", "web" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProduitWebControllerMockMvcTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests MockMvc du controller web. */
	public static final String TAG = "controller-web-mockmvc";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** SousTypeProduit parent : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** Produit : "marteau". */
	public static final String MARTEAU = "marteau";

	/** Produit modifié : "marteau-pro". */
	public static final String MARTEAU_PRO = "marteau-pro";
	
	/**
	 * "/produit/message"
	 */
	public static final String PRODUIT_MESSAGE = "/produit/message";

	// *************************** BEANS ***********************************/

	/** MockMvc Spring MVC. */
	@Autowired
	private MockMvc mockMvc;


	/** SERVICE UC mocké sous le nom réellement injecté par le controller. */
	@MockitoBean(name = "ProduitCuService")
	private ProduitICuService service;

	// *************************** CONSTRUCTEUR ****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitWebControllerMockMvcTest() {
		super();
	}
	
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
	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import(ProduitWebController.class)
	public static class MockMvcBootConfiguration {
		/* configuration minimale de bootstrapping du test. */
	}

	// *************************** METHODES *******************************/



	/**
	 * <div>
	 * <p>POST /produit/creer sans body : le binding HTTP transmet {@code null}.</p>
	 * <ul>
	 * <li>le mapping POST /creer existe réellement</li>
	 * <li>le body absent n'est pas rejeté par Spring MVC</li>
	 * <li>le controller produit MESSAGE_CREER_VUE_NULL</li>
	 * <li>le service n'est jamais appelé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /produit/creer sans body : 200 + message local MESSAGE_CREER_VUE_NULL + aucune interaction service")
	public void testCreerSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/produit/creer"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						ProduitIController.MESSAGE_CREER_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>POST /produit/parent sans body : le binding HTTP transmet {@code null}.</p>
	 * <ul>
	 * <li>le mapping POST /parent existe réellement</li>
	 * <li>le body absent n'est pas rejeté par Spring MVC</li>
	 * <li>le controller produit MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL</li>
	 * <li>le service n'est jamais appelé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /produit/parent sans body : 200 + message local MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL + aucune interaction service")
	public void testFindAllByParentSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/produit/parent"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						ProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>GET /produit/rechercherTous : smoke test du mapping GET et du rendu JSON.</p>
	 * <ul>
	 * <li>le mapping GET /rechercherTous existe réellement</li>
	 * <li>la liste JSON restituée correspond au DTO du service</li>
	 * <li>le message du service est récupéré ensuite sur /message</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("GET /produit/rechercherTous : 200 + tableau JSON + message service")
	public void testRechercherTousHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitDTO.OutputDTO dto
			= new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);

		when(this.service.rechercherTous()).thenReturn(List.of(dto));
		when(this.service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(get("/produit/rechercherTous"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].idProduit").value(1))
				.andExpect(jsonPath("$[0].typeProduit").value(BAZAR))
				.andExpect(jsonPath("$[0].sousTypeProduit").value(OUTILLAGE))
				.andExpect(jsonPath("$[0].produit").value(MARTEAU));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						ProduitICuService.MESSAGE_RECHERCHE_OK));

		verify(this.service, times(1)).rechercherTous();
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>PUT /produit : smoke test du mapping PUT racine et du binding JSON.</p>
	 * <ul>
	 * <li>le JSON d'entrée est bien désérialisé en {@link ProduitDTO.InputDTO}</li>
	 * <li>le service reçoit bien les trois champs métier attendus</li>
	 * <li>la réponse HTTP restitue l'{@link ProduitDTO.OutputDTO}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("PUT /produit : 200 + binding JSON InputDTO + JSON OutputDTO + message service")
	public void testUpdateHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitDTO.InputDTO input
			= new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU_PRO);
		final ProduitDTO.OutputDTO output
			= new ProduitDTO.OutputDTO(7L, BAZAR, OUTILLAGE, MARTEAU_PRO);

		when(this.service.update(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())
				&& OUTILLAGE.equals(dto.getSousTypeProduit())
				&& MARTEAU_PRO.equals(dto.getProduit()))))
				.thenReturn(output);
		when(this.service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_MODIF_OK + MARTEAU_PRO);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				put("/produit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar",
								  "sousTypeProduit": "outillage",
								  "produit": "marteau-pro"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.idProduit").value(7))
				.andExpect(jsonPath("$.typeProduit").value(BAZAR))
				.andExpect(jsonPath("$.sousTypeProduit").value(OUTILLAGE))
				.andExpect(jsonPath("$.produit").value(MARTEAU_PRO));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						ProduitICuService.MESSAGE_MODIF_OK + MARTEAU_PRO));

		verify(this.service, times(1)).update(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())
				&& OUTILLAGE.equals(dto.getSousTypeProduit())
				&& MARTEAU_PRO.equals(dto.getProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>DELETE /produit : smoke test du mapping DELETE racine et du binding JSON.</p>
	 * <ul>
	 * <li>le JSON d'entrée est bien désérialisé</li>
	 * <li>le service est bien invoqué</li>
	 * <li>la réponse HTTP reste vide</li>
	 * <li>le message du service reste accessible sur /message</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("DELETE /produit : 200 + binding JSON InputDTO + message service")
	public void testDeleteHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitDTO.InputDTO input
			= new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		doNothing().when(this.service)
				.delete(argThat(dto ->
						dto != null
						&& BAZAR.equals(dto.getTypeProduit())
						&& OUTILLAGE.equals(dto.getSousTypeProduit())
						&& MARTEAU.equals(dto.getProduit())));
		when(this.service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_DELETE_OK + MARTEAU);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				delete("/produit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar",
								  "sousTypeProduit": "outillage",
								  "produit": "marteau"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						ProduitICuService.MESSAGE_DELETE_OK + MARTEAU));

		verify(this.service, times(1)).delete(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())
				&& OUTILLAGE.equals(dto.getSousTypeProduit())
				&& MARTEAU.equals(dto.getProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



} // FIN DE LA CLASSE ProduitWebControllerMockMvcTest.--------------------