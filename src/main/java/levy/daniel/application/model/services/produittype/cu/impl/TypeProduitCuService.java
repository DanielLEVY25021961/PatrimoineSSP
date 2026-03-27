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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import levy.daniel.application.model.dto.produittype.ConvertisseurMetierToOutputDTOTypeProduit;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitCuService.java :
 * </p>
 *
 * <p>
 * Cette classe modélise :
 * le <span style="font-weight:bold;">
 * SERVICE METIER (Use Case) ADAPTER</span>
 * pour l'objet métier <code style="font-weight:bold;">TypeProduit</code>.
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
 * le DAO JPA <code style="font-weight:bold;">TypeProduitDaoJPA</code>
 * de la classe TECHNIQUE concrète <code style="font-weight:bold;">
 * TypeProduitGatewayJPAService</code> qui implémente l'interface
 * de SERVICE TECHNIQUE <code style="font-weight:bold;">
 * TypeProduitGatewayIService</code>.</p>
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
@Service(value = "TypeProduitCuService")
@Profile({ "desktop", "dev", "prod" })
public class TypeProduitCuService implements TypeProduitICuService {

	// *************************** ATTRIBUTS *******************************/

	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * INTERFACE SERVICE TECHNIQUE GATEWAY</p>
	 * <ul>
	 * <li>ne connaissant pas l'implémentation technique du stockage.</li>
	 * <li>implémentée par des classes concrètes connaissant
	 * la technique de stockage.</li>
	 * </ul>
	 */
	private final TypeProduitGatewayIService gateway;

	/**
	 * <div>
	 * <p style="font-weight:bold;"
	 * >message à l'attention de l'Utilisateur
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
		= LogManager.getLogger(TypeProduitCuService.class);

	// ************************* METHODES **********************************/


	/**
	 * <div>
	 * <p style="font-weight:bold;">
	 * CONSTRUCTEUR COMPLET</p>
	 * <p>Indispensable pour l'injection par SPRING
	 * du TypeProduitGatewayIService via le constructeur.</p>
	 * <p>ATTENTION : Ne surtout pas créer de Constructeur d'arité nulle
	 * dans cette classe, faute de quoi SPRING ne pourra plus injecter.</p>
	 * </div>
	 *
	 * @param pGateway : TypeProduitGatewayIService :
	 * Interface de SERVICE TECHNIQUE chargé de la communication
	 * avec le stockage.
	 */
	public TypeProduitCuService(
			@Qualifier("TypeProduitGatewayJPAService")
			final TypeProduitGatewayIService pGateway) {
		super();
		this.gateway = pGateway;
	}


	
	/**
	* {@inheritDoc}
	*/	
	@Override
	public TypeProduitDTO.OutputDTO creer(
			final TypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* REGLES METIER. */
		/*
		 * ERREUR UTILISATEUR BENIGNE :
		 * si pInputDTO == null : 
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Retourne null avec un message utilisateur.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/*
		 * Récupère le libellé dans l'InputDTO.
		 */
		final String libelle = pInputDTO.getTypeProduit();

		/*
		 * si le libellé dans le DTO est blank : 
		 * émet un Message, LOG et jette une Exception applicative 
		 * ExceptionParametreBlank.
		 */
		if (StringUtils.isBlank(libelle)) {
			return this.traiterErreur(
					MESSAGE_CREER_NOM_BLANK,
					new ExceptionParametreBlank(MESSAGE_CREER_NOM_BLANK));
		}

		/*
		 * Vérifie le doublon fonctionnel.
		 * Toute anomalie technique lors de ce contrôle
		 * est transformée en message utilisateur rationalisé.
		 */
		final boolean doublon;
		
		try {
			
			doublon = this.isDoublon(pInputDTO);
			
		} catch (final Exception e) {
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			return this.traiterErreur(
					PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER + messageSecurise,
					e);
		}

		/*
		 * si le DTO représente un doublon : 
		 * émet un message, LOG et jette une Exception applicative 
		 * ExceptionDoublon.
		 */
		if (doublon) {
			
			final String messageDoublon = MESSAGE_DOUBLON + libelle;
			
			return this.traiterErreur(
					messageDoublon,
					new ExceptionDoublon(messageDoublon));
		}

		/*
		 * Convertit l'InputDTO en objet métier
		 * puis délègue la création au Gateway.
		 */
		final TypeProduit typeProduit 
			= this.convertirInputDTOEnMetier(pInputDTO);

		final TypeProduit cree;
		
		try {
			
			/* appelle le SERVICE GATEWAY pour l'opération 
			 * sur le stockage et récupère l'objet métier 
			 * retourné par GATEWAY. */
			cree = this.gateway.creer(typeProduit);
			
		} catch (final Exception e) {
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			return this.traiterErreur(
					PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER 
					+ messageSecurise, e);
		}

		/*
		 * Sécurise le contrat observable du UC :
		 * le Gateway ne doit pas conduire à un succès
		 * si aucun objet créé n'est réellement disponible.
		 */
		if (cree == null) {
			return this.traiterErreur(
					MESSAGE_CREATION_TECHNIQUE_KO_CREER,
					new IllegalStateException(
							MESSAGE_CREATION_TECHNIQUE_KO_CREER));
		}

		/*
		 * Prépare la réponse utilisateur finale.
		 * Le message de succès n'est posé qu'après conversion réussie.
		 */
		final TypeProduitDTO.OutputDTO dto;
		
		try {
			
			/* convertit l'objet métier -> OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOTypeProduit.convert(cree);
			
		} catch (final Exception e) {
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			return this.traiterErreur(
					PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER + messageSecurise,
					e);
		}

		if (dto == null) {
			return this.traiterErreur(
					MESSAGE_CONVERSION_TECHNIQUE_KO_CREER,
					new IllegalStateException(
							MESSAGE_CONVERSION_TECHNIQUE_KO_CREER));
		}

		/* émet le message de création OK. */
		this.message.set(MESSAGE_CREER_OK);

		/* retourne l'OutputDTO. */
		return dto;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<TypeProduitDTO.OutputDTO> rechercherTous() throws Exception {

		/*
		 * Délègue au GATEWAY la recherche exhaustive dans le stockage.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final List<TypeProduit> records;
		
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
		 * Sécurise le contrat observable du UC :
		 * le stockage ne doit pas retourner null.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Sécurise la réponse côté UC :
		 * retrait des nulls, tri métier,
		 * puis conversion en OutputDTO avec dédoublonnage.
		 */
		final List<TypeProduit> recordsNonNullTries 
			= this.filtrerEtTrier(records);

		final List<TypeProduitDTO.OutputDTO> dtos;
		
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
		 * Délègue au GATEWAY la recherche exhaustive dans le stockage.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final List<TypeProduit> records;

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
		 * Sécurise le contrat observable du UC :
		 * le stockage ne doit pas retourner null.
		 */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/*
		 * Prépare la réponse utilisateur complète :
		 * retrait des nulls, tri métier,
		 * extraction des libellés non blank,
		 * puis dédoublonnage en conservant l'ordre.
		 */
		final List<String> libelles;

		try {

			/* filtre les null et trie. */
			final List<TypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(records);

			final Set<String> uniques = new LinkedHashSet<String>();

			for (final TypeProduit tp : recordsNonNullTries) {

				/*
				 * recordsNonNullTries ne contient pas de null
				 * après filtrage préalable.
				 */
				final String libelle = tp.getTypeProduit();

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
		 * Le message observable n'est positionné
		 * qu'après préparation complète de la réponse utilisateur.
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
		 * Le contrat UC refuse une requête de pagination null.
		 * Si pRequetePage == null : émet un message MESSAGE_PAGEABLE_NULL 
		 * + LOG + IllegalStateException.
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
		final ResultatPage<TypeProduit> resultatPagine;

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
		 * Si resultatPagine == null : émet un MESSAGE_RECHERCHE_PAGINEE_KO 
		 * + LOG + IllegalStateException.
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
			final List<TypeProduit> contenus = resultatPagine.getContent();

			/* Retire les null, trie la liste d'objets métier. */
			final List<TypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(contenus);

			/* Convertit la liste d'objets métier sans null 
			 * en dédoublonnant et en conservant l'ordre. */
			final List<TypeProduitDTO.OutputDTO> dtos
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
	public OutputDTO findByLibelle(final String pLibelle) throws Exception {

		/*
		 * Retourne null avec un message observable
		 * si le libellé transmis n'est pas exploitable.
		 * Si StringUtils.isBlank(pLibelle) : 
		 * émet un message MESSAGE_PARAM_BLANK et retourne null.
		 */
		if (StringUtils.isBlank(pLibelle)) {
			this.message.set(MESSAGE_PARAM_BLANK);
			return null;
		}

		/*
		 * Délègue au GATEWAY la recherche exacte dans le stockage.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final TypeProduit typeProduit;

		try {
			
			/* Délègue au GATEWAY la recherche exacte dans le stockage.*/
			typeProduit = this.gateway.findByLibelle(pLibelle);
			
		} catch (final Exception e) {
			final String messageSecurise = StringUtils.isNotBlank(e.getMessage())
					? e.getMessage()
					: MSG_ERREUR_NON_SPECIFIEE;
			return this.traiterErreur(
					KO_TECHNIQUE_RECHERCHE + TIRET_ESPACE + messageSecurise,
					e);
		}

		/*
		 * Retourne null avec un message observable
		 * si aucun objet n'est trouvé en stockage.
		 * Si typeProduit == null : 
		 * émet un message MESSAGE_OBJ_INTROUVABLE + pLibelle 
		 * et retourne null.
		 */
		if (typeProduit == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pLibelle);
			return null;
		}

		/*
		 * Prépare la réponse utilisateur finale
		 * à partir de l'objet métier trouvé.
		 */
		final TypeProduitDTO.OutputDTO dto;

		try {
			
			/* Convertir l'objet métier retourné par le GATEWAY en OutputDTO. */
			dto = ConvertisseurMetierToOutputDTOTypeProduit.convert(typeProduit);
			
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
		 * un DTO null après conversion est une rupture technique.
		 * Si dto == null : émet un meassage + LOG + IllegalStateException
		 */
		if (dto == null) {
			final String messageTechnique = KO_TECHNIQUE_RECHERCHE
					+ TIRET_ESPACE
					+ MSG_ERREUR_NON_SPECIFIEE;
			return this.traiterErreur(
					messageTechnique,
					new IllegalStateException(messageTechnique));
		}

		/*
		 * Le message de succès MESSAGE_SUCCES_RECHERCHE n'est positionné
		 * qu'après préparation complète de la réponse utilisateur.
		 */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* Retourne l'OutputDTO résultat. */
		return dto;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<OutputDTO> findByLibelleRapide(
			final String pContenu) throws Exception {

		/*
		 * Le contrat UC refuse un contenu de recherche null.
		 * si pContenu == null : 
		 * émet un message observable MESSAGE_PARAM_NULL 
		 * + LOG + exception IllegalStateException.
		 */
		if (pContenu == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new IllegalStateException(MESSAGE_PARAM_NULL));
		}

		/*
		 * Le contrat UC délègue le cas blank
		 * au scénario complet de recherche exhaustive.
		 * Si StringUtils.isBlank(pContenu) : 
		 * retourne tous les enregistrements du stockage.
		 */
		if (StringUtils.isBlank(pContenu)) {
			return this.rechercherTous();
		}

		/*
		 * Délègue au GATEWAY la recherche rapide dans le stockage.
		 * Toute anomalie technique de recherche est transformée
		 * en message utilisateur rationalisé côté UC.
		 */
		final List<TypeProduit> records;

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
		 * Sécurise le contrat observable du UC :
		 * le stockage ne doit pas retourner null.
		 * Si records == null : 
		 * émet un message observable MESSAGE_STOCKAGE_NULL 
		 * + LOG + exception applicative ExceptionStockageVide.
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
			
			/* filtre les null et trie la réponse du stockage. */
			final List<TypeProduit> recordsNonNullTries
					= this.filtrerEtTrier(records);

			/* dédoublonne, conserve l'ordre et 
			 * convertit la réponse du stockage filtrée en OutputDTOs. */
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
	public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception {

		/*
		 * Si pInputDTO == null :
		 * émet MESSAGE_RECHERCHE_OBJ_NULL
		 * et retourne null.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
			return null;
		}

		/*
		 * Sinon :
		 * délègue la recherche exacte
		 * à findByLibelle(pInputDTO.getTypeProduit()).
		 *
		 * Les messages, retours et exceptions observables
		 * de la recherche déléguée
		 * restent ceux de findByLibelle(...).
		 */
		return this.findByLibelle(pInputDTO.getTypeProduit());
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
		final TypeProduit res = this.gateway.findById(pId);

		/* message "introuvable" et
		 * retourne null si l'objet métier n'est pas trouvé. */
		if (res == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pId);
			return null;
		}

		/* convertit la réponse en DTO et le retourne. */
		final TypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOTypeProduit.convert(res);

		/* émet un message de succès. */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* retourne l'OutputDTO. */
		return dto;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public TypeProduitDTO.OutputDTO update(
			final TypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* alimente this.message, LOG et jette une Exception
		 * si pInputDTO == null. */
		if (pInputDTO == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
		}

		/* alimente this.message, LOG et jette une
		 * Exception si le libellé est blank. */
		if (StringUtils.isBlank(pInputDTO.getTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
		}

		/*
		 * IMPORTANT :
		 * TypeProduitDTO.InputDTO ne porte pas d'ID.
		 * On récupère donc l'objet existant via une recherche
		 * (par libellé exact), afin d'obtenir l'ID persistant.
		 */
		/* délègue au Gateway la recherche par libellé. */
		final TypeProduit existant = this.gateway.findByLibelle(
				pInputDTO.getTypeProduit());

		/* émet un message et retourne null si le Gateway
		 * ne trouve pas d'objet par libellé. */
		if (existant == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE
					+ pInputDTO.getTypeProduit());
			return null;
		}

		/* alimente this.message, LOG et jette une Exception
		 * si l'objet métier n'est pas persisté. */
		if (existant.getIdTypeProduit() == null) {

			final String messageUtil
				= MESSAGE_OBJ_NON_PERSISTE
					+ pInputDTO.getTypeProduit();

			return this.traiterErreur(
					messageUtil,
					new ExceptionNonPersistant(messageUtil));
		}

		/* convertit l'InputDTO en objet métier. */
		final TypeProduit tp
			= this.convertirInputDTOEnMetier(pInputDTO);

		/* réinjecte l'ID persistant récupéré. */
		tp.setIdTypeProduit(existant.getIdTypeProduit());

		/* MODIFICATION - applique la modification - fait par le Gateway. */

		/* Délègue au Gateway la tâche de modifier dans le stockage. */
		final TypeProduit modifie = this.gateway.update(tp);

		/* émet un message et retourne null si modifie == null. */
		if (modifie == null) {
			this.message.set(MESSAGE_MODIF_KO
					+ pInputDTO.getTypeProduit());
			return null;
		}

		/* message de succès. */
		this.message.set(MESSAGE_MODIF_OK + pInputDTO.getTypeProduit());

		/* convertit l'objet métier modifié en DTO. */
		final TypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOTypeProduit.convert(modifie);

		/* retourne l'OutputDTO modifié. */
		return dto;
	}

	

	/**
	* {@inheritDoc}
	*/
	@Override
	public void delete(final TypeProduitDTO.InputDTO pInputDTO)
			throws Exception {

		/* alimente this.message, LOG et jette une
		 * Exception si pTypeProduit est null.*/
		if (pInputDTO == null) {
			this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new ExceptionParametreNull(MESSAGE_PARAM_NULL));
			return;
		}

		/* alimente this.message, LOG et jette une
		 * Exception si le libellé est blank. */
		if (StringUtils.isBlank(pInputDTO.getTypeProduit())) {
			this.traiterErreur(
					MESSAGE_PARAM_BLANK,
					new ExceptionParametreBlank(MESSAGE_PARAM_BLANK));
			return;
		}

		final String libelle = pInputDTO.getTypeProduit();

		/* délègue au Gateway la recherche par libellé dans le stockage. */
		final TypeProduit tp = this.gateway.findByLibelle(libelle);

		/* émet un message KO et ne fait rien
		 * si l'objet est introuvable par libellé. */
		if (tp == null) {
			this.message.set(MESSAGE_OBJ_INTROUVABLE + libelle);
			return;
		}

		try {

			/* délègue au Gateway la tâche de la destruction de l'objet. */
			this.gateway.delete(tp);

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
	 * Détermine si un Objet métier existe déjà dans le stockage.</p>
	 * <p>retourne true si c'est le cas.</p>
	 * </div>
	 *
	 * @param pInputDTO : TypeProduitDTO.InputDTO
	 * @return boolean : true si doublon
	 * @throws Exception
	 */
	private boolean isDoublon(
			final TypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* recherche dans le stockage. */
		final TypeProduit typeProduitExistant
			= this.gateway.findByLibelle(pInputDTO.getTypeProduit());

		return typeProduitExistant != null;
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
	 * Filtre les nulls puis trie (TypeProduit est Comparable).</p>
	 * <p>Retourne toujours une liste non nulle.</p>
	 * </div>
	 *
	 * @param pSource : List&lt;TypeProduit&gt; (peut être null)
	 * @return List&lt;TypeProduit&gt; : liste filtrée et triée (non null)
	 */
	private List<TypeProduit> filtrerEtTrier(
			final List<TypeProduit> pSource) {

		final List<TypeProduit> recordsNonNull
			= new ArrayList<TypeProduit>();

		if (pSource != null) {
			for (final TypeProduit tp : pSource) {
				if (tp != null) {
					recordsNonNull.add(tp);
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
	 * @param pMetiers : List&lt;TypeProduit&gt; (non null de préférence)
	 * @return List&lt;TypeProduitDTO.OutputDTO&gt; : liste non nulle
	 */
	private List<TypeProduitDTO.OutputDTO> convertirEtDedoublonner(
			final List<TypeProduit> pMetiers) {

		final Set<TypeProduitDTO.OutputDTO> uniques
			= new LinkedHashSet<TypeProduitDTO.OutputDTO>();

		if (pMetiers != null) {
			for (final TypeProduit tp : pMetiers) {

				final TypeProduitDTO.OutputDTO dto
					= ConvertisseurMetierToOutputDTOTypeProduit
						.convert(tp);

				if (dto != null) {
					uniques.add(dto);
				}
			}
		}

		return new ArrayList<TypeProduitDTO.OutputDTO>(uniques);
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
	 * @param pResultatPage : ResultatPage&lt;TypeProduit&gt;
	 * @return long : totalElements (>= 0)
	 */
	private long safeTotalElements(
			final ResultatPage<TypeProduit> pResultatPage) {

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
	 * <p style="font-weight:bold;">
	 * Centralise le traitement des erreurs</p>
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
