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

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitWebControllerMockMvcTest.java :
 * </p>
 * <ul>
 * <li>Tests MockMvc ciblés du CONTROLLER ADAPTER WEB
 * {@link TypeProduitWebController}.</li>
 * <li>Vérifie le comportement HTTP réel exposé par Spring MVC :
 * existence des mappings,
 * binding JSON,
 * gestion d'un body absent,
 * sérialisation JSON de la réponse
 * et accessibilité du message exposé par l'endpoint dédié.</li>
 * <li>Le SERVICE UC {@link TypeProduitICuService} est mocké :
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
@WebMvcTest(controllers = TypeProduitWebController.class)

/*
 * Indique à Spring quelle configuration minimale utiliser
 * pour démarrer proprement ce test MockMvc.
 * Cela évite d'aller chercher une configuration plus large
 * qui ne serait pas utile ici.
 */
@ContextConfiguration(
		classes = TypeProduitWebControllerMockMvcTest.MockMvcBootConfiguration.class)

/*
 * Active le groupe de profils test-web-jpa.
 * Ici, cela garde une cohérence avec le mode WEB de test,
 * même si le service métier est mocké dans cette classe.
 */
@ActiveProfiles({  "test-web-jpa" })

/*
 * Demande à Spring de reconstruire un contexte propre après chaque méthode
 * afin d'éviter qu'un test ne pollue le suivant
 * par l'état mémorisé du controller ou des mocks Spring.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TypeProduitWebControllerMockMvcTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests MockMvc du controller web. */
	public static final String TAG = "controller-web-mockmvc";

	/** TypeProduit : "bazar". */
	public static final String BAZAR = "bazar";

	/** TypeProduit modifié : "tourisme". */
	public static final String TOURISME = "tourisme";

	/** "/typeproduit/message" */
	public static final String TYPE_PRODUIT_MESSAGE = "/typeproduit/message";

	// *************************** BEANS ***********************************/

	/** MockMvc Spring MVC. */
	@Autowired
	private MockMvc mockMvc;

	/** SERVICE UC mocké sous le nom réellement injecté par le controller. */
	@MockitoBean(name = "TypeProduitCuService")
	private TypeProduitICuService service;

	// ********************** CONFIGURATION DE TEST ***********************/

	/**
	 * <div>
	 * <p>Configuration Spring Boot minimale dédiée au test MockMvc.</p>
	 * </div>
	 */
	/*
	 * Déclare une classe de configuration Spring dédiée à ce test d'intégration.
	 * Elle sert de point d'entrée au contexte Spring Boot de test.
	 * Pourquoi @SpringBootConfiguration au lieu de @Configuration
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
	@Import(TypeProduitWebController.class)
	public static class MockMvcBootConfiguration {
		/* configuration minimale de bootstrapping du test. */
	}

	// *************************** CONSTRUCTEUR ****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitWebControllerMockMvcTest() {
		super();
	}

	// *************************** METHODES *******************************/

	
	
	/**
	 * <div>
	 * <p>POST /typeproduit/creer sans body : le binding HTTP transmet {@code null}.</p>
	 * <ul>
	 * <li>le mapping POST /typeproduit/creer existe réellement ;</li>
	 * <li>l'absence de body n'est pas rejetée par Spring MVC ;</li>
	 * <li>le controller produit le message local
	 * {@link TypeProduitIController#MESSAGE_CREER_VUE_NULL} ;</li>
	 * <li>le service n'est jamais appelé.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /typeproduit/creer sans body : 200 + message local MESSAGE_CREER_VUE_NULL + aucune interaction service")
	public void testCreerSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/typeproduit/creer"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitIController.MESSAGE_CREER_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>GET /typeproduit/rechercherTous : 
	 * smoke test (test simple de bon fonctionnement HTTP) 
	 * du mapping GET et du rendu JSON.</p>
	 * <ul>
	 * <li>test simple du mapping GET et du rendu JSON</li>
	 * <li>le mapping GET /typeproduit/rechercherTous existe réellement ;</li>
	 * <li>la liste JSON restituée correspond au DTO renvoyé par le service ;</li>
	 * <li>le message du service reste ensuite accessible sur l'endpoint
	 * {@code /typeproduit/message}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("GET /typeproduit/rechercherTous : 200 + tableau JSON + message service")
	public void testRechercherTousHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitDTO.OutputDTO dto
			= new TypeProduitDTO.OutputDTO(1L, BAZAR);

		when(this.service.rechercherTous()).thenReturn(List.of(dto));
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(get("/typeproduit/rechercherTous"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].idTypeProduit").value(1))
				.andExpect(jsonPath("$[0].typeProduit").value(BAZAR));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_RECHERCHE_OK));

		verify(this.service, times(1)).rechercherTous();
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>PUT /typeproduit : 
	 * smoke test (test simple de bon fonctionnement HTTP) 
	 * du mapping PUT racine et du binding JSON.</p>
	 * <ul>
	 * <li>test simple du mapping PUT racine et de la lecture du JSON</li>
	 * <li>le JSON d'entrée est bien désérialisé en
	 * {@link TypeProduitDTO.InputDTO} ;</li>
	 * <li>le service reçoit bien le libellé métier attendu ;</li>
	 * <li>la réponse HTTP restitue correctement le
	 * {@link TypeProduitDTO.OutputDTO} renvoyé par le service ;</li>
	 * <li>le message du service reste accessible sur l'endpoint
	 * {@code /typeproduit/message}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("PUT /typeproduit : 200 + binding JSON InputDTO + JSON OutputDTO + message service")
	public void testUpdateHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitDTO.OutputDTO output
			= new TypeProduitDTO.OutputDTO(7L, TOURISME);

		when(this.service.update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit()))))
				.thenReturn(output);
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_MODIF_OK + TOURISME);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				put("/typeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "tourisme"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.idTypeProduit").value(7))
				.andExpect(jsonPath("$.typeProduit").value(TOURISME));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_MODIF_OK + TOURISME));

		verify(this.service, times(1)).update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>DELETE /typeproduit : 
	 * smoke test (test simple de bon fonctionnement HTTP) 
	 * du mapping DELETE racine et du binding JSON.</p>
	 * <ul>
	 * <li>test simple du mapping DELETE racine et de la lecture du JSON</li>
	 * <li>le JSON d'entrée est bien désérialisé en
	 * {@link TypeProduitDTO.InputDTO} ;</li>
	 * <li>le service est bien invoqué avec le bon DTO ;</li>
	 * <li>la réponse HTTP reste vide, conformément au contrat observable ;</li>
	 * <li>le message du service reste accessible sur l'endpoint
	 * {@code /typeproduit/message}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("DELETE /typeproduit : 200 + binding JSON InputDTO + message service")
	public void testDeleteHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		doNothing().when(this.service)
				.delete(argThat(dto ->
						dto != null
						&& BAZAR.equals(dto.getTypeProduit())));
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_DELETE_OK + BAZAR);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				delete("/typeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_DELETE_OK + BAZAR));

		verify(this.service, times(1)).delete(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



} // FIN DE LA CLASSE TypeProduitWebControllerMockMvcTest.----------------