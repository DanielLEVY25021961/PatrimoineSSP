package levy.daniel.application.controllers.metier.produittype.desktop;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;

import levy.daniel.application.controllers.metier.produittype.SousTypeProduitIController;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;


/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitDesktopController.java :
 * </p>
 *
 * <p>Cette classe modélise :
 * <span style="font-weight:bold;">l' ADAPTER CONTROLLER DESKTOP</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link SousTypeProduit}</code>.</p>
 *
 * <p>Cette classe implémente :
 * <code style="font-weight:bold;">
 * {@link SousTypeProduitIController}</code>.</p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette classe est appelée par les VUES Desktop.</li>
 * <li>Cette classe ne communique qu'avec le
 * <code style="font-weight:bold;">{@link SousTypeProduitICuService}</code>
 * injecté par SPRING via le constructeur.</li>
 * <li>Cette classe ne connait ni GATEWAY,
 * ni DAO, ni persistance.</li>
 * </ul>
 * </div>
 *
 * <div>
 * <p>Cette classe implémente :</p>
 * <ul>
 * <li>la création d'un objet métier dans le stockage
 * via {@link #creer(InputDTO)}.</li>
 * <li>la recherche exhaustive des objets métier dans le stockage
 * via <code>rechercherTous()</code>.</li>
 * <li>la recherche exhaustive des libellés dans le stockage
 * via <code>rechercherTousString()</code>.</li>
 * <li>la recherche paginée des objets métier dans le stockage
 * via <code>rechercherTousParPage(...)</code>.</li>
 * <li>la recherche exacte d'objets métier par libellé
 * via <code>findByLibelle(...)</code>.</li>
 * <li>la recherche rapide d'objets métier par contenu de libellé
 * via <code>findByLibelleRapide(...)</code>.</li>
 * <li>la recherche des objets métier rattachés à un
 * {@link TypeProduit} parent
 * via <code>findAllByParent(...)</code>.</li>
 * <li>la recherche d'un objet métier à partir d'un DTO d'entrée
 * via <code>findByDTO(...)</code>.</li>
 * <li>la recherche d'un objet métier par identifiant
 * via <code>findById(...)</code>.</li>
 * <li>la modification d'un objet métier dans le stockage
 * via <code>update(...)</code>.</li>
 * <li>la suppression d'un objet métier dans le stockage
 * via <code>delete(...)</code>.</li>
 * <li>le comptage des objets métier présents dans le stockage
 * via <code>count()</code>.</li>
 * <li>la récupération du message utilisateur courant
 * via <code>getMessage()</code>.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 7 avril 2026
 */
public class SousTypeProduitDesktopController 
				implements SousTypeProduitIController {

	// ************************ATTRIBUTS************************************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">SERVICE USE CASE injecté par SPRING.</p>
	 * <p>Visibilité interface uniquement.</p>
	 * </div>
	 */
	private final SousTypeProduitICuService service;

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
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(SousTypeProduitDesktopController.class);
	
	
	

	// *************************METHODES************************************/

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR COMPLET.</p>
	 * <ul>
	 * <li>Indispensable pour l'injection par SPRING
	 * du {@link SousTypeProduitICuService} via le constructeur.</li>
	 * <li>Ne surtout pas créer de constructeur d'arité nulle
	 * dans cette classe (sinon, SPRING chercherait
	 * à l'utiliser et cela casserait l'injection du SERVICE).</li>
	 * </ul>
	 * </div>
	 *
	 * @param pService : SousTypeProduitICuService :
	 * le SERVICE UC utilisé par ce CONTROLLER Desktop.
	 */
	public SousTypeProduitDesktopController(
			@Qualifier("SousTypeProduitCuService")
			final SousTypeProduitICuService pService) {
		super();
		this.service = pService;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SousTypeProduitDTO.OutputDTO creer(
			final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* ****** CONTROLES DE SURFACE SUR L'INPUTDTO. ****** */
		/* Si pInputDTO == null : 
		 * émet un message MESSAGE_CREER_VUE_NULL + Retourne null. */
		if (pInputDTO == null) {
			this.message = MESSAGE_CREER_VUE_NULL;
			return null;
		}

		/* Si libellé blank (null ou espaces) :  
		 * émet un message MESSAGE_CREER_VUE_BLANK + Retourne null. */
		if (pInputDTO.getSousTypeProduit() == null
				|| pInputDTO.getSousTypeProduit().isBlank()) {
			this.message = MESSAGE_CREER_VUE_BLANK;
			return null;
		}

		/* ****** CONTROLE DE SURFACE SUR LE LIBELLE DU PARENT. ****** */
		/* Si le parent est null ou a un libellé null : 
		 * émet un message MESSAGE_CREER_VUE_PARENT_BLANK 
		 * + Retourne null. */
		if (pInputDTO.getTypeProduit() == null
				|| pInputDTO.getTypeProduit().isBlank()) {
			this.message = MESSAGE_CREER_VUE_PARENT_BLANK;
			return null;
		}

		/* ****** CREATION VIA LE SERVICE UC. ****** */
		try {

			/*
			 * Délègue la création au SERVICE UC
			 * puis récupère le message utilisateur du Service.
			 */
			final SousTypeProduitDTO.OutputDTO reponse
				= this.service.creer(pInputDTO);
			this.message = this.service.getMessage();

			/*
			 * retourne la réponse obtenue du Service.
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
	public List<SousTypeProduitDTO.OutputDTO> rechercherTous() 
			throws Exception {

		/* ****** RECHERCHE EXHAUSTIVE. ****** */
		try {

			/*
			 * Délègue la recherche exhaustive au SERVICE UC
			 * puis récupère le message utilisateur du Service.
			 */
			final List<SousTypeProduitDTO.OutputDTO> reponse
				= this.service.rechercherTous();
			this.message = this.service.getMessage();

			/*
			 * retourne la liste obtenue du Service.
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
	
	
	
} // FIN DE LA CLASSE SousTypeProduitDesktopController.--------------------
