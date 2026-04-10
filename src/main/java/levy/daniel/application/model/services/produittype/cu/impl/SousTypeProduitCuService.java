/* ********************************************************************* */
/* ******************** ADAPTER SERVICE CU ***************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.cu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOSousTypeProduit;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduitI;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitCuService.java :
 * </p>
 *
 * <p>
 * Cette classe modélise :
 * le <span style="font-weight:bold;">
 * SERVICE METIER (Use Case) ADAPTER</span>
 * pour l'objet métier <code style="font-weight:bold;">
 * {@link SousTypeProduit}</code>.
 * </p>
 *
 * <p style="font-weight:bold;">SERVICE USE CASE
 * </p>
 * <p>Cette classe <span style="font-weight:bold;">
 * SERVICE METIER (Use Case)</span> ne connait que l'INTERFACE
 * TECHNIQUE GATEWAY qui est injectée par SPRING via le Constructeur.</p>
 * <p>Cette classe <span style="font-weight:bold;">
 * SERVICE METIER (Use Case)</span> ne connait
 * <span style="font-weight:bold;">pas</span> par exemple
 * le DAO JPA <code style="font-weight:bold;">
 * {@link SousTypeProduitDaoJPA}</code>
 * de la classe TECHNIQUE concrète <code style="font-weight:bold;">
 * {@link SousTypeProduitGatewayJPAService}</code> 
 * qui implémente l'interface
 * de SERVICE TECHNIQUE <code style="font-weight:bold;">
 * {@link SousTypeProduitGatewayIService}</code>.
 * </p>
 *
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
 * @since 29 décembre 2025
 */
@Service(value = "SousTypeProduitCuService")
@Profile({ "desktop", "dev", "prod" })
public class SousTypeProduitCuService implements SousTypeProduitICuService {

	// *************************** ATTRIBUTS ******************************/
	/**
	 * <div>
	 * <p style="font-weight:bold;"> 
	 * INTERFACE SERVICE TECHNIQUE GATEWAY JPA 
	 * pour les {@link TypeProduit}</p>
	 * <ul>
	 * <li>ne connaissant pas l'implémentation technique du stockage.</li>
	 * <li>implémentée par des classes concrètes connaissant
	 * la technique de stockage.</li>
	 * <li style="font-weight:bold;">
	 * nécessaire pour rattacher le parent persistant.</li>
	 * </ul>
	 * </div> 
	 * */
	private final TypeProduitGatewayIService typeProduitGateway;


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * INTERFACE SERVICE TECHNIQUE GATEWAY JPA 
	 * pour les {@link SousTypeProduit}</p>
	 * <ul>
	 * <li>ne connaissant pas l'implémentation technique du stockage.</li>
	 * <li>implémentée par des classes concrètes connaissant
	 * la technique de stockage.</li>
	 * </ul>
	 * </div>
	 */
	private final SousTypeProduitGatewayIService gateway;

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * message à l'attention de l'Utilisateur
	 * généré en cas de problème lors des opérations de stockage.</p>
	 * <p>ThreadLocal pour garantir le bon fonctionnement 
	 * même en cas d'application multithreadée avec SPRING.</p>
	 * </div>
	 */
	private final ThreadLocal<String> message = new ThreadLocal<>();

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
	private static final Logger LOG
		= LogManager.getLogger(SousTypeProduitCuService.class);

	// ************************* METHODES **********************************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">CONSTRUCTEUR COMPLET</p>
	 * <p>Indispensable pour l'injection par SPRING
	 * du SousTypeProduitGatewayIService via le constructeur.</p>
	 * <p>ATTENTION : Ne surtout pas créer de Constructeur d'arité nulle
	 * dans cette classe, faute de quoi SPRING ne pourra plus injecter.</p>
	 * </div>
	 *
	 * @param pGateway : SousTypeProduitGatewayIService :
	 * Interface de SERVICE TECHNIQUE JPA chargé de la communication
	 * avec le stockage pour les {@link SousTypeProduit}.
	 * @param pTypeProduitGateway : TypeProduitGatewayIService :
	 * Interface de SERVICE TECHNIQUE JPA chargé de la communication
	 * avec le stockage pour les {@link TypeProduit}. 
	 */
	public SousTypeProduitCuService(
			final SousTypeProduitGatewayIService pGateway,
			final TypeProduitGatewayIService pTypeProduitGateway) {

		super();
		this.gateway = pGateway;
		this.typeProduitGateway = pTypeProduitGateway;

	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public SousTypeProduitDTO.OutputDTO creer(
			final SousTypeProduitDTO.InputDTO pInputDTO)
			throws Exception {

		/*
		 * Erreur utilisateur bénigne :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Si pInputDTO == null : 
		 * Retourne null avec un message utilisateur MESSAGE_CREER_NULL
		 * .
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/*
		 * Récupère le libellé métier du SousTypeProduit.
		 */
		final String libelle = pInputDTO.getSousTypeProduit();

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
		 * Le parent TypeProduit est une 
		 * précondition observable du SERVICE UC.
		 * Si son libellé est blank : 
		 * émet un message MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		final String libelleParent = pInputDTO.getTypeProduit();

		if (StringUtils.isBlank(libelleParent)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_CREER,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Récupère le parent persistant.
		 * Toute anomalie technique de recherche du parent
		 * est rationalisée côté UC.
		 */
		final TypeProduit parentPersistant;

		try {

			/* Délègue au GATEWAY la récupération du parent. */
			parentPersistant 
				= this.typeProduitGateway.findByLibelle(libelleParent);

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
				|| parentPersistant.getIdTypeProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_CREER,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Convertit l'InputDTO en objet métier
		 * puis rattache explicitement le parent persistant.
		 */
		final SousTypeProduit sousTypeProduit
				= this.convertirInputDTOEnMetier(pInputDTO);

		sousTypeProduit.setTypeProduit(parentPersistant);

		/*
		 * Délègue la création au GATEWAY.
		 * Toute anomalie technique de création
		 * est rationalisée côté UC.
		 */
		final SousTypeProduit cree;

		try {

			/* Délègue la création au GATEWAY. */
			cree = this.gateway.creer(sousTypeProduit);

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
		 * si cree == null : 
		 * émet un message MESSAGE_CREATION_TECHNIQUE_KO_CREER 
		 * + LOG + IllegalStateException
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
		final SousTypeProduitDTO.OutputDTO dto;

		try {

			/* Convertit l'objet persistant cree en OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOSousTypeProduit.convert(cree);

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

		/* Si dto == null : 
		 * émet un message MESSAGE_CONVERSION_TECHNIQUE_KO_CREER 
		 * + LOG + IllegalStateException. */
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
	public List<SousTypeProduitDTO.OutputDTO> rechercherTous()
									throws Exception {

		/*
		 * Appelle le GATEWAY pour lire tous les SousTypeProduit.
		 */
		final List<SousTypeProduit> records;

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
		final List<SousTypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/*
		 * Convertit la liste métier en OutputDTO
		 * puis dédoublonne la réponse.
		 */
		final List<SousTypeProduitDTO.OutputDTO> dtos;

		try {

			dtos = this.convertirEtDedoublonner(recordsNonNullTries);

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
		 * Appelle le GATEWAY pour lire tous les SousTypeProduit.
		 */
		final List<SousTypeProduit> records;

		try {

			records = this.gateway.rechercherTous();

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS_STRING,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_RECHERCHER_TOUS_STRING,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Prépare la liste finale des libellés :
		 * retrait des null, tri métier,
		 * extraction des libellés non blank,
		 * puis dédoublonnage en conservant l'ordre.
		 */
		final List<String> libelles;

		try {

			final List<SousTypeProduit> recordsNonNullTries
				= this.filtrerEtTrier(records);

			final Set<String> uniques = new LinkedHashSet<String>();

			for (final SousTypeProduit stp : recordsNonNullTries) {

				/*
				 * recordsNonNullTries ne contient pas de null
				 * après filtrage préalable.
				 */
				final String libelle = stp.getSousTypeProduit();

				/*
				 * N'ajoute que les libellés réellement exploitables
				 * pour la couche appelante.
				 */
				if (!StringUtils.isBlank(libelle)) {
					uniques.add(libelle);
				}
			}

			libelles = new ArrayList<String>(uniques);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_RECHERCHER_TOUS_STRING,
					e);
		}

		/*
		 * Positionne le message observable
		 * après préparation complète de la réponse.
		 */
		if (libelles.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/*
		 * Retourne toujours une liste de libellés non null
		 * et éventuellement vide.
		 */
		return libelles;
	}
	


	/**
	* {@inheritDoc}
	*/
	@Override
	public ResultatPage<OutputDTO> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		/*
		 * Si pRequetePage == null :
		 * émet MESSAGE_PAGEABLE_NULL + LOG + IllegalStateException.
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
		final ResultatPage<SousTypeProduit> resultatPagine;

		try {

			/* Délègue au GATEWAY la recherche paginée. */
			resultatPagine
				= this.gateway.rechercherTousParPage(pRequetePage);

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
		 * Si resultatPagine == null :
		 * émet MESSAGE_RECHERCHE_PAGINEE_KO + LOG + IllegalStateException.
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
			final List<SousTypeProduit> contenus = resultatPagine.getContent();

			/* Retire les null, trie la liste d'objets métier. */
			final List<SousTypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(contenus);

			/* Convertit la liste d'objets métier sans null
			 * en dédoublonnant et en conservant l'ordre. */
			final List<SousTypeProduitDTO.OutputDTO> dtos
					= this.convertirEtDedoublonner(recordsNonNullTries);

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
	public List<OutputDTO> findByLibelle(
			final String pLibelle) throws Exception {

		/*
		 * Si le libellé transmis n'est pas exploitable :
		 * retourne une liste vide avec un message observable,
		 * sans LOG et sans exception.
		 * Si StringUtils.isBlank(pLibelle) : 
		 * émet un message MESSAGE_PARAM_BLANK et 
		 * retourne une nouvelle ArrayList vide.
		 */
		if (StringUtils.isBlank(pLibelle)) {
			this.message.set(MESSAGE_PARAM_BLANK);
			return new ArrayList<OutputDTO>();
		}

		/*
		 * Délègue au GATEWAY la recherche exacte
		 * de tous les SousTypeProduit portant ce libellé.
		 */
		final List<SousTypeProduit> records;

		try {

			records = this.gateway.findByLibelle(pLibelle);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_BY_LIBELLE,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_FIND_BY_LIBELLE,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Prépare la réponse utilisateur :
		 * retrait des null, tri métier,
		 * conversion en OutputDTO,
		 * puis dédoublonnage en conservant l'ordre.
		 */
		final List<OutputDTO> dtos;

		try {

			final List<SousTypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(records);

			dtos = this.convertirEtDedoublonner(recordsNonNullTries);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_BY_LIBELLE,
					e);
		}

		/*
		 * Si aucun résultat exploitable n'est trouvé :
		 * retourne une liste vide et 
		 * émet un MESSAGE_OBJ_INTROUVABLE + libellé.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pLibelle);
			return dtos;
		}

		/*
		 * Positionne le message observable de succès
		 * après préparation complète de la réponse.
		 */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/*
		 * Retourne toujours une liste non null.
		 */
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
		 * émet MESSAGE_PARAM_NULL + LOG + IllegalStateException.
		 */
		if (pContenu == null) {
			
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					new IllegalStateException(MESSAGE_PARAM_NULL));
		}

		/*
		 * Si pContenu est blank :
		 * délègue au scénario complet de rechercherTous().
		 */
		if (StringUtils.isBlank(pContenu)) {
			return this.rechercherTous();
		}

		/*
		 * Délègue au GATEWAY la recherche rapide dans le stockage.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final List<SousTypeProduit> records;

		try {

			/* Délègue au GATEWAY la recherche rapide dans le stockage. */
			records = this.gateway.findByLibelleRapide(pContenu);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Prépare la réponse utilisateur complète :
		 * retrait des nulls, tri métier,
		 * puis conversion en OutputDTO avec dédoublonnage.
		 */
		final List<OutputDTO> dtos;

		try {

			/* Filtre les null et trie la réponse du stockage. */
			final List<SousTypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(records);

			/* Dédoublonne, conserve l'ordre
			 * et convertit en OutputDTO. */
			dtos = this.convertirEtDedoublonner(recordsNonNullTries);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_BY_LIBELLE_RAPIDE,
					e);
		}

		/*
		 * Le message observable n'est positionné
		 * qu'après préparation complète de la réponse utilisateur.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/*
		 * Retourne toujours une liste d'OutputDTO non null
		 * et éventuellement vide.
		 */
		return dtos;
	}
	


	/**
	* {@inheritDoc}
	*/
	@Override
	public List<SousTypeProduitDTO.OutputDTO> findAllByParent(
			final TypeProduitDTO.InputDTO pTypeProduit)
			throws Exception {

		/*
		 * Si pTypeProduit == null :
		 * émet RECHERCHE_TYPEPRODUIT_NULL + LOG + IllegalStateException.
		 */
		if (pTypeProduit == null) {
			
			return this.traiterErreur(
					RECHERCHE_TYPEPRODUIT_NULL,
					METHODE_FIND_ALL_BY_PARENT,
					new IllegalStateException(RECHERCHE_TYPEPRODUIT_NULL));
		}

		/*
		 * Si le libellé du parent n'est pas exploitable :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		final String libelleParent = pTypeProduit.getTypeProduit();

		if (StringUtils.isBlank(libelleParent)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_ALL_BY_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche le parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final TypeProduit parentPersistant;

		try {

			/* Délègue au GATEWAY la recherche du parent persisté. */
			parentPersistant 
				= this.typeProduitGateway.findByLibelle(libelleParent);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_ALL_BY_PARENT,
					e);
		}

		/*
		 * Si le parent est absent du stockage
		 * ou non persistant :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdTypeProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_ALL_BY_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche tous les SousTypeProduit rattachés au parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final List<SousTypeProduit> records;

		try {

			/* Délègue au GATEWAY la recherche des enfants persistants. */
			records = this.gateway.findAllByParent(parentPersistant);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_ALL_BY_PARENT,
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_FIND_ALL_BY_PARENT,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Prépare la réponse utilisateur complète :
		 * retrait des nulls, tri métier,
		 * puis conversion en OutputDTO avec dédoublonnage.
		 */
		final List<SousTypeProduitDTO.OutputDTO> dtos;

		try {

			/* filtre les null et trie. */
			final List<SousTypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(records);

			/* convertit et dédoublonne. */
			dtos = this.convertirEtDedoublonner(recordsNonNullTries);

		} catch (final Exception e) {

			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					METHODE_FIND_ALL_BY_PARENT,
					e);
		}

		/*
		 * Le message observable n'est positionné
		 * qu'après préparation complète de la réponse utilisateur.
		 */
		if (dtos.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			this.message.set(MESSAGE_RECHERCHE_OK);
		}

		/*
		 * Retourne toujours une liste d'OutputDTO non null
		 * et éventuellement vide.
		 */
		return dtos;
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception {

		/*
		 * Si pInputDTO == null :
		 * retourne null avec un message utilisateur 
		 * MESSAGE_RECHERCHE_OBJ_NULL, sans LOG et sans exception.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
			return null;
		}

		/*
		 * Si le parent n'est pas exploitable :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		final String libelleParent = pInputDTO.getTypeProduit();

		if (StringUtils.isBlank(libelleParent)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_FIND_BY_DTO,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche le parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final TypeProduit parentPersistant;

		try {
			
			/* Délègue au GATEWAY la recherche du parent par libellé. */
			parentPersistant 
				= this.typeProduitGateway.findByLibelle(libelleParent);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_DTO,
					e);
		}

		/*
		 * Si le parent est absent du stockage
		 * ou non persistant :
		 * retourne null avec MESSAGE_RECHERCHE_VIDE.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdTypeProduit() == null) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		/*
		 * Recherche les SousTypeProduit rattachés au parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final List<SousTypeProduit> records;

		try {
			
			/* Délègue au Gateway la recherche de la 
			 * collection d'enfants du parent. */
			records = this.gateway.findAllByParent(parentPersistant);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_DTO,
					e);
		}

		/*
		 * Si aucun enfant n'est disponible pour ce parent :
		 * retourne null avec MESSAGE_RECHERCHE_VIDE.
		 */
		if (records == null || records.isEmpty()) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		/*
		 * Recherche dans la liste filtrée et triée
		 * l'enfant correspondant au couple [parent, libellé].
		 */
		final String libelleSousType = pInputDTO.getSousTypeProduit();
		final SousTypeProduit resultat;

		try {
			
			final List<SousTypeProduit> recordsNonNullTries =
					this.filtrerEtTrier(records);

			SousTypeProduit trouve = null;

			for (final SousTypeProduit sousTypeProduit : recordsNonNullTries) {
				if (Strings.CI.equals(
						sousTypeProduit.getSousTypeProduit(),
						libelleSousType)) {
					trouve = sousTypeProduit;
					break;
				}
			}

			resultat = trouve;

		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_DTO,
					e);
		}

		/*
		 * Si aucun enfant correspondant n'est trouvé :
		 * retourne null avec MESSAGE_RECHERCHE_VIDE.
		 */
		if (resultat == null) {
			this.message.set(MESSAGE_RECHERCHE_VIDE);
			return null;
		}

		/*
		 * Prépare la réponse utilisateur finale.
		 * Toute anomalie technique de conversion
		 * est transformée en message utilisateur rationalisé côté UC.
		 */
		final OutputDTO dto;

		try {
			
			/* convertit l'objet trouvé en OutputDTO. */
			dto 
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(resultat);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_DTO,
					e);
		}

		/*
		 * Si la conversion finale retourne null :
		 * émet un message technique cohérent + LOG + IllegalStateException.
		 */
		if (dto == null) {
			final String messageTechnique =
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					messageTechnique,
					METHODE_FIND_BY_DTO,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Le message observable de succès
		 * n'est positionné qu'après préparation complète
		 * de la réponse utilisateur.
		 */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/*
		 * Retourne l'OutputDTO correspondant
		 * au couple [parent, libellé].
		 */
		return dto;
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutputDTO findById(final Long pId) throws Exception {

		/*
		 * Retourne null avec un message observable
		 * si l'identifiant transmis n'est pas exploitable.
		 * Si pId == null :
		 * émet MESSAGE_PARAM_NULL et retourne null.
		 */
		if (pId == null) {
			this.message.set(MESSAGE_PARAM_NULL);
			return null;
		}

		/*
		 * Délègue au GATEWAY la recherche par identifiant persistant.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final SousTypeProduit sousTypeProduit;

		try {
			
			/* Délègue au GATEWAY la recherche exacte par identifiant. */
			sousTypeProduit = this.gateway.findById(pId);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_ID,
					e);
		}

		/*
		 * Retourne null avec un message observable
		 * si aucun objet n'est trouvé en stockage.
		 * Si sousTypeProduit == null :
		 * émet MESSAGE_OBJ_INTROUVABLE + pId
		 * et retourne null.
		 */
		if (sousTypeProduit == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pId);
			return null;
		}

		/*
		 * Prépare la réponse utilisateur finale
		 * à partir de l'objet métier trouvé.
		 */
		final SousTypeProduitDTO.OutputDTO dto;

		try {
			
			/* Convertit l'objet métier retourné par le GATEWAY en OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOSousTypeProduit.convert(
					sousTypeProduit);
			
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_FIND_BY_ID,
					e);
		}

		/*
		 * Sécurise le contrat observable du UC :
		 * un DTO null après conversion est une rupture technique.
		 * Si dto == null :
		 * émet un message + LOG + IllegalStateException.
		 */
		if (dto == null) {
			
			final String messageTechnique =
					KO_TECHNIQUE_RECHERCHE
							+ TIRET_ESPACE
							+ MSG_ERREUR_NON_SPECIFIEE;

			return this.traiterErreur(
					messageTechnique,
					METHODE_FIND_BY_ID,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Le message de succès MESSAGE_SUCCES_RECHERCHE
		 * n'est positionné qu'après préparation complète
		 * de la réponse utilisateur.
		 */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* Retourne l'OutputDTO résultat. */
		return dto;
	}
	


	/**
	 * {@inheritDoc}
	 */
	@Override
	public SousTypeProduitDTO.OutputDTO update(
			final SousTypeProduitDTO.InputDTO pInputDTO)
			throws Exception {

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
		 * Extrait les libellés métier portés par le DTO.
		 */
		final String libelleParent = pInputDTO.getTypeProduit();
		final String libelleSousType = pInputDTO.getSousTypeProduit();

		/*
		 * Le contrat UC refuse un libellé enfant blank.
		 * Si StringUtils.isBlank(libelleSousType) :
		 * émet MESSAGE_PARAM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelleSousType)) {
			
			return this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					METHODE_UPDATE,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
		}

		/*
		 * Le contrat UC refuse un parent blank.
		 * Si StringUtils.isBlank(libelleParent) :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(libelleParent)) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_UPDATE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche d'abord le parent persistant.
		 * Toute anomalie technique de recherche
		 * est transformée en message utilisateur rationalisé.
		 */
		final TypeProduit parentPersistant;

		try {

			/* Délègue au GATEWAY la recherche du parent persistant. */
			parentPersistant 
				= this.typeProduitGateway.findByLibelle(libelleParent);

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
		 * Le parent doit exister et être persistant.
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdTypeProduit() == null) {
			
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_UPDATE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		/*
		 * Recherche ensuite tous les enfants du parent persistant,
		 * afin de ré-identifier la cible exacte sur le couple
		 * [parent, libellé].
		 */
		final List<SousTypeProduit> records;

		try {

			/* Délègue au GATEWAY la recherche 
			 * des enfants du parent persistant. */
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
		 * Un retour null du GATEWAY pendant cette ré-identification
		 * constitue une anomalie technique de stockage.
		 */
		if (records == null) {
			
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					METHODE_UPDATE,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Filtre les nulls, trie puis identifie la cible exacte
		 * sur le couple [parent, libellé].
		 */
		final List<SousTypeProduit> recordsNonNullTries =
				this.filtrerEtTrier(records);

		SousTypeProduit existant = null;

		for (final SousTypeProduit candidat : recordsNonNullTries) {
			
			if (Strings.CI.equals(
					candidat.getSousTypeProduit(),
					libelleSousType)) {
				existant = candidat;
				break;
			}
		}

		/*
		 * Si aucun objet persistant n'est retrouvé pour ce couple :
		 * retourne null + MESSAGE_OBJ_INTROUVABLE + libellé.
		 */
		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelleSousType);
			return null;
		}

		/*
		 * L'objet ré-identifié doit être persistant.
		 * Si existant.getIdSousTypeProduit() == null :
		 * émet un message MESSAGE_OBJ_NON_PERSISTE + libelleSousType
		 * + LOG + ExceptionNonPersistant.
		 */
		if (existant.getIdSousTypeProduit() == null) {
			
			final String messageUtil =
					MESSAGE_OBJ_NON_PERSISTE + libelleSousType;
			
			return this.traiterErreur(
					messageUtil,
					METHODE_UPDATE,
					new ExceptionNonPersistant(messageUtil));
		}

		/*
		 * Reconstruit l'objet métier à partir du DTO,
		 * puis réinjecte l'ID persistant retrouvé
		 * et le parent persistant exact.
		 */
		final SousTypeProduit stp =
				this.convertirInputDTOEnMetier(pInputDTO);

		stp.setTypeProduit(parentPersistant);
		stp.setIdSousTypeProduit(existant.getIdSousTypeProduit());

		/*
		 * Délègue ensuite la modification au GATEWAY.
		 * Toute anomalie technique est transformée
		 * en message utilisateur cohérent.
		 */
		final SousTypeProduit modifie;

		try {

			/* Délègue ensuite la modification au GATEWAY. */
			modifie = this.gateway.update(stp);

		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			
			return this.traiterErreur(
					MESSAGE_MODIF_KO
							+ libelleSousType
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Si le GATEWAY retourne null :
		 * retourne null + MESSAGE_MODIF_KO + libellé.
		 */
		if (modifie == null) {
			this.message.set(MESSAGE_MODIF_KO + libelleSousType);
			return null;
		}

		/*
		 * L'objet retourné après modification
		 * doit rester persistant.
		 * Si modifie.getIdSousTypeProduit() == null : 
		 * émet un message MESSAGE_OBJ_NON_PERSISTE + libelleSousType 
		 * + LOG + IllegalStateException
		 */
		if (modifie.getIdSousTypeProduit() == null) {
			
			final String messageTechnique =
					MESSAGE_OBJ_NON_PERSISTE + libelleSousType;
			
			return this.traiterErreur(
					messageTechnique,
					METHODE_UPDATE,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Prépare la réponse utilisateur finale
		 * à partir de l'objet métier modifié.
		 */
		final SousTypeProduitDTO.OutputDTO dto;

		try {

			dto 
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(modifie);

		} catch (final Exception e) {
			
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			
			return this.traiterErreur(
					MESSAGE_MODIF_KO
							+ libelleSousType
							+ TIRET_ESPACE
							+ messageSecurise,
					METHODE_UPDATE,
					e);
		}

		/*
		 * Un DTO null après conversion est une rupture technique.
		 * Si dto == null : 
		 * message technique + LOG + IllegalStateException
		 */
		if (dto == null) {
			
			final String messageTechnique =
					MESSAGE_MODIF_KO
							+ libelleSousType
							+ TIRET_ESPACE
							+ MSG_ERREUR_NON_SPECIFIEE;
			
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
		this.message.set(MESSAGE_MODIF_OK + libelleSousType);

		/* Retourne l'OutputDTO modifié. */
		return dto;
	}

	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final SousTypeProduitDTO.InputDTO pInputDTO)
			throws Exception {

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
		final String libelleParent = pInputDTO.getTypeProduit();
		final String libelleSousType = pInputDTO.getSousTypeProduit();

		/*
		 * Le contrat UC refuse un libellé enfant blank.
		 * Si StringUtils.isBlank(libelleSousType) :
		 * émet MESSAGE_PARAM_BLANK + LOG + ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelleSousType)) {
			
			this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					METHODE_DELETE,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
			return;
		}

		/*
		 * Le contrat UC refuse un parent blank.
		 * Si StringUtils.isBlank(libelleParent) :
		 * émet MESSAGE_PAS_PARENT + LOG + IllegalStateException.
		 */
		if (StringUtils.isBlank(libelleParent)) {
			
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
		final TypeProduit parentPersistant;

		try {
			
			/* Délègue au GATEWAY la recherche 
			 * du parent persistant via libellé. */
			parentPersistant 
				= this.typeProduitGateway.findByLibelle(libelleParent);
			
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
		 * sinon MESSAGE_PAS_PARENT + LOG + IllegalStateException
		 */
		if (parentPersistant == null
				|| parentPersistant.getIdTypeProduit() == null) {
			
			this.traiterErreur(
					MESSAGE_PAS_PARENT,
					METHODE_DELETE,
					new IllegalStateException(MESSAGE_PAS_PARENT));
			return;
		}

		/*
		 * Recherche ensuite tous les enfants du parent persistant,
		 * afin de ré-identifier la cible exacte sur le couple
		 * [parent, libellé].
		 */
		final List<SousTypeProduit> records;

		try {
			
			/* Délègue au GATEWAY la recherche des 
			 * enfants du parent persistant. */
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
		 * émet un MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
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
		final List<SousTypeProduit> recordsNonNullTries =
				this.filtrerEtTrier(records);

		SousTypeProduit existant = null;

		for (final SousTypeProduit candidat : recordsNonNullTries) {
			
			if (Strings.CI.equals(
					candidat.getSousTypeProduit(),
					libelleSousType)) {
				
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
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelleSousType);
			return;
		}

		/*
		 * L'objet ré-identifié doit être persistant :
		 * sinon émet un MESSAGE_OBJ_NON_PERSISTE + libelleSousType 
		 * + LOG + ExceptionNonPersistant
		 */
		if (existant.getIdSousTypeProduit() == null) {
			
			final String messageUtil =
					MESSAGE_OBJ_NON_PERSISTE + libelleSousType;

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
			this.gateway.delete(existant);
		} catch (final Exception e) {
			
			final String messageSecurise =
					StringUtils.isNotBlank(e.getMessage())
							? e.getMessage()
							: MSG_ERREUR_NON_SPECIFIEE;

			this.traiterErreur(
					MESSAGE_DELETE_KO
							+ libelleSousType
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
		this.message.set(MESSAGE_DELETE_OK + libelleSousType);
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
		 * Retourne simplement le dernier message courant
		 * stocké localement dans le ThreadLocal du service.
		 * Aucun accès Gateway, aucun LOG, aucun recalcul.
		 * Une valeur null reste acceptable
		 * avant toute opération ayant positionné un message.
		 */
		return this.message.get();
	}
	


	// ========================== METHODES PRIVEES =========================

	
	
	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Détermine si un doublon métier existe déjà dans le stockage.</p>
	 * <p>Le doublon d'un {@link SousTypeProduit} se contrôle
	 * sur le couple [parent, libellé] et non sur le seul libellé.</p>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO :
	 * DTO de création à contrôler.
	 * @return boolean : true si un SousTypeProduit portant
	 * le même libellé sous le même parent existe déjà.
	 * @throws Exception
	 */
	private boolean isDoublon(
			final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/*
		 * Sécurise le cas pInputDTO == null.
		 * Le contrat appelant traite ce cas en amont.
		 */
		if (pInputDTO == null) {
			return false;
		}

		/*
		 * Récupère les 2 composantes du couple métier à contrôler :
		 * [parent, libellé].
		 */
		final String libelleRecherche = pInputDTO.getSousTypeProduit();
		final String parentRecherche = pInputDTO.getTypeProduit();

		/*
		 * Si le parent n'est pas exploitable :
		 * ne conclut pas à un doublon ici.
		 * Le contrôle du parent est traité ensuite par creer(...).
		 */
		if (StringUtils.isBlank(parentRecherche)) {
			return false;
		}

		/*
		 * Le GATEWAY sait rechercher par libellé exact.
		 * Le filtrage final du doublon se fait ici
		 * sur le couple [parent, libellé].
		 */
		final List<SousTypeProduit> sousTypesProduitsExistants
				= this.gateway.findByLibelle(libelleRecherche);

		/*
		 * Absence de résultats exploitables :
		 * pas de doublon.
		 */
		if (sousTypesProduitsExistants == null
				|| sousTypesProduitsExistants.isEmpty()) {
			return false;
		}

		/*
		 * Retourne true uniquement si un enregistrement existant
		 * porte le même libellé sous le même parent.
		 */
		for (final SousTypeProduit existant : sousTypesProduitsExistants) {

			if (existant == null) {
				continue;
			}

			final String libelleExistant = existant.getSousTypeProduit();

			final TypeProduitI parentExistantObjet = existant.getTypeProduit();

			final String parentExistant = parentExistantObjet != null
					? parentExistantObjet.getTypeProduit()
					: null;

			if (Strings.CI.equals(libelleRecherche, libelleExistant)
					&& Strings.CI.equals(parentRecherche, parentExistant)) {
				return true;
			}
		}

		/*
		 * Aucun enregistrement ne matche le couple [parent, libellé].
		 */
		return false;
	}
	

	
	/**
	 * <div>
	 * <p style="font-weight:bold;">Convertit un InputDTO 
	 * en objet métier {@link SousTypeProduit}.
	 * </p>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO
	 * @return SousTypeProduit : objet métier (ou null si pInputDTO == null)
	 */
	private SousTypeProduit convertirInputDTOEnMetier(
			final SousTypeProduitDTO.InputDTO pInputDTO) {

		if (pInputDTO == null) {
			return null;
		}

		final String tpString = pInputDTO.getTypeProduit();
		final String libelle = pInputDTO.getSousTypeProduit();
		final TypeProduit tp = new TypeProduit(tpString);

		return new SousTypeProduit(libelle, tp);
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit un InputDTO en objet métier {@link TypeProduit}.</p>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO
	 * @return TypeProduit : objet métier (ou null si pInputDTO == null)
	 */
	private TypeProduit convertirInputDTOEnMetier(
			final TypeProduitDTO.InputDTO pInputDTO) {

		if (pInputDTO == null) {
			return null;
		}

		return new TypeProduit(pInputDTO.getTypeProduit());
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Filtre les nulls puis trie (SousTypeProduit est Comparable).</p>
	 * <p>Retourne toujours une liste non nulle.</p>
	 * </div>
	 *
	 * @param pSource : List&lt;SousTypeProduit&gt; (peut être null)
	 * @return List&lt;SousTypeProduit&gt; : 
	 * liste filtrée et triée (non null)
	 */
	private List<SousTypeProduit> filtrerEtTrier(
			final List<SousTypeProduit> pSource) {

		final List<SousTypeProduit> recordsNonNull
			= new ArrayList<SousTypeProduit>();

		if (pSource != null) {
			for (final SousTypeProduit stp : pSource) {
				if (stp != null) {
					recordsNonNull.add(stp);
				}
			}
		}

		Collections.sort(recordsNonNull);

		return recordsNonNull;
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Convertit une liste d'objets métier en OutputDTO et dédoublonne
	 * en conservant l'ordre d'insertion.</p>
	 * </div>
	 *
	 * @param pMetiers : List&lt;SousTypeProduit&gt; (non null de préférence)
	 * @return List&lt;SousTypeProduitDTO.OutputDTO&gt; : liste non nulle
	 */
	private List<SousTypeProduitDTO.OutputDTO> convertirEtDedoublonner(
			final List<SousTypeProduit> pMetiers) {

		final Set<SousTypeProduitDTO.OutputDTO> uniques
			= new LinkedHashSet<SousTypeProduitDTO.OutputDTO>();

		if (pMetiers != null) {
			for (final SousTypeProduit stp : pMetiers) {

				final SousTypeProduitDTO.OutputDTO dto
					= ConvertisseurMetierToOutputDTOSousTypeProduit
						.convert(stp);

				if (dto != null) {
					uniques.add(dto);
				}
			}
		}

		return new ArrayList<SousTypeProduitDTO.OutputDTO>(uniques);
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Alimente <code>this.message</code> avec le message du Gateway.</p>
	 * </div>
	 */
	private void alimenterMessageDepuisGateway() {
		// Règle 1 : le message affichable est celui du CU.
		// On conserve la méthode (sans effet sur le message CU) 
		// pour ne pas casser la structure.
	}



	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * Sécurise la récupération de totalElements depuis un ResultatPage.</p>
	 * </div>
	 *
	 * @param pResultatPage : ResultatPage&lt;SousTypeProduit&gt;
	 * @return long : totalElements (>= 0)
	 */
	private long safeTotalElements(
			final ResultatPage<SousTypeProduit> pResultatPage) {

		if (pResultatPage == null) {
			return 0L;
		}

		final long totalElements = pResultatPage.getTotalElements();

		if (totalElements < 0L) {
			return 0L;
		}

		return totalElements;
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
			return Strings.CI.equals(pMessage, MESSAGE_PARAM_NULL)
					|| Strings.CI.equals(pMessage, MESSAGE_PAGEABLE_NULL)
					|| Strings.CI.equals(pMessage, MESSAGE_PAS_PARENT)
					|| Strings.CI.equals(pMessage, RECHERCHE_TYPEPRODUIT_NULL);
		}

		return false;
	}
	
	

}
