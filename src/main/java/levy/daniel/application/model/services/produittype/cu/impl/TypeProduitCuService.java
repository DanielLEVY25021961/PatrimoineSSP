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
		if (StringUtils.isBlank(pInputDTO.getTypeProduit())) {
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
				= MESSAGE_DOUBLON + pInputDTO.getTypeProduit();
			return this.traiterErreur(
					messageDoublon,
					new ExceptionDoublon(messageDoublon));
		}

		/* convertit l'Input DTO en Objet métier. */
		final TypeProduit typeProduit
			= this.convertirInputDTOEnMetier(pInputDTO);

		/* appelle le SERVICE GATEWAY pour l'opération sur le stockage. */
		/* récupère l'objet métier retourné par GATEWAY. */
		final TypeProduit cree = this.gateway.creer(typeProduit);

		/* émet le message de création OK. */
		message.set(MESSAGE_CREER_OK);

		/* convertit l'objet métier -> OutputDTO. */
		final TypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOTypeProduit.convert(cree);

		/* retourne OutputDTO. */
		return dto;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<TypeProduitDTO.OutputDTO> rechercherTous() 
			throws Exception {

		/* délègue au Gateway la recherche des résultats. */
		final List<TypeProduit> records = this.gateway.rechercherTous();

		/* émet un message, LOG et jette une Exception
		 * si le stockage ne retourne rien. */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/* retire les null et trie. */
		final List<TypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/* convertit la réponse en OutputDTO. */
		final List<TypeProduitDTO.OutputDTO> dtos
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
		final List<TypeProduit> records = this.gateway.rechercherTous();

		/* émet un message, LOG et jette
		 * une Exception si records == null. */
		if (records == null) {
			return this.traiterErreur(
					MESSAGE_STOCKAGE_NULL,
					new ExceptionStockageVide(MESSAGE_STOCKAGE_NULL));
		}

		/* purifie et trie les résultats. */
		final List<TypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(records);

		/* Set pour le dédoublonnage en O(n). */
		final Set<String> uniques = new LinkedHashSet<String>();

		for (final TypeProduit tp : recordsNonNullTries) {

			/* recordsNonNullTries ne contient pas de null
			 * (filtrage préalable). */
			final String libelle = tp.getTypeProduit();

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

		/* retourne la liste des résultats sous forme de Strings. */
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
		final ResultatPage<TypeProduit> resultatPagine
			= this.gateway.rechercherTousParPage(pRequetePage);

		/* émet un message KO et retourne null si resultatPagine == null. */
		if (resultatPagine == null) {
			message.set(MESSAGE_RECHERCHE_PAGINEE_KO);
			return null;
		}

		final List<TypeProduit> contenus = resultatPagine.getContent();

		/* trie et filtre */
		final List<TypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(contenus);

		/* convertit en DTOS. */
		final List<TypeProduitDTO.OutputDTO> dtos
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
		final TypeProduit tp
			= this.gateway.findByLibelle(pLibelle);

		if (tp == null) {
			/* message KO objet introuvable. */
			this.message.set(MESSAGE_OBJ_INTROUVABLE + pLibelle);
			return null;
		}

		/* conversion en OutputDTO */
		final TypeProduitDTO.OutputDTO dto
			= ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		/* message de succès. */
		this.message.set(MESSAGE_SUCCES_RECHERCHE);

		/* retourne l'OutputDTO resultat. */
		return dto;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public List<TypeProduitDTO.OutputDTO> findByLibelleRapide(
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
		final List<TypeProduit> reponses
			= this.gateway.findByLibelleRapide(pContenu);

		/* trie et filtre les réponses. */
		final List<TypeProduit> recordsNonNullTries
			= this.filtrerEtTrier(reponses);

		/* convertit en OutputDTO. */
		final List<TypeProduitDTO.OutputDTO> rep
			= ConvertisseurMetierToOutputDTOTypeProduit
				.convertList(recordsNonNullTries);

		/*
		 * Contrat observable :
		 * le message reflète la liste effectivement retournée.
		 */
		if (rep.isEmpty()) {
			/* message recherche vide si pas de résultats. */
			message.set(MESSAGE_RECHERCHE_VIDE);
		} else {
			/* message recherche OK si résultats. */
			message.set(MESSAGE_RECHERCHE_OK);
		}

		/* retourne la réponse. */
		return rep;
	}


	
	/**
	* {@inheritDoc}
	*/
	@Override
	public OutputDTO findByDTO(final InputDTO pInputDTO) throws Exception {
	
		/* émet un message et retourne null. */
		if (pInputDTO == null) {
			this.message.set(MESSAGE_RECHERCHE_OBJ_NULL);
			return null;
		}
		
		/* délègue à this.findByLibelle(libelle). */
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
