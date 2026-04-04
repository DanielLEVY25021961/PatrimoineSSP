/* ********************************************************************* */
/* ******************* ADAPTER WEB CONTROLLER ************************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitWebController.java :</p>
 *
 * <p>Cette classe modélise :
 * <span style="font-weight:bold;">l' ADAPTER CONTROLLER WEB</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link TypeProduit}</code>.</p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette classe est appelée par la couche VUES / HTTP.</li>
 * <li>Cette classe ne communique qu'avec le
 * <code style="font-weight:bold;">{@link TypeProduitICuService}</code>
 * injecté par SPRING via le constructeur.</li>
 * <li>Cette classe ne connait ni GATEWAY,
 * ni DAO, ni persistance.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p>Dans l'état courant du chantier, elle implémente :</p>
 * <ul>
 * <li>la création d'un objet métier dans le stockage
 * via {@link #creer(InputDTO)}.</li>
 * <li>la recherche exhaustive des objets métier dans le stockage
 * via {@link #rechercherTous()}.</li>
 * <li>la récupération du message utilisateur courant
 * via {@link #getMessage()}.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
@RestController("TypeProduitWebController")
@RequestMapping("/typeproduit")
@Profile({ "web", "dev", "prod" })
public class TypeProduitWebController implements TypeProduitIController {

	/* *************************** ATTRIBUTS ****************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * SERVICE USE CASE injecté par SPRING.</p>
	 * <p>Visibilité interface uniquement.</p>
	 * </div>
	 */
	private final TypeProduitICuService service;

	/**
	 * <div>
	 * <p>Message à l'attention de l'utilisateur (VUE).</p>
	 * <p>Provient généralement directement du SERVICE METIER UC.
	 * Peut être créé ou enrichi par les contrôles de
	 * surface du présent CONTROLLER.</p>
	 * </div>
	 */
	private String message;

	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger :</p>
	 * <p>Logger pour Log4j
	 * (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances :</p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG
		= LogManager.getLogger(TypeProduitWebController.class);

	/* *************************** METHODES ******************************* */

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>Indispensable pour l'injection par SPRING
	 * du {@link TypeProduitICuService} via le constructeur.</li>
	 * <li>Ne surtout pas créer de constructeur d'arité nulle
	 * dans cette classe (sinon, SPRING chercherait
	 * à l'utiliser et cela casserait l'injection du SERVICE).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pService : TypeProduitICuService :
	 * le SERVICE UC utilisé par ce CONTROLLER Web.
	 */
	public TypeProduitWebController(
			@Qualifier("TypeProduitCuService")
			final TypeProduitICuService pService) {
		super();
		this.service = pService;
	}

	
		
	/**
	* {@inheritDoc}
	*/
	@Override
	@PostMapping
	public OutputDTO creer(@RequestBody final InputDTO pInputDTO)
			throws Exception {

		/* ******** TRAITEMENTS DE SURFACE ********/
		/*
		 * Si pInputDTO == null :
		 * émet un message utilisateur MESSAGE_CREER_VUE_NULL
		 * + retourne null.
		 */
		if (pInputDTO == null) {
			this.message = MESSAGE_CREER_VUE_NULL;
			return null;
		}

		final String libelle = pInputDTO.getTypeProduit();

		/*
		 * Si le libellé est blank (null ou espaces) :
		 * émet un message utilisateur MESSAGE_CREER_VUE_BLANK
		 * + retourne null.
		 */
		if (StringUtils.isBlank(libelle)) {
			this.message = MESSAGE_CREER_VUE_BLANK;
			return null;
		}

		/* ****** CREATION. ****** */		
		try {

			/*
			 * Délègue la création au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final OutputDTO reponse = this.service.creer(pInputDTO);
			message = this.service.getMessage();

			/*
			 * retourne l'OutputDTO créé.
			 */
			return reponse;

		} catch (Exception pException) {

			/*
			 * Récupère le message utilisateur éventuel du Service
			 * puis laisse l'Exception remonter à la VUE.
			 */
			this.message = this.service.getMessage();
			throw pException;

		}
		
	}

	
		
	/**
	* {@inheritDoc}
	*/
	@Override
	@GetMapping
	public List<OutputDTO> rechercherTous() throws Exception {

		/* ****** RECHERCHE EXHAUSTIVE. ****** */
		try {

			/*
			 * Délègue la recherche exhaustive au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final List<OutputDTO> reponse = this.service.rechercherTous();
			this.message = this.service.getMessage();

			/*
			 * retourne la liste d'OutputDTO obtenue.
			 */
			return reponse;

		} catch (final Exception pException) {

			/*
			 * Récupère le message utilisateur éventuel du Service
			 * puis laisse l'Exception remonter à la VUE.
			 */
			this.message = this.service.getMessage();
			throw pException;

		}

	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@GetMapping
	public List<String> rechercherTousString() throws Exception {

		/* ****** RECHERCHE EXHAUSTIVE STRING. ****** */
		try {

			/*
			 * Délègue la recherche exhaustive des libellés au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final List<String> reponse = this.service.rechercherTousString();
			this.message = this.service.getMessage();

			/*
			 * retourne la liste de String obtenue.
			 */
			return reponse;

		} catch (final Exception pException) {

			/*
			 * Récupère le message utilisateur éventuel du Service
			 * puis laisse l'Exception remonter à la VUE.
			 */
			this.message = this.service.getMessage();
			throw pException;

		}

	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {

		/*
		 * Retourne le message utilisateur courant
		 * produit par le SERVICE UC ou généré par le présent CONTROLLER.
		 */
		return this.message;
	}

	
	
}