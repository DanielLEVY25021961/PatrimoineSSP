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
 * pour l'objet métier <code style="font-weight:bold;">SousTypeProduit</code>.
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
 * le DAO JPA <code style="font-weight:bold;">SousTypeProduitDaoJPA</code>
 * de la classe TECHNIQUE concrète <code style="font-weight:bold;">
 * SousTypeProduitGatewayJPAService</code> qui implémente l'interface
 * de SERVICE TECHNIQUE <code style="font-weight:bold;">
 * SousTypeProduitGatewayIService</code>.</p>
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
					e);
		}

		/* Si dto == null : 
		 * émet un message MESSAGE_CONVERSION_TECHNIQUE_KO_CREER 
		 * + LOG + IllegalStateException. */
		if (dto == null) {
			return this.traiterErreur(
					MESSAGE_CONVERSION_TECHNIQUE_KO_CREER,
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
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
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
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
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
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
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
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
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
					e);
		}

		/*
		 * Si le stockage retourne null :
		 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
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
	public OutputDTO findByDTO(
	        final InputDTO pInputDTO) throws Exception {

	    /* REGLES METIER. */

	    /*
	     * ERREUR UTILISATEUR BENIGNE :
	     * aucun traitement, aucun LOG, aucune Exception.
	     * Retourne null avec un message utilisateur si pInputDTO == null.
	     */
	    if (pInputDTO == null) {
	        this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
	        return null;
	    }

	    /* CONTRAT (PORT) :
	     * Si pInputDTO.getTypeProduit() est Blank,
	     * positionne getMessage() à MESSAGE_PAS_PARENT et lève une exception.
	     *
	     * IMPORTANT : ce contrôle doit être fait AVANT le try/catch technique,
	     * sinon l'exception est catchée et le message est écrasé par
	     * KO_TECHNIQUE_RECHERCHE (violation du contrat).
	     */
	    final String libelleTP = pInputDTO.getTypeProduit();
	    if (StringUtils.isBlank(libelleTP)) {
	        this.message.set(MESSAGE_PAS_PARENT);
	        throw new IllegalStateException(MESSAGE_PAS_PARENT);
	    }

	    try {

	        final String libelleSTP = pInputDTO.getSousTypeProduit();

	        /* ============= RATTACHEMENT PARENT PERSISTÉ =============== */

	        final TypeProduit parentPersistant
	            = this.typeProduitGateway.findByLibelle(libelleTP);

	        /*
	         * Si le parent n'existe pas / pas d'ID, la recherche ne peut pas aboutir :
	         * retourne null avec MESSAGE_RECHERCHE_VIDE.
	         */
	        if (parentPersistant == null
	                || parentPersistant.getIdTypeProduit() == null) {
	            this.message.set(MESSAGE_RECHERCHE_VIDE);
	            return null;
	        }

	        /* délègue au Gateway la recherche de la liste d'objets métier attachés au parent. */
	        final List<SousTypeProduit> possibles
	            = this.gateway.findAllByParent(parentPersistant);

	        /* émet un message et retourne null si la recherche ne retourne rien. */
	        if (possibles.isEmpty()) {
	            this.message.set(MESSAGE_RECHERCHE_VIDE);
	            return null;
	        }

	        SousTypeProduit resultat = null;

	        /* recherche l'objet métier dans la liste des possibles. */
	        for (final SousTypeProduit stp : possibles) {
	            if (Strings.CI.equals(stp.getSousTypeProduit(), libelleSTP)) {
	                resultat = stp;
	                break;
	            }
	        }

	        /* émet un message et retourne null si la recherche ne retourne rien. */
	        if (resultat == null) {
	            this.message.set(MESSAGE_RECHERCHE_VIDE);
	            return null;
	        }

	        /* convertit l'objet métier en OutputDTO. */
	        final SousTypeProduitDTO.OutputDTO stpDTO
	            = ConvertisseurMetierToOutputDTOSousTypeProduit.convert(resultat);

	        /* émet un message. */
	        this.message.set(MESSAGE_SUCCES_RECHERCHE);

	        /* retourne le OutputDTO. */
	        return stpDTO;

	    } catch (Exception e) {
	        return this.traiterErreur(KO_TECHNIQUE_RECHERCHE, e);
	    }
	}
	
	

	/**
	* {@inheritDoc}
	*/
	@Override
	public OutputDTO findById(final Long pId) throws Exception {

		/* message paramètre null et retourne null si pId == null. */
		if (pId == null) {
			this.message.set(MESSAGE_PARAM_NULL);
			return null;
		}

		/* délègue au Gateway la recherche du résultat. */
		final SousTypeProduit res = this.gateway.findById(pId);

		/* message "introuvable" et 
		 * retourne null si l'objet métier n'est pas trouvé. */
		if (res == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pId);
			return null;
		}

		/* convertit la réponse en DTO et le retourne. */
		final SousTypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(res);

		/* émet un message de succès. */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* retourne l'OutputDTO. */
		return dto;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public SousTypeProduitDTO.OutputDTO update(
			final SousTypeProduitDTO.InputDTO pInputDTO) 
					throws Exception {

		/* alimente this.message, LOG et jette une Exception 
		 * si pInputDTO == null. */
		if (pInputDTO == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
		}

		/* alimente this.message, LOG et jette une
		 * Exception si le libellé est blank. */
		if (StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
		}

		/*
		 * IMPORTANT :
		 * SousTypeProduitDTO.InputDTO ne porte pas d'ID.
		 * On récupère donc l'objet existant via une recherche
		 * (par libellé exact), afin d'obtenir l'ID persistant.
		 */
		/* délègue au Gateway la recherche par libellé. */
		final SousTypeProduit existant = this.gateway.findByLibelle(
				pInputDTO.getSousTypeProduit()).get(0);

		/* émet un message et retourne null si le Gateway 
		 * ne trouve pas d'objet par libellé. */
		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE 
					+ pInputDTO.getSousTypeProduit());
			return null;
		}

		/* alimente this.message, LOG et jette une Exception
		 * si l'objet métier n'est pas persisté. */
		if (existant.getIdSousTypeProduit() == null) {

			final String messageUtil
				= MESSAGE_OBJ_NON_PERSISTE
					+ pInputDTO.getSousTypeProduit();

			return this.traiterErreur(
					messageUtil,
					new ExceptionNonPersistant(messageUtil));
		}

		/* convertit l'InputDTO en objet métier. */
		final SousTypeProduit stp
			= this.convertirInputDTOEnMetier(pInputDTO);

		/* réinjecte l'ID persistant récupéré. */
		stp.setIdSousTypeProduit(existant.getIdSousTypeProduit());

		/* MODIFICATION - applique la modification - fait par le Gateway. */

		/* Délègue au Gateway la tâche de modifier dans le stockage. */
		final SousTypeProduit modifie = this.gateway.update(stp);

		/* émet un message et retourne null si modifie == null. */
		if (modifie == null) {
			this.message.set(MESSAGE_MODIF_KO 
					+ pInputDTO.getSousTypeProduit());
			return null;
		}

		/* message de succès. */
		this.message.set(MESSAGE_MODIF_OK + pInputDTO.getTypeProduit());

		/* convertit l'objet métier modifié en DTO. */
		final SousTypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(modifie);

		/* retourne l'OutputDTO modifié. */
		return dto;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public void delete(final SousTypeProduitDTO.InputDTO pInputDTO) 
			throws Exception {

		/* alimente this.message, LOG et jette une
		 * Exception si pSousTypeProduit est null.*/
		if (pInputDTO == null) {
			this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
			return;
		}

		/* alimente this.message, LOG et jette une
		 * Exception si le libellé est blank. */
		if (StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
			return;
		}

		final String libelle = pInputDTO.getSousTypeProduit();

		/* délègue au Gateway la recherche par libellé dans le stockage. */
		final SousTypeProduit stp = this.gateway.findByLibelle(libelle).get(0);

		/* émet un message KO et ne fait rien 
		 * si l'objet est introuvable par libellé. */
		if (stp == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelle);
			return;
		}

		try {

			/* délègue au Gateway la tâche de la destruction de l'objet. */
			this.gateway.delete(stp);

			/* alimente un message. */
			this.message.set(MESSAGE_DELETE_OK + libelle);

		} catch (Exception e) {

			/* alimente this.message, LOG et jette 
			 * une Exception si la destruction a échoué. */
			this.traiterErreur(MESSAGE_DELETE_KO + libelle, e);
		}		
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public long count() throws Exception {
		return this.gateway.count();
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public String getMessage() {
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
	 * <p style="font-weight:bold;">Centralise le traitement des erreurs</p>
	 * <p>alimente this.message, LOG et jette une Exception typée.</p>
	 * </div>
	 *
	 * @param pMessage : String : message d'erreur
	 * @param pE : Exception typée à jeter
	 * @return T : pour chaîner dans les méthodes retournant une valeur
	 * @throws Exception
	 */
	private <T> T traiterErreur(
			final String pMessage,
			final Exception pE) throws Exception {

		final String messageFinal
			= (pMessage != null) ? pMessage : MSG_ERREUR_NON_SPECIFIEE;

		this.message.set(messageFinal);

		if (LOG.isInfoEnabled()) {
			LOG.info(messageFinal, pE);
		}

		if (pE != null) {
			throw pE;
		}

		throw new Exception(messageFinal);
	}

}
