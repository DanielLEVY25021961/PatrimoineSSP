/* ********************************************************************* */
/* ******************** ADAPTER SERVICE CU ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOProduit;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
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
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitCuService :
 * </p>
 * 
 * <p>
 * Cette classe modélise :
 * le <span style="font-weight:bold;">
 * SERVICE METIER (Use Case) ADAPTER</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link Produit}</code>.
 * </p>
 * </div>
 * 
 * <div>
 * <p style="font-weight:bold;">SERVICE USE CASE
 * </p>
 * <ul>
 * <li>Cette classe <span style="font-weight:bold;">
 * SERVICE METIER (Use Case)</span> ne connait que l'INTERFACE
 * TECHNIQUE GATEWAY qui est injectée par SPRING via le Constructeur.</li>
 * <li>Cette classe <span style="font-weight:bold;">
 * SERVICE METIER (Use Case)</span> ne connait
 * <span style="font-weight:bold;">pas</span> par exemple
 * le DAO JPA <code style="font-weight:bold;">
 * {@link ProduitDaoJPA}</code>
 * de la classe TECHNIQUE concrète <code style="font-weight:bold;">
 * {@link ProduitGatewayJPAService}</code> 
 * qui implémente l'interface
 * de SERVICE TECHNIQUE <code style="font-weight:bold;">
 * {@link ProduitGatewayIService}</code>.</li>
 * <li>SERVICE UC commun à tous les modes d'accès à l'application 
 * (WEB, MOBILE, DESKTOP) et à tous les environnements d'exécution 
 * (TEST, DEV, PROD, ...) -> Les SERVICES UC ne doivent 
 * pas avoir de PROFIL SPRING.</li>
 * <li>Cette classe ne dépend ni du mode d'entrée (desktop / web),
 * ni de l'environnement d'exécution (test / dev / prod).</li>
 * <li>Le choix du mode d'accès appartient aux controllers 
 * (Web, Mobile, Desktop),
 * et le choix du mode de stockage appartient aux gateways 
 * (JPA, XML, ...).</li>
 * </ul>
 * </div>
 * 
 * <div>
 * <p>C'est dans ce SERVICE USE CASE ADAPTER METIER que l'on :</p>
 * <ul>
 * <li>implémente la <span style="font-weight:bold;">
 * logique métier
 * </span>.</li>
 * <li>Reçoit des InputDTO provenant des Controllers</li>
 * <li><span style="font-weight:bold;">Convertit</span>
 * les InputDTO en Objets métier</li>
 * <li>Sollicite le SERVICE GATEWAY en lui passant des ObjetsMetier.</li>
 * <li>Traite les réponses (Objets métier) retournées
 * par le service Gateway.</li>
 * <li>Convertit les Objets métier retournés
 * par le gateway en OutputDTO.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@Service(value = "ProduitCuService")
public class ProduitCuService implements ProduitICuService {

	// *************************** ATTRIBUTS ******************************/

	/**
	 * Service Gateway (Technique) pour Produit.
	 */
	private final ProduitGatewayIService gateway;

	/**
	 * Service Gateway (Technique) pour SousTypeProduit (parent).
	 */
	private final SousTypeProduitGatewayIService sousTypeProduitGateway;

	/**
	 * Message observable (ThreadLocal).
	 */
	private final ThreadLocal<String> message = new ThreadLocal<String>();
	
	// *************************** LOG ******************************/

	/**
	 * Log Apache Commons.
	 */
	private static final Log LOG = LogFactory.getLog(ProduitCuService.class);


	// ************************* CONSTRUCTEURS ****************************/

	/**
	 * <div>
	 * <p>Constructeur Spring (DI).</p>
	 * </div>
	 *
	 * @param pGateway gateway Produit
	 * @param pSousTypeProduitGateway gateway SousTypeProduit
	 */
	public ProduitCuService(
			final ProduitGatewayIService pGateway,
			final SousTypeProduitGatewayIService pSousTypeProduitGateway) {
		super();
		this.gateway = pGateway;
		this.sousTypeProduitGateway = pSousTypeProduitGateway;
	}

	// *************************** METHODES *******************************/

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO creer(
			final InputDTO pInputDTO) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Si pInputDTO == null :
		 * Retourne null avec un message utilisateur MESSAGE_CREER_NULL.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/*
		 * Récupère le libellé métier du Produit.
		 */
		final String libelle = pInputDTO.getProduit();

		/*
		 * Si le libellé est blank :
		 * émet MESSAGE_CREER_NOM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelle)) {
			
			return this.traiterErreur(
					MESSAGE_CREER_NOM_BLANK,
					METHODE_CREER,
					new ExceptionParametreBlank(MESSAGE_CREER_NOM_BLANK));
		}

		/*
		 * Le parent SousTypeProduit est une 
		 * précondition observable du SERVICE UC.
		 * Si son libellé est blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		final String libelleParent = pInputDTO.getSousTypeProduit();

		if (StringUtils.isBlank(libelleParent)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_CREER,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Vérifie le doublon fonctionnel.
		 * Toute anomalie technique pendant ce contrôle
		 * est rationalisée côté UC.
		 */
		final boolean doublon;

		try {

			doublon = this.isDoublon(pInputDTO);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
							+ messageSecurise,
					METHODE_CREER,
					e);
		}

		/*
		 * Si doublon :
		 * émet MESSAGE_DOUBLON + libellé + LOG + ExceptionDoublon.
		 */
		if (doublon) {

			final String messageDoublon = MESSAGE_DOUBLON + libelle;

			return this.traiterErreur(
					messageDoublon,
					METHODE_CREER,
					new ExceptionDoublon(messageDoublon));
		}

		/*
		 * Récupère le parent persistant.
		 * Toute anomalie technique de recherche du parent
		 * est rationalisée côté UC.
		 */
		final SousTypeProduit parentPersistant;

		try {

			parentPersistant = this.rechercherParentPersistant(pInputDTO);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
							+ messageSecurise,
					METHODE_CREER,
					e);
		}

		/*
		 * Si le parent n'existe pas en stockage
		 * ou n'est pas persistant :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdSousTypeProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_CREER,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Convertit l'InputDTO en objet métier
		 * puis rattache explicitement le parent persistant.
		 */
		final Produit produit = this.convertirInputDTOEnMetier(pInputDTO);

		produit.setSousTypeProduit(parentPersistant);

		/*
		 * Délègue la création au GATEWAY.
		 * Toute anomalie technique de création
		 * est rationalisée côté UC.
		 */
		final Produit cree;

		try {

			/* Délègue la création au GATEWAY. */
			cree = this.gateway.creer(produit);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
							+ messageSecurise,
					METHODE_CREER,
					e);
		}

		/*
		 * Le GATEWAY ne doit pas conduire à un succès
		 * si aucun objet créé n'est réellement disponible.
		 * Si cree == null :
		 * émet MESSAGE_CREATION_TECHNIQUE_KO_CREER
		 * + LOG + IllegalStateException.
		 */
		if (cree == null) {
			
			return this.traiterErreur(
					MESSAGE_CREATION_TECHNIQUE_KO_CREER,
					METHODE_CREER,
					new IllegalStateException(
							MESSAGE_CREATION_TECHNIQUE_KO_CREER));
		}

		/*
		 * Prépare la réponse utilisateur finale.
		 * Le message de succès n'est positionné
		 * qu'après conversion réussie.
		 */
		final ProduitDTO.OutputDTO dto;

		try {

			/* Convertit l'objet métier créé en OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOProduit.convert(cree);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
							+ messageSecurise,
					METHODE_CREER,
					e);
		}

		/*
		 * Si dto == null :
		 * émet MESSAGE_CONVERSION_TECHNIQUE_KO_CREER
		 * + LOG + IllegalStateException.
		 */
		if (dto == null) {
			
			return this.traiterErreur(
					MESSAGE_CONVERSION_TECHNIQUE_KO_CREER,
					METHODE_CREER,
					new IllegalStateException(
							MESSAGE_CONVERSION_TECHNIQUE_KO_CREER));
		}

		/*
		 * Positionne le message observable seulement
		 * après préparation complète de la réponse.
		 */
		this.message.set(MESSAGE_CREER_OK);

		/*
		 * Retourne l'OutputDTO final.
		 */
		return dto;
	}

	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<OutputDTO> rechercherTous() throws Exception {

		/*
		 * Appelle le GATEWAY pour lire tous les Produit.
		 */
		final List<Produit> records;

		try {

			records = this.gateway.rechercherTous();

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_RECHERCHER_TOUS,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Retire les null et trie les objets métier.
		 */
		final List<Produit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/*
		 * Convertit la liste métier en OutputDTO
		 * puis dédoublonne la réponse.
		 */
		final List<OutputDTO> dtos;

		try {

			dtos = ConvertisseurMetierToOutputDTOProduit
					.convertList(recordsNonNullTries);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS,
					e);
		}

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/*
		 * Retourne toujours une liste d'OutputDTO non null
		 * (éventuellement vide).
		 */
		return dtos;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> rechercherTousString() throws Exception {

		/*
		 * Délègue d'abord la recherche exhaustive
		 * à rechercherTous().
		 */
		final List<OutputDTO> dtos = this.rechercherTous();

		/*
		 * Extrait ensuite uniquement les libellés Produit exploitables.
		 */
		final List<String> retour = new ArrayList<>();

		for (final OutputDTO dto : dtos) {
			if (dto != null && StringUtils.isNotBlank(dto.getProduit())) {
				retour.add(dto.getProduit());
			}
		}

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (retour.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/* Retourne la liste exhaustive des libellés. */
		return retour;
	}
	
	
	
	/**
	* {@inheritDoc}
	*/
	@Override
	public ResultatPage<OutputDTO> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		/*
		 * Le contrat UC refuse une requête de pagination null.
		 * Si pRequetePage == null : émet MESSAGE_PAGEABLE_NULL
		 * + LOG + IllegalStateException.
		 */
		if (pRequetePage == null) {
			
			return this.traiterErreur(
					MESSAGE_PAGEABLE_NULL,
					METHODE_RECHERCHER_TOUS_PAGE,
					new IllegalStateException(MESSAGE_PAGEABLE_NULL));
		}

		/*
		 * Délègue au GATEWAY la recherche paginée.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final ResultatPage<Produit> resultatPagine;

		try {

			/* Délègue au GATEWAY la recherche paginée. */
			resultatPagine = this.gateway.rechercherTousParPage(pRequetePage);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS_PAGE,
					e);
		}

		/*
		 * Sécurise le contrat observable du UC :
		 * une réponse paginée null du GATEWAY
		 * est une rupture technique.
		 * Si resultatPagine == null : émet MESSAGE_RECHERCHE_PAGINEE_KO
		 * + LOG + IllegalStateException.
		 */
		if (resultatPagine == null) {
			
			return this.traiterErreur(
					MESSAGE_RECHERCHE_PAGINEE_KO,
					METHODE_RECHERCHER_TOUS_PAGE,
					new IllegalStateException(MESSAGE_RECHERCHE_PAGINEE_KO));
		}

		/*
		 * Prépare la réponse paginée utilisateur complète :
		 * retrait des nulls, tri métier,
		 * conversion en OutputDTO avec dédoublonnage,
		 * puis reconstruction d'un ResultatPage cohérent.
		 */
		final ResultatPage<OutputDTO> resultatUc;

		try {

			/* Récupère la liste d'objets métier auprès du resultatPagine. */
			final List<Produit> contenus = resultatPagine.getContent();

			/* Retire les null, trie la liste d'objets métier. */
			final List<Produit> recordsNonNullTries
					= this.filtrerEtTrier(contenus);

			/* Convertit la liste d'objets métier sans null
			 * en dédoublonnant et en conservant l'ordre. */
			final List<ProduitDTO.OutputDTO> dtos
					= ConvertisseurMetierToOutputDTOProduit.convertList(
							recordsNonNullTries);

			final int numeroPage = resultatPagine.getPageNumber();
			final int pageSize = resultatPagine.getPageSize();
			final long totalElements = this.safeTotalElements(resultatPagine);

			/* Reconstruit un ResultatPage cohérent. */
			resultatUc = new ResultatPage<OutputDTO>(
					dtos,
					numeroPage,
					pageSize,
					totalElements);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS_PAGE,
					e);
		}

		/*
		 * Le message observable de succès MESSAGE_RECHERCHE_PAGINEE_OK
		 * n'est positionné qu'après préparation complète
		 * de la réponse paginée utilisateur.
		 */
		this.message.set(MESSAGE_RECHERCHE_PAGINEE_OK);

		/*
		 * Retourne toujours un ResultatPage non null
		 * lorsque le scénario se termine avec succès.
		 */
		return resultatUc;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProduitDTO.OutputDTO> findByLibelle(
			final String pLibelle) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * libellé blank => null + MESSAGE_PARAM_BLANK, sans exception.
		 */
		if (StringUtils.isBlank(pLibelle)) {
			this.message.set(MESSAGE_PARAM_BLANK);
			return null;
		}

		/*
		 * Délègue au GATEWAY la recherche exacte par libellé.
		 */
		final List<Produit> reponses = this.gateway.findByLibelle(pLibelle);

		/*
		 * Une réponse technique null du GATEWAY
		 * est une anomalie de recherche.
		 * Si reponses == null : 
		 * émet un message KO_TECHNIQUE_RECHERCHE + LOG + RuntimeException.
		 */
		if (reponses == null) {
			
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					METHODE_FIND_BY_LIBELLE,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		/*
		 * Retire les null, trie les objets métier,
		 * puis convertit la réponse en OutputDTO.
		 */
		final List<Produit> recordsNonNullTries
				= this.filtrerEtTrier(reponses);

		final List<ProduitDTO.OutputDTO> dtos
				= ConvertisseurMetierToOutputDTOProduit
						.convertList(recordsNonNullTries);

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		return dtos;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OutputDTO> findByLibelleRapide(
			final String pContenu) throws Exception {

		/*
		 * Si pContenu == null : 
		 * émet un message MESSAGE_PARAM_NULL + LOG + IllegalStateException;
		 */
		if (pContenu == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					new IllegalStateException(MESSAGE_PARAM_NULL));
		}

		/* Si pContenu blank : retourne tous les éléments 
		 * en délègant à rechercherTous().*/
		if (StringUtils.isBlank(pContenu)) {
			return this.rechercherTous();
		}

		/*
		 * Délègue au GATEWAY la recherche rapide.
		 */
		final List<Produit> reponses
			= this.gateway.findByLibelleRapide(pContenu);

		/*
		 * Une réponse technique null du GATEWAY
		 * est une anomalie de recherche.
		 * Si reponses == null : 
		 * émet un message KO_TECHNIQUE_RECHERCHE + LOG + RuntimeException
		 */
		if (reponses == null) {
			
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		/*
		 * Retire les null, trie les objets métier,
		 * puis convertit la réponse en OutputDTO.
		 */
		final List<Produit> recordsNonNullTries
			= this.filtrerEtTrier(reponses);

		final List<OutputDTO> dtos
			= ConvertisseurMetierToOutputDTOProduit
				.convertList(recordsNonNullTries);

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		return dtos;
	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OutputDTO> findAllByParent(
			final SousTypeProduitDTO.InputDTO pSousTypeProduit)
						throws Exception {
	
		/*
		 * Le parent demandé est une précondition observable.
		 * Si pSousTypeProduit == null :
		 * émet RECHERCHE_SOUSTYPEPRODUIT_NULL + LOG + RuntimeException.
		 */
		if (pSousTypeProduit == null) {
			
			return this.traiterErreur(
					RECHERCHE_SOUSTYPEPRODUIT_NULL,
					METHODE_FIND_ALL_BY_PARENT,
					new RuntimeException(RECHERCHE_SOUSTYPEPRODUIT_NULL));
		}
	
		/*
		 * Le libellé du parent ne doit pas être blank.
		 * Si blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(pSousTypeProduit.getSousTypeProduit())) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_ALL_BY_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}
	
		/*
		 * Recherche le parent persistant.
		 */
		final List<SousTypeProduit> parents
			= this.sousTypeProduitGateway
				.findByLibelle(pSousTypeProduit.getSousTypeProduit());
	
		SousTypeProduit parentPersistant = null;
	
		if (parents != null) {
			for (final SousTypeProduit parent : parents) {
				if (parent != null
						&& parent.getIdSousTypeProduit() != null) {
					parentPersistant = parent;
					break;
				}
			}
		}
	
		/*
		 * Refuse un parent absent ou non persistant.
		 * Si parent null ou non persistant : 
		 * émet un message MESSAGE_PAS_PARENT + LOG + IllegalStateException
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdSousTypeProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_ALL_BY_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}
	
		/*
		 * Délègue au GATEWAY Produit la recherche
		 * de tous les Produits rattachés à ce parent.
		 */
		final List<Produit> reponses
			= this.gateway.findAllByParent(parentPersistant);
	
		/*
		 * Une réponse technique null du GATEWAY
		 * est une anomalie de recherche.
		 * Si reponses == null : 
		 * émet un message KO_TECHNIQUE_RECHERCHE + LOG + RuntimeException
		 */
		if (reponses == null) {
			
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					METHODE_FIND_ALL_BY_PARENT,
					new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}
	
		/*
		 * Positionne le message observable
		 * puis prépare la réponse utilisateur.
		 */
		if (reponses.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}
	
		final List<OutputDTO> retours;
		
		final List<Produit> recordsNonNullTries
			= this.filtrerEtTrier(reponses);
		
		retours = ConvertisseurMetierToOutputDTOProduit
				.convertList(recordsNonNullTries);
	
		/* retourne la liste d'OutputDTO. */
		return retours;
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findByDTO(
			final InputDTO pInputDTO) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Si pInputDTO == null :
		 * Retourne null avec un message utilisateur 
		 * MESSAGE_RECHERCHE_OBJ_NULL.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
			return null;
		}

		/*
		 * Le parent SousTypeProduit est une 
		 * précondition observable du SERVICE UC.
		 * Si le type parent ou le sous-type parent est blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(pInputDTO.getTypeProduit())
				|| StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_BY_DTO,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Délègue au GATEWAY la Recherche 
		 * du parent persistant correspondant.
		 */
		final List<SousTypeProduit> parents
			= this.sousTypeProduitGateway
				.findByLibelle(pInputDTO.getSousTypeProduit());

		SousTypeProduit parentPersistant = null;

		if (parents != null) {
			for (final SousTypeProduit parent : parents) {
				if (parent != null
						&& parent.getIdSousTypeProduit() != null) {
					parentPersistant = parent;
					break;
				}
			}
		}

		/*
		 * Si aucun parent persistant n'est disponible :
		 * retourne null avec un message utilisateur MESSAGE_RECHERCHE_VIDE.
		 */
		if (parentPersistant == null) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		/*
		 * Si le libellé Produit est blank :
		 * aucune correspondance exacte n'est possible.
		 * Retourne null avec un message utilisateur MESSAGE_RECHERCHE_VIDE.
		 */
		if (StringUtils.isBlank(pInputDTO.getProduit())) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		try {

			/*
			 * Délègue au Gateway la Recherche de 
			 * tous les Produits rattachés au parent persistant.
			 */
			final List<Produit> possibles
				= this.gateway.findAllByParent(parentPersistant);

			/*
			 * Si le stockage ne retourne aucun candidat :
			 * retourne null avec un message utilisateur 
			 * MESSAGE_RECHERCHE_VIDE.
			 */
			if (possibles == null || possibles.isEmpty()) {
				this.message.set(MESSAGE_RECHERCHE_VIDE);
				return null;
			}

			/*
			 * Recherche l'objet correspondant exactement au libellé demandé.
			 */
			for (final Produit p : this.filtrerEtTrier(possibles)) {
				if (p != null
						&& Strings.CI.equals(
								p.getProduit(),
								pInputDTO.getProduit())) {
					
					/* positionne le message utilisateur 
					 * sur MESSAGE_SUCCES_RECHERCHE. */
					this.message.set(MESSAGE_SUCCES_RECHERCHE);
					
					final OutputDTO reponse;
					
					/* Convertit l'objet métier en OutputDTO. */
					reponse 
						= ConvertisseurMetierToOutputDTOProduit.convert(p);
					
					/* retourne la réponse. */
					return reponse;
				}
			}

			/*
			 * Aucun objet exact trouvé :
			 * retourne null avec un message utilisateur
			 * MESSAGE_RECHERCHE_VIDE.
			 */
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;

		} catch (final Exception e) {

			/*
			 * Si KO technique pendant la recherche : 
			 * émet un message KO_TECHNIQUE_RECHERCHE 
			 * + LOG + Exception technique
			 */
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE,
					METHODE_FIND_BY_DTO,
					e);
		}
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findById(
			final Long pId) throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Si pId == null :
		 * Retourne null avec un message utilisateur MESSAGE_PARAM_NULL.
		 */
		if (pId == null) {
			this.message.set(MESSAGE_PARAM_NULL);
			return null;
		}

		/*
		 * Délègue au GATEWAY la recherche par identifiant.
		 */
		final Produit reponse = this.gateway.findById(pId);

		/*
		 * Si l'objet est introuvable :
		 * Retourne null avec un message utilisateur
		 * MESSAGE_OBJ_INTROUVABLE + pId.
		 */
		if (reponse == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pId);
			return null;
		}

		/*
		 * Prépare la réponse utilisateur finale.
		 * Le message de succès n'est positionné
		 * qu'après conversion réussie.
		 */
		final OutputDTO dto
			= ConvertisseurMetierToOutputDTOProduit.convert(reponse);

		/*
		 * Positionne le message observable seulement
		 * après préparation complète de la réponse.
		 */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/*
		 * Retourne l'OutputDTO final.
		 */
		return dto;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO update(
			final InputDTO pInputDTO) throws Exception {

		/*
		 * Le contrat UC refuse un DTO de modification null.
		 * Si pInputDTO == null :
		 * émet MESSAGE_PARAM_NULL + LOG + ExceptionParametreNull.
		 */
		if (pInputDTO == null) {
			
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					METHODE_UPDATE,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
		}

		/*
		 * Récupère les libellés métier portés par le DTO.
		 */
		final String libelleProduit = pInputDTO.getProduit();
		final String libelleSousType = pInputDTO.getSousTypeProduit();
		final String libelleType = pInputDTO.getTypeProduit();

		/*
		 * Le contrat UC refuse un libellé Produit blank.
		 * Si libelleProduit est blank :
		 * émet MESSAGE_PARAM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelleProduit)) {
			
			return this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					METHODE_UPDATE,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
		}

		/*
		 * Le parent est une précondition observable du SERVICE UC.
		 * Si le type parent ou le sous-type parent est blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(libelleType)
				|| StringUtils.isBlank(libelleSousType)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_UPDATE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche le parent persistant correspondant au DTO.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final List<SousTypeProduit> parents;

		try {
			
			/*Délègue la recherche du parent persistant au Gateway. */
			parents 
				= this.sousTypeProduitGateway.findByLibelle(libelleSousType);
			
		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_UPDATE,
					e);
		}

		SousTypeProduit parentPersistant = null;

		/* Recherche le bon parent dans la liste des parents 
		 * possibles retournée. */
		if (parents != null) {
			for (final SousTypeProduit parent : parents) {
				if (parent != null
						&& parent.getIdSousTypeProduit() != null
						&& parent.getTypeProduit() != null
						&& Strings.CI.equals(
								parent.getSousTypeProduit(),
								libelleSousType)
						&& Strings.CI.equals(
								parent.getTypeProduit().getTypeProduit(),
								libelleType)) {
					
					parentPersistant = parent;
					break;
				}
			}
		}

		/*
		 * Si aucun parent persistant cohérent n'est retrouvé :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (parentPersistant == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_UPDATE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche tous les Produits rattachés au parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final List<Produit> records;

		try {
			
			/* délègue au GATEWAY la recherche des enfants 
			 * du parent persistant. */
			records = this.gateway.findAllByParent(parentPersistant);
			
		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Si aucun enfant n'est disponible pour ce parent :
		 * retourne null avec MESSAGE_OBJ_INTROUVABLE + libellé.
		 */
		if (records == null || records.isEmpty()) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelleProduit);
			return null;
		}

		/*
		 * Recherche dans la liste filtrée et triée
		 * l'objet correspondant au couple [parent, libellé].
		 */
		final Produit existant;

		try {
			
			/* filtre les null et trie la liste des enfants 
			 * du parent persistant. */
			final List<Produit> recordsNonNullTries 
				= this.filtrerEtTrier(records);

			Produit trouve = null;

			/* recherche le produit à modifier parmi les enfants 
			 * du parent persistant. */
			for (final Produit produit : recordsNonNullTries) {
				if (Strings.CI.equals(
						produit.getProduit(),
						libelleProduit)) {
					
					trouve = produit;
					break;
				}
			}

			existant = trouve;

		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Si aucun Produit persistant correspondant n'est trouvé :
		 * retourne null avec MESSAGE_OBJ_INTROUVABLE + libellé.
		 */
		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelleProduit);
			return null;
		}

		/*
		 * L'objet retrouvé doit être persistant avant modification.
		 * Si existant.getIdProduit() == null :
		 * émet MESSAGE_OBJ_NON_PERSISTE + libellé
		 * + LOG + ExceptionNonPersistant.
		 */
		if (existant.getIdProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_OBJ_NON_PERSISTE + libelleProduit,
					METHODE_UPDATE,
					new ExceptionNonPersistant(
							MESSAGE_OBJ_NON_PERSISTE + libelleProduit));
		}

		/*
		 * Reconstruit l'objet métier à modifier
		 * en réinjectant l'identifiant persistant retrouvé
		 * et le parent persistant.
		 */
		final Produit produitAModifier 
			= new Produit(libelleProduit, parentPersistant);
		
		produitAModifier.setIdProduit(existant.getIdProduit());

		/*
		 * Délègue la modification technique au GATEWAY.
		 * Toute anomalie technique de modification
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final Produit modifie;

		try {
			
			/* Délègue la modification technique au GATEWAY. */
			modifie = this.gateway.update(produitAModifier);
			
		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					MESSAGE_MODIF_KO + libelleProduit 
					+ TIRET_ESPACE + messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Si le GATEWAY retourne null :
		 * retourne null + MESSAGE_MODIF_KO + libellé.
		 */
		if (modifie == null) {
			this.message.set(MESSAGE_MODIF_KO + libelleProduit);
			return null;
		}

		/*
		 * L'objet retourné après modification
		 * doit rester persistant.
		 * Si modifie.getIdProduit() == null :
		 * émet MESSAGE_OBJ_NON_PERSISTE + libellé
		 * + LOG + ExceptionNonPersistant.
		 */
		if (modifie.getIdProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_OBJ_NON_PERSISTE + libelleProduit,
					METHODE_UPDATE,
					new ExceptionNonPersistant(
							MESSAGE_OBJ_NON_PERSISTE + libelleProduit));
		}

		final OutputDTO dto;

		try {
			
			/*
			 * Convertit l'objet métier modifié en OutputDTO final.
			 */
			dto = ConvertisseurMetierToOutputDTOProduit.convert(modifie);
			
		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					MESSAGE_MODIF_KO + libelleProduit 
					+ TIRET_ESPACE + messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Si dto == null :
		 * message technique + LOG + IllegalStateException
		 */
		if (dto == null) {
			
			final String messageTechnique =
					MESSAGE_MODIF_KO + libelleProduit 
					+ TIRET_ESPACE + MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					messageTechnique,
					METHODE_UPDATE,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Le message observable de succès
		 * n'est positionné qu'après préparation complète
		 * de la réponse utilisateur finale.
		 */
		this.message.set(MESSAGE_MODIF_OK + libelleProduit);

		/* Retourne l'OutputDTO modifié. */
		return dto;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(
			final InputDTO pInputDTO) throws Exception {

		/*
		 * Le contrat UC refuse un DTO de suppression null.
		 * Si pInputDTO == null :
		 * émet MESSAGE_PARAM_NULL + LOG + ExceptionParametreNull.
		 */
		if (pInputDTO == null) {
			
			this.traiterErreur(
					MESSAGE_PARAM_NULL,
					METHODE_DELETE,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
			return;
		}

		/*
		 * Extrait les libellés métier portés par le DTO.
		 */
		final String libelleProduit = pInputDTO.getProduit();
		final String libelleSousType = pInputDTO.getSousTypeProduit();
		final String libelleType = pInputDTO.getTypeProduit();

		/*
		 * Le contrat UC refuse un libellé Produit blank.
		 * Si StringUtils.isBlank(libelleProduit) :
		 * émet MESSAGE_PARAM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelleProduit)) {
			this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					METHODE_DELETE,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
			return;
		}

		/*
		 * Le parent est une précondition observable du SERVICE UC.
		 * Si le type parent ou le sous-type parent est blank :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(libelleType)
				|| StringUtils.isBlank(libelleSousType)) {
			
			this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_DELETE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
			return;
		}

		/*
		 * Recherche d'abord le parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé.
		 */
		final SousTypeProduit parentPersistant;

		try {

			/* Délègue la recherche du parent persistant
			 * à la méthode utilitaire de service. */
			parentPersistant = this.rechercherParentPersistant(pInputDTO);

		} catch (final Exception e) {

			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_DELETE,
					e);
			return;
		}

		/*
		 * Le parent doit exister et être persistant :
		 * sinon MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdSousTypeProduit() == null) {
			
			this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_DELETE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
			return;
		}

		/*
		 * Recherche ensuite tous les Produits du parent persistant
		 * afin de ré-identifier la cible exacte
		 * sur le couple [parent, libellé].
		 */
		final List<Produit> records;

		try {

			/* Délègue au GATEWAY la recherche
			 * des enfants du parent persistant. */
			records = this.gateway.findAllByParent(parentPersistant);

		} catch (final Exception e) {

			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_DELETE,
					e);
			return;
		}

		/*
		 * Un retour null du GATEWAY pendant cette ré-identification
		 * constitue une anomalie technique de stockage :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			
			this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_DELETE,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
			return;
		}

		/*
		 * Filtre les nulls, trie puis identifie la cible exacte
		 * sur le couple [parent, libellé].
		 */
		final List<Produit> recordsNonNullTries =
				this.filtrerEtTrier(records);

		Produit existant = null;

		for (final Produit candidat : recordsNonNullTries) {

			if (Strings.CI.equals(
					candidat.getProduit(),
					libelleProduit)) {

				existant = candidat;
				break;
			}
		}

		/*
		 * Si aucun objet persistant n'est retrouvé pour ce couple :
		 * ne détruit rien, retourne,
		 * et positionne MESSAGE_OBJ_INTROUVABLE + libellé.
		 */
		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelleProduit);
			return;
		}

		/*
		 * L'objet ré-identifié doit être persistant :
		 * sinon émet MESSAGE_OBJ_NON_PERSISTE + libellé
		 * + LOG + ExceptionNonPersistant.
		 */
		if (existant.getIdProduit() == null) {
			final String messageUtil =
					MESSAGE_OBJ_NON_PERSISTE + libelleProduit;

			this.traiterErreur(
					messageUtil,
					METHODE_DELETE,
					new ExceptionNonPersistant(messageUtil));
			return;
		}

		/*
		 * Délègue ensuite la destruction au GATEWAY.
		 * Toute anomalie technique de destruction
		 * est transformée en message utilisateur cohérent.
		 */
		try {
			
			/* Délègue ensuite la destruction au GATEWAY. */
			this.gateway.delete(existant);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			this.traiterErreur(
					MESSAGE_DELETE_KO
							+ libelleProduit
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_DELETE,
					e);
			return;
		}

		/*
		 * Le message observable de succès
		 * n'est positionné qu'après destruction effective
		 * de l'objet persistant.
		 */
		this.message.set(MESSAGE_DELETE_OK + libelleProduit);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long count() throws Exception {

		/*
		 * Délègue le comptage au GATEWAY.
		 * Toute anomalie technique est transformée
		 * en message utilisateur technique cohérent.
		 */
		final long resultat;

		try {
			
			/*
			 * Délègue au GATEWAY le comptage exact
			 * du nombre d'objets présents en stockage.
			 */
			resultat = this.gateway.count();
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_COUNT,
					e);
		}

		/*
		 * Un comptage strictement négatif est incohérent
		 * pour un résultat observable côté UC.
		 * Si resultat < 0L :
		 * émet un message technique
		 * + LOG + IllegalStateException.
		 */
		if (resultat < 0L) {
			
			final String messageTechnique =
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ "comptage négatif incohérent : "
							+ resultat;

			return this.traiterErreur(
					messageTechnique,
					METHODE_COUNT,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Positionne le message observable
		 * seulement après récupération effective du comptage.
		 * 0 -> MESSAGE_RECHERCHE_VIDE
		 * > 0 -> MESSAGE_RECHERCHE_OK
		 */
		if (resultat == 0L) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/* Retourne le comptage final validé. */
		return resultat;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {

		/*
		 * Retourne en lecture pure
		 * le dernier message observable
		 * actuellement mémorisé par le SERVICE UC.
		 * Cette méthode ne délègue à aucun GATEWAY,
		 * ne déclenche aucun recalcul,
		 * n'émet aucun LOG
		 * et ne modifie pas l'état courant.
		 * Un retour null est acceptable
		 * tant qu'aucune opération précédente
		 * n'a encore produit de message.
		 */
		return this.message.get();

	}
	
	
	
	// *************************** METHODES UTILITAIRES ********************/


	
	/**
	 * <div>
	 * <p>Détermine si un {@link ProduitDTO.InputDTO}
	 * correspond à un doublon fonctionnel.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return boolean : true si doublon, false sinon.
	 * @throws Exception
	 */
	private boolean isDoublon(
			final ProduitDTO.InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null) {
			return false;
		}

		final String libelleProduitRecherche = pInputDTO.getProduit();
		final String libelleSousTypeRecherche = pInputDTO.getSousTypeProduit();
		final String libelleTypeRecherche = pInputDTO.getTypeProduit();

		if (StringUtils.isBlank(libelleSousTypeRecherche)) {
			return false;
		}

		final List<Produit> produitsExistants
				= this.gateway.findByLibelle(libelleProduitRecherche);

		if (produitsExistants == null || produitsExistants.isEmpty()) {
			return false;
		}

		for (final Produit existant : produitsExistants) {

			if (existant == null) {
				continue;
			}

			final String produitExistant = existant.getProduit();
			final SousTypeProduitI sousTypeExistantObjet = existant.getSousTypeProduit();
			final String sousTypeExistant = sousTypeExistantObjet != null
					? sousTypeExistantObjet.getSousTypeProduit()
					: null;
			final String typeExistant = sousTypeExistantObjet != null
					&& sousTypeExistantObjet.getTypeProduit() != null
							? sousTypeExistantObjet.getTypeProduit().getTypeProduit()
							: null;

			final boolean typeCompatible = StringUtils.isBlank(libelleTypeRecherche)
					|| Strings.CI.equals(libelleTypeRecherche, typeExistant);

			if (Strings.CI.equals(libelleProduitRecherche, produitExistant)
					&& Strings.CI.equals(libelleSousTypeRecherche, sousTypeExistant)
					&& typeCompatible) {
				return true;
			}
		}

		return false;
	}

	
	
	/**
	 * <div>
	 * <p>Recherche le {@link SousTypeProduit} parent persistant
	 * correspondant au DTO d'entrée.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return SousTypeProduit : parent persistant ou null.
	 * @throws Exception
	 */
	private SousTypeProduit rechercherParentPersistant(
			final ProduitDTO.InputDTO pInputDTO) throws Exception {

		if (pInputDTO == null
				|| StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return null;
		}

		final String libelleSousTypeRecherche = pInputDTO.getSousTypeProduit();
		final String libelleTypeRecherche = pInputDTO.getTypeProduit();

		final List<SousTypeProduit> parents
				= this.sousTypeProduitGateway.findByLibelle(libelleSousTypeRecherche);

		if (parents == null || parents.isEmpty()) {
			return null;
		}

		for (final SousTypeProduit parent : parents) {

			if (parent == null) {
				continue;
			}

			if (parent.getIdSousTypeProduit() == null) {
				continue;
			}

			final String libelleSousType = parent.getSousTypeProduit();
			final String libelleType = parent.getTypeProduit() != null
					? parent.getTypeProduit().getTypeProduit()
					: null;

			final boolean typeCompatible = StringUtils.isBlank(libelleTypeRecherche)
					|| Strings.CI.equals(libelleTypeRecherche, libelleType);

			if (Strings.CI.equals(libelleSousTypeRecherche, libelleSousType)
					&& typeCompatible) {
				return parent;
			}
		}

		return null;
	}

	
	
	/**
	 * <div>
	 * <p>Convertit un InputDTO en objet métier {@link Produit}.</p>
	 * </div>
	 *
	 * @param pInputDTO : ProduitDTO.InputDTO
	 * @return Produit : objet métier (ou null si pInputDTO == null)
	 */
	private Produit convertirInputDTOEnMetier(
			final ProduitDTO.InputDTO pInputDTO) {

		if (pInputDTO == null) {
			return null;
		}

		return new Produit(pInputDTO.getProduit());
	}

	
	
	/**
	 * <div>
	 * <p>Filtre les nulls, puis trie 
	 * par libellé produit (case-insensitive).</p>
	 * </div>
	 *
	 * @param pListe liste potentiellement null
	 * @return liste non nulle filtrée/triée
	 */
	private List<Produit> filtrerEtTrier(
			final List<Produit> pListe) {

		final List<Produit> resultat = new ArrayList<Produit>();

		if (pListe == null) {
			return resultat;
		}

		for (final Produit p : pListe) {
			if (p != null) {
				resultat.add(p);
			}
		}

		resultat.sort(new Comparator<Produit>() {
			@Override
			public int compare(final Produit o1, final Produit o2) {
				final String s1 = (o1 != null) ? o1.getProduit() : null;
				final String s2 = (o2 != null) ? o2.getProduit() : null;
				return Strings.CI.compare(s1, s2);
			}
		});

		return resultat;
	}

	
	
	/**
	 * <div>
	 * <p>Récupère un totalElements sûr depuis un ResultatPage.</p>
	 * </div>
	 *
	 * @param rp resultat page
	 * @return totalElements >= 0
	 */
	private long safeTotalElements(
			final ResultatPage<?> rp) {

		if (rp == null) {
			return 0L;
		}

		final long total = rp.getTotalElements();

		if (total < 0L) {
			return 0L;
		}

		return total;
	}

	
	
	/**
	 * <div>
	 * <p>Extrait un libellé parent (SousTypeProduit) 
	 * pour les messages de modification.</p>
	 * </div>
	 *
	 * @param pProduit produit
	 * @return libellé parent ou "?"
	 */
	private String safeParentLibelle(
			final Produit pProduit) {

		if (pProduit == null || pProduit.getSousTypeProduit() == null) {
			return "?";
		}

		final SousTypeProduitI stp = pProduit.getSousTypeProduit();

		if (StringUtils.isBlank(stp.getSousTypeProduit())) {
			return "?";
		}

		return stp.getSousTypeProduit();
	}

	
	
	/**
	 * <div>
	 * <p>Centralise le traitement des erreurs :
	 * <ul>
	 * <li>alimente le message utilisateur ; </li>
	 * <li>ajoute le nom de la méthode appelante au message de l'utilisateur 
	 * pour former le message visible dans les logs ; </li>
	 * <li>journalise selon la nature de l'erreur ; </li>
	 * <li>propage l'exception initale.</p>
	 * </ul>
	 * <p>
	 * Les erreurs métier attendues sont journalisées
	 * sans pile d'exception afin d'éviter
	 * un bruit inutile dans les tests verts (OK)
	 * et dans les logs de build CI.
	 * </p>
	 * </div>
	 *
	 * @param pMessage : String : message d'erreur.
	 * @param pMethode : String : nom de la méthode appelante.
	 * @param pE : Exception : exception typée transmise 
	 * par la méthode appelante à jeter.
	 * @param <T> : type de retour : pour chaîner dans les méthodes 
	 * retournant une valeur.
	 * @return T : jamais atteint.
	 * @throws Exception
	 */
	private <T> T traiterErreur(
			final String pMessage,
			final String pMethode,
			final Exception pE) throws Exception {

		final String messageFinal
			= (pMessage != null) ? pMessage : MSG_ERREUR_NON_SPECIFIEE;

		this.message.set(messageFinal);

		final String messagePourLog = pMethode + TIRET_ESPACE + messageFinal;
		/*
		 * Une erreur métier attendue ne nécessite pas
		 * de pile complète dans les logs :
		 * un DEBUG avec le message suffit.
		 */
		if (this.isErreurMetierAttendue(messageFinal, pE)) {
			if (LOG.isDebugEnabled()) {
				
				LOG.debug(messagePourLog);
			}
		} else if (LOG.isErrorEnabled()) {
			/*
			 * Une erreur technique ou inattendue
			 * conserve un LOG.error avec stacktrace.
			 */
			LOG.error(messagePourLog, pE);
		}

		if (pE != null) {
			throw pE;
		}

		throw new Exception(messageFinal);

	}
	
	

	/**
	 * <div>
	 * <p>Détermine si l'erreur correspond à un scénario métier attendu
	 * pour lequel une pile d'exception complète
	 * n'apporte rien dans les logs.</p>
	 * </div>
	 *
	 * @param pMessage : String :
	 * le message utilisateur courant.
	 * @param pException : Exception :
	 * l'exception sur le point d'être relancée.
	 * @return boolean :
	 * true si l'erreur est attendue côté métier.
	 */
	private boolean isErreurMetierAttendue(
			final String pMessage,
			final Exception pException) {
	
		if (pException == null) {
			return false;
		}
	
		/*
		 * Les exceptions métier explicites du SERVICE UC
		 * ne nécessitent pas de stacktrace complète.
		 */
		if (pException instanceof ExceptionParametreBlank
				|| pException instanceof ExceptionParametreNull
				|| pException instanceof ExceptionDoublon
				|| pException instanceof ExceptionNonPersistant) {
			return true;
		}
	
		/*
		 * Certains IllegalStateException relèvent aussi
		 * d'une précondition métier attendue.
		 */
		if (pException instanceof IllegalStateException) {
			return Strings.CI.equals(pMessage, MESSAGE_PAS_PARENT)
					|| Strings.CI.equals(pMessage, MESSAGE_PARAM_NULL)
					|| Strings.CI.equals(pMessage, MESSAGE_PAGEABLE_NULL);
		}
	
		/*
		 * Le parent null dans findAllByParent(...)
		 * est un scénario attendu du contrat de service.
		 */
		if (pException instanceof RuntimeException) {
			return Strings.CI.equals(
					pMessage,
					RECHERCHE_SOUSTYPEPRODUIT_NULL);
		}
	
		return false;
	}

	
	
}
