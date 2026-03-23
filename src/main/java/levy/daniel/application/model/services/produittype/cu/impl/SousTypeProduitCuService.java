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
			final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* REGLES METIER. */

		/*
		 * ERREUR UTILISATEUR BENIGNE :
		 * aucun traitement, aucun LOG, aucune Exception.
		 * Retourne null avec un message utilisateur.
		 */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_CREER_NULL);
			return null;
		}

		/*
		 * émet un message, LOG et jette une 
		 * ExceptionParametreBlank si le libellé dans le DTO est blank.
		 */
		if (StringUtils.isBlank(pInputDTO.getSousTypeProduit())) {
			return this.traiterErreur(
					MESSAGE_CREER_NOM_BLANK,
					new ExceptionParametreBlank(MESSAGE_CREER_NOM_BLANK));
		}

		/*
		 * émet un message, LOG et jette une ExceptionDoublon si le DTO
		 * est un doublon.
		 */
		if (this.isDoublon(pInputDTO)) {
			final String messageDoublon
				= MESSAGE_DOUBLON + pInputDTO.getSousTypeProduit();
			return this.traiterErreur(
					messageDoublon,
					new ExceptionDoublon(messageDoublon));
		}

		/* ========= RATTACHEMENT DU PARENT PERSISTÉ ===================== */

		/* IMPORTANT :
		 * On doit récupérer le TypeProduit PERSISTÉ (avec ID) 
		 * depuis la base, puis l'utiliser comme parent 
		 * du SousTypeProduit métier.
		 *
		 * => nécessite un accès au TypeProduitGatewayIService 
		 * depuis ce CU Service.
		 */
		final String libelleParent = pInputDTO.getTypeProduit();

		final TypeProduit parentPersistant 
			= this.typeProduitGateway.findByLibelle(libelleParent);

		if (parentPersistant == null 
				|| parentPersistant.getIdTypeProduit() == null) {
			this.message.set(MESSAGE_PAS_PARENT);
			throw new IllegalStateException(MESSAGE_PAS_PARENT);
		}

		/* ===================== CREATION ===================== */

		/* convertit l'Input DTO en Objet métier. */
		final SousTypeProduit sousTypeProduit
			= this.convertirInputDTOEnMetier(pInputDTO);

		/* rattache le parent persistant 
		 * (évite TypeProduit transient côté JPA). */
		sousTypeProduit.setTypeProduit(parentPersistant);

		/* appelle le SERVICE GATEWAY pour l'opération sur le stockage. */
		/* récupère l'objet métier retourné par GATEWAY. */
		final SousTypeProduit cree = this.gateway.creer(sousTypeProduit);

		/* émet le message de création OK. */
		message.set(MESSAGE_CREER_OK);

		/* convertit l'objet métier -> OutputDTO. */
		final SousTypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(cree);

		/* retourne OutputDTO. */
		return dto;
	}


	/**
	* {@inheritDoc}
	*/
	@Override
	public List<SousTypeProduitDTO.OutputDTO> rechercherTous() 
									throws Exception {

		/* délègue au Gateway la recherche des résultats. */
		final List<SousTypeProduit> records = this.gateway.rechercherTous();

		/* émet un message, LOG et jette une Exception 
		 * si le stockage ne retourne rien. */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/* retire les null et trie. */
		final List<SousTypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/* convertit la réponse en OutputDTO. */
		final List<SousTypeProduitDTO.OutputDTO> dtos
			= this.convertirEtDedoublonner(recordsNonNullTries);

		if (dtos.isEmpty()) {
			/* message recherche vide si pas de résultats. */
			message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			/* message recherche OK si résultats. */
			message.set(MESSAGE_RECHERCHE_OK);
		}

		/* retourne les résultats sous forme d'OutputDTOs. */
		return dtos;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public List<String> rechercherTousString() throws Exception {

		/* délègue au Gateway la recherche des résultats. */
		final List<SousTypeProduit> records = this.gateway.rechercherTous();

		/* émet un message, LOG et jette 
		 * une Exception si records == null. */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/* purifie et trie les résultats. */
		final List<SousTypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/* Set pour le dédoublonnage en O(n). */
		final Set<String> uniques = new LinkedHashSet<String>();

		for (final SousTypeProduit stp : recordsNonNullTries) {

			/* recordsNonNullTries ne contient pas de null
			 * (filtrage préalable). */
			final String libelle = stp.getSousTypeProduit();

			/* n'ajoute que les éléments avec des libellés non blank.*/
			if (!StringUtils.isBlank(libelle)) {
				uniques.add(libelle);
			}
		}

		if (uniques.isEmpty()) {
			/* message recherche vide si pas de résultats. */
			message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			/* message recherche OK si résultats. */
			message.set(MESSAGE_RECHERCHE_OK);
		}

		/* retourne la liste des résultats sous forme d'OutputDTOs. */
		return new ArrayList<String>(uniques);
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public ResultatPage<OutputDTO> rechercherTousParPage(
			final RequetePage pRequetePage) throws Exception {

		/* émet un message, LOG et jette une
		 * IllegalStateException si pRequetePage == null. */
		if (pRequetePage == null) {
			return this.traiterErreur(
					MESSAGE_PAGEABLE_NULL,
					new IllegalStateException(MESSAGE_PAGEABLE_NULL));
		}

		/* délègue au Service Gateway la tâche de retourner
		 * le résultat paginé. */
		final ResultatPage<SousTypeProduit> resultatPagine
			= this.gateway.rechercherTousParPage(pRequetePage);

		/* émet un message KO et retourne null si resultatPagine == null. */
		if (resultatPagine == null) {
			message.set(MESSAGE_RECHERCHE_PAGINEE_KO);
			return null;
		}

		final List<SousTypeProduit> contenus = resultatPagine.getContent();

		/* trie et filtre */
		final List<SousTypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(contenus);

		/* convertit en DTOS. */
		final List<SousTypeProduitDTO.OutputDTO> dtos
			= this.convertirEtDedoublonner(recordsNonNullTries);

		final int numeroPage = pRequetePage.getPageNumber();
		final int pgSize = pRequetePage.getPageSize();

		/*
		 * IMPORTANT :
		 * Le constructeur de ResultatPage exige totalElements (4e paramètre).
		 * On le reprend donc depuis le résultat du Gateway,
		 * afin d'avoir une pagination cohérente (totalPages, hasNext, ...).
		 */
		final long totalElements = this.safeTotalElements(resultatPagine);

		/* instancie la réponse ResultatPage. */
		final ResultatPage<OutputDTO> rp = new ResultatPage<OutputDTO>(
				dtos, numeroPage, pgSize, totalElements);

		/* émet un message de recherche paginée OK. */
		message.set(MESSAGE_RECHERCHE_PAGINEE_OK);

		/* retourne le résultat paginé. */
		return rp;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public OutputDTO findByLibelle(
			final String pLibelle) throws Exception {

		if (StringUtils.isBlank(pLibelle)) {
			/* message KO paramètre blanc. */
			this.message.set(MESSAGE_PARAM_BLANK);
			return null;
		}

		/* recherche déléguée au gateway. */
		final SousTypeProduit stp 
			= this.gateway.findByLibelle(pLibelle).get(0);

		if (stp == null) {
			/* message KO objet introuvable. */
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pLibelle);
			return null;
		}

		/* conversion en OutputDTO */
		final SousTypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOSousTypeProduit.convert(stp);

		/* message de succès. */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* retourne l'OutputDTO resultat. */
		return dto;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public List<SousTypeProduitDTO.OutputDTO> findByLibelleRapide(
			final String pContenu) throws Exception {

		/* émet un message, LOG et jette une Exception 
		 * si pContenu == null. */
		if (pContenu == null) {
			return this.traiterErreur(
					MESSAGE_PARAM_NULL,
					new IllegalStateException(MESSAGE_PARAM_NULL));
		}

		if (StringUtils.isBlank(pContenu)) {			
			/* retourne tous les enregistrements si pContenu est blank. */
			return this.rechercherTous();			
		} 

		/* délègue au Gateway le recherche des résultats. */
		final List<SousTypeProduit> reponses 
			= this.gateway.findByLibelleRapide(pContenu);

		/* émet un message, LOG et jette une Exception 
		 * si le Gateway retourne null. */
		if (reponses == null) {
			return this.traiterErreur(KO_TECHNIQUE_RECHERCHE
					, new RuntimeException(KO_TECHNIQUE_RECHERCHE));
		}

		if (reponses.isEmpty()) {
			/* message recherche vide si pas de résultats. */
			message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			/* message recherche OK si résultats. */
			message.set(MESSAGE_RECHERCHE_OK);
		}

		/* trie et filtre les réponses. */
		final List<SousTypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(reponses);

		/* convertit en OutputDTO. */
		final List<SousTypeProduitDTO.OutputDTO> rep
			= ConvertisseurMetierToOutputDTOSousTypeProduit
				.convertList(recordsNonNullTries);

		/* retourne la réponse. */
		return rep;
	}



	/**
	* {@inheritDoc}
	*/
	@Override
	public List<SousTypeProduitDTO.OutputDTO> findAllByParent(
			final TypeProduitDTO.InputDTO pTypeProduit)
			throws Exception {

		/* émet un message, LOG et jette une Exception 
		 * si pTypeProduit == null. */
		if (pTypeProduit == null) {
			return this.traiterErreur(
					RECHERCHE_TYPEPRODUIT_NULL,
					new RuntimeException(RECHERCHE_TYPEPRODUIT_NULL));
		}

		/* ==========RATTACHEMENT PARENT PERSISTÉ ===================== */

		/* IMPORTANT :
		 * On doit récupérer le TypeProduit PERSISTÉ (avec ID) depuis la base,
		 * puis l'utiliser comme parent pour la recherche des SousTypeProduit.
		 *
		 * => nécessite un accès au TypeProduitGatewayIService depuis ce CU Service.
		 */
		final String libelleParent = pTypeProduit.getTypeProduit();

		final TypeProduit parentPersistant
			= this.typeProduitGateway.findByLibelle(libelleParent);

		if (parentPersistant == null
				|| parentPersistant.getIdTypeProduit() == null) {
			return this.traiterErreur(
					MESSAGE_PAS_PARENT,
					new IllegalStateException(MESSAGE_PAS_PARENT));
		}

		try {

			/* essaie de récupérer la liste des enfants 
			 * auprès du service Gateway. */
			final List<SousTypeProduit> listStp
				= this.gateway.findAllByParent(parentPersistant);

			if (listStp.isEmpty()) {
				/* message recherche vide si pas de résultats. */
				message.set(MESSAGE_RECHERCHE_VIDE);
			} else {
				/* message recherche OK si résultats. */
				message.set(MESSAGE_RECHERCHE_OK);
			}

			/* convertit en OutputDTO, dédoublonne et trie la réponse. */
			final List<SousTypeProduitDTO.OutputDTO> reponse
				= this.convertirEtDedoublonner(listStp);

			/* retourne la réponse sous forme de Liste d'OutputDTO. */
			return reponse;

		} catch (Exception e) {
			return this.traiterErreur(KO_TECHNIQUE_RECHERCHE, e);
		}
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
	 * Détermine si un Objet métier existe déjà dans le stockage.</p>
	 * <p>retourne true si c'est le cas.</p>
	 * </div>
	 *
	 * @param pInputDTO : SousTypeProduitDTO.InputDTO
	 * @return boolean : true si doublon
	 * @throws Exception
	 */
	private boolean isDoublon(
			final SousTypeProduitDTO.InputDTO pInputDTO) throws Exception {

		/* recherche dans le stockage. */
		final SousTypeProduit sousTypeProduitExistant
			= this.gateway.findByLibelle(pInputDTO.getSousTypeProduit()).get(0);

		return sousTypeProduitExistant != null;
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
