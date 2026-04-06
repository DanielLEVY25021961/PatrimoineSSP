/* ********************************************************************* */
/* ****************** ADAPTER DESKTOP CONTROLLER *********************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.desktop;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.pagination.DirectionTriDTO;
import levy.daniel.application.model.dto.pagination.RequetePageDTO;
import levy.daniel.application.model.dto.pagination.ResultatPageDTO;
import levy.daniel.application.model.dto.pagination.TriSpecDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.pagination.DirectionTri;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;
import levy.daniel.application.model.services.produittype.pagination.TriSpec;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitDesktopController.java :
 * </p>
 *
 * <p>Cette classe modélise :
 * <span style="font-weight:bold;">l' ADAPTER CONTROLLER DESKTOP</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link TypeProduit}</code>.</p>
 * </div>
 *
 * <div>
 * <ul>
 * <li>Cette classe est appelée par les VUES Desktop.</li>
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
@Controller("TypeProduitDesktopController")
@Profile("desktop")
public class TypeProduitDesktopController implements TypeProduitIController {

	/* ************************* ATTRIBUTS ***************************** */

	/**
	 * <div>
	 * <p style="font-weight:bold;">SERVICE USE CASE injecté par SPRING.</p>
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
		= LogManager.getLogger(TypeProduitDesktopController.class);

	/* ************************** METHODES ***************************** */

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
	 * le SERVICE UC utilisé par ce CONTROLLER Desktop.
	 */
	public TypeProduitDesktopController(
			@Qualifier("TypeProduitCuService")
			final TypeProduitICuService pService) {
		super();
		this.service = pService;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public OutputDTO creer(final InputDTO pInputDTO) throws Exception {

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
			this.message = this.service.getMessage();

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
	public ResultatPageDTO<TypeProduitDTO.OutputDTO> rechercherTousParPage(
					final RequetePageDTO pRequetePageDTO)
							throws Exception {

		/* ******** TRAITEMENTS DE SURFACE ********/
		/*
		 * Si pRequetePageDTO == null :
		 * émet un message utilisateur
		 * MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL
		 * + retourne null.
		 */
		if (pRequetePageDTO == null) {
			this.message = MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL;
			return null;
		}

		/* ****** RECHERCHE PAGINEE. ****** */
		try {

			/*
			 * Convertit d'abord la pagination DTO reçue de la VUE
			 * en pagination interne du SERVICE UC 
			 * (appartenant au package services.pagination).
			 * Le numéro de page exposé dans la VUE reste "humain" :
			 * page 1 -> index interne 0.
			 */
			final int pageNumberHumain
				= pRequetePageDTO.getPageNumber();

			/* applique le numéro de page par défaut 
			 * NUMERO_PAGE_HUMAIN_PAR_DEFAUT 
			 * si la VUE a omis d'en fournir un. */
			final int pageNumberInterne
				= pageNumberHumain >= NUMERO_PAGE_HUMAIN_PAR_DEFAUT
					? pageNumberHumain - 1
					: NUMERO_PAGE_HUMAIN_PAR_DEFAUT - 1;

			/* applique la taille de page par défaut 
			 * NOMBRE_ENREGISTREMENTS_PAR_PAGE_PAR_DEFAUT 
			 * si la VUE a omis de l'indiquer. */
			final int pageSizeInterne
				= pRequetePageDTO.getPageSize() >= 1
					? pRequetePageDTO.getPageSize()
					: NOMBRE_ENREGISTREMENTS_PAR_PAGE_PAR_DEFAUT;

			final List<TriSpec> trisInternes
				= new ArrayList<TriSpec>();

			for (final TriSpecDTO triDTO : pRequetePageDTO.getTris()) {

				if (triDTO != null) {

					final DirectionTri directionInterne
						= triDTO.getDirection() == DirectionTriDTO.DESC
							? DirectionTri.DESC
							: DirectionTri.ASC;

					trisInternes.add(
							new TriSpec(
									triDTO.getPropriete(),
									directionInterne));

				}

			}

			/* Crée un RequetePage interne 
			 * (appartenant au package services.pagination). */
			final RequetePage requeteInterne
				= new RequetePage(
						pageNumberInterne,
						pageSizeInterne,
						trisInternes);

			/*
			 * Délègue la recherche paginée au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final ResultatPage<TypeProduitDTO.OutputDTO> reponseInterne
				= this.service.rechercherTousParPage(requeteInterne);
			this.message = this.service.getMessage();

			/* Si le Service retourne null : 
			 * retourne null + message du service.*/
			if (reponseInterne == null) {
				return null;
			}

			/*
			 * Retourne à la VUE un résultat paginé DTO.
			 * La numérotation redevient "humaine" :
			 * index interne 0 -> page 1.
			 */
			final ResultatPageDTO<TypeProduitDTO.OutputDTO> reponse 
				= new ResultatPageDTO<TypeProduitDTO.OutputDTO>(
					reponseInterne.getContent(),
					reponseInterne.getPageNumber() + 1,
					reponseInterne.getPageSize(),
					reponseInterne.getTotalElements());
			
			/* Retourne la réponse à la VUE. */
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
	public OutputDTO findByLibelle(final String pLibelle) throws Exception {

		/* ******** TRAITEMENTS DE SURFACE ********/
		/*
		 * Si pLibelle == null :
		 * émet un message utilisateur MESSAGE_FIND_BY_LIBELLE_VUE_NULL
		 * + retourne null.
		 */
		if (pLibelle == null) {
			this.message = MESSAGE_FIND_BY_LIBELLE_VUE_NULL;
			return null;
		}

		final String libelle = pLibelle;

		/*
		 * Si le libellé est blank (null ou espaces) :
		 * émet un message utilisateur MESSAGE_FIND_BY_LIBELLE_VUE_BLANK
		 * + retourne null.
		 */
		if (StringUtils.isBlank(libelle)) {
			this.message = MESSAGE_FIND_BY_LIBELLE_VUE_BLANK;
			return null;
		}

		/* ****** RECHERCHE EXACTE PAR LIBELLE. ****** */
		try {

			/*
			 * Délègue la recherche exacte par libellé au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final OutputDTO reponse = this.service.findByLibelle(libelle);
			this.message = this.service.getMessage();

			/*
			 * retourne l'OutputDTO obtenu.
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
	public List<OutputDTO> findByLibelleRapide(
			final String pContenu)
					throws Exception {

		/* ******** TRAITEMENTS DE SURFACE ********/
		/*
		 * Si pContenu == null :
		 * émet un message utilisateur
		 * MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL
		 * + retourne null.
		 */
		if (pContenu == null) {
			this.message = MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL;
			return null;
		}

		final String contenu = pContenu;

		/* ****** RECHERCHE RAPIDE PAR LIBELLE. ****** */
		try {

			/*
			 * Le cas blank n'est pas bloqué localement
			 * par le CONTROLLER.
			 * Il est délégué au SERVICE UC,
			 * qui applique alors son propre contrat (retourner tout).
			 */

			/*
			 * Délègue la recherche rapide par libellé au SERVICE UC
			 * et récupère le message éventuel du Service.
			 */
			final List<OutputDTO> reponse
				= this.service.findByLibelleRapide(contenu);
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
	public String getMessage() {

		/*
		 * Retourne le message utilisateur courant
		 * produit par le SERVICE UC ou généré par le présent CONTROLLER.
		 */
		return this.message;
	}

	
	
}